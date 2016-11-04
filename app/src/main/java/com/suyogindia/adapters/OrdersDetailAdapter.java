package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suyogindia.flashdeals.MyOrdersActivity;
import com.suyogindia.flashdeals.R;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.model.OrderItem;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/24/2016.
 */

public class OrdersDetailAdapter extends RecyclerView.Adapter<OrdersDetailAdapter.MyOrderDetailsViewHolder> {
    public static final int TYPE_SELLER = 0;
    public static final int TYPE_ITEMS = 1;
    public static final int TYPE_STATUS = 2;
    private Context context;
    private ArrayList<OrderItem> list;


    public OrdersDetailAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @Override
    public MyOrderDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SELLER) {
            View view = LayoutInflater.from(context).inflate(R.layout.seller_list, parent, false);
            return new SellersViewHolder(view);
        }
        if (viewType == TYPE_ITEMS) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
            return new ItemsViewHolder(view);
        }
        if (viewType == TYPE_STATUS) {
            View view = LayoutInflater.from(context).inflate(R.layout.status_list, parent, false);
            return new StatsusViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MyOrderDetailsViewHolder holder, final int position) {
        final OrderItem orders = list.get(position);
        switch (orders.getType()) {
            case 0:
                if (holder instanceof SellersViewHolder) {
                    SellersViewHolder sellersViewHolder = (SellersViewHolder) holder;
                    if (!TextUtils.isEmpty(orders.getSeller_name())) {
                        sellersViewHolder.txtSellerEmail.setText(orders.getSeller_name());
                    }
                    sellersViewHolder.txtSellerOrderId.setText("Order Id: " + orders.getSeller_order_id());
                    sellersViewHolder.txtSellerAddr.setText(orders.getSeller_address());
                    sellersViewHolder.txtSellerPhone.setText(orders.getPhone());
                }
                break;
            case 1:
                if (holder instanceof ItemsViewHolder) {
                    ItemsViewHolder itemsViewHolder = (ItemsViewHolder) holder;

                    itemsViewHolder.txtItemDesc.setText(orders.getOrders().getDescription());
                    itemsViewHolder.txtItemMrp.setText(AppConstants.RUPEE + orders.getOrders().getMrp());
                    itemsViewHolder.txtItemOfferPrice.setText(orders.getOrders().getOffer_price());
                    itemsViewHolder.txtItemDiscount.setText(orders.getOrders().getDiscount() + "%");
                    itemsViewHolder.txtItemOfferStartTime.setText(orders.getOrders().getOffer_start_time());
                    itemsViewHolder.txtItemOfferEndTime.setText(orders.getOrders().getOffer_end_time());
                }
                break;
            case 2:
                if (holder instanceof StatsusViewHolder) {
                    final StatsusViewHolder statsusViewHolder = (StatsusViewHolder) holder;
                    if (orders.getDelevery_status().equals("0")) {
                        statsusViewHolder.txtDeliveryStatus.setText("Pending");
                        statsusViewHolder.linRadioRate.setVisibility(View.GONE);
                    } else if (orders.getDelevery_status().equals("1")) {
                        statsusViewHolder.txtDeliveryStatus.setText("Delivered");
                        statsusViewHolder.linRadioRate.setVisibility(View.VISIBLE);
                    } else {
                        statsusViewHolder.txtDeliveryStatus.setText("Cancelled");
                        statsusViewHolder.linRadioRate.setVisibility(View.GONE);
                    }
                    if (orders.getUser_delevery_status().equals("1") && orders.getDelevery_status().equals("1")) {
                        statsusViewHolder.relYesNo.setVisibility(View.GONE);
                    } else {
                        statsusViewHolder.relYesNo.setVisibility(View.VISIBLE);
                    }
                    if (orders.getUser_delevery_status().equals("2") && orders.getDelevery_status().equals("1")) {
                        statsusViewHolder.noRadioGrp.setChecked(true);
                    }
                    statsusViewHolder.rdioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            String ids;
                            if (checkedId == R.id.yesRadioGrp) {
                                ids = "1";
                                ((MyOrdersActivity) context).sendRadioRespond(orders.getSeller_order_id(), ids);
                                if (!TextUtils.isEmpty(MyOrdersActivity.responseString)) {
                                    if (MyOrdersActivity.responseString.equals("1")) {
                                        statsusViewHolder.relYesNo.setVisibility(View.GONE);
                                    }
                                }
                            }
                            if (checkedId == R.id.noRadioGrp) {
                                ids = "2";
                                ((MyOrdersActivity) context).sendRadioRespond(orders.getSeller_order_id(), ids);
                                statsusViewHolder.relYesNo.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    Float f = new Float(orders.getRating());
                    statsusViewHolder.ratingSeller.setRating(f);
                    statsusViewHolder.ratingSeller.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            ((MyOrdersActivity) context).rateSeller(orders.getSeller_email(), MyOrdersActivity.userDemoid, orders.getSeller_order_id(), rating);
                        }
                    });

                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if (list.get(position).getType() == 0) {
            return TYPE_SELLER;
        } else if (list.get(position).getType() == 2) {
            return TYPE_STATUS;
        } else {
            return TYPE_ITEMS;
        }
    }

    public void add(ArrayList<OrderItem> itemOrdersFrom) {
        list.clear();
        list.addAll(itemOrdersFrom);
        notifyDataSetChanged();
    }

    public class SellersViewHolder extends MyOrderDetailsViewHolder {
        TextView txtSellerEmail, txtSellerDlvryMode, txtSellerShippingCharge, txtSellerAddr, txtSellerCity, txtSellerState, txtSellerCountry, txtSellerZip, txtSellerPhone, txtSellerAddrEmail, txtSellerOrderId, txtSellerDeliveryStatus;

        public SellersViewHolder(View itemView) {
            super(itemView);
            txtSellerEmail = (TextView) itemView.findViewById(R.id.txtSellerEmail);
//            txtSellerDlvryMode = (TextView)itemView.findViewById(R.id.txtSellerDlvryMode);
//            txtSellerShippingCharge = (TextView)itemView.findViewById(R.id.txtSellerShippingCharge);
            txtSellerAddr = (TextView) itemView.findViewById(R.id.txtSellerAddr);
//            txtSellerCity = (TextView)itemView.findViewById(R.id.txtSellerCity);
//            txtSellerState = (TextView)itemView.findViewById(R.id.txtSellerState);
//            txtSellerCountry = (TextView)itemView.findViewById(R.id.txtSellerCountry);
//            txtSellerZip = (TextView)itemView.findViewById(R.id.txtSellerZip);
            txtSellerPhone = (TextView) itemView.findViewById(R.id.txtSellerPhone);
//            txtSellerAddrEmail = (TextView)itemView.findViewById(R.id.txtSellerAddrEmail);
            txtSellerOrderId = (TextView) itemView.findViewById(R.id.txtSellerOrderId);
            //txtSellerDeliveryStatus = (TextView)itemView.findViewById(R.id.txtSellerDeliveryStatus);
        }
    }

    public class ItemsViewHolder extends MyOrderDetailsViewHolder {
        TextView txtItemDesc, txtItemMrp, txtItemOfferPrice, txtItemDiscount, txtItemOfferStartTime, txtItemOfferEndTime;

        public ItemsViewHolder(View itemView) {
            super(itemView);
            txtItemDesc = (TextView) itemView.findViewById(R.id.txtItemDesc);
            txtItemMrp = (TextView) itemView.findViewById(R.id.txtItemMrp);
            txtItemOfferPrice = (TextView) itemView.findViewById(R.id.txtItemOfferPrice);
            txtItemDiscount = (TextView) itemView.findViewById(R.id.txtItemDiscount);
            txtItemOfferStartTime = (TextView) itemView.findViewById(R.id.txtItemOfferStartTime);
            txtItemOfferEndTime = (TextView) itemView.findViewById(R.id.txtItemOfferEndTime);
        }
    }

    public class StatsusViewHolder extends MyOrderDetailsViewHolder {
        TextView txtDeliveryStatus;
        LinearLayout linRadioRate;
        RelativeLayout relYesNo;
        RadioGroup rdioGrp;
        RadioButton yesRadioGrp, noRadioGrp;
        RatingBar ratingSeller;

        public StatsusViewHolder(View itemView) {
            super(itemView);
            txtDeliveryStatus = (TextView) itemView.findViewById(R.id.txtDeliveryStatus);
            linRadioRate = (LinearLayout) itemView.findViewById(R.id.linRadioRate);
            relYesNo = (RelativeLayout) itemView.findViewById(R.id.relYesNo);
            rdioGrp = (RadioGroup) itemView.findViewById(R.id.rdioGrp);
            yesRadioGrp = (RadioButton) itemView.findViewById(R.id.yesRadioGrp);
            noRadioGrp = (RadioButton) itemView.findViewById(R.id.noRadioGrp);
            ratingSeller = (RatingBar) itemView.findViewById(R.id.ratingSeller);
        }
    }

    public class MyOrderDetailsViewHolder extends RecyclerView.ViewHolder {

        public MyOrderDetailsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
