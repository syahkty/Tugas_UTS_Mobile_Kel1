package com.example.bacakomik;

import android.util.Log; // Wajib ada untuk logging
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KomikRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // PENTING: Sesuaikan ini dengan nama koleksi di Firebase Anda!
    // Berdasarkan screenshot sebelumnya, namanya adalah "comics" (bukan "comiks" atau "komiks")
    private static final String KOMIKS_COLLECTION = "comics";

    // Tag ini digunakan untuk memfilter pencarian di Logcat
    private static final String TAG = "CEK_DATABASE";

    public LiveData<List<Komik>> getFavorites() {
        MutableLiveData<List<Komik>> favoriteList = new MutableLiveData<>();

        if (auth.getCurrentUser() == null) {
            favoriteList.setValue(new ArrayList<>());
            return favoriteList;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("favorites")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FAV", "Listen failed.", error);
                        return;
                    }

                    List<Komik> list = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Komik komik = doc.toObject(Komik.class);
                            // Pastikan folderName terisi (karena @Exclude)
                            komik.setFolderName(doc.getId());
                            list.add(komik);
                        }
                    }
                    favoriteList.setValue(list);
                });

        return favoriteList;
    }

    /**
     * Mengambil daftar komik dengan filter dan LOGGING LENGKAP.
     */
    public LiveData<List<Komik>> getKomiksByFilter(String filter) {
        Log.d(TAG, "1. [START] Memulai request ke Firestore. Koleksi: " + KOMIKS_COLLECTION + ", Filter: " + filter);

        MutableLiveData<List<Komik>> komikListLiveData = new MutableLiveData<>();
        Query query;

        if (filter.equals("all")) {
            query = db.collection(KOMIKS_COLLECTION);
        } else {
            query = db.collection(KOMIKS_COLLECTION).whereEqualTo("status", filter);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // LOGGING 1: Cek apakah connect berhasil
                    Log.d(TAG, "2. [SUKSES KONEKSI] Berhasil terhubung ke Firestore.");
                    Log.d(TAG, "3. [JUMLAH DATA] Ditemukan " + queryDocumentSnapshots.size() + " dokumen.");

                    List<Komik> komikList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // LOGGING 2: Cek data mentah sebelum diubah ke Java Object
                            Log.d(TAG, "   > Memproses Dokumen ID: " + document.getId());
                            Log.d(TAG, "     Data Mentah: " + document.getData());

                            // Proses Deserialisasi (Mapping ke Komik.java)
                            Komik komik = document.toObject(Komik.class);

                            // Set ID manual
                            komik.setFolderName(document.getId());

                            // LOGGING 3: Cek hasil mapping
                            Log.d(TAG, "     Hasil Mapping -> Judul: " + komik.getJudul() + ", Status: " + komik.getStatus());

                            if (komik.getJudul() == null) {
                                Log.e(TAG, "     ⚠️ PERINGATAN: Judul terbaca NULL! Cek @PropertyName di Komik.java");
                            }

                            komikList.add(komik);
                        } catch (Exception e) {
                            Log.e(TAG, "     ❌ ERROR saat convert dokumen ini: " + e.getMessage());
                        }
                    }

                    // Kirim data ke ViewModel
                    komikListLiveData.setValue(komikList);
                    Log.d(TAG, "4. [SELESAI] Data dikirim ke ViewModel. Total item: " + komikList.size());
                })
                .addOnFailureListener(e -> {
                    // LOGGING ERROR FATAL
                    Log.e(TAG, "❌ [GAGAL TOTAL] Tidak bisa mengambil data: " + e.getMessage(), e);
                    komikListLiveData.setValue(new ArrayList<>());
                });

        return komikListLiveData;
    }

    /**
     * Mengambil jumlah total chapter
     */
    public LiveData<Integer> getTotalChapter(String folderName) {
        Log.d(TAG, "Request Total Chapter untuk ID: " + folderName);
        MutableLiveData<Integer> totalChapterLiveData = new MutableLiveData<>();

        db.collection(KOMIKS_COLLECTION).document(folderName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long totalChapters = documentSnapshot.getLong("totalChapters");
                        Log.d(TAG, "   > Total Chapter ditemukan: " + totalChapters);
                        totalChapterLiveData.setValue(totalChapters != null ? totalChapters.intValue() : 0);
                    } else {
                        Log.w(TAG, "   > Dokumen komik tidak ditemukan!");
                        totalChapterLiveData.setValue(0);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "   > Gagal ambil total chapter: " + e.getMessage());
                    totalChapterLiveData.setValue(0);
                });

        return totalChapterLiveData;
    }

    /**
     * Mengambil jumlah halaman per chapter
     */
    public LiveData<Integer> getJumlahHalaman(String folderName, int chapter) {
        Log.d(TAG, "Request Halaman untuk Komik: " + folderName + ", Chapter: " + chapter);
        MutableLiveData<Integer> pageCountLiveData = new MutableLiveData<>();

        db.collection(KOMIKS_COLLECTION).document(folderName)
                .collection("chapters").document(String.valueOf(chapter))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long pageCount = documentSnapshot.getLong("pageCount");
                    Log.d(TAG, "   > Jumlah Halaman ditemukan: " + pageCount);
                    pageCountLiveData.setValue(pageCount != null ? pageCount.intValue() : 0);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "   > Gagal ambil halaman: " + e.getMessage());
                    pageCountLiveData.setValue(0);
                });

        return pageCountLiveData;
    }

    // 1. Tambah ke Favorit
    public void addFavorite(Komik komik) {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        // Kita simpan data minimal (Judul & Cover) agar mudah ditampilkan di menu "Favorit Saya" nanti
        Map<String, Object> favData = new HashMap<>();
        favData.put("title", komik.getJudul());
        favData.put("coverUrl", komik.getCoverUrl());
        favData.put("folderName", komik.getFolderName());
        favData.put("deskripsi", komik.getDeskripsi());
        favData.put("timestamp", System.currentTimeMillis()); // Agar bisa diurutkan

        db.collection("users").document(uid)
                .collection("favorites").document(komik.getFolderName())
                .set(favData)
                .addOnSuccessListener(aVoid -> Log.d("FAV", "Berhasil ditambahkan"))
                .addOnFailureListener(e -> Log.e("FAV", "Gagal nambah: " + e.getMessage()));
    }

    // 2. Hapus dari Favorit
    public void removeFavorite(String folderName) {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("favorites").document(folderName)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("FAV", "Berhasil dihapus"));
    }

    // 3. Cek Status Favorit (Untuk Ikon Awal)
    public LiveData<Boolean> checkIsFavorite(String folderName) {
        MutableLiveData<Boolean> isFav = new MutableLiveData<>();

        if (auth.getCurrentUser() == null) {
            isFav.setValue(false);
            return isFav;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("favorites").document(folderName)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        isFav.setValue(false);
                        return;
                    }
                    // Jika dokumen ada, berarti Favorit = True
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        isFav.setValue(true);
                    } else {
                        isFav.setValue(false);
                    }
                });

        return isFav;
    }

    // 1. Simpan Progres Baca ke Firestore
    public void saveReadingProgress(String folderName, int chapter, int pageIndex, String judul, String coverUrl) {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        // Data yang disimpan
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("folderName", folderName);
        historyData.put("title", judul); // Disimpan agar bisa ditampilkan di list History
        historyData.put("coverUrl", coverUrl);
        historyData.put("lastChapter", chapter);
        historyData.put("lastPageIndex", pageIndex); // Halaman terakhir dilihat
        historyData.put("lastReadTime", System.currentTimeMillis()); // Untuk urutan waktu

        // Simpan di koleksi users -> history -> [id_komik]
        db.collection("users").document(uid)
                .collection("history").document(folderName)
                .set(historyData) // Menggunakan .set() agar menimpa data lama
                .addOnFailureListener(e -> Log.e("HISTORY", "Gagal simpan history: " + e.getMessage()));
    }

    // 2. Ambil Posisi Terakhir (Untuk Resume)
    public LiveData<Integer> getLastReadPage(String folderName, int chapter) {
        MutableLiveData<Integer> lastPage = new MutableLiveData<>();

        if (auth.getCurrentUser() == null) {
            lastPage.setValue(0);
            return lastPage;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("history").document(folderName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Cek apakah chapter yang disimpan SAMA dengan chapter yang dibuka sekarang
                        Long savedChapter = documentSnapshot.getLong("lastChapter");
                        Long savedPage = documentSnapshot.getLong("lastPageIndex");

                        if (savedChapter != null && savedChapter == chapter && savedPage != null) {
                            lastPage.setValue(savedPage.intValue());
                        } else {
                            // Jika chapter beda (misal user buka chapter baru), mulai dari 0
                            lastPage.setValue(0);
                        }
                    } else {
                        lastPage.setValue(0);
                    }
                });

        return lastPage;
    }

    // ... kode sebelumnya ...

    /**
     * Mengambil Riwayat Baca (Diurutkan dari yang terbaru)
     */
    public LiveData<List<Komik>> getReadingHistory() {
        MutableLiveData<List<Komik>> historyList = new MutableLiveData<>();

        if (auth.getCurrentUser() == null) {
            historyList.setValue(new ArrayList<>());
            return historyList;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("history")
                .orderBy("lastReadTime", com.google.firebase.firestore.Query.Direction.DESCENDING) // Urutkan waktu
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("HISTORY", "Listen failed.", error);
                        return;
                    }

                    List<Komik> list = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Komik komik = doc.toObject(Komik.class);
                            // Mapping manual jika nama field beda, tapi jika sudah pakai @PropertyName aman
                            komik.setFolderName(doc.getId());
                            list.add(komik);
                        }
                    }
                    historyList.setValue(list);
                });

        return historyList;
    }
}