package com.emmanuelhmar.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsContent> newsContents;
    private Context context;

    public NewsAdapter(Context context, List<NewsContent> newsContents) {
        this.context = context;
        this.newsContents = newsContents;
        Log.d(TAG, "NewsAdapter: " + newsContents.size());
        Log.d(TAG, "NewsAdapter: " + newsContents);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_layout, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.title.setText(newsContents.get(position).getWebTitle());
        holder.section.setText(newsContents.get(position).getSectionName());
        holder.date.setText(newsContents.get(position).getDate());
        holder.type.setText(newsContents.get(position).getType());
        holder.pillar.setText(newsContents.get(position).getPillarName());
    }

    @Override
    public int getItemCount() {
        return newsContents.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.pillar)
        TextView pillar;
        @BindView(R.id.section)
        TextView section;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.type)
        TextView type;

        public final View view;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            ButterKnife.bind(this, itemView);
        }
    }
}
