package ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.or.bf.carnetdettes.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etNom, etPrenom, etTelephone, etAdresse;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etAdresse = findViewById(R.id.etAdresse);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Inscription ---
        SupabaseService.getInstance().signUp(email, password, new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {

                    JsonObject body = response.body();

                    String userId = body.getAsJsonObject("user").get("id").getAsString();
                    JsonElement accessTokenElement = body.get("access_token");

                    if (accessTokenElement == null || accessTokenElement.isJsonNull()) {
                        Log.d("SUPABASE_SIGNUP", "Inscription réussie. Veuillez confirmer votre email.");
                        Toast.makeText(RegisterActivity.this, "Inscription réussie. Veuillez confirmer votre email.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String accessToken = accessTokenElement.getAsString();
                    Log.d("SUPABASE_SIGNUP", "USER ID = " + userId);
                    Log.d("SUPABASE_SIGNUP", "ACCESS TOKEN = " + accessToken);

                    // --- Création du profil ---
                    SupabaseService.getInstance().createProfile(
                            accessToken,
                            userId,
                            nom,
                            prenom,
                            telephone,
                            adresse
                    ).enqueue(new Callback<Void>() { // <- on passe le Callback ici
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d("SUPABASE_PROFILE", "Profil créé");
                                Toast.makeText(RegisterActivity.this,
                                        "Compte et profil créés avec succès",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                String errorBodyString = "No error body";
                                try {
                                    if (response.errorBody() != null) {
                                        errorBodyString = response.errorBody().string();
                                    }
                                } catch (IOException e) {
                                    errorBodyString = "Could not read error body: " + e.getMessage();
                                }
                                Log.e("SUPABASE_PROFILE", "Erreur création profil - Code: " + response.code() + ", Body: " + errorBodyString);
                                Toast.makeText(RegisterActivity.this,
                                        "Compte créé mais le profil n'a pas pu être enregistré.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("SUPABASE_PROFILE", "Erreur réseau profil", t);
                            Toast.makeText(RegisterActivity.this,
                                    "Erreur réseau lors de la création du profil",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    String errorBodyString = "No error body";
                    try {
                        if (response.errorBody() != null) {
                            errorBodyString = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorBodyString = "Could not read error body: " + e.getMessage();
                    }
                    Log.e("SUPABASE_SIGNUP", "Erreur inscription - Code: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBodyString);
                    Toast.makeText(RegisterActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SUPABASE_SIGNUP", "Erreur réseau inscription", t);
                Toast.makeText(RegisterActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
