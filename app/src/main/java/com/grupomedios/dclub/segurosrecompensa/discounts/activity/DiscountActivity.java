package com.grupomedios.dclub.segurosrecompensa.discounts.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.VolleySingleton;
import com.grupomedios.dclub.segurosrecompensa.activity.DesclubGeneralActivity;
import com.grupomedios.dclub.segurosrecompensa.discounts.adapter.DiscountAdapter;
import com.grupomedios.desclub.desclubapi.representations.CorporateMembershipRepresentation;
import com.grupomedios.desclub.desclubapi.representations.DiscountRepresentation;
import com.grupomedios.desclub.desclubapi.representations.FakeCategoryRepresentation;
import com.grupomedios.desclub.desclubutil.gps.GPSService;
import com.grupomedios.desclub.desclubutil.security.UserHelper;
import com.grupomedios.desclub.desclubutil.ui.image.ImageUtil;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

/**
 * Created by jhoncruz on 29/05/15.
 */
public class DiscountActivity extends DesclubGeneralActivity {

    private final static String TAG = "DiscountActivity";

    public static final String CURRENT_DISCOUNT_PARAM = "com.grupomedios.dclub.segurosrecompensa.discounts.activity.DiscountActivity.currentDiscount";
    public static final String CURRENT_CATEGORY_PARAM = "com.grupomedios.dclub.segurosrecompensa.discounts.activity.DiscountActivity.currentCategory";

    private RequestQueue requestQueue;
    private MaterialDialog progressDialog;

    private DiscountRepresentation discount;
    private FakeCategoryRepresentation category;

    private GPSService gpsService;

    @Inject
    UserHelper userHelper;

    private TextView mCardTextView;
    private TextView mCashTextView;
    private TextView mPhoneTextView;
    private View mPhoneContainer;
    private View mCardCashDiscountsContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUi();

        this.discount = (DiscountRepresentation) getIntent().getSerializableExtra(CURRENT_DISCOUNT_PARAM);
        this.category = (FakeCategoryRepresentation) getIntent().getSerializableExtra(CURRENT_CATEGORY_PARAM);

        requestQueue = VolleySingleton.getInstance().getRequestQueue();

        gpsService = new GPSService(this);
        gpsService.getLocation();
        trackEvent(getString(R.string.analytics_category_discount), getString(R.string.analytics_event_discount_prefix) + discount.getBranch().getName());

    }

    private void initUi() {
        mCashTextView = (TextView) findViewById(R.id.viewDiscount_cash_textView);
        mCardTextView = (TextView) findViewById(R.id.viewDiscount_card_textView);
        mPhoneTextView = (TextView) findViewById(R.id.viewDiscount_branch_phone_textView);
        mPhoneContainer = findViewById(R.id.viewDiscount_branch_phone_container);
        mCardCashDiscountsContainer = findViewById(R.id.viewDiscount_card_cash_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //set category color
        this.setActionBarBackgroundColor(getResources().getColor(category.getColor()));
        changeStatusBarColor(category);
        //set title
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        this.setTitle(discount.getBranch().getName());

        setDiscountData();

    }

    @TargetApi(21)
    private void changeStatusBarColor(FakeCategoryRepresentation category) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(category.getColor()));
        }
    }

    private void setDiscountData() {
        boolean showCard = false;
        boolean showCash = false;

        mCashTextView.setTextColor(getResources().getColor(category.getColor()));
        if (discount.isCashValueValid()) {
            mCashTextView.setText(discount.getCash() + "%");
            showCash = true;
        } else {
            showCash = false;
        }

        mCardTextView.setTextColor(getResources().getColor(category.getColor()));
        if (discount.isCardValueValid()) {
            mCardTextView.setText(discount.getCard() + "%");
            showCard = true;
        } else {
            showCard = false;
        }

        if (showCard && !showCash) {
            mCashTextView.setText("0%");
            mCardTextView.setVisibility(View.VISIBLE);
            mCashTextView.setVisibility(View.VISIBLE);
        }

        if (!showCard && showCash) {
            mCardTextView.setText("0%");
            mCardTextView.setVisibility(View.VISIBLE);
            mCashTextView.setVisibility(View.VISIBLE);
        }

        if (!showCard && !showCash) {
            mCardCashDiscountsContainer.setVisibility(View.GONE);
        } else
            mCardCashDiscountsContainer.setVisibility(View.VISIBLE);

        // Show promo
        RelativeLayout promoLayout = (RelativeLayout) findViewById(R.id.viewDiscount_promo_layout);
        promoLayout.setVisibility(View.VISIBLE);

        TextView promoTextView = (TextView) findViewById(R.id.viewDiscount_promo_textView);

        if (discount.getPromo() != null && !discount.getPromo().isEmpty()) {
            promoTextView.setTextColor(getResources().getColor(category.getColor()));
            promoTextView.setText(discount.getPromo());
            promoTextView.setVisibility(View.VISIBLE);
        } else
            promoTextView.setVisibility(View.GONE);

        //logo
        ImageView logoImage = (ImageView) findViewById(R.id.viewDiscount_business_logo_imageView);
        ImageUtil.displayImage(logoImage, discount.getBrand().getLogoSmall(), null);

        //validity
        TextView validity = (TextView) findViewById(R.id.viewDiscount_validity_end_textView);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(discount.getBrand().getValidity_end());
        validity.setText("Vigente hasta: " + formattedDate);

        //name
        TextView businessName = (TextView) findViewById(R.id.viewDiscount_branch_name_textView);
        businessName.setText(discount.getBranch().getName());

        //distance
        if (gpsService.getLocation() != null && discount.getLocation() != null) {
            Location discountLocation = new Location("");
            discountLocation.setLongitude(discount.getLocation().getCoordinates()[0]);
            discountLocation.setLatitude(discount.getLocation().getCoordinates()[1]);
            float distanceInKm = discountLocation.distanceTo(gpsService.getLocation()) / 1000;
            TextView distance = (TextView) findViewById(R.id.viewDiscount_distance_textView);
            distance.setText(DiscountAdapter.calculateDistance(new Double(distanceInKm)));
        }

        // phone
        if (discount.getBranch().getPhone() != null && !discount.getBranch().getPhone().isEmpty()) {
            mPhoneTextView.setText(discount.getBranch().getPhone());
            mPhoneContainer.setVisibility(View.VISIBLE);
            mPhoneTextView.setOnClickListener(mOnClickOnCall);
        } else {
            mPhoneContainer.setVisibility(View.GONE);
            mPhoneTextView.setOnClickListener(null);
        }


        //address
        TextView addressName = (TextView) findViewById(R.id.viewDiscount_branch_address_textView);
        String address = discount.getBranch().getStreet() + " " +
                discount.getBranch().getExtNum() + " " +
                discount.getBranch().getIntNum() + " " +
                discount.getBranch().getColony() + ", " +
                discount.getBranch().getZipCode() + ", " +
                discount.getBranch().getCity();
        addressName.setText(address);

        //restrictions
        TextView restrictions = (TextView) findViewById(R.id.viewDiscount_restrictions_textView);
        restrictions.setText(discount.getRestriction());

        //button color
        RelativeLayout useCouponButton = (RelativeLayout) findViewById(R.id.viewDiscount_coupon_layout);
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.round_background);
        drawable.setColor(getResources().getColor(category.getColor()));
        useCouponButton.setBackgroundDrawable(drawable);
        useCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCoupon();
            }
        });

    }

    /**
     * Toggle visibility of the coupon
     */
    private void toggleCoupon() {

        RelativeLayout viewDiscountLayout = (RelativeLayout) findViewById(R.id.viewDiscount_couponView_layout);
        TextView couponLabel = (TextView) findViewById(R.id.viewDiscount_coupon_label);

        if (viewDiscountLayout.getVisibility() == View.GONE) {
            trackEvent(getString(R.string.analytics_category_coupon), getString(R.string.analytics_event_coupon_prefix) + discount.getBranch().getName());

            //force landscape
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            couponLabel.setText(getString(R.string.hide_coupon));

            LinearLayout membershipMessage = (LinearLayout) findViewById(R.id.viewDiscount_showMembershipMessage);
            RelativeLayout showCardLayout = (RelativeLayout) findViewById(R.id.viewDiscount_showCardLayout);

            TextView memebershipNumber = (TextView) findViewById(R.id.viewDiscount_membershipNumber);
            TextView name = (TextView) findViewById(R.id.viewDiscount_name);
            TextView validThru = (TextView) findViewById(R.id.viewDiscount_validThru);

            if (userHelper.isLoggedIn() && discount.getLocation() != null) {

                Location discountLocation = new Location("");
                discountLocation.setLongitude(discount.getLocation().getCoordinates()[0]);
                discountLocation.setLatitude(discount.getLocation().getCoordinates()[1]);

                float distanceInMeters = discountLocation.distanceTo(gpsService.getLocation());

                if (distanceInMeters < 500) {

                    membershipMessage.setVisibility(View.GONE);
                    showCardLayout.setVisibility(View.VISIBLE);

                    CorporateMembershipRepresentation currentUser = userHelper.getCurrentUser();

                    memebershipNumber.setText(userHelper.getCardNumber());
                    name.setText(currentUser.getName());
                    validThru.setText(userHelper.getValidThru());

                    expand(viewDiscountLayout);
                } else {

                    //force portrait
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    couponLabel.setText(getString(R.string.use_coupon));

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage(getString(R.string.out_of_range));
                    builder.setIcon(R.drawable.logo_small);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }

            } else {
                membershipMessage.setVisibility(View.VISIBLE);
                showCardLayout.setVisibility(View.GONE);

                memebershipNumber.setText("");
                name.setText("");

                expand(viewDiscountLayout);
            }

        } else {
            //force portrait
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            couponLabel.setText(getString(R.string.use_coupon));

            collapse(viewDiscountLayout);
        }

    }

    public static void expand(final View v) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_discount;
    }

    @Override
    public String getScreenName() {
        return getString(R.string.analytics_screen_discount);
    }


    private View.OnClickListener mOnClickOnCall = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", discount.getBranch().getPhone(), null));
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    };
}
