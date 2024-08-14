package Resources; 
import java.io.*;
public class Connexion
{
        InterfaceUtilisateur ui = new InterfaceUtilisateur();
public  String[] ControleEtsaisieNomPrenom()
        {
        String nom, prenom; 
        do{
       	 nom = ui.lireChaine("Nom  : ").toUpperCase();// tout est passer en majuscule avant sauvegarde ou recherche 
       	 }while(nom.equals(""));
       	 do{
       	 prenom = ui.lireChaine("Prenom  : ").toUpperCase();
       	 }while(prenom.equals("")); 
       	 String infoId[]={nom,prenom}; 
       	 return  infoId; 
        }
public String  signUpOrlogin()
        {
        String rep; 
        ui.ecrireChaine("Pour se connecter tapez 1 \n");
        ui.ecrireChaine("Pour creer un compte tapez 2 \n");  
        do{rep = ui.lireChaine();}while(!rep.equals("1") && !rep.equals("2")); 
        return rep; 
        }
public  Personne seConnecter(PrintWriter sisw, BufferedReader sisr,ObjectOutputStream outputStream,ObjectInputStream inputStream)
	{   Personne pers=null; 
	try{
		 sisw.println("seConnecter"); 
	         String[] infoId= ControleEtsaisieNomPrenom();
	         outputStream.writeObject(new Personne(infoId[0],infoId[1]));
        	 String  rep= sisr.readLine(); // lit le  message retourné 
        	 ui.ecrireChaine(rep+"\n"); 
        	 pers = (Personne)inputStream.readObject(); //on lit la pesonne retourné ou nul 
        	}catch(Exception e){ }
	     return pers; 	
	}
public void creeCompte(PrintWriter sisw, BufferedReader sisr,ObjectOutputStream outputStream,ObjectInputStream inputStream)
	{    try{
	         sisw.println("creerCompte");
       		 String[] infoId= ControleEtsaisieNomPrenom(); 
	         outputStream.writeObject(new Personne(infoId[0],infoId[1]));
	         String  rep= sisr.readLine(); // lit le  message retourné 
        	 ui.ecrireChaine(rep+"\n\n");   
    		}catch (Exception e) {e.printStackTrace();} 
	}	
}
