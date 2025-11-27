package com.example.bacakomik;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable; // Tambahkan import ini
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bacakomik.databinding.ActivityMainBinding;

// Import Google Sign-In and Firebase classes
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AuthViewModel authViewModel;
    private boolean isLoginAttempted = false;

    private static final String TAG = "AuthDebug";

    // VARIABEL BARU UNTUK GOOGLE SIGN-IN
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 9001; // Request Code unik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // --- 1. KONFIGURASI GOOGLE SIGN-IN ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // Wajib menggunakan string default_web_client_id dari google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // --- AKHIR KONFIGURASI GOOGLE SIGN-IN ---

        // --- 2. OBSERVER UNTUK STATUS LOGIN (EMAIL/PASSWORD & GOOGLE) ---
        authViewModel.getLoginStatus().observe(this, isSuccess -> {
            if (isSuccess) {
                // Login berhasil, kita ambil user saat ini dari Firebase
                FirebaseUser user = authViewModel.getCurrentUser().getValue();
                if (user != null) {
                    Toast.makeText(this, "Login Berhasil: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    // Mengambil data dari objek user Firebase
                    String email = user.getEmail();
                    // Firebase Auth hanya menyimpan display name/username jika diatur saat registrasi
                    // atau jika login menggunakan Google/provider lain.
                    String displayName = user.getDisplayName() != null ? user.getDisplayName() : "Pengguna";

                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("USERNAME", displayName);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Login berhasil, namun data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }

            } else if (isLoginAttempted) {
                Toast.makeText(this, "Otentikasi Gagal. Email atau Password salah.", Toast.LENGTH_SHORT).show();
                isLoginAttempted = false; // Reset status
            }
        });
        // --- AKHIR OBSERVER ---

        // --- 3. LISTENER UNTUK LOGIN EMAIL/PASSWORD (TETAP SAMA) ---
        binding.btnLogin.setOnClickListener((View v) -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            isLoginAttempted = true;
            authViewModel.login(email, password);
        });
        // --- AKHIR LISTENER LOGIN EMAIL/PASSWORD ---

        // --- 4. LISTENER BARU UNTUK GOOGLE SIGN-IN ---
        // Asumsi: Anda memiliki tombol dengan ID 'btnGoogleSignIn' di layout Anda.
        binding.btnGoogleSignIn.setOnClickListener(v -> {
            signInWithGoogle();
        });
        // --- AKHIR LISTENER GOOGLE SIGN-IN ---

        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    // --- 5. METODE BARU UNTUK MEMULAI PROSES GOOGLE SIGN-IN ---
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // --- 6. TANGANI HASIL DARI GOOGLE SIGN-IN INTENT ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Hasil dikembalikan dari peluncuran Intent dari Google Sign In
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In berhasil. Dapatkan ID Token
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getIdToken());

                // Kirim ID Token ke ViewModel untuk proses autentikasi Firebase
                isLoginAttempted = true; // Set flag agar observer tahu ini upaya login
                authViewModel.signInWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In gagal
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign In Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                isLoginAttempted = false;
            }
        }
    }
    // --- AKHIR TANGANI HASIL ---
}