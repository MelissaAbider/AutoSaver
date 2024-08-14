#!/usr/bin/bash

# Remove .class files from Resources directory
rm -f Resources/*.class

# Remove .class files from current directory
rm -f *.class

javac Serveur.java
#javac -Xlint Serveur.java


# Vérifier si la compilation s'est bien déroulée
if [ $? -eq 0 ]; then
    # Exécuter le programme
    java Serveur.java
else
    echo "Erreur lors de la compilation."
fi
