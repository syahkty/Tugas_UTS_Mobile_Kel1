package com.example.bacakomik;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class KomikAdapter extends RecyclerView.Adapter<KomikAdapter.ViewHolder> {

    private List<Komik> komikList;      // List yang ditampilkan
    private List<Komik> originalList;   // List cadangan (untuk reset search)
    private int layoutType;
    private OnItemClickCallback onItemClickCallback;

    public KomikAdapter(List<Komik> comicList, int layoutType) {
        this.layoutType = layoutType;
        // Inisialisasi kedua list
        this.komikList = (comicList != null) ? comicList : new ArrayList<>();
        this.originalList = new ArrayList<>(this.komikList);
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    // --- [UPDATE DATA DARI FIREBASE] ---
    public void setKomikList(List<Komik> newList) {
        this.komikList = newList;
        // Simpan salinan data asli untuk keperluan searching
        this.originalList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // --- [LOGIKA SEARCH / FILTER BARU] ---
    public void filter(String query) {
        if (query.isEmpty()) {
            // Jika search kosong, kembalikan ke list asli
            komikList = new ArrayList<>(originalList);
        } else {
            List<Komik> filtered = new ArrayList<>();
            String text = query.toLowerCase(); // Ubah ke huruf kecil agar tidak sensitif case

            for (Komik item : originalList) {
                // Cek apakah judul mengandung teks pencarian
                if (item.getJudul().toLowerCase().contains(text)) {
                    filtered.add(item);
                }
            }
            komikList = filtered;
        }
        notifyDataSetChanged(); // Update tampilan
    }
    // -------------------------------------

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = (layoutType == 1) ? R.layout.item_list : R.layout.item_grid;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komik komik = komikList.get(position);

        if (holder.tvJudul != null) holder.tvJudul.setText(komik.getJudul());

        if (layoutType == 1 && holder.tvDeskripsi != null) {
            holder.tvDeskripsi.setText(komik.getDeskripsi());
        }

        Glide.with(holder.itemView.getContext())
                .load(komik.getCoverUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickCallback != null) {
                onItemClickCallback.onItemClicked(komik); // Kirim objek komik langsung
            }
        });
    }

    @Override
    public int getItemCount() {
        return (komikList != null) ? komikList.size() : 0;
    }

    public interface OnItemClickCallback {
        void onItemClicked(Komik data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgCover;
        public TextView tvJudul;
        public TextView tvDeskripsi;

        public ViewHolder(@NonNull View itemView, int layoutType) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgKomik);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            if (layoutType == 1) {
                tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            }
        }
    }
}