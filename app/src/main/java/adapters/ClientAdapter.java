package adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.models.Client;
import data.models.Dette;
import data.models.Paiement;
import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ui.client.ClientDetailActivity;
import ui.client.EditClientActivity;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {

    private final List<Client> fullList;
    private final List<Client> filteredList;
    private final Context context;
    private final Map<String, Double> soldeMap; // Cache pour les soldes
    private final Map<String, Boolean> loadingMap; // Pour suivre les chargements

    public ClientAdapter(List<Client> clients, Context context) {
        this.fullList = new ArrayList<>(clients);
        this.filteredList = new ArrayList<>(clients);
        this.context = context;
        this.soldeMap = new HashMap<>();
        this.loadingMap = new HashMap<>();

        // Charger les soldes pour tous les clients
        for (Client client : clients) {
            loadClientBalance(client);
        }
    }

    public void updateList(List<Client> newClients) {
        fullList.clear();
        fullList.addAll(newClients);
        filteredList.clear();
        filteredList.addAll(newClients);
        soldeMap.clear();
        loadingMap.clear();

        // Charger les soldes pour les nouveaux clients
        for (Client client : newClients) {
            loadClientBalance(client);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Client client = filteredList.get(position);
        holder.tvName.setText(client.getNom() + " " + client.getPrenom());

        if (holder.tvTelephone != null) {
            holder.tvTelephone.setText(client.getTelephone());
        }

        // Afficher le solde du client
        displayClientBalance(holder, client.getId());

        // Click sur l'item pour voir les détails
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ClientDetailActivity.class);
            i.putExtra("client_id", client.getId());
            v.getContext().startActivity(i);
        });

        // Bouton Modifier
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditClientActivity.class);
            intent.putExtra("client_id", client.getId());
            context.startActivity(intent);
        });

        // Bouton Supprimer - avec vérification du solde
        holder.btnDelete.setOnClickListener(v -> {
            checkAndDeleteClient(client);
        });
    }

    private void loadClientBalance(Client client) {
        String clientId = client.getId();

        // Si déjà en cours de chargement ou déjà chargé, ne pas recharger
        if (loadingMap.containsKey(clientId) || soldeMap.containsKey(clientId)) {
            return;
        }

        loadingMap.put(clientId, true);

        // Récupérer toutes les dettes du client
        SupabaseService.getInstance().fetchDettesByClient(clientId)
                .enqueue(new Callback<List<Dette>>() {
                    @Override
                    public void onResponse(Call<List<Dette>> call, Response<List<Dette>> response) {
                        double totalDettes = 0;
                        if (response.isSuccessful() && response.body() != null) {
                            for (Dette dette : response.body()) {
                                totalDettes += dette.getMontant();
                            }
                        }

                        // Récupérer tous les paiements du client
                        loadPaiementsForBalance(clientId, totalDettes);
                    }

                    @Override
                    public void onFailure(Call<List<Dette>> call, Throwable t) {
                        loadingMap.remove(clientId);
                    }
                });
    }

    private void loadPaiementsForBalance(String clientId, double totalDettes) {
        SupabaseService.getInstance().fetchPaiementsByClient(clientId)
                .enqueue(new Callback<List<Paiement>>() {
                    @Override
                    public void onResponse(Call<List<Paiement>> call, Response<List<Paiement>> response) {
                        double totalPaiements = 0;
                        if (response.isSuccessful() && response.body() != null) {
                            for (Paiement paiement : response.body()) {
                                totalPaiements += paiement.getMontant();
                            }
                        }

                        // Calculer et stocker le solde
                        double solde = totalDettes - totalPaiements;
                        soldeMap.put(clientId, solde);
                        loadingMap.remove(clientId);

                        // Notifier l'adaptateur de mettre à jour cette position
                        notifyItemChanged(getPositionForClient(clientId));
                    }

                    @Override
                    public void onFailure(Call<List<Paiement>> call, Throwable t) {
                        loadingMap.remove(clientId);
                    }
                });
    }

    private int getPositionForClient(String clientId) {
        for (int i = 0; i < filteredList.size(); i++) {
            if (filteredList.get(i).getId().equals(clientId)) {
                return i;
            }
        }
        return -1;
    }

    private void displayClientBalance(ViewHolder holder, String clientId) {
        if (soldeMap.containsKey(clientId)) {
            double solde = soldeMap.get(clientId);

            if (holder.tvSolde != null) {
                String soldeText;
                int backgroundResId;
                int textColor;

                if (solde > 0) {
                    // Solde négatif (doit de l'argent)
                    soldeText = String.format("-%.2f FCFA", solde);
                    backgroundResId = R.drawable.bg_solde_negatif;
                    textColor = context.getResources().getColor(android.R.color.white);
                }  else {
                    // Solde = 0
                    soldeText = "0 FCFA";
                    backgroundResId = R.drawable.bg_solde_zero;
                    textColor = context.getResources().getColor(android.R.color.white);
                }

                holder.tvSolde.setText(soldeText);
                holder.tvSolde.setBackgroundResource(backgroundResId);
                holder.tvSolde.setTextColor(textColor);
                holder.tvSolde.setVisibility(View.VISIBLE);
            }
        } else {
            // Solde pas encore chargé
            if (holder.tvSolde != null) {
                holder.tvSolde.setText("Chargement...");
                holder.tvSolde.setBackgroundResource(R.drawable.bg_solde_zero);
                holder.tvSolde.setVisibility(View.VISIBLE);
            }
        }
    }

    // ==================== VÉRIFICATION DU SOLDE POUR SUPPRESSION ====================
    private void checkAndDeleteClient(Client client) {
        AlertDialog loadingDialog = new AlertDialog.Builder(context)
                .setTitle("Vérification...")
                .setMessage("Vérification du solde du client")
                .setCancelable(false)
                .show();

        String clientId = client.getId();

        // Récupérer toutes les dettes du client
        SupabaseService.getInstance().fetchDettesByClient(clientId)
                .enqueue(new Callback<List<Dette>>() {
                    @Override
                    public void onResponse(Call<List<Dette>> call, Response<List<Dette>> response) {
                        double totalDettes = 0;
                        if (response.isSuccessful() && response.body() != null) {
                            for (Dette dette : response.body()) {
                                totalDettes += dette.getMontant();
                            }
                        }

                        checkPaiementsForDeletion(client, totalDettes, loadingDialog);
                    }

                    @Override
                    public void onFailure(Call<List<Dette>> call, Throwable t) {
                        loadingDialog.dismiss();
                        Toast.makeText(context,
                                "Erreur réseau lors de la vérification",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkPaiementsForDeletion(Client client, double totalDettes, AlertDialog loadingDialog) {
        SupabaseService.getInstance().fetchPaiementsByClient(client.getId())
                .enqueue(new Callback<List<Paiement>>() {
                    @Override
                    public void onResponse(Call<List<Paiement>> call, Response<List<Paiement>> response) {
                        loadingDialog.dismiss();

                        double totalPaiements = 0;
                        if (response.isSuccessful() && response.body() != null) {
                            for (Paiement paiement : response.body()) {
                                totalPaiements += paiement.getMontant();
                            }
                        }

                        double solde = totalDettes - totalPaiements;

                        if (solde > 0) {
                            showCannotDeleteDialog(client, solde);
                        } else {
                            showDeleteConfirmationDialog(client, solde);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Paiement>> call, Throwable t) {
                        loadingDialog.dismiss();
                        Toast.makeText(context,
                                "Erreur réseau lors de la vérification",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCannotDeleteDialog(Client client, double solde) {
        String message = String.format(
                "Impossible de supprimer %s %s\n\n" +
                        "Montant dû : %.2f FCFA\n\n" +
                        "⚠Ce client a encore des dettes non réglées.\n" +
                        "Veuillez d'abord régler toutes les dettes avant de supprimer.",
                client.getNom(), client.getPrenom(), solde
        );

        new AlertDialog.Builder(context)
                .setTitle("Suppression impossible")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDeleteConfirmationDialog(Client client, double solde) {
        String message;

        if (solde == 0) {
            message = String.format(
                    "Voulez-vous vraiment supprimer %s %s ?\n\n" +
                            " Solde : 0 FCFA (toutes les dettes sont réglées)",
                    client.getNom(), client.getPrenom()
            );
        } else {
            message = String.format(
                    "Voulez-vous vraiment supprimer %s %s ?\n\n" +
                            " Crédit : %.2f FCFA (a payé plus que dû)\n\n" +
                            " Cette action supprimera également toutes ses dettes et paiements.",
                    client.getNom(), client.getPrenom(), Math.abs(solde)
            );
        }

        new AlertDialog.Builder(context)
                .setTitle("Supprimer le client")
                .setMessage(message)
                .setPositiveButton("Supprimer", (dialog, which) -> deleteClient(client))
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteClient(Client client) {
        String clientId = client.getId();

        SupabaseService.getInstance().deleteClientPaiements(clientId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        SupabaseService.getInstance().deleteClientDettes(clientId)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        SupabaseService.getInstance().deleteClient(clientId)
                                                .enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                                        Toast.makeText(context,
                                                                "Client supprimé avec succès",
                                                                Toast.LENGTH_SHORT).show();
                                                        removeClientFromList(clientId);
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Void> call, Throwable t) {
                                                        Toast.makeText(context,
                                                                "Erreur réseau",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context,
                                                "Erreur lors de la suppression des dettes",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context,
                                "Erreur lors de la suppression des paiements",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeClientFromList(String clientId) {
        for (int i = 0; i < filteredList.size(); i++) {
            if (filteredList.get(i).getId().equals(clientId)) {
                filteredList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }

        for (int i = 0; i < fullList.size(); i++) {
            if (fullList.get(i).getId().equals(clientId)) {
                fullList.remove(i);
                break;
            }
        }

        // Retirer du cache
        soldeMap.remove(clientId);
        loadingMap.remove(clientId);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String text) {
        filteredList.clear();

        if (text == null || text.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            text = text.toLowerCase();
            for (Client client : fullList) {
                String nomComplet = (client.getNom() + " " + client.getPrenom()).toLowerCase();
                if (nomComplet.contains(text)) {
                    filteredList.add(client);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTelephone, tvSolde;
        ImageView btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTelephone = itemView.findViewById(R.id.tvTelephone);
            tvSolde = itemView.findViewById(R.id.tvSolde);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}