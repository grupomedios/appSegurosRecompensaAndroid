package com.grupomedios.dclub.segurosrecompensa.notifications.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.fragment.BaseFragment;
import com.grupomedios.dclub.segurosrecompensa.notifications.OneSignalApiClient;
import com.grupomedios.dclub.segurosrecompensa.notifications.adapter.NotificationsAdapter;
import com.grupomedios.dclub.segurosrecompensa.notifications.model.Notification;
import com.grupomedios.dclub.segurosrecompensa.notifications.model.ResponseNotifications;
import com.grupomedios.desclub.desclubutil.ui.list.PaginableActivity;
import com.grupomedios.desclub.desclubutil.ui.scroll.EndlessScrollListener;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.yalantis.phoenix.PullToRefreshView;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Main {@link Fragment} subclass.
 */
public class NotificationsFragment extends BaseFragment implements PaginableActivity {
    private static final int PAGE_SIZE = 50;
    BaseAdapter mAdapter = null;
    AnimationAdapter mAnimAdapter;

    private ListView mListView;
    private List<Notification> mNotifications;

    private View mLoadingFooter;
    private PullToRefreshView mPullToRefreshView;
    private int mOffset = 0;
    private View mEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_notifications, container, false);

        mPullToRefreshView = (PullToRefreshView) mainView.findViewById(R.id.notifications_list_pull_to_refresh);
        mEmptyView = mainView.findViewById(R.id.notifications_empty_view);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //reload list
                if (mNotifications != null) {
                    mNotifications.clear();
                }
                loadNotifications();
            }
        });

        return mainView;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }

    public void loadNotifications() {
        if (mNotifications != null) {
            mNotifications.clear();
            mNotifications = new ArrayList<>();
            mNotifications = null;
            if (mAnimAdapter != null) {
                mAnimAdapter.notifyDataSetChanged();
            }
        }

        mListView = (ListView) getView().findViewById(R.id.notifications_listView);
        mListView.setOnScrollListener(new EndlessScrollListener(this));
        mLoadingFooter = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.part_loading_footer, null, false);

        mOffset = 0;
        loadPage();
    }


    @Override
    public void loadPage() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (mListView.getFooterViewsCount() == 0) {
            //show loading
            mListView.addFooterView(mLoadingFooter);
        }

        if (mNotifications == null) {
            mNotifications = new ArrayList<Notification>();
            mAdapter = new NotificationsAdapter(getActivity(), mNotifications);
            mAnimAdapter = new AlphaInAnimationAdapter(mAdapter);
            mAnimAdapter.setAbsListView(mListView);
            mListView.setAdapter(mAnimAdapter);
        }

        if (mPullToRefreshView != null) {
            mPullToRefreshView.setRefreshing(true);
        }

        OneSignalApiClient.getServiceClient(getActivity()).getNotifications(getString(R.string.onesignal_app_id), PAGE_SIZE, mOffset, new Callback<ResponseNotifications>() {
            @Override
            public void success(ResponseNotifications responseNotifications, retrofit.client.Response response) {
                if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                    try {
                        mOffset += responseNotifications.getNotifications().size();
                        mNotifications.addAll(filterNotifications(responseNotifications.getNotifications()));

                        if (mNotifications.isEmpty()) {
                            mNotifications.add(new Notification());
                            mEmptyView.setVisibility(View.VISIBLE);
                            mPullToRefreshView.setVisibility(View.GONE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                            mPullToRefreshView.setVisibility(View.VISIBLE);
                        }

                        mPullToRefreshView.setRefreshing(false);
                        //hide loading
                        mListView.removeFooterView(mLoadingFooter);
                        mAnimAdapter.notifyDataSetChanged();

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                    mPullToRefreshView.setRefreshing(false);
                    //hide loading
                    mListView.removeFooterView(mLoadingFooter);
                    Toast.makeText(getActivity(), R.string.error_connection_detail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public String getScreenName() {
        return getString(R.string.analytics_screen_notifications);
    }

    private List<Notification> filterNotifications(List<Notification> notifications) {
        // Filter by date
        List<Notification> filteredNotifications = new ArrayList<>();
        for (Notification notification : notifications) {
            // 1481155200 = Valid notifications must be greather than 08/12/2016
            if (notification.getQueuedAt() > 1481155200 && notification.isAndroid())
                filteredNotifications.add(notification);
        }

        return filteredNotifications;
    }
}