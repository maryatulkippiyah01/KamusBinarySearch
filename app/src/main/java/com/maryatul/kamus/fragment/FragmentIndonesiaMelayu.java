package com.maryatul.kamus.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maryatul.kamus.R;
import com.maryatul.kamus.adapter.KamusAdapter;
import com.maryatul.kamus.database.KamusHelper;
import com.maryatul.kamus.model.ModelKamus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentIndonesiaMelayu extends Fragment {

    @BindView(R.id.search_kata)
    SearchView searchKata;
    @BindView(R.id.rv_kata)
    RecyclerView rvKata;

    KamusAdapter adapter;
    SearchView searchView;
    KamusHelper kamusHelper;

    ArrayList<ModelKamus> listKamus = new ArrayList<>();

    public FragmentIndonesiaMelayu() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.indonesia_melayu));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_indonesia_melayu, container, false);
        ButterKnife.bind(this, v);

        int searchPlateId = searchKata.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchKata.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundColor (Color.TRANSPARENT);
            int searchTextId = searchPlate.getContext ().getResources ().getIdentifier ("android:id/search_src_text", null, null);

        }

        kamusHelper = new KamusHelper(getActivity());
        adapter = new KamusAdapter(getActivity());
        rvKata.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvKata.setHasFixedSize(true);
        rvKata.setAdapter(adapter);

        initAllData();

        searchKata.setQueryHint(getString(R.string.masukan_kata));
        searchKata.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                initDataById(newText);
                return true;
            }
        });

        return v;
    }

    private void initDataById(String query) {
        kamusHelper.open();
        listKamus = kamusHelper.selectByKataBinarySearch(String.valueOf(query), true);
        kamusHelper.close();

        adapter.replaceItem(listKamus);
    }

    private void initAllData() {
        kamusHelper.open();
        listKamus = kamusHelper.selectAll(true);
        kamusHelper.close();

        adapter.addItem(listKamus);
    }

}
