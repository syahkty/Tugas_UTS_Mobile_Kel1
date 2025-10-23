package com.example.bacakomik;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);


        ImageView imgMenu = view.findViewById(R.id.imgMenu);

        imgMenu.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_account, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit_profile) {
                    Toast.makeText(getContext(), "Edit Profil diklik", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.action_settings) {
                    Toast.makeText(getContext(), "Pengaturan diklik", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.action_logout) {
                    logout();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        applyUserInfo(view);
        return view;
    }

    private void logout() {
        Intent i = new Intent(requireContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

        requireActivity().finish();
    }

    private void applyUserInfo(@NonNull View root) {
        TextView tvUsername = root.findViewById(R.id.tvUsername);
        TextView tvEmail    = root.findViewById(R.id.tvEmail);

        if (getActivity() == null) return;
        Intent intent = getActivity().getIntent();

        String email    = intent != null ? intent.getStringExtra("EMAIL")    : null;
        String username = intent != null ? intent.getStringExtra("USERNAME") : null;

        if (username != null && !username.isEmpty()) tvUsername.setText(username);
        if (email != null && !email.isEmpty())       tvEmail.setText(email);
    }
}
