package com.grupomedios.dclub.segurosrecompensa.discounts.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.desclub.desclubapi.representations.FakeCategoryRepresentation;
import com.grupomedios.desclub.desclubapi.representations.NearByDiscountRepresentation;
import com.grupomedios.desclub.desclubutil.ui.image.ImageUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Discount adapter list
 *
 * @author jhon
 */
public class DiscountAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<NearByDiscountRepresentation> discountsList;
    private FakeCategoryRepresentation category;

    public DiscountAdapter(Context context, List<NearByDiscountRepresentation> discountModels, FakeCategoryRepresentation category) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        discountsList = discountModels;
        this.category = category;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return discountsList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        convertView = mInflater.inflate(R.layout.part_discount, parent, false);
        holder = new ViewHolder();
        holder.logoImage = (ImageView) convertView.findViewById(R.id.discount_businessLogo_imageView);
        holder.businessName = (TextView) convertView.findViewById(R.id.discount_businessName_textView);
        holder.businessAddress = (TextView) convertView.findViewById(R.id.discount_businessAddress_textView);
        holder.distance = (TextView) convertView.findViewById(R.id.discount_distance_textView);
        holder.cash = (TextView) convertView.findViewById(R.id.discount_cash_textView);
        holder.card = (TextView) convertView.findViewById(R.id.discount_card_textView);
        holder.promoLayout = (RelativeLayout) convertView.findViewById(R.id.discount_promo_circle_layout);
        holder.parentLayout = (LinearLayout) convertView.findViewById(R.id.discount_parent_layout);
        convertView.setTag(holder);

        NearByDiscountRepresentation dm = discountsList.get(position);

        ImageUtil.displayImage(holder.logoImage, dm.getDiscount().getBrand().getLogoSmall(), null);
        holder.businessName.setText(dm.getDiscount().getBranch().getName());
        String address = dm.getDiscount().getBranch().getStreet() + " " +
                dm.getDiscount().getBranch().getExtNum() + " " +
                dm.getDiscount().getBranch().getIntNum() + " " +
                dm.getDiscount().getBranch().getColony() + ", " +
                dm.getDiscount().getBranch().getZipCode() + ", " +
                dm.getDiscount().getBranch().getCity();
        holder.businessAddress.setText(address);
        holder.distance.setText(calculateDistance(dm.getDis()));
        holder.distance.setTextColor(convertView.getResources().getColor(category.getColor()));

        boolean showCash = false;
        boolean showCard = false;

        if (dm.getDiscount().isCashValueValid()) {
            holder.cash.setTextColor(convertView.getResources().getColor(category.getColor()));
            holder.cash.setText(dm.getDiscount().getCash() + "%");
            showCash = true;
        } else {
            showCash = false;
            holder.parentLayout.removeView(holder.cash);
        }

        if (dm.getDiscount().isCardValueValid()) {
            holder.card.setTextColor(convertView.getResources().getColor(category.getColor()));
            holder.card.setText(dm.getDiscount().getCard() + "%");
            showCard = true;
        } else {
            holder.parentLayout.removeView(holder.card);
            showCard = false;
        }

        if (!showCard && !showCash) {
            holder.parentLayout.removeView(holder.card);
            holder.parentLayout.removeView(holder.cash);

            //customize background color
            GradientDrawable drawable = (GradientDrawable) convertView.getResources().getDrawable(R.drawable.round_background);
            drawable.setColor(convertView.getResources().getColor(category.getColor()));
            holder.promoLayout.setBackgroundDrawable(drawable);
        } else {
            holder.parentLayout.removeView(holder.promoLayout);
        }

        return convertView;
    }


    /**
     * Format the distance in meters or kilometers
     *
     * @param distanceInKm the distance in km
     * @return
     */
    public static String calculateDistance(Double distanceInKm) {
        NumberFormat formatter1 = new DecimalFormat("#0");
        NumberFormat formatter2 = new DecimalFormat("#0.00");

        if (distanceInKm < 1) {
            return formatter1.format(distanceInKm * 1000) + " m";
        } else {
            return formatter2.format(distanceInKm) + " km";
        }
    }

    private static class ViewHolder {
        public ImageView logoImage;
        public TextView businessName;
        public TextView businessAddress;
        public TextView distance;
        public TextView cash;
        public TextView card;
        public RelativeLayout promoLayout;
        public LinearLayout parentLayout;
    }
}
