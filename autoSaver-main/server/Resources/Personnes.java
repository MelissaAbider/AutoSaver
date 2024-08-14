package Resources;
import java.util.*;
import java.io.Serializable;

public class Personnes implements Serializable
{
       private static final long serialVersionUID = 1L; 
   ArrayList<Personne> personnes;
   public Personnes()
   {
   this.personnes= new ArrayList<Personne>(); 
   }
   public boolean ajouterPersonne(Personne p)
   {
   if( personnes.contains(p)){ // compare les objet avec la methode equals() surchargé dans Personne
      return true; 
      }
   else
   {
    personnes.add(p); 
    return false; 
   }
   }
   public boolean  chercher(Personne p)
   {
     if(personnes.contains(p))
	return true;
     else
        return false; 
   }
   
}

