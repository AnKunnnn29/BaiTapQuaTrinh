package com.example.doan.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.doan.Fragments.AccountFragment;
import com.example.doan.Fragments.HomeFragment;
import com.example.doan.Fragments.OrderFragment;
import com.example.doan.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private int selectedItemId = R.id.homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        LinearLayout supportBtn = findViewById(R.id.supportBtn);
        LinearLayout settingsBtn = findViewById(R.id.settingsBtn);

        homeBtn.setOnClickListener(this);
        profileBtn.setOnClickListener(this);
        cartBtn.setOnClickListener(this);
        supportBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);

        // Load the default fragment (HomeFragment)
        if (savedInstanceState == null) {
            // Không cần animate lần đầu
            loadFragment(new HomeFragment(), false);
        } else {
            // Lấy lại ID đang chọn khi Activity được khôi phục (xoay màn hình, v.v.)
            selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.homeBtn);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu lại trạng thái của mục đã chọn
        outState.putInt("selectedItemId", selectedItemId);
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == selectedItemId) {
            return; // Do nothing if the same item is clicked
        }

        Fragment fragment = null;

        if (itemId == R.id.homeBtn) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.profileBtn) {
            fragment = new AccountFragment();
        } else if (itemId == R.id.cartBtn) {
            fragment = new OrderFragment();
        } else if (itemId == R.id.supportBtn) {
            // For now, support button loads Home.
            // You can create a SupportFragment and load it here.
            fragment = new HomeFragment();
        } else if (itemId == R.id.settingsBtn) {
            // For now, settings button loads Home.
            // You can create a SettingsFragment and load it here.
            fragment = new HomeFragment();
        }
        
        if (fragment != null) {
            loadFragment(fragment, true);
            selectedItemId = itemId;
        }
    }

    private static final int CONTENT_CONTAINER_ID = R.id.content_container;

    private void loadFragment(Fragment fragment, boolean animate) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (animate) {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        }

        fragmentTransaction.replace(CONTENT_CONTAINER_ID, fragment);
        fragmentTransaction.commit();
    }
}