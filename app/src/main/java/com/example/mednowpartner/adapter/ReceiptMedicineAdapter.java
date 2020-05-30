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

public class ReceiptMedicineAdapter extends RecyclerView.Adapter<ReceiptMedicineAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Medicine> medicines;
    private List<Medicine> medicinesAll;
    private ReceiptMedicineAdapter.MedClickListener medClickListener;

    public ReceiptMedicineAdapter(Context context, List<Medicine> medicines, MedClickListener medClickListener) {
        this.context = context;
        this.medicines = medicines;
        this.medicinesAll = new ArrayList<>(medicines);
        this.medClickListener = medClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_receipt_med_list, parent, false), medClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewMedName.setText(medicines.get(position).getMedName());
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
                if (constraint == null || constraint.length() == 0) {
                    filteredMedicines.addAll(medicinesAll);
                } else {
                    String search = constraint.toString().toLowerCase().trim();
                    for (Medicine medicine : medicinesAll) {
                        if (medicine.getMedName().toLowerCase().startsWith(search)) {
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
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewMedName;
        ReceiptMedicineAdapter.MedClickListener medClickListener;

        ViewHolder(@NonNull View itemView,ReceiptMedicineAdapter.MedClickListener medClickListener) {
            super(itemView);
            textViewMedName = itemView.findViewById(R.id.make_receipt_text_view_med_name);
            this.medClickListener = medClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            medClickListener.onMedClick(getAdapterPosition());
            Medicine med = medicines.get(getAdapterPosition());
            medicines.remove(med);
            medicinesAll.remove(med);
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(),medicines.size());
        }
    }

    public interface MedClickListener {
        void onMedClick(int position);
    }
}
