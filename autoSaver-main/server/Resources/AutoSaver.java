
package Resources;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.*;
import java.nio.file.*;
import java.time.Instant;
import java.nio.file.attribute.*;
import java.lang.Integer;
import java . io .*;


public class AutoSaver{
    long sourceLastModified; 
    Personne user; 
    String dirName; 
    BufferedReader sisr; 
    PrintWriter sisw ; 
    ObjectOutputStream outputStream; 
    ObjectInputStream inputStream;
    Socket socket; 
    String requete;
    int port=8081;
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
 public  void firstUpload(String destDir) { 
 try{
  dataAccess.receiveFileOrDirectory(destDir, inputStream); 
   } catch (Exception e) {
                e.printStackTrace();
    }
 }
public void demarrer()
{
	    try{ 
	    while (!( requete = sisr . readLine () ) . equals ( "END" ) ) {
               if(requete.equals("envoi src"))
               {

                dirName= sisr.readLine();
	        File destinationDir = new File("destination/"+user.getNom()+"/"+dirName);
	        if (!destinationDir.exists()) 
           	 {

           	     sisw.println("Nexist");  
           	     String freq = sisr.readLine();  
           	     destinationDir.mkdir();
           	     ServerSocket serveur = new ServerSocket (port) ;
           	     ui.ecrireChaine("attente...\n");
           	     Saver s=new Saver(user,destinationDir,freq, serveur.accept(),true);
           	     ui.ecrireChaine("connection succed\n"); 
           	     s.start();
           	 }
           	 else
           	 {  
           	    sisw.println("exist");  
           	    String freq = sisr.readLine();  
           	     ServerSocket serveur = new ServerSocket (port) ;
           	     ui.ecrireChaine("attente...\n");
           	     Saver s=new Saver(user,destinationDir,freq, serveur.accept(),false);
           	     ui.ecrireChaine("connection succed\n"); 
           	     s.start();
           	 }
	        }
	      }
	    } catch (Exception e) {e.printStackTrace();}
   }
}
