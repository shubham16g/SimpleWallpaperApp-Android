package com.shubhamgupta16.simplewallpaper.activities;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shubhamgupta16.simplewallpaper.R;
import com.shubhamgupta16.simplewallpaper.utils.SQLHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        switchFrag(currentFragPos, false);
        if (currentFragPos == 0)
            wallsFragment.focus();
        if (currentFragPos == 2)
            favoriteFragment.focus();
    }



    private void searchFilter(String query) {
        Log.d("tagtag", "search");
        switch (currentFragPos) {
            case 0:
                wallsFragment.setFragment(SQLHelper.TYPE_QUERY, query);
                break;
            case 1:
                categoryFragment.setFragment(query);
                break;
            case 2:
                favoriteFragment.setFragment(SQLHelper.TYPE_FAVORITE_QUERY, query);
                break;
        }
    }

    private void closeSearch() {
        Log.d("tagtag", "closeSearch");
        switch (currentFragPos) {
            case 0:
                wallsFragment.setFragment(SQLHelper.TYPE_NONE, "");
                break;
            case 1:
                categoryFragment.setFragment(null);
                break;
            case 2:
                favoriteFragment.setFragment(SQLHelper.TYPE_FAVORITE, "");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

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
            case 2:
                searchView.setQueryHint("Search Favorite...");
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
        favoriteFragment.setFragment(SQLHelper.TYPE_FAVORITE, null);
        wallsFragment.setFragment(SQLHelper.TYPE_NONE, null);
        categoryFragment.setFragment(null);
        switchFrag(0, false);

        BottomNavigationView bNav = findViewById(R.id.bottomNav);
        bNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_walls)
                switchFrag(0, true);
            else if (id == R.id.action_category)
                switchFrag(1, true);
            else if (id == R.id.action_favorite)
                switchFrag(2, true);
            return true;
        });
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