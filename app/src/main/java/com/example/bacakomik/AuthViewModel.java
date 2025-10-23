package com.example.bacakomik;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    private final DatabaseHelper databaseHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
    }

    public LiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }

    public LiveData<Boolean> getRegistrationStatus() {
        return registrationStatus;
    }

    public void login(String email, String password) {
        executor.execute(() -> {
            boolean isLoggedIn = databaseHelper.checkUserByEmail(email, password);
            loginStatus.postValue(isLoggedIn);
        });
    }

    public void register(String username, String email, String password) {
        executor.execute(() -> {
            boolean isRegistered = databaseHelper.addUser(username, email, password);
            registrationStatus.postValue(isRegistered);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
