# EQUA - Finance Without Barriers

Application web immersive 3D pour l'écosystème de microfinance décentralisée EQUA.

## 🌟 Caractéristiques

- **Scène 3D immersive** : Canvas fullscreen avec Three.js
- **Token EQUA en 3D** : Modèle 3D du token avec effets de bloom et particules
- **Animations au scroll** : GSAP ScrollTrigger pour des animations fluides
- **Post-processing** : Effet Bloom (UnrealBloomPass) pour un rendu cinématique
- **Interface moderne** : Design sombre et minimaliste avec typographie Syne
- **API Spring Boot** : Communication avec le backend pour les données des projets
- **Responsive** : Optimisé pour tous les écrans

## 🚀 Stack Technique

### Frontend
- **Angular 19** : Framework principal
- **Three.js** : Rendu 3D WebGL
- **GSAP** : Animations (ScrollTrigger)
- **TypeScript** : Typage statique
- **SCSS** : Styles avancés

### Backend (à configurer séparément)
- **Spring Boot 3** : API REST
- **Spring Data JPA** : Accès aux données
- **H2/PostgreSQL** : Base de données

## 📦 Installation

### Prérequis
- Node.js (v18 ou supérieur)
- npm ou yarn
- Angular CLI (optionnel)

### Étapes

1. **Cloner le repository**
```bash
git clone <repository-url>
cd Frontend
```

2. **Installer les dépendances**
```bash
npm install
```

3. **Lancer l'application**
```bash
npm start
```

L'application sera disponible sur `http://localhost:4200`

## 🎮 Utilisation

### Démarrage du Frontend (port 4200)

```bash
npm start
```

### Build de production

```bash
npm run build
```

Les fichiers de build seront dans le dossier `dist/`.

### Démarrage du Backend (port 8080)

Le backend doit être lancé séparément. Créez un projet Spring Boot avec les endpoints suivants :

- `GET http://localhost:8080/api/projects` — Liste des projets
- `GET http://localhost:8080/api/projects/{id}` — Détail d'un projet

## 🎨 Fonctionnalités

### Scène 3D
- Canvas fullscreen avec Three.js
- Chargement du token EQUA en 3D
- Particules animées en arrière-plan
- Éclairage dynamique (ambiant, directionnel, point light, spot light)
- Post-processing avec Bloom pour un effet de lueur

### Animations Scroll
- Rotation du token basée sur le scroll
- Zoom de caméra progressif
- Animations des sections au défilement
- Indicateur de scroll personnalisé

### Interactions
- Clic sur le token pour afficher les informations
- Overlay avec détails des projets
- Animation de caméra vers l'objet cliqué
- Retour à la vue initiale après fermeture de l'overlay

### Sections
1. **Hero** : Introduction avec titre animé et CTA
2. **About** : Caractéristiques principales d'EQUA
3. **Tokenomics** : Modèle économique du token
4. **Projects** : Solutions de l'écosystème EQUA
5. **Stats** : Statistiques clés
6. **CTA** : Appel à l'action
7. **Footer** : Liens et informations

## 📁 Structure du Projet

```
Frontend/
├── src/
│   ├── app/
│   │   ├── models/
│   │   │   └── project.model.ts
│   │   ├── pages/
│   │   │   └── home/
│   │   │       ├── home.component.ts
│   │   │       ├── home.component.html
│   │   │       └── home.component.scss
│   │   ├── services/
│   │   │   ├── api.service.ts
│   │   │   └── three.service.ts
│   │   ├── app.component.ts
│   │   └── app.routes.ts
│   ├── assets/
│   │   ├── images/
│   │   │   └── equa-coin.png
│   │   └── models/
│   │       └── (vos modèles .glb/.gltf)
│   ├── index.html
│   ├── main.ts
│   └── styles.scss
├── angular.json
├── package.json
├── tsconfig.json
└── README.md
```

## 🎯 Configuration

### API Backend

Modifiez l'URL de l'API dans `src/app/services/api.service.ts` :

```typescript
private apiUrl = 'http://localhost:8080/api';
```

### Modèles 3D

Pour utiliser vos propres modèles 3D :

1. Placez vos fichiers `.glb` ou `.gltf` dans `src/assets/models/`
2. Mettez à jour le chemin dans le service Three.js

### CORS

Assurez-vous que votre backend autorise les requêtes depuis `http://localhost:4200`.

Exemple de configuration Spring Boot :

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

## 🎨 Personnalisation

### Couleurs

Modifiez les variables CSS dans `src/styles.scss` :

```scss
:root {
  --primary-gold: #FFD700;
  --dark-bg: #0a0a0a;
  --dark-surface: #1a1a1a;
  --text-primary: #ffffff;
  --text-secondary: #a0a0a0;
  --accent-blue: #00d4ff;
}
```

### Typographie

La police Syne est chargée depuis Google Fonts. Pour changer :

```html
<!-- Dans src/index.html -->
<link href="https://fonts.googleapis.com/css2?family=VotrePolice:wght@400;700&display=swap" rel="stylesheet">
```

## 🐛 Dépannage

### Le canvas 3D ne s'affiche pas
- Vérifiez que Three.js est correctement installé
- Ouvrez la console du navigateur pour voir les erreurs
- Assurez-vous que l'image du token existe dans `src/assets/images/`

### Les animations ne fonctionnent pas
- Vérifiez que GSAP et ScrollTrigger sont installés
- Regardez la console pour les erreurs GSAP

### Erreurs CORS
- Configurez le backend pour autoriser les requêtes depuis localhost:4200
- Vérifiez les en-têtes CORS dans la réponse du serveur

## 📄 Licence

Copyright © 2025-2026 EQUA. Tous droits réservés.

## 👥 Équipe

Projet développé dans le cadre du PI 4ème année - ESPRIT

## 📞 Contact

Pour toute question ou suggestion, contactez l'équipe EQUA.

---

**EQUA - Finance Without Barriers** 🚀
