package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.suyogindia.flashdeals.R;
import com.suyogindia.model.ListCategoryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyogcomputech on 17/10/16.
 */

public class SelectDealsAdapter extends RecyclerView.Adapter<SelectDealsAdapter.ViewHolder> {
    private final OnItemClickListener listener;
    private Context myContext;
    private List<ListCategoryResponse.Category> list;


    public SelectDealsAdapter(Context myContext, OnItemClickListener listener) {
        this.myContext = myContext;
        list = new ArrayList<>();

        this.listener = listener;

    }

    public void addCategoryList(List<ListCategoryResponse.Category> categoryList) {
        list.clear();
        list.addAll(categoryList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.item_select_deals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position), listener);

    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else
            return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(ListCategoryResponse.Category item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        CheckBox chDeal;
        CardView cvDeals;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_select_deal_title);
            chDeal = (CheckBox) itemView.findViewById(R.id.ch_deals);
            cvDeals = (CardView) itemView.findViewById(R.id.cv_select_deals);
            chDeal.setEnabled(false);
        }

        void bind(final ListCategoryResponse.Category category, final OnItemClickListener listener) {
            tvTitle.setText(category.getName());
            cvDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chDeal.isChecked()) {
                        chDeal.setChecked(false);
                    } else {
                        chDeal.setChecked(true);
                    }
                    listener.onItemClick(category);
                }
            });
        }
    }
}
