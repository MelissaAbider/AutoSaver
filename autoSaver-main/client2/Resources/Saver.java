package Resources; 
import java.io.*;
import java.util.*;
import java.nio.*;
import java.net.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Saver extends Thread {
	private Personne user; 
	private File src;
        Socket s; 
        boolean first; 
        BufferedReader sisr; 
        PrintWriter sisw ; 
        ObjectOutputStream outputStream; 
        ObjectInputStream inputStream;
        String requete; 
       InterfaceUtilisateur ui = new InterfaceUtilisateur();
       AccesDonnees dataAccess; 
public Saver(Personne user,File src,Socket s,boolean first)throws Exception
      {
        this.user=user; 
	this.src=src;
	this.s=s; 
	this.first=first; 
	 sisr = new BufferedReader(new InputStreamReader(s.getInputStream()));
         sisw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
         outputStream = new ObjectOutputStream(s.getOutputStream());
         inputStream = new ObjectInputStream(s.getInputStream());
         this.dataAccess= new AccesDonnees( user, sisw,  sisr, outputStream, inputStream);
      }
public void run()  {
     try{
      if(first){
      ui.ecrireChaine("firstUpload "+s+" \n");
      dataAccess.sendFileOrDirectory(src,outputStream);
      dataAccess.setSrc(src); 
      outputStream.writeObject(null);
	// debut envoi du .ser
      dataAccess.writeFileInfos(src); 
      dataAccess.ecrireDansSer(); 
      dataAccess.sendSer(outputStream); 
      
      while (true) {
           System.out.println("j'attend... ");
        if((requete=sisr.readLine()).equals("copie differentielle")){
        // en fait une copie différentielle 
          System.out.println("debut cp");
        dataAccess.copieDifferentielle(this.src); 
        System.out.println("fin cp");
        }  
      }
      }else
      {
          String srcName= dataAccess.chercherSource("InfoFics/"); 
          src= new File("sources/"+srcName); 
          dataAccess.setSrc(src); 
          while (true) {
           System.out.println("j'attend... ");
          if((requete=sisr.readLine()).equals("copie differentielle")){
        // en fait une copie différentielle 
          System.out.println("debut cp");
          dataAccess.copieDifferentielle(this.src); 
          System.out.println("fin cp");
        }  
      }  
      }
      } catch (Exception e) {
            e.printStackTrace();
     }
    }
}
 
