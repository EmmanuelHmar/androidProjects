package com.emmanuelhmar.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsContent> newsContents;
    private onNoteListener clickListener;

    public NewsAdapter(List<NewsContent> newsContents, onNoteListener onClickListener) {
        this.newsContents = newsContents;
        this.clickListener = onClickListener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_layout, parent, false);
        return new NewsViewHolder(view, clickListener);
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

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        onNoteListener onNoteListener;

        public final View view;

        public NewsViewHolder(@NonNull View itemView, onNoteListener clickListener) {
            super(itemView);
            view = itemView;
            onNoteListener = clickListener;

            itemView.setOnClickListener(this::onClick);

            ButterKnife.bind(this, itemView);
        }

            @Override
            public void onClick (View view){
                onNoteListener.onViewClick(getAdapterPosition());
            }
        }

        public interface onNoteListener {
            void onViewClick(int position);
        }
    }
