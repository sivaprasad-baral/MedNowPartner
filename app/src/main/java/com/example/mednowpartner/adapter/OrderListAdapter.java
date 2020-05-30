package com.example.mednowpartner.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mednowpartner.R;
import com.example.mednowpartner.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private OrderListAdapter.OrderListClickListener orderListClickListener;

    public OrderListAdapter(Context context, List<User> users, OrderListAdapter.OrderListClickListener orderListClickListener) {
        this.context = context;
        this.users = users;
        this.orderListClickListener = orderListClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_order_list, parent, false), orderListClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.textViewCustomerName.setText(user.getName());
        if(user.getProfileImg() == null) {
            Glide.with(context).load(R.drawable.ic_person_black_24dp).into(holder.circleImageViewCustomerImage);
        } else {
            Glide.with(context).load(Uri.parse(user.getProfileImg())).into(holder.circleImageViewCustomerImage);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewCustomerName;
        CircleImageView circleImageViewCustomerImage;
        OrderListAdapter.OrderListClickListener orderListClickListener;

        ViewHolder(@NonNull View itemView, OrderListAdapter.OrderListClickListener orderListClickListener) {
            super(itemView);
            textViewCustomerName = itemView.findViewById(R.id.order_list_text_view_customer_name);
            circleImageViewCustomerImage = itemView.findViewById(R.id.order_list_circle_image_view_customer_image);
            this.orderListClickListener = orderListClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            orderListClickListener.onOrderClick(getAdapterPosition());
        }
    }

    public interface OrderListClickListener {
        void onOrderClick(int position);
    }
}
