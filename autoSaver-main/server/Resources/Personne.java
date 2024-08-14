package Resources;
import java.io.Serializable;

public class Personne implements Serializable
{
       private static final long serialVersionUID = 1L; 

	private String nom ; 
	private String prenom;  

	public Personne(String nom , String prenom)
	{
	this.nom= nom;
	this.prenom= prenom;
	}
	
	public void setNom( String nom)
	{
	this.nom =nom;
	}
	public void setPrenom(String prenom)
	{
	this.prenom=prenom;
	}
	
	public  String getNom()
	{
	return this.nom;
	}
	public  String getPrenom()
	{
	return this.prenom;
	}
	
	@Override
        public String toString()
        {
        return this.nom+" "+this.prenom; 
        }
        @Override
        public int hashCode() {
        // on choisi nombres premiers arbitraires pour la multiplication et l'addition
        int prime = 31;
        int result = 1;
        result = prime * result + ((nom == null) ? 0 : nom.hashCode());
        result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
        return result;
    }
         @Override
    	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Personne personne = (Personne) obj;
        return this.prenom.equals(personne.getPrenom()) && this.nom.equals(personne.getNom());
    }
}

 

