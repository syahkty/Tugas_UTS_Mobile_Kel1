package com.example.bacakomik;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment {

    private RecyclerView recyclerView;
    private KomikAdapter adapter;
    private List<Komik> komikList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        komikList = new ArrayList<>();

        String[] names = getResources().getStringArray(R.array.data_name);
        String[] descriptions = getResources().getStringArray(R.array.data_description);

        TypedArray images = getResources().obtainTypedArray(R.array.data_photo);

        for (int i = 0; i < names.length; i++) {
            komikList.add(new Komik(
                    names[i],
                    descriptions[i],
                    images.getResourceId(i, -1)
            ));
        }

        images.recycle();
        adapter = new KomikAdapter(komikList, 1);

        recyclerView.setAdapter(adapter);

        return view;
    }
}
