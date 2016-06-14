package com.grupomedios.dclub.segurosrecompensa;

import com.grupomedios.dclub.segurosrecompensa.fragment.CardFragment;
import com.grupomedios.dclub.segurosrecompensa.activity.BaseActivity;
import com.grupomedios.dclub.segurosrecompensa.activity.DesclubGeneralActivity;
import com.grupomedios.dclub.segurosrecompensa.discounts.activity.DiscountActivity;
import com.grupomedios.dclub.segurosrecompensa.discounts.activity.DiscountListActivity;
import com.grupomedios.dclub.segurosrecompensa.discounts.fragment.DiscountListFragment;
import com.grupomedios.dclub.segurosrecompensa.home.activity.DesclubMainActivity;
import com.grupomedios.dclub.segurosrecompensa.home.activity.SplashActivity;
import com.grupomedios.dclub.segurosrecompensa.home.fragment.MainFragment;
import com.grupomedios.dclub.segurosrecompensa.map.fragment.DesclubMapFragment;
import com.grupomedios.dclub.segurosrecompensa.recommended.fragment.RecommendedFragment;
import com.grupomedios.desclub.desclubutil.MCXModule;

import dagger.Module;

/**
 * Created by jhon on 22/01/15.
 */
@Module(
        injects = {
                DesclubApplication.class,
                SplashActivity.class,
                DesclubGeneralActivity.class,
                BaseActivity.class,
                DesclubMainActivity.class,
                DiscountListActivity.class,
                DiscountActivity.class,

                MainFragment.class,
                DesclubMapFragment.class,
                RecommendedFragment.class,
                CardFragment.class,
                DiscountListFragment.class

        },
        includes = {
                MCXModule.class
        },
        overrides = true
)
public class DesclubModule {

}
