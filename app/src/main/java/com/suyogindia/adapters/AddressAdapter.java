package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suyogindia.flashdeals.R;
import com.suyogindia.flashdeals.ShowAddressActivity;
import com.suyogindia.helpers.MenuListener;
import com.suyogindia.model.Address;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/20/2016.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private Context context;
    private ArrayList<Address> addresseList;
    private OnItemClickListner listner;
    private MenuListener mListener;
    private boolean isManagedAddr;

    public AddressAdapter(Context context, boolean isManagedAddr) {
        this.context = context;
        this.addresseList = new ArrayList<>();
        this.isManagedAddr = isManagedAddr;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adr_list_items, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        Address myAddress = addresseList.get(position);
        holder.bindData(myAddress);
//        if (addresseList.size() >= 4) {
//            mListener.setMenuItemVisible(false);
//        } else {
//            mListener.setMenuItemVisible(true);
//        }
    }

    @Override
    public int getItemCount() {
        return addresseList.size();
    }

    public void setOnItemclikListner(OnItemClickListner listner1) {
        this.listner = listner1;
    }

    public void add(ArrayList<Address> addresListData) {
        addresseList.addAll(addresListData);
        notifyDataSetChanged();
    }

    public void clear() {
        addresseList.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListner {
        void onItemClick(View view, int position);
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtAddr, txtCity, txtState, txtZip, txtPhone,txtPlotNo;
        LinearLayout btnSelect,txtEdit,txtRemove;

        public AddressViewHolder(View itemView) {
            super(itemView);
            txtAddr = (TextView) itemView.findViewById(R.id.txtAddr);
            txtCity = (TextView) itemView.findViewById(R.id.txtCity);
            txtState = (TextView) itemView.findViewById(R.id.txtState);
            txtZip = (TextView) itemView.findViewById(R.id.txtZip);
            txtPhone = (TextView) itemView.findViewById(R.id.txtPhone);
            txtEdit = (LinearLayout) itemView.findViewById(R.id.txtEdit);
            txtPlotNo = (TextView)itemView.findViewById(R.id.txtPlotNo);
            txtRemove = (LinearLayout) itemView.findViewById(R.id.txtRemove);
            btnSelect = (LinearLayout) itemView.findViewById(R.id.btn_address_select);
//            txtEdit.setOnClickListener(this);
//            txtRemove.setOnClickListener(this);
            btnSelect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!isManagedAddr && listner != null) {
                listner.onItemClick(v, getAdapterPosition());

            }
        }

        public void bindData(Address myAddress) {
            txtAddr.setText(myAddress.getAddress());
            txtCity.setText(myAddress.getCity());
            txtState.setText(myAddress.getState());
            txtZip.setText(myAddress.getZip());
            txtPhone.setText(myAddress.getPhone());
            txtPlotNo.setText(myAddress.getPlotno());
            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((ShowAddressActivity) context).editAddress(getAdapterPosition());
                }
            });
            txtRemove.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ((ShowAddressActivity) context).deleteAddress(getAdapterPosition());
                                             }
                                         }

            );
        }
    }
}
