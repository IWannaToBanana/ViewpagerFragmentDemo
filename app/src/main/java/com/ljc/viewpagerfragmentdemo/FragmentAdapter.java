package com.ljc.viewpagerfragmentdemo;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.LinkedList;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private LinkedList<BlankFragment> fragments;

    public FragmentAdapter(FragmentManager fm, LinkedList<BlankFragment> fragmentLinkedList) {
        super(fm);
        fragments = fragmentLinkedList;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_UNCHANGED;
    }
}
