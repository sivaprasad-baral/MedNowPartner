package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednowpartner.model.Medicine;
import com.example.mednowpartner.model.Order;
import com.example.mednowpartner.model.Partner;
import com.example.mednowpartner.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ConfirmReceipt extends AppCompatActivity {

    List<Medicine> medicines;
    String orderId;
    User customer;
    Partner partner;
    Order order;

    TextView textViewCustomerName,textViewCustomerAddress,textViewPharmacyName,textViewPharmacyAddress,textViewOrderDateTime,textViewCost;
    RecyclerView recyclerView;

    DatabaseReference databaseReferenceUser,databaseReferencePartner,databaseReferenceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_receipt);

        textViewCustomerName = findViewById(R.id.confirm_receipt_text_view_customer_name);
        textViewCustomerAddress = findViewById(R.id.confirm_receipt_text_view_customer_address);
        textViewPharmacyName = findViewById(R.id.confirm_receipt_text_view_pharmacy_name);
        textViewPharmacyAddress = findViewById(R.id.confirm_receipt_text_view_pharmacy_address);
        textViewOrderDateTime = findViewById(R.id.confirm_receipt_text_view_order_date_time);
        textViewCost = findViewById(R.id.confirm_receipt_text_view_cost);

        orderId = getIntent().getStringExtra("orderId");
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        databaseReferencePartner = FirebaseDatabase.getInstance().getReference("Partners");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order = dataSnapshot.getValue(Order.class);
                databaseReferenceOrder.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConfirmReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        databaseReferenceUser.child(order.getCustomerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customer = dataSnapshot.getValue(User.class);
                databaseReferenceUser.child(order.getCustomerId()).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConfirmReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        databaseReferencePartner.child(order.getPartnerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                partner = dataSnapshot.getValue(Partner.class);
                databaseReferenceUser.child(order.getPartnerId()).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConfirmReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendReceiptBtn(View view) {
    }
}
