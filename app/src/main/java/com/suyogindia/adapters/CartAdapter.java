package com.suyogindia.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.suyogindia.flashdeals.CartActivity;
import com.suyogindia.flashdeals.DealsDetailsActivity;
import com.suyogindia.flashdeals.R;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.model.CartItem;
import com.suyogindia.model.Seller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyogcomputech on 19/10/16.
 */

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_GRAND = 3;

    ArrayList<CartItem> cartDataList;
    Context myContext;

    public CartAdapter(Context myContext) {
        cartDataList = new ArrayList<>();
        this.myContext = myContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_header, parent, false);
            return new HeaderViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_generic, parent, false);
            return new GenericViewHolder(v);
        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_footer, parent, false);
            return new FooterViewHolder(v);
        } else if (viewType == TYPE_GRAND) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_grand, parent, false);
            return new GrandViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CartItem item = cartDataList.get(position);
        switch (item.getType()) {
            case 0:
                if (holder instanceof HeaderViewHolder) {
                    HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                    headerHolder.tvSellerName.setText(item.getCategory());
                }
                break;
            case 1:
                if (holder instanceof GenericViewHolder) {
                    GenericViewHolder genericViewHolder = (GenericViewHolder) holder;
                    genericViewHolder.bindData(item);
                }
                break;
            case 2:
                if (holder instanceof FooterViewHolder) {
                    FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                    footerViewHolder.bindData(item);
                }
                break;
            case 3:
                if (holder instanceof GrandViewHolder) {
                    GrandViewHolder grandViewHolder = (GrandViewHolder) holder;
                    grandViewHolder.bindData(item);
                }
                break;
        }
    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if (cartDataList.get(position).getType() == 0) {
            return TYPE_HEADER;
        } else if (cartDataList.get(position).getType() == 2) {
            return TYPE_FOOTER;
        } else if (cartDataList.get(position).getType() == 3) {
            return TYPE_GRAND;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return cartDataList.size();
    }

    public void add(List<CartItem> list) {
        cartDataList.clear();
        cartDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        cartDataList.clear();
        notifyDataSetChanged();
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSellerName;

        public HeaderViewHolder(View v) {
            super(v);
            tvSellerName = (TextView) v.findViewById(R.id.tv_cart_seller);
        }
    }

    private class GrandViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotalQty, tvTotalPrice;

        public GrandViewHolder(View v) {
            super(v);
            tvTotalQty = (TextView) v.findViewById(R.id.tv_total_qty);
            tvTotalPrice = (TextView) v.findViewById(R.id.tv_final_grand_total);
        }

        public void bindData(CartItem item) {
            Log.i("total", item.getGrandTotal());
            tvTotalQty.setText(item.getTotalQuantity().substring(0, item.getTotalQuantity().length() - 2));
            tvTotalPrice.setText(AppConstants.RUPEE + item.getGrandTotal());
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotal, tvshippingText, tvShippingCharge, tvGrandTotal, tvSellerShippingInfo;
        RadioGroup rgMode;

        public FooterViewHolder(View v) {
            super(v);
            tvGrandTotal = (TextView) v.findViewById(R.id.tv_grand_total);
            tvShippingCharge = (TextView) v.findViewById(R.id.tv_shipping_charge);
            tvshippingText = (TextView) v.findViewById(R.id.tv_text_shipping);
            tvTotal = (TextView) v.findViewById(R.id.tv_seller_total);
            tvSellerShippingInfo = (TextView) v.findViewById(R.id.tv_seller_shipping_info);
            rgMode = (RadioGroup) v.findViewById(R.id.rg_mode_delevery);
        }

        public void bindData(CartItem item) {

            final Seller mySeller = item.getSeller();
            tvTotal.setText(mySeller.getTotalPrice());
            tvShippingCharge.setText(AppConstants.RUPEE + mySeller.getShippingCharge());
            tvSellerShippingInfo.setText("This seller Provides Free Home delivery above " + AppConstants.RUPEE + mySeller.getMaxPrice()
                    + " – below " + AppConstants.RUPEE + mySeller.getMaxPrice() + ", " + AppConstants.RUPEE + mySeller.getShippingCharge() + " chargable.");
            rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_home_delivery:
                            setShippingValue(1);
                            break;
                        case R.id.rb_pick_up:
                            setShippingValue(2);
                            break;
                    }
                }
            });
            if (mySeller.getShippingAdded() == 1) {
                tvshippingText.setVisibility(View.VISIBLE);
                tvShippingCharge.setVisibility(View.VISIBLE);
                double grandTotal = Double.parseDouble(mySeller.getTotalPrice()) + Double.parseDouble(mySeller.getMaxPrice());
                tvGrandTotal.setText(String.valueOf(grandTotal));
            } else {
                tvshippingText.setVisibility(View.GONE);
                tvShippingCharge.setVisibility(View.GONE);
                tvGrandTotal.setText(AppConstants.RUPEE + mySeller.getTotalPrice());
            }
        }

        private void setShippingValue(int a) {

            ((CartActivity) myContext).updateDeliveryInfoAndTotalPrice(getAdapterPosition(), a);

        }
    }

    private class GenericViewHolder extends RecyclerView.ViewHolder {
        Button btnEdit, btnRemove;
        TextView tvDesc, tvOfferPrice, tvQty, tvTotal;

        public GenericViewHolder(View v) {
            super(v);
            btnEdit = (Button) v.findViewById(R.id.btn_cart_edit);
            btnRemove = (Button) v.findViewById(R.id.btn_cart_remove);
            tvDesc = (TextView) v.findViewById(R.id.tv_cart_desc);
            tvOfferPrice = (TextView) v.findViewById(R.id.tv_cart_offer_price);
            tvQty = (TextView) v.findViewById(R.id.tv_cart_qty);
            tvTotal = (TextView) v.findViewById(R.id.tv_cart_total);
        }

        public void bindData(CartItem item) {
            tvTotal.setText(AppConstants.RUPEE + item.getTotalPrice());
            tvQty.setText(item.getQty());
            tvOfferPrice.setText(AppConstants.RUPEE + item.getOfferPrice());
            tvDesc.setText(item.getDesc());
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CartActivity) myContext).removeItem(cartDataList.get(getAdapterPosition()).getDelaId());
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(myContext, DealsDetailsActivity.class);
                    i.putExtra(AppConstants.CARTITEM, cartDataList.get(getAdapterPosition()));
                    i.putExtra(AppConstants.DETAILSTYPE, 1);
                    myContext.startActivity(i);
                    ((CartActivity) myContext).finish();
                }
            });
        }
    }
}