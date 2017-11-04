package site.zhguixin.dytt.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import site.zhguixin.dytt.R;
import site.zhguixin.dytt.adapter.NewMovieAdapter;
import site.zhguixin.dytt.entity.MovieInfo;
import site.zhguixin.dytt.presenter.NewMovieInfoCallback;
import site.zhguixin.dytt.presenter.NewMoviePresenter;
import site.zhguixin.dytt.utils.BottomRefreshListener;
import site.zhguixin.dytt.utils.BottomTrackListener;
import site.zhguixin.dytt.utils.Contants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends BaseFragment {
    private static final String TAG = "MovieFragment";

    private List<MovieInfo> mMovieInfos;
    private List<String> mPageList;
    private NewMovieAdapter mAdapter;
    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private Context mContext;
    private TextView mErrorInfoView;
    private ProgressBar mLoadingBar;
    private TextView mMoreInfoView;
    private ProgressBar mLoadingMoreBar;
    private String mUrl;
    private static int mPageIndex = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                mLoadingBar.setVisibility(View.GONE);
                mErrorInfoView.setVisibility(View.VISIBLE);
            } else if (msg.what == 1) {
                mLoadingBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                mLoadingMoreBar.setVisibility(View.GONE);
                mMoreInfoView.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            } else if (msg.what == 4) {
                mLoadingMoreBar.setVisibility(View.GONE);
                mMoreInfoView.setVisibility(View.GONE);
                Toast.makeText(mContext, "没有更多影片", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    public static MovieFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(Contants.URL, url);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(Contants.URL);
        Log.d(TAG, "onCreate: url=" + mUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        mRecycleView.addOnScrollListener(new BottomTrackListener(
                view.getRootView().findViewById(R.id.navigation)));
        mRecycleView.addOnScrollListener(new BottomRefreshListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMoreData();
            }
        });
    }

    private void initView(View view) {
        mContext = getActivity();

        mErrorInfoView = view.findViewById(R.id.error_info);
        mLoadingBar = view.findViewById(R.id.loading_bar);
        mLoadingMoreBar = view.findViewById(R.id.loading_more);
        mMoreInfoView = view.findViewById(R.id.loading_more_info);

        mRecycleView = view.findViewById(R.id.new_movie_list);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecycleView.setLayoutManager(mLinearLayoutManager);

        mMovieInfos = new ArrayList<>();
        mPageList = new ArrayList<>();
        mAdapter = new NewMovieAdapter(mContext, mMovieInfos);
        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickLitener(new NewMovieAdapter.OnItemClickLitener() {

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra(Contants.URL, mMovieInfos.get(position).getUrl());
                intent.putExtra(Contants.NAME, mMovieInfos.get(position).getTitle());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        Log.d(TAG, "onFragmentVisibleChange: isVisible=" + isVisible);
        if (isVisible) {
            //更新界面数据，如果还在加载要显示loadingbar
            mAdapter.notifyDataSetChanged();
//            if (mRefreshState == STATE_REFRESHING) {
//                mRefreshListener.onRefreshing();
//            }
        } else {
            //关闭加载框
//            mRefreshListener.onRefreshFinish();
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        //去服务器下载数据
        String url = Contants.HOST_URL + mUrl + Contants.INDEX;
        NewMoviePresenter.getNewMovie(url, new NewMovieInfoCallback() {
            @Override
            public void onSuccess(List<MovieInfo> info, List<String> pages) {
                mMovieInfos.addAll(info);
                mPageList.addAll(pages);
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onFailed() {
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    private void loadMoreData() {
        mLoadingMoreBar.setVisibility(View.VISIBLE);
        mMoreInfoView.setVisibility(View.VISIBLE);
        NewMoviePresenter.getNextPage(mUrl +"/"+ mPageList.get(mPageIndex++), new NewMovieInfoCallback() {
            @Override
            public void onSuccess(List<MovieInfo> info, List<String> pages) {
                Log.d(TAG, "getNextPage success, new movie size=" + info.size());
                mMovieInfos.addAll(info);
                Log.d(TAG, "getNextPage success, all movie size=" + mMovieInfos.size());
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onFailed() {
                mHandler.sendEmptyMessage(4);
            }
        });
    }
}
