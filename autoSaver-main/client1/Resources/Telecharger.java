package Resources;
import java.io.*; 
import java.nio.*;

public class Telecharger
{
       private Personne user; 
       private Consultation c;
       private String repertoire;
       String requete;
       BufferedReader sisr; 
       PrintWriter sisw ; 
       ObjectOutputStream outputStream; 
       ObjectInputStream inputStream; 
       InterfaceUtilisateur ui = new InterfaceUtilisateur();
       AccesDonnees dataAccess; 
       public Telecharger(Personne user,PrintWriter sisw, BufferedReader sisr,ObjectOutputStream outputStream,ObjectInputStream inputStream)
       {
          this.sisr=sisr; 
          this.sisw=sisw ; 
          this.outputStream=outputStream; 
          this.inputStream=inputStream;
          this.user=user; 
          c=new Consultation(user,sisw,sisr, outputStream, inputStream); 
          repertoire= user.getNom(); 
          this.dataAccess= new AccesDonnees( user, sisw,  sisr, outputStream, inputStream);
       }
public void start(){
       sisw.println("telecharger"); 
      try{
       while (!( requete = sisr . readLine () ).equals ("EOF") ) { ui.ecrireChaine(requete+"\n"); };         // affiche le contenue du repertoire
       do {
             do{ // contrôle saisie 
      		requete = ui.lireChaine(" entre p pour parcourir   c pour telecharger ou s pour sortir :\n\n");
      	     }while(!requete.equals("p") && !requete.equals("c") &&!requete.equals("s")); 

      	     sisw.println(requete);
      	     if(requete.equals("p")){
      	      while (!( requete = sisr . readLine () ) . equals ("le parcours est fini") ) {
                 if(requete.equals("Donne le nom du repertoire à consulter :")){
                   requete = ui.lireChaine(requete+"\n\n");
                   sisw.println(requete); 
                 }
                 else
                 ui.ecrireChaine(requete+"\n"); // on affice les fichier parcourus 
               } 
              }
              if(requete.equals("c")){
       		String fileName = ui.lireChaine("entrez le nom du fichier à copier\n");
       		sisw.println(fileName);
       		String reponse="";
       		try{
       		      if((reponse=sisr.readLine()).equals("ok"))
                    dataAccess.receiveFileOrDirectory("sources/telechargement",inputStream);
                else{
                    ui.ecrireChaine(reponse+"\n");
                    }
                }catch(Exception e){ e.printStackTrace(); }
              }
    	} while (!requete.equalsIgnoreCase("s"));
  }catch(Exception e){ e.printStackTrace(); }
 }
}


