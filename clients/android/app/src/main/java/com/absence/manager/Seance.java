package com.absence.manager;

import java.util.ArrayList;
import java.util.List;


public class Seance {
    private int id_seance;
    private String nom_seance;
    private List<Etudiant> list_etudiant;
    private List<Absence> list_absent;
    private int unixTime;

    public Seance(int id_seance, String nom_seance, int unixTime) {
        this.id_seance = id_seance;
        this.nom_seance = nom_seance;
        this.list_etudiant = new ArrayList<>();
        this.list_absent = new ArrayList<>();
        this.unixTime = unixTime;

    }

    public int getUnixTime() {
        return unixTime;
    }

    public int getIdSeance() {
        return id_seance;
    }

    public String getNomSeance() {
        return nom_seance;
    }

    public List<Etudiant> getListEtudiant() {
        return list_etudiant;
    }

    public void ajouterEtudiant(Etudiant etudiant) {
        list_etudiant.add(etudiant);
    }

    public void setAbsence(int id_etudiant, int presence) {
        boolean found = false;
        for (int i = 0; i < list_absent.size(); i++) {
            Absence absence = list_absent.get(i);
            if (absence.getIdEtudiant() == id_etudiant) {
                list_absent.set(i, new Absence(id_seance, id_etudiant, presence));

                found = true;
                break;
            }
        }
        if (!found) {
            list_absent.add(new Absence(id_seance, id_etudiant, presence));
        }
    }
    public List<Absence> getListAbsent() {
        return list_absent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSeance: ").append(nom_seance).append(" [ID - ").append(id_seance).append("]\n");

        for (Etudiant etudiant : list_etudiant) {
            sb.append("Etudiant: ").append(etudiant.getNomEtudiant()).append(" - Présence: ");
            boolean found = false;
            for (Absence absence : list_absent) {
                if (absence.getIdEtudiant() == etudiant.getIdEtudiant()) {
                    sb.append(absence.getPresence() == 0 ? "Absent" : "Présent");
                    found = true;
                    break;
                }
            }
            if (!found) {
                sb.append("Non marqué");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}