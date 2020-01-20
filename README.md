# 2019PrInfo-Info3
Application JavaFX permettant d'analyser des statistiques sur des utilisateurs ou des hashtags de Twitter.
Fonctionne grâce à l'API Twitter (https://developer.twitter.com/)

<p align="center">
 <a href="https://drive.google.com/open?id=1vOLUq7P4GxDFbeYNH-2gjuY_Ba6oOsHz">
  <img src="https://github.com/telecom-se/2019PrInfo-Info3/blob/master/WikiResources/video.png?raw=true" alt="Regarder la vidéo"/>
 </a>
</p>

## Sommaire
* [A propos du projet](#A-propos-du-projet)
* [Commmencer](#Commmencer)
  * [Prérequis](#Prérequis)
  * [Installation](#Installation)
  * [Limitations](#Limitations)
  
* [Contribuer ou Modifier](#Contribuer-ou-Modifier)

* [Contributors](#Contributors)

## A propos du projet
Ce projet a été réalisé dans le cadre d'un projet informatique à Télécom Saint Etienne.
L'objectif est de pouvoir suivre et analyser les statistiques sur des utilisateurs ou des hashtags de Twitter. 
En créant un compte, l'utilisateur peut aussi créer des points d'intérêts regroupant plusieurs utilisateurs ou hashtags. Il peut aussi ajouter des users ou des hasthags à ses favoris.
Pour des informations techniques détaillées, veuillez consulter le wiki. 

## Commmencer
### Prérequis
Java 11.
### Installation
Télécharger le .jar suivant : [Executable](https://github.com/telecom-se/2019PrInfo-Info3/blob/master/2019PrInfo-Info3.jar).
Ce .jar contient javaFX et toutes les dépendances nécéssaires au fonctionnement de l'application.
Afin de créer la base de donnée, il faut ajtouer l'argument install la première fois. Cela va créer une base de données locale (simple fichier .h2.db) à la racine de votre répertoire personnel.
+ Sous Linux/MacOs : 
Ouvrez un terminal, rendez-vous dans le répertoire contenant le fichier et entrez la commande suivant
```sh
java -jar TwitterAnalytics.jar install
```
+ Sous Linux/MacOs : 
Ouvrez un cmd, rendez-vous dans le répertoire contenant le fichier, et entrez la commande suivant
```cmd
java -jar TwitterAnalytics.jar install
```

### Utilisation
+ Sous linux: 
Ouvrez un terminal, rendez-vous dans le répertoire contenant le fichier et entrez la commande suivant
```sh
java -jar TwitterAnalytics.jar 
```
+ Sous Windows: 
Rendez-vous dans le répertoire contenant le fichier, double-cliquez sur le .jar ou lancez la commande suivante dans le cmd. 
```cmd
java -jar TwitterAnalytics.jar 
```

### Limitations
Afin de fonctionner, l'application fait des requêtes à l'API twitter qui nécessite d'avoir un compte développeur et des autorisations. Pour utiliser votre propre compte, veuillez configurer vos propres identfiants. Rendez-vous dans le wiki dans la partie Request Manager.

## Contribuer ou Modifier
Cette application a été réalisée dans le cadre d'un projet scolaire. Elle ne sera donc pas maintenue et aucun bug ne sera corrigé.
Afin de modifier le code source, vous pouvez forker le projet. 
Un wiki a été rédigé afin d'expliquer l'architecture technique et le fonctionnement du logiciel. 

### Contributors :
Kamil CAGLAR : kamilcaglar.contact@gmail.com  
Taha ALAMI IDRISSI : taha.alami@um5s.net.ma  
Laila Abouzaid : lailaabouzaid@gmail.com  
Sergiy MAZANKA : mazanka.sergiy13@gmail.com  
Sobun UNG : sobunung@yahoo.fr
