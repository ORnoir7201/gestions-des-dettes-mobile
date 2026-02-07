package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;

import java.util.List;

import data.models.Paiement;

public class PaiementAdapter
        extends RecyclerView.Adapter<PaiementAdapter.ViewHolder> {

    private final List<Paiement> paiements;

    public PaiementAdapter(List<Paiement> paiements) {
        this.paiements = paiements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paiement, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        Paiement p = paiements.get(position);
        holder.tvMontant.setText(p.getMontant() + " FCFA");
        holder.tvDate.setText(p.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return paiements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMontant, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvMontant = itemView.findViewById(R.id.tvMontant);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
