package Resources; 
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.net.*;
import java.nio.*;
import java.time.Instant;
import java .io.*;

public class AccesDonnees
{
    Personne user; 
    BufferedReader sisr; 
    PrintWriter sisw ; 
    ObjectOutputStream outputStream; 
    ObjectInputStream inputStream;
    int port =8081;
    String host="127.0.0.0"; 
    InterfaceUtilisateur ui = new InterfaceUtilisateur();
    FileData data; 
    private String repertoire;
    private File src;
    Hashtable<String, Long> Hashtable; 
 public AccesDonnees(Personne user,PrintWriter sisw, BufferedReader sisr,ObjectOutputStream outputStream,ObjectInputStream inputStream)
 {
   this.sisr=sisr; 
   this.sisw=sisw ; 
   this.outputStream=outputStream; 
   this.inputStream=inputStream;
   this.user = user;
   data= new FileData(); 
 }
 
  public void sendFileOrDirectory(File file, ObjectOutputStream objectOutputStream) throws IOException {
        if (file.isDirectory()) {
            // Si c'est un répertoire, zippez-le et envoyez le fichier zip
            File zipFile = new File(file.getParentFile(), file.getName() + ".zip");
            zipDirectory(file, zipFile);
            sendFile(zipFile, objectOutputStream);
            // Supprimez le fichier zip après l'envoi
            zipFile.delete();
        } else {
            sendFile(file, objectOutputStream);
        }
    }
    
  private void sendFile(File file, ObjectOutputStream objectOutputStream) throws IOException {
        // Envoyez le nom du fichier
        objectOutputStream.writeObject(file.getName());
        // Envoyez le contenu du fichier
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                objectOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }
      private void zipDirectory(File directory, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zip(directory, directory, zos);
        }
    }
    private void zip(File rootDirectory, File directory, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[1024];
        int bytesRead;
        for (File file : files) {
            if (file.isDirectory()) {
                zip(rootDirectory, file, zos);
            } else {
                String relativePath = rootDirectory.toURI().relativize(file.toURI()).getPath();
                ZipEntry entry = new ZipEntry(relativePath);
                zos.putNextEntry(entry);
                try (FileInputStream fis = new FileInputStream(file)) {
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }
                zos.closeEntry();
            }
        }
    }
    public void receiveFileOrDirectory(String directoryPath, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        while (true) {
            // Recevoir le nom du fichier ou du répertoire depuis le serveur
            String fileName = (String) objectInputStream.readObject();
            if (fileName == null)
                break; // Fin du répertoire         
            // Si le nom de fichier contient un chemin, assurez-vous que les dossiers sont créés
            String filePath = directoryPath + File.separator + fileName;
            if (fileName.endsWith(File.separator)) {
                // Si c'est un répertoire, créez-le
                new File(filePath).mkdirs();
                receiveFileOrDirectory(filePath,inputStream);
            } else {
                // Si c'est un fichier, créez-le et copiez son contenu depuis le flux
                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = objectInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
   } 
public void parcourir(String chemin) {
    String nom_rep = user.getNom();
    File chemin_fichier = new File(chemin);
    if (chemin_fichier.isDirectory()) {
      File[] files = chemin_fichier.listFiles();
      if (files.length > 0) {
        ui.ecrireChaine("Contenu du repertoire " + repertoire + ":\n");
        for (int i = 0; i < files.length; i++) {
          if (files[i].isDirectory()) {// si cest un repertoire
            ui.ecrireChaine(files[i].getName() + " -un repertoire\n");
          } else if (files[i].isFile()) {// si cest un fichier
            ui.ecrireChaine(files[i].getName() + " -un fichier\n");
          }
        }
      } else { // Si le répertoire est vide
        ui.ecrireChaine("Le répertoire est vide.\n");
      }
    } else {
      ui.ecrireChaine("Le chemin spécifié ne correspond pas à un répertoire.\n");
    }
  }
  
  //********************ecrit dans .ser*******   
public  void writeFileInfos(File directory) throws IOException {  
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                writeFileInfos(file); // Appel récursif pour les sous-répertoires
            } else {
                sendFileInfos(file);
            }
        }
    }
private  void sendFileInfos(File file) throws IOException {
        String relativePath = getRelativePath(file);
        long lastModified = file.lastModified();
        data.setFileData(relativePath,lastModified); // ecrit le chemin relatif et la date de la dernière modification
    }
private  String getRelativePath(File file) {
        String destPath = new File("sources").getAbsolutePath();
        String filePath = file.getAbsolutePath();
        return filePath.substring(destPath.length() + 1); // retourne le chemin sans    chaine avant rep courant + antislash 
    }
    

public void  copieDifferentielle(File dst) throws Exception
    {        
            writeFileInfos(dst);   // on reecrit les infofichiers dans data (1°)
            ecrireDansSer(); //on mais à jours le .ser
            boolean ok=false; 
            Hashtable= receiveSer();  // on reçoi l'ancien hashtable du serveur 
            
            for (String relativePath :Hashtable.keySet()) {
                long lastModified = (long)Hashtable.get(relativePath); //On recupère la date 
                 File sourceFile = new File("sources", relativePath);// recupère le fichier locale corespondant au chemin 
                if( sourceFile.exists() && sourceFile.lastModified() != lastModified ){
                    ok=true; 
                    // on met à jour la date du fichier et on renvoie à la fin  fileHashtable
                    Hashtable.put(relativePath,sourceFile.lastModified()); 
                    sisw.println("envoie"); 
		    sendFileOrDirectory(sourceFile, outputStream); 
		    outputStream.writeObject(null);
                }   
            }       
            // on verifi s'il y a de nouveau fichier 
            //writeFileInfos(dst);   // on reecrit les infofichiers  
            //jusqu'ici Hashtable est eventuellement mis à jours sur les dates mais pas les nouveaux fichiers 
              Hashtable<String, Long> fileHashtable2 = data.getFileHashtable(); //fileHashtable2 contient toutes les mis à jours 
              for (String relativePath : fileHashtable2.keySet()) {
                   if (!Hashtable.containsKey(relativePath)) { // un nouveau fichier 
                   ok=true; 
                   long lastModified = (long) fileHashtable2.get(relativePath); //On recupère la date 
                   Hashtable.put(relativePath,lastModified); // ajout des infos du nouveau fichier           
                    File sourceFile = new File("sources", relativePath);// recupère le fichier à envoyer
                    sisw.println("envoie"); 
		    sendFileOrDirectory(sourceFile, outputStream); 
		    outputStream.writeObject(null);
                }
            }
            // à la fin Hashtable et fileHashtable contiennent les même données 
           sisw.println("FinCopie"); 
           if(ok) // soit un fichier a été modifié soit un nouveau fichier est ajouté 
           {
// envoi du hashtable 
           sendSer(outputStream); 
// fin envoi
        }
    }
    
public   synchronized  void sendSer( ObjectOutputStream objectOutputStream)
  {
     String requete; 
   try {
   while(!(requete=sisr.readLine()).equals("envoiH")){ }
   System.out.println(requete); 
     FileInputStream fileIn = new FileInputStream("InfoFics/"+src.getName()+".ser");
      ObjectInputStream instream = new ObjectInputStream(fileIn); 
      FileData dat= (FileData)instream.readObject();
      //Hashtable<String, Long> fileHashtable= data.getFileHashtable(); // on peut lire dans data car (1°)
      outputStream.writeObject(dat.getFileHashtable()); // ou envoyé Hashtable directement
      ui.ecrireChaine("write   "+dat.getFileHashtable()+"\n ou\n"+Hashtable); 
} catch (Exception e) {
    e.printStackTrace();
}
  }
private synchronized Hashtable<String, Long>  receiveSer()  {
Hashtable<String, Long> receivedHashtable = new Hashtable<String, Long>(); 
 try {
    // Lire le Hashtable sérialisé
Object obj = inputStream.readObject();							
if (obj instanceof Hashtable<?, ?>) {
    receivedHashtable = (Hashtable<String, Long>) obj;
} else {

    System.out.println("n'est pas un hashtable "+obj);
     
}                			   
} catch (Exception  e) {
    e.printStackTrace();
}
 return receivedHashtable ; 
}
public void ecrireDansSer()
{
     try {
      System.out.println(src); 
      FileOutputStream fileOut = new FileOutputStream("InfoFics/"+src.getName()+".ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut); 
      System.out.println(data); 
      out.writeObject(data);
      }catch(Exception e){ e.printStackTrace(); }
   }
   
public void setSrc(File src)
{
this.src= src;
} 


public String chercherSource(String path) {
        // Créer un objet File à partir du chemin donné
        File directory = new File(path);

        // Vérifier si le répertoire existe
        if (directory.isDirectory()) {
            // Parcourir les fichiers du répertoire
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Vérifier si le fichier est un fichier .ser
                    if (file.isFile() && file.getName().endsWith(".ser")) {
                        // Extraire le nom du fichier sans extension et le retourner
                        String fileName = file.getName();
                        int lastIndex = fileName.lastIndexOf('.');
                        if (lastIndex != -1) {
                            return fileName.substring(0, lastIndex);
                        } else {
                            return fileName;
                        }
                    }
                }
            }
        }
        // Aucun fichier .ser trouvé dans le répertoire
        return null;
    } 
}


// Classe pour encapsuler les informations sur le fichier
class FileData implements Serializable {
    // Hashtable pour stocker les informations sur les fichiers
    private Hashtable<String, Long> fileHashtable ;
    public FileData()
    {
    fileHashtable = new Hashtable<>();
    }
    public FileData(Hashtable<String, Long> fileHashtable) {
    this.fileHashtable=fileHashtable; 
    }
    public void setFileData(String fileName, long lastModified) {
        fileHashtable.put(fileName, lastModified);
    }
    public  Hashtable<String, Long> getFileHashtable() {
        return fileHashtable;
    }
}
