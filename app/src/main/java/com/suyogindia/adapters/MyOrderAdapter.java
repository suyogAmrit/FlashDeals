package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suyogindia.flashdeals.R;
import com.suyogindia.helpers.OnMyOrderItemClickListener;
import com.suyogindia.model.Orders;

import java.util.ArrayList;

/**
 * Created by Tanmay on 10/21/2016.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder>{
    private Context context;
    private ArrayList<Orders> orderseList;
    private OnMyOrderItemClickListener listener;

    public MyOrderAdapter(Context context, ArrayList<Orders> orderseList) {
        this.context = context;
        this.orderseList = orderseList;
    }

    @Override
    public MyOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_orders_items,parent,false);
        return new MyOrderViewHolder(view);
    }
    public void setMyorderClickListener(OnMyOrderItemClickListener listener1){
        this.listener = listener1;
    }

    @Override
    public void onBindViewHolder(MyOrderViewHolder holder, int position) {
        Orders orders = orderseList.get(position);
        holder.txtOrderId.setText("OrderId: "+orders.getOrder_id());
        holder.txtModeOfPayment.setText("Mode Of Payment: "+orders.getMode_of_payment());
        holder.txtPaymentStatus.setText("Payment Status: "+ orders.getPayment_status());
        holder.txtTotalPrice.setText("Total Price: "+orders.getTotal_price());
        holder.txtOrderStatus.setText("Order Message: "+orders.getOrder_status());
    }

    @Override
    public int getItemCount() {
        return orderseList.size();
    }

    public class MyOrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtOrderId,txtModeOfPayment,txtPaymentStatus,txtTotalPrice,txtOrderStatus;
        public MyOrderViewHolder(View itemView) {
            super(itemView);
            txtOrderId = (TextView)itemView.findViewById(R.id.txtOrderId);
            txtModeOfPayment = (TextView)itemView.findViewById(R.id.txtModeOfPayment);
            txtPaymentStatus = (TextView)itemView.findViewById(R.id.txtPaymentStatus);
            txtTotalPrice = (TextView)itemView.findViewById(R.id.txtTotalPrice);
            txtOrderStatus = (TextView)itemView.findViewById(R.id.txtOrderStatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener!=null){
                listener.onMyOrderItemclick(v,getAdapterPosition());
            }
        }
    }
}
