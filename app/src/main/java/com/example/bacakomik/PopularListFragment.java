package com.example.bacakomik;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PopularListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PopularComicAdapter adapter;
    private HomeViewModel homeViewModel;

    private static final String ARG_FILTER = "filter_type";
    private String filterType; // Akan diisi "weekly", "monthly", atau "all"

    // --- newInstance (Memperbaiki error di PopularTabsAdapter) ---
    public static PopularListFragment newInstance(String filterType) {
        PopularListFragment fragment = new PopularListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTER, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterType = getArguments().getString(ARG_FILTER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inisialisasi ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        recyclerView = view.findViewById(R.id.rvPopularComics);
        // Pastikan ID ini (recycler_popular_list) sama dengan ID di XML fragment_popular_list.xml
        // Di kode Anda tertulis R.id.rvPopularComics, pastikan mana yang benar di XML.

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Inisialisasi Adapter (dengan List kosong)
        adapter = new PopularComicAdapter(new ArrayList<>());

        // --- [BAGIAN PENTING YANG KURANG] ---
        // Pasang Callback Klik untuk pindah halaman
        adapter.setOnItemClickCallback(data -> {
            Intent intent = new Intent(getContext(), BacaKomikActivity.class);

            // Kirim data penting untuk halaman baca
            intent.putExtra("EXTRA_FOLDER_NAME", data.getFolderName());
            intent.putExtra("EXTRA_JUDUL", data.getJudul());
            intent.putExtra("EXTRA_DESKRIPSI", data.getDeskripsi());
            intent.putExtra("EXTRA_COVER_URL", data.getCoverUrl());

            startActivity(intent);
        });
        // ------------------------------------

        recyclerView.setAdapter(adapter);

        // 3. Amati LiveData
        observeKomikData();
    }

    private void observeKomikData() {
        // Menggunakan filterType yang diterima
        homeViewModel.getFilteredKomiks(filterType).observe(getViewLifecycleOwner(), komikList -> {
            if (komikList != null && !komikList.isEmpty()) {
                adapter.setKomikList(komikList);
            } else {
                Toast.makeText(getContext(), "Gagal memuat data popular: " + filterType, Toast.LENGTH_SHORT).show();
            }
        });
    }
}