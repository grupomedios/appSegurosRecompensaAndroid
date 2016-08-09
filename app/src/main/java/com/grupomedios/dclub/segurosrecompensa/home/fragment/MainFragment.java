package com.grupomedios.dclub.segurosrecompensa.home.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.grupomedios.dclub.segurosrecompensa.DesclubApplication;
import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.discounts.activity.DiscountListActivity;
import com.grupomedios.dclub.segurosrecompensa.fragment.BaseFragment;
import com.grupomedios.dclub.segurosrecompensa.home.adapter.CategoryAdapter;
import com.grupomedios.dclub.segurosrecompensa.home.util.FakeCategoryUtil;
import com.grupomedios.desclub.desclubapi.representations.FakeCategoryRepresentation;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Main {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {

    public static final String LIST_HOTEL_OPTION = "com.beepquest.beepquestandroid.home.fragment.MainFragment";
    private final String TAG = "MainFragment";

    private GridView categoryGridView;
    private List<FakeCategoryRepresentation> categories;

    public MainFragment() {
        // Required empty public constructor
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            FakeCategoryRepresentation fakeCategoryRepresentation = categories.get(position);
            //if not red medica => show category listing
            if (!fakeCategoryRepresentation.get_id().equals("1")) {
                trackEvent(getString(R.string.analytics_category_category), getString(R.string.analytics_event_category_prefix) + fakeCategoryRepresentation.getName());

                Intent intent = new Intent(getActivity(), DiscountListActivity.class);
                intent.putExtra(DiscountListActivity.CURRENT_CATEGORY_PARAM, fakeCategoryRepresentation);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.app_name);
                builder.setMessage(getString(R.string.red_medica_alert_text));
                builder.setIcon(R.drawable.logo_small);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DesclubApplication) getActivity().getApplication()).inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_main, container, false);
        return mainView;

    }


    @Override
    public void onStart() {
        super.onStart();
        categoryGridView = (GridView) getView().findViewById(R.id.categories_gridView);
        categoryGridView.setOnItemClickListener(itemClickListener);

        setUpCategories();

    }

    private void setUpCategories() {

        categories = getFakeCategories();
        BaseAdapter adapter = new CategoryAdapter(getActivity(), categories);

        AnimationAdapter animAdapter = new AlphaInAnimationAdapter(adapter);

        animAdapter.setAbsListView(categoryGridView);
        categoryGridView.setAdapter(animAdapter);

    }

    private List<FakeCategoryRepresentation> getFakeCategories() {

        List<FakeCategoryRepresentation> list = new ArrayList<FakeCategoryRepresentation>();


        list.add(FakeCategoryUtil.buildCategory(getActivity(), getResources().getString(R.string.alimentos_id)));
        list.add(FakeCategoryUtil.buildCategory(getActivity(), getResources().getString(R.string.belleza_salud_id)));
        list.add(FakeCategoryUtil.buildCategory(getActivity(), getResources().getString(R.string.educacion_id)));
        list.add(FakeCategoryUtil.buildCategory(getActivity(), getResources().getString(R.string.entretenimiento_id)));
        list.add(FakeCategoryUtil.buildCategory(getActivity(), getResources().getString(R.string.moda_hogar_id)));
        list.add(FakeCategoryUtil.buildCategory(getActivity(), getResources().getString(R.string.servicios_id)));
        list.add(FakeCategoryUtil.buildCategory(getActivity(), getResources().getString(R.string.turismo_id)));
        list.add(FakeCategoryUtil.buildCategory(getActivity(), "0"));
        //red medica
        //list.add(new FakeCategoryRepresentation("1", getResources().getString(R.string.red_medica), R.drawable.red_medica, R.color.desclub_blue));

        return list;
    }

    @Override
    public String getScreenName() {
        return null;
    }

}