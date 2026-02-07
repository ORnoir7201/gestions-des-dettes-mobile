package data.models;

public class HistoriqueItem {

    private String clientNom;
    private String clientPrenom;
    private String type; // "DETTE" ou "PAIEMENT"
    private String description;
    private double montant;
    private String date;

    public HistoriqueItem(String clientNom, String clientPrenom,
                          String type, String description,
                          double montant, String date) {
        this.clientNom = clientNom;
        this.clientPrenom = clientPrenom;
        this.type = type;
        this.description = description;
        this.montant = montant;
        this.date = date;
    }

    public String getClientNom() { return clientNom; }
    public String getClientPrenom() { return clientPrenom; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public double getMontant() { return montant; }
    public String getDate() { return date; }
}
