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

public class PopularListFragment extends Fragment {

    private RecyclerView rvPopularComics;
    private PopularComicAdapter popularAdapter;
    private List<Komik> komikList;
    private String type;

    private static final String ARG_TYPE = "arg_type";

    public static PopularListFragment newInstance(String type) {
        PopularListFragment fragment = new PopularListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPopularComics = view.findViewById(R.id.rvPopularComics);
        rvPopularComics.setLayoutManager(new LinearLayoutManager(getContext()));

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

        popularAdapter = new PopularComicAdapter(komikList);
        rvPopularComics.setAdapter(popularAdapter);

        loadData(type);
    }

    private void loadData(String type) {
    }
}