package com.example.mednowpartner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mednowpartner.AddMedicine;
import com.example.mednowpartner.R;
import com.example.mednowpartner.model.Medicine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddMedAdapter extends RecyclerView.Adapter<AddMedAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Medicine> medicines;
    private List<Medicine> medicinesAll;
    private AddMedAdapter.MedClickListener medClickListener;

    public AddMedAdapter(Context context, List<Medicine> medicines, AddMedAdapter.MedClickListener medClickListener) {
        this.context = context;
        this.medicines = medicines;
        this.medicinesAll = new ArrayList<>(medicines);
        this.medClickListener = medClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_add_med_list,parent,false),medClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.textViewMedName.setText(medicine.getMedName());
        String priceTag = "Rs.".concat(medicine.getMedPrice());
        holder.textViewMedPrice.setText(priceTag);
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
                    msg = "Select items to add to inventory";
                }
                AddMedicine.textViewMsg.setText(msg);
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewMedName,textViewMedPrice;
        AddMedAdapter.MedClickListener medClickListener;

        ViewHolder(@NonNull View itemView, AddMedAdapter.MedClickListener medClickListener) {
            super(itemView);
            textViewMedName = itemView.findViewById(R.id.add_med_text_view_med_name);
            textViewMedPrice = itemView.findViewById(R.id.add_med_text_view_med_price);
            this.medClickListener = medClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            medClickListener.onMedClick(getAdapterPosition());
            Medicine med = medicines.get(getAdapterPosition());
            medicines.remove(med);
            medicinesAll.remove(med);
            if(medicines.isEmpty()) {
                String msg = "No items to add";
                AddMedicine.textViewMsg.setText(msg);
            }
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(),medicines.size());
        }
    }

    public interface MedClickListener {
        void onMedClick(int position);
    }
}
