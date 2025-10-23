package com.example.bacakomik;

import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private KomikAdapter adapter;
    private List<Komik> komikList;

    private TabLayout tabLayoutPopular;
    private ViewPager2 viewPagerPopular;
    private PopularTabsAdapter popularTabsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayoutPopular = view.findViewById(R.id.tabLayoutPopular);
        viewPagerPopular = view.findViewById(R.id.viewPagerPopular);

        popularTabsAdapter = new PopularTabsAdapter(getChildFragmentManager(), getLifecycle());
        viewPagerPopular.setAdapter(popularTabsAdapter);

        new TabLayoutMediator(tabLayoutPopular, viewPagerPopular,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Weekly");
                            break;
                        case 1:
                            tab.setText("Monthly");
                            break;
                        case 2:
                            tab.setText("All");
                            break;
                    }
                }
        ).attach();

        recyclerView = view.findViewById(R.id.recyclerGrid);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

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

        KomikAdapter adapter = new KomikAdapter(komikList, 2);
        recyclerView.setAdapter(adapter);
    }
}