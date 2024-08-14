package Resources; 
import java . io .*;
import java . net .*;
public class Session
{
  private Personne user;
   BufferedReader sisr; 
   PrintWriter sisw ; 
   ObjectOutputStream outputStream; 
   ObjectInputStream inputStream;
   Socket socket; 
  public Session(Personne user,PrintWriter sisw, BufferedReader sisr,ObjectOutputStream outputStream,ObjectInputStream inputStream)
  {
   this.sisr=sisr; 
   this.sisw=sisw ; 
   this.outputStream=outputStream; 
   this.inputStream=inputStream;
   this.user= user;
  }
  public void addSocket(Socket socket)
  { this.socket=socket;}
  public void start()
  {
   InterfaceUtilisateur ui = new InterfaceUtilisateur();
   String rep="1"; 

   ui.ecrireChaine("session demarré pour "+user+"\n");
      do{
   ui.ecrireChaine("Que voulez vous faire ? \n");
   ui.ecrireChaine("Pour consulter vos fichiers  tapez 1 \n");
   ui.ecrireChaine("Pour ajouter un repertoire à sauvegarder tapez 2 \n");
   ui.ecrireChaine("Pour pour télécharger un fichier  tapez 3 \n");
   ui.ecrireChaine("Pour pour quitter  tapez 4 \n");
   do{ rep = ui.lireChaine(); }while(!rep.equals("1") && !rep.equals("2") &&!rep.equals("3")&&!rep.equals("4")); 
   switch(rep)
   {
      case "1":
            Consultation c= new Consultation(user,sisw,sisr, outputStream, inputStream);
            c.consulter(); 
      break;
      case "2":
            AutoSaver as = new AutoSaver(user,sisw,sisr, outputStream, inputStream);
            as.addSocket(this.socket); 
            as.demarrer();
      break;
      case"3":
            Telecharger t= new Telecharger(user,sisw,sisr, outputStream, inputStream);
            t.start(); 
      default:// on quitte  
    }
    }while(!rep.equals("4")); 
   }
}
