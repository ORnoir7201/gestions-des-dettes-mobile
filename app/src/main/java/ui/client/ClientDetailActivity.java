package ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;

import java.util.List;

import data.models.Client;
import data.models.Dette;
import data.models.Paiement;
import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ui.dette.AddDetteActivity;
import ui.paiement.AddPaiementActivity;
import adapters.PaiementAdapter;
import adapters.DetteAdapter;


public class ClientDetailActivity extends AppCompatActivity {

    // UI
    private TextView tvName, tvPhone, tvAddress, tvSolde;
    private RecyclerView rvPaiements;
    private RecyclerView rvDettes;

    private Button btnAddDette, btnAddPaiement;

    // Data
    private String clientId;
    private double totalDettes = 0;
    private double totalPaiements = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        // Bind UI
        tvName = findViewById(R.id.tvClientName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvSolde = findViewById(R.id.tvSolde);

        rvDettes = findViewById(R.id.rvDettes);
        rvDettes.setLayoutManager(new LinearLayoutManager(this));

        rvPaiements = findViewById(R.id.rvPaiements);
        rvPaiements.setLayoutManager(new LinearLayoutManager(this));


        btnAddDette = findViewById(R.id.btnAddDette);
        btnAddPaiement = findViewById(R.id.btnAddPaiement);

        // Get client ID
        clientId = getIntent().getStringExtra("client_id");
        if (clientId == null) {
            finish();
            return;
        }

        // Buttons actions
        btnAddDette.setOnClickListener(v -> {
            Intent i = new Intent(this, AddDetteActivity.class);
            i.putExtra("client_id", clientId);
            startActivity(i);
        });

        btnAddPaiement.setOnClickListener(v -> {
            double solde = totalDettes - totalPaiements;

            if (solde <= 0) {
                Toast.makeText(this,
                        "Aucune dette à payer",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(this, AddPaiementActivity.class);
            i.putExtra("client_id", clientId);
            i.putExtra("solde", solde);
            startActivity(i);
        });

        // Load data
        loadClient();
        loadDettes();
        loadPaiements();
    }

    private void loadClient() {
        SupabaseService.getInstance()
                .fetchClientById(clientId)
                .enqueue(new Callback<List<Client>>() {
                    @Override
                    public void onResponse(Call<List<Client>> call,
                                           Response<List<Client>> response) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && !response.body().isEmpty()) {

                            Client c = response.body().get(0);
                            tvName.setText(c.getNom() + " " + c.getPrenom());
                            tvPhone.setText(c.getTelephone());
                            tvAddress.setText(c.getAdresse());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Client>> call, Throwable t) {
                    }
                });
    }

    private void loadDettes() {
        SupabaseService.getInstance()
                .fetchDettesByClient(clientId)
                .enqueue(new Callback<List<Dette>>() {
                    @Override
                    public void onResponse(Call<List<Dette>> call,
                                           Response<List<Dette>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            totalDettes = 0;
                            for (Dette d : response.body()) {
                                totalDettes += d.getMontant();
                            }
                            rvDettes.setAdapter(
                                    new DetteAdapter(response.body())
                            );
                        }
                        updateSolde();
                    }

                    @Override
                    public void onFailure(Call<List<Dette>> call, Throwable t) {}
                });
    }


    private void loadPaiements() {
        SupabaseService.getInstance()
                .fetchPaiementsByClient(clientId)
                .enqueue(new Callback<List<Paiement>>() {
                    @Override
                    public void onResponse(Call<List<Paiement>> call,
                                           Response<List<Paiement>> response) {

                        totalPaiements = 0;

                        if (response.isSuccessful() && response.body() != null) {
                            for (Paiement p : response.body()) {
                                totalPaiements += p.getMontant();
                            }
                            rvPaiements.setAdapter(
                                    new PaiementAdapter(response.body())
                            );
                        }
                        updateSolde();
                    }

                    @Override
                    public void onFailure(Call<List<Paiement>> call, Throwable t) {
                        Toast.makeText(ClientDetailActivity.this,
                                "Erreur chargement paiements",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateSolde() {
        double solde = totalDettes - totalPaiements;
        tvSolde.setText("Solde dû : " + solde + " FCFA");

        btnAddPaiement.setEnabled(solde > 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDettes();
        loadPaiements();
    }
}
