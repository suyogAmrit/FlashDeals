package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.suyogindia.flashdeals.MainActivity;
import com.suyogindia.flashdeals.R;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;
import com.suyogindia.model.Deals;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyogcomputech on 18/10/16.
 */

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder> {
    private final OnItemClickListener listener;
    Context myContext;
    ArrayList<Deals> dealsArrayList;

    public DealsAdapter(Context context, OnItemClickListener listener) {
        myContext = context;
        dealsArrayList = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public DealsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.item_deals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DealsAdapter.ViewHolder holder, int position) {
        Deals myDeals = dealsArrayList.get(position);
        Log.i("deals", myDeals.getDesciption());
        holder.bind(myDeals, listener);
    }

    @Override
    public int getItemCount() {
        return dealsArrayList.size();
    }

    public void addDeals(List<Deals> list) {
        dealsArrayList.clear();
        dealsArrayList.addAll(list);

        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Deals deals);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvDeal;
        TextView tvSeller, tvDiscount, tvDesc, tvOfferPrice;
        ImageButton btnQuickAdd;

        ViewHolder(View itemView) {
            super(itemView);
            tvSeller = (TextView) itemView.findViewById(R.id.tv_deal_seller);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_deal_desc);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);
            tvOfferPrice = (TextView) itemView.findViewById(R.id.tv_offer_price);
            cvDeal = (CardView) itemView.findViewById(R.id.cv_deal);
            btnQuickAdd = (ImageButton) itemView.findViewById(R.id.btn_quick_add);
        }

        void bind(final Deals myDeals, final OnItemClickListener listener) {
            tvDesc.setText(myDeals.getDesciption());
            tvDiscount.setText(AppConstants.DISCOUNT + ": " + myDeals.getDiscount() + "%");
            tvSeller.setText(myDeals.getSeller_name());
            tvOfferPrice.setText("Offer Price: "+AppConstants.RUPEE + myDeals.getOffer_price());
            btnQuickAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //addto cart with qty 1
                    long row = AppHelpers.addToCart(myContext, "1", myDeals.getOffer_price(), myDeals);
                    if (row > 0) {
                        ((MainActivity) myContext).updateCart();
                    } else {
                        Toast.makeText(myContext, AppConstants.WENTWRONG, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            cvDeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(myDeals);
                }
            });
        }
    }
}
