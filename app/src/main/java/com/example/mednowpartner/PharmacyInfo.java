package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mednowpartner.model.Partner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PharmacyInfo extends AppCompatActivity {

    String pharmacyName,licenseNo;

    EditText editTextPharmacyName,editTextLicenseNo;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_info);

        editTextPharmacyName = findViewById(R.id.pharmacy_info_edit_text_pharmacy_name);
        editTextLicenseNo = findViewById(R.id.pharmacy_info_edit_text_license_no);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Partners").child(firebaseUser.getUid());
    }

    public void pharmacyRegBtn(View view) {
        pharmacyName = editTextPharmacyName.getText().toString();
        licenseNo = editTextLicenseNo.getText().toString();
        if(pharmacyName.isEmpty()) {
            editTextPharmacyName.setError("Invalid store name");
            editTextPharmacyName.requestFocus();
            return;
        } else if(licenseNo.isEmpty()) {
            editTextLicenseNo.setError("Invalid license no");
            editTextLicenseNo.requestFocus();
            return;
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Partner partner = dataSnapshot.getValue(Partner.class);
                databaseReference.removeEventListener(this);
                Objects.requireNonNull(partner).setPharmacyName(pharmacyName);
                partner.setLicenseNo(licenseNo);
                partner.setRating(0.0);
                databaseReference.setValue(partner).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(PharmacyInfo.this,Location.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        } else {
                            Toast.makeText(PharmacyInfo.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PharmacyInfo.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        databaseReference.removeValue();
        firebaseUser.delete();
        startActivity(new Intent(PharmacyInfo.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
