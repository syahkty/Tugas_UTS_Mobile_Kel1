package com.example.bacakomik;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView; // Import SearchView
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookFragment extends Fragment {

    private RecyclerView recyclerView;
    private KomikAdapter adapter;
    private HomeViewModel homeViewModel;
    private SearchView searchView; // Variabel SearchView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inisialisasi View
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView); // Hubungkan ID

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new KomikAdapter(new ArrayList<>(), 1); // 1 = Linear List

        adapter.setOnItemClickCallback(data -> {
            Intent intent = new Intent(getContext(), BacaKomikActivity.class);
            intent.putExtra("EXTRA_FOLDER_NAME", data.getFolderName());
            intent.putExtra("EXTRA_JUDUL", data.getJudul());
            intent.putExtra("EXTRA_DESKRIPSI", data.getDeskripsi());
            intent.putExtra("EXTRA_COVER_URL", data.getCoverUrl());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // --- LOGIKA SEARCH VIEW ---
        setupSearchView();
        // --------------------------

        observeKomikData();

        return view;
    }

    private void setupSearchView() {
        // Mencegah keyboard langsung muncul saat dibuka
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Aksi saat tombol enter ditekan (Opsional, karena kita pakai real-time)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Aksi saat teks berubah (Real-time search)
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return true;
            }
        });
    }

    private void observeKomikData() {
        homeViewModel.getAllKomiks().observe(getViewLifecycleOwner(), komikList -> {
            if (komikList != null && !komikList.isEmpty()) {
                adapter.setKomikList(komikList);
            } else {
                Toast.makeText(getContext(), "Gagal memuat data komik dari Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}