#!/usr/bin/bash

# Compiler les fichiers source en fichiers objets
rm -f Resources/*.class
rm -f *.class

javac    Client.java


# Vérifier si la compilation s'est bien déroulée
if [ $? -eq 0 ]; then
    # Exécuter le programme
    java Client.java 172.31.18.70 8080
else
    echo "Erreur lors de la compilation."
fi
