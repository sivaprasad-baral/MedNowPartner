package com.example.mednowpartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mednowpartner.Inventory;
import com.example.mednowpartner.R;
import com.example.mednowpartner.model.Medicine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemAdapter.ViewHolder> {

    private Context context;
    private List<Medicine> medicines;

    private DatabaseReference databaseReference;

    public ReceiptItemAdapter(Context context, List<Medicine> medicines) {
        this.context = context;
        this.medicines = medicines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_receipt_item_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Medicine medicine = medicines.get(position);
        holder.textViewMedName.setText(medicine.getMedName());
        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Inventory").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(medicine.getMedId());
                databaseReference.removeValue();
                medicines.remove(medicine);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,medicines.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMedName;
        ImageView btnClose;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMedName = itemView.findViewById(R.id.confirm_receipt_text_view_med_name);
            btnClose = itemView.findViewById(R.id.confirm_receipt_image_close_btn);
        }
    }
}
