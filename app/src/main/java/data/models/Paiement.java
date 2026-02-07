package data.models;

public class Paiement {

    private String id;
    private String client_id;
    private double montant;
    private String created_at;

    // Obligatoire pour Gson
    public Paiement() {}

    public String getId() {
        return id;
    }

    public String getClient_id() {
        return client_id;
    }

    public double getMontant() {
        return montant;
    }

    public String getCreated_at() {
        return created_at;
    }
}
