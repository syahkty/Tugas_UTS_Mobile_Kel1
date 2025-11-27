package com.example.bacakomik;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager; // Tambahkan ini
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AccountFragment extends Fragment {

    private HomeViewModel viewModel;

    // Adapter
    private KomikAdapter favAdapter;
    private KomikAdapter historyAdapter; // Adapter khusus History

    // UI Components - Favorit
    private RecyclerView rvFavorites;
    private LinearLayout emptyFavoriteLayout;

    // UI Components - History
    private RecyclerView rvHistory;
    private LinearLayout emptyHistoryLayout;

    // UI Components - Profil
    private TextView tvUsername, tvEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // 1. Inisialisasi View
        initViews(view);

        // 2. Setup ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 3. Setup UI Logic
        setupMenuLogout(view);
        applyUserInfo();

        // 4. Setup Lists
        setupHistoryList(); // <--- Setup History
        setupFavoritesList(); // <--- Setup Favorit

        return view;
    }

    private void initViews(View view) {
        rvFavorites = view.findViewById(R.id.rvFavorites);
        emptyFavoriteLayout = view.findViewById(R.id.emptyFavoriteLayout);

        rvHistory = view.findViewById(R.id.rvHistory);
        emptyHistoryLayout = view.findViewById(R.id.emptyHistoryLayout);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
    }

    private void setupMenuLogout(View view) {
        ImageView imgMenu = view.findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(v -> showPopupMenu(v));
    }

    private void applyUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                tvUsername.setText(user.getDisplayName());
            } else {
                String nameFromEmail = user.getEmail().split("@")[0];
                tvUsername.setText(nameFromEmail);
            }
        }
    }

    private void setupHistoryList() {
        // 1. UBAH INI: Gunakan angka 2 (Grid) bukan 1 (List)
        historyAdapter = new KomikAdapter(new ArrayList<>(), 2);

        // 2. UBAH INI: Gunakan GridLayoutManager (2 kolom)
        rvHistory.setLayoutManager(new GridLayoutManager(getContext(), 2));

        rvHistory.setAdapter(historyAdapter);

        // ... sisa kode tetap sama ...
        historyAdapter.setOnItemClickCallback(data -> {
            bukaKomik(data);
        });

        viewModel.getReadingHistory().observe(getViewLifecycleOwner(), historyList -> {
            // ... logika visibility tetap sama ...
            if (historyList != null && !historyList.isEmpty()) {
                rvHistory.setVisibility(View.VISIBLE);
                emptyHistoryLayout.setVisibility(View.GONE);
                historyAdapter.setKomikList(historyList);
            } else {
                rvHistory.setVisibility(View.GONE);
                emptyHistoryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    // --- SETUP FAVORIT ---
    private void setupFavoritesList() {
        // Menggunakan Layout Type 2 (Grid)
        favAdapter = new KomikAdapter(new ArrayList<>(), 2);
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFavorites.setAdapter(favAdapter);

        favAdapter.setOnItemClickCallback(data -> {
            bukaKomik(data);
        });

        viewModel.getFavorites().observe(getViewLifecycleOwner(), favList -> {
            if (favList != null && !favList.isEmpty()) {
                rvFavorites.setVisibility(View.VISIBLE);
                emptyFavoriteLayout.setVisibility(View.GONE);
                favAdapter.setKomikList(favList);
            } else {
                rvFavorites.setVisibility(View.GONE);
                emptyFavoriteLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    // Helper method untuk buka Activity (biar tidak duplikat kode)
    private void bukaKomik(Komik data) {
        Intent intent = new Intent(getContext(), BacaKomikActivity.class);
        intent.putExtra("EXTRA_FOLDER_NAME", data.getFolderName());
        intent.putExtra("EXTRA_JUDUL", data.getJudul());
        intent.putExtra("EXTRA_DESKRIPSI", data.getDeskripsi());
        intent.putExtra("EXTRA_COVER_URL", data.getCoverUrl());
        startActivity(intent);
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_account, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_about) {
                // --- PINDAH KE HALAMAN ABOUT ---
                Intent intent = new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            }
            else if (id == R.id.action_logout) {
                // --- LOGOUT ---
                logout();
                return true;
            }

            return false;
        });
        popupMenu.show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(requireContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        requireActivity().finish();
    }
}