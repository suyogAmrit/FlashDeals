package com.suyogindia.flashdeals;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suyogindia.adapters.DealsAdapter;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.model.Category;
import com.suyogindia.model.Deals;

/**
 * Created by suyogcomputech on 18/10/16.
 */

public class DealsFragment extends Fragment {
    Category myCategory;
    RecyclerView myRecyclerView;
    DealsAdapter adapter;


    public static Fragment newInstance(Category category) {
        DealsFragment myDealsFragment = new DealsFragment();
        Bundle b = new Bundle();
        b.putParcelable(AppConstants.CATEGORY, category);
        Log.i("size", category.getDealsList().size() + "");

        myDealsFragment.setArguments(b);
        return myDealsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        myCategory = b.getParcelable(AppConstants.CATEGORY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myRecyclerView = new RecyclerView(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(llm);
        adapter = new DealsAdapter(getActivity(), new DealsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Deals deals) {
                Intent i = new Intent(getActivity(), DealsDetailsActivity.class);
                i.putExtra(AppConstants.DEAL, deals);
                i.putExtra(AppConstants.DETAILSTYPE, 2);
                startActivity(i);
            }
        });
        if (myCategory.getDealsList() != null)
            adapter.addDeals(myCategory.getDealsList());
        myRecyclerView.setAdapter(adapter);
        return myRecyclerView;
    }
}
