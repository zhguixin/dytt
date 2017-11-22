package site.zhguixin.dytt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import site.zhguixin.dytt.R;
import site.zhguixin.dytt.entity.MovieBean;
import site.zhguixin.dytt.entity.MovieInfo;

/**
 * Created by 10200927 on 2017/10/23.
 */

public class NewMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MovieInfo> mMovies;
    private Context mContext;

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }

    // 绑定ItemView的点击事件，View 层可以设置监听点击事件
    private OnItemClickLitener mOnItemClickLitener;
    public NewMovieAdapter(Context context, List<MovieInfo> list) {
        mContext = context;
        mMovies = list;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.list_new_movies_cardview, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MovieInfo movieInfo = mMovies.get(position);
        ((MovieHolder)holder).bindMovie(movieInfo);

        if(mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
