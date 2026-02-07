package data.models;

public class PaiementAvecClient {

    private double montant;
    private String created_at;

    // jointure Supabase
    private Client clients;

    public double getMontant() {
        return montant;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Client getClients() {
        return clients;
    }
}
