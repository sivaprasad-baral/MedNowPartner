package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednowpartner.adapter.ReceiptItemAdapter;
import com.example.mednowpartner.model.Medicine;
import com.example.mednowpartner.model.Order;
import com.example.mednowpartner.model.Partner;
import com.example.mednowpartner.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfirmReceipt extends AppCompatActivity {

    public static double cost = 0;
    List<Medicine> medicines;
    String orderId;
    User customer;
    Partner partner;
    Order order;
    ReceiptItemAdapter itemAdapter;

    TextView textViewCustomerName,textViewCustomerAddress,textViewPharmacyName,textViewPharmacyAddress;
    public static TextView textViewCost;
    RecyclerView recyclerView;

    DatabaseReference databaseReferenceUser,databaseReferencePartner,databaseReferenceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_receipt);

        orderId = getIntent().getStringExtra("orderId");

        textViewCustomerName = findViewById(R.id.confirm_receipt_text_view_customer_name);
        textViewCustomerAddress = findViewById(R.id.confirm_receipt_text_view_customer_address);
        textViewPharmacyName = findViewById(R.id.confirm_receipt_text_view_pharmacy_name);
        textViewPharmacyAddress = findViewById(R.id.confirm_receipt_text_view_pharmacy_address);
        textViewCost = findViewById(R.id.confirm_receipt_text_view_cost);
        recyclerView = findViewById(R.id.confirm_receipt_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ConfirmReceipt.this));
        itemAdapter = new ReceiptItemAdapter(ConfirmReceipt.this, new ArrayList<Medicine>(), orderId);
        recyclerView.setAdapter(itemAdapter);

        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        databaseReferencePartner = FirebaseDatabase.getInstance().getReference("Partners");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order = dataSnapshot.getValue(Order.class);
                databaseReferenceOrder.removeEventListener(this);
                databaseReferenceUser.child(order.getCustomerId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        customer = dataSnapshot.getValue(User.class);
                        databaseReferenceUser.child(order.getCustomerId()).removeEventListener(this);
                        databaseReferencePartner.child(order.getPartnerId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                partner = dataSnapshot.getValue(Partner.class);
                                databaseReferenceUser.child(order.getPartnerId()).removeEventListener(this);
                                cost = order.getCost();
                                displayDetails();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ConfirmReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ConfirmReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConfirmReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDetails() {
        medicines = new ArrayList<>();
        for (String key : order.getMedicines().keySet()) {
            medicines.add(order.getMedicines().get(key));
        }
        if(!medicines.isEmpty()) {
            textViewCustomerName.setText(customer.getName());
            try {
                Geocoder geocoder = new Geocoder(ConfirmReceipt.this, Locale.getDefault());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(customer.getLatitude(),customer.getLongitude(),1);
                String address = addresses.get(0).getAddressLine(0);
                textViewCustomerAddress.setText(address);
            } catch (IOException e) {
                Toast.makeText(ConfirmReceipt.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            textViewPharmacyName.setText(partner.getPharmacyName());
            try {
                Geocoder geocoder = new Geocoder(ConfirmReceipt.this, Locale.getDefault());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(partner.getLatitude(),partner.getLongitude(),1);
                String address = addresses.get(0).getAddressLine(0);
                textViewPharmacyAddress.setText(address);
            } catch (IOException e) {
                Toast.makeText(ConfirmReceipt.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            itemAdapter = new ReceiptItemAdapter(ConfirmReceipt.this, medicines, orderId);
            recyclerView.setAdapter(itemAdapter);
            String costTag = "Rs. ".concat(String.valueOf(cost));
            textViewCost.setText(costTag);
        }
    }

    public void sendReceiptBtn(View view) {
        databaseReferenceOrder.child("orderStatus").setValue(Order.ORDER_SENT_FOR_CONFIRMATION);
        Toast.makeText(ConfirmReceipt.this,"Order sent for confirmation",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ConfirmReceipt.this,OrderList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}