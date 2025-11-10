package com.gestcontact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PhoneBook {
    public static final Scanner sc = new Scanner(System.in);

    //Création de l'objet mapper JSON
    public static final ObjectMapper mapper = new ObjectMapper();

    //Obtenir le chemin vers le fichier de maniere dynamique
    private static final Path phoneBookPath = getPhoneBookPath();

    public static void main(String[] args) throws IOException {
        //Creer le fichier de sauvegarde des contacts en JSON
        initApplicationEnvironment();

        //Charger les contacts existant
        List<Contact> contacts = loadContacts(phoneBookPath);

        boolean run = true;
        while (run) {
            //afficher le menu
            displayMenu();

            //Recuperer le choix du user
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    //Lister les contacts
                    displayContacts(contacts);
                    break;
                case "2":
                    //ajouter un contact
                    addNewContact(contacts);
                    break;
                case "3":
                    //Rechercher un contact
                    searchContacts(contacts);
                    break;
                case "4":
                    //Supprimer un contact
                    deleteContacts(contacts);
                    break;
                case "5":
                    //Quitter
                    run = false;
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez taper 1, 2, 3, 4 ou 5.");
            }

        }

        System.out.println("Merci d'avoir utilisé GestContact. Au revoir !");
        sc.close();
    }

    /**
     * S'assure que le dossier de l'application (.gestContactApp) existe.
     * Doit être appelée au démarrage.
     */
    private static void initApplicationEnvironment() {
        try {
            Path parentDir = phoneBookPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                System.out.println("Dossier de l'application initialisé : " + parentDir);
            }
        } catch (IOException e) {
            System.err.println("Erreur critique: Impossible de créer le dossier de l'application : " + e.getMessage());

        }
    }

    /**
     * Affiche le menu principal des options.
     */
    private static void displayMenu(){
        System.out.println("\n--- Menu GestContacts ---");
        System.out.println("1. Lister tous les contacts");
        System.out.println("2. Ajouter un nouveau contact");
        System.out.println("3. Rechercher un contact");
        System.out.println("4. Supprimer un contact");
        System.out.println("5. Quitter");
        System.out.print("Votre choix : ");
    }

    /**
     * Demande à l'utilsateur d'entrer une info personelle et retourne sa reponse
     * @param userRequest est l'info personnelle demandé
     * @return La chaine de caracter entrée par l'utilisateur
     */
    public static String getUserInput(String userRequest) {
        System.out.println(userRequest);
        return sc.nextLine();
    }

    /**
     * Construit un chemin portable vers le fichier de sauvegarde
     * dans le dossier personnel de l'utilisateur.
     *
     * @return Le Path vers le fichier PhoneBook.txt
     */
    private static Path getPhoneBookPath() {
        String userHome = System.getProperty("user.home");
        //je construis le chemin en fonction de l'architecture du systeme hôte
        return Paths.get(userHome, ".gestContactApp", "PhoneBook.txt");
    }

    /**
     * Charge tous les contacts depuis le fichier de sauvegarde.
     * @param path Le chemin vers le fichier PhoneBook.txt
     * @return Une liste de Contacts. Retourne une liste vide si le fichier n'existe pas.
     */
    private static List<Contact> loadContacts(Path path){
        List<Contact> contacts = new ArrayList<>();
        //Si le fichier n'existe pas il n'y'a rien à charger
        if(Files.notExists(path)){
            return contacts; //Retoune une liste vide
        }

        try{
            //Lit toutes les lignes du fichier d'un coup
            List<String> lines = Files.readAllLines(path);

            for(String jsonLine : lines){
                //Ignorer les lignes vide qui pourraient exister
                if(jsonLine.isBlank()){
                    continue;
                }
                try{
                    //Transformation de la ligne JSON en objet Contact
                    Contact contact = mapper.readValue(jsonLine,Contact.class);
                    contacts.add(contact);
                }catch(JsonProcessingException e){
                    System.err.println("Erreur: Cette ligne JSON a un problème: " + jsonLine);
                }
            }
        }catch(IOException e){
            System.err.println("Erreur: Impossible de lire le fichier de contact :" + e.getMessage());
        }
        return contacts;
    }

    /**
     * Affiche la liste des contacts dans la console.
     * @param contacts La liste des contacts à afficher.
     */
    private static void displayContacts(List<Contact> contacts){
        if(contacts.isEmpty()){
            System.out.println("La liste de contacts est vide !");
            return;
        }

        System.out.println("--- Votre Répertoire de Contacts ---");
        int i=1;
        for(Contact contact : contacts){
            System.out.println(i + ". " + contact);
            i++;
        }
        System.out.println("------------------FIN--------------------");
    }

    /**
     * Sauvegarde la liste COMPLÈTE des contacts dans le fichier.
     * ÉCRASE le fichier existant.
     * @param contacts La liste totale des contacts.
     * @return true si la sauvegarde a réussi, false sinon.
     */
    private static boolean saveContactsToFile(List<Contact> contacts){
        List<String> jsonLines = new ArrayList<>();

        //Convertir chaque objet Contact en ligne JSON
        for(Contact contact : contacts){
            try{
                jsonLines.add(mapper.writeValueAsString(contact));
            } catch(JsonProcessingException e){
                System.err.println("Erreur : Echec de conversion de " + contact + ".Sauvergarde annulée");
                return false;
            }
        }
        //Ecrire toutes les lignes dans le fichier, en ECRASANT l'ancien
        try{
            Files.write(phoneBookPath, jsonLines, StandardOpenOption.CREATE);
            return true;
        }catch(IOException e){
            System.err.println("Erreur lors de l'ecriture dans le fichier : " + e.getMessage());
            return false;
        }
    }

    /**
     * Recherche des contacts par nom ou prénom.
     * @param contacts La liste de tous les contacts.
     */
    private static void searchContacts(List<Contact> contacts){
        System.out.println("\n--- Rechercher un contact ---");
        String searchContact = getUserInput("Entrer un nom ou prénom à rechercher :").toLowerCase();

        //Utilisation de Streams pour filtrer la liste
        List<Contact> results = contacts.stream()
                .filter(contact ->
                        contact.getLastName().toLowerCase().contains(searchContact) ||
                        contact.getFirstName().toLowerCase().contains(searchContact)
                )
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("Aucun contact trouvé pour '" + searchContact + "' !");
        } else {
            System.out.println("Résultats de la recherche pour '" + searchContact + "' !");
            displayContacts(results);
        }
    }

    /**
     * Trouve et supprime un contact.
     * @param contacts La liste de tous les contacts (sera modifiée).
     */
    private static void deleteContacts(List<Contact> contacts){
        System.out.println("\n--- Supprimer un contact ---");
        String searchContact = getUserInput("Entrer le nom ou le prénom exact du contact :").toLowerCase();

        //Recherche du contact avec Streams
        List<Contact> results = contacts.stream()
                .filter(contact ->
                        contact.getLastName().toLowerCase().contains(searchContact) ||
                                contact.getFirstName().toLowerCase().contains(searchContact)
                )
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("Aucun contact trouvé pour '" + searchContact + "' !");
            return;
        }

        Contact contactToDelete;
        if(results.size() > 1){
            System.out.println("Plusieurs contacts trouvés. Lequel voulez-vous supprimer ?");
            displayContacts(results);
            try{
                int choice = Integer.parseInt(getUserInput("Entrer le numéro à supprimer (0 pour annuler):"));
                if(choice == 0 || choice > results.size()){
                    System.out.println("Suppression annulée.");
                    return;
                }
                contactToDelete = results.get(choice-1);
            } catch(NumberFormatException e){
                System.out.println("Entrer invalide. Suppression annulée.");
                return;
            }
        } else{
            contactToDelete = results.get(0);
        }

        // Confirmation
        System.out.println("Contact trouvé :  " + contactToDelete);
        String confirmation = getUserInput("Etes-vous sur de vouloir supprimer ce contact ?(O/N) :");

        if(confirmation.equalsIgnoreCase("O")){
            // Supprimer de la liste en memoire
            contacts.remove(contactToDelete);

            //Sauvegarder la liste mise à jour sur le disque
            if(saveContactsToFile(contacts)){
                System.out.println("Contact supprimé avec succès");
            } else{
                System.err.println("Erreur : Le contact a été supprimé de la session mais la sauvergarde sur disque a echoué");
            }
        }else{
            System.out.println("Suppression annulée.");
        }
    }



    /**
     * Gère le processus d'ajout d'un nouveau contact.
     * @param contacts La liste en mémoire, qui sera mise à jour.
     */
    private static void addNewContact(List<Contact> contacts){
        System.out.println("\n--- Ajouter un nouveau contact ---");
        String userLastName = getUserInput("Entrer votre nom de famille :");
        String userFirstName = getUserInput("Entrer votre prenom :");
        String userPhoneNumber = getUserInput("Entrer votre numero de telephone :");

        Contact newContact = new Contact(userLastName, userFirstName, userPhoneNumber);
        //Mise à jour de la liste
        contacts.add(newContact);

        //Sauvegarder ce nouveau contact le fichier
        /*// Preparer le contenu à ecrire
        String contentToWrite;
        try {
            //Convertit l'objet java 'contact' en une chaine JSON
            contentToWrite = mapper.writeValueAsString(newContact) + "\n";
        } catch (JsonProcessingException e) {
            System.err.println("Erreur critique : Echec de la conversion du contact en JSON");
            e.printStackTrace();
            sc.close();
            return; //Si la conversion à echoué le programme s'arrête
        }

        //Ecriture dans notre fichier PhoneBook.txt
        try {
            //S'assurer que le dossier parent existe
            Path parentDir = phoneBookPath.getParent();
            if (Files.notExists(parentDir)) {
                Files.createDirectories(parentDir);
                System.out.println("Le dossier de l'application est créé : " + parentDir);
            }

            // Ecriture dans le fichier
            // StandardOpenOption.APPEND : ajoute à la fin du fichier
            // StandartOpenOption.CREATE : créé le fichier s'il n'existe pas
            Files.writeString(phoneBookPath, contentToWrite, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

            System.out.println("Contact ajouté ! Fichier sauvegardé ici : " + phoneBookPath);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ecriture dans le fichier :");
            e.printStackTrace();
        }

         */

        if(saveContactsToFile(contacts)){
            System.out.println("Contact ajouté avec succès");
        }else{
            System.err.println("Erreur : Le contact a été ajouté en mémoire, mais la sauvegarde sur le disque à echoué");
        }

    }


}
