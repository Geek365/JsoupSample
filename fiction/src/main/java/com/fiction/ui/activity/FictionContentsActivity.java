package com.fiction.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fiction.R;
import com.fiction.adapter.FictionContentAdapter;
import com.fiction.manager.ApiConfig;
import com.fiction.mvp.model.FictionModel;
import com.fiction.mvp.presenter.FictionContentsPresenterImpl;
import com.fiction.mvp.view.ViewManager;
import com.framework.base.BaseActivity;
import com.framework.base.BaseRecyclerAdapter;
import com.framework.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * by y on 2017/4/6.
 */

public class FictionContentsActivity extends BaseActivity
        implements ViewManager.FictionContentsView, BaseRecyclerAdapter.OnItemClickListener<FictionModel> {

    private static final String URL = "url";
    private static final String TITLE = "title";
    private static final String TYPE = "type";

    private String type = ApiConfig.Type.ZW_81;

    private Toolbar toolbar;
    private ContentLoadingProgressBar progressBar;
    private RecyclerView recyclerView;
    private FictionContentAdapter adapter;

    private List<FictionModel> list;

    public static void getInstance(String type, String url, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        bundle.putString(TITLE, title);
        bundle.putString(TYPE, type);
        UIUtils.startActivity(FictionContentsActivity.class, bundle);
    }


    @Override
    protected void initCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        type = extras.getString(TYPE);
        toolbar.setTitle(extras.getString(TITLE));
        setSupportActionBar(toolbar);
        list = new ArrayList<>();
        adapter = new FictionContentAdapter(list);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        new FictionContentsPresenterImpl(this).startContents(extras.getString(URL), type);
    }

    @Override
    protected void initById() {
        toolbar = getView(R.id.toolbar);
        recyclerView = getView(R.id.recyclerView);
        progressBar = getView(R.id.progress_bar);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fiction_list_contents;
    }


    @Override
    public void netWorkSuccess(List<FictionModel> data) {
        Collections.reverse(data);
        adapter.addAll(data);
    }

    @Override
    public void netWorkError() {
        UIUtils.snackBar(getView(R.id.rootView), getString(R.string.network_error));
    }

    @Override
    public void showProgress() {
        progressBar.show();
    }

    @Override
    public void hideProgress() {
        progressBar.hide();
    }

    @Override
    public void onItemClick(View view, int position, FictionModel info) {
        FictionDetailActivity.getInstance(type, info.detailUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contents_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }
}