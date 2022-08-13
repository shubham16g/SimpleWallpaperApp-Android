package com.cleanwallpaper.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanwallpaper.app.adapters.CategoryAdapter;
import com.cleanwallpaper.app.models.CategoryPOJO;
import com.cleanwallpaper.app.R;
import com.cleanwallpaper.app.utils.SQLHelper;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {


    public CategoryFragment() {
        // Required empty public constructor
    }

    private View view;
    private ArrayList<CategoryPOJO> list;
    private CategoryAdapter adapter;
    private SQLHelper sqlHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reycler, container, false);
        init();
        return view;
    }

    private void init() {
        sqlHelper = new SQLHelper(getContext());
        RecyclerView wallsRecycler = view.findViewById(R.id.recyclerView);
        wallsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        list = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), list);
        wallsRecycler.setAdapter(adapter);
    }

    public void setFragment(String query){
        list.clear();
        list.addAll(sqlHelper.getCategories(query));
        adapter.notifyDataSetChanged();
    }

    public void focus(){
        adapter.notifyDataSetChanged();
    }

}