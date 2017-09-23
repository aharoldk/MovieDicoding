package com.example.aharoldk.moviedicoding.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aharoldk.moviedicoding.R;
import com.example.aharoldk.moviedicoding.detailclicklistener.DetailClickListener;
import com.example.aharoldk.moviedicoding.pojo.ResultsItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<ResultsItem> list;

    private DetailClickListener detailClickListener;

    public MovieAdapter(List<ResultsItem> list) {
        this.list = list;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_main, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        holder.bind(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailClickListener != null) {
                    detailClickListener.onItemDetailClicked(String.valueOf(list.get(position).getId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<ResultsItem> datas) {
        this.list.clear();
        list.addAll(datas);
        notifyDataSetChanged();
    }

    public void setItemClickListener(DetailClickListener itemClickListener) {
        detailClickListener = itemClickListener;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPoster) ImageView ivPoster;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDesk) TextView tvDesk;
        @BindView(R.id.tvDate) TextView tvDate;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ResultsItem resultsItem) {

            tvTitle.setText(String.valueOf(resultsItem.getTitle()));
            tvDesk.setText(String.valueOf(resultsItem.getOverview()));
            tvDate.setText(String.valueOf(resultsItem.getReleaseDate()));

            Picasso.with(itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w185"+resultsItem.getPosterPath())
                    .into(ivPoster);
        }
    }
}

