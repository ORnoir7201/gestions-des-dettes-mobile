package ui.client;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.or.bf.carnetdettes.R;

import java.util.List;

import data.models.Client;
import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditClientActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etTelephone, etAdresse;
    private Button btnSave, btnCancel;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        // Initialiser les vues
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etAdresse = findViewById(R.id.etAdresse);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Récupérer l'ID du client
        clientId = getIntent().getStringExtra("client_id");
        if (clientId == null) {
            Toast.makeText(this, "Client non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Charger les informations du client
        loadClientInfo();

        // Bouton Sauvegarder
        btnSave.setOnClickListener(v -> saveClient());

        // Bouton Annuler
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadClientInfo() {
        SupabaseService.getInstance().fetchClientById(clientId)
                .enqueue(new Callback<List<Client>>() {
                    @Override
                    public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Client client = response.body().get(0);
                            etNom.setText(client.getNom());
                            etPrenom.setText(client.getPrenom());
                            etTelephone.setText(client.getTelephone());
                            etAdresse.setText(client.getAdresse());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Client>> call, Throwable t) {
                        Toast.makeText(EditClientActivity.this,
                                "Erreur de chargement", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveClient() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty()) {
            Toast.makeText(this, "Nom et prénom sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseService.getInstance().updateClient(clientId, nom, prenom, telephone, adresse)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditClientActivity.this,
                                    "Client modifié avec succès", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(EditClientActivity.this,
                                    "Erreur de modification", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(EditClientActivity.this,
                                "Erreur réseau", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}