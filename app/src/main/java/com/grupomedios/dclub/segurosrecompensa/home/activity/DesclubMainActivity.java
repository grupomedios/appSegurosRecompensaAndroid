package com.grupomedios.dclub.segurosrecompensa.home.activity;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.fragment.CardFragment;
import com.grupomedios.dclub.segurosrecompensa.activity.DesclubGeneralActivity;
import com.grupomedios.dclub.segurosrecompensa.home.fragment.MainFragment;
import com.grupomedios.dclub.segurosrecompensa.map.fragment.DesclubMapFragment;
import com.grupomedios.dclub.segurosrecompensa.recommended.fragment.RecommendedFragment;
import com.grupomedios.desclub.desclubapi.facade.CorporateMembershipFacade;
import com.grupomedios.desclub.desclubutil.security.UserHelper;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v13.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v13.FragmentPagerItems;

import javax.inject.Inject;

/**
 * Created by jhon on 22/01/15.
 */
public class DesclubMainActivity extends DesclubGeneralActivity {

    private final static String TAG = "DesclubMainActivity";

    private final static Integer REQUEST_ENABLE_BT = 564;

    @Inject
    UserHelper userHelper;

    @Inject
    CorporateMembershipFacade corporateMembershipFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //set tabs
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.home, MainFragment.class)
                .add(R.string.map, DesclubMapFragment.class)
                .add(R.string.recommended, RecommendedFragment.class)
                .add(R.string.card, CardFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        final SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        final LayoutInflater inflater = LayoutInflater.from(viewPagerTab.getContext());

        SmartTabLayout.TabProvider customTabProvider = new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup viewGroup, int position, PagerAdapter pagerAdapter) {

                LinearLayout mainLayout = null;
                switch (position) {
                    //mapa
                    case 1:
                        mainLayout = (LinearLayout) inflater.inflate(R.layout.tab_map, viewGroup, false);
                        break;
                    //recomendados
                    case 2:
                        mainLayout = (LinearLayout) inflater.inflate(R.layout.tab_recommended, viewGroup, false);
                        break;
                    //card
                    case 3:
                        mainLayout = (LinearLayout) inflater.inflate(R.layout.tab_card, viewGroup, false);
                        break;
                    //home
                    default:
                        mainLayout = (LinearLayout) inflater.inflate(R.layout.tab_home, viewGroup, false);
                        break;
                }

                return mainLayout;
            }
        };
        viewPagerTab.setCustomTabView(customTabProvider);
        viewPagerTab.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    //mapa
                    case 1:
                        trackScreen(getString(R.string.analytics_screen_map_tab));
                        break;
                    //recomendados
                    case 2:
                        trackScreen(getString(R.string.analytics_screen_recommended));
                        break;
                    //card
                    case 3:
                        trackScreen(getString(R.string.analytics_screen_card));
                        break;
                    //home
                    default:
                        trackScreen(getString(R.string.analytics_screen_home));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // For first time
        trackScreen(getString(R.string.analytics_screen_home));

        //try to show the initial dialog if conditions are met
        userHelper.showInitialDialog(this, requestQueue, corporateMembershipFacade);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public String getScreenName() {
        return null;
    }

}
