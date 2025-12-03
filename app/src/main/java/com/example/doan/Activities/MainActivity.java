package com.example.doan.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.doan.Fragments.AccountFragment;
import com.example.doan.Fragments.HomeFragment;
import com.example.doan.Fragments.OrderFragment;
import com.example.doan.R;
import com.example.doan.Fragments.StoreFragment;
import com.example.doan.Utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private final FragmentManager fm = getSupportFragmentManager();
    private final HomeFragment homeFragment = new HomeFragment();
    private final OrderFragment orderFragment = new OrderFragment();
    private final StoreFragment storeFragment = new StoreFragment();
    private final AccountFragment accountFragment = new AccountFragment();
    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView userNameTextView = findViewById(R.id.user_name);
        SessionManager sessionManager = new SessionManager(this);
        String fullName = sessionManager.getFullName();

        if(sessionManager.isLoggedIn() && fullName != null && !fullName.isEmpty()){
            userNameTextView.setText("Hi, " + fullName);
        } else {
            userNameTextView.setText("Hi, Guest");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Add all fragments and hide them initially, show the home fragment
        fm.beginTransaction().add(R.id.content_container, accountFragment, "4").hide(accountFragment).commit();
        fm.beginTransaction().add(R.id.content_container, storeFragment, "3").hide(storeFragment).commit();
        fm.beginTransaction().add(R.id.content_container, orderFragment, "2").hide(orderFragment).commit();
        fm.beginTransaction().add(R.id.content_container, homeFragment, "1").commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
            activeFragment = homeFragment;
            return true;
        } else if (itemId == R.id.nav_order) {
            fm.beginTransaction().hide(activeFragment).show(orderFragment).commit();
            activeFragment = orderFragment;
            return true;
        } else if (itemId == R.id.nav_store) {
            fm.beginTransaction().hide(activeFragment).show(storeFragment).commit();
            activeFragment = storeFragment;
            return true;
        } else if (itemId == R.id.nav_account) {
            fm.beginTransaction().hide(activeFragment).show(accountFragment).commit();
            activeFragment = accountFragment;
            return true;
        }
        return false;
    }
}