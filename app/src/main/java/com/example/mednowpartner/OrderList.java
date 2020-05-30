package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednowpartner.adapter.OrderListAdapter;
import com.example.mednowpartner.model.Prescription;
import com.example.mednowpartner.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class OrderList extends AppCompatActivity implements OrderListAdapter.OrderListClickListener {

    ArrayList<User> users;
    ArrayList<Prescription> orders;

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView textViewHeader;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReferenceCustomers,databaseReferencePrescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        toolbar = findViewById(R.id.order_list_toolbar);

        recyclerView = findViewById(R.id.order_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderList.this));
        textViewHeader = findViewById(R.id.order_list_text_view_header);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView.setAdapter(new OrderListAdapter(OrderList.this,new ArrayList<User>(),OrderList.this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceCustomers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferencePrescriptions = FirebaseDatabase.getInstance().getReference().child("Prescriptions");

        orders = new ArrayList<>();
        databaseReferencePrescriptions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Prescription prescription = snapshot.getValue(Prescription.class);
                    if(Objects.requireNonNull(prescription).getPartnerId().equals(firebaseUser.getUid())) {
                        orders.add(prescription);
                    }
                }
                if(orders.isEmpty()) {
                    String orderHeader = "No current orders";
                    textViewHeader.setText(orderHeader);
                } else {
                    displayOrdersList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String orderHeader = "Failed to get orders";
                textViewHeader.setText(orderHeader);
                Toast.makeText(OrderList.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrdersList() {
        users = new ArrayList<>();
        databaseReferenceCustomers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(Prescription order : orders) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(order.getUserId().equals(snapshot.getKey())) {
                            User user = snapshot.getValue(User.class);
                            users.add(user);
                        }
                    }
                }
                String orderHeader = "Active orders";
                textViewHeader.setText(orderHeader);
                recyclerView.setAdapter(new OrderListAdapter(OrderList.this,users,OrderList.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String orderHeader = "Failed to get orders";
                textViewHeader.setText(orderHeader);
                Toast.makeText(OrderList.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(int position) {
        startActivity(new Intent(OrderList.this,ViewPrescription.class).putExtra("prescriptionId",orders.get(position).getPrescriptionId()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderList.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
