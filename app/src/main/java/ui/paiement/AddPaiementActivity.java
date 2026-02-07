package ui.paiement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.or.bf.carnetdettes.R;

import androidx.appcompat.app.AppCompatActivity;

import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPaiementActivity extends AppCompatActivity {

    private EditText etMontant;
    private Button btnSave;

    private String clientId;
    private double solde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paiement);

        // 1️⃣ Récupération des vues
        etMontant = findViewById(R.id.etMontantPaiement);
        btnSave = findViewById(R.id.btnSavePaiement);

        // 2️⃣ Récupération des données envoyées
        clientId = getIntent().getStringExtra("client_id");
        solde = getIntent().getDoubleExtra("solde", 0);

        // Sécurité
        if (clientId == null) {
            finish();
            return;
        }

        // 3️⃣ Click sur Enregistrer
        btnSave.setOnClickListener(v -> {
            String montantStr = etMontant.getText().toString().trim();

            if (montantStr.isEmpty()) {
                etMontant.setError("Montant requis");
                return;
            }

            double montant = Double.parseDouble(montantStr);

            // 🚫 BLOQUAGE paiement > dette
            if (montant > solde) {
                Toast.makeText(this,
                        "Le paiement dépasse la dette restante",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            savePaiement(montant);
        });
    }
    private void savePaiement(double montant) {
        SupabaseService.getInstance()
                .insertPaiement(clientId, montant)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddPaiementActivity.this,
                                    "Paiement enregistré",
                                    Toast.LENGTH_SHORT).show();
                            finish(); // ⬅️ retourne au détail client
                        } else {
                            Toast.makeText(AddPaiementActivity.this,
                                    "Erreur lors de l'enregistrement",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AddPaiementActivity.this,
                                "Erreur réseau",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

