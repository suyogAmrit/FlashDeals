package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suyogindia.flashdeals.R;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.model.OrderItem;
import com.suyogindia.model.Seller;
import com.suyogindia.model.SellerOrders;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/24/2016.
 */

public class OrdersDetailAdapter extends RecyclerView.Adapter<OrdersDetailAdapter.MyOrderDetailsViewHolder>{
    private Context context;
    private ArrayList<OrderItem> list;
    public static final int TYPE_SELLER = 0;
    public static final int TYPE_ITEMS = 1;
    public static final int TYPE_STATUS =2;

    public OrdersDetailAdapter(Context context, ArrayList<OrderItem> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public MyOrderDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_SELLER){
            View view = LayoutInflater.from(context).inflate(R.layout.seller_list,parent,false);
            return new SellersViewHolder(view);
        }
        if (viewType==TYPE_ITEMS){
            View view = LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
            return new ItemsViewHolder(view);
        }
        if (viewType==TYPE_STATUS){
            View view = LayoutInflater.from(context).inflate(R.layout.status_list,parent,false);
            return new StatsusViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MyOrderDetailsViewHolder holder, int position) {
        OrderItem orders = list.get(position);
        switch (orders.getType()) {
            case 0:
            if (holder instanceof SellersViewHolder) {
                SellersViewHolder sellersViewHolder = (SellersViewHolder) holder;
                if (!TextUtils.isEmpty(orders.getSeller_name())) {
                    sellersViewHolder.txtSellerEmail.setText("Seller Name: "+orders.getSeller_name());
                }
                sellersViewHolder.txtSellerOrderId.setText("Order Id: "+orders.getSeller_order_id());
            }
                break;
            case 1:
            if (holder instanceof ItemsViewHolder) {
                ItemsViewHolder itemsViewHolder = (ItemsViewHolder) holder;

                itemsViewHolder.txtItemDesc.setText("About Item: "+orders.getOrders().getDescription());
                itemsViewHolder.txtItemMrp.setText("Item Price: "+ AppConstants.RUPEE+orders.getOrders().getMrp());
                itemsViewHolder.txtItemOfferPrice.setText("Offer Price: "+orders.getOrders().getOffer_price());
                itemsViewHolder.txtItemDiscount.setText("Off: "+orders.getOrders().getDiscount()+"%");
                itemsViewHolder.txtItemOfferStartTime.setText("Offer Starts on: "+orders.getOrders().getOffer_start_time());
                itemsViewHolder.txtItemOfferEndTime.setText("Offer Ends on: "+orders.getOrders().getOffer_end_time());
            }
                break;
            case 2:
                if (holder instanceof StatsusViewHolder){
                    StatsusViewHolder statsusViewHolder = (StatsusViewHolder)holder;
                    if (orders.getDelevery_status().equals("0")){
                        statsusViewHolder.txtDeliveryStatus.setText("Pending");
                    }else if (orders.getDelevery_status().equals("1")){
                        statsusViewHolder.txtDeliveryStatus.setText("Delivered");
                    }else {
                        statsusViewHolder.txtDeliveryStatus.setText("Cancelled");
                    }
                    //statsusViewHolder.txtDeliveryStatus.setText(""+orders.getDelevery_status());
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
        if (list.get(position).getType()==0){
            return TYPE_SELLER;
        }else if (list.get(position).getType()==2){
            return TYPE_STATUS;
        }
        else {
            return TYPE_ITEMS;
        }
    }

    public class SellersViewHolder extends MyOrderDetailsViewHolder{
        TextView txtSellerEmail,txtSellerDlvryMode,txtSellerShippingCharge,txtSellerAddr,txtSellerCity,txtSellerState,txtSellerCountry,txtSellerZip,txtSellerPhone,txtSellerAddrEmail,txtSellerOrderId,txtSellerDeliveryStatus;
        public SellersViewHolder(View itemView) {
            super(itemView);
            txtSellerEmail = (TextView)itemView.findViewById(R.id.txtSellerEmail);
//            txtSellerDlvryMode = (TextView)itemView.findViewById(R.id.txtSellerDlvryMode);
//            txtSellerShippingCharge = (TextView)itemView.findViewById(R.id.txtSellerShippingCharge);
//            txtSellerAddr = (TextView)itemView.findViewById(R.id.txtSellerAddr);
//            txtSellerCity = (TextView)itemView.findViewById(R.id.txtSellerCity);
//            txtSellerState = (TextView)itemView.findViewById(R.id.txtSellerState);
//            txtSellerCountry = (TextView)itemView.findViewById(R.id.txtSellerCountry);
//            txtSellerZip = (TextView)itemView.findViewById(R.id.txtSellerZip);
//            txtSellerPhone = (TextView)itemView.findViewById(R.id.txtSellerPhone);
//            txtSellerAddrEmail = (TextView)itemView.findViewById(R.id.txtSellerAddrEmail);
            txtSellerOrderId = (TextView)itemView.findViewById(R.id.txtSellerOrderId);
            //txtSellerDeliveryStatus = (TextView)itemView.findViewById(R.id.txtSellerDeliveryStatus);
        }
    }
    public class  ItemsViewHolder extends MyOrderDetailsViewHolder{
        TextView txtItemDesc,txtItemMrp,txtItemOfferPrice,txtItemDiscount,txtItemOfferStartTime,txtItemOfferEndTime;
        public ItemsViewHolder(View itemView) {
            super(itemView);
            txtItemDesc = (TextView)itemView.findViewById(R.id.txtItemDesc);
            txtItemMrp = (TextView)itemView.findViewById(R.id.txtItemMrp);
            txtItemOfferPrice = (TextView)itemView.findViewById(R.id.txtItemOfferPrice);
            txtItemDiscount = (TextView)itemView.findViewById(R.id.txtItemDiscount);
            txtItemOfferStartTime = (TextView)itemView.findViewById(R.id.txtItemOfferStartTime);
            txtItemOfferEndTime = (TextView)itemView.findViewById(R.id.txtItemOfferEndTime);
        }
    }
    public class StatsusViewHolder extends MyOrderDetailsViewHolder{
        TextView txtDeliveryStatus;
        public StatsusViewHolder(View itemView) {
            super(itemView);
            txtDeliveryStatus = (TextView)itemView.findViewById(R.id.txtDeliveryStatus);
        }
    }
    public class MyOrderDetailsViewHolder extends RecyclerView.ViewHolder{

        public MyOrderDetailsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
