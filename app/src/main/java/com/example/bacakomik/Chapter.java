package com.example.bacakomik;

public class Chapter {

    private int pageCount;
    private String title;
    private int chapterNumber;

    // KONSTRUKTOR KOSONG WAJIB ADA UNTUK FIREBASE
    public Chapter() {
    }

    // --- GETTERS DAN SETTERS ---

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(int chapterNumber) { this.chapterNumber = chapterNumber; }
}