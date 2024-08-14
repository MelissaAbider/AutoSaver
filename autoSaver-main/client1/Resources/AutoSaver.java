package Resources;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.net.*;
import java.nio.*;
import java.time.Instant;
import java.lang.Integer;
import java .io.*;

public class AutoSaver{
    long sourceLastModified; 
    Personne user; 
    BufferedReader sisr; 
    PrintWriter sisw ; 
    ObjectOutputStream outputStream; 
    ObjectInputStream inputStream;
    Socket socket;
    int port =8081;
    String host="172.31.18.70"; 
    InterfaceUtilisateur ui = new InterfaceUtilisateur();
    AccesDonnees dataAccess; 
    public AutoSaver(Personne user,PrintWriter sisw, BufferedReader sisr,ObjectOutputStream outputStream,ObjectInputStream inputStream)
     {
   this.sisr=sisr; 
   this.sisw=sisw ; 
   this.outputStream=outputStream; 
   this.inputStream=inputStream;
   this.user = user;
   this.dataAccess= new AccesDonnees( user, sisw,  sisr, outputStream, inputStream);
     }  
  public void addSocket(Socket socket)
  {
  this.socket=socket; 
  }
 public  void firstUpload(File sourceDir)throws IOException {
   dataAccess.sendFileOrDirectory(sourceDir,outputStream); 
   }
public void demarrer()
	{
	    String requete; 
	    sisw.println("AutoSave"); 
	    File srcDire = new File("sources/");
	    File[] files = srcDire.listFiles();
	    for (File file : files)
	       if(file.isDirectory()) ui.ecrireChaine("   - "+file.getName()+"\n"); 
	    String dirName = ui.lireChaine("\n Veillez saisir le repertoire de sauvegarde parmis la liste ci-dessus (sous repertoire de source/)\n");
	    
	    try{
	    File srcDir = new File("sources/"+dirName);
	    // on vérifie s'il existe deja un repertoire sauvegarder en ce nom dans l'espace du client
	    if(srcDir.isDirectory()){// si le repertoire source existe bien
             sisw.println("envoi src"); 
             sisw.println(dirName); 
             requete= sisr.readLine();
             requete= sisr.readLine();

           if (requete.equals("Nexist")){
	        String freq = ui.lireChaine("veillez saisir la frequence de sauvegarde \n Q pour quotidienne\n H pour hebdomadaire\n");  
	        sisw.println(freq); 
	        try { Thread.sleep((long) (4000));  } catch (InterruptedException e) {e.printStackTrace();} // attente de 4 seconde pour que le serveur perpare le socket avant de tenter la connexion 
	        Socket s = new Socket(host,port) ;
           	Saver sa =new Saver(user,srcDir, s,true);
           	sa.start();
	    }   
	   else // requete= exist
           {  
           	String freq = ui.lireChaine("veillez saisir la frequence de sauvegarde \n Q pour quotidienne\n H pour hebdomadaire\n");  
	        sisw.println(freq); 
                ui.ecrireChaine("reprise des sauvegardes\n");
           try { Thread.sleep((long) (4000));  } catch (InterruptedException e) {e.printStackTrace();} // attente de 4 seconde pour que le serveur perpare le socket avant de tenter la connexion   
		Socket s = new Socket(host,port) ;
           	Saver sa =new Saver(user,srcDir, s,false);
           	sa.start();
            }
           sisw.println("END"); 
	  }else
	       ui.ecrireChaine(" le repertoire spécifié n'existe pas , veillez vérifier votre saisie ou creer le repertoire en question \n");   
       } catch (Exception e) {e.printStackTrace();} 
   }
}
