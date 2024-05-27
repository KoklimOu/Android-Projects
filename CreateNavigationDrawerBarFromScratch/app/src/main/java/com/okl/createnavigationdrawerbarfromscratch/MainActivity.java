package com.okl.createnavigationdrawerbarfromscratch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;

    List<String> policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        policy = new ArrayList<>();
        policy.add("camera");
        policy.add("screenshot");
        policy.add("record");
        policy.add("SD Card");
        policy.add("airplane mode");
        policy.add("roaming");
        policy.add("GPS");
        policy.add("microphone");
        policy.add("communication");

        Log.e(TAG, "onCreate: "+policy);

        String[] policies = new String[]{
                "camera",
                "screenshot",
                "record",
                "SD Card",
                "airplane mode",
                "roaming",
                "GPS",
                "microphone",
                "communication"
        };

//        ABoutWorkAppFragment aBoutWorkAppFragment = new ABoutWorkAppFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
//                .beginTransaction();
//        Bundle bundle = new Bundle();
//        bundle.putString("policy", Arrays.toString(policies));
//        fragmentTransaction.replace(R.id.aboutWorkAppFragment, aBoutWorkAppFragment).commit();

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.imageMenu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.navigationView);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.aboutWorkAppFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*Change title at navbar to each on the fragment title when click on it*/
        final TextView textTitle = findViewById(R.id.txtViewTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                textTitle.setText(navDestination.getLabel());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public String[] getPolicy(){
        String[] policies = new String[]{
                "camera",
                "screenshot",
                "record",
                "SD Card",
                "airplane mode",
                "roaming",
                "GPS"
        };
        return policies;
    }

}