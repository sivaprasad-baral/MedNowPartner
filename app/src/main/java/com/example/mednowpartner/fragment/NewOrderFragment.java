package com.example.mednowpartner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mednowpartner.OrderList;
import com.example.mednowpartner.R;
import com.example.mednowpartner.ViewPrescription;
import com.example.mednowpartner.adapter.NewOrderAdapter;
import com.example.mednowpartner.model.Order;
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

public class NewOrderFragment extends Fragment implements NewOrderAdapter.OrderListClickListener {

    private ArrayList<User> customers;
    private ArrayList<Order> orders;

    private RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceCustomers,databaseReferenceOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_order,container,false);

        recyclerView = view.findViewById(R.id.new_order_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(container).getContext()));
        recyclerView.setAdapter(new NewOrderAdapter(container.getContext(),new ArrayList<User>(),NewOrderFragment.this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceCustomers = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceOrders = FirebaseDatabase.getInstance().getReference("Orders");

        orders = new ArrayList<>();
        databaseReferenceOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders.clear();
                databaseReferenceOrders.removeEventListener(this);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if(Objects.requireNonNull(order).getPartnerId().equals(firebaseUser.getUid()) && order.getOrderStatus() == Order.ORDER_SENT) {
                        orders.add(order);
                    }
                }
                if(orders.isEmpty()) {
                    String orderHeader = "No new orders";
                    OrderList.textViewHeader.setText(orderHeader);
                } else {
                    displayOrdersList(container);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String orderHeader = "Failed to get orders";
                OrderList.textViewHeader.setText(orderHeader);
                Toast.makeText(container.getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void displayOrdersList(final ViewGroup container) {
        customers = new ArrayList<>();
        databaseReferenceCustomers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customers.clear();
                for(Order order : orders) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(order.getCustomerId().equals(snapshot.getKey())) {
                            User user = snapshot.getValue(User.class);
                            customers.add(user);
                        }
                    }
                }
                databaseReferenceCustomers.removeEventListener(this);
                String orderHeader = "New orders";
                OrderList.textViewHeader.setText(orderHeader);
                recyclerView.setAdapter(new NewOrderAdapter(container.getContext(),customers,NewOrderFragment.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String orderHeader = "Failed to get orders";
                OrderList.textViewHeader.setText(orderHeader);
                Toast.makeText(container.getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(int position) {
        startActivity(new Intent(getActivity(), ViewPrescription.class).putExtra("orderId",orders.get(position).getOrderId()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
