package com.example.bacakomik;

public class KomikData {

    public static int getTotalChapter(String folderName) {
        if (folderName.equals("the_undefeatable_swordsman")) {
            return 2; // Misal kamu baru punya 1 chapter di GitHub
        }
        if (folderName.equals("chronicles_of_the_demon_faction")) {
            return 1; // Misal kamu baru punya 1 chapter di GitHub
        }

        if (folderName.equals("solo_leveling")) {
            return 5; // Misal ada 5 chapter
        }
        // Default
        return 1;
    }

    // Method ini menerima Judul Folder dan Nomor Chapter
    // Lalu mengembalikan jumlah halaman (gambar) yang ada
    public static int getJumlahHalaman(String folderName, int chapter) {

        // 1. LOGIKA UNTUK SOLO LEVELING
        if (folderName.equals("solo_leveling")) {
            if (chapter == 1) return 40; // Misal chapter 1 ada 40 gambar
            if (chapter == 2) return 35; // Misal chapter 2 ada 35 gambar
            // ... dst
        }

        // 2. LOGIKA UNTUK ONE PIECE
        if (folderName.equals("one_piece")) {
            if (chapter == 1) return 50;
            if (chapter == 2) return 48;
        }

        if (folderName.equals("the_undefeatable_swordsman")) {
            if (chapter == 1) return 19;
            if (chapter == 2) return 94;
        }

        if (folderName.equals("chronicles_of_the_demon_faction")) {
            if (chapter == 1) return 109;
            if (chapter == 2) return 94;
        }

        // 3. LOGIKA UNTUK JUDUL LAIN (Tambahkan sendiri sesuai folder supabase)
        if (folderName.equals("eleceed")) {
            return 30; // Kalau malas if-else per chapter, bisa dipukul rata 30 semua
        }

        // Default: Kalau judul tidak dikenali, kembalikan 12 (sebagai jaga-jaga)
        return 18;
    }
}