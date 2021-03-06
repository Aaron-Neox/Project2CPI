package com.example.appname.View.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.appname.R;
import com.example.appname.View.folders.FoldersFragment;
import com.example.appname.View.home.HomeFragment;
import com.example.appname.View.settings.SettingsFragment;
import com.example.appname.View.sort.SortFragment;
import com.example.appname.ViewModel.ImageViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    //==============================================================================================
    //  ATTRIBUTES
    //==============================================================================================

    private Toast mToast = null;
    private BackPressedListener mBackPressedListener;
    private BottomNavigationView mBottomNavBar;
    private ImageViewModel mImageViewModel;

    //==============================================================================================
    //  STATE FUNCTIONS
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //request for permissions
        if (!canAccessExternalSd()){
            Intent askForPerms = new Intent(MainActivity.this, RequestForPermissionActivity.class);
            startActivity(askForPerms);
        }

        setContentView(R.layout.activity_main);
        initNavBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUnsortedImages();
    }

    //==============================================================================================
    //  INIT FUNCTIONS
    //==============================================================================================


    private void initNavBar() {
        mBottomNavBar = findViewById(R.id.bottomNavBar);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();
        mBottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_folders:
                        selectedFragment = new FoldersFragment();
                        break;
                    case R.id.nav_sort:
                        selectedFragment = SortFragment.newInstance(mImageViewModel.getUnsortedImages());
                        break;
                    case R.id.nav_settings:
                        selectedFragment = new SettingsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                        selectedFragment).commit();
                return true;
            }
        });
    }

    private void loadUnsortedImages() {
        mImageViewModel = ViewModelProviders.of(this).get(ImageViewModel.class);
        mImageViewModel.startLoading();
    }
    //==============================================================================================
    //  FUNCTIONS
    //==============================================================================================

    private void showToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    //==============================================================================================
    //  LISTENERS FUNCTIONS
    //==============================================================================================


    @Override
    public void onBackPressed() {
        int currentItemId = mBottomNavBar.getSelectedItemId();
        switch (currentItemId) {
            case R.id.nav_home:
                showToast("Backed in home page!");
                break;
            case R.id.nav_folders:
                mBackPressedListener.onBackPressed();
                break;
            case R.id.nav_sort:
                mBackPressedListener.onBackPressed();
                break;
            case R.id.nav_settings:
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    public void setBackListener(BackPressedListener listener){
        mBackPressedListener = listener;
    }

    public interface BackPressedListener{
        void onBackPressed();
    }

    //==============================================================================================
    //  FOR PERMISSION
    //==============================================================================================

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }

}
