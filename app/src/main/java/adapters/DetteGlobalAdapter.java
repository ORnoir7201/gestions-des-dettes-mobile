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

import data.models.DetteAvecClient;

public class DetteGlobalAdapter
        extends RecyclerView.Adapter<DetteGlobalAdapter.ViewHolder> {

    private final List<DetteAvecClient> fullList;     // liste complète
    private final List<DetteAvecClient> filteredList; // liste affichée

    public DetteGlobalAdapter(List<DetteAvecClient> dettes) {
        this.fullList = new ArrayList<>(dettes);
        this.filteredList = new ArrayList<>(dettes);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dette_global, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder h, int position) {

        DetteAvecClient d = filteredList.get(position);

        h.tvClient.setText(
                d.getClients().getNom() + " " +
                        d.getClients().getPrenom()
        );
        h.tvDescription.setText(d.getDescription());
        h.tvMontant.setText(d.getMontant() + " FCFA");
        h.tvDate.setText(d.getCreated_at());
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

            for (DetteAvecClient d : fullList) {
                String contenu =
                        (d.getDescription() + " " +
                                d.getClients().getNom() + " " +
                                d.getClients().getPrenom())
                                .toLowerCase();

                if (contenu.contains(text)) {
                    filteredList.add(d);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClient, tvDescription, tvMontant, tvDate;

        ViewHolder(View v) {
            super(v);
            tvClient = v.findViewById(R.id.tvClient);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvMontant = v.findViewById(R.id.tvMontant);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }
}
