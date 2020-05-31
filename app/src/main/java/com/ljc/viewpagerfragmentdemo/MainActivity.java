package com.ljc.viewpagerfragmentdemo;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private int[] mData = {R.drawable.img0, R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4};
    private ViewPager viewPager;
    private Button tv;
    private LinkedList<ViewPagerItemBean> linkedList = new LinkedList<>();
    private FragmentAdapter adapter;
    private LinkedList<BlankFragment> fragments = new LinkedList<>();

    private GestureDetector detector;
    int Current = 0;
    private Handler handler;
    private Runnable runnable;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initdata();
        initView();
        add();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        handler.removeCallbacks(runnable);
                        Log.i("Current", "handler.removeCallbacks(runnable)");
                        break;
                    case 1:
                        handler.postDelayed(runnable, 1000 * 3);//延迟3s执行
                        Log.i("Current", "handler.postDelayed(runnable, 1000 * 3)");
                        break;
                }
                super.handleMessage(msg);
            }
        };
        runnable = new Runnable() {
            @Override
            public void run() {
                if (fragments.size() - 1 == Current) {
                    viewPager.setCurrentItem(0);
                    Log.i("Current", "runnable Current=0 " + Current);
                } else {
                    viewPager.setCurrentItem(Current + 1);
                    Log.i("Current", "runnable Current++ " + Current);
                }
                handler.postDelayed(this, 1000 * 3);//延迟3s执行
            }
        };
        handler.postDelayed(runnable, 1000 * 3);//延迟3s执行
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initdata() {
        for (int i = 0; i < mData.length; i++) {
            ViewPagerItemBean o = new ViewPagerItemBean(mData[i], "订单：" + i);
            linkedList.add(i, o);
        }
        for (int i = 0; i < linkedList.size(); i++) {
            BlankFragment fragment = BlankFragment.newInstance(this, linkedList.get(i));
            fragments.add(fragment);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, fragments);
        viewPager = findViewById(R.id.cardViewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        getSupportActionBar().setTitle("AlphaPageTransformer");
        viewPager.setPageMargin(40);
        viewPager.setPageTransformer(true, new AlphaAndScalePageTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //有上滑删除得自己维护Current
                Current = i;
                Log.i("Current", "当前fragment：" + Current);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        ViewConfiguration configuration = ViewConfiguration.get(this);
        final int mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            private ObjectAnimator mObjectAnimatorY;
            int touchFlag = 0; //通知UP事件类型,左右滑动禁止上下滑动，反之上下滑动禁止左右滑动
            float x = 0, y = 0;//父容器坐标轴
            float rawX = 0, rawY = 0;//系统页面坐标轴
            float lastX = 0, lastY = 0;//记录上次坐标
            float xDiff = 0, yDiff = 0;//偏移量
            float nextY = 0;
            boolean isSlideDel = false;
            View childAt;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("MotionEvent", "ACTION_DOWN");
                        Message msg = Message.obtain();
                        msg.what = 0;
                        handler.sendMessage(msg);

                        rawX = event.getRawX();
                        rawY = event.getRawY();
                        touchFlag = 0;
//                        x = event.getX();
//                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("MotionEvent", "ACTION_MOVE");
//                        Log.i("Current", "viewPager.getChildCount() = " + viewPager.getChildCount()); //可见页count
//                        Log.i("Current", "viewPager.getCurrentItem() = " + viewPager.getCurrentItem());
                        //MotionEvent比Listener优先级更高，得自己计算位置了
//                        if (Current == 0) {
//                            childAt = viewPager.getChildAt(0);//拿到的是可见页对应count的item,预加载2页position范围永远0-4
//                        } else {
                        childAt = adapter.getItem(Current).getView();
//                        }
                        if (childAt != null) {
                            //每次移动的距离
                            float distanceX = 0;
                            float distanceY = 0;
                            if (lastY != 0 && lastX != 0) {
                                distanceX = event.getRawX() - lastX;
                                distanceY = event.getRawY() - lastY;

                                nextY = 0;

                                //通知ACTION_UP本次是什么事件
//                                xDiff = Math.abs(event.getX() - x);
//                                yDiff = Math.abs(event.getY() - y);
                                xDiff = Math.abs(event.getRawX() - rawX);
                                yDiff = Math.abs(event.getRawY() - rawY);
                                if (xDiff < mTouchSlop && yDiff < mTouchSlop) {
                                    Log.i("MotionEvent", "点击事件");
                                    touchFlag = 0;
                                } else if (xDiff > mTouchSlop && xDiff > yDiff) {
                                    if (touchFlag != 2) {
                                        touchFlag = 1;
                                        Log.i("MotionEvent", "左右滑动事件");

                                        if (Math.abs(distanceX) > 100) {
                                            //左右滑动交给viewpager 这里不做处理,只记录坐标
                                            Log.i("TAG", "fragment" + Current + "左右滑动");
                                            Log.i("TAG", "fragment" + Current + "左右滑动距离 = " + Math.abs(distanceX));
                                            lastX = event.getRawX();
                                            lastY = event.getRawY();
                                        }
                                        break;
                                    }
                                } else if (yDiff > mTouchSlop && yDiff > xDiff) {
                                    if (touchFlag != 1) {
                                        touchFlag = 2;
                                        Log.i("MotionEvent", "上下滑动事件");

                                        if (event.getRawY() < lastY) {
                                            //上滑distanceY为负
                                            if (distanceY < -250) {
                                                //滑出页面
                                                nextY = childAt.getY() + distanceY;
                                                isSlideDel = true;
                                                Log.i("TAG", "fragment" + Current + "上滑——滑出页面");
                                            } else {
                                                Log.i("TAG", "fragment" + Current + "上滑——弹回原位");
                                                nextY = childAt.getY() + distanceY;
                                                isSlideDel = false;
                                            }
                                        } else if (event.getRawY() > lastY) {
                                            //下滑distanceY为正
                                            nextY = childAt.getY() + distanceY;
                                            if (nextY > 200) {
                                                nextY = 200;
                                            }
                                            isSlideDel = false;
                                            Log.i("TAG", "fragment" + Current + "下滑——弹回原位");
                                        }
                                        Log.i("TAG", "fragment" + Current + "上下滑动距离 = " + distanceY);
                                        slide();
                                    }
                                }
                            }
                            //移动完之后记录当前坐标
                            lastX = event.getRawX();
                            lastY = event.getRawY();
                            break;
                        }
                    case MotionEvent.ACTION_UP:
                        Log.i("MotionEvent", "ACTION_UP");
                        Log.i("TAG", "touchFlag =" + touchFlag);
                        Message msg1 = Message.obtain();
                        msg1.what = 1;
                        handler.sendMessage(msg1);

                        if (touchFlag == 0) {//单击事件
                            startActivity(new Intent(MainActivity.this, TipActivity.class));
                        } else if (touchFlag == 1) {//左右滑动
                            //TODO 把旁边的view位置归零
                            View leftView;
                            View rightView;
                            if (Current - 1 > 0) {
                                leftView = viewPager.getChildAt(Current - 1);
                                back(leftView);
                            }
                            if (Current + 1 < fragments.size()) {
                                rightView = viewPager.getChildAt(Current + 1);
                                back(rightView);
                            }
                        } else if (touchFlag == 2) {//上下滑动
                            Log.i("TAG", "nextY = " + nextY);
                            if (isSlideDel || nextY < -500) {//通过删除滑动距离和最后抬手的位置确定用户是否想删除
                                //TODO 应该在手指离开后确定执行删除动画
                                slideDel();
                                //滑出了
                                Log.i("TAG", "fragments.size=" + fragments.size() + "  Delete Current = " + Current);
                                //TODO 删除
                                if (fragments.size() > 1 && Current > 0) { //删除后显示上一张\
                                    Log.i("Current", "删除" + linkedList.get(Current).getTilte_text());
                                    linkedList.remove(Current);
                                    fragments.remove(Current);
                                    adapter.notifyDataSetChanged();
                                    viewPager.setAdapter(adapter);

                                    if (Current == 1) {
                                        Current = 0;//0不回调onPageSelected,手动记录
                                    }

                                    viewPager.setCurrentItem(Current - 1);
                                    Log.i("Current", "Current-- " + Current);
                                } else if (fragments.size() > 1 && Current == 0) {
                                    Log.i("Current", "删除" + linkedList.get(Current).getTilte_text());
                                    linkedList.remove(Current);
                                    fragments.remove(Current);
                                    adapter.notifyDataSetChanged();
                                    viewPager.setAdapter(adapter);
                                    viewPager.setCurrentItem(Current);
                                    Log.i("Current", "Current++ " + Current);
                                } else if (fragments.size() == 1) {//最后一个了
                                    Log.i("Current", "删除" + linkedList.get(Current).getTilte_text());
                                    linkedList.remove(0);
                                    fragments.remove(Current);
                                    adapter.notifyDataSetChanged();
                                    viewPager.setAdapter(adapter);
                                    Current = 0;
                                    Log.i("Current", "Current = 0 " + Current);
                                }
                            } else {
                                back(childAt);
                            }
                        }
                }
                return false;
            }

            private void slide() {
                Log.i("TAG", "fragment" + Current + "slide = " + nextY);
                mObjectAnimatorY = ObjectAnimator.ofFloat(childAt, "y", childAt.getY(), nextY);
                AnimatorSet mAnimatorSet = new AnimatorSet();
                mAnimatorSet.playTogether(mObjectAnimatorY);//只允许上下滑动
                mAnimatorSet.setDuration(0);
                mAnimatorSet.start();
            }

            private void slideDel() {
                Log.i("TAG", "fragment" + Current + "slideDel = " + nextY);
                mObjectAnimatorY = ObjectAnimator.ofFloat(childAt, "y", childAt.getY(), -2000f);
                AnimatorSet mAnimatorSet = new AnimatorSet();
                mAnimatorSet.playTogether(mObjectAnimatorY);//只允许上下滑动
                mAnimatorSet.setDuration(300);
                mAnimatorSet.start();
            }

            /**
             * 未滑出，弹回原位
             */
            private void back(View view) {
                mObjectAnimatorY = ObjectAnimator.ofFloat(view, "y", 0);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(mObjectAnimatorY);//只需要上下滑动
                animatorSet.setDuration(500);
                animatorSet.start();
                Log.i("TAG", "fragment" + Current + "弹回原位");
            }
        });
    }

    private void add() {
        tv = findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linkedList.size() == 10) {
                    synchronized (linkedList) {
                        linkedList.remove(0);
                    }
                }
                ViewPagerItemBean o = new ViewPagerItemBean(R.drawable.img0, "订单：" + linkedList.size());
                synchronized (linkedList) {
                    linkedList.add(linkedList.size(), o);
                }
                BlankFragment fragment = BlankFragment.newInstance(MainActivity.this, o);
                fragments.add(fragment);

                adapter.notifyDataSetChanged();
            }
        });
    }
}
