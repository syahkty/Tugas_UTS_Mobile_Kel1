package com.example.bacakomik;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.bacakomik.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    // Tambahkan variabel untuk melacak upaya registrasi
    private boolean isRegistrationAttempted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // --- 1. Ubah Observer Registrasi ---
        // Kita tidak hanya mengecek isSuccess, tapi juga pesan error spesifik jika ada
        authViewModel.getRegistrationStatus().observe(this, isSuccess -> {
            if (isRegistrationAttempted) {
                if (isSuccess) {
                    // Registrasi Firebase berhasil
                    Toast.makeText(this, "Registrasi Berhasil! Silakan masuk.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // Registrasi Firebase gagal
                    // PENTING: Anda perlu mengubah AuthViewModel untuk menyediakan pesan error yang lebih detail
                    // (misalnya: 'Email sudah terdaftar' atau 'Password terlalu lemah')
                    Toast.makeText(this, "Registrasi Gagal! Coba email lain atau password lebih kuat.", Toast.LENGTH_LONG).show();
                }
                isRegistrationAttempted = false;
            }
        });
        // ------------------------------------

        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            String username = binding.username.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) { // Tambahkan validasi username
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) { // Firebase membutuhkan minimal 6 karakter
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            isRegistrationAttempted = true;

            // --- 2. Panggil ViewModel ---
            // Saat ini, Firebase hanya menyimpan email/password.
            // Jika Anda ingin menyimpan 'username', Anda harus menambahkan logika penyimpanan Firestore/Database
            // di dalam metode register pada AuthViewModel setelah Firebase Auth berhasil.
            authViewModel.register(username, email, password);
        });

        binding.btnLogin.setOnClickListener(v -> finish());
    }
}