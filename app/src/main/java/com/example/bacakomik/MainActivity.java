package com.example.bacakomik;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bacakomik.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AuthViewModel authViewModel;
    private boolean isLoginAttempted = false;

    private static final String TAG = "AuthDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getLoginStatus().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();

                String email = binding.email.getText().toString().trim();
                DatabaseHelper dbh = new DatabaseHelper(this);
                String displayName = dbh.getUsernameByEmail(email);

                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.putExtra("EMAIL", email);
                intent.putExtra("USERNAME", displayName);
                startActivity(intent);
                finish();
            } else if (isLoginAttempted) {
                Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show();
            }
        });

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
            authViewModel.login(email, password); //
        });

        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }
}
