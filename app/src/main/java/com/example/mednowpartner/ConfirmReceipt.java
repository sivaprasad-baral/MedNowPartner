package com.example.mednowpartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mednowpartner.model.Medicine;
import com.example.mednowpartner.model.Partner;
import com.example.mednowpartner.model.User;

import java.util.List;

public class ConfirmReceipt extends AppCompatActivity {

    List<Medicine> medicines;
    String orderId;
    User customer;
    Partner partner;

    TextView textViewCustomerName,textViewCustomerAddress,textViewPharmacyName,textViewPharmacyAddress,textViewOrderDateTime,textViewCost;
    RecyclerView recyclerView;

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


    }

    public void sendReceiptBtn(View view) {
    }
}
