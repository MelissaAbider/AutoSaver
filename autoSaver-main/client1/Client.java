package Resources;
import java.io.*;
import java.net.*;
import Resources.Session;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(args[0], Integer.parseInt(args[1])); // args[0] et args[1] retournent respectivement l'adresse hote et le port qui sont fournit aut autoCompiler.sh
        System.out.println("SOCKET = " + socket);
        BufferedReader sisr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter sisw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        Connexion c= new Connexion(); 
        Personne user=null ; 
	// Créer une instance de Scanner pour lire l'entrée utilisateur
        String rep = c.signUpOrlogin(); 
        if(rep.equals("1"))
        {   
        	user=c.seConnecter(sisw,sisr, outputStream, inputStream); 	// retourne l'utilisateur qui est connecté ou null 	
        }
        else 
        if(rep.equals("2"))
        {
	       c.creeCompte(sisw,sisr,outputStream, inputStream);  
        }
         if(user!=null)
         {
           Session s= new Session(user,sisw,sisr, outputStream, inputStream); // demarre une session avec user 
           s.addSocket(socket); 
           s.start(); 
         }
        sisw.println("END"); // Message de fermeture
        sisr.close();
        sisw.close();
        socket.close();
    }
}

