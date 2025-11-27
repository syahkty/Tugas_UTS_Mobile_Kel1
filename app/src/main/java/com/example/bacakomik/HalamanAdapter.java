package com.example.bacakomik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList; // Tambahkan ini
import java.util.List;

public class HalamanAdapter extends RecyclerView.Adapter<HalamanAdapter.ViewHolder> {

    // Inisialisasi dengan ArrayList kosong agar tidak crash jika data belum ada
    private List<String> imageUrls = new ArrayList<>();
    private Context context;

    // Konstruktor disederhanakan (Data dikirim nanti)
    public HalamanAdapter(Context context) {
        this.context = context;
    }

    // --- [PENTING] TAMBAHAN METODE UNTUK UPDATE DATA ---
    // Metode ini dipanggil oleh BacaKomikActivity setelah URL berhasil dibuat
    public void setListUrlGambar(List<String> newUrls) {
        this.imageUrls = newUrls;
        notifyDataSetChanged(); // Refresh RecyclerView
    }
    // ---------------------------------------------------

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Pastikan nama layout XML Anda benar (misal: item_halaman_komik)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_halaman_komik, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Kita asumsikan Activity sudah mengirim URL LENGKAP (misal: https://.../1.jpg)
        String urlLengkap = imageUrls.get(position);

        // 1. Animasi Loading (Kode Anda yang Bagus)
        CircularProgressDrawable loader = new CircularProgressDrawable(context);
        loader.setStrokeWidth(5f);
        loader.setCenterRadius(30f);
        loader.start();

        // 2. Pasang ke Glide
        Glide.with(context)
                .load(urlLengkap) // JANGAN ditambah ".jpg" lagi disini agar fleksibel
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(loader) // Tampilkan spinner saat loading
//                .error(R.drawable.ic_launcher_background) // Gambar error jika gagal (Ganti icon error Anda)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Pastikan ID di XML item_halaman_komik adalah imgHalaman (atau sesuaikan)
            imageView = itemView.findViewById(R.id.img_halaman);
        }
    }
}