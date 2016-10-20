package com.suyogindia.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suyogindia.flashdeals.AddAddressActivity;
import com.suyogindia.flashdeals.R;
import com.suyogindia.flashdeals.ShowAddressActivity;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.model.Address;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/20/2016.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private Context context;
    private ArrayList<Address> addresseList;
    private OnItemClickListner listner;

    public AddressAdapter(Context context, ArrayList<Address> addresseList) {
        this.context = context;
        this.addresseList = addresseList;
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
    }

    @Override
    public int getItemCount() {
        return addresseList.size();
    }

    public void setOnItemclikListner(OnItemClickListner listner1) {
        this.listner = listner1;
    }

    public interface OnItemClickListner {
        void onItemClick(View view, int position);
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtAddr, txtCity, txtState, txtCountry, txtZip, txtPhone, txtEmail, txtEdit, txtRemove;

        public AddressViewHolder(View itemView) {
            super(itemView);
            txtAddr = (TextView) itemView.findViewById(R.id.txtAddr);
            txtCity = (TextView) itemView.findViewById(R.id.txtCity);
            txtState = (TextView) itemView.findViewById(R.id.txtState);
            txtCountry = (TextView) itemView.findViewById(R.id.txtCountry);
            txtZip = (TextView) itemView.findViewById(R.id.txtZip);
            txtPhone = (TextView) itemView.findViewById(R.id.txtPhone);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            txtEdit = (TextView) itemView.findViewById(R.id.txtEdit);
            txtRemove = (TextView) itemView.findViewById(R.id.txtRemove);
            txtEdit.setOnClickListener(this);
            txtRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listner != null) {
                listner.onItemClick(v, getAdapterPosition());
            }
        }

        public void bindData(Address myAddress) {
            txtAddr.setText("Locality: " + myAddress.getAddress());
            txtCity.setText("City: " + myAddress.getCity());
            txtState.setText("State: " + myAddress.getState());
            txtCountry.setText("Country: " + myAddress.getCountry());
            txtZip.setText("Zip: " + myAddress.getZip());
            txtPhone.setText("Phone: " + myAddress.getPhone());
            txtEmail.setText("Email: " + myAddress.getEmail());
            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddAddressActivity.class);
                    intent.putExtra(AppConstants.EXTRA_ADDRESS, addresseList.get(getAdapterPosition()));
                    context.startActivity(intent);
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
