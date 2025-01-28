package src.com.client_java;

public class Etudiant {
    private int id_etudiant;
    private String nom_etudiant;

    public Etudiant(int id_etudiant, String nom_etudiant) {
        this.id_etudiant = id_etudiant;
        this.nom_etudiant = nom_etudiant;
    }

    public int getIdEtudiant() {
        return id_etudiant;
    }

    public String getNomEtudiant() {
        return nom_etudiant;
    }

    @Override
    public String toString() {
        return "Name = " + nom_etudiant + " | ID [" + id_etudiant + "]";
    }

}