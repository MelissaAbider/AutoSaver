package Resources;
import java.net.SocketPermission;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java . io .*;

public class Consultation {
  private Personne user;
  private String repertoire;
   BufferedReader sisr; 
   PrintWriter sisw ; 
   ObjectOutputStream outputStream; 
   ObjectInputStream inputStream;
   InterfaceUtilisateur ui = new InterfaceUtilisateur();
  public Consultation(Personne user,PrintWriter sisw, BufferedReader sisr,ObjectOutputStream outputStream,ObjectInputStream inputStream)
  {
   this.sisr=sisr; 
   this.sisw=sisw ; 
   this.outputStream=outputStream; 
   this.inputStream=inputStream;
   this.user = user;
  }
  public void consulter() {
  ui.ecrireChaine("je suis consultation\n"); 
  String requete=""; 
  sisw.println("consulter"); 
  // affiche le contenue du repertoire
  try{
  while (!( requete = sisr . readLine () ).equals ("EOF")) {
   ui.ecrireChaine(requete+"\n"); 
     }
  }
  catch(Exception e){ }
  try{
   while (!( requete = sisr . readLine () ) . equals ( "le parcours est fini" ) ) {
    if(requete.equals("rep")){
      repertoire = ui.lireChaine("Donne le nom du repertoire à consulter ou le mot q pour quitter :\n");
      sisw.println(repertoire); 
      }
      else{
        ui.ecrireChaine(requete+"\n"); // on affice les fichier parcourus 
        }
  }
  }catch(Exception e){ e.printStackTrace(); }
  ui.ecrireChaine("le parcours est fini \n");
  }
}
