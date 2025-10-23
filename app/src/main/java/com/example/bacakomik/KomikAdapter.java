package com.example.bacakomik;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class KomikAdapter extends RecyclerView.Adapter<KomikAdapter.ViewHolder> {

    private List<Komik> komikList;
    private int layoutType;

    public KomikAdapter(List<Komik> komikList, int layoutType) {
        this.komikList = komikList;
        this.layoutType = layoutType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = (layoutType == 1)
                ? R.layout.item_list
                : R.layout.item_grid;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komik komik = komikList.get(position);
        holder.tvJudul.setText(komik.getJudul());
        holder.imgKomik.setImageResource(komik.getGambar());

        if (layoutType == 1 && holder.tvDeskripsi != null) {
            holder.tvDeskripsi.setText(komik.getDeskripsi());
        }
    }

    @Override
    public int getItemCount() {
        return komikList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgKomik;
        TextView tvJudul, tvDeskripsi;

        public ViewHolder(@NonNull View itemView, int layoutType) {
            super(itemView);
            imgKomik = itemView.findViewById(R.id.imgKomik);
            tvJudul = itemView.findViewById(R.id.tvJudul);

            if (layoutType == 1) {
                tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            }
        }
    }
}
