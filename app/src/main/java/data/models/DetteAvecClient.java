package data.models;

public class DetteAvecClient {

    private String description;
    private double montant;
    private String created_at;
    private Client clients;

    public String getDescription() {
        return description;
    }

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
