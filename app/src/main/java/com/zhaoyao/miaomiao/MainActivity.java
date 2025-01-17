package com.zhaoyao.miaomiao;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.sohuvideo.ui_plugin.fragment.SohuVideoFragment;
import com.zhaoyao.miaomiao.activity.Ad360Activity;
import com.zhaoyao.miaomiao.activity.AdActivity;
import com.zhaoyao.miaomiao.activity.AdGdtClickActivity;
import com.zhaoyao.miaomiao.activity.BaseNewActivity;
import com.zhaoyao.miaomiao.activity.GoogleAdActivity;
import com.zhaoyao.miaomiao.activity.ImageRecognitionActivity;
import com.zhaoyao.miaomiao.adapter.TabFragmentAdapter;
import com.zhaoyao.miaomiao.db.impl.GdtAdExposureClickEntityImpl;
import com.zhaoyao.miaomiao.entity.GdtAdExposureClickEntity;
import com.zhaoyao.miaomiao.fragment.CartoonRecommendFragment;
import com.zhaoyao.miaomiao.handler.TaskHandler;
import com.zhaoyao.miaomiao.handler.TaskHandlerImpl;
import com.zhaoyao.miaomiao.service.AdService;
import com.zhaoyao.miaomiao.util.DateUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseNewActivity implements View.OnClickListener, TaskHandler {

    private CartoonRecommendFragment mContent;
    /**
     * 广告
     */
    private TextView mTvAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdService.startService(this, 1);
        MobileAds.initialize(this.getApplicationContext(), "ca-app-pub-2850046637182646~7046734019");
        initView();
//        这个是禁止滑动
//        DrawerLayout mDrawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


//        initSlidingMenu(savedInstanceState);
    }

    private List<Fragment> fragments;
    private List<String> lists = null;
    private TabFragmentAdapter fragmentAdapter;
    private ViewPager viewPager;

    public void initView() {
        fragments = new ArrayList<>();
        lists = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        lists.add("漫画");
        fragments.add(CartoonRecommendFragment.newInstance());


        lists.add("视频");
        SohuVideoFragment sohuVideoFragment = SohuVideoFragment.newInstance(true);
        fragments.add(sohuVideoFragment);

        if (fragments.size() != lists.size() || lists.size() == 0) {
            return;
        }

        fragmentAdapter = new TabFragmentAdapter(fragments, lists, getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentAdapter);

        viewPager.setOffscreenPageLimit(fragments.size());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        mTvAd = (TextView) findViewById(R.id.tv_ad);
        mTvAd.setOnClickListener(this);
        findViewById(R.id.tv_ad_click).setOnClickListener(this);
        findViewById(R.id.tv_google_ad).setOnClickListener(this);
        findViewById(R.id.tv_360_ad).setOnClickListener(this);
        findViewById(R.id.tv_image_recognition).setOnClickListener(this);
        findViewById(R.id.tv_query).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 初始化侧边栏
     */
    private void initSlidingMenu(Bundle savedInstanceState) {
        // 如果保存的状态不为空则得到之前保存的Fragment，否则实例化MyFragment
        if (savedInstanceState != null) {
            mContent = (CartoonRecommendFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }

        if (mContent == null) {
            mContent = CartoonRecommendFragment.newInstance();
        }
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content_frame, mContent).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ad:
                startActivityForResult(new Intent(this, AdActivity.class), 100);
                break;
            case R.id.tv_google_ad:
                startActivity(new Intent(this, GoogleAdActivity.class));
                break;
            case R.id.tv_ad_click:
                final View inflate = LayoutInflater.from(this).inflate(R.layout.alert_dialog_goud, null);
                final RadioGroup mRadioGroup = (RadioGroup) inflate.findViewById(R.id.rgp);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择广告组")
//                        .setMessage("message")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                RadioButton rb = (RadioButton)inflate.findViewById(mRadioGroup.getCheckedRadioButtonId());
                                String trim = rb.getText().toString().trim();
                                Intent intent = new Intent(MainActivity.this, AdGdtClickActivity.class);
                                intent.putExtra("AD_GROUP", Integer.parseInt(trim));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).setView(inflate);
                builder.show();
                break;
            case R.id.tv_360_ad:
                startActivity(new Intent(this, Ad360Activity.class));
                break;
            case R.id.tv_image_recognition:
                startActivity(new Intent(this, ImageRecognitionActivity.class));
                break;
            case R.id.tv_query:
                if (v instanceof TextView) {
                    GdtAdExposureClickEntityImpl gdtAdExposureClickEntity = new GdtAdExposureClickEntityImpl(this);
                    GdtAdExposureClickEntity byCreateTime = gdtAdExposureClickEntity.findByCreateTime(DateUtils.dateFormat.get().format(new Date()));
                    if (byCreateTime != null) {
                        String format = String.format("查询：\t%s\t:\t%s", byCreateTime.getExposureNumber(), byCreateTime.getClickNumber());
                        ((TextView) v).setText(format);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 100) {

            mHandler.removeMessages(100);
            Message message = mHandler.obtainMessage();
            message.what = 100;
            message.obj = data;
            mHandler.sendMessageDelayed(message, 3 * 1000);
        }
    }

    private boolean isOnPause = false;

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
    }

    private Handler mHandler = new TaskHandlerImpl(this);

    @Override
    public void handleMessage(WeakReference weakReference, Message msg) {
        if (msg.what == 100) {
            if (msg.obj != null && msg.obj instanceof Intent) {
                Intent o = (Intent) msg.obj;
                if (isOnPause) {
                    mHandler.removeMessages(100);
                    Message message = mHandler.obtainMessage();
                    message.what = 100;
                    message.obj = o;
                    mHandler.sendMessageDelayed(message, 3 * 1000);
                } else {
                    Intent intent = new Intent(this, AdActivity.class);
                    Bundle extras = o.getExtras();
                    intent.putExtras(extras);
                    startActivityForResult(intent, 100);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        AdService.stopService(this);
        super.onDestroy();
    }
}
