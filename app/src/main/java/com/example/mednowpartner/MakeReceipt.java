package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mednowpartner.adapter.ReceiptMedicineAdapter;
import com.example.mednowpartner.model.Medicine;
import com.example.mednowpartner.model.Order;
import com.example.mednowpartner.model.Prescription;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MakeReceipt extends AppCompatActivity implements ReceiptMedicineAdapter.MedClickListener {

    String prescriptionId;
    List<Medicine> medicines;
    HashMap<String,Object> medicinesOrdered;
    ReceiptMedicineAdapter medicineAdapter;
    Prescription prescription;

    BigImageView bigImageViewPrescription;
    EditText editTextSearch;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReferencePrescriptions,databaseReferenceInventory,databaseReferenceOrders;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(MakeReceipt.this));
        setContentView(R.layout.activity_make_receipt);

        medicinesOrdered = new HashMap<>();

        bigImageViewPrescription = findViewById(R.id.make_receipt_image_view_prescription);
        editTextSearch = findViewById(R.id.make_receipt_edit_text_search);
        recyclerView = findViewById(R.id.make_receipt_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MakeReceipt.this));
        medicineAdapter = new ReceiptMedicineAdapter(MakeReceipt.this,new ArrayList<Medicine>(),MakeReceipt.this);
        recyclerView.setAdapter(medicineAdapter);

        prescriptionId = getIntent().getStringExtra("prescriptionId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferencePrescriptions = FirebaseDatabase.getInstance().getReference("Prescriptions").child(prescriptionId);
        databaseReferenceInventory = FirebaseDatabase.getInstance().getReference("Inventory").child(firebaseUser.getUid());
        databaseReferenceOrders = FirebaseDatabase.getInstance().getReference("Orders").child(prescriptionId);
        displayPrescription();
        displayMedicine();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                medicineAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void displayMedicine() {
        medicines = new ArrayList<>();
        databaseReferenceInventory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicines.clear();
                databaseReferenceInventory.removeEventListener(this);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    medicines.add(snapshot.getValue(Medicine.class));
                }
                medicineAdapter = new ReceiptMedicineAdapter(MakeReceipt.this, medicines,MakeReceipt.this);
                recyclerView.setAdapter(medicineAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MakeReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPrescription() {
        databaseReferencePrescriptions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prescription = dataSnapshot.getValue(Prescription.class);
                databaseReferencePrescriptions.removeEventListener(this);
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Objects.requireNonNull(prescription).getPrescriptionImg());
                bigImageViewPrescription.showImage(Uri.parse(Objects.requireNonNull(prescription).getPrescriptionImg()));
                bigImageViewPrescription.setProgressIndicator(new ProgressPieIndicator());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MakeReceipt.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMedClick(int position) {
        Medicine medicine = medicines.get(position);
        medicine.setMedQty(1);
        medicinesOrdered.put(Objects.requireNonNull(medicines.get(position)).getMedId(),medicine);
        Toast.makeText(MakeReceipt.this,"Added To Receipt",Toast.LENGTH_SHORT).show();
    }

    public void makeReceiptBtn(View view) {
        if(medicinesOrdered.isEmpty()) {
            Toast.makeText(MakeReceipt.this,"Empty Receipt No Medicines Added",Toast.LENGTH_SHORT).show();
        } else {
            double total = 0;
            for(Map.Entry<String, Object> entry : medicinesOrdered.entrySet()) {
                Medicine med = (Medicine) entry.getValue();
                total = total + Double.parseDouble(med.getMedPrice());
            }
            Order order = new Order(prescription.getPartnerId(),prescription.getUserId(),prescriptionId,total,medicinesOrdered);
            databaseReferenceOrders.setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(MakeReceipt.this,"Bill generated successfully for the order",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MakeReceipt.this,ConfirmReceipt.class).putExtra("orderId",prescriptionId).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        Toast.makeText(MakeReceipt.this,"Bill generation failed. Try again...",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
