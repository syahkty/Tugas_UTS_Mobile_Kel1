package com.example.bacakomik;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class BacaKomikActivity extends AppCompatActivity {

    // --- Variabel UI ---
    private ImageView imgCover, imgCoverBackground;
    private TextView tvDetailJudul, tvDetailDeskripsi;
    private ImageButton btnBack, btnFavorite;
    private RecyclerView rvHalamanKomik;

    // --- Variabel Data & Logic ---
    private HalamanAdapter adapter;
    private HomeViewModel viewModel;
    private String folderName; // ID Dokumen
    private int currentChapter = 1; // Default Chapter 1

    // --- Variabel History (Resume Reading) ---
    private int lastVisiblePage = 0;
    private String judulKomik, coverUrlKomik;

    // --- Variabel Favorit ---
    private boolean isFavorite = false;
    private Komik currentKomik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baca_komik);

        // 1. Inisialisasi Tampilan
        initViews();

        // 2. Tangkap Data dari Halaman Sebelumnya
        folderName = getIntent().getStringExtra("EXTRA_FOLDER_NAME");
        String judul = getIntent().getStringExtra("EXTRA_JUDUL");
        String deskripsi = getIntent().getStringExtra("EXTRA_DESKRIPSI");
        String coverUrl = getIntent().getStringExtra("EXTRA_COVER_URL");

        // Simpan data ke variabel global untuk keperluan History/Favorit
        judulKomik = judul;
        coverUrlKomik = coverUrl;

        // Siapkan objek Komik untuk fitur Favorit
        currentKomik = new Komik();
        currentKomik.setFolderName(folderName);
        currentKomik.setJudul(judul);
        currentKomik.setDeskripsi(deskripsi);
        currentKomik.setCoverUrl(coverUrl);

        // 3. Tampilkan Data Header
        setupHeader(judul, deskripsi, coverUrl);

        // 4. Setup RecyclerView
        setupRecyclerView();

        // 5. Setup ViewModel & Load Data
        if (folderName != null) {
            initViewModelAndLoadData(folderName, currentChapter);
        } else {
            Toast.makeText(this, "Error: ID Komik tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        // 6. Setup Listener Tombol & Scroll
        btnBack.setOnClickListener(v -> finish());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        setupScrollListener();
    }

    private void initViews() {
        imgCover = findViewById(R.id.imgCover);
        imgCoverBackground = findViewById(R.id.imgCoverBackground);
        tvDetailJudul = findViewById(R.id.tvDetailJudul);
        tvDetailDeskripsi = findViewById(R.id.tvDetailDeskripsi);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        rvHalamanKomik = findViewById(R.id.rv_halaman_komik);
    }

    private void setupHeader(String judul, String deskripsi, String coverUrl) {
        tvDetailJudul.setText(judul);
        if (deskripsi != null && !deskripsi.isEmpty()) {
            tvDetailDeskripsi.setText(deskripsi);
        } else {
            tvDetailDeskripsi.setText("Tidak ada sinopsis.");
        }

        if (coverUrl != null) {
            Glide.with(this).load(coverUrl).into(imgCover);
            Glide.with(this).load(coverUrl).into(imgCoverBackground);
        }
    }

    private void setupRecyclerView() {
        adapter = new HalamanAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvHalamanKomik.setLayoutManager(layoutManager);
        rvHalamanKomik.setHasFixedSize(true);
        rvHalamanKomik.setItemViewCacheSize(20);
        rvHalamanKomik.setAdapter(adapter);
    }

    private void initViewModelAndLoadData(String idKomik, int chapterKe) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // A. Load Gambar Chapter
        viewModel.getJumlahHalaman(idKomik, chapterKe).observe(this, totalHalaman -> {
            if (totalHalaman > 0) {
                generateGithubUrls(idKomik, chapterKe, totalHalaman);

                // B. Setelah gambar siap, Cek History untuk Resume
                checkAndRestoreLastRead(idKomik, chapterKe);

            } else {
                Toast.makeText(this, "Chapter " + chapterKe + " belum tersedia.", Toast.LENGTH_SHORT).show();
            }
        });

        // C. Cek Status Favorit
        checkFavoriteStatus(idKomik);
    }

    // --- LOGIKA GENERATE URL GITHUB ---
    private void generateGithubUrls(String folderName, int chapter, int totalHalaman) {
        List<String> urls = new ArrayList<>();
        String baseUrl = "https://raw.githubusercontent.com/syahkty/MyComicAssets/main/";
        String folderPath = folderName + "/chapter" + chapter + "/";

        for (int i = 1; i <= totalHalaman; i++) {
            String namaFile = String.format("%02d", i);
            String finalUrl = baseUrl + folderPath + namaFile + ".jpg";
            urls.add(finalUrl);
        }
        adapter.setListUrlGambar(urls);
    }

    // --- LOGIKA HISTORY / RESUME READING ---
    private void setupScrollListener() {
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);

        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    // Hitung posisi relatif RecyclerView terhadap ScrollView
                    // (Karena ada Header di atas RecyclerView)
                    int rvTop = rvHalamanKomik.getTop();

                    // Posisi Y di dalam RecyclerView yang sedang dilihat user
                    // Kita tambah sedikit offset (misal 50px) agar tidak terlalu sensitif di perbatasan
                    int targetY = scrollY - rvTop + 50;

                    // Cari View (Gambar) mana yang ada di koordinat Y tersebut
                    View child = rvHalamanKomik.findChildViewUnder(0, targetY);

                    if (child != null) {
                        int position = rvHalamanKomik.getChildAdapterPosition(child);
                        if (position != RecyclerView.NO_POSITION) {
                            lastVisiblePage = position;

                            // Log untuk memastikan (Bisa dihapus nanti)
                            // android.util.Log.d("HISTORY", "Sekarang halaman: " + lastVisiblePage);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewModel != null && folderName != null) {
            viewModel.saveReadingProgress(folderName, currentChapter, lastVisiblePage, judulKomik, coverUrlKomik);
        }
    }

    // Ganti method ini dengan yang baru
    private void checkAndRestoreLastRead(String idKomik, int chapterKe) {
        viewModel.getLastReadPage(idKomik, chapterKe).observe(this, savedPage -> {
            if (savedPage > 0) {
                Toast.makeText(this, "Melanjutkan dari halaman " + (savedPage + 1), Toast.LENGTH_SHORT).show();

                // Delay agak lama (1.5 detik) untuk memastikan:
                // 1. URL gambar selesai dibuat
                // 2. Layout selesai digambar
                // 3. Gambar minimal placeholder sudah muncul
                new Handler().postDelayed(() -> {
                    try {
                        // 1. Cari View (Gambar) di posisi tersebut
                        RecyclerView.ViewHolder holder = rvHalamanKomik.findViewHolderForAdapterPosition(savedPage);

                        if (holder != null) {
                            // 2. Hitung posisi Y gambar tersebut relatif terhadap RecyclerView
                            float yGambar = holder.itemView.getY();

                            // 3. Hitung posisi Y RecyclerView relatif terhadap layar atas (di bawah Header)
                            int yRecycler = rvHalamanKomik.getTop();

                            // 4. Scroll NESTED SCROLL VIEW ke total Y tersebut
                            NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
                            if (nestedScrollView != null) {
                                nestedScrollView.smoothScrollTo(0, (int) (yGambar + yRecycler));
                            }
                        } else {
                            // Jika View null (biasanya karena belum ter-render karena jauh di bawah),
                            // Kita paksa scroll RecyclerView dulu agar sistem me-render view-nya
                            rvHalamanKomik.scrollToPosition(savedPage);

                            // Lalu coba scroll parent lagi setelah sedikit delay
                            new Handler().postDelayed(() -> {
                                RecyclerView.ViewHolder holder2 = rvHalamanKomik.findViewHolderForAdapterPosition(savedPage);
                                if (holder2 != null) {
                                    NestedScrollView nsv = findViewById(R.id.nestedScrollView);
                                    if (nsv != null) {
                                        nsv.smoothScrollTo(0, (int) (holder2.itemView.getY() + rvHalamanKomik.getTop()));
                                    }
                                }
                            }, 200);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 1500); // Delay 1.5 detik (bisa disesuaikan)
            }
        });
    }

    // --- LOGIKA FAVORIT ---
    private void checkFavoriteStatus(String idKomik) {
        viewModel.checkIsFavorite(idKomik).observe(this, isFav -> {
            this.isFavorite = isFav;
            updateFavoriteIcon(isFav);
        });
    }

    private void toggleFavorite() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Silakan Login terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            viewModel.removeFromFavorite(folderName);
            Toast.makeText(this, "Dihapus dari Favorit", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.addToFavorite(currentKomik);
            Toast.makeText(this, "Ditambahkan ke Favorit", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteIcon(boolean isFav) {
        if (isFav) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }
}