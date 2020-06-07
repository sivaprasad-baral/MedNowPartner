package com.example.mednowpartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Medicine> medicines;
    private List<Medicine> medicinesAll;

    private DatabaseReference databaseReference;

    public InventoryAdapter(Context context, List<Medicine> medicines) {
        this.context = context;
        this.medicines = medicines;
        this.medicinesAll = new ArrayList<>(medicines);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_inventory_list, parent, false));
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
                databaseReference = FirebaseDatabase.getInstance().getReference("Inventory").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(medicine.getMedId());
                databaseReference.removeValue();
                medicines.remove(medicine);
                medicinesAll.remove(medicine);
                if(medicines.isEmpty()) {
                    String msg = "No item currently in inventory";
                    Inventory.textViewMsg.setText(msg);
                }
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,medicines.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Medicine> filteredMedicines = new ArrayList<>();
                if(constraint == null || constraint.length() == 0) {
                    filteredMedicines.addAll(medicinesAll);
                } else {
                    String search = constraint.toString().toLowerCase().trim();
                    for(Medicine medicine : medicinesAll) {
                        if(medicine.getMedName().toLowerCase().startsWith(search)) {
                            filteredMedicines.add(medicine);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredMedicines;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                medicines.clear();
                medicines.addAll((Collection<? extends Medicine>) results.values);
                notifyDataSetChanged();
                String msg;
                if(medicines.isEmpty()) {
                    msg = "No items matched your search";
                } else {
                    msg = "Items currently in Inventory";
                }
                Inventory.textViewMsg.setText(msg);
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMedName,textViewMedPrice;
        ImageView btnClose;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMedName = itemView.findViewById(R.id.inventory_text_view_med_name);
            textViewMedPrice = itemView.findViewById(R.id.inventory_text_view_med_price);
            btnClose = itemView.findViewById(R.id.inventory_image_close_btn);
        }
    }
}
