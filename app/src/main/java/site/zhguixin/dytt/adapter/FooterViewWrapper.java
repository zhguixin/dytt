package site.zhguixin.dytt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import site.zhguixin.dytt.R;

/**
 * Created by zhguixin on 2017/11/22.
 */

public class FooterViewWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int FOOTER_VIEW = 1;
    private static final int ITEM_VIEW = 2;
    private RecyclerView.Adapter mAdapter;
    private Context mContext;
    private boolean mVisible = true;

    public FooterViewWrapper(Context context, RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.footer_item, parent, false);
            return new FooterViewHolder(view);
        } else{
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof FooterViewHolder) {
        if (getItemViewType(position) == FOOTER_VIEW) {
            FooterViewHolder footerHolder = (FooterViewHolder)holder;
            if (!mVisible) {
                footerHolder.mLoadingMoreBar.setVisibility(View.GONE);
                footerHolder.mMoreInfoView.setVisibility(View.GONE);
                mVisible = true;
            }
        } else {
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return FOOTER_VIEW;
        } else {
            return ITEM_VIEW;
        }
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView mMoreInfoView;
        private ProgressBar mLoadingMoreBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mLoadingMoreBar = itemView.findViewById(R.id.loading_more);
            mMoreInfoView = itemView.findViewById(R.id.loading_more_info);
        }
    }
}
