package com.zhaoyao.miaomiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.zhaoyao.miaomiao.IView.activity.ComicChapterDetailsActivityView;
import com.zhaoyao.miaomiao.R;
import com.zhaoyao.miaomiao.adapter.ComicChapterDetailsActivityAdapter;
import com.zhaoyao.miaomiao.adapter.base.HealthyMultipleAdapter;
import com.zhaoyao.miaomiao.entity.ComicChapterDetailsEntity;
import com.zhaoyao.miaomiao.persenter.activity.ComicChapterDetailsActivityPersenter;
import com.zhaoyao.miaomiao.persenter.activity.impl.ComicChapterDetailsActivityPersenterImpl;
import com.zhaoyao.miaomiao.util.Constants;
import com.zhaoyao.miaomiao.util.LogUtils;
import com.zhaoyao.miaomiao.util.ToastUtil;

import java.util.List;

/**
 * 漫画章节详情
 * Created by liuwei on 2017/8/28 16:27
 */

public class ComicChapterDetailsActivity extends BaseNewActivity implements
        ComicChapterDetailsActivityView,HealthyMultipleAdapter.OnItemClickListener<ComicChapterDetailsEntity.ComicChapterBean>{


    public static final String COMIC_ID_KEY = "COMIC_ID_KEY";

    public static void startActivity(Context context,String comic_id) {
        Intent intent = new Intent(context, ComicChapterDetailsActivity.class);
        intent.putExtra(COMIC_ID_KEY,comic_id);
        context.startActivity(intent);
    }

    private ComicChapterDetailsActivityPersenter mComicChapterDetailsActivityPersenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_chapter_details);
        String comic_id = getIntent().getStringExtra(COMIC_ID_KEY);
        if (TextUtils.isEmpty(comic_id)){
            finish();
            return;
        }
        initView();
        mComicChapterDetailsActivityPersenter = new ComicChapterDetailsActivityPersenterImpl(this);
        mComicChapterDetailsActivityPersenter.getComicInfo(comic_id);
    }

    LinearLayout llAd = null;
    private BannerView mBannerView;

    private RecyclerView mRecyclerView;
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rlv_activity_cartoon_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        llAd = (LinearLayout) findViewById(R.id.ll_ad);
        doRefreshBanner();
    }

    private void initBanner() {
        mBannerView = new BannerView(this, ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
        mBannerView.setRefresh(30);
        llAd.removeAllViews();
        mBannerView.setADListener(new AbstractBannerADListener() {
            @Override
            public void onNoAD(AdError adError) {
                LogUtils.a(adError);
            }

            @Override
            public void onADReceiv() {
                LogUtils.a("onADReceiv");
            }
        });
        llAd.addView(mBannerView);
    }

    private void doRefreshBanner() {
        if (mBannerView == null) {
            initBanner();
        }
        mBannerView.loadAD();
    }

    private void doCloseBanner() {
        llAd.removeAllViews();
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
        }
    }


    @Override
    public void setComicChapterDetailsEntity(ComicChapterDetailsEntity mComicChapterDetailsEntity) {
        if (mComicChapterDetailsEntity == null)return;
        List<ComicChapterDetailsEntity.ComicChapterBean> comic_chapter = mComicChapterDetailsEntity.getComic_chapter();
        ComicChapterDetailsActivityAdapter comicChapterDetailsActivityAdapter = new ComicChapterDetailsActivityAdapter(comic_chapter);
//        comicChapterDetailsActivityAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(comicChapterDetailsActivityAdapter);
    }

    @Override
    public void onItemClick(View view, ComicChapterDetailsEntity.ComicChapterBean item, int position) {
        ToastUtil.toastSome(this,item.getChapter_type());
    }

    @Override
    public void startRequest() {

    }

    @Override
    public void endRequest() {

    }

    @Override
    public void connectionFailed(String failedMessge) {
        ToastUtil.toastSome(this,failedMessge);
    }

    @Override
    public void resultError(int code, String errorMessge) {
        ToastUtil.toastSome(this,errorMessge);
    }
}
