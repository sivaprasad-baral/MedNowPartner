package com.example.mednowpartner.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mednowpartner.ConfirmReceipt;
import com.example.mednowpartner.Inventory;
import com.example.mednowpartner.MakeReceipt;
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
    private String orderId;

    public ReceiptItemAdapter(Context context, List<Medicine> medicines, String orderId) {
        this.context = context;
        this.medicines = medicines;
        this.orderId = orderId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_receipt_item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Medicine medicine = medicines.get(position);
        holder.textViewMedName.setText(medicine.getMedName());
        String priceTag = "Rs. ".concat(medicine.getMedPrice());
        holder.textViewMedPrice.setText(priceTag);
        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("medicines").child(medicine.getMedId()).removeValue();
                ConfirmReceipt.cost -= Double.parseDouble(medicine.getMedPrice());
                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("cost").setValue(ConfirmReceipt.cost);
                String costTag = "Rs. ".concat(String.valueOf(ConfirmReceipt.cost));
                ConfirmReceipt.textViewCost.setText(costTag);
                medicines.remove(medicine);
                if(medicines.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("Orders").child(orderId).removeValue();
                    Toast.makeText(context,"No items to send",Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, MakeReceipt.class).putExtra("prescriptionId",orderId).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,medicines.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMedName,textViewMedPrice;
        ImageView btnClose;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMedName = itemView.findViewById(R.id.confirm_receipt_text_view_med_name);
            textViewMedPrice = itemView.findViewById(R.id.confirm_receipt_text_view_med_price);
            btnClose = itemView.findViewById(R.id.confirm_receipt_image_close_btn);
        }
    }
}
