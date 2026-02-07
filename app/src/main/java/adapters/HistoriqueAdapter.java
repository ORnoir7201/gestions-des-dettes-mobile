package adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;

import data.models.HistoriqueItem;
import androidx.annotation.NonNull;
import com.example.or.bf.carnetdettes.R;

public class HistoriqueAdapter extends RecyclerView.Adapter<HistoriqueAdapter.ViewHolder> {

    private List<HistoriqueItem> fullList = new ArrayList<>();
    private List<HistoriqueItem> filteredList = new ArrayList<>();

    public HistoriqueAdapter() {}

    public void updateData(List<HistoriqueItem> newItems) {
        fullList.clear();
        fullList.addAll(newItems);

        filteredList.clear();
        filteredList.addAll(newItems);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historique, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoriqueItem item = filteredList.get(position);

        holder.tvClient.setText(item.getClientNom() + " " + item.getClientPrenom());
        holder.tvType.setText(item.getType());
        holder.tvDescription.setText(item.getDescription());
        holder.tvMontant.setText(item.getMontant() + " FCFA");
        holder.tvDate.setText(item.getDate());

        if (item.getType().equals("PAIEMENT")) {
            holder.tvType.setBackgroundColor(Color.parseColor("#388E3C")); // vert
        } else {
            holder.tvType.setBackgroundColor(Color.parseColor("#D32F2F")); // rouge
        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String text) {
        filteredList.clear();

        if (text.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            text = text.toLowerCase();
            for (HistoriqueItem item : fullList) {
                String nom = (item.getClientNom() + " " + item.getClientPrenom()).toLowerCase();
                if (nom.contains(text)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClient, tvType, tvDescription, tvMontant, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvClient = itemView.findViewById(R.id.tvClient);
            tvType = itemView.findViewById(R.id.tvType);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvMontant = itemView.findViewById(R.id.tvMontant);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
