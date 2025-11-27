package com.example.bacakomik;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final KomikRepository repository;

    public HomeViewModel() {
        this.repository = new KomikRepository();
    }

    // Dipanggil oleh HomeFragment dan BookFragment
    public LiveData<List<Komik>> getAllKomiks() {
        return repository.getKomiksByFilter("all");
    }

    // Dipanggil oleh PopularListFragment
    public LiveData<List<Komik>> getFilteredKomiks(String filter) {
        return repository.getKomiksByFilter(filter);
    }

    public LiveData<Integer> getJumlahHalaman(String folderName, int chapter) {
        return repository.getJumlahHalaman(folderName, chapter);
    }

    // Fungsi Favorit
    public void addToFavorite(Komik komik) {
        repository.addFavorite(komik);
    }

    public void removeFromFavorite(String folderName) {
        repository.removeFavorite(folderName);
    }

    public LiveData<Boolean> checkIsFavorite(String folderName) {
        return repository.checkIsFavorite(folderName);
    }

    public LiveData<List<Komik>> getFavorites() {
        return repository.getFavorites();
    }

    public void saveReadingProgress(String folderName, int chapter, int pageIndex, String judul, String coverUrl) {
        repository.saveReadingProgress(folderName, chapter, pageIndex, judul, coverUrl);
    }

    public LiveData<Integer> getLastReadPage(String folderName, int chapter) {
        return repository.getLastReadPage(folderName, chapter);
    }

    public LiveData<List<Komik>> getReadingHistory() {
        return repository.getReadingHistory();
    }
}