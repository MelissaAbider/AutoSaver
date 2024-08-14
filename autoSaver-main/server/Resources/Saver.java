package Resources;

import java.io.FileInputStream; // pr la lecture de flux en entrée   // permetent de manipuler des données brutes donc on peut lire tout type de fichier 
import java.io.FileOutputStream;//       l'eciture       en sortie,,//
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.net.*;
import java.util.zip.*;
import java.io.Serializable;
import java.util.Hashtable;

public class Saver extends Thread {
    private Personne user;
    private double frequence;
    private File dst;
    Socket s;
    boolean first;
    BufferedReader sisr;
    PrintWriter sisw;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    String requete;
    int cpt = 0;
    InterfaceUtilisateur ui = new InterfaceUtilisateur();
    AccesDonnees dataAccess;

    public Saver(Personne user, File dst, String frequence, Socket s, boolean first) throws Exception {
        this.user = user;
        this.frequence = frequence.equals("Q") ? 0.2 : 1; // opérateur ternaire
        this.dst = dst;
        this.s = s;
        this.first = first;
        sisr = new BufferedReader(new InputStreamReader(s.getInputStream()));
        sisw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
        outputStream = new ObjectOutputStream(s.getOutputStream());
        inputStream = new ObjectInputStream(s.getInputStream());
        dataAccess = new AccesDonnees(user, sisw, sisr, outputStream, inputStream);
    }

    public void run() {
        try {
            if (first) {
                ui.ecrireChaine("firsUpload " + s + "\n");
                File f = new File("destination/" + user.getNom() + "/" + dst.getName());
                File zipf = new File(
                        "destination/" + user.getNom() + "/" + dst.getName() + "/" + dst.getName() + ".zip");
                dataAccess.receiveFileOrDirectory("destination/" + user.getNom() + "/" + dst.getName(), inputStream);
                dataAccess.unzip(zipf, f);
                // Suppression du fichier zip après décompression et creation du fileInfo.ser
                dataAccess.others(zipf, dst);
                first = false;

                while (true) {
                    // en fait une copie différentielle
                    sisw.println("copie differentielle");
                    ui.ecrireChaine("debut cp\n");
                    dataAccess.copieDifferentielle(this.dst);
                    ui.ecrireChaine("fin cp\n");
                    try {
                        sleep((long) (this.frequence * 60000)); // le temps durant lequel le serveur ne fait rien en
                                                                // fonction de la fréquence
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String dstName = dataAccess.chercherSource("InfoFics/" + user.getNom() + "/");
                dst = new File("destination/" + user.getNom() + "/" + dstName);
                while (true) {
                    sisw.println("copie differentielle");
                    ui.ecrireChaine("debut cp\n");
                    dataAccess.copieDifferentielle(this.dst);
                    ui.ecrireChaine("fin cp\n");
                    try {
                        sleep((long) (this.frequence * 60000)); // le temps durant lequel le serveur ne fait rien en
                                                                // fonction de la fréquence
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zip(String path) {

    }
}
