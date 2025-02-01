package com.client_java;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Date;

public class Seance {
    private int id_seance;
    private String nom_seance;
    private List<Etudiant> list_etudiant;
    private List<Absence> list_absent;
    private Long unixTime;

    public Seance(int id_seance, String nom_seance, Long unixTime) {
        this.id_seance = id_seance;
        this.nom_seance = nom_seance;
        this.list_etudiant = new ArrayList<>();
        this.list_absent = new ArrayList<>();
        this.unixTime = unixTime;
       
    }

    public Long getUnixTime() {
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

    public void supprimerEtudiant(int etudiantId) {
        for (Etudiant etudiant : list_etudiant) {
            if (etudiant.getIdEtudiant() == etudiantId) {
                list_etudiant.remove(etudiant);
                break;
            }
        }
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
        // Convert Unix time to readable date and time
        System.out.println(unixTime);

        Date date = new Date(unixTime * 1000); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);

        StringBuilder sb = new StringBuilder();
        sb.append("\nSeance: ").append(nom_seance).append(" [ID ").append(id_seance).append("] - [").append(formattedDate).append("]\n");
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