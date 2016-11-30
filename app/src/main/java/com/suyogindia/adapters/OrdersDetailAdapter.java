package com.suyogindia.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.suyogindia.flashdeals.OrdersActivity;
import com.suyogindia.flashdeals.R;
import com.suyogindia.flashdeals.SellerMapActivity;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.model.ItemOrder;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/24/2016.
 */

public class OrdersDetailAdapter extends RecyclerView.Adapter<OrdersDetailAdapter.MyOrderDetailsViewHolder> {
    public static final int TYPE_SELLER = 0;
    public static final int TYPE_ITEMS = 1;
    public static final int TYPE_DELIVERY = 2;
    public static final int TYPE_STATUS = 3;
    private Context context;
    //private ArrayList<OrderItem> list;
    private ArrayList<ItemOrder> list;


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
        if (viewType == TYPE_DELIVERY) {
            View view = LayoutInflater.from(context).inflate(R.layout.delivery_info, parent, false);
            return new DeliveryViewHolder(view);
        }
        if (viewType == TYPE_STATUS) {
            View view = LayoutInflater.from(context).inflate(R.layout.status_list, parent, false);
            return new StatsusViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MyOrderDetailsViewHolder holder, final int position) {
        final ItemOrder orders = list.get(position);
        switch (orders.getType()) {
            case 0:
                if (holder instanceof SellersViewHolder) {
                    SellersViewHolder sellersViewHolder = (SellersViewHolder) holder;
                    if (!TextUtils.isEmpty(orders.getSeller_name())) {
                        sellersViewHolder.txtSellerEmail.setText(orders.getSeller_name());
                    }
                    sellersViewHolder.txtSellerOrderId.setText("Order Id: " + orders.getSeller_order_id());
                    sellersViewHolder.txtSellerAddr.setText(orders.getSeller_address());
                    sellersViewHolder.txtSellerPhone.setText(orders.getContact_number());
                    sellersViewHolder.txtSellerShippingCharge.setText(AppConstants.RUPEE + orders.getShipping_charge());
                    sellersViewHolder.txtSellerTotalPrice.setText(AppConstants.RUPEE + orders.getSeller_total_price());
                    sellersViewHolder.imgCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((OrdersActivity) context).requestCallSeller(orders.getContact_number());
                        }
                    });
                    sellersViewHolder.txtSellerOrderDate.setText(orders.getOrder_date());
                    sellersViewHolder.imgSellerLoc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SellerMapActivity.class);
                            intent.putExtra(AppConstants.EXTRA_SELLER_NAME,orders.getSeller_name());
                            intent.putExtra(AppConstants.EXTRA_LATTITUDE,orders.getLatitude());
                            intent.putExtra(AppConstants.EXTRA_LONGITUDE, orders.getLongitude());
                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(intent);
                            }else {
                                Toast.makeText(context,"Your Device Not Supporing Google Map",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                break;
            case 1:
                if (holder instanceof ItemsViewHolder) {
                    ItemsViewHolder itemsViewHolder = (ItemsViewHolder) holder;

                    itemsViewHolder.txtItemDesc.setText(orders.getItem().getDescription());
                    itemsViewHolder.txtItemMrp.setText(AppConstants.RUPEE + orders.getItem().getMrp());
                    itemsViewHolder.txtItemOfferPrice.setText(AppConstants.RUPEE + orders.getItem().getOffer_price());
                    itemsViewHolder.txtItemDiscount.setText(orders.getItem().getDiscount() + "%");
                    itemsViewHolder.txtItemOfferStartTime.setText(orders.getItem().getOffer_start_time());
                    itemsViewHolder.txtItemOfferEndTime.setText(orders.getItem().getOffer_end_time());
                    itemsViewHolder.txtItemTotalAmount.setText(AppConstants.RUPEE + orders.getItem().getTotal_price());
                    itemsViewHolder.txtQuantity.setText("" + orders.getItem().getQuantity());
                    itemsViewHolder.txtOrderDate.setText(orders.getItem().getOrder_date());
                    itemsViewHolder.tvOrderId.setText(orders.getItem().getNew_order_id());

                    Glide.with(context)
                            .load(orders.getItem().getImage_url())
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(80, 80)
                            .placeholder(R.drawable.ic_picasa)
                            .into(itemsViewHolder.ivDeal);
                }
                break;
            case 2:
                if (holder instanceof DeliveryViewHolder) {
                    DeliveryViewHolder deliveryViewHolder = (DeliveryViewHolder) holder;
                    if (orders.getDelevery_info().getDelevery_mode().equals("2")) {
                        deliveryViewHolder.txtdeliveryInfo.setText("Please pick from Seller");
                    } else {
                        deliveryViewHolder.txtdeliveryInfo.setText("Delivery Address : " + orders.getDelevery_info().getAddress() + "," +
                                orders.getDelevery_info().getCity() + "," + orders.getDelevery_info().getState() + "," + orders.getDelevery_info().getCountry()
                                + "," + orders.getDelevery_info().getZip() + "," + orders.getDelevery_info().getEmail() + "," + orders.getDelevery_info().getPhone());
                    }
                }
                break;
            case 3:
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
                                ((OrdersActivity) context).sendRadioRespond(orders.getSeller_order_id(), ids);
                                if (!TextUtils.isEmpty(OrdersActivity.responseString)) {
                                    if (OrdersActivity.responseString.equals("1")) {
                                        statsusViewHolder.relYesNo.setVisibility(View.GONE);
                                    }
                                }
                            }
                            if (checkedId == R.id.noRadioGrp) {
                                ids = "2";
                                ((OrdersActivity) context).sendRadioRespond(orders.getSeller_order_id(), ids);
                                statsusViewHolder.relYesNo.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    // Float f = Float.valueOf(orders.getRating());
                    statsusViewHolder.ratingSeller.setNumStars(5);
                    statsusViewHolder.ratingSeller.setRating(orders.getRating());
                    statsusViewHolder.ratingSeller.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            ((OrdersActivity) context).rateSeller(orders.getSeller_email(), OrdersActivity.userDemoid, orders.getSeller_order_id(), rating);
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
        if (list.get(position).getType() == 0) {
            return TYPE_SELLER;
        } else if (list.get(position).getType() == 2) {
            return TYPE_DELIVERY;
        } else if (list.get(position).getType() == 3) {
            return TYPE_STATUS;
        } else {
            return TYPE_ITEMS;
        }
    }

    public void add(ArrayList<ItemOrder> itemOrdersFrom) {
        list.clear();
        list.addAll(itemOrdersFrom);
        notifyDataSetChanged();
    }

    public class SellersViewHolder extends MyOrderDetailsViewHolder {
        TextView txtSellerEmail, txtSellerDlvryMode, txtSellerShippingCharge, txtSellerAddr, txtSellerCity, txtSellerState, txtSellerCountry, txtSellerZip, txtSellerPhone, txtSellerAddrEmail, txtSellerOrderId, txtSellerDeliveryStatus, txtSellerTotalPrice, txtSellerOrderDate;
        ImageView imgCall,imgSellerLoc;

        public SellersViewHolder(View itemView) {
            super(itemView);
            txtSellerEmail = (TextView) itemView.findViewById(R.id.txtSellerEmail);
//            txtSellerDlvryMode = (TextView)itemView.findViewById(R.id.txtSellerDlvryMode);
            txtSellerShippingCharge = (TextView) itemView.findViewById(R.id.txtSellerShippingCharge);
            txtSellerAddr = (TextView) itemView.findViewById(R.id.txtSellerAddr);
//            txtSellerCity = (TextView)itemView.findViewById(R.id.txtSellerCity);
//            txtSellerState = (TextView)itemView.findViewById(R.id.txtSellerState);
//            txtSellerCountry = (TextView)itemView.findViewById(R.id.txtSellerCountry);
//            txtSellerZip = (TextView)itemView.findViewById(R.id.txtSellerZip);
            txtSellerPhone = (TextView) itemView.findViewById(R.id.txtSellerPhone);
//            txtSellerAddrEmail = (TextView)itemView.findViewById(R.id.txtSellerAddrEmail);
            txtSellerOrderId = (TextView) itemView.findViewById(R.id.txtSellerOrderId);
            //txtSellerDeliveryStatus = (TextView)itemView.findViewById(R.id.txtSellerDeliveryStatus);
            txtSellerTotalPrice = (TextView) itemView.findViewById(R.id.txtSellerTotalPrice);
            imgCall = (ImageView) itemView.findViewById(R.id.imgCall);
            txtSellerOrderDate = (TextView) itemView.findViewById(R.id.txtSellerOrderDate);
            imgSellerLoc=(ImageView)itemView.findViewById(R.id.imgSellerLoc);
        }
    }

    public class ItemsViewHolder extends MyOrderDetailsViewHolder {
        TextView txtItemDesc, txtItemMrp, txtItemOfferPrice, txtItemDiscount, txtItemOfferStartTime, txtItemOfferEndTime, txtItemTotalAmount, txtQuantity, txtOrderDate;
        ImageView ivDeal;
        TextView tvOrderId;

        public ItemsViewHolder(View itemView) {
            super(itemView);
            txtItemDesc = (TextView) itemView.findViewById(R.id.txtItemDesc);
            txtItemMrp = (TextView) itemView.findViewById(R.id.txtItemMrp);
            txtItemOfferPrice = (TextView) itemView.findViewById(R.id.txtItemOfferPrice);
            txtItemDiscount = (TextView) itemView.findViewById(R.id.txtItemDiscount);
            txtItemOfferStartTime = (TextView) itemView.findViewById(R.id.txtItemOfferStartTime);
            txtItemOfferEndTime = (TextView) itemView.findViewById(R.id.txtItemOfferEndTime);
            txtItemTotalAmount = (TextView) itemView.findViewById(R.id.txtItemTotalAmount);
            txtQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
            txtOrderDate = (TextView) itemView.findViewById(R.id.txtOrderDate);
            ivDeal = (ImageView) itemView.findViewById(R.id.iv_deal);
            tvOrderId = (TextView) itemView.findViewById(R.id.tvOrder_id);

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

    public class DeliveryViewHolder extends MyOrderDetailsViewHolder {
        TextView txtdeliveryInfo;

        public DeliveryViewHolder(View itemView) {
            super(itemView);
            txtdeliveryInfo = (TextView) itemView.findViewById(R.id.txtdeliveryInfo);
        }
    }

    public class MyOrderDetailsViewHolder extends RecyclerView.ViewHolder {

        public MyOrderDetailsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
