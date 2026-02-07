package ui.client;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.or.bf.carnetdettes.R;
import com.google.android.material.textfield.TextInputEditText;

import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddClientActivity extends AppCompatActivity {

    private TextInputEditText etNom, etPrenom, etTelephone, etAdresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etAdresse = findViewById(R.id.etAdresse);
        Button btnSave = findViewById(R.id.btnSaveClient);

        btnSave.setOnClickListener(v -> saveClient());
    }

    private void saveClient() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseService.getInstance()
                .insertClient(nom, prenom, telephone, adresse)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddClientActivity.this,
                                    "Client ajouté avec succès", Toast.LENGTH_SHORT).show();
                            finish(); // retour au dashboard
                        } else {
                            Toast.makeText(AddClientActivity.this,
                                    "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AddClientActivity.this,
                                "Erreur réseau", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
