package com.example.bacakomik;

import android.content.Intent;
// Hapus import yang tidak digunakan untuk data statis:
// import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Tambahkan ini
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private KomikAdapter adapter;
    private List<Komik> komikList; // Sebaiknya dihapus atau dijadikan penampung awal

    private TabLayout tabLayoutPopular;
    private ViewPager2 viewPagerPopular;
    private PopularTabsAdapter popularTabsAdapter;

    // VARIABEL BARU UNTUK FIREBASE/MVVM
    private HomeViewModel homeViewModel; // (Pastikan Anda sudah membuat HomeViewModel.java)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. INISIALISASI VIEWMODEL ---
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // ---------------------------------

        // --- TABS LAYOUT (Tidak berubah) ---
        tabLayoutPopular = view.findViewById(R.id.tabLayoutPopular);
        viewPagerPopular = view.findViewById(R.id.viewPagerPopular);

        popularTabsAdapter = new PopularTabsAdapter(getChildFragmentManager(), getLifecycle());
        viewPagerPopular.setAdapter(popularTabsAdapter);

        new TabLayoutMediator(tabLayoutPopular, viewPagerPopular,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Weekly");
                            break;
                        case 1:
                            tab.setText("Monthly");
                            break;
                        case 2:
                            tab.setText("All");
                            break;
                    }
                }
        ).attach();
        // -----------------------------------

        recyclerView = view.findViewById(R.id.recyclerGrid);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // --- 2. HAPUS LOGIKA DATA STATIS ---
        // Hapus SEMUA kode yang memuat dari R.array.*
        /* komikList = new ArrayList<>();
        String[] names = getResources().getStringArray(R.array.data_name);
        String[] descriptions = getResources().getStringArray(R.array.data_description);
        TypedArray images = getResources().obtainTypedArray(R.array.data_photo);

        for (int i = 0; i < names.length; i++) {
            komikList.add(new Komik(
                    names[i],
                    descriptions[i],
                    images.getResourceId(i, -1)
            ));
        }
        images.recycle();
        */
        // -----------------------------------

        // --- 3. INISIALISASI ADAPTER DENGAN LIST KOSONG ---
        // KomikAdapter baru harus bisa menerima list kosong di awal
        adapter = new KomikAdapter(new ArrayList<>(), 2); // 2 = Grid

        // Pasang callback item click
        adapter.setOnItemClickCallback(data -> {
            Intent intent = new Intent(getContext(), BacaKomikActivity.class);

            // Data yang sudah ada (Benar)
            intent.putExtra("EXTRA_FOLDER_NAME", data.getFolderName());
            intent.putExtra("EXTRA_JUDUL", data.getJudul());

            // --- [TAMBAHKAN 2 BARIS INI] ---
            // Mengirim Deskripsi agar sinopsis muncul
            intent.putExtra("EXTRA_DESKRIPSI", data.getDeskripsi());

            // Mengirim URL Gambar agar cover tampil
            intent.putExtra("EXTRA_COVER_URL", data.getCoverUrl());
            // -------------------------------

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // --- 4. AMATI LIVDATA DARI VIEWMODEL (PENGGANTI DATA STATIS) ---
        observeKomikData();
    }

    /**
     * Metode untuk mengamati LiveData dari ViewModel
     */
    private void observeKomikData() {
        homeViewModel.getAllKomiks().observe(getViewLifecycleOwner(), komikList -> {
            if (komikList != null && !komikList.isEmpty()) {
                // Ketika data dari Firebase berhasil diambil, update adapter
                adapter.setKomikList(komikList); // Asumsi KomikAdapter memiliki setKomikList()
            } else {
                // Tampilkan UI Loading atau pesan Error/Kosong
                Toast.makeText(getContext(), "Gagal memuat data komik dari server.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}