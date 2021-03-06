package com.imooc.imooc_voice.view.home.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imooc.imooc_voice.R;
import com.imooc.imooc_voice.R2;
import com.imooc.imooc_voice.view.home.search.artist.ArtistSortDelegate;
import com.imooc.lib_api.RequestCenter;
import com.imooc.lib_api.model.search.DefaultSearchBean;
import com.imooc.lib_api.model.search.HotSearchDetailBean;
import com.imooc.lib_common_ui.delegate.NeteaseDelegate;
import com.imooc.lib_network.listener.DisposeDataListener;


import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchDelegate extends NeteaseDelegate {


    @BindView(R2.id.rv_suggest_list)
    RecyclerView mRecyclerViewSuggest;
    @BindView(R2.id.tv_search_keyword)
    EditText mEtKeywords;

    private HotSearchAdapter mAdapter;

    @Override
    public Object setLayout() {
        return R.layout.delegate_search;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View view) {
        getSupportDelegate().showSoftInput(view);
        mEtKeywords.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchKeyWord = mEtKeywords.getText().toString();
                if(TextUtils.isEmpty(searchKeyWord)){
                    //使用默认搜索关键词
                    searchKeyWord = mEtKeywords.getHint().toString();
                }
                getSupportDelegate().start(SearchResultDelegate.newInstance(searchKeyWord));
                getSupportDelegate().hideSoftInput();
            }
            return false;
        });

        // 获取默认搜索关键词
        RequestCenter.getDefaultSearch(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                DefaultSearchBean bean = (DefaultSearchBean) responseObj;
                mEtKeywords.setHint(bean.getData().getShowKeyword());
            }

            @Override
            public void onFailure(Object reasonObj) {

            }
        });

        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        RequestCenter.getSearchHotDetail(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                HotSearchDetailBean bean = (HotSearchDetailBean) responseObj;
                List<HotSearchDetailBean.DataBean> data = bean.getData();
                mAdapter = new HotSearchAdapter(data);
                mAdapter.setHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.header_suggest, null));
                mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        HotSearchDetailBean.DataBean entity = (HotSearchDetailBean.DataBean) adapter.getItem(position);
                        String searchword = entity.getSearchWord();
                        getSupportDelegate().start(SearchResultDelegate.newInstance(searchword));
                    }
                });
                mRecyclerViewSuggest.setAdapter(mAdapter);
                mRecyclerViewSuggest.setLayoutManager(manager);
            }

            @Override
            public void onFailure(Object reasonObj) {
                Toast.makeText(getContext(), "加载失败",Toast.LENGTH_SHORT).show();
            }
        });

    }


    @OnClick(R2.id.img_search_artist)
    void onClickArtistSort(){
        getSupportDelegate().start(new ArtistSortDelegate());
    }

    @OnClick(R2.id.img_suggest_back)
    void onClickBack(){
        getSupportDelegate().hideSoftInput();
        getSupportDelegate().pop();
    }

}
