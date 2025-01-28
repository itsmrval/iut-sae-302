package src.com.client_java;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.util.ArrayList;
import java.util.List;


public class Client {
    private SSLSocket sockfd;
    private DataOutputStream enSortie;
    private DataInputStream enEntree;
    private String serverIP;
    private int serverPort;

    public boolean connectToServer(String hote, int port) {
        try {

            // Load the certificate
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null); // Initialize an empty KeyStore

            String certPath = new File("ssl" + File.separator + "cert.pem").getCanonicalPath();
            System.out.println("[INFO] Loading certificate from: " + certPath);

            try (FileInputStream fis = new FileInputStream(certPath)) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);
                keyStore.setCertificateEntry("cert", cert);
            }



            // Initialize the TrustManagerFactory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Initialize the SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Create an SSLSocketFactory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Create an SSLSocket
            sockfd = (SSLSocket) sslSocketFactory.createSocket(hote, port);
            enSortie = new DataOutputStream(sockfd.getOutputStream());
            enEntree = new DataInputStream(sockfd.getInputStream());
            serverIP = hote;
            serverPort = port;
            System.out.println("[INFO] Connected to the server | " + hote + ":" + port + "\n");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendCommand(String command) throws IOException {
        byte[] buffer = command.getBytes();
        enSortie.write(buffer, 0, buffer.length);
    }

    public List<Seance> getSeances() {
        List<Seance> seances = new ArrayList<>();
        try {
            sendCommand("<g>seances");
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            if (n <= 5) {
                System.out.println("No seances available");
            } else {
                String seancesStr = new String(buffer, 0, n).substring(4);
                seancesStr = seancesStr.substring(0, seancesStr.length() - 1);
                String[] seancesArray = seancesStr.split(";");

                for (String seanceStr : seancesArray) {
                    String[] parts = seanceStr.split(",");
                    int id_seance = Integer.parseInt(parts[0].split(":")[1]);
                    String nom_seance = parts[1].split(":")[1];
                    int unixTime = Integer.parseInt(parts[2].split(":")[1]);
                    Seance seance = new Seance(id_seance, nom_seance, unixTime);
                    seances.add(seance);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seances;
    }

    public List<Etudiant> getStudents() {
        List<Etudiant> students = new ArrayList<>();
        try {
            sendCommand("<g>students");
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            if (n <= 5) {
                System.out.println("No students available");
            } else {
                String studentsStr = new String(buffer, 0, n).substring(4);
                studentsStr = studentsStr.substring(0, studentsStr.length() - 1);
                String[] studentsArray = studentsStr.split(";");
    
                for (String studentStr : studentsArray) {
                    String[] parts = studentStr.split(",");
                    if (parts.length >= 2) { // Vérifie que les données contiennent au moins deux parties (id et nom)
                        int id_student = Integer.parseInt(parts[0].split(":")[1]);
                        String nom_student = parts[1].split(":")[1];
                        Etudiant etudiant = new Etudiant(id_student, nom_student);
                        students.add(etudiant);
                    } else {
                        System.out.println("Invalid student data: " + studentStr);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean createSeance(String name, long unixTime) {
        try {
            sendCommand("<p>seance/" + name + "/" + unixTime);
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            String response = new String(buffer, 0, n);

            if (response.startsWith("202/")) {
                System.out.println("\n[INFO] - Seance '" + name + "' created successfully.");
                return true;
            } else {
                System.out.println("Failed to create student. Server response: " + response);
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteSeance(int idSeance) {
        try {
            sendCommand("<d>seance/" + idSeance);
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            String response = new String(buffer, 0, n);

            if (response.startsWith("202/")) {
                System.out.println("\n[INFO] - Seance '" + idSeance + "' deleted successfully.");
            } else {
                System.out.println("Failed to create student. Server response: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createStudent(String name) {
        try {
            sendCommand("<p>student/" + name);
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            String response = new String(buffer, 0, n);

            if (response.startsWith("202/")) {
                System.out.println("\n[INFO] - Seance '" + name + "' created successfully.");
                return true;
            } else {
                System.out.println("Failed to create student. Server response: " + response);
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteStudent(int idStudent) {
        try {
            sendCommand("<d>student/" + idStudent);
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            String response = new String(buffer, 0, n);

            if (response.startsWith("202/")) {
                System.out.println("\n[INFO] - Seance '" + idStudent + "' deleted successfully.");
            } else {
                System.out.println("Failed to create student. Server response: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAbsence(int idSeance, int idEtudiant, int statut) {
        try {
            sendCommand("<p>attendance/" + idSeance + "/" + idEtudiant + "/" + statut);
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            String response = new String(buffer, 0, n);

            if (response.startsWith("202/OK")) {
                System.out.println("\n[INFO] - Absence recorded successfully.");
            } else {
                System.out.println("Failed to record absence. Server response: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAttendanceStudent(int idSeance, int idEtudiant) {
        try {
            sendCommand("<g>attendance/" + idSeance + "/" + idEtudiant);
            byte[] buffer = new byte[1024];
            int n = enEntree.read(buffer, 0, 1023);
            String response = new String(buffer, 0, n);
            if (response.startsWith("202")) {
                String[] parts = response.split(",");
                for (String part : parts) {
                    if (part.startsWith("status:")) {
                        return Integer.parseInt(part.split(":")[1]);
                    }
                }
            } else {
                System.out.println("Failed to get absence. Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // Return a default value indicating an error
    }

    public void closeResources() {
        try {
            if (enSortie != null) enSortie.close();
            if (enEntree != null) enEntree.close();
            if (sockfd != null) sockfd.close();
            System.out.println("[INFO] - Disconnected from the server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }
}