package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suyogindia.flashdeals.OrderReviewActivity;
import com.suyogindia.flashdeals.R;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.model.ReviewOrderItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by suyogcomputech on 27/10/16.
 */
public class OrderReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_SELLER_FOOTER = 3;
    private static final int TYPE_GRAND = 4;
    private static final int TYPE_ADDRESS = 5;
    Context mContext;
    ArrayList<ReviewOrderItem> list;

    public OrderReviewAdapter(Context context) {
        mContext = context;
        list = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_header, parent, false);
            return new SellerHeaderViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_generic, parent, false);
            return new ItemViewHolder(v);
        } else if (viewType == TYPE_SELLER_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_seller, parent, false);
            return new SellerFooterHolder(v);
        } else if (viewType == TYPE_GRAND) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_seller, parent, false);
            return new GrandTotalHolder(v);
        } else if (viewType == TYPE_ADDRESS) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_header, parent, false);
            return new AddressHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReviewOrderItem item = list.get(position);
        switch (item.getType()) {
            case TYPE_HEADER:
                if (holder instanceof SellerHeaderViewHolder) {
                    SellerHeaderViewHolder headerViewHolder = (SellerHeaderViewHolder) holder;
                    headerViewHolder.bind(item);
                }
                break;
            case TYPE_ITEM:
                if (holder instanceof ItemViewHolder) {
                    ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                    itemViewHolder.bind(item);
                }
                break;
            case TYPE_SELLER_FOOTER:
                if (holder instanceof SellerFooterHolder) {
                    SellerFooterHolder sellerFooterHolder = (SellerFooterHolder) holder;
                    sellerFooterHolder.bind(item);
                }
                break;
            case TYPE_GRAND:
                if (holder instanceof GrandTotalHolder) {
                    GrandTotalHolder grandTotalHolder = (GrandTotalHolder) holder;
                    grandTotalHolder.bind(item);
                }
                break;
            case TYPE_ADDRESS:
                if (holder instanceof AddressHolder) {
                    AddressHolder addressHolder = (AddressHolder) holder;
                    addressHolder.bind(item);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(ArrayList<ReviewOrderItem> listItems) {
        list.clear();
        list.addAll(listItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    class SellerHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_cart_seller)
        TextView tvSeller;

        public SellerHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ReviewOrderItem item) {
            tvSeller.setText(item.getSeller_category());
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_cart_desc)
        TextView tvDesc;
        @BindView(R.id.tv_cart_offer_price)
        TextView tvOfferPrice;
        @BindView(R.id.tv_cart_qty)
        TextView tvQty;
        @BindView(R.id.tv_cart_total)
        TextView tvTotal;
        @BindView(R.id.tv_review_message)
        TextView tvMessge;
        @BindView(R.id.tv_shipping_charge)
        TextView tvShippingChages;
        @BindView(R.id.tv_total_amount)
        TextView tvTotalAmount;
        @BindView(R.id.btn_cart_edit)
        LinearLayout btnEdit;
        @BindView(R.id.btn_cart_remove)
        LinearLayout btnRemove;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ReviewOrderItem item) {
            tvDesc.setText(item.getDescription());
            tvOfferPrice.setText(AppConstants.RUPEE + " " + item.getOffer_price());
            tvQty.setText(item.getQuantity_available());
            tvTotal.setText(AppConstants.RUPEE + " " + item.getItem_price());
            tvShippingChages.setText(AppConstants.RUPEE + " " + item.getShipping_price());
            tvTotalAmount.setText(AppConstants.RUPEE + " " + item.getTotal_item_price());
            if (item.getReview_status() == 0) {
                tvMessge.setVisibility(View.VISIBLE);
                tvMessge.setText(item.getReview_message());
            }
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dealId = list.get(getAdapterPosition()).getDealId();
                    String qty = list.get(getAdapterPosition()).getQuantity_available();
                    String price = list.get(getAdapterPosition()).getOffer_price();
                    ((OrderReviewActivity) mContext).editQunatity(dealId, qty, price);
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dealId = list.get(getAdapterPosition()).getDealId();
                    if (AppHelpers.deleteFromCart(mContext, dealId) > 0) {
                        ((OrderReviewActivity) mContext).pullNewDataAndDelete(dealId);
                    }
                }
            });

        }
    }

    class SellerFooterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_grand_total)
        TextView tvTotal;

        public SellerFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ReviewOrderItem item) {
            tvTotal.setText(AppConstants.RUPEE + " " + item.getFooterItem());
        }
    }

    class GrandTotalHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_grand_total)
        TextView tvTotal;
        @BindView(R.id.textView10)
        TextView textView;

        public GrandTotalHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ReviewOrderItem item) {
            tvTotal.setText(AppConstants.RUPEE + " " + item.getFooterItem());
            textView.setText("Grand Total:");
        }
    }

    class AddressHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_cart_seller)
        TextView tvAddress;

        public AddressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ReviewOrderItem item) {
            tvAddress.setText("Delivery Address:\n" + item.getFooterItem());
        }
    }

}
