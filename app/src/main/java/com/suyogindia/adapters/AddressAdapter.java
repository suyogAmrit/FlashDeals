package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suyogindia.flashdeals.R;
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
        holder.txtAddr.setText("Locality: " + addresseList.get(position).getAddress());
        holder.txtCity.setText("City: " + addresseList.get(position).getCity());
        holder.txtState.setText("State: " + addresseList.get(position).getState());
        holder.txtCountry.setText("Country: " + addresseList.get(position).getCountry());
        holder.txtZip.setText("Zip: " + addresseList.get(position).getZip());
        holder.txtPhone.setText("Phone: " + addresseList.get(position).getPhone());
        holder.txtEmail.setText("Email: " + addresseList.get(position).getEmail());
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
    }
}
