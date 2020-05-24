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

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private ChatListClickListener chatListClickListener;

    public ChatHistoryAdapter(Context context, List<User> users, ChatListClickListener chatListClickListener) {
        this.context = context;
        this.users = users;
        this.chatListClickListener = chatListClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_chat_list_history, parent, false), chatListClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        ChatListClickListener chatListClickListener;

        ViewHolder(@NonNull View itemView, ChatListClickListener chatListClickListener) {
            super(itemView);
            textViewCustomerName = itemView.findViewById(R.id.chat_history_text_view_customer_name);
            circleImageViewCustomerImage = itemView.findViewById(R.id.chat_history_circle_image_view_customer_image);
            this.chatListClickListener = chatListClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            chatListClickListener.onChatClick(getAdapterPosition());
        }
    }

    public interface ChatListClickListener {
        void onChatClick(int position);
    }
}
