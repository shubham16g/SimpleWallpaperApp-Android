package com.shubhamgupta16.simplewallpaper.activities;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.data_source.DataService;
import com.shubhamgupta16.simplewallpaper.utils.Utils;
import com.shubhamgupta16.simplewallpaper.fragments.CategoryFragment;
import com.shubhamgupta16.simplewallpaper.fragments.WallsFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager;
    private WallsFragment wallsFragment;
    private CategoryFragment categoryFragment;
    private WallsFragment favoriteFragment;
    private int currentFragPos = 0;
    private MenuItem searchItem;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initAd();
        init();
    }

    private void initAd() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        switchFrag(currentFragPos, false);
        Log.d("TAG", "onStart: called");
        if (currentFragPos == 0)
            wallsFragment.focus();
        if (currentFragPos == 2)
            favoriteFragment.focus();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    private void searchFilter(String query) {
        switch (currentFragPos) {
            case 0:
                Log.d("TAG", "searchFilter: " + query);
                wallsFragment.setFragment(DataService.QueryType.SEARCH, query);
                break;
            case 1:
                categoryFragment.setFragment(query);
                break;
        }
    }

    private void closeSearch() {
        switch (currentFragPos) {
            case 0:
                wallsFragment.setFragment(DataService.QueryType.NONE, "");
                break;
            case 1:
                categoryFragment.setFragment(null);
                break;
            case 2:
                favoriteFragment.setFragment(DataService.QueryType.FAVORITE, "");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentFragPos == 2) {
            getMenuInflater().inflate(R.menu.main_menu_fav, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        });

        menu.findItem(R.id.action_policy).setOnMenuItemClickListener(menuItem -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.privicy_policy)));
            startActivity(i);
            return true;
        });

        if (currentFragPos == 2) {
            return super.onCreateOptionsMenu(menu);
        }

        searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem menuItem) {
                closeSearch();
                return true;
            }
        });
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView == null) {
            return super.onCreateOptionsMenu(menu);
        }
        searchView.setIconifiedByDefault(false);
        switch (currentFragPos){
            case 0:
                searchView.setQueryHint("Search Wallpaper...");
            break;
            case 1:
                searchView.setQueryHint("Search Collection...");
                break;
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Utils.hideKeyboard(MainActivity.this);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchFilter(s);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        manager = getSupportFragmentManager();
        wallsFragment = (WallsFragment) manager.findFragmentById(R.id.wallsFragment);
        categoryFragment = (CategoryFragment) manager.findFragmentById(R.id.categoryFragment);
        favoriteFragment = (WallsFragment) manager.findFragmentById(R.id.favoriteFragment);
        assert favoriteFragment != null;
        favoriteFragment.setFragment(DataService.QueryType.FAVORITE, null);
        wallsFragment.setFragment(DataService.QueryType.NONE, null);
        categoryFragment.setFragment(null);

        BottomNavigationView bNav = findViewById(R.id.bottomNav);
        bNav.setOnItemSelectedListener(item -> {
            switchFrag(getPositionById(item.getItemId()), true);
            return true;
        });

        bNav.setSelectedItemId(R.id.action_walls);
        switchFrag(0, false);
    }

    private int getPositionById(@IdRes int id){
        if (id == R.id.action_walls) return 0;
        else if (id == R.id.action_category) return 1;
        else if (id == R.id.action_favorite) return 2;
        else return 0;
    }

    private void switchFrag(int position, boolean animate) {
        FragmentTransaction transaction = manager.beginTransaction();
        invalidateOptionsMenu();
        if (searchItem != null) {
            if (searchItem.isActionViewExpanded()){
                Log.d("tagtag", "expanded");
                closeSearch();
                searchItem.collapseActionView();
            }
        }
        currentFragPos = position;
        switch (position) {
            case 0:
                transaction.show(wallsFragment);
                transaction.hide(categoryFragment);
                transaction.hide(favoriteFragment);
                wallsFragment.focus();
                break;
            case 1:
                transaction.hide(wallsFragment);
                transaction.show(categoryFragment);
                transaction.hide(favoriteFragment);
                categoryFragment.focus();
                break;
            case 2:
                transaction.hide(wallsFragment);
                transaction.hide(categoryFragment);
                transaction.show(favoriteFragment);
                favoriteFragment.focus();
                break;
        }
        if (animate)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
}