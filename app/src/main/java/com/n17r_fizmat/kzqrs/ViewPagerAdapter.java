package com.n17r_fizmat.kzqrs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.ArrayList;

/**
 * Created by Alisher on 7/7/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();
    private Context context;
    private int[] imageResId = {
            R.drawable.newspaper,
            R.drawable.account,
            R.drawable.magnify
    };

    public void addFragments(Fragment fragment, String tabTitle) {
        this.fragments.add(fragment);
        this.tabTitles.add(tabTitle);
    }
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
