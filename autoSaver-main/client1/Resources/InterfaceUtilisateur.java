package Resources;
import java.util.Scanner;

public class InterfaceUtilisateur {
    private Scanner scanner;

    public InterfaceUtilisateur() {
        this.scanner = new Scanner(System.in);
    }

    // Méthode pour lire une chaîne de caractères depuis la console
    public String lireChaine(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
        public String lireChaine() {
        return scanner.nextLine();
    }

    // Méthode pour lire un entier depuis la console
    public int lireEntier(String message) {
        System.out.print(message);
        return scanner.nextInt();
    }

    // Méthode pour écrire une chaîne de caractères dans la console
    public void ecrireChaine(String message) {
        System.out.print(message);
    }

    // Méthode pour écrire un entier dans la console
    public void ecrireEntier(int entier) {
        System.out.print(entier);
    }

    // Méthode pour fermer le scanner
    public void fermerScanner() {
        scanner.close();
    }
 }

