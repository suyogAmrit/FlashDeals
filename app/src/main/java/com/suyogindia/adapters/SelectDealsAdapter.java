package com.suyogindia.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.suyogindia.flashdeals.R;
import com.suyogindia.model.ListCategoryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyogcomputech on 17/10/16.
 */

public class SelectDealsAdapter extends RecyclerView.Adapter<SelectDealsAdapter.ViewHolder> {
    public List<Boolean> listSelected;
    public List<ListCategoryResponse.Category> list;
    int width = 0, height = 0;
    private Context myContext;

    public SelectDealsAdapter(Context myContext) {
        this.myContext = myContext;
        list = new ArrayList<>();
        setUpImageSize();
    }

    private void setUpImageSize() {
        WindowManager wm = (WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        width = metrics.widthPixels / 2;
        height = metrics.heightPixels / 5;
    }

    public void addCategoryList(List<ListCategoryResponse.Category> categoryList) {
        list.clear();
        list.addAll(categoryList);
        notifyDataSetChanged();
        listSelected = new ArrayList<>(list.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.item_select_deals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position));
        listSelected.add(false);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else
            return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        CheckBox chDeal;
        CardView cvDeals;
        ImageView imgCategory;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_select_deal_title);
            chDeal = (CheckBox) itemView.findViewById(R.id.ch_deals);
            cvDeals = (CardView) itemView.findViewById(R.id.cv_select_deals);
            imgCategory = (ImageView) itemView.findViewById(R.id.category_img);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            imgCategory.setLayoutParams(layoutParams);
        }

        void bind(final ListCategoryResponse.Category category) {
            tvTitle.setText(category.getName());
            Glide.with(myContext).load(category.getImage_url()).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).override(width, height).into(imgCategory);
            cvDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (chDeal.isChecked()) {
//                        chDeal.setChecked(false);
//                    } else {
//                        chDeal.setChecked(true);
//                    }
                    if (listSelected.get(getAdapterPosition())) {
                        listSelected.set(getAdapterPosition(), false);
                        chDeal.setChecked(false);
                    } else {
                        listSelected.set(getAdapterPosition(), true);
                        chDeal.setChecked(true);
                    }
                }
            });
            chDeal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listSelected.set(getAdapterPosition(), isChecked);
                }
            });
        }
    }

}
