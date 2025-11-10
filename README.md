#  GestContact

> Gestionnaire de Contacts en Console

Projet simple d'application console en Java pour gérer un répertoire téléphonique personnel. Ce projet est un **"kata" d'apprentissage** délibéré, axé sur la maîtrise des principes fondamentaux de l'ingénierie logicielle Java, de la gestion des I/O modernes (`java.nio.file`) à la sérialisation JSON (Jackson).

---

## ✨ Fonctionnalités

* **Ajouter** un nouveau contact (Nom, Prénom, Téléphone).
* **Lister** tous les contacts existants.
* **Rechercher** un contact à partir de son Nom ou prénom
* **Supprimer** un contact à partir de son Nom ou prénom
* **Persistance Robuste** : Utilise la sérialisation **JSON** (via la bibliothèque Jackson) pour une sauvegarde fiable. Les données sont stockées de manière portable dans le répertoire personnel de l'utilisateur (`user.home`), garantissant un fonctionnement multi-OS.
* **Menu Interactif** : Une boucle d'application simple pour une navigation facile.

### Prochaines étapes :

- [ ] Modifier un contact

---

## 🚀 Comment Lancer le Projet

Ce projet utilise Maven pour la gestion des dépendances (notamment Jackson pour le JSON).

1.  **Clôner le dépôt :**
    ```bash
    git clone https://github.com/VOTRE_NOM_UTILISATEUR/gestContact.git
    cd gestContact
    ```

2.  **Compiler le projet (via Maven) :**
    <p>Cela va télécharger les dépendances (comme Jackson) et compiler le code.</p>
    
    ```bash
    mvn compile
    ```

3.  **Exécuter l'application :**
    ```bash
    mvn exec:java -Dexec.mainClass="com.gestcontact.PhoneBook"
    ```

---

*Ce projet est développé dans le cadre d'un parcours d'apprentissage pour devenir Ingénieur Logiciel de Qualité.*