package Resources;
 import java . io .*;
 import java . net .*;
 import java.util.*;

 import Resources.Connexions; 

public class Serveur {

 
 public static void main ( String [] args )throws IOException {
  InterfaceUtilisateur ui = new InterfaceUtilisateur();
  ServerSocket serveur = new ServerSocket ( 8080) ;
  ui.ecrireChaine(" attente de connexion ...\n"); 
 while ( true ) { // attente des demandes de connexion
 Connexions connexion = new Connexions ( serveur . accept () ) ;
 connexion . start () ;
 
  }
 }
}
 
 

