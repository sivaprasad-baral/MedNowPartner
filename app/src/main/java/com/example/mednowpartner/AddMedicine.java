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

import com.example.mednowpartner.adapter.AddMedAdapter;
import com.example.mednowpartner.model.Medicine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddMedicine extends AppCompatActivity implements AddMedAdapter.MedClickListener {

    ArrayList<Medicine> medicinesAdded,medicinesNotAdded;
    HashMap<String,Object> medicinesToAddHashMap;
    AddMedAdapter addMedAdapter;

    EditText editTextSearch;
    public static TextView textViewMsg;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReferenceInventory,databaseReferenceMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        medicinesToAddHashMap = new HashMap<>();

        editTextSearch = findViewById(R.id.add_med_edit_text_search);
        textViewMsg = findViewById(R.id.add_med_text_view_msg);
        recyclerView = findViewById(R.id.add_med_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddMedicine.this));
        addMedAdapter = new AddMedAdapter(AddMedicine.this,new ArrayList<Medicine>(),AddMedicine.this);
        recyclerView.setAdapter(addMedAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceInventory = FirebaseDatabase.getInstance().getReference("Inventory").child(firebaseUser.getUid());
        databaseReferenceMedicine = FirebaseDatabase.getInstance().getReference("Medicine");

        displayMedicines();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addMedAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void displayMedicines() {
        medicinesAdded = new ArrayList<>();
        medicinesNotAdded = new ArrayList<>();
        databaseReferenceInventory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicinesAdded.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    medicinesAdded.add(snapshot.getValue(Medicine.class));
                }
                databaseReferenceInventory.removeEventListener(this);
                databaseReferenceMedicine.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        medicinesNotAdded.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Medicine medicine = snapshot.getValue(Medicine.class);
                            boolean added = false;
                            for(Medicine med : medicinesAdded) {
                                if(med.getMedId().equals(Objects.requireNonNull(medicine).getMedId())) {
                                    added = true;
                                    break;
                                }
                            }
                            if(!added) {
                                medicinesNotAdded.add(medicine);
                            }
                        }
                        databaseReferenceMedicine.removeEventListener(this);
                        String msg;
                        if(medicinesNotAdded.isEmpty()) {
                            msg = "No items to add";
                        } else {
                            msg = "Select items to add to inventory";
                        }
                        textViewMsg.setText(msg);
                        addMedAdapter = new AddMedAdapter(AddMedicine.this,medicinesNotAdded,AddMedicine.this);
                        recyclerView.setAdapter(addMedAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddMedicine.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddMedicine.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void confirmAddBtn(View view) {
        databaseReferenceInventory.updateChildren(medicinesToAddHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(AddMedicine.this,"Medicines Added Successfully",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(AddMedicine.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMedClick(int position) {
        Medicine medicine = medicinesNotAdded.get(position);
        medicinesToAddHashMap.put(medicinesNotAdded.get(position).getMedId(),medicine);
        Toast.makeText(AddMedicine.this,"Added To Your List",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddMedicine.this,Inventory.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
