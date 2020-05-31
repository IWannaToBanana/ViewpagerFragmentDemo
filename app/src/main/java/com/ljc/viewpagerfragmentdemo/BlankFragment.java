package com.ljc.viewpagerfragmentdemo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends android.support.v4.app.Fragment {

    private GestureDetector detector;
    private static Context context;

    public static BlankFragment newInstance(Context c, ViewPagerItemBean ViewPagerItemBean) {
        context = c;
        Bundle bundle = new Bundle();
        bundle.putSerializable("ViewPagerItemBean", ViewPagerItemBean);
        BlankFragment fragment = new BlankFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_blank, null, false);
        TextView tv = root.findViewById(R.id.tv);
        ImageView iv = root.findViewById(R.id.iv);
        Bundle arguments = getArguments();
        ViewPagerItemBean ViewPagerItemBean = (ViewPagerItemBean) arguments.getSerializable("ViewPagerItemBean");
        tv.setText(ViewPagerItemBean.getTilte_text());
        iv.setBackground(getResources().getDrawable(ViewPagerItemBean.getImg_url()));
        return root;
    }
}
