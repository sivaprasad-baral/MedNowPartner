package com.example.mednowpartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class OrderList extends AppCompatActivity {

    public static TextView textViewHeader;
    BottomNavigationView bottomNavigationView;

    NavController navController;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        setSupportActionBar((Toolbar) findViewById(R.id.order_list_toolbar));
        textViewHeader = findViewById(R.id.order_list_text_view_header);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bottomNavigationView = findViewById(R.id.order_list_bottom_nav_view);
        navController = Navigation.findNavController(OrderList.this,R.id.order_list_nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.newOrderFragment,R.id.confirmedOrderFragment,R.id.allOrderFragment).build();
        NavigationUI.setupActionBarWithNavController(OrderList.this,navController,appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,appBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderList.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
