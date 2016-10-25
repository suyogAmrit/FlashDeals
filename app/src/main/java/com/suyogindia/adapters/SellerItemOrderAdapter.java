package com.suyogindia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.suyogindia.flashdeals.R;
import com.suyogindia.model.SellerOrders;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class SellerItemOrderAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<SellerOrders> sellerOrderseList;
    public SellerItemOrderAdapter(Context context, ArrayList<SellerOrders> sellerOrderseList) {
        this.context = context;
        this.sellerOrderseList = sellerOrderseList;
    }
    @Override
    public int getGroupCount() {
        return sellerOrderseList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sellerOrderseList.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sellerOrderseList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sellerOrderseList.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        SellerViewHolder holder = null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.seller_list,parent,false);
            holder = new SellerViewHolder();
            holder.txtSellerEmail = (TextView)convertView.findViewById(R.id.txtSellerEmail);
//            holder.txtSellerDlvryMode = (TextView)convertView.findViewById(R.id.txtSellerDlvryMode);
//            holder.txtSellerShippingCharge = (TextView)convertView.findViewById(R.id.txtSellerShippingCharge);
//            holder.txtSellerAddr = (TextView)convertView.findViewById(R.id.txtSellerAddr);
//            holder.txtSellerCity = (TextView)convertView.findViewById(R.id.txtSellerCity);
//            holder.txtSellerState = (TextView)convertView.findViewById(R.id.txtSellerState);
//            holder.txtSellerCountry = (TextView)convertView.findViewById(R.id.txtSellerCountry);
//            holder.txtSellerZip = (TextView)convertView.findViewById(R.id.txtSellerZip);
//            holder.txtSellerPhone = (TextView)convertView.findViewById(R.id.txtSellerPhone);
//            holder.txtSellerAddrEmail = (TextView)convertView.findViewById(R.id.txtSellerAddrEmail);
            convertView.setTag(holder);
        }else {
            holder = (SellerViewHolder) convertView.getTag();
        }
        holder.txtSellerEmail.setText(sellerOrderseList.get(groupPosition).getSeller_name());
        holder.txtSellerDlvryMode.setText(sellerOrderseList.get(groupPosition).getDelevery_mode());
        holder.txtSellerShippingCharge.setText(sellerOrderseList.get(groupPosition).getShipping_charge());
        holder.txtSellerAddr.setText(sellerOrderseList.get(groupPosition).getAddress());
        holder.txtSellerCity.setText(sellerOrderseList.get(groupPosition).getCity());
        holder.txtSellerState.setText(sellerOrderseList.get(groupPosition).getState());
        holder.txtSellerCountry.setText(sellerOrderseList.get(groupPosition).getCountry());
        holder.txtSellerZip.setText(sellerOrderseList.get(groupPosition).getZip());
        holder.txtSellerPhone.setText(sellerOrderseList.get(groupPosition).getPhone());
        holder.txtSellerAddrEmail.setText(sellerOrderseList.get(groupPosition).getEmail());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder holder =null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
            holder = new ItemViewHolder();
            holder.txtItemDesc = (TextView)convertView.findViewById(R.id.txtItemDesc);
            holder.txtItemMrp = (TextView)convertView.findViewById(R.id.txtItemMrp);
            holder.txtItemOfferPrice = (TextView)convertView.findViewById(R.id.txtItemOfferPrice);
            holder.txtItemDiscount = (TextView)convertView.findViewById(R.id.txtItemDiscount);
            holder.txtItemOfferStartTime = (TextView)convertView.findViewById(R.id.txtItemOfferStartTime);
            holder.txtItemOfferEndTime = (TextView)convertView.findViewById(R.id.txtItemOfferEndTime);
            convertView.setTag(holder);
        }else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        holder.txtItemDesc.setText(sellerOrderseList.get(groupPosition).getItems().get(childPosition).getDescription());
        holder.txtItemMrp.setText(sellerOrderseList.get(groupPosition).getItems().get(childPosition).getMrp());
        holder.txtItemOfferPrice.setText(sellerOrderseList.get(groupPosition).getItems().get(childPosition).getOffer_price());
        holder.txtItemDiscount.setText(sellerOrderseList.get(groupPosition).getItems().get(childPosition).getDiscount());
        holder.txtItemOfferStartTime.setText(sellerOrderseList.get(groupPosition).getItems().get(childPosition).getOffer_start_time());
        holder.txtItemOfferEndTime.setText(sellerOrderseList.get(groupPosition).getItems().get(childPosition).getOffer_end_time());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    public class SellerViewHolder{
        TextView txtSellerEmail,txtSellerDlvryMode,txtSellerShippingCharge,txtSellerAddr,txtSellerCity,txtSellerState,txtSellerCountry,txtSellerZip,txtSellerPhone,txtSellerAddrEmail;
    }
    public class ItemViewHolder{
        TextView txtItemDesc,txtItemMrp,txtItemOfferPrice,txtItemDiscount,txtItemOfferStartTime,txtItemOfferEndTime;
    }
}
