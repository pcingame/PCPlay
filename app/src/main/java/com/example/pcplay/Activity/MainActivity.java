package com.example.pcplay.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pcplay.Activity.LogActivity.SignInActivity;
import com.example.pcplay.Fragment.BottomNavigation.FavoriteFragment;
import com.example.pcplay.Fragment.BottomNavigation.HistoryFragment;
import com.example.pcplay.Fragment.BottomNavigation.HomeFragment;
import com.example.pcplay.Fragment.MyProfileFragment;
import com.example.pcplay.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_FAVORITE = 1;
    public static final int FRAGMENT_HISTORY = 2;
    public static final int FRAGMENT_MY_PROFILE = 3;
    private int mCurrentFragment = FRAGMENT_HOME;
    public static final int MY_REQUEST_CODE = 111;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView mBottomNavigationView;
    private ImageView imgAvatar;
    private TextView tvName, tvEmail;
   // private LinearLayout layoutBottomSheet;
    //private BottomSheetBehavior bottomSheetBehavior;

    final public MyProfileFragment mMyProfileFragment = new MyProfileFragment();
    final public ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();
                        if(intent == null){
                            return;
                        }
                        Uri uri = intent.getData();
                        mMyProfileFragment.setmUri(uri);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            mMyProfileFragment.setBitmapImageView(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //navigationview
        navigationView.setNavigationItemSelectedListener(this);
        replaceFragment(new HomeFragment());
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        //Bottom
        mBottomNavigationView.getMenu().findItem(R.id.bottom_home).setChecked(true);
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.bottom_home){
                    openHomeFragment();
                    navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                }else if(id == R.id.bottom_favorite){
                    openFavoriteFragment();
                    navigationView.getMenu().findItem(R.id.nav_favorite).setChecked(true);
                }else if(id == R.id.bottom_history){
                    openHistoryFragment();
                    navigationView.getMenu().findItem(R.id.nav_history).setChecked(true);
                }
                return true;
            }
        });

        showUserInformation();
    }

    private void initUI() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
       // layoutBottomSheet = findViewById(R.id.layout_bottom_sheet);
       // bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            openHomeFragment();
            mBottomNavigationView.getMenu().findItem(R.id.bottom_home).setChecked(true);
        }else if(id == R.id.nav_favorite){
            openFavoriteFragment();
            mBottomNavigationView.getMenu().findItem(R.id.bottom_favorite).setChecked(true);
        }else if(id == R.id.nav_my_profile){
            openMyProfileFragment();
        }else if(id == R.id.nav_history){
            openHistoryFragment();
            mBottomNavigationView.getMenu().findItem(R.id.bottom_history).setChecked(true);
        }else if(id == R.id.nav_change_password){
            // startActivity(new Intent(MainActivity.this, RealtimeDbActivity.class));

        }else if(id == R.id.nav_sign_out){
           /* if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }*/
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    private void openHomeFragment(){
        if(mCurrentFragment != FRAGMENT_HOME){
            replaceFragment(new HomeFragment());
            mCurrentFragment = FRAGMENT_HOME;
        }
    }

    private void openFavoriteFragment(){
        if(mCurrentFragment != FRAGMENT_FAVORITE){
            replaceFragment(new FavoriteFragment());
            mCurrentFragment = FRAGMENT_FAVORITE;
        }
    }

    private void openHistoryFragment(){
        if(mCurrentFragment != FRAGMENT_HISTORY){
            replaceFragment(new HistoryFragment());
            mCurrentFragment = FRAGMENT_HISTORY;
        }
    }

    private void openMyProfileFragment(){
        if(mCurrentFragment != FRAGMENT_MY_PROFILE){
            replaceFragment(mMyProfileFragment);
            mCurrentFragment = FRAGMENT_MY_PROFILE;
        }
    }

    public void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        else {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            if(name == null){
                tvName.setVisibility(View.GONE);
            }else {
                tvName.setVisibility(View.VISIBLE);
                tvName.setText(name);
            }
            tvEmail.setText(email);
            Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(imgAvatar);
        }
    }


    //request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
            else {
                //vui lòng cho phép truy cập ảnh
            }
        }
    }


    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh từ thư viện"));
    }
}