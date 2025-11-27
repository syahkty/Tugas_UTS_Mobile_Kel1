package com.example.bacakomik;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.ArrayList; // Import ini diperlukan

public class PopularComicAdapter extends RecyclerView.Adapter<PopularComicAdapter.ViewHolder> {

    private List<Komik> komikList;

    // 1. Tambahkan variabel untuk menampung Callback
    private OnItemClickCallback onItemClickCallback;

    // 2. Tambahkan method Setter agar Fragment bisa memasang aksinya
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public PopularComicAdapter(List<Komik> comicList) {
        // Jika list yang masuk null, inisialisasi dengan list kosong
        this.komikList = comicList != null ? comicList : new ArrayList<>();
    }

    // --- METODE YANG HILANG (PENTING UNTUK MVVM) ---
    /**
     * Metode untuk menerima data baru (List<Komik>) dari ViewModel/Firebase
     * dan memperbarui tampilan RecyclerView.
     */
    public void setKomikList(List<Komik> newList) {
        // 1. Ganti list data lama dengan yang baru
        this.komikList = newList;

        // 2. Beri tahu RecyclerView bahwa data telah berubah
        notifyDataSetChanged();
    }
    // ------------------------------------------------

    // 3. Tambahkan Interface
    public interface OnItemClickCallback {
        void onItemClicked(Komik data);
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
        Komik komik = komikList.get(position);

        // PENTING: Implementasi data binding dan Glide

        // Pemuatan Gambar dari URL (Firebase)
        Glide.with(holder.itemView.getContext())
                .load(komik.getCoverUrl())
                .into(holder.ivCover);

        // Binding data lainnya
        holder.tvTitle.setText(komik.getJudul());
        // holder.tvGenres.setText(komik.getGenres()); // Jika ada field genres
         holder.tvRank.setText(String.valueOf(position + 1)); // Untuk rank

        // 4. Tambahkan Listener pada itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickCallback != null) {
                    onItemClickCallback.onItemClicked(komikList.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Pastikan tidak terjadi NullPointerException
        return komikList != null ? komikList.size() : 0;
    }
}