package ui.dette;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.or.bf.carnetdettes.R;

import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDetteActivity extends AppCompatActivity {

    private EditText etMontant, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dette);

        etMontant = findViewById(R.id.etMontant);
        etDescription = findViewById(R.id.etDescription);
        Button btnSave = findViewById(R.id.btnSaveDette);

        // TEMPORAIRE (on automatisera plus tard)
        String clientId = getIntent().getStringExtra("client_id");

        btnSave.setOnClickListener(v -> {
            int montant = Integer.parseInt(etMontant.getText().toString());
            String description = etDescription.getText().toString();

            SupabaseService.getInstance()
                    .insertDette(clientId, montant, description)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(AddDetteActivity.this,
                                    "Dette ajoutée", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(AddDetteActivity.this,
                                    "Erreur ajout dette", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
}
