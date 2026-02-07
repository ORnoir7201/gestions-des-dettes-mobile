package ui.paiement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;

import java.util.List;

import adapters.PaiementListAdapter;
import data.models.PaiementAvecClient;
import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaiementListActivity extends AppCompatActivity {

    private RecyclerView rvPaiements;
    private PaiementListAdapter adapter;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paiement_list);

        rvPaiements = findViewById(R.id.rvPaiements);
        rvPaiements.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearch);
        setupSearch();

        loadPaiements();
    }

    private void loadPaiements() {
        SupabaseService.getInstance()
                .fetchAllPaiements()
                .enqueue(new Callback<List<PaiementAvecClient>>() {
                    @Override
                    public void onResponse(
                            Call<List<PaiementAvecClient>> call,
                            Response<List<PaiementAvecClient>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            adapter = new PaiementListAdapter(response.body());
                            rvPaiements.setAdapter(adapter);
                        } else {
                            Toast.makeText(
                                    PaiementListActivity.this,
                                    "Aucun paiement trouvé",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<PaiementAvecClient>> call,
                            Throwable t) {

                        Toast.makeText(
                                PaiementListActivity.this,
                                "Erreur chargement paiements",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}

            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }
        });
    }
}
