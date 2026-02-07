package ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.or.bf.carnetdettes.R;
import com.google.gson.JsonObject;

import data.remote.SupabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> handleReset());
    }

    private void handleReset() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseService.getInstance().resetPassword(email, new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Email de réinitialisation envoyé",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Échec de l'envoi de l'email",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(
                        ForgotPasswordActivity.this,
                        "Erreur réseau : " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}





