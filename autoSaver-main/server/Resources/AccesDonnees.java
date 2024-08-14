package Resources; 
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;
import java.net.*;
import java.nio.*;
import java.time.Instant;
import java .io.*;
import java.text.*;
import java.nio.file.*;




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
    private File dst;
    Hashtable<String, Long> Hashtable; 
    String requete ;
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
            // Si c'est un fichier ordinaire, envoyez simplement son nom et son contenu
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
            System.out.println(fileName); 
            if (fileName == null)
                break; // Fin du répertoire         
            // Si le nom de fichier contient un chemin, assurez-vous que les dossiers sont créés
            String filePath = directoryPath + File.separator + fileName;
            if (fileName.endsWith(File.separator)) {
                // Si c'est un répertoire, créez-le
                new File(filePath).mkdirs();
                receiveFileOrDirectory(filePath,inputStream);
            } else {
               zipOld(new File(filePath)); // zip si le fichier existe déjà
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
    if(!nom_rep.equals("q")){
    File chemin_fichier = new File(chemin);
    // Vérifie que le chemin spécifié correspond à un répertoire
    if (chemin_fichier.isDirectory()) {
      File[] files = chemin_fichier.listFiles();
      if (files.length > 0) {
        sisw.println("Contenu du repertoire " + chemin_fichier.getName() + ":\n");
        for (int i = 0; i < files.length; i++) {
          if (files[i].isDirectory()) {// si cest un repertoire
            sisw.println("   "+files[i].getName() + " :repertoire");
          } else if (files[i].isFile()) {// si cest un fichier
            sisw.println("   "+files[i].getName() + " :fichier");
          }
        }
      } else { // Si le répertoire est vide
        sisw.println("Le répertoire est vide.");
      }

    } else {
      sisw.println("Le chemin spécifié ne correspond pas à un répertoire.");
    }
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
        boolean ok=false; 
          // deserialiser
               FileInputStream fileIn = new FileInputStream("InfoFics/"+user.getNom()+"/"+dst.getName()+".ser");
    		ObjectInputStream in = new ObjectInputStream(fileIn);
    		FileData fd =  (FileData) in.readObject();
    		in.close();
    		fileIn.close();
            Hashtable<String, Long> hashtable = fd.getFileHashtable(); 
      sendSer(hashtable,outputStream);  // envoie de la hashtable 
      while(!(requete=sisr.readLine()).equals("FinCopie")){ // et on reste à l'ecoute 
          if(requete.equals("envoie")){
          ok=true; 
          ui.ecrireChaine("envoie\n"); 
	   receiveFileOrDirectory("destination/" + user.getNom()+"/"+dst.getName(), inputStream);
	    ui.ecrireChaine("fin receive\n"); 
          }
        }
      if(ok){ 
      // on lit et applique les mis à jours eventuelles 
      ui.ecrireChaine("recoie hashtable..\n"); 
      Hashtable<String, Long> fileHashtable =receiveSer(); 
      ui.ecrireChaine("j'ai recu  ..."+fileHashtable); 
      FileData data= new FileData(fileHashtable); 
      try (FileOutputStream fileOut = new FileOutputStream("InfoFics/"+user.getNom()+"/"+dst.getName()+".ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(data);
      }   
     }
    }
    
public void zipOld(File newFile) throws IOException {
        if (newFile.exists()) { // si le fichier existe déjà
            // Obtenir la date actuelle
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String currentDate = dateFormat.format(new Date());

            // Créer un sous-répertoire avec la date actuelle
            Path subdirectory = Paths.get(newFile.getParent(), currentDate);
            Files.createDirectories(subdirectory);

            // Créer un fichier zip dans le sous-répertoire
            Path zipFilePath = Paths.get(subdirectory.toString(), newFile.getName() + ".zip");
            try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                ZipEntry zipEntry = new ZipEntry(newFile.getName());
                zos.putNextEntry(zipEntry);
                
                // Lire le contenu de l'ancien fichier et l'écrire dans le fichier zip
                try (FileInputStream fis = new FileInputStream(newFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }
                
                zos.closeEntry();
            }

        } 
    }
    
private synchronized void sendSer(Hashtable<String, Long> fileHashtable, ObjectOutputStream objectOutputStream)throws Exception
  {
   try {
    // Envoyer le Hashtable sérialisé
    outputStream.writeObject(fileHashtable);
    outputStream.flush(); 
  } catch (IOException e){
    e.printStackTrace();
  }
 }
private synchronized Hashtable<String, Long>  receiveSer() throws Exception {
      sisw.println("envoiH"); 	
  return   (Hashtable<String, Long>) inputStream.readObject();	

}
public void ecrireDansSer()
{
     try {
      FileOutputStream fileOut = new FileOutputStream("InfoFics/"+user.getNom()+"/"+dst.getName()+".ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut); 
      System.out.println(data); 
      out.writeObject(data);
      }catch(Exception e){ e.printStackTrace(); }
   }
   //******************************
 public Personnes DeserialiserPersonne()
 { 
 try{
        	 FileInputStream fileIn = new FileInputStream("Resources/personnes.ser");// lit Personnes.ser
    		 ObjectInputStream inStream = new ObjectInputStream(fileIn);
    		 Personnes p = (Personnes) inStream.readObject();
    		 inStream.close();
    		 fileIn.close();
    		 return p; 
    	}catch(Exception e){}; 
    	return  new Personnes(); 
 }
 
 public void contenue()throws Exception {
    String nom_rep = user.getNom();
    File repertoire_perso = new File("destination/" + nom_rep);// a verifie si cest dans source
    // Vérifie que le chemin spécifié correspond à un répertoire
    if (repertoire_perso.isDirectory()) {
      File[] files = repertoire_perso.listFiles();
      sisw.println("\nContenu du repertoire " + nom_rep + ":\n");
      if (files.length > 0) {
        for (int i = 0; i < files.length; i++) {
          if (files[i].isDirectory()) {// si cest un repertoire
            sisw.println("   "+files[i].getName() + " :repertoire");
          } else if (files[i].isFile()) {// si cest un fichier
            sisw.println("   "+files[i].getName() + " :fichier");
          }
        }
       sisw.println ("EOF"); 
      } else
      {
        sisw.println("Le répertoire est vide.");
        sisw.println ("EOF"); 
      }

    } else {
      sisw.println("Le chemin spécifié ne correspond pas à un répertoire");
    }

  }
 public void serialiserPersonne(Personnes p)throws Exception
 {
 		   File monRep= new File("destination/"+user.getNom()); 
		   monRep.mkdirs(); 
    		   p.ajouterPersonne(user); 
    		   FileOutputStream fileOut = new FileOutputStream("Resources/personnes.ser");// cree Personnes.ser
    		   ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
    		   outStream.writeObject(p); // sérialisation
    		   sisw.println("compte crée avec succès"); 
    		   fileOut.close();
 }
 
 // Méthode pour décompresser un fichier zip dans un répertoire cible     
public  void unzip(File zipFile, File targetDirectory) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry entry;
            byte[] buffer = new byte[1024];
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
 
public void others(File zipf,File dst ) throws Exception
{
      this.dst=dst; 
      zipf.delete();
      File infD= new File("InfoFics/"+user.getNom());// on cree le repertoir pour la première fois 
      infD.mkdirs();  
      Hashtable<String, Long> Hashtable =receiveSer(); 
      FileData data= new FileData(Hashtable); 
      try (FileOutputStream fileOut = new FileOutputStream("InfoFics/"+user.getNom()+"/"+dst.getName()+".ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(data);
     }
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
    private  Hashtable<String, Long> fileHashtable ;
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
