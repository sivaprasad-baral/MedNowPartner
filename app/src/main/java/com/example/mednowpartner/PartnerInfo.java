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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class PartnerInfo extends AppCompatActivity {

    String name,email;

    EditText editTextName,editTextEmail;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_info);

        editTextName = findViewById(R.id.partner_info_edit_text_name);
        editTextEmail = findViewById(R.id.partner_info_edit_text_email);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Partners").child(firebaseUser.getUid());
    }

    public void partnerRegBtn(View view) {
        name = editTextName.getText().toString();
        email = editTextEmail.getText().toString();
        if(name.isEmpty()) {
            editTextName.setError("Invalid name");
            editTextName.requestFocus();
            return;
        } else if(email.isEmpty() || !email.contains("@") || !email.contains(".com")) {
            editTextEmail.setError("Invalid email");
            editTextEmail.requestFocus();
            return;
        }
        UserProfileChangeRequest nameChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();   // Create a name change request
        firebaseUser.updateProfile(nameChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Partner partner = new Partner(firebaseUser.getEmail(),firebaseUser.getDisplayName(),firebaseUser.getPhoneNumber(),firebaseUser.getUid());
                                databaseReference.setValue(partner).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            startActivity(new Intent(PartnerInfo.this,PharmacyInfo.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        } else {
                                            Toast.makeText(PartnerInfo.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(PartnerInfo.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(PartnerInfo.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        databaseReference.removeValue();
        firebaseUser.delete();
        startActivity(new Intent(PartnerInfo.this,Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
