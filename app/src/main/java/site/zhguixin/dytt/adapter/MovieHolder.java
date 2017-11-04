package site.zhguixin.dytt.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import site.zhguixin.dytt.R;
import site.zhguixin.dytt.entity.MovieBean;
import site.zhguixin.dytt.entity.MovieInfo;

/**
 * Created by 10200927 on 2017/10/23.
 */

class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTitle;
    private TextView mFullTitle;
    private TextView mDate;

    private MovieInfo mMovie;

    public MovieHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mTitle = itemView.findViewById(R.id.title);
        mFullTitle = itemView.findViewById(R.id.full_title);
        mDate = itemView.findViewById(R.id.date);
    }

    public void bindMovie(MovieInfo movie) {
        mMovie = movie;
        mTitle.setText(movie.getTitle());
        mFullTitle.setText(movie.getFullTitle());
        mDate.setText(movie.getDate());
    }

    @Override
    public void onClick(View v) {
        Log.d("zgx", "onClick: url=" + mMovie.getUrl());
//        Intent intent = new Intent(context, MovieDetail.class);
//        intent.putExtra(URL, mMovie.getUrl());
//        context.startActivity(intent);
    }
}
