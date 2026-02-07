package dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import data.models.Client;
import data.models.Dette;
import data.models.Paiement;
import data.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;

import retrofit2.Response;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


import adapters.TopClientAdapter;
import data.models.ClientDebt;
import data.remote.SupabaseService;
import ui.auth.LoginActivity;
import ui.dette.DetteListActivity;
import ui.paiement.PaiementListActivity;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private List<Dette> dettes = new ArrayList<>();
    private List<Paiement> paiements = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // --- Toolbar & Drawer --- //
        setupToolbarAndDrawer();

        // --- FAB --- //
        FloatingActionButton fab = findViewById(R.id.fab_add_client);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, ui.client.AddClientActivity.class));
        });

        loadDashboardData();
        loadUserInfo();
        updateNavHeaderUser();




        // --- Handle Back Press (Modern Approach) --- //
                    getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                    // To fall back to the default system behavior (finishing the activity),
                    // we disable this callback and trigger the back press again.
                    if (isEnabled()) {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                }
            }
        });
    }

    private void logout() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vous déconnecter ?")

                .setPositiveButton("Oui", (dialog, which) -> performLogout())
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        SupabaseService.getInstance().signOut(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                // Même si Supabase répond vide → on considère OK
                redirectToLogin();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // En cas d'erreur réseau, on force quand même la déconnexion locale
                redirectToLogin();
            }
        });

        SupabaseService.getInstance().fetchClients().enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Client> clients = response.body();
                    for (Client c : clients) {
                        Log.d("CLIENT",
                                c.getNom() + " " + c.getPrenom()
                                        + " | " + c.getTelephone()
                                        + " | " + c.getAdresse());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Log.e("CLIENT", "Erreur chargement clients", t);
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

        startActivity(intent);

        // petite animation propre
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }



    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tableau de Bord");

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupRecyclerView(List<ClientDebt> data) {
        RecyclerView recyclerView = findViewById(R.id.rvTopClients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TopClientAdapter adapter = new TopClientAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    private void updateStatistics(List<ClientDebt> top5Clients, Map<String, Integer> soldeParClient) {
        TextView tvTotalClients = findViewById(R.id.tvTotalClients);
        TextView tvTotalDebt = findViewById(R.id.tvTotalDebt);
        TextView tvMaxDebt = findViewById(R.id.tvMaxDebt);

        // Calculer sur TOUS les clients qui ont une dette > 0
        int totalClientsAvecDette = 0;
        int totalDebt = 0;
        int maxDebt = 0;

        for (Map.Entry<String, Integer> entry : soldeParClient.entrySet()) {
            if (entry.getValue() > 0) {
                totalClientsAvecDette++;
                totalDebt += entry.getValue();
                if (entry.getValue() > maxDebt) {
                    maxDebt = entry.getValue();
                }
            }
        }

        tvTotalClients.setText(String.valueOf(totalClientsAvecDette));
        tvTotalDebt.setText(String.format("%,d FCFA", totalDebt));
        tvMaxDebt.setText(String.format("%,d FCFA", maxDebt));
    }

    private void loadUserInfo() {
        SupabaseService.getInstance().getCurrentUser().enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Profile profile = response.body().get(0);

                    NavigationView nav = findViewById(R.id.nav_view);
                    TextView tvUserName = nav.getHeaderView(0).findViewById(R.id.tvUserName);
                    tvUserName.setText(profile.getNom() + " " + profile.getPrenom());
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.e("DASHBOARD", "Erreur chargement profil", t);
            }
        });
    }



    private void loadDashboardData() {
        SupabaseService.getInstance().fetchDettes().enqueue(new Callback<List<Dette>>() {
            @Override
            public void onResponse(Call<List<Dette>> call, Response<List<Dette>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dettes = response.body();
                    loadPaiements();
                }
            }

            @Override
            public void onFailure(Call<List<Dette>> call, Throwable t) {
                showToast("Erreur chargement dettes");
            }
        });
    }
    private void loadPaiements() {
        SupabaseService.getInstance().fetchPaiements().enqueue(new Callback<List<Paiement>>() {
            @Override
            public void onResponse(Call<List<Paiement>> call, Response<List<Paiement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    paiements = response.body();
                    calculerDashboard();
                }
            }

            @Override
            public void onFailure(Call<List<Paiement>> call, Throwable t) {
                showToast("Erreur chargement paiements");
            }
        });
    }


    private void updateNavHeaderUser() {
        NavigationView navView = findViewById(R.id.nav_view);
        TextView tvUserName = navView.getHeaderView(0).findViewById(R.id.tvUserName);
        TextView tvUserEmail = navView.getHeaderView(0).findViewById(R.id.tvUserEmail);

        String email = SupabaseService.getInstance().getCurrentUserEmail(this);

        tvUserName.setText("CARNET DE DETTES");
        tvUserEmail.setText(email != null ? email : "");
    }






    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_dashboard) {
            // Déjà sur cet écran
        } else if (itemId == R.id.nav_clients) {
            startActivity(new Intent(this, ui.client.ClientListActivity.class));
        }
        else if (itemId == R.id.nav_dettes) {
            startActivity(new Intent(this, ui.dette.DetteListActivity.class));
        }
        else if (itemId == R.id.nav_paiements) {
            startActivity(new Intent(this, ui.paiement.PaiementListActivity.class));
        }
        else if (itemId == R.id.nav_history) {
            startActivity(new Intent(this, ui.historique.HistoriqueActivity.class));
        }
        else if (itemId == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void calculerDashboard() {
        // D'abord charger les clients pour avoir les noms
        SupabaseService.getInstance().fetchClients().enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Client> allClients = response.body();

                    // Créer une map ID client -> Nom complet
                    Map<String, String> clientNames = new HashMap<>();
                    for (Client client : allClients) {
                        clientNames.put(client.getId(), client.getNom() + " " + client.getPrenom());
                    }

                    // Maintenant calculer les soldes avec les vrais noms
                    calculerSoldesAvecNoms(clientNames);
                } else {
                    Toast.makeText(DashboardActivity.this,
                            "Erreur chargement clients", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Log.e("DASHBOARD", "Erreur chargement clients", t);
                Toast.makeText(DashboardActivity.this,
                        "Erreur réseau clients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculerSoldesAvecNoms(Map<String, String> clientNames) {
        Map<String, Integer> soldeParClient = new HashMap<>();

        // 1️⃣ Calculer les dettes
        for (Dette d : dettes) {
            String clientId = d.getClient_id();
            int montantDette = (int) d.getMontant();
            soldeParClient.put(
                    clientId,
                    soldeParClient.getOrDefault(clientId, 0) + montantDette
            );
        }

        // 2️⃣ Soustraire les paiements
        for (Paiement p : paiements) {
            String clientId = p.getClient_id();
            int montantPaiement = (int) p.getMontant();
            soldeParClient.put(
                    clientId,
                    soldeParClient.getOrDefault(clientId, 0) - montantPaiement
            );
        }

        // 3️⃣ Créer la liste finale avec VRAIS NOMS (pour le top 5)
        List<ClientDebt> allClientsWithDebt = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : soldeParClient.entrySet()) {
            if (entry.getValue() > 0) {
                String clientId = entry.getKey();
                String clientName = clientNames.get(clientId);

                String displayName;
                if (clientName != null && !clientName.trim().isEmpty()) {
                    displayName = clientName;
                } else {
                    displayName = "Client " + (clientId.length() > 8 ? clientId.substring(0, 8) : clientId);
                }

                allClientsWithDebt.add(new ClientDebt(displayName, entry.getValue()));
            }
        }

        // 4️⃣ Trier par montant décroissant
        allClientsWithDebt.sort((a, b) -> b.getMontant() - a.getMontant());

        // 5️⃣ Garder seulement les 5 premiers pour l'affichage
        List<ClientDebt> top5Result;
        if (allClientsWithDebt.size() > 5) {
            top5Result = allClientsWithDebt.subList(0, 5);
        } else {
            top5Result = allClientsWithDebt;
        }

        final List<ClientDebt> finalTop5Result = new ArrayList<>(top5Result);
        final Map<String, Integer> finalSoldeMap = new HashMap<>(soldeParClient); // Copie finale

        // 6️⃣ Mettre à jour l'UI
        runOnUiThread(() -> {
            setupRecyclerView(finalTop5Result);
            updateStatistics(finalTop5Result, finalSoldeMap); // Passer la map complète
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }


}
