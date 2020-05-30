package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednowpartner.adapter.InventoryAdapter;
import com.example.mednowpartner.model.Medicine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Inventory extends AppCompatActivity {

    ArrayList<Medicine> medicines;
    InventoryAdapter inventoryAdapter;

    EditText editTextSearch;
    public static TextView textViewMsg;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        editTextSearch = findViewById(R.id.inventory_edit_text_search);
        textViewMsg = findViewById(R.id.inventory_text_view_msg);
        recyclerView = findViewById(R.id.inventory_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Inventory.this));
        inventoryAdapter = new InventoryAdapter(Inventory.this,new ArrayList<Medicine>());
        recyclerView.setAdapter(inventoryAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Inventory").child(firebaseUser.getUid());

        displayMedicines();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inventoryAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void displayMedicines() {
        medicines = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicines.clear();
                databaseReference.removeEventListener(this);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    medicines.add(snapshot.getValue(Medicine.class));
                }
                String msg;
                if(medicines.isEmpty()) {
                    msg = "No items currently in Inventory";
                } else {
                    msg = "Items currently in Inventory";
                }
                textViewMsg.setText(msg);
                inventoryAdapter = new InventoryAdapter(Inventory.this,medicines);
                recyclerView.setAdapter(inventoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Inventory.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addMedBtn(View view) {
        startActivity(new Intent(Inventory.this,AddMedicine.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Inventory.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
