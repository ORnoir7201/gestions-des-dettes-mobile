package data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.List;

import data.models.Client;
import data.models.ClientDebt;
import data.models.Dette;
import data.models.DetteAvecClient;
import data.models.Paiement;
import data.models.PaiementAvecClient;
import data.models.Profile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class SupabaseService {

    private static final String BASE_URL =
            "https://rcncqhkfwkxglecmjarq.supabase.co/";
    private static final String API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJjbmNxaGtmd2t4Z2xlY21qYXJxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njk2OTAwODIsImV4cCI6MjA4NTI2NjA4Mn0.UFMt3IQG0tHDnY1_MJgZY66h76VzZFDuLf-MdDffabc";

    private static final String AUTH =
            "Bearer " + API_KEY;

    private static SupabaseService instance;
    private final SupabaseApi api;

    private SupabaseService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(SupabaseApi.class);
    }

    public static SupabaseService getInstance() {
        if (instance == null) {
            instance = new SupabaseService();
        }
        return instance;
    }

    // ================== API SUPABASE ==================
    interface SupabaseApi {

        @Headers("Content-Type: application/json")
        @POST("auth/v1/signup")
        Call<JsonObject> signUp(
                @Body JsonObject body,
                @Header("apikey") String apiKey
        );

        @Headers("Content-Type: application/json")
        @POST("auth/v1/token?grant_type=password")
        Call<JsonObject> signIn(
                @Body JsonObject body,
                @Header("apikey") String apiKey
        );

        @Headers("Content-Type: application/json")
        @POST("auth/v1/recover")
        Call<JsonObject> resetPassword(
                @Body JsonObject body,
                @Header("apikey") String apiKey
        );

        @Headers({"Content-Type: application/json"})
        @POST("rest/v1/profiles")
        Call<Void> insertProfile(
                @Header("apikey") String apiKey,
                @Header("Authorization") String authorization,
                @Body JsonObject body
        );

        @POST("auth/v1/logout")
        Call<JsonObject> signOut();

        @GET("rest/v1/clients?select=*")
        Call<List<Client>> getClients(@Header("apikey") String apiKey);

        @GET("rest/v1/dettes")
        Call<List<Dette>> getDettesByClient(@Header("apikey") String apiKey,
                                            @Query("client_id") String clientId);

        @Headers("Content-Type: application/json")
        @POST("rest/v1/clients")
        Call<Void> insertClient(@Header("apikey") String apiKey,
                                @Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("rest/v1/dettes")
        Call<Void> insertDette(@Header("apikey") String apiKey,
                               @Body JsonObject body);

        @GET("rest/v1/clients")
        Call<List<Client>> getClientById(@Header("apikey") String apiKey,
                                         @Query("id") String clientId);

        @Headers("Content-Type: application/json")
        @POST("rest/v1/paiements")
        Call<Void> insertPaiement(@Header("apikey") String apiKey,
                                  @Body JsonObject body);

        @GET("rest/v1/paiements")
        Call<List<Paiement>> getPaiementsByClient(@Header("apikey") String apiKey,
                                                  @Query("client_id") String clientId);

        @GET("rest/v1/dettes?order=created_at.desc")
        Call<List<DetteAvecClient>> getAllDettes(@Header("apikey") String apiKey,
                                                 @Header("Authorization") String auth,
                                                 @Query("select") String select);

        @GET("rest/v1/paiements?order=created_at.desc")
        Call<List<PaiementAvecClient>> getAllPaiements(@Header("apikey") String apiKey,
                                                       @Header("Authorization") String auth,
                                                       @Query("select") String select);

        @GET("rest/v1/profiles?select=*")
        Call<List<Profile>> getProfiles(@Header("apikey") String apiKey,
                                        @Header("Authorization") String auth);

        @GET("rest/v1/top_clients_debt")
        Call<List<ClientDebt>> getTopClientsDebt(@Header("apikey") String apiKey,
                                                 @Header("Authorization") String auth);
        @GET("rest/v1/dettes?select=*")
        Call<List<Dette>> getDettes(
                @Header("apikey") String apiKey,
                @Header("Authorization") String auth
        );

        @GET("rest/v1/paiements?select=*")
        Call<List<Paiement>> getPaiements(
                @Header("apikey") String apiKey,
                @Header("Authorization") String auth
        );

        @Headers("Content-Type: application/json")
        @HTTP(method = "DELETE", path = "rest/v1/clients", hasBody = true)
        Call<Void> deleteClient(
                @Header("apikey") String apiKey,
                @Query("id") String clientId
        );

        @Headers("Content-Type: application/json")
        @HTTP(method = "PATCH", path = "rest/v1/clients", hasBody = true)
        Call<Void> updateClient(
                @Header("apikey") String apiKey,
                @Header("Authorization") String auth,
                @Query("id") String clientId,
                @Body JsonObject body
        );

        @Headers("Content-Type: application/json")
        @HTTP(method = "DELETE", path = "rest/v1/dettes", hasBody = true)
        Call<Void> deleteClientDettes(
                @Header("apikey") String apiKey,
                @Header("Authorization") String auth,
                @Query("client_id") String clientId
        );

        @Headers("Content-Type: application/json")
        @HTTP(method = "DELETE", path = "rest/v1/paiements", hasBody = true)
        Call<Void> deleteClientPaiements(
                @Header("apikey") String apiKey,
                @Header("Authorization") String auth,
                @Query("client_id") String clientId
        );

    }

    // ================== SESSION UTILISATEUR ==================
    private static final String PREFS = "USER_SESSION";
    private static final String KEY_EMAIL = "user_email";

    public void saveCurrentUserEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getCurrentUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, null);
    }

    // ================== MÉTHODES UTILES ==================
    public Call<List<Profile>> getCurrentUser() {
        return api.getProfiles(API_KEY, AUTH);
    }

    public Call<List<ClientDebt>> fetchTopClientsDebt() {
        return api.getTopClientsDebt(API_KEY, AUTH);
    }

    public Call<List<Client>> fetchClients() {
        return api.getClients(API_KEY);
    }

    public Call<List<Client>> fetchClientById(String clientId) {
        return api.getClientById(API_KEY, "eq." + clientId);
    }

    public Call<List<Dette>> fetchDettesByClient(String clientId) {
        return api.getDettesByClient(API_KEY, "eq." + clientId);
    }

    public Call<List<Paiement>> fetchPaiementsByClient(String clientId) {
        return api.getPaiementsByClient(API_KEY, "eq." + clientId);
    }

    public Call<List<DetteAvecClient>> fetchAllDettes() {
        return api.getAllDettes(API_KEY, AUTH, "*,clients:client_id(nom,prenom)");
    }

    public Call<List<PaiementAvecClient>> fetchAllPaiements() {
        return api.getAllPaiements(API_KEY, AUTH, "*,clients:client_id(nom,prenom)");
    }
    public Call<List<Dette>> fetchDettes() {
        return api.getDettes(API_KEY, AUTH);
    }

    public Call<List<Paiement>> fetchPaiements() {
        return api.getPaiements(API_KEY, AUTH);
    }
    public Call<Void> deleteClient(String clientId) {
        return api.deleteClient(API_KEY, "eq." + clientId);
    }

    public Call<Void> updateClient(String clientId, String nom, String prenom, String telephone, String adresse) {
        JsonObject body = new JsonObject();
        body.addProperty("nom", nom);
        body.addProperty("prenom", prenom);
        body.addProperty("telephone", telephone);
        body.addProperty("adresse", adresse);
        return api.updateClient(API_KEY, AUTH, "eq." + clientId, body);
    }

    public Call<Void> deleteClientDettes(String clientId) {
        return api.deleteClientDettes(API_KEY, AUTH, "eq." + clientId);
    }

    public Call<Void> deleteClientPaiements(String clientId) {
        return api.deleteClientPaiements(API_KEY, AUTH, "eq." + clientId);
    }



    public Call<Void> insertClient(String nom, String prenom, String telephone, String adresse) {
        JsonObject body = new JsonObject();
        body.addProperty("nom", nom);
        body.addProperty("prenom", prenom);
        body.addProperty("telephone", telephone);
        body.addProperty("adresse", adresse);
        return api.insertClient(API_KEY, body);
    }

    public Call<Void> insertDette(String clientId, int montant, String description) {
        JsonObject body = new JsonObject();
        body.addProperty("client_id", clientId);
        body.addProperty("montant", montant);
        body.addProperty("description", description);
        return api.insertDette(API_KEY, body);
    }

    public Call<Void> insertPaiement(String clientId, double montant) {
        JsonObject body = new JsonObject();
        body.addProperty("client_id", clientId);
        body.addProperty("montant", montant);
        return api.insertPaiement(API_KEY, body);
    }

    public Call<Void> createProfile(String accessToken, String userId,
                                    String nom, String prenom, String telephone, String adresse) {
        JsonObject body = new JsonObject();
        body.addProperty("id", userId);
        body.addProperty("nom", nom);
        body.addProperty("prenom", prenom);
        body.addProperty("telephone", telephone);
        body.addProperty("adresse", adresse);

        String authHeader = "Bearer " + accessToken;
        return api.insertProfile(API_KEY, authHeader, body);
    }

    public void signUp(String email, String password, Callback<JsonObject> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);
        api.signUp(body, API_KEY).enqueue(callback);
    }

    public void signIn(String email, String password, Callback<JsonObject> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);
        api.signIn(body, API_KEY).enqueue(callback);
    }

    public void resetPassword(String email, Callback<JsonObject> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        api.resetPassword(body, API_KEY).enqueue(callback);
    }

    public void signOut(Callback<JsonObject> callback) {
        api.signOut().enqueue(callback);
    }

}
