 package Resources; 
 import Resources.Personnes; 
 import java . io .*;
 import java . net .*;
 import java.util.zip.ZipOutputStream;
 import java.util.zip.ZipEntry;
 import java.io.FileOutputStream;

 public class Connexions extends Thread {
 Socket socket ; // connexion socket complet
 private String repertoire;
 String cheminComplet; 
 String requete ;
 Personne user=null ; 
 PrintWriter out;  
 BufferedReader in;
 ObjectOutputStream outputStream;
 ObjectInputStream inputStream;  
 InterfaceUtilisateur ui = new InterfaceUtilisateur(); 
 AccesDonnees dataAccess; 
 public Connexions (Socket s) { socket = s ;}
 @Override
 public  synchronized void run (){
        Personnes p=null; 
 	try{
         out = new PrintWriter ( new BufferedWriter (new OutputStreamWriter ( socket . getOutputStream () ) ) , true);
 	 in = new BufferedReader ( new InputStreamReader (socket. getInputStream () )) ;
 	 outputStream = new ObjectOutputStream(socket.getOutputStream());
         inputStream = new ObjectInputStream(socket.getInputStream());
 	 String requete= in.readLine(); 
 	if( requete.equals("seConnecter")){
 	    try {
 	      	  user = (Personne) inputStream.readObject(); // lit l'objet serialisé depuis le socket 
 	      	  dataAccess= new AccesDonnees( user, out,  in, outputStream, inputStream); 

 	          p = dataAccess.DeserialiserPersonne(); 

    		 if(p.chercher(user)) // recherche la personne 
    		 {
    		   out.println("connecter avec succès \n"); 
    		   ui.ecrireChaine(user.getNom()+" connecter avec succès \n\n");   
		   outputStream.writeObject(user);
		   while (!( requete = in . readLine () ) . equals ( "END" ) ) {
		      if(requete.equals("consulter")){
		      try{
		        dataAccess.contenue(); 
		         }catch(Exception e){}
    			String cheminComplet = "destination/" + user.getNom();
    			do {
      			out.println("rep");
      			repertoire = in.readLine();
      			ui.ecrireChaine(repertoire+"\n");
      		        if(new File(cheminComplet +"/" + repertoire).exists())
      		       { 
      			cheminComplet += "/" + repertoire;

      			if (!repertoire.equals("q"))
        		 dataAccess.parcourir(cheminComplet);
        	        }
      			else
      			{
        		out.println("Le chemin spécifié ne correspond pas à un répertoire.");
        		}
        		
    			} while (!repertoire.equals("q"));
    			
    			out.println("le parcours est fini");
		      }
		      if(requete.equals("AutoSave")){
		        AutoSaver as = new AutoSaver(user,out,in, outputStream, inputStream);
		        as.addSocket(socket); 
            		as.demarrer();
		      
		      }
		      if(requete.equals("telecharger")){
		         try{
		         dataAccess.contenue(); 
		         }catch(Exception e){}
		         File f;
    			 cheminComplet = "destination/" + user.getNom();
    			 int cpt=0; 
    		         do{
    		          requete= in.readLine();  // p c s
    			  switch(requete)
      	   			{
      	    			case "p":
      	       			        out.println("Donne le nom du repertoire à consulter :");
      			                requete = in.readLine();
      			                repertoire=requete; 
      			                if(new File(cheminComplet +"/" + requete).exists() && (new File(cheminComplet +"/" + requete)).isDirectory())
      			                {
      			                cheminComplet += "/" + requete;
        				dataAccess.parcourir(cheminComplet);
        				}
        				else
        				{
        				out.println("Le chemin spécifié ne correspond pas à un répertoire.");
        				}
        				out.println("le parcours est fini");
      	    			break;
      	    			case "c":
      	                               requete = in.readLine(); // nom du fichier 
      	                               ui.ecrireChaine(cheminComplet+"\n"); 
      	                               f= new File(cheminComplet+"/"+requete); 
      	                               if(f.exists()){
      	                               out.println("ok"); 
      	                               dataAccess.sendFileOrDirectory( f,outputStream ); 
      	                               // Marquer la fin du répertoire ou du fichier
                                      outputStream.writeObject(null);
                                      }
                                      else
                                      out.println("Le chemin spécifié ne correspond pas à un répertoire.");
      	   			}
      	   	            } while (!requete.equals("s"));
		      
		        }
 	            }
    		 }
    		 else
    		 {
    		  out.println("utilisateur non trouver verrifier vos informations ou creer un compte"); 
    		 }
               }catch (IOException | ClassNotFoundException e) { out.println("utilisateur non trouver verrifier vos informations ou creer un compte");  } 
 	}
 	else if( requete.equals("creerCompte")){
 	 try  // on essaye de lire dans Personne.ser
		{
		 user = (Personne) inputStream.readObject(); // lit l'objet serialisé depuis le socket
		 dataAccess= new AccesDonnees( user, out,  in, outputStream, inputStream); 
		 try{
        	 p = dataAccess.DeserialiserPersonne(); 
        	 }catch(Exception e){ e.printStackTrace(); }
    		}catch (IOException | ClassNotFoundException e) // s'il y a exception alors on cree une nouvelle instance de Personnes
    		{
    		 //e.printStackTrace();  generaelement FileNotFoundException
    		 p= new Personnes();  // on cree une nouvelle instance de Personnes si Personnes.ser nexistait pas 
    		} 	
        	  try 
		 {
		   if(!(p.chercher(user))) //si la personne n'est pas encore enregistrée
		   {
                     dataAccess.serialiserPersonne(p);
    		  }
    		  else{
    		  	out.println("vous avez deja un compte"); 
    		  }
    		}catch (Exception e) {e.printStackTrace();} 
 	  }
 	  try{
 	  Thread.sleep(500); 
 	   }catch(Exception e){}; 
 	socket . close () ;
 	} catch ( IOException e ) { }
   }
 }
