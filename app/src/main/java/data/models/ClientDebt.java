package data.models;

public class ClientDebt {
    private final String nom;
    private final int montant;

    public ClientDebt(String nom, int montant) {
        this.nom = nom;
        this.montant = montant;
    }

    public String getNom() {
        return nom;
    }

    public int getMontant() {
        return montant;
    }
}
