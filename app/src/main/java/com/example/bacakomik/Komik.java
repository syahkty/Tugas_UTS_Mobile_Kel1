package com.example.bacakomik;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class Komik implements Serializable {

    // --- 1. ID DOKUMEN (FOLDER NAME) ---
    // @Exclude artinya field ini TIDAK disimpan di dalam data dokumen Firestore,
    // tapi kita isi manual di Repository agar aplikasi tahu ID komiknya.
    @Exclude
    private String folderName;

    // --- 2. DATA DARI FIREBASE ---

    // Di Java kita pakai 'judul', tapi di Firestore namanya 'title'.
    // Anotasi ini menghubungkan keduanya.
    @PropertyName("title")
    private String judul;

    private String coverUrl;      // Sesuai dengan field 'coverUrl' di Firestore
    private int totalChapters;    // Sesuai dengan field 'totalChapters' di Firestore

    // Pastikan Anda menambahkan field 'status' (weekly/monthly) di Firestore
    private String status;

    // Pastikan Anda menambahkan field 'deskripsi' di Firestore jika ingin deskripsi tampil
    private String deskripsi;

    private double rating;        // Opsional: untuk RatingBar

    // --- 3. KONSTRUKTOR ---

    // WAJIB: Konstruktor kosong untuk Firestore
    public Komik() {
    }

    // Konstruktor manual (opsional)
    public Komik(String judul, String coverUrl, int totalChapters, String status) {
        this.judul = judul;
        this.coverUrl = coverUrl;
        this.totalChapters = totalChapters;
        this.status = status;
    }

    // --- 4. GETTERS & SETTERS (PENTING) ---

    // Mapping untuk JUDUL <-> TITLE
    @PropertyName("title")
    public String getJudul() {
        return judul;
    }

    @PropertyName("title")
    public void setJudul(String judul) {
        this.judul = judul;
    }

    // Mapping untuk FolderName (ID)
    @Exclude
    public String getFolderName() {
        return folderName;
    }

    @Exclude
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    // Getter Setter Standar (Nama variabel sama dengan nama field di Firestore)
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getTotalChapters() {
        return totalChapters;
    }

    public void setTotalChapters(int totalChapters) {
        this.totalChapters = totalChapters;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}