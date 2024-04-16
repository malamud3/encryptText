package com.calssy.encrypttext;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddTextFragment())
                    .commit();
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            menuItem -> {
                Fragment selectedFragment = null;

                if (menuItem.getItemId() == R.id.action_add_text) {
                    selectedFragment = new AddTextFragment();
                    Log.d(TAG, "Add Text Fragment selected");
                } else if (menuItem.getItemId() == R.id.action_get_text) {
                    selectedFragment = new GetTextFragment();
                    Log.d(TAG, "Get Text Fragment selected");
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                } else {
                    return false;
                }
            };
}
