package site.zhguixin.dytt.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import site.zhguixin.dytt.R;
import site.zhguixin.dytt.adapter.FooterViewWrapper;
import site.zhguixin.dytt.adapter.NewMovieAdapter;
import site.zhguixin.dytt.entity.MovieInfo;
import site.zhguixin.dytt.presenter.NewMovieInfoCallback;
import site.zhguixin.dytt.presenter.SearchMoviePresenter;
import site.zhguixin.dytt.ui.view.FlowLayout;
import site.zhguixin.dytt.utils.BottomRefreshListener;
import site.zhguixin.dytt.utils.BottomTrackListener;
import site.zhguixin.dytt.utils.Contants;
import site.zhguixin.dytt.utils.Utils;

/**
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private Context mContext;
    private FlowLayout mFlowLayout;
    private EditText mInputView;
    private RecyclerView mSearchListView;
    private ProgressBar mLoadingBar;
    private TextView mNoResultView;
    private List<String> mHotWords;
    private List<MovieInfo> mMovieInfos;
    private List<String> mPageList;
    private NewMovieAdapter mAdapter;
    private FooterViewWrapper mFooterViewWrapper;
    private int mPageIndex = 0;
    private boolean mIsLoading = false;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Contants.FAILED) {
                mLoadingBar.setVisibility(View.GONE);
                mNoResultView.setVisibility(View.VISIBLE);
                mFooterViewWrapper.setVisible(false);
            } else if (msg.what == Contants.SUCCESS) {
                mLoadingBar.setVisibility(View.GONE);
                mFooterViewWrapper.setVisible(true);
                mFooterViewWrapper.notifyDataSetChanged();
            } else if (msg.what == Contants.MORE_DATA) {
                mFooterViewWrapper.setVisible(true);
                mFooterViewWrapper.notifyDataSetChanged();
            } else if (msg.what == Contants.NO_MORE) {
                mFooterViewWrapper.setVisible(false);
                mFooterViewWrapper.notifyDataSetChanged();
                Toast.makeText(mContext, "没有更多影片", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        layoutHotWords();
    }

    private void initView(View view) {
        mContext = getActivity();
        mFlowLayout = view.findViewById(R.id.flow_layout);
        mLoadingBar = view.findViewById(R.id.loading_search_bar);
        mNoResultView = view.findViewById(R.id.search_no_result);

        mInputView = view.findViewById(R.id.search_input);
        mInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.hideKeyboard(getActivity());
                    if(mInputView.getText().equals("")){
                        Toast.makeText(mContext, "请输入正确的关键字",Toast.LENGTH_LONG).show();
                        return false;
                    }
                    Toast.makeText(mContext, mInputView.getText(),Toast.LENGTH_LONG).show();
                    triggerSearch(mInputView.getText().toString());
                }
                return false;
            }
        });

        mSearchListView = view.findViewById(R.id.search_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        mSearchListView.setLayoutManager(mLinearLayoutManager);
        mMovieInfos = new ArrayList<>();
        mPageList = new ArrayList<>();
        mAdapter = new NewMovieAdapter(mContext, mMovieInfos);
        mFooterViewWrapper = new FooterViewWrapper(mContext, mAdapter);
        mSearchListView.setAdapter(mFooterViewWrapper);
        mFooterViewWrapper.setVisible(false);
        mSearchListView.addOnScrollListener(new BottomTrackListener(
                view.getRootView().findViewById(R.id.navigation)));
        mSearchListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalItem;
            private int lastVisible;
            private int visibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItem = mSearchListView.getChildCount();
                totalItem = mSearchListView.getLayoutManager().getItemCount();
                lastVisible = ((LinearLayoutManager)mSearchListView.getLayoutManager()).findLastVisibleItemPosition();
                if (!mIsLoading && lastVisible >= totalItem - 1 && visibleItem > 1) {
                    mIsLoading = true;
                    Log.d(TAG, "onScrolled: load more");
                    loadMoreMovies();
                }
//                Log.d(TAG, "onScrolled: totalItem=" + totalItem + " firstVisible=" + firstVisible +
//                    " visibleItem=" + visibleItem);
            }
        });

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

    // 热搜词汇，目前先固定写死了
    private void layoutHotWords(){
        mHotWords = new ArrayList<>();
        mHotWords.add("周星驰");
        mHotWords.add("动作喜剧");
        mHotWords.add("成龙");
        mHotWords.add("凯文史派西");
        mHotWords.add("低俗小说");
        mHotWords.add("恐怖悬疑");
        mHotWords.add("综艺");
        mHotWords.add("游戏");
        mHotWords.add("动漫");

        for (int i = 0; i < mHotWords.size(); i++) {
            hotSearchLayout(i);
        }
    }

    private void hotSearchLayout(int index) {
        int ranHeight = Utils.dip2px(mContext, 30);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ranHeight);
        lp.setMargins(Utils.dip2px(mContext, 10), Utils.dip2px(mContext, 10),
                Utils.dip2px(mContext, 10), 0);
        TextView tv = new TextView(mContext);
        tv.setPadding(Utils.dip2px(mContext, 15), 0, Utils.dip2px(mContext, 15), 0);
        tv.setTextColor(Color.parseColor("#FF3030"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        final int pos = index;
        tv.setText(mHotWords.get(index));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setLines(1);
        tv.setBackgroundResource(R.drawable.hot_words_bg);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mHotWords.get(pos), Toast.LENGTH_SHORT).show();
                triggerSearch(mHotWords.get(pos));
//                MovieFragment.newInstance(Contants.SEARCH_URL + Utils.encodeKeywod(mHotWords.get(pos)));
            }
        });
        mFlowLayout.addView(tv, lp);
    }

    private void triggerSearch(String keywords) {
        mLoadingBar.setVisibility(View.VISIBLE);
        mNoResultView.setVisibility(View.GONE);
        mMovieInfos.clear();
        mPageList.clear();

        mFooterViewWrapper.setVisible(false);
        mFooterViewWrapper.notifyDataSetChanged();

        String url = Contants.SEARCH_URL + Utils.encodeKeywod(keywords);
        SearchMoviePresenter.getMovie(url, keywords, new NewMovieInfoCallback() {

            @Override
            public void onSuccess(List<MovieInfo> info, List<String> pageList) {
                Log.d(TAG, "onSuccess: info=" + info.size() +
                " pages=" + pageList.size());
                mMovieInfos.addAll(info);
                if (info.size() > 0) {
                    mHandler.sendEmptyMessage(Contants.SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(Contants.FAILED);
                }
                mPageIndex = 0;
                mPageList.addAll(pageList);
            }

            @Override
            public void onFailed() {
                Log.d(TAG, "onFailed: search failed");
                mHandler.sendEmptyMessage(Contants.FAILED);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu: ");
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        // 搜索框默认为展开
        searchView.setIconifiedByDefault(false);
        searchView.setBackground(getResources().getDrawable(R.drawable.search_view_bg));
        searchView.setQueryHint("请输入要查询电影的关键字");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(mContext,query,Toast.LENGTH_SHORT).show();
                triggerSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void loadMoreMovies() {
        mIsLoading = false;
        if (mPageIndex >= mPageList.size()) {
            mHandler.sendEmptyMessage(Contants.NO_MORE);
            return;
        }
        SearchMoviePresenter.getNextPage(mPageList.get(mPageIndex), new NewMovieInfoCallback() {
            @Override
            public void onSuccess(List<MovieInfo> info, List<String> pageList) {
                mMovieInfos.addAll(info);
                mPageIndex++;
                if (info.size() > 0) {
                    mHandler.sendEmptyMessage(Contants.MORE_DATA);
                } else {
                    mHandler.sendEmptyMessage(Contants.FAILED);
                }
            }

            @Override
            public void onFailed() {
                Log.d(TAG, "onFailed: get more movies failed");
                mHandler.sendEmptyMessage(Contants.FAILED);
            }
        });
    }
}
