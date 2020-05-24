package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednowpartner.adapter.MessageAdapter;
import com.example.mednowpartner.model.Chat;
import com.example.mednowpartner.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerChat extends AppCompatActivity {

    String customerUserId;
    List<Chat> chats;

    TextView textViewCustomerName;
    EditText editTextMsg;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReferenceCustomers,databaseReferenceChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_chat);

        textViewCustomerName = findViewById(R.id.customer_chat_text_view_customer_name);
        editTextMsg = findViewById(R.id.customer_chat_edit_text_message);
        recyclerView = findViewById(R.id.customer_chat_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CustomerChat.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new MessageAdapter(CustomerChat.this,new ArrayList<Chat>()));

        setSupportActionBar((Toolbar) findViewById(R.id.customer_chat_toolbar));
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        customerUserId = getIntent().getStringExtra("customerUserId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceCustomers = FirebaseDatabase.getInstance().getReference().child("Users").child(customerUserId);
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child("Chats");
        databaseReferenceCustomers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                databaseReferenceCustomers.removeEventListener(this);
                textViewCustomerName.setText(Objects.requireNonNull(user).getName());
                readMsg();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
                Toast.makeText(CustomerChat.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMsgBtn(View view) {
        String msg = editTextMsg.getText().toString();
        if(!msg.isEmpty()) {
            Chat chat = new Chat(firebaseUser.getUid(),customerUserId,msg);
            databaseReferenceChats.push().setValue(chat);
        } else {
            editTextMsg.requestFocus();
            Toast.makeText(CustomerChat.this,"Cannot send empty message",Toast.LENGTH_SHORT).show();
        }
        editTextMsg.setText(null);
    }

    public void readMsg() {
        chats = new ArrayList<>();
        databaseReferenceChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(Objects.requireNonNull(chat).getReceiverId().equals(customerUserId) && chat.getSenderId().equals(firebaseUser.getUid()) || chat.getSenderId().equals(customerUserId) && chat.getReceiverId().equals(firebaseUser.getUid())) {
                        chats.add(chat);
                    }
                    recyclerView.setAdapter(new MessageAdapter(CustomerChat.this,chats));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
                Toast.makeText(CustomerChat.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CustomerChat.this,ChatHistory.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
