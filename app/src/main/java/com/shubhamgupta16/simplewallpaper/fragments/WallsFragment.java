package com.shubhamgupta16.simplewallpaper.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
import com.shubhamgupta16.simplewallpaper.adapters.WallsAdapter;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;

public class WallsFragment extends Fragment {


    public WallsFragment() {
        // Required empty public constructor
    }

    private View view;
    private ArrayList<WallsPOJO> list;
    private WallsAdapter adapter;
    private SQLHelper sqlHelper;
    private boolean isScrollLoad = false;
    private int maxPage = 0, lastFetch = 0;
    private LinearLayout errorLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reycler, container, false);
        init();
        return view;
    }

    private void init() {
        Log.d("tagtag", "init");
        sqlHelper = new SQLHelper(getContext());
        list = new ArrayList<>();
        errorLayout = view.findViewById(R.id.errorLayout);
        RecyclerView wallsRecycler = view.findViewById(R.id.recyclerView);
        final StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
//        final GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        wallsRecycler.setLayoutManager(manager);
        adapter = new WallsAdapter(getContext(), list, SQLHelper.TYPE_NONE);
        adapter.setOnRemoveFromFavSecotion(this::handleErrorLayout);
        wallsRecycler.setAdapter(adapter);
        wallsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentItems = manager.getChildCount();
                int totalItems = manager.getItemCount();
                int[] firstVisibleItemPositions = new int[list.size()];
                int scrollOutItems;
                if (list.size() >= 2) {
                    scrollOutItems = manager.findFirstVisibleItemPositions(firstVisibleItemPositions)[0];
                } else {
                    scrollOutItems = 0;
                }

                if (isScrollLoad && (currentItems + scrollOutItems == totalItems)) {
                    maxPage = sqlHelper.getPagesCount(type, extras);
                    if (lastFetch < maxPage) {
                        isScrollLoad = false;
                        fetchWalls(lastFetch + 1);
                        Log.d("tagtag", "scroll to bottom " + type);
                    }
                }
            }
        });
    }

    private int type = SQLHelper.TYPE_NONE;
    private String extras;

    @SuppressLint("NotifyDataSetChanged")
    public void setFragment(int type, String extras) {
        Log.d("tagtag", "set" + type);
        this.type = type;
        this.extras = extras;
        lastFetch = 0;
        maxPage = 0;
        list.clear();
        adapter.setType(type);
        adapter.notifyDataSetChanged();
        maxPage = sqlHelper.getPagesCount(type, extras);
        errorLayout.setVisibility(View.GONE);
        setErrorLayout();
        fetchWalls(1);
    }

    private void setErrorLayout() {
        ImageView errorImage = errorLayout.findViewById(R.id.errorImage);
        TextView errorTitle = errorLayout.findViewById(R.id.errorTitle);
        if (type == SQLHelper.TYPE_FAVORITE || type == SQLHelper.TYPE_FAVORITE_QUERY) {
            errorImage.setImageResource(R.drawable.no_fav);
            errorTitle.setText(R.string.no_fav);
        } else {
            errorImage.setImageResource(R.drawable.no_res);
            errorTitle.setText(R.string.no_res);
        }
    }

    private void handleErrorLayout() {
        if (list.isEmpty()) {
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    private void fetchWalls(final int page) {
        Log.d("tagtag", "fetch page: " + page + "type" + type);

        if (page == 1) {
            handleRes(page, sqlHelper.getWallpapers(page, type, extras));
        } else {
            new Handler().postDelayed(() -> handleRes(page, sqlHelper.getWallpapers(page, type, extras)), 1000);
        }

    }

    private void handleRes(int page, ArrayList<WallsPOJO> walls) {
        if (page != 1) {
            if (list.size() >= 1) {
                list.remove(list.size() - 1);
                adapter.notifyItemRemoved(list.size());
            }

            if (list.size() >= 1) {
                list.remove(list.size() - 1);
                adapter.notifyItemRemoved(list.size());
            }
        }
        int from = list.size();
        list.addAll(walls);
        if (page >= 1 && page != maxPage && !list.isEmpty()) {
//            Toast.makeText(getContext(), maxPage + "", Toast.LENGTH_SHORT).show();
            list.add(new WallsPOJO(0, null, null, "ad", null, false, false));
            list.add(new WallsPOJO(0, null, null, null, null, false, false));
            list.add(new WallsPOJO(0, null, null, null, null, false, false));
        }

        adapter.notifyItemRangeInserted(from, list.size());

        lastFetch = page;
        isScrollLoad = true;

        handleErrorLayout();
    }

    public void focus() {
        Log.d("tagtag", "focus, " + list.size() + " " + type);
        maxPage = sqlHelper.getPagesCount(type, extras);
        if (type == SQLHelper.TYPE_FAVORITE) {
            list.clear();
            fetchWalls(1);
        } else
            adapter.notifyDataSetChanged();
    }

}