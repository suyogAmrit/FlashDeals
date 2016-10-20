package com.suyogindia.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.suyogindia.flashdeals.DealsFragment;
import com.suyogindia.model.Category;
import com.suyogindia.model.GetDealsResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyogcomputech on 18/10/16.
 */

public class DealsFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Category> categoryList = new ArrayList<>();

    public DealsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Category category = categoryList.get(position);
        return DealsFragment.newInstance(category);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getName();
    }

    public void addItems(List<Category> categoryList) {
        this.categoryList.addAll(categoryList);
        notifyDataSetChanged();
    }
}
