package com.example.bacakomik;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// Import Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

// Kita tidak lagi butuh ExecutorService karena Firebase menjalankan operasi I/O di background thread.
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    // Ganti DatabaseHelper dengan FirebaseAuth
    private final FirebaseAuth firebaseAuth;
    // private final DatabaseHelper databaseHelper; // Hapus atau jadikan komentar
    // private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Hapus

    // Status login dan register tetap sama
    private final MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();

    // Tambahan: Untuk mendapatkan user yang sedang login (opsional)
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        // Inisialisasi Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // databaseHelper = new DatabaseHelper(application); // Hapus

        // Cek status user saat ViewModel dibuat
        currentUser.setValue(firebaseAuth.getCurrentUser());
    }

    // ... (getters tetap sama)
    public LiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }

    public LiveData<Boolean> getRegistrationStatus() {
        return registrationStatus;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }
    // ...

    /**
     * Metode Login menggunakan Firebase Authentication (Email dan Password)
     */
    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login berhasil
                        loginStatus.postValue(true);
                        currentUser.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        // Login gagal
                        loginStatus.postValue(false);
                        // Anda bisa mendapatkan pesan error: task.getException().getMessage()
                    }
                });
        // Tidak perlu executor.execute(() -> {...}) lagi
    }
    public void signInWithGoogle(String idToken) {
        // 1. Buat credential Firebase dari ID Token Google
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        // 2. Gunakan credential untuk sign-in ke Firebase
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign-in dengan Firebase menggunakan Google berhasil
                        loginStatus.postValue(true);
                        currentUser.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        // Sign-in gagal
                        loginStatus.postValue(false);
                    }
                });
    }

    /**
     * Metode Register menggunakan Firebase Authentication (Email dan Password)
     */
    public void register(String username, String email, String password) {
        // Firebase Auth membuat user baru hanya dengan email dan password.
        // Jika Anda ingin menyimpan 'username' tambahan, Anda harus menggunakan
        // Firestore/Realtime Database setelah registrasi berhasil.

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registrasi berhasil
                        registrationStatus.postValue(true);
                        currentUser.postValue(firebaseAuth.getCurrentUser());

                        // TODO: Jika Anda perlu menyimpan 'username', lakukan di sini
                        // Contoh: databaseHelper.addUser(username, email, password);
                    } else {
                        // Registrasi gagal
                        registrationStatus.postValue(false);
                        // Anda bisa mendapatkan pesan error: task.getException().getMessage()
                    }
                });
        // Tidak perlu executor.execute(() -> {...}) lagi
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Tidak perlu mematikan executor lagi
        // executor.shutdown(); // Hapus
    }
}