# PMU Racing API

Cette API de course est conçue pour fournir des fonctionnalités essentielles pour la gestion des courses. Elle intègre plusieurs fonctionnalités avancées pour assurer la sécurité, la documentation, le caching, et les tests unitaires et d'intégration.

## Fonctionnalités Principales

### 🛡️ Validation des Données

La validation des données est cruciale pour garantir l'intégrité et la sécurité des données manipulées par l'API. J'ai utilisé le module `spring-boot-starter-validation` pour gérer la validation des entrées utilisateur. Chaque entrée est validée selon les contraintes définies dans les modèles de données, assurant ainsi des données cohérentes et sécurisées.

### 📚 Documentation avec Swagger

Swagger est utilisé pour générer une documentation interactive de l'API. Grâce à Swagger, les utilisateurs peuvent facilement explorer les endpoints disponibles, leurs paramètres et réponses attendues, ce qui facilite l'intégration avec l'API.

### 🔗 HATEOAS (Hypermedia as the Engine of Application State)

HATEOAS est une architecture qui permet à l'API de renvoyer des liens hypertexte aux clients pour naviguer entre les différentes ressources de manière dynamique. Cela améliore la découvrabilité de l'API et réduit le couplage entre le client et le serveur.

### 🔄 Caching avec Spring Starter Cache

Le caching est utilisé pour améliorer les performances en stockant en mémoire les résultats des requêtes fréquemment exécutées. J'ai intégré le module Spring Starter Cache pour mettre en cache les données les plus demandées, réduisant ainsi le temps de réponse de l'API.

### 🧪 Tests Unitaires avec Mockito

Les tests unitaires sont essentiels pour garantir la fiabilité du code. J'ai utilisé Mockito pour créer des mocks des dépendances et des comportements attendus, permettant ainsi de tester chaque composant de manière isolée.

### 🛠️ Tests d'Intégration avec MockMvc

Les tests d'intégration sont réalisés avec MockMvc, un framework de test intégré à Spring MVC. Ces tests vérifient le comportement de l'API dans un environnement similaire à celui de production, en testant les requêtes HTTP et les réponses retournées.

### 🚫 Global Error Handler

Un gestionnaire d'erreurs global a été mis en place pour gérer les erreurs API de manière cohérente. Cela garantit que toutes les exceptions sont correctement capturées et traitées, avec des réponses d'erreur appropriées renvoyées aux clients.

### 🔄 Pattern Strategy pour les Services

Le pattern Strategy a été utilisé pour organiser la logique métier en différentes stratégies interchangeables. Cela rend le code plus modulaire, extensible et facile à maintenir, en permettant de changer dynamiquement le comportement des services sans modifier leur structure.

### 📄 Pagination des résultats

J'ai ajouté la pagination aux endpoints de récupération des données pour améliorer les performances et la convivialité de l'API. Désormais, lors de la récupération de grandes quantités de données, vous pouvez spécifier les paramètres de pagination pour obtenir des résultats par pages.

La pagination est mise en œuvre à l'aide du paramètre `Pageable` dans les endpoints de récupération de données. Vous pouvez spécifier les paramètres suivants pour contrôler la pagination :

- `page` : Le numéro de la page à récupérer. Par défaut, la première page est récupérée.
- `size` : Le nombre d'éléments par page. Par défaut, une taille de page est utilisée.

Par exemple, pour récupérer la deuxième page avec 10 éléments par page, vous pouvez envoyer une requête avec les paramètres `page=1` et `size=10`.

Cette fonctionnalité améliorera l'expérience des utilisateurs en permettant une récupération efficace des données, en particulier lors du travail avec de grands ensembles de données.

### 🚌 Exposition des Courses via Kafka

Les courses sont exposées via un bus Kafka nommé `race-events`. Kafka est utilisé pour la diffusion des événements de course en temps réel, permettant ainsi aux applications clientes de suivre l'état des courses en direct.

## Technologies Utilisées

- Java
- Spring Boot
- Spring Starter Validation
- Swagger
- HATEOAS
- Spring Starter Cache
- Mockito
- MockMvc
- Kafka

## Résultats de tests

Après l'exécution de l'API, j'ai fais quelques tests pour s'assurer du bon fonctionnement. 

1. La réception des messages sur le consummer Kafka :
   
   <img width="1249" alt="Kafka consumer" src="https://github.com/nakigami/pmu-racing-api/assets/37220485/94522f6a-8ab6-4337-badb-8ea81baffa6f">

2. Le temps de réponse avant la mise en place du caching : (272 ms)
   
   <img width="882" alt="272-before-caching" src="https://github.com/nakigami/pmu-racing-api/assets/37220485/51e559fe-7fc9-4456-9e7c-2a7231621671">

3. Le temps de réponse après la mise en place du caching : (9ms)
   
<img width="882" alt="9 after caching" src="https://github.com/nakigami/pmu-racing-api/assets/37220485/686f1d00-03dd-490c-853e-0ded7e0b5a49">

4. La documentation Swagger :
   
![image](https://github.com/nakigami/pmu-racing-api/assets/37220485/db00f6d7-d20e-403e-b8e7-46ea6dea204f)

5. La mise en place de Hateoas :
    
<img width="1326" alt="image" src="https://github.com/nakigami/pmu-racing-api/assets/37220485/2b442ade-fddb-4591-ad54-dc203827bec5">

6. La mise en place de la pagination :
    
<img width="1326" alt="image" src="https://github.com/nakigami/pmu-racing-api/assets/37220485/e85f70e3-514d-4568-89f1-fdfe7859b881">


## Auteur

Ce projet a été développé par [Anas RIANI](https://www.linkedin.com/in/anas-riani-027091139/).
