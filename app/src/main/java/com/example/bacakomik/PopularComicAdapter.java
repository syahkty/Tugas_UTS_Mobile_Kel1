package com.example.bacakomik;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PopularComicAdapter extends RecyclerView.Adapter<PopularComicAdapter.ViewHolder> {

    private List<Komik> comicList;

    public PopularComicAdapter(List<Komik> comicList) {
        this.comicList = comicList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRank;
        final ImageView ivCover;
        final TextView tvTitle;
        final TextView tvGenres;
        final RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            tvRank = view.findViewById(R.id.tvRank);
            ivCover = view.findViewById(R.id.ivCover);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvGenres = view.findViewById(R.id.tvGenres);
            ratingBar = view.findViewById(R.id.ratingBar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_popular_comic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komik komik = comicList.get(position);

        holder.tvRank.setText(String.valueOf(position + 1));

        holder.tvTitle.setText(komik.getJudul());
        holder.tvGenres.setText(komik.getDeskripsi());
        holder.ivCover.setImageResource(komik.getGambar());

        holder.ratingBar.setRating(4.5f);
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }
}