package com.grupomedios.dclub.segurosrecompensa.discounts.activity;

import android.os.Bundle;

import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.activity.DesclubGeneralActivity;
import com.grupomedios.dclub.segurosrecompensa.discounts.fragment.DiscountListFragment;
import com.grupomedios.desclub.desclubapi.representations.FakeCategoryRepresentation;

/**
 * Created by jhoncruz on 28/05/15.
 */
public class DiscountListActivity extends DesclubGeneralActivity {

    public static final String CURRENT_CATEGORY_PARAM = "com.grupomedios.dclub.segurosrecompensa.discounts.activity.DiscountListActivity";

    private final static String TAG = "DiscountListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FakeCategoryRepresentation currentCategory = (FakeCategoryRepresentation) getIntent().getSerializableExtra(CURRENT_CATEGORY_PARAM);

        DiscountListFragment discountListFragment = (DiscountListFragment) getFragmentManager().findFragmentById(R.id.discount_list_fragment);
        discountListFragment.loadDiscounts(currentCategory);

        setTitle(currentCategory.getName());

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_discount_list;
    }

}
