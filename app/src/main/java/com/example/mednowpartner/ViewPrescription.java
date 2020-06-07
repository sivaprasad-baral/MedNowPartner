package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mednowpartner.model.Order;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ViewPrescription extends AppCompatActivity {

    String orderId;

    BigImageView bigImageViewPrescription;

    DatabaseReference databaseReferenceOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(ViewPrescription.this));
        setContentView(R.layout.activity_view_prescription);

        bigImageViewPrescription = findViewById(R.id.view_prescription_image_view_prescription);

        orderId = getIntent().getStringExtra("orderId");
        databaseReferenceOrders = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        databaseReferenceOrders.child("orderStatus").setValue(Order.ORDER_PROCESSING);
        displayPrescription();
    }

    private void displayPrescription() {
        databaseReferenceOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                databaseReferenceOrders.removeEventListener(this);
                bigImageViewPrescription.showImage(Uri.parse(Objects.requireNonNull(order).getPrescriptionImg()));
                bigImageViewPrescription.setProgressIndicator(new ProgressPieIndicator());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewPrescription.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void rejectBtn(View view) {
        onBackPressed();
    }

    public void acceptBtn(View view) {
        startActivity(new Intent(ViewPrescription.this,MakeReceipt.class).putExtra("orderId",orderId).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        databaseReferenceOrders.child("orderStatus").setValue(Order.ORDER_REJECTED_BY_PARTNER);
        startActivity(new Intent(ViewPrescription.this,OrderList.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
