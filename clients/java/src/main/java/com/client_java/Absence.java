package com.client_java;

public class Absence {
    private int id_seance;
    private int id_etudiant;
    private int presence;

    public Absence(int id_seance, int id_etudiant, int presence) {
        this.id_seance = id_seance;
        this.id_etudiant = id_etudiant;
        this.presence = presence;
    }

    public int getIdSeance() {
        return id_seance;
    }

    public int getIdEtudiant() {
        return id_etudiant;
    }

    public int getPresence() {
        return presence;
    }

    @Override
    public String toString() {
        return "Absence{" +
                "id_seance=" + id_seance +
                ", id_etudiant=" + id_etudiant +
                ", presence=" + presence +
                '}';
    }
}