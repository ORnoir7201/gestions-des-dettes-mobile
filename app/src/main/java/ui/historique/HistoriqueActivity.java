package ui.historique;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;

import java.util.ArrayList;
import java.util.List;

import adapters.HistoriqueAdapter;
import data.models.DetteAvecClient;
import data.models.PaiementAvecClient;
import data.models.HistoriqueItem;
import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoriqueActivity extends AppCompatActivity {

    private RecyclerView rvHistorique;
    private HistoriqueAdapter adapter;
    private List<HistoriqueItem> allItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        rvHistorique = findViewById(R.id.rvHistorique);
        rvHistorique.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoriqueAdapter();
        rvHistorique.setAdapter(adapter);

        loadDettes();
        loadPaiements();

        setupSearch();
    }

    private void loadDettes() {
        SupabaseService.getInstance()
                .fetchAllDettes()
                .enqueue(new Callback<List<DetteAvecClient>>() {
                    @Override
                    public void onResponse(Call<List<DetteAvecClient>> call,
                                           Response<List<DetteAvecClient>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            for (DetteAvecClient d : response.body()) {
                                allItems.add(new HistoriqueItem(
                                        d.getClients().getNom(),
                                        d.getClients().getPrenom(),
                                        "DETTE",
                                        d.getDescription(),
                                        d.getMontant(),
                                        d.getCreated_at()
                                ));
                            }
                            adapter.updateData(allItems);


                        }
                    }

                    @Override
                    public void onFailure(Call<List<DetteAvecClient>> call, Throwable t) {}
                });
    }

    private void loadPaiements() {
        SupabaseService.getInstance()
                .fetchAllPaiements()
                .enqueue(new Callback<List<PaiementAvecClient>>() {
                    @Override
                    public void onResponse(Call<List<PaiementAvecClient>> call,
                                           Response<List<PaiementAvecClient>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            for (PaiementAvecClient p : response.body()) {
                                allItems.add(new HistoriqueItem(
                                        p.getClients().getNom(),
                                        p.getClients().getPrenom(),
                                        "PAIEMENT",
                                        "Paiement",
                                        p.getMontant(),
                                        p.getCreated_at()
                                ));
                            }

                            adapter.updateData(allItems);


                        }
                    }

                    @Override
                    public void onFailure(Call<List<PaiementAvecClient>> call, Throwable t) {}
                });
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
