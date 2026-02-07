package ui.dette;

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

import java.util.List;

import adapters.DetteGlobalAdapter;
import data.models.DetteAvecClient;
import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetteListActivity extends AppCompatActivity {

    private RecyclerView rvDettes;
    private DetteGlobalAdapter adapter;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dette_list);

        rvDettes = findViewById(R.id.rvDettes);
        rvDettes.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearch);
        setupSearch();

        loadDettes();
    }

    private void loadDettes() {
        SupabaseService.getInstance()
                .fetchAllDettes()
                .enqueue(new Callback<List<DetteAvecClient>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<DetteAvecClient>> call,
                            @NonNull Response<List<DetteAvecClient>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter = new DetteGlobalAdapter(response.body());
                            rvDettes.setAdapter(adapter);
                        } else {
                            Toast.makeText(
                                    DetteListActivity.this,
                                    "Aucune dette trouvée",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<DetteAvecClient>> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                DetteListActivity.this,
                                "Erreur réseau",
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
