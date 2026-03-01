# EQUA — Wallet Management Microservice

## Sommaire

1. [Apercu du Projet](#1-apercu-du-projet)
2. [Stack Technique](#2-stack-technique)
3. [Architecture Microservices](#3-architecture-microservices)
4. [Diagramme de Classes (Entites)](#4-diagramme-de-classes-entites)
5. [Gestion Wallet — CRUD Complet](#5-gestion-wallet--crud-complet)
6. [Gestion Token — CRUD Complet](#6-gestion-token--crud-complet)
7. [Gestion Asset — CRUD Complet](#7-gestion-asset--crud-complet)
8. [Regles Metiers Avancees](#8-regles-metiers-avancees)
9. [Modele IA Python (Microservice Flask)](#9-modele-ia-python-microservice-flask)
10. [WebSocket — Notifications Temps Reel](#10-websocket--notifications-temps-reel)
11. [Analytics et Export CSV](#11-analytics-et-export-csv)
12. [Eureka Discovery Server](#12-eureka-discovery-server)
13. [API Gateway](#13-api-gateway)
14. [Lancement du Projet (Docker)](#14-lancement-du-projet-docker)
15. [Guide de Test Swagger — Etape par Etape](#15-guide-de-test-swagger--etape-par-etape)
16. [Resume des Regles Metiers](#16-resume-des-regles-metiers)
17. [Structure du Projet](#17-structure-du-projet)

---

## 1. Apercu du Projet

**EQUA** est une application microservices de gestion de portefeuille numerique (Wallet Management). Elle permet de gerer des wallets, des tokens et des assets avec des regles metiers avancees : multi-devises, programme de fidelite, detection de fraude, scoring IA, et notifications en temps reel.

Le projet est compose de **5 services Docker** qui communiquent entre eux :
- Un **service Wallet** (Spring Boot) pour toute la logique metier
- Un **service IA Python** (Flask + scikit-learn) pour le scoring de credit avance
- Un **serveur Eureka** pour la decouverte de services
- Un **API Gateway** pour le routage centralise
- Une base **PostgreSQL** pour la persistance

---

## 2. Stack Technique

| Technologie | Version | Role |
|-------------|---------|------|
| **Java** | 21 | Langage principal (Wallet Service) |
| **Spring Boot** | 3.2.1 | Framework backend |
| **Spring Cloud** | 2023.0.0 | Eureka + Gateway |
| **Maven** | 3.8.8 | Gestion des dependances |
| **PostgreSQL** | 16 | Base de donnees relationnelle |
| **Python** | 3.11 | Microservice IA |
| **Flask** | 3.0.0 | API REST Python |
| **scikit-learn** | 1.3.2 | Modele ML ensemble |
| **Docker / Docker Compose** | - | Containerisation |
| **Swagger / OpenAPI** | 3.0 | Documentation API interactive |
| **WebSocket (STOMP)** | - | Notifications temps reel |
| **Lombok** | - | Reduction du code repetitif Java |
| **JPA / Hibernate** | - | ORM pour la persistance |

---

## 3. Architecture Microservices

```
                    +------------------------------------------+
                    |          API GATEWAY (:8088)              |
                    |        Spring Cloud Gateway               |
                    |  /api/wallets/**  --> wallet-service      |
                    |  /api/tokens/**   --> wallet-service      |
                    |  /api/assets/**   --> wallet-service      |
                    |  /api/advanced/** --> wallet-service      |
                    |  /api/analytics/**--> wallet-service      |
                    |  /api/ai/**       --> ai-service          |
                    +----+------------------+-------------------+
                         |                  |
          +--------------+------+    +------+----------+
          |                     |    |                  |
+---------v--------+  +---------v--+  +-----v---------+
| EUREKA SERVER    |  | WALLET     |  | AI SERVICE    |
| (:8761)          |  | SERVICE    |  | (Python)      |
| Service Registry |  | (:8080)    |  | (:5000)       |
| Dashboard        |  | Spring Boot|  | Flask + ML    |
+------------------+  | JPA + WS   |  | Ensemble      |
                      +------+------+  +---------------+
                             |
                      +------v------+
                      | PostgreSQL  |
                      | (:5432)     |
                      | equadb      |
                      +-------------+
```

### Ports des services

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5432 | - |
| Eureka Server | 8761 | http://localhost:8761 |
| Wallet Service | 8080 | http://localhost:8080 |
| AI Service Python | 5000 | http://localhost:5000/health |
| API Gateway | 8088 | http://localhost:8088 |
| Swagger UI | 8080 | docker-compose up --build|

---

## 4. Diagramme de Classes (Entites)

### Relations

```
User (1) ----------- (1) Wallet
                           |
                      (1) ---- (*) Token
                           |
                      (1) ---- (*) Asset
```

### Wallet

```
+--------------------------------------------+
|                  Wallet                      |
+--------------------------------------------+
| - walletId: Long (auto-generated)           |
| - balance: Double (default: 0.0)            |
| - customerId: Long (unique, required)       |
| - publicKey: String (unique, auto UUID)     |
| - status: String (ACTIVE/SUSPENDED/CLOSED)  |
| - currency: String (EUR/USD/GBP/BTC/ETH)   |
| - loyaltyPoints: Integer (default: 0)       |
| - loyaltyTier: String (BRONZE/SILVER/GOLD/  |
|                        PLATINUM)             |
| - transactionHistory: List<String>          |
| - createdAt: LocalDateTime                  |
| - updatedAt: LocalDateTime                  |
| - tokens: List<Token> (OneToMany)           |
| - assets: List<Asset> (OneToMany)           |
+--------------------------------------------+
| + sendTokens(amount, recipient): boolean    |
| + receiveTokens(amount): void               |
| + deposit(amount): boolean                  |
| + withdraw(amount): boolean                 |
+--------------------------------------------+
```

**Role** : Le Wallet est le portefeuille numerique d'un client. Il contient son solde, sa devise, ses points de fidelite, et l'historique de toutes les transactions. Chaque wallet possede une cle publique unique (generee automatiquement) et peut contenir plusieurs tokens et assets.

### Token

```
+--------------------------------------------+
|                  Token                       |
+--------------------------------------------+
| - tokenId: Long (auto-generated)            |
| - value: Double (required)                  |
| - customerId: Long (required)               |
| - conversionRate: Double (default: 1.0)     |
| - totalSupply: Integer (default: 0)         |
| - createdAt: LocalDateTime                  |
| - updatedAt: LocalDateTime                  |
| - wallet: Wallet (ManyToOne)                |
+--------------------------------------------+
| + transfer(recipientId, amount): boolean    |
| + getConvertedValue(): Double               |
+--------------------------------------------+
```

**Role** : Le Token represente une unite de valeur numerique liee a un wallet. Il a un taux de conversion (borne entre 0.5 et 2.0 pour eviter la volatilite). La methode `transfer` permet de deduire une quantite, et `getConvertedValue` calcule `value * conversionRate`.

### Asset

```
+--------------------------------------------+
|                  Asset                       |
+--------------------------------------------+
| - assetId: Long (auto-generated)            |
| - ownerId: String (required)                |
| - assetType: String (REAL_ESTATE/VEHICLE/   |
|              EQUIPMENT/DIGITAL/OTHER)        |
| - value: Double (required)                  |
| - status: String (REGISTERED/TRANSFERRED/   |
|                   DEVALUED)                  |
| - createdAt: LocalDateTime                  |
| - updatedAt: LocalDateTime                  |
| - wallet: Wallet (ManyToOne)                |
+--------------------------------------------+
| + registerAsset(ownerId, assetType, value): |
|   boolean                                    |
| + updateAssetValue(newValue): void          |
| + transferAsset(newOwnerId): boolean        |
+--------------------------------------------+
```

**Role** : L'Asset represente un bien reel ou numerique (immobilier, vehicule, equipement, digital). Quand un asset est enregistre, 10% de sa valeur est credite au wallet comme collateral. Le transfert d'asset deplace aussi le collateral entre wallets.

---

## 5. Gestion Wallet — CRUD Complet

### Couche Repository (`WalletRepository.java`)

Interface JPA qui fournit les requetes de base + requetes personnalisees :
- `findByCustomerId(Long)` — trouver un wallet par client
- `findByPublicKey(String)` — trouver par cle publique
- `findByStatus(String)` — filtrer par statut
- `findLowBalanceWallets(Double)` — wallets sous un seuil de solde
- `existsByCustomerId(Long)` — verifier si un wallet existe deja pour un client

### Couche Service (`WalletService.java`)

La logique metier pour les wallets :
- **Conversion DTO <-> Entity** : les donnees sont transformees entre WalletDTO (API) et Wallet (base de donnees)
- **Creation** : genere automatiquement une cle publique UUID, initialise le solde a 0, le statut a ACTIVE, la devise a EUR, les points de fidelite a 0, et le tier a BRONZE
- **Depot** : verifie que le wallet est ACTIVE et que le montant ne depasse pas 50 000
- **Retrait** : verifie le solde suffisant et le seuil minimum de 10
- **Transfert** : debite l'expediteur, credite le destinataire, enregistre dans les deux historiques
- **Suspension/Activation** : change le statut entre ACTIVE et SUSPENDED

### Couche Controller (`WalletController.java`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/wallets` | Creer un wallet |
| `GET` | `/api/wallets` | Lister tous les wallets |
| `GET` | `/api/wallets/{id}` | Wallet par ID |
| `GET` | `/api/wallets/customer/{customerId}` | Wallet par client |
| `PUT` | `/api/wallets/{id}` | Modifier un wallet |
| `DELETE` | `/api/wallets/{id}` | Supprimer un wallet |
| `POST` | `/api/wallets/{id}/deposit?amount=X` | Deposer des fonds |
| `POST` | `/api/wallets/{id}/withdraw?amount=X` | Retirer des fonds |
| `POST` | `/api/wallets/{id}/transfer` | Transferer (body: TransferRequest) |
| `PUT` | `/api/wallets/{id}/suspend` | Suspendre le wallet |
| `PUT` | `/api/wallets/{id}/activate` | Activer le wallet |
| `GET` | `/api/wallets/low-balance?threshold=X` | Wallets a solde faible |
| `GET` | `/api/wallets/risk/{customerId}` | Scoring IA du client |

### Regles metiers du Wallet

| Regle | Description |
|-------|-------------|
| Transaction max | Toute transaction > 50 000 est rejetee |
| Solde minimum | Le solde ne peut pas descendre sous 10 apres retrait |
| Statut ACTIVE | Depots, retraits et transferts bloques si SUSPENDED ou CLOSED |
| Client unique | Un seul wallet par customerId |
| Historique | Chaque operation (DEPOSIT, WITHDRAW, SEND, RECEIVE) est tracee avec timestamp |

---

## 6. Gestion Token — CRUD Complet

### Couche Repository (`TokenRepository.java`)

- `findByCustomerId(Long)` — tokens d'un client
- `findByWalletWalletId(Long)` — tokens d'un wallet
- `getTotalValueByCustomerId(Long)` — somme des valeurs par client
- `getTotalConvertedValueByWalletId(Long)` — somme des valeurs converties par wallet

### Couche Service (`TokenService.java`)

- **Creation** : verifie que le wallet existe et est ACTIVE, que le taux de conversion est entre 0.5 et 2.0
- **Transfert** : deduit du token source, cree un nouveau token pour le destinataire dans son wallet
- **Anti-volatilite** : le `conversionRate` est borne entre 0.5 et 2.0 pour empecher la speculation

### Couche Controller (`TokenController.java`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/tokens` | Creer un token |
| `GET` | `/api/tokens` | Lister tous les tokens |
| `GET` | `/api/tokens/{id}` | Token par ID |
| `GET` | `/api/tokens/wallet/{walletId}` | Tokens d'un wallet |
| `GET` | `/api/tokens/customer/{customerId}` | Tokens d'un client |
| `PUT` | `/api/tokens/{id}` | Modifier un token |
| `DELETE` | `/api/tokens/{id}` | Supprimer un token |
| `POST` | `/api/tokens/{id}/transfer?recipientCustomerId=X&amount=Y` | Transferer avec conversion |
| `GET` | `/api/tokens/wallet/{walletId}/total-value` | Valeur totale convertie |

### Regles metiers du Token

| Regle | Description |
|-------|-------------|
| Anti-volatilite | Taux de conversion borne entre 0.5 et 2.0 |
| Wallet actif | Le wallet doit etre ACTIVE pour creer un token |
| Valeur convertie | `valeur_convertie = value * conversionRate` |
| Transfert | Deduit du token source, cree un nouveau token chez le destinataire |

---

## 7. Gestion Asset — CRUD Complet

### Couche Repository (`AssetRepository.java`)

- `findByOwnerId(String)` — assets d'un proprietaire
- `findByAssetType(String)` — filtrer par type
- `findByWalletWalletId(Long)` — assets d'un wallet
- `findByStatus(String)` — filtrer par statut
- `getTotalAssetValueByOwnerId(String)` — valeur totale par proprietaire
- `getTotalAssetValueByWalletId(Long)` — valeur totale par wallet

### Couche Service (`AssetService.java`)

- **Enregistrement** : verifie le wallet ACTIVE, le type d'asset valide, la valeur max 10 000 000, puis credite 10% en collateral au wallet
- **Revalorisation** : ajuste le collateral proportionnellement (difference de 10%)
- **Transfert** : change le proprietaire, deplace le collateral entre wallets, met le statut a TRANSFERRED

### Couche Controller (`AssetController.java`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/assets` | Enregistrer un asset (+10% collateral) |
| `GET` | `/api/assets` | Lister tous les assets |
| `GET` | `/api/assets/{id}` | Asset par ID |
| `GET` | `/api/assets/wallet/{walletId}` | Assets d'un wallet |
| `GET` | `/api/assets/owner/{ownerId}` | Assets d'un proprietaire |
| `GET` | `/api/assets/type/{assetType}` | Assets par type |
| `PUT` | `/api/assets/{id}/value?newValue=X` | Revaloriser un asset |
| `PUT` | `/api/assets/{id}/transfer?newOwnerId=X` | Transferer la propriete |
| `DELETE` | `/api/assets/{id}` | Supprimer un asset |
| `GET` | `/api/assets/wallet/{walletId}/total-value` | Valeur totale des assets |

### Regles metiers de l'Asset

| Regle | Description |
|-------|-------------|
| Collateral 10% | Enregistrement d'un asset = +10% de sa valeur creditee au wallet |
| Ajustement collateral | Revalorisation = ajuste le collateral proportionnellement |
| Transfert collateral | Transfert d'asset = collateral debite du wallet source, credite au wallet cible |
| Types valides | REAL_ESTATE, VEHICLE, EQUIPMENT, DIGITAL, OTHER |
| Valeur max | Un asset ne peut pas depasser 10 000 000 |

---

## 8. Regles Metiers Avancees

### 8.1 Multi-Devises (CurrencyExchangeService)

Le service gere la conversion entre 8 devises avec des taux fixes par rapport a l'EUR :

| Devise | Taux (base EUR) |
|--------|-----------------|
| EUR | 1.00 |
| USD | 1.08 |
| GBP | 0.86 |
| CHF | 0.94 |
| JPY | 162.50 |
| TND | 3.35 |
| BTC | 0.000016 |
| ETH | 0.00031 |

**Frais de conversion** : 0.5% sur chaque echange.

**Endpoints** :

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/advanced/exchange/rates` | Voir tous les taux de change |
| `GET` | `/api/advanced/exchange/preview?from=X&to=Y&amount=Z` | Previsualiser une conversion |
| `POST` | `/api/advanced/exchange/{walletId}?targetCurrency=X&amount=Y` | Effectuer un echange |
| `POST` | `/api/advanced/exchange/cross-transfer?senderWalletId=X&recipientWalletId=Y&amount=Z` | Transfert cross-devise |

### 8.2 Programme de Fidelite (LoyaltyRewardsService)

Systeme de points avec 4 niveaux :

| Tier | Seuil de points | Multiplicateur |
|------|-----------------|----------------|
| BRONZE | 0 | x1.0 |
| SILVER | 500 | x1.5 |
| GOLD | 2000 | x2.0 |
| PLATINUM | 5000 | x3.0 |

- **Gain** : 10 points par 100 EUR de transaction (multiplie par le tier)
- **Echange** : 1 point = 0.10 EUR (credite au wallet)
- **Upgrade automatique** quand le seuil est atteint

**Endpoints** :

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/advanced/loyalty/{walletId}` | Voir le statut fidelite |
| `POST` | `/api/advanced/loyalty/{walletId}/earn?transactionAmount=X` | Gagner des points |
| `POST` | `/api/advanced/loyalty/{walletId}/redeem?points=X` | Echanger des points contre du cash |

### 8.3 Detection de Fraude (FraudDetectionService)

5 algorithmes de detection :

| Algorithme | Ce qu'il detecte | Seuil |
|-----------|-------------------|-------|
| **Velocity Check** | Transactions trop frequentes | > 10/heure ou < 30s entre 2 |
| **Amount Anomaly** | Montants anormalement eleves | > 5x la moyenne ou > 25 000 |
| **Round-Trip** | Aller-retour (blanchiment) | SEND puis RECEIVE meme montant |
| **Dormant Account** | Activite soudaine apres inactivite | Grosses transactions apres dormance |
| **Currency Abuse** | Trop d'echanges de devises | > 3 echanges recents |

**Niveaux de risque** :

| Score | Niveau | Action recommandee |
|-------|--------|-------------------|
| 0-19 | LOW | CLEAR — Aucune activite suspecte |
| 20-49 | MEDIUM | MONITOR — Surveillance renforcee |
| 50-79 | HIGH | FLAG_FOR_REVIEW — Examen manuel |
| 80+ | CRITICAL | BLOCK_ACCOUNT — Suspension immediate |

**Endpoint** :

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/advanced/fraud/{walletId}` | Analyser les transactions pour fraude |

---

## 9. Modele IA Python (Microservice Flask)

### Description

Un microservice Python independant qui fournit un **scoring de credit avance** utilisant un modele d'ensemble (Machine Learning). Il est entraine automatiquement au demarrage sur 5000 donnees synthetiques.

### Architecture du modele

**Algorithme** : Ensemble Voting (soft) de 3 modeles :
- **Random Forest** (poids 3) — 100 arbres, profondeur max 10
- **Gradient Boosting** (poids 2) — 100 estimateurs, learning rate 0.1
- **Logistic Regression** (poids 1) — regularisation C=1.0

### 12 Features d'entree

| Feature | Description |
|---------|-------------|
| `wallet_balance` | Solde du wallet |
| `total_asset_value` | Valeur totale des assets |
| `total_token_value` | Valeur totale des tokens |
| `total_transactions` | Nombre total de transactions |
| `late_payments` | Nombre de retards de paiement |
| `repayment_rate` | Taux de remboursement (0 a 1) |
| `asset_coverage_ratio` | Ratio assets / balance |
| `avg_transaction_amount` | Montant moyen des transactions |
| `transaction_frequency` | Frequence des transactions |
| `balance_volatility` | Volatilite du solde |
| `loyalty_points` | Points de fidelite |
| `account_age_days` | Age du compte en jours |

### Sortie du modele

| Champ | Description |
|-------|-------------|
| `credit_score` | Score 300-850 (type FICO) |
| `default_probability` | Probabilite de defaut (0 a 1) |
| `risk_level` | LOW / MEDIUM / HIGH / CRITICAL |
| `recommendation` | APPROVE / REVIEW / DENY avec justification |
| `max_allowed_transaction` | Montant max autorise |
| `model_predictions` | Prediction de chaque modele individuellement |

### Endpoints du service IA

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/health` | Verifier que le service est UP |
| `GET` | `/api/ai/model-info` | Informations du modele + feature importance |
| `POST` | `/api/ai/predict` | Prediction pour un client |
| `POST` | `/api/ai/predict/batch` | Predictions par lot |
| `POST` | `/api/ai/explain` | Prediction + explication detaillee |
| `POST` | `/api/ai/score` | Score de credit uniquement (leger) |
| `POST` | `/api/ai/train` | Re-entrainer le modele |

### Fichiers du service IA

```
ai-service/
  app.py           --> API REST Flask (7 endpoints)
  model.py         --> Classe WalletRiskModel (ensemble ML)
  requirements.txt --> Dependances Python
  Dockerfile       --> Image Python 3.11-slim + gunicorn
```

---

## 10. WebSocket — Notifications Temps Reel

### Configuration

Le fichier `WebSocketConfig.java` configure STOMP sur `/ws` avec SockJS :

- **Message Broker** : `/topic` (pour les abonnements)
- **Prefix application** : `/app` (pour les envois)
- **Endpoint** : `/ws` avec fallback SockJS

### Channels disponibles

| Topic | Description | Quand |
|-------|-------------|-------|
| `/topic/wallet/{id}` | Notifications d'un wallet specifique | Depot, retrait, transfert |
| `/topic/transactions` | Toutes les transactions en temps reel | Chaque transaction |
| `/topic/fraud-alerts` | Alertes de fraude | Detection de fraude |

### Service de notification (`NotificationService.java`)

3 types de notifications :
- **notifyTransaction** : envoie walletId, type, montant, details, timestamp
- **notifyFraudAlert** : envoie walletId, riskLevel, action recommandee
- **notifyLoyaltyEvent** : envoie walletId, event, points

### Exemple de connexion (JavaScript)

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, () => {
    // S'abonner aux transactions
    stompClient.subscribe('/topic/transactions', (msg) => {
        console.log('Transaction:', JSON.parse(msg.body));
    });
    // S'abonner aux alertes de fraude
    stompClient.subscribe('/topic/fraud-alerts', (msg) => {
        console.log('ALERTE FRAUDE:', JSON.parse(msg.body));
    });
    // S'abonner a un wallet specifique
    stompClient.subscribe('/topic/wallet/1', (msg) => {
        console.log('Wallet 1:', JSON.parse(msg.body));
    });
});
```

---

## 11. Analytics et Export CSV

### Service Analytics (`AnalyticsService.java`)

Genere des rapports automatiques toutes les 5 minutes (`@Scheduled`) et a la demande :

**Rapport global** :
- Nombre total de wallets
- Statistiques de solde (total, moyenne, min, max)
- Distribution par statut (ACTIVE, SUSPENDED)
- Distribution par devise (EUR, USD, etc.)
- Distribution par tier de fidelite
- Top wallets par solde
- Nombre de wallets a solde faible
- Resume des tokens et assets

**Rapport par wallet** :
- Solde, devise, statut
- Points de fidelite et tier
- Decomposition des transactions (depots, retraits, envois, receptions, echanges)
- Valeur totale des tokens et assets
- Valeur nette (balance + tokens + assets)

### Endpoints Analytics et Export

| Methode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/analytics/report` | Rapport analytique complet |
| `GET` | `/api/analytics/report/latest` | Dernier rapport schedule |
| `GET` | `/api/analytics/wallet/{walletId}` | Analytics d'un wallet |
| `GET` | `/api/analytics/export/wallets` | Export CSV de tous les wallets |
| `GET` | `/api/analytics/export/transactions/{walletId}` | Export CSV des transactions d'un wallet |

---

## 12. Eureka Discovery Server

### Role

Eureka est le **registre de services**. Chaque microservice s'y enregistre au demarrage. Le Gateway utilise Eureka pour trouver dynamiquement les services.

### Configuration (`eureka-server/src/main/resources/application.yml`)

- Port : **8761**
- Ne s'enregistre pas lui-meme (`register-with-eureka: false`)
- Ne recupere pas de registre (`fetch-registry: false`)
- Self-preservation desactivee pour le dev

### Dashboard

Ouvrir **http://localhost:8761** pour voir :
- Les services enregistres (wallet-service, gateway-service)
- Leur statut (UP/DOWN)
- Leur adresse IP et port

---

## 13. API Gateway

### Role

Le Gateway est le **point d'entree unique** de l'application. Tous les appels passent par le port 8088 et sont routes vers le bon service.

### Routes configurees (`gateway/src/main/resources/application.yml`)

| Route | Destination | Description |
|-------|-------------|-------------|
| `/api/wallets/**` | wallet-service | CRUD wallets |
| `/api/tokens/**` | wallet-service | CRUD tokens |
| `/api/assets/**` | wallet-service | CRUD assets |
| `/api/advanced/**` | wallet-service | Regles metiers avancees |
| `/api/analytics/**` | wallet-service | Analytics et export |
| `/swagger-ui/**` | wallet-service | Documentation Swagger |
| `/api/ai/**` | ai-service (Python :5000) | Modele IA |

### CORS

Le Gateway active CORS pour tous les origines (`*`), toutes les methodes et tous les headers.

---

## 14. Lancement du Projet (Docker) — Guide Complet

### Pre-requis

Avant de commencer, verifier que ces outils sont installes sur votre machine :

```bash
# Verifier Docker
docker --version
# Docker version 24.x ou superieur

# Verifier Docker Compose
docker-compose --version
# Docker Compose version 2.x ou superieur

# Verifier Java (optionnel, pour compilation locale)
java -version
# openjdk version "21.0.x"

# Verifier Maven (optionnel, pour compilation locale)
mvn -version
# Apache Maven 3.8.x
```

> **Note** : Java et Maven ne sont **pas necessaires** si vous lancez uniquement via Docker. Les Dockerfiles contiennent deja Maven et Java 21 pour la compilation.

---

### METHODE 1 : Tout lancer avec Docker Compose (recommande)

C'est la methode la plus simple. Une seule commande demarre les 5 microservices.

#### Etape 1 : Cloner le projet

```bash
git clone <repository-url>
cd equa
```

#### Etape 2 : Lancer tous les services

```bash
docker-compose up --build
```

> Cette commande va :
> 1. Telecharger l'image PostgreSQL 16
> 2. Compiler l'Eureka Server (Java 21 + Maven)
> 3. Compiler le Wallet Service (Java 21 + Maven)
> 4. Construire l'image AI Service (Python 3.11 + pip install)
> 5. Compiler le Gateway (Java 21 + Maven)
> 6. Demarrer les 5 conteneurs dans l'ordre correct

**Premier lancement** : ~3–5 minutes (telechargement des images + compilation Maven).
**Lancements suivants** : ~30 secondes (images en cache).

#### Etape 3 : Attendre que tout soit "healthy"

Les services demarrent dans cet ordre grace aux `depends_on` et `healthcheck` :

```
1. equa-postgres   (port 5432) ← demarre en premier
      ↓ healthy
2. equa-eureka     (port 8761) ← attend que PostgreSQL soit pret
      ↓ healthy
3. equa-ai         (port 5000) ← demarre independamment
      ↓ healthy
4. equa-wallet     (port 8080) ← attend PostgreSQL + Eureka
      ↓ healthy
5. equa-gateway    (port 8088) ← attend Eureka + Wallet + AI
```

#### Etape 4 : Verifier que les 5 conteneurs sont UP

```bash
docker ps
```

Resultat attendu (5 conteneurs) :

```
CONTAINER ID   IMAGE              STATUS                   PORTS                    NAMES
xxxxxxxxxxxx   equa-gateway       Up                       0.0.0.0:8088->8088/tcp   equa-gateway
xxxxxxxxxxxx   equa-wallet-...    Up (healthy)             0.0.0.0:8080->8080/tcp   equa-wallet
xxxxxxxxxxxx   equa-ai-service    Up (healthy)             0.0.0.0:5000->5000/tcp   equa-ai
xxxxxxxxxxxx   equa-eureka-...    Up (healthy)             0.0.0.0:8761->8761/tcp   equa-eureka
xxxxxxxxxxxx   postgres:16-alpine Up (healthy)             0.0.0.0:5432->5432/tcp   equa-postgres
```

> **Si un conteneur n'est pas "healthy"**, attendez 30 secondes et refaites `docker ps`. Les healthchecks prennent du temps.

#### Etape 5 : Verifier chaque service individuellement

Ouvrir ces URLs dans le navigateur :

| # | Service | URL | Resultat attendu |
|---|---------|-----|-----------------|
| 1 | **PostgreSQL** | — (pas d'UI) | Voir `docker ps` → healthy |
| 2 | **Eureka Dashboard** | http://localhost:8761 | Page Eureka avec WALLET-SERVICE enregistre |
| 3 | **Wallet Service (Swagger)** | http://localhost:8080/swagger-ui/index.html | Swagger UI avec tous les endpoints |
| 4 | **AI Service (Health)** | http://localhost:5000/health | `{"status": "UP", "model_trained": true}` |
| 5 | **Gateway** | http://localhost:8088/api/wallets | Repond via le proxy (meme resultat que :8080) |

#### Etape 6 : Tester un endpoint rapide

```bash
# Creer un wallet via le Wallet Service direct
curl -X POST http://localhost:8080/api/wallets \
  -H "Content-Type: application/json" \
  -d '{"customerId": 1, "balance": 0}'

# Creer un wallet via le Gateway
curl -X POST http://localhost:8088/api/wallets \
  -H "Content-Type: application/json" \
  -d '{"customerId": 2, "balance": 0}'

# Tester l'IA
curl http://localhost:8080/api/ai/health

# Tester Eureka
curl http://localhost:8761/eureka/apps
```

Si tout repond → **le projet est 100% fonctionnel** !

---

### METHODE 2 : Lancer les services un par un (debug)

Utile si vous voulez voir les logs de chaque service separement.

#### Etape 1 : Demarrer PostgreSQL

```bash
docker-compose up -d postgres
```

Verifier :
```bash
docker ps | grep postgres
# equa-postgres ... Up (healthy)
```

#### Etape 2 : Demarrer Eureka Server

```bash
docker-compose up --build -d eureka-server
```

Attendre ~30s puis verifier :
```bash
curl -s http://localhost:8761/actuator/health
# {"status":"UP"}
```

Ou ouvrir http://localhost:8761 dans le navigateur.

#### Etape 3 : Demarrer le service IA Python

```bash
docker-compose up --build -d ai-service
```

Attendre ~20s puis verifier :
```bash
curl -s http://localhost:5000/health
# {"status":"UP","model_trained":true,"model_version":"2.0.0"}
```

#### Etape 4 : Demarrer le Wallet Service

```bash
docker-compose up --build -d wallet-service
```

Attendre ~40s puis verifier :
```bash
curl -s http://localhost:8080/api/wallets
# [] (liste vide, normal au premier lancement)
```

Ou ouvrir http://localhost:8080/swagger-ui/index.html

#### Etape 5 : Demarrer le Gateway

```bash
docker-compose up --build -d gateway
```

Verifier :
```bash
curl -s http://localhost:8088/api/wallets
# [] (meme resultat, via le proxy Gateway)
```

#### Verification finale : tous les services

```bash
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

```
NAMES            STATUS           PORTS
equa-gateway     Up               0.0.0.0:8088->8088/tcp
equa-wallet      Up (healthy)     0.0.0.0:8080->8080/tcp
equa-ai          Up (healthy)     0.0.0.0:5000->5000/tcp
equa-eureka      Up (healthy)     0.0.0.0:8761->8761/tcp
equa-postgres    Up (healthy)     0.0.0.0:5432->5432/tcp
```

---

### METHODE 3 : Lancer localement sans Docker (developpement)

Si vous voulez lancer les services Java directement sur votre machine (pour le developpement avec hot-reload).

#### Pre-requis obligatoires

- Java 21 installe (`java -version`)
- Maven 3.8+ installe (`mvn -version`)
- PostgreSQL 16 installe et lance
- Python 3.11 installe (`python3 --version`)

#### Etape 1 : Creer la base de donnees

```bash
sudo -u postgres psql -c "CREATE USER equauser WITH PASSWORD 'equapass';"
sudo -u postgres psql -c "CREATE DATABASE equadb OWNER equauser;"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE equadb TO equauser;"
```

#### Etape 2 : Compiler et lancer Eureka Server

```bash
cd eureka-server
mvn clean package -DskipTests
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
```

Attendre `Started EurekaServerApplication` dans les logs.

#### Etape 3 : Lancer le service IA Python

```bash
cd ai-service
pip install -r requirements.txt
python app.py
```

Attendre `Model trained successfully` dans les logs.

#### Etape 4 : Compiler et lancer le Wallet Service

```bash
cd equa   # racine du projet
mvn clean package -DskipTests
java -jar target/walletManagement-0.0.1-SNAPSHOT.jar
```

Attendre `Started Application` dans les logs.

#### Etape 5 : Compiler et lancer le Gateway

```bash
cd gateway
mvn clean package -DskipTests
java -jar target/gateway-0.0.1-SNAPSHOT.jar
```

---

### Tableau recapitulatif des ports

| Service | Port | URL de test |
|---------|------|-------------|
| **PostgreSQL** | 5432 | `psql -h localhost -U equauser equadb` |
| **Eureka Server** | 8761 | http://localhost:8761 |
| **AI Service (Python)** | 5000 | http://localhost:5000/health |
| **Wallet Service** | 8080 | http://localhost:8080/swagger-ui/index.html |
| **API Gateway** | 8088 | http://localhost:8088/api/wallets |

---

### Commandes Docker utiles

```bash
# ========== LANCER ==========

# Tout lancer (avec build, logs visibles)
docker-compose up --build

# Tout lancer en arriere-plan
docker-compose up --build -d

# Lancer un seul service
docker-compose up --build -d wallet-service

# ========== LOGS ==========

# Voir les logs de tous les services
docker-compose logs -f

# Voir les logs d'un seul service
docker-compose logs -f wallet-service
docker-compose logs -f ai-service
docker-compose logs -f eureka-server
docker-compose logs -f gateway
docker-compose logs -f postgres

# ========== ARRETER ==========

# Arreter tous les services
docker-compose down

# Arreter + supprimer les donnees (base vide au prochain lancement)
docker-compose down -v

# ========== RECONSTRUIRE ==========

# Reconstruire un seul service (apres modification du code)
docker-compose up --build wallet-service -d

# Reconstruire tout depuis zero (sans cache)
docker-compose build --no-cache
docker-compose up -d

# ========== DEBUG ==========

# Voir les conteneurs en cours
docker ps

# Voir les conteneurs arretes aussi
docker ps -a

# Entrer dans un conteneur (ex: wallet-service)
docker exec -it equa-wallet bash

# Entrer dans la base PostgreSQL
docker exec -it equa-postgres psql -U equauser equadb

# Voir les tables
docker exec -it equa-postgres psql -U equauser equadb -c "\dt"

# Voir les wallets en base
docker exec -it equa-postgres psql -U equauser equadb -c "SELECT * FROM wallet;"

# ========== NETTOYER ==========

# Supprimer tous les conteneurs et images du projet
docker-compose down --rmi all -v

# Nettoyer tout Docker (attention: supprime TOUT)
docker system prune -a
```

---

### Resoudre les problemes courants

| Probleme | Cause | Solution |
|----------|-------|----------|
| `Port 5432 already in use` | PostgreSQL local deja lance | `sudo systemctl stop postgresql` puis relancer |
| `Port 8080 already in use` | Un autre service utilise ce port | `lsof -i :8080` puis `kill <PID>` |
| Eureka: `Connection refused` | Eureka pas encore demarre | Attendre 30s, les healthchecks gerent l'ordre |
| Wallet: `Connection refused to postgres` | PostgreSQL pas encore healthy | `docker-compose down` puis `docker-compose up --build` |
| AI Service: `ModuleNotFoundError` | Dependances Python manquantes | `docker-compose build --no-cache ai-service` |
| Gateway: `503 Service Unavailable` | Wallet pas encore enregistre sur Eureka | Attendre 60s que l'enregistrement Eureka se fasse |
| `No space left on device` | Docker manque d'espace disque | `docker system prune -a` |
| Build Maven echoue | Cache Maven corrompu | `docker-compose build --no-cache` |

---

## 15. Guide de Test Swagger — Etape par Etape

### Ouvrir Swagger

Aller sur : **http://localhost:8080/swagger-ui/index.html**

Vous verrez 5 sections :
- **Wallet Management** — CRUD wallets
- **Token Management** — CRUD tokens
- **Asset Management** — CRUD assets
- **Advanced Business Rules** — Multi-devises, fidelite, fraude
- **Analytics & Export** — Rapports et CSV

> **Comment utiliser** : Cliquer sur un endpoint -> "Try it out" -> Remplir les champs -> "Execute"

---

### ETAPE 1 : Creer les Wallets

**`POST /api/wallets`** — Creer wallet client 1 :
```json
{
  "customerId": 1,
  "balance": 0
}
```
Resultat : `walletId: 1`, `status: ACTIVE`, `currency: EUR`, `loyaltyTier: BRONZE`

**`POST /api/wallets`** — Creer wallet client 2 :
```json
{
  "customerId": 2,
  "balance": 0
}
```
Resultat : `walletId: 2`

---

### ETAPE 2 : Deposer des fonds

**`POST /api/wallets/1/deposit`** — id=`1`, amount=`5000`

Resultat : `balance: 5000.0`, transactionHistory contient `"DEPOSIT | +5000.0"`

**`POST /api/wallets/2/deposit`** — id=`2`, amount=`3000`

Resultat : `balance: 3000.0`

---

### ETAPE 3 : Retirer des fonds

**`POST /api/wallets/1/withdraw`** — id=`1`, amount=`500`

Resultat : `balance: 4500.0`, transactionHistory contient `"WITHDRAW | -500.0"`

**Test erreur** : `POST /api/wallets/1/deposit` avec amount=`60000`
- Erreur 400 : `"Transaction amount 60000.0 exceeds maximum allowed: 50000.0"`

---

### ETAPE 4 : Transferer entre wallets

**`POST /api/wallets/1/transfer`** — id=`1`, body :
```json
{
  "amount": 1000,
  "recipientWalletId": 2
}
```

Resultat : Wallet 1 balance=3500, Wallet 2 balance=4000. Les deux historiques sont mis a jour.

---

### ETAPE 5 : Suspendre / Activer

**`PUT /api/wallets/1/suspend`** — Resultat : `status: SUSPENDED`

**`POST /api/wallets/1/deposit`** avec amount=`100`
- Erreur 400 : `"Wallet is not active. Current status: SUSPENDED"`

**`PUT /api/wallets/1/activate`** — Resultat : `status: ACTIVE`

---

### ETAPE 6 : Creer des Tokens

**`POST /api/tokens`** :
```json
{
  "value": 1000,
  "customerId": 1,
  "conversionRate": 1.0,
  "totalSupply": 5000,
  "walletId": 1
}
```
Resultat : `tokenId: 1`

**`POST /api/tokens`** — 2eme token (taux 1.5) :
```json
{
  "value": 2000,
  "customerId": 1,
  "conversionRate": 1.5,
  "totalSupply": 3000,
  "walletId": 1
}
```

**Test anti-volatilite** : `POST /api/tokens` avec `conversionRate: 5.0`
- Erreur 400 : `"Conversion rate must be between 0.5 and 2.0"`

---

### ETAPE 7 : Transferer des Tokens

**`POST /api/tokens/1/transfer`** — tokenId=`1`, recipientCustomerId=`2`, amount=`300`

Resultat : Token 1 value=700 (1000-300). Un nouveau token de 300 est cree dans le wallet 2.

Verifier : `GET /api/tokens/customer/2`

---

### ETAPE 8 : Enregistrer des Assets

**`POST /api/assets`** :
```json
{
  "ownerId": "1",
  "assetType": "REAL_ESTATE",
  "value": 100000,
  "walletId": 1
}
```
Resultat : `assetId: 1`, `status: REGISTERED`

**Verifier le collateral** : `GET /api/wallets/1`
- Le solde a augmente de +10 000 (10% de 100 000)

**`POST /api/assets`** — 2eme asset :
```json
{
  "ownerId": "1",
  "assetType": "VEHICLE",
  "value": 25000,
  "walletId": 1
}
```
Le wallet recoit +2 500 en collateral.

---

### ETAPE 9 : Revaloriser et Transferer un Asset
/api/assets/{id}/value
Update asset value (with collateral adjustment)
**`PUT /api/assets/1/value`** — assetId=`1`, newValue=`150000`

Le collateral est ajuste : +5 000 (10% de la difference 50 000).

**`PUT /api/assets/2/transfer`** — assetId=`2`, newOwnerId=`2`

L'asset passe au wallet 2, le collateral (2 500) est debite du wallet 1 et credite au wallet 2.

---

### ETAPE 10 : Multi-Devises

**`GET /api/advanced/exchange/rates`** — Voir tous les taux

**`GET /api/advanced/exchange/preview`** — from=`EUR`, to=`USD`, amount=`1000`
- Resultat : `convertedAmount: 1080`, `fee: 5.4`, `finalAmount: 1074.6`

**`POST /api/advanced/exchange/1`** — walletId=`1`, targetCurrency=`USD`, amount=`1000`
- 1000 EUR convertis en 1074.6 USD (apres frais 0.5%)

---

### ETAPE 11 : Programme de Fidelite

**`POST /api/advanced/loyalty/1/earn`** — walletId=`1`, transactionAmount=`5000`
- Resultat : +500 points, tier upgrade BRONZE -> SILVER

**`POST /api/advanced/loyalty/1/earn`** — transactionAmount=`10000`
- Avec SILVER (x1.5) : +1500 points, tier upgrade SILVER -> GOLD

**`POST /api/advanced/loyalty/1/redeem`** — walletId=`1`, points=`200`
- 200 points = 20 EUR credites au wallet

**`GET /api/advanced/loyalty/1`** — Voir le statut complet

---

### ETAPE 12 : Detection de Fraude

**`GET /api/advanced/fraud/1`** — walletId=`1`

Resultat : score de risque, niveau (LOW/MEDIUM/HIGH/CRITICAL), alertes detectees, action recommandee.

---

### ETAPE 13 : Scoring IA Java

**`GET /api/wallets/risk/1`** — customerId=`1`

Resultat :
```json
{
  "creditScore": 557,
  "riskLevel": "HIGH",
  "predictedDefaultProbability": 0.24,
  "recommendation": "DENY - High risk profile...",
  "maxAllowedTransaction": 16797.3,
  "financialIndicators": { ... }
}
```

---

### ETAPE 14 : IA Python (Testable dans Swagger)

#### Role du microservice IA Python

Le microservice IA est un **service Python Flask independant** qui fournit un **scoring de credit avance** base sur le Machine Learning. Il utilise un **modele d'ensemble** (Ensemble Voting) compose de 3 algorithmes :

- **Random Forest** (poids 3) — 100 arbres de decision
- **Gradient Boosting** (poids 2) — 100 estimateurs, learning rate 0.1
- **Logistic Regression** (poids 1) — regularisation C=1.0

Le modele est **entraine automatiquement** au demarrage du service sur 5000 donnees synthetiques. Il analyse **12 features financieres** d'un client et retourne :

| Sortie | Description |
|--------|-------------|
| `credit_score` | Score de credit entre 300 et 850 (type FICO) |
| `default_probability` | Probabilite de defaut de paiement (0 a 1) |
| `risk_level` | LOW / MEDIUM / HIGH / CRITICAL |
| `recommendation` | APPROVE / REVIEW / DENY avec justification |
| `max_allowed_transaction` | Montant maximum autorise pour ce client |
| `model_predictions` | Prediction individuelle de chaque algorithme |

#### Comment tester dans Swagger

> L'IA Python est maintenant accessible dans Swagger grace au proxy `AIServiceController.java` qui redirige les appels vers le service Python.

Ouvrir **http://localhost:8080/swagger-ui/index.html** → section **"AI Service (Python)"**

#### Test 14a : Verifier le statut de l'IA

**`GET /api/ai/health`** → "Try it out" → "Execute"

Resultat attendu :
```json
{
  "status": "UP",
  "model_trained": true,
  "model_version": "2.0.0",
  "service": "equa-ai-service"
}
```

#### Test 14b : Voir les infos du modele

**`GET /api/ai/model-info`** → "Try it out" → "Execute"

Resultat : nom de l'algorithme, liste des 12 features, importance de chaque feature, metriques de performance (accuracy, precision, recall).

#### Test 14c : Prediction de risque pour un client

**`POST /api/ai/predict`** → "Try it out" → Coller ce JSON dans le body :

```json
{
  "wallet_balance": 15000,
  "total_asset_value": 150000,
  "total_token_value": 4000,
  "total_transactions": 25,
  "late_payments": 0,
  "repayment_rate": 0.85,
  "asset_coverage_ratio": 10.0,
  "avg_transaction_amount": 600,
  "transaction_frequency": 5,
  "balance_volatility": 0.2,
  "loyalty_points": 2000,
  "account_age_days": 365
}
```

Execute → Resultat attendu :
```json
{
  "credit_score": 557,
  "default_probability": 0.0,
  "risk_level": "HIGH",
  "recommendation": "DENY - High risk. Score: 557. Default prob: 0.0%. Require collateral.",
  "max_allowed_transaction": 15000.0,
  "model_predictions": {
    "random_forest": 0.0,
    "gradient_boosting": 0.0,
    "logistic_regression": 0.0,
    "ensemble": 0.0
  }
}
```

#### Explication des 12 features d'entree

| Feature | Description | Exemple |
|---------|-------------|---------|
| `wallet_balance` | Solde actuel du wallet | 15000 |
| `total_asset_value` | Valeur totale des assets enregistres | 150000 |
| `total_token_value` | Valeur totale des tokens | 4000 |
| `total_transactions` | Nombre total de transactions effectuees | 25 |
| `late_payments` | Nombre de retards de paiement | 0 |
| `repayment_rate` | Taux de remboursement (0.0 a 1.0) | 0.85 |
| `asset_coverage_ratio` | Ratio assets / balance (couverture) | 10.0 |
| `avg_transaction_amount` | Montant moyen des transactions | 600 |
| `transaction_frequency` | Frequence des transactions par mois | 5 |
| `balance_volatility` | Volatilite du solde (0 = stable, 1 = instable) | 0.2 |
| `loyalty_points` | Points de fidelite accumules | 2000 |
| `account_age_days` | Age du compte en jours | 365 |

#### Test 14d : Explication detaillee (pourquoi ce score ?)

**`POST /api/ai/explain`** → Meme JSON que ci-dessus → Execute

Resultat : la prediction + les **top facteurs de risque** avec leur contribution au score. Utile pour comprendre **pourquoi** un client est juge a risque.

#### Test 14e : Score leger (rapide)

**`POST /api/ai/score`** → Meme JSON → Execute

Retourne uniquement `credit_score` et `risk_level` (plus rapide, moins de donnees).

#### Test 14f : Client a haut risque (pour comparer)

**`POST /api/ai/predict`** → Coller ce JSON (client risque) :

```json
{
  "wallet_balance": 100,
  "total_asset_value": 0,
  "total_token_value": 0,
  "total_transactions": 2,
  "late_payments": 5,
  "repayment_rate": 0.2,
  "asset_coverage_ratio": 0.0,
  "avg_transaction_amount": 50,
  "transaction_frequency": 0,
  "balance_volatility": 0.9,
  "loyalty_points": 0,
  "account_age_days": 30
}
```

Resultat attendu : `risk_level: CRITICAL`, `default_probability` elevee, `recommendation: DENY`

---

### ETAPE 15 : Analytics et Export

**`GET /api/analytics/report`** — Rapport global de la plateforme

**`GET /api/analytics/wallet/1`** — Analytics detailles du wallet 1 :
- Net worth, decomposition des transactions, tokens, assets

**`GET /api/analytics/export/wallets`** — Telecharge un fichier CSV avec tous les wallets

**`GET /api/analytics/export/transactions/1`** — Telecharge un CSV des transactions du wallet 1

---

### ETAPE 16 : Verifier les autres services

**Eureka Dashboard** : http://localhost:8761
- Vous verrez wallet-service et gateway-service enregistres

**Gateway** : http://localhost:8088/api/wallets
- Meme resultat que http://localhost:8080/api/wallets (le gateway reroute)

> `http://localhost:8088` seul donne une erreur Whitelabel — c'est **normal**, il n'y a pas de page d'accueil. Il faut toujours ajouter le chemin (`/api/wallets`, `/api/tokens`, etc.)

> `http://localhost:5000` seul donne "Not Found" — c'est **normal**, utilisez `/health`

---

## 16. Resume des Regles Metiers

| # | Regle | Service | Type |
|---|-------|---------|------|
| 1 | Max transaction 50 000 | WalletService | Securite |
| 2 | Solde minimum 10 | WalletService | Securite |
| 3 | Wallet ACTIVE requis pour toute operation | WalletService | Validation |
| 4 | Client unique par wallet | WalletService | Validation |
| 5 | Collateral 10% automatique | AssetService | Finance |
| 6 | Ajustement collateral a la revalorisation | AssetService | Finance |
| 7 | Transfert collateral entre wallets | AssetService | Finance |
| 8 | Anti-volatilite [0.5, 2.0] | TokenService | Regulation |
| 9 | Audit complet (transactionHistory) | Tous les services | Tracabilite |
| 10 | Scoring IA Java (300-850) | RiskAssessmentService | IA |
| 11 | Recommandation auto (APPROVE/REVIEW/DENY) | RiskAssessmentService | IA |
| 12 | Multi-devises + frais 0.5% | CurrencyExchangeService | Avance |
| 13 | Fidelite (points + tiers BRONZE->PLATINUM) | LoyaltyRewardsService | Avance |
| 14 | Detection de fraude (5 algorithmes) | FraudDetectionService | Avance |

### 3 Valeurs Ajoutees

| # | Valeur Ajoutee | Description |
|---|---------------|-------------|
| 1 | **WebSocket temps reel** | Notifications STOMP pour transactions et alertes fraude |
| 2 | **Analytics schedule** | Rapports automatiques toutes les 5 min + dashboard API |
| 3 | **Export CSV** | Telechargement des donnees wallets et transactions en CSV |
### 3 Valeurs Ajoutees

| # | Valeur Ajoutee | Description |
|---|---------------|-------------|
| 1 | **WebSocket temps reel** | Notifications STOMP pour transactions et alertes fraude |
| 2 | **Analytics schedule** | Rapports automatiques toutes les 5 min + dashboard API |
| 3 | **Export CSV** | Telechargement des donnees wallets et transactions en CSV |

---

## 17. Structure du Projet

```
equa/
  src/main/java/com/rayen/
    Application.java                        -> Main + @EnableScheduling + @EnableDiscoveryClient
    config/
      OpenApiConfig.java                    -> Configuration Swagger/OpenAPI
      WebSocketConfig.java                  -> Configuration WebSocket STOMP
    walletManagement/
      entity/
        Wallet.java                         -> Entite JPA (balance, currency, loyalty, relations)
        Token.java                          -> Entite JPA (ManyToOne -> Wallet)
        Asset.java                          -> Entite JPA (ManyToOne -> Wallet)
      model/
        WalletDTO.java                      -> DTO Wallet (API <-> Service)
        TokenDTO.java                       -> DTO Token
        AssetDTO.java                       -> DTO Asset
        TransferRequest.java                -> DTO pour les transferts
        RiskAssessmentDTO.java              -> DTO pour le scoring IA
      repository/
        WalletRepository.java              -> Requetes JPA Wallet
        TokenRepository.java               -> Requetes JPA Token
        AssetRepository.java               -> Requetes JPA Asset
      service/
        WalletService.java                 -> CRUD + depot/retrait/transfert
        TokenService.java                  -> CRUD + anti-volatilite + transfert
        AssetService.java                  -> CRUD + collateral 10%
        RiskAssessmentService.java         -> Scoring IA Java (regression logistique)
        CurrencyExchangeService.java       -> Multi-devises + conversion
        LoyaltyRewardsService.java         -> Programme fidelite + tiers
        FraudDetectionService.java         -> Detection de fraude (5 algorithmes)
        AnalyticsService.java              -> Rapports + @Scheduled
        NotificationService.java           -> WebSocket notifications
      controller/
        WalletController.java              -> REST CRUD wallet + risque
        TokenController.java               -> REST CRUD token + transfert
        AssetController.java               -> REST CRUD asset + collateral
        AdvancedBusinessController.java    -> Exchange + Loyalty + Fraud
        AnalyticsExportController.java     -> Analytics + CSV export
        GlobalExceptionHandler.java        -> Gestion globale des erreurs

  ai-service/                               -> Microservice Python IA
    app.py                                  -> API Flask (7 endpoints)
    model.py                                -> Ensemble ML (RF + GB + LR)
    requirements.txt                        -> Dependances Python
    Dockerfile                              -> Image Python 3.11

  eureka-server/                            -> Service Discovery
    src/.../EurekaServerApplication.java    -> @EnableEurekaServer
    src/.../application.yml                 -> Port 8761
    pom.xml                                 -> Spring Cloud Netflix Eureka
    Dockerfile                              -> Java 21

  gateway/                                  -> API Gateway
    src/.../GatewayApplication.java         -> @EnableDiscoveryClient
    src/.../application.yml                 -> Routes + CORS
    pom.xml                                 -> Spring Cloud Gateway
    Dockerfile                              -> Java 21

  docker-compose.yml                        -> 5 services orchestres
  Dockerfile                                -> Build wallet-service (Java 21)
  pom.xml                                   -> Maven + Spring Cloud
  README-COMPLET.md                         -> Ce fichier
```
