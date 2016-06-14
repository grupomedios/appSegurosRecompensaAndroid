package com.grupomedios.dclub.segurosrecompensa.discounts.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.grupomedios.dclub.segurosrecompensa.DesclubApplication;
import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.VolleySingleton;
import com.grupomedios.dclub.segurosrecompensa.activity.BaseActivity;
import com.grupomedios.dclub.segurosrecompensa.discounts.activity.DiscountActivity;
import com.grupomedios.dclub.segurosrecompensa.discounts.adapter.DiscountAdapter;
import com.grupomedios.desclub.desclubapi.facade.CategoryFacade;
import com.grupomedios.desclub.desclubapi.facade.DiscountFacade;
import com.grupomedios.desclub.desclubapi.facade.StateFacade;
import com.grupomedios.desclub.desclubapi.facade.ZoneFacade;
import com.grupomedios.desclub.desclubapi.representations.FakeCategoryRepresentation;
import com.grupomedios.desclub.desclubapi.representations.NearByDiscountRepresentation;
import com.grupomedios.desclub.desclubapi.representations.StateRepresentation;
import com.grupomedios.desclub.desclubapi.representations.SubcategoryRepresentation;
import com.grupomedios.desclub.desclubapi.representations.ZoneRepresentation;
import com.grupomedios.desclub.desclubutil.exception.SilentErrorListener;
import com.grupomedios.desclub.desclubutil.exception.SilentListErrorListener;
import com.grupomedios.desclub.desclubutil.gps.GPSService;
import com.grupomedios.desclub.desclubutil.network.volley.GsonRequest;
import com.grupomedios.desclub.desclubutil.ui.dialog.DialogUtil;
import com.grupomedios.desclub.desclubutil.ui.list.PaginableActivity;
import com.grupomedios.desclub.desclubutil.ui.scroll.EndlessScrollListener;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.yalantis.phoenix.PullToRefreshView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by jhoncruz on 28/05/15.
 */
public class DiscountListFragment extends Fragment implements PaginableActivity {

    private final String TAG = "DiscountListFragment";

    BaseAdapter adapter = null;
    AnimationAdapter animAdapter;

    private FakeCategoryRepresentation currentCategory;
    private ListView discountsListView;
    private List<NearByDiscountRepresentation> discountList;

    private View loadingFooter;
    private RequestQueue requestQueue;
    private MaterialDialog progressDialog;
    private PullToRefreshView pullToRefreshView;
    private GPSService gpsService;

    private FloatingActionMenu searchFloatingMenu;

    private MaterialDialog statesDialog;
    private MaterialDialog zonesDialog;
    private MaterialDialog subcategoryDialog;

    private StateRepresentation selectedState;
    private ZoneRepresentation selectedZone;
    private SubcategoryRepresentation selectedSubcategory;

    private List<StateRepresentation> allStates;
    private List<ZoneRepresentation> allZones;
    private List<SubcategoryRepresentation> allSubcategories;

    private View stateIconView;
    private View zoneIconView;
    private View subcategoryIconView;


    @Inject
    DiscountFacade discountFacade;

    @Inject
    StateFacade stateFacade;

    @Inject
    CategoryFacade categoryFacade;

    @Inject
    ZoneFacade zoneFacade;

    /**
     * Click listener for the discount list
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            if (searchFloatingMenu != null) {
                searchFloatingMenu.close(true);
            }

            Intent intent;
            intent = new Intent(getActivity(), DiscountActivity.class);
            intent.putExtra(DiscountActivity.CURRENT_DISCOUNT_PARAM, discountList.get(position).getDiscount());
            intent.putExtra(DiscountActivity.CURRENT_CATEGORY_PARAM, currentCategory);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "----------- onCreate");

        super.onCreate(savedInstanceState);
        ((DesclubApplication) getActivity().getApplication()).inject(this);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        gpsService = new GPSService(getActivity());
        gpsService.getLocation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discounts_list, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            pullToRefreshView = (PullToRefreshView) getView().findViewById(R.id.discount_list_pull_to_refresh);
            pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    resetSearch();

                }
            });

            setupFloatingSearchButton();
            initDialogs();
        }
    }

    private void resetSearch() {
        //reload list
        if (discountList != null) {
            discountList.clear();
        }
        loadDiscounts(currentCategory);
    }

    /**
     * @param text
     * @param icon
     * @param listener
     * @return
     */
    private View createFloatingButton(int text, int icon, View.OnClickListener listener) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View floatingButtonCommonView = inflater.inflate(R.layout.part_floating_search_button, null);

        ImageView buttonImage = (ImageView) floatingButtonCommonView.findViewById(R.id.floatingSearch_image_imageView);
        TextView buttonText = (TextView) floatingButtonCommonView.findViewById(R.id.floatingSearch_text_textView);
        RelativeLayout circleLayout = (RelativeLayout) floatingButtonCommonView.findViewById(R.id.floatingSearch_circle_layout);

        buttonImage.setImageDrawable(getResources().getDrawable(icon));
        buttonImage.setOnClickListener(listener);
        buttonText.setText(getResources().getText(text));
        buttonText.setOnClickListener(listener);

        GradientDrawable blueBackgroundDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.round_background);
        blueBackgroundDrawable.setColor(getResources().getColor(R.color.desclub_blue_dark));
        circleLayout.setBackground(blueBackgroundDrawable);

        return floatingButtonCommonView;

    }

    /**
     * @param view
     * @param text
     */
    private void changeIconViewText(View view, String text) {
        TextView buttonText = (TextView) view.findViewById(R.id.floatingSearch_text_textView);
        buttonText.setText(text);
    }

    /**
     *
     */
    private void setupFloatingSearchButton() {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View floatingButtonCommonView = inflater.inflate(R.layout.part_floating_search_button, null);

        // Set up the white button on the lower right corner
        // more or less with default parameter
        final ImageView fabIconNew = new ImageView(getActivity());
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));

        GradientDrawable blueBackgroundDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.round_background);
        blueBackgroundDrawable.setColor(getResources().getColor(R.color.desclub_blue_dark));

        GradientDrawable transparentBackgroundDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.round_background_transparent);

        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(getActivity())
                .setContentView(fabIconNew)
                .setBackgroundDrawable(blueBackgroundDrawable)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .build();

        stateIconView = createFloatingButton(R.string.state, R.drawable.ic_fullscreen_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statesDialog.show();
            }
        });

        zoneIconView = createFloatingButton(R.string.zone, R.drawable.ic_fullscreen_exit_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zonesDialog.show();
            }
        });

        subcategoryIconView = createFloatingButton(R.string.subcategory, R.drawable.ic_list_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subcategoryDialog.show();
            }
        });

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        searchFloatingMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(subcategoryIconView)
                .addSubActionView(stateIconView)
                .addSubActionView(zoneIconView)
                .attachTo(rightLowerButton)
                .setStartAngle(200)
                .setEndAngle(340)
                .build();

        zoneIconView.setVisibility(View.INVISIBLE);

        // Listen menu open and close events to animate the button content view
        searchFloatingMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_white_24dp));
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
            }
        });
    }

    private void initDialogs() {

        initStatesDialog();
        initZonesDialog();
        initSubcategoryDialog();

    }

    /**
     *
     */
    private void initSubcategoryDialog() {

        subcategoryDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.select_subcategory)
                .positiveText(R.string.filter)
                .cancelable(true)
                .customView(R.layout.part_dialog_subcategories, false)
                .build();

        GsonRequest<SubcategoryRepresentation[]> subcategoriesRequest = categoryFacade.getAllSubcategories(getActivity(), new Response.Listener<SubcategoryRepresentation[]>() {
            @Override
            public void onResponse(final SubcategoryRepresentation[] subcategories) {

                View subcategoriesCustomView = subcategoryDialog.getCustomView();

                Spinner subcategoriesSpinner = (Spinner) subcategoriesCustomView.findViewById(R.id.subcategories_spinner);

                allSubcategories = new ArrayList<SubcategoryRepresentation>();
                allSubcategories.add(new SubcategoryRepresentation("", 0, "Seleccione un subgiro", ""));
                allSubcategories.addAll(Arrays.asList(subcategories));

                ArrayAdapter<SubcategoryRepresentation> dataAdapter = new ArrayAdapter<SubcategoryRepresentation>
                        (getActivity(), R.layout.part_spinner_item, allSubcategories);

                subcategoriesSpinner.setAdapter(dataAdapter);
                subcategoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        selectedSubcategory = allSubcategories.get(position);
                        if (position > 0) {
                            changeIconViewText(subcategoryIconView, selectedSubcategory.getName());
                        } else {
                            changeIconViewText(subcategoryIconView, getResources().getString(R.string.subcategory));
                        }
                        resetSearch();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, new SilentErrorListener(TAG, getActivity(), progressDialog), currentCategory.get_id());

        requestQueue.add(subcategoriesRequest);
    }

    /**
     *
     */
    private void initZonesDialog() {

        zonesDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.select_zone)
                .positiveText(R.string.filter)
                .cancelable(true)
                .customView(R.layout.part_dialog_zones, false)
                .build();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("limit", "50"));

        if (selectedState != null) {
            params.add(new BasicNameValuePair("stateId", selectedState.getStateId().toString()));
        }

        GsonRequest<ZoneRepresentation[]> zonesRequest = zoneFacade.getAllZones(getActivity(), new Response.Listener<ZoneRepresentation[]>() {
            @Override
            public void onResponse(final ZoneRepresentation[] zones) {

                View zonesCustomView = zonesDialog.getCustomView();

                Spinner zonesSpinner = (Spinner) zonesCustomView.findViewById(R.id.zones_spinner);

                allZones = new ArrayList<ZoneRepresentation>();
                allZones.add(new ZoneRepresentation("", 0, "Seleccione una zona"));
                allZones.addAll(Arrays.asList(zones));

                ArrayAdapter<ZoneRepresentation> dataAdapter = new ArrayAdapter<ZoneRepresentation>
                        (getActivity(), R.layout.part_spinner_item, allZones);

                zonesSpinner.setAdapter(dataAdapter);
                zonesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        selectedZone = allZones.get(position);
                        if (position > 0) {
                            changeIconViewText(zoneIconView, selectedZone.getName());
                        } else {
                            changeIconViewText(zoneIconView, getResources().getString(R.string.zone));
                        }
                        resetSearch();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, new SilentErrorListener(TAG, getActivity(), progressDialog), params);

        requestQueue.add(zonesRequest);
    }

    /**
     *
     */
    private void initStatesDialog() {

        statesDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.select_state)
                .positiveText(R.string.filter)
                .cancelable(true)
                .customView(R.layout.part_dialog_states, false)
                .build();

        GsonRequest<StateRepresentation[]> guestAndLoginRequest = stateFacade.getAllStates(getActivity(), new Response.Listener<StateRepresentation[]>() {
            @Override
            public void onResponse(final StateRepresentation[] states) {

                View statesCustomView = statesDialog.getCustomView();

                Spinner statesSpinner = (Spinner) statesCustomView.findViewById(R.id.states_spinner);

                allStates = new ArrayList<StateRepresentation>();
                allStates.add(new StateRepresentation("", 0, "Seleccione un estado"));
                allStates.addAll(Arrays.asList(states));

                ArrayAdapter<StateRepresentation> dataAdapter = new ArrayAdapter<StateRepresentation>
                        (getActivity(), R.layout.part_spinner_item, allStates);

                statesSpinner.setAdapter(dataAdapter);
                statesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        selectedState = allStates.get(position);
                        if (position > 0) {
                            changeIconViewText(stateIconView, selectedState.getName());

                            if (selectedState.getStateId().equals(new Integer(9)) || selectedState.getStateId().equals(new Integer(15))) {
                                zoneIconView.setVisibility(View.VISIBLE);
                                initZonesDialog();
                            } else {
                                selectedZone = null;
                                zoneIconView.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            changeIconViewText(stateIconView, getResources().getString(R.string.state));
                        }

                        resetSearch();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, new SilentErrorListener(TAG, getActivity(), progressDialog));

        requestQueue.add(guestAndLoginRequest);

    }

    public void loadDiscounts(FakeCategoryRepresentation category) {

        Log.d(TAG, " ----------------------------- loadDiscounts");

        this.currentCategory = category;

        if (discountList != null) {
            discountList.clear();
            discountList = new ArrayList<>();
            discountList = null;
            if (animAdapter != null) {
                animAdapter.notifyDataSetChanged();
            }
        }

        discountsListView = (ListView) getView().findViewById(R.id.discounts_listView);
        discountsListView.setOnItemClickListener(itemClickListener);
        discountsListView.setOnScrollListener(new EndlessScrollListener(this));

        loadingFooter = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.part_loading_footer, null, false);

        //set category color
        ((BaseActivity) getActivity()).setActionBarBackgroundColor(getView().getResources().getColor(category.getColor()));
        changeStatusBarColor(category);

        //set category icon
        ImageView categoryImage = (ImageView) getView().findViewById(R.id.toolbar_category_image);
        if (categoryImage != null) {
            categoryImage.setImageResource(category.getImage());
        }


        loadPage();
    }

    @TargetApi(21)
    private void changeStatusBarColor(FakeCategoryRepresentation category) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(category.getColor()));
        }
    }

    @Override
    public void loadPage() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (discountsListView.getFooterViewsCount() == 0) {
            //show loading
            discountsListView.addFooterView(loadingFooter);
        }

        int currentResultsSize = 0;
        if (discountList != null) {
            currentResultsSize = discountList.size();
        }

        if (!currentCategory.get_id().equals("0")) {
            params.add(new BasicNameValuePair("category", currentCategory.get_id()));
        }
        params.add(new BasicNameValuePair("limit", "30"));
        params.add(new BasicNameValuePair("latitude", String.valueOf(gpsService.getLatitude())));
        params.add(new BasicNameValuePair("longitude", String.valueOf(gpsService.getLongitude())));
        if (discountList != null && discountList.size() > 0) {
            params.add(new BasicNameValuePair("minDistance", String.valueOf(discountList.get(discountList.size() - 1).getDis() + 0.00000001)));
        }

        if (selectedState != null) {
            params.add(new BasicNameValuePair("state", selectedState.get_id()));
        }

        if (selectedZone != null) {
            params.add(new BasicNameValuePair("zone", selectedZone.get_id()));
        }

        if (selectedSubcategory != null) {
            params.add(new BasicNameValuePair("subcategory", selectedSubcategory.get_id()));
        }

        if (discountList == null) {
            discountList = new ArrayList<NearByDiscountRepresentation>();
            adapter = new DiscountAdapter(getActivity(), discountList, currentCategory);

            animAdapter = new AlphaInAnimationAdapter(adapter);

            animAdapter.setAbsListView(discountsListView);
            discountsListView.setAdapter(animAdapter);
        }

        progressDialog = DialogUtil.createProgressDialog(getActivity());

        if (pullToRefreshView != null) {
            pullToRefreshView.setRefreshing(true);
        }

        GsonRequest<NearByDiscountRepresentation[]> guestAndLoginRequest = discountFacade.getAllDiscounts(getActivity(), new Response.Listener<NearByDiscountRepresentation[]>() {
            @Override
            public void onResponse(NearByDiscountRepresentation[] hotelModuleRepresentations) {
                Log.d(TAG, String.valueOf(hotelModuleRepresentations.length));

                if (pullToRefreshView != null) {
                    pullToRefreshView.setRefreshing(false);
                }

                //hide loading
                discountsListView.removeFooterView(loadingFooter);

                discountList.addAll(Arrays.asList(hotelModuleRepresentations));
                animAdapter.notifyDataSetChanged();

            }
        }, new SilentListErrorListener(TAG, getActivity(), progressDialog, pullToRefreshView), params);

        requestQueue.add(guestAndLoginRequest);
    }
}