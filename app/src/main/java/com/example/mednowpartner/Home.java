package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    public final String RECEIVE_ORDER = "Current Status\nReceiving";
    public final String NOT_RECEIVE_ORDER = "Current Status\nNot Receiving";

    boolean pharmacyDisplay = false,profileDisplay = false;
    Partner partner;

    ImageView imageViewShowHidePharmacy,imageViewShowHideProfile;
    ImageView imageViewAvailabilityStatus,imageViewPharmacyImg;
    RelativeLayout relativeLayoutProfileInfo;
    LinearLayout linearLayoutProfileButtons;
    TextView textViewName,textViewPhone,textViewEmail,textViewPharmacyName;
    TextView textViewAvailabilityStatus;
    CircleImageView circleImageViewProfileImage;

    DatabaseReference databaseReferencePartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageViewPharmacyImg = findViewById(R.id.home_image_view_pharmacy_image);
        imageViewShowHideProfile = findViewById(R.id.home_image_view_show_hide_profile);
        imageViewShowHidePharmacy = findViewById(R.id.home_image_view_show_hide_pharmacy);
        relativeLayoutProfileInfo = findViewById(R.id.home_relative_layout_profile_info);
        imageViewAvailabilityStatus = findViewById(R.id.home_image_view_availability_status);
        textViewAvailabilityStatus = findViewById(R.id.home_text_view_availability_status);
        linearLayoutProfileButtons = findViewById(R.id.home_linear_layout_profile_buttons);
        textViewName = findViewById(R.id.home_text_view_profile_name);
        textViewPhone = findViewById(R.id.home_text_view_profile_phone);
        textViewEmail = findViewById(R.id.home_text_view_profile_email);
        textViewPharmacyName = findViewById(R.id.home_text_view_pharmacy_name);
        circleImageViewProfileImage = findViewById(R.id.home_circular_image_view_profile_image);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferencePartner = FirebaseDatabase.getInstance().getReference().child("Partners").child(Objects.requireNonNull(firebaseUser).getUid());
        databaseReferencePartner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                partner = dataSnapshot.getValue(Partner.class);
                databaseReferencePartner.removeEventListener(this);
                partner.setAvailable(true);
                databaseReferencePartner.setValue(partner);
                imageViewAvailabilityStatus.setImageResource(R.drawable.ic_plus_circle);
                textViewAvailabilityStatus.setText(RECEIVE_ORDER);
                String label = "Name : ";
                textViewName.setText(label.concat(partner.getName()));
                if(partner.getPhone() == null) {
                    label = "Mobile : N/A";
                    textViewPhone.setText(label);
                } else {
                    label = "Mobile : ";
                    textViewPhone.setText(label.concat(partner.getPhone()));
                }
                label = "Email : ";
                textViewEmail.setText(label.concat(partner.getEmail()));
                textViewPharmacyName.setText(partner.getPharmacyName());
                if(partner.getProfileImg() == null) {
                    Glide.with(Home.this).load(R.drawable.ic_person_black_24dp).into(circleImageViewProfileImage);
                } else {
                    Glide.with(Home.this).load(Uri.parse(partner.getProfileImg())).into(circleImageViewProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showHidePharmacyBtn(View view) {
        if(pharmacyDisplay) {
            imageViewPharmacyImg.setVisibility(View.GONE);
            imageViewShowHidePharmacy.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            pharmacyDisplay = false;
        } else {
            imageViewPharmacyImg.setVisibility(View.VISIBLE);
            imageViewShowHidePharmacy.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            pharmacyDisplay = true;
        }
    }

    public void showHideProfileBtn(View view) {
        if(profileDisplay) {
            relativeLayoutProfileInfo.setVisibility(View.GONE);
            linearLayoutProfileButtons.setVisibility(View.GONE);
            imageViewShowHideProfile.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            profileDisplay = false;
        } else {
            relativeLayoutProfileInfo.setVisibility(View.VISIBLE);
            linearLayoutProfileButtons.setVisibility(View.VISIBLE);
            imageViewShowHideProfile.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            profileDisplay = true;
        }
    }

    public void availabilityToggleBtn(View view) {
        if(partner.isAvailable()) {
            partner.setAvailable(false);
            databaseReferencePartner.setValue(partner).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(Home.this,"You will not be receiving orders",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Home.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            imageViewAvailabilityStatus.setImageResource(R.drawable.ic_plus_circle_red);
            textViewAvailabilityStatus.setText(NOT_RECEIVE_ORDER);
        } else {
            partner.setAvailable(true);
            databaseReferencePartner.setValue(partner).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(Home.this,"You will be receiving orders",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Home.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            imageViewAvailabilityStatus.setImageResource(R.drawable.ic_plus_circle);
            textViewAvailabilityStatus.setText(RECEIVE_ORDER);
        }
    }

    public void logOutBtn(View view) {
        partner.setAvailable(false);
        databaseReferencePartner.setValue(partner);
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Home.this,Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        partner.setAvailable(false);
        databaseReferencePartner.setValue(partner);
        super.onBackPressed();
    }

    public void chatHistoryBtn(View view) {
        startActivity(new Intent(Home.this,ChatHistory.class));
    }

    public void orderListBtn(View view) {
        startActivity(new Intent(Home.this,OrderList.class));
    }

    public void inventoryBtn(View view) {
        startActivity(new Intent(Home.this,Inventory.class));
    }
}
