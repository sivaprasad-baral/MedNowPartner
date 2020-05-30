package com.example.mednowpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednowpartner.adapter.ChatHistoryAdapter;
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
import java.util.Objects;

public class ChatHistory extends AppCompatActivity implements ChatHistoryAdapter.ChatListClickListener {

    ArrayList<User> users;
    ArrayList<String> customerUserIds;

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView textViewHeader;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReferenceCustomers,databaseReferenceChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        toolbar = findViewById(R.id.chat_history_toolbar);
        recyclerView = findViewById(R.id.chat_history_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatHistory.this));
        textViewHeader = findViewById(R.id.chat_history_text_view_header);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView.setAdapter(new ChatHistoryAdapter(ChatHistory.this,new ArrayList<User>(),ChatHistory.this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceCustomers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child("Chats");

        customerUserIds = new ArrayList<>();
        databaseReferenceChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerUserIds.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    String customerId = null;
                    if(Objects.requireNonNull(chat).getReceiverId().equals(firebaseUser.getUid())) {
                        customerId = chat.getSenderId();
                    } else if(chat.getSenderId().equals(firebaseUser.getUid())) {
                        customerId = chat.getReceiverId();
                    }
                    if(!customerUserIds.contains(customerId)) {
                        customerUserIds.add(customerId);
                    }
                }
                if(customerUserIds.isEmpty()) {
                    String msgHeader = "No chats found";
                    textViewHeader.setText(msgHeader);
                } else {
                    displayChatsHistory();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msgHeader = "No chats found";
                textViewHeader.setText(msgHeader);
                Toast.makeText(ChatHistory.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChatsHistory() {
        users = new ArrayList<>();
        databaseReferenceCustomers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for(String userId : customerUserIds) {
                        if (Objects.requireNonNull(user).getUserId().equals(userId)) {
                            users.add(user);
                        }
                    }
                }
                String msgHeader = "Your chats";
                textViewHeader.setText(msgHeader);
                recyclerView.setAdapter(new ChatHistoryAdapter(ChatHistory.this,users,ChatHistory.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msgHeader = "No chats found";
                textViewHeader.setText(msgHeader);
                Toast.makeText(ChatHistory.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onChatClick(int position) {
        startActivity(new Intent(ChatHistory.this,CustomerChat.class).putExtra("customerUserId",users.get(position).getUserId()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatHistory.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
