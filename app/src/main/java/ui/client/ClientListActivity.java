package ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import adapters.ClientAdapter;
import data.models.Client;
import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClientAdapter adapter;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        recyclerView = findViewById(R.id.rvClients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialiser l'adaptateur avec le contexte
        adapter = new ClientAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // ===== Recherche =====
        etSearch = findViewById(R.id.etSearch);
        setupSearch();

        // ===== FAB =====
        FloatingActionButton fab = findViewById(R.id.fab_add_client);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddClientActivity.class));
        });

        loadClients();
    }

    private void loadClients() {
        SupabaseService.getInstance().fetchClients()
                .enqueue(new Callback<List<Client>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Client>> call,
                                           @NonNull Response<List<Client>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            adapter.updateList(response.body());
                        } else {
                            Toast.makeText(ClientListActivity.this,
                                    "Aucun client trouvé",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Client>> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(ClientListActivity.this,
                                "Erreur réseau",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les clients quand on revient à cette activité
        loadClients();
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
        });
    }
}