package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;

import java.util.ArrayList;
import java.util.List;

import data.models.PaiementAvecClient;

public class PaiementListAdapter
        extends RecyclerView.Adapter<PaiementListAdapter.ViewHolder> {

    private final List<PaiementAvecClient> fullList;     // liste complète
    private final List<PaiementAvecClient> filteredList; // liste affichée

    public PaiementListAdapter(List<PaiementAvecClient> paiements) {
        this.fullList = new ArrayList<>(paiements);
        this.filteredList = new ArrayList<>(paiements);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paiement_global, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        PaiementAvecClient p = filteredList.get(position);

        holder.tvClient.setText(
                p.getClients().getNom() + " " +
                        p.getClients().getPrenom()
        );
        holder.tvMontant.setText(p.getMontant() + " FCFA");
        holder.tvDate.setText(p.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // 🔍 FILTRE RECHERCHE
    public void filter(String text) {
        filteredList.clear();

        if (text == null || text.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            text = text.toLowerCase();

            for (PaiementAvecClient p : fullList) {
                String contenu =
                        (p.getClients().getNom() + " " +
                                p.getClients().getPrenom() + " " +
                                p.getMontant())
                                .toLowerCase();

                if (contenu.contains(text)) {
                    filteredList.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClient, tvMontant, tvDate;

        ViewHolder(View v) {
            super(v);
            tvClient = v.findViewById(R.id.tvClient);
            tvMontant = v.findViewById(R.id.tvMontant);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }
}
