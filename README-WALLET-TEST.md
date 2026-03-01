# 💰 Wallet Management — Guide Complet : Run & Test

## Tech Stack
- **Java**: 21.0.9
- **Spring Boot**: 3.2.1
- **Maven**: 3.8.8
- **Database**: PostgreSQL 16
- **Docker**: Containerisation
- **Swagger/OpenAPI**: Documentation API

---

## 🚀 Comment Lancer le Projet

### Étape 1 : Cloner le Repository
```bash
git clone <repository-url>
cd equa
```

### Étape 2 : Créer la Base de Données PostgreSQL

**Option A — PostgreSQL local déjà installé :**
```bash
sudo -u postgres psql -c "CREATE USER equauser WITH PASSWORD 'equapass';"
sudo -u postgres psql -c "CREATE DATABASE equadb OWNER equauser;"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE equadb TO equauser;"
```

> Si le rôle ou la base existe déjà, ignorez l'erreur.

**Option B — Via Docker (si pas de PostgreSQL local) :**
```bash
docker-compose up -d postgres
```

Vérifier :
```bash
docker ps
# equa-postgres ... Up ... 5432/tcp
```

### Étape 3 : Compiler
```bash
mvn clean compile
```

Résultat attendu :
```
[INFO] Compiling 43 source files with javac
[INFO] BUILD SUCCESS
```

### Étape 4 : Lancer l'Application
```bash
mvn spring-boot:run
```

Résultat attendu :
```
Hibernate: create table wallets ...
Hibernate: create table tokens ...
Hibernate: create table assets ...
Started Application in X.XXX seconds
Tomcat started on port 8080 (http)
```

### Étape 5 : Ouvrir Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

Vous verrez 3 sections :
- **wallet-controller** — Gestion des wallets
- **token-controller** — Gestion des tokens
- **asset-controller** — Gestion des assets

---

## 🧪 Guide de Test Complet — Étape par Étape

> **Important** : Suivez les étapes dans l'ordre. Chaque étape dépend de la précédente.
>
> Dans Swagger : cliquez sur l'endpoint → **"Try it out"** → remplir les champs → **"Execute"**

---

### 📋 ÉTAPE 1 : CRUD Wallet

#### 1.1 — Créer le Wallet du Client 1
**`POST /api/wallets`**

Body :
```json
{
  "customerId": 1,
  "balance": 0
}
```

Réponse attendue (HTTP 200) :
```json
{
  "walletId": 1,
  "balance": 0.0,
  "customerId": 1,
  "publicKey": "PK-XXXXXXXX-XXXX-XX",
  "status": "ACTIVE",
  "transactionHistory": [],
  "tokens": [],
  "assets": []
}
```

> **Explication** : Un wallet est créé avec solde 0, clé publique auto-générée (UUID), statut ACTIVE.

#### 1.2 — Créer le Wallet du Client 2
**`POST /api/wallets`**

Body :
```json
{
  "customerId": 2,
  "balance": 0
}
```

#### 1.3 — Lire tous les Wallets
**`GET /api/wallets`**

Réponse : Liste de 2 wallets avec tokens[] et assets[] vides.

#### 1.4 — Lire un Wallet par ID
**`GET /api/wallets/1`**

Réponse : Wallet complet du client 1 avec relations.

#### 1.5 — Lire un Wallet par Customer ID
**`GET /api/wallets/customer/1`**

Réponse : Wallet associé au customerId 1.

#### 1.6 — Modifier un Wallet
**`PUT /api/wallets/1`**

Body :
```json
{
  "status": "ACTIVE"
}
```

#### 1.7 — Supprimer un Wallet (test de règle métier)
**`DELETE /api/wallets/1`**

> **Règle** : La suppression sera bloquée si le wallet a un solde > 0 ou des tokens/assets attachés.

---

### 💰 ÉTAPE 2 : Règles Métiers — Dépôt & Retrait

#### 2.1 — Déposer 5000 dans le Wallet 1
**`POST /api/wallets/1/deposit?amount=5000`**

Réponse attendue :
```json
{
  "walletId": 1,
  "balance": 5000.0,
  "transactionHistory": [
    "2026-02-26T20:12:51 | DEPOSIT | +5000.0"
  ]
}
```

> **Explication** : Le solde passe de 0 à 5000. L'opération est tracée dans transactionHistory.

#### 2.2 — Déposer 3000 dans le Wallet 2
**`POST /api/wallets/2/deposit?amount=3000`**

#### 2.3 — Retirer 500 du Wallet 1
**`POST /api/wallets/1/withdraw?amount=500`**

Réponse attendue :
```json
{
  "balance": 4500.0,
  "transactionHistory": [
    "... | DEPOSIT | +5000.0",
    "... | WITHDRAW | -500.0"
  ]
}
```

#### 2.4 — Tester la limite de transaction (max 50 000)
**`POST /api/wallets/1/deposit?amount=60000`**

Réponse attendue (HTTP 400) :
```json
{
  "error": "Transaction amount 60000.0 exceeds maximum allowed: 50000.0",
  "status": 400
}
```

> **Règle métier** : Toute transaction > 50 000 est rejetée.

#### 2.5 — Tester le retrait insuffisant
**`POST /api/wallets/1/withdraw?amount=999999`**

Réponse attendue (HTTP 400) :
```json
{
  "error": "Insufficient balance. Available: 4500.0, Requested: 999999.0",
  "status": 400
}
```

---

### 🔄 ÉTAPE 3 : Règles Métiers — Transfert entre Wallets

#### 3.1 — Transférer 1000 du Wallet 1 vers le Wallet 2
**`POST /api/wallets/1/transfer`**

Body :
```json
{
  "amount": 1000,
  "recipientWalletId": 2
}
```

Réponse attendue (Wallet 1) :
```json
{
  "balance": 3500.0,
  "transactionHistory": [
    "... | DEPOSIT | +5000.0",
    "... | WITHDRAW | -500.0",
    "... | SEND | -1000.0 | to: PK-XXXXXXXX"
  ]
}
```

Vérifier le Wallet 2 : **`GET /api/wallets/2`**
```json
{
  "balance": 4000.0,
  "transactionHistory": [
    "... | DEPOSIT | +3000.0",
    "... | RECEIVE | +1000.0 | from: PK-XXXXXXXX"
  ]
}
```

> **Explication** : 1000 débité du wallet 1, crédité au wallet 2. Les deux historiques sont mis à jour avec SEND et RECEIVE.

---

### ⛔ ÉTAPE 4 : Règles Métiers — Suspension / Activation

#### 4.1 — Suspendre le Wallet 1
**`PUT /api/wallets/1/suspend`**

Réponse :
```json
{
  "status": "SUSPENDED"
}
```

#### 4.2 — Tenter un dépôt sur un wallet SUSPENDED
**`POST /api/wallets/1/deposit?amount=100`**

Réponse attendue (HTTP 400) :
```json
{
  "error": "Wallet is not active. Current status: SUSPENDED",
  "status": 400
}
```

> **Règle métier** : Aucune opération financière n'est possible sur un wallet suspendu.

#### 4.3 — Réactiver le Wallet 1
**`PUT /api/wallets/1/activate`**

Réponse :
```json
{
  "status": "ACTIVE"
}
```

> Le wallet peut maintenant recevoir des opérations.

---

### 🪙 ÉTAPE 5 : CRUD Token + Relations

#### 5.1 — Créer un Token pour le Wallet 1
**`POST /api/tokens`**

Body :
```json
{
  "value": 1000,
  "customerId": 1,
  "conversionRate": 1.0,
  "totalSupply": 5000,
  "walletId": 1
}
```

Réponse attendue :
```json
{
  "tokenId": 1,
  "value": 1000.0,
  "customerId": 1,
  "conversionRate": 1.0,
  "totalSupply": 5000,
  "walletId": 1
}
```

> **Relation** : Le token est lié au wallet 1 via `walletId`. C'est une relation `@ManyToOne`.

#### 5.2 — Créer un 2ème Token (taux de conversion 1.5)
**`POST /api/tokens`**

Body :
```json
{
  "value": 2000,
  "customerId": 1,
  "conversionRate": 1.5,
  "totalSupply": 3000,
  "walletId": 1
}
```

#### 5.3 — Tester l'anti-volatilité (taux > 2.0 = rejeté)
**`POST /api/tokens`**

Body :
```json
{
  "value": 500,
  "customerId": 1,
  "conversionRate": 5.0,
  "totalSupply": 1000,
  "walletId": 1
}
```

Réponse attendue (HTTP 400) :
```json
{
  "error": "Conversion rate must be between 0.5 and 2.0 for stability",
  "status": 400
}
```

> **Règle anti-volatilité** : Le taux de conversion est borné entre 0.5 et 2.0 pour éviter la spéculation.

#### 5.4 — Voir les Tokens du Wallet 1 (relation 1..*)
**`GET /api/tokens/wallet/1`**

Réponse : Liste de 2 tokens rattachés au wallet 1.

#### 5.5 — Voir la valeur totale convertie
**`GET /api/tokens/wallet/1/total-value`**

Réponse : Somme de `value × conversionRate` pour chaque token.

#### 5.6 — Transférer des Tokens entre clients
**`POST /api/tokens/1/transfer?recipientCustomerId=2&amount=300`**

Réponse attendue :
```json
{
  "tokenId": 1,
  "value": 700.0,
  "customerId": 1
}
```

> **Explication** : 300 tokens déduits du token 1. Un nouveau token est créé pour le client 2 dans son wallet.

Vérifier : **`GET /api/tokens/customer/2`** → un nouveau token de valeur 300.

#### 5.7 — Vérifier la relation dans le Wallet
**`GET /api/wallets/1`**

Dans la réponse, le champ `tokens` contient la liste des tokens :
```json
{
  "tokens": [
    { "tokenId": 1, "value": 700.0, "conversionRate": 1.0 },
    { "tokenId": 2, "value": 2000.0, "conversionRate": 1.5 }
  ]
}
```

> **Relation JPA** : `Wallet @OneToMany → Token @ManyToOne`

---

### 🏠 ÉTAPE 6 : CRUD Asset + Collatéral

#### 6.1 — Enregistrer un Asset REAL_ESTATE (100 000)
**`POST /api/assets`**

Body :
```json
{
  "ownerId": "1",
  "assetType": "REAL_ESTATE",
  "value": 100000,
  "walletId": 1
}
```

Réponse attendue :
```json
{
  "assetId": 1,
  "ownerId": "1",
  "assetType": "REAL_ESTATE",
  "value": 100000.0,
  "status": "REGISTERED",
  "walletId": 1
}
```

> **Règle métier collatéral** : Le wallet 1 reçoit automatiquement **10 000** (10% de 100 000) en collatéral.

Vérifier : **`GET /api/wallets/1`**
```json
{
  "balance": 13500.0,
  "transactionHistory": [
    "... | DEPOSIT | +5000.0",
    "... | WITHDRAW | -500.0",
    "... | SEND | -1000.0",
    "... | RECEIVE | +10000.0",
    "... | ASSET_REGISTERED | collateral: +10000.0 | asset: REAL_ESTATE valued at 100000.0"
  ]
}
```

#### 6.2 — Enregistrer un Asset VEHICLE (25 000)
**`POST /api/assets`**

Body :
```json
{
  "ownerId": "1",
  "assetType": "VEHICLE",
  "value": 25000,
  "walletId": 1
}
```

> Le wallet reçoit +2 500 en collatéral (10% de 25 000). Balance → 16 000.

#### 6.3 — Voir les Assets du Wallet 1
**`GET /api/assets/wallet/1`**

Réponse : 2 assets (REAL_ESTATE + VEHICLE).

#### 6.4 — Voir les Assets par type
**`GET /api/assets/type/REAL_ESTATE`**

#### 6.5 — Revaloriser un Asset (100K → 150K)
**`PUT /api/assets/1/value?newValue=150000`**

Réponse :
```json
{
  "assetId": 1,
  "value": 150000.0
}
```

> **Règle** : Le collatéral est ajusté. Différence = 150K - 100K = 50K → +5 000 crédités (10%).
> Vérifier : wallet balance augmente de +5 000.

#### 6.6 — Transférer un Asset au Client 2
**`PUT /api/assets/2/transfer?newOwnerId=2`**

Réponse :
```json
{
  "assetId": 2,
  "ownerId": "2",
  "status": "TRANSFERRED",
  "walletId": 2
}
```

> **Règle** : Le collatéral (2 500) est débité du wallet 1 et crédité au wallet 2.

Vérifier : **`GET /api/wallets/1`** → transactionHistory contient :
```
... | WITHDRAW | -2500.0
... | ASSET_TRANSFERRED_OUT | asset: 2 | to: 2
```

#### 6.7 — Valeur totale des Assets
**`GET /api/assets/wallet/1/total-value`**

Réponse : Somme des valeurs des assets du wallet 1.

#### 6.8 — Vérifier la relation dans le Wallet
**`GET /api/wallets/1`**

```json
{
  "assets": [
    { "assetId": 1, "assetType": "REAL_ESTATE", "value": 150000.0, "status": "REGISTERED" }
  ]
}
```

> **Relation JPA** : `Wallet @OneToMany → Asset @ManyToOne`

---

### 🤖 ÉTAPE 7 : Modèle IA — Scoring & Prédiction de Risque

#### 7.1 — Évaluation de Risque du Client 1
**`GET /api/wallets/risk/1`**

Réponse attendue :
```json
{
  "customerId": 1,
  "creditScore": 556,
  "riskLevel": "HIGH",
  "maxAllowedTransaction": 16750.0,
  "totalAssetValue": 150000.0,
  "walletBalance": 18500.0,
  "totalTransactions": 12,
  "latePayments": 0,
  "repaymentRate": 0.33,
  "predictedDefaultProbability": 0.2443,
  "recommendation": "DENY - High risk profile. Score: 556. Default probability: 24.4%",
  "financialIndicators": {
    "walletBalance": 18500.0,
    "totalAssetValue": 150000.0,
    "totalTokenValue": 4030.0,
    "assetCoverageRatio": 8.1,
    "repaymentRate": 0.33,
    "latePayments": 0.0,
    "totalTransactions": 12.0
  },
  "assessmentDate": "2026-02-26T..."
}
```

#### Explication du Modèle IA

| Champ | Description |
|-------|-------------|
| **creditScore** | Score 300–850 (type FICO) calculé avec des facteurs pondérés |
| **riskLevel** | Classification : LOW (≥750) / MEDIUM (≥650) / HIGH (≥550) / CRITICAL (<550) |
| **predictedDefaultProbability** | Probabilité de défaut via **régression logistique** (fonction sigmoïde) |
| **recommendation** | APPROVE / REVIEW / DENY avec justification |
| **assetCoverageRatio** | Ratio total assets / balance (couverture) |
| **repaymentRate** | Taux de remboursement (RECEIVE / total transactions) |

**Facteurs du Credit Score :**

| Facteur | Poids | Description |
|---------|-------|-------------|
| Stabilité du solde | 25% | Score basé sur le montant du solde |
| Couverture d'actifs | 25% | Ratio valeur totale des assets / balance |
| Historique transactions | 20% | Nombre de transactions effectuées |
| Retards de paiement | 20% | Pénalité pour chaque retard |
| Taux de remboursement | 10% | % de transactions entrantes vs totales |

**Prédiction de défaut (régression logistique) :**
```
z = β₀ + β₁×balance + β₂×assets + β₃×latePayments + β₄×repaymentRate + β₅×transactions
probabilité = 1 / (1 + e^(-z))     ← fonction sigmoïde
```

#### 7.2 — Évaluation de Risque du Client 2
**`GET /api/wallets/risk/2`**

Comparer les deux clients :

| Indicateur | Client 1 | Client 2 |
|-----------|----------|----------|
| Credit Score | 556 | 551 |
| Default Probability | 24.4% | 43.1% |
| Wallet Balance | 18 500 | 6 800 |
| Total Assets | 150 000 | 25 000 |
| Transactions | 12 | 5 |
| Recommendation | DENY | DENY |

> **Interprétation** : Le client 2 a un risque de défaut plus élevé (43% vs 24%) car moins de balance, moins d'assets, et moins de transactions.

---

### 🗑️ ÉTAPE 8 : Suppression (DELETE)

#### 8.1 — Supprimer un Token
**`DELETE /api/tokens/2`**

Réponse : HTTP 200 (token supprimé).

#### 8.2 — Supprimer un Asset
**`DELETE /api/assets/1`**

Réponse : HTTP 200 (asset supprimé).

#### 8.3 — Supprimer un Wallet
**`DELETE /api/wallets/1`**

> **Règle** : Si le wallet a encore un solde > 0, des tokens ou des assets, la suppression sera refusée.

---

## 🐳 Dockerisation

### Architecture Docker
```
docker-compose.yml
├── postgres (PostgreSQL 16)
│   ├── Port: 5432
│   ├── Database: equadb
│   ├── User: equauser
│   └── Healthcheck: pg_isready
└── app (Spring Boot)
    ├── Port: 8080
    ├── Depends on: postgres (healthy)
    └── Build: Dockerfile (multi-stage)
```

### Dockerfile (Build Multi-Stage)
```
Stage 1: maven:3.8.8-eclipse-temurin-21  → Compile avec Maven
Stage 2: eclipse-temurin:21-jre-alpine   → Image légère pour exécution
```

### Commandes Docker
```bash
# Lancer toute la stack (PostgreSQL + App)
docker-compose up --build

# Lancer PostgreSQL seul (dev local)
docker-compose up -d postgres

# Arrêter tout
docker-compose down

# Voir les logs
docker-compose logs -f app
docker-compose logs -f postgres

# Vérifier les conteneurs
docker ps
```

### Variables d'environnement Docker
| Variable | Valeur |
|----------|--------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/equadb` |
| `SPRING_DATASOURCE_USERNAME` | `equauser` |
| `SPRING_DATASOURCE_PASSWORD` | `equapass` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |

---

## 📊 Résumé des Relations entre Entités

```
┌──────────────────────────────────────────────────┐
│                    WALLET                         │
│  walletId | balance | customerId | publicKey      │
│  status   | transactionHistory | createdAt        │
├──────────────────────────────────────────────────┤
│  @OneToMany → Token (cascade = ALL)              │
│  @OneToMany → Asset (cascade = ALL)              │
└───────────────┬──────────────────┬───────────────┘
                │                  │
       ┌────────▼──────┐  ┌───────▼───────┐
       │    TOKEN       │  │    ASSET       │
       │ tokenId        │  │ assetId        │
       │ value           │  │ ownerId        │
       │ customerId     │  │ assetType      │
       │ conversionRate │  │ value          │
       │ totalSupply    │  │ status         │
       │ @ManyToOne     │  │ @ManyToOne     │
       │  → Wallet      │  │  → Wallet      │
       └────────────────┘  └────────────────┘
```

**Relations JPA :**
- `User (1) ↔ (1) Wallet` — via customerId (unique)
- `Wallet (1) ↔ (*) Token` — @OneToMany / @ManyToOne
- `Wallet (1) ↔ (*) Asset` — @OneToMany / @ManyToOne

---

## 📝 Résumé des Règles Métiers

| # | Règle | Implémentation |
|---|-------|---------------|
| 1 | Max transaction 50 000 | Rejet si montant > 50 000 |
| 2 | Solde minimum 10 | Alerte si balance < seuil |
| 3 | Wallet ACTIVE requis | Dépôt/retrait/transfert bloqués si SUSPENDED ou CLOSED |
| 4 | Collatéral 10% | Enregistrement d'asset → +10% crédité au wallet |
| 5 | Ajustement collatéral | Revalorisation → ajuste proportionnellement |
| 6 | Transfert collatéral | Transfert d'asset → collatéral transféré entre wallets |
| 7 | Anti-volatilité | Taux de conversion borné [0.5, 2.0] |
| 8 | Audit complet | Chaque opération tracée dans transactionHistory |
| 9 | Scoring IA | Credit score 300–850 avec régression logistique |
| 10 | Recommandation | APPROVE / REVIEW / DENY automatique |

---

## 🔗 URLs Importantes
- **Application** : http://localhost:8080
- **Swagger UI** : http://localhost:8080/swagger-ui/index.html
- **API Docs JSON** : http://localhost:8080/v3/api-docs


---

## 🆕 ÉTAPE 9 : Règle Métier Avancée — Multi-Devises (Currency Exchange)

#### 9.1 — Voir les taux de change supportés
**`GET /api/advanced/exchange/rates`**

Réponse :
```json
{
  "baseCurrency": "EUR",
  "rates": { "EUR": 1.0, "USD": 1.08, "GBP": 0.86, "BTC": 0.000016, "ETH": 0.00031, "TND": 3.35, "CHF": 0.94, "JPY": 162.5 },
  "feeRate": 0.005,
  "maxExchangeAmount": 100000.0,
  "supportedCurrencies": ["EUR","USD","GBP","BTC","ETH","TND","CHF","JPY"]
}
```

#### 9.2 — Prévisualiser une conversion (sans exécution)
**`GET /api/advanced/exchange/preview?from=EUR&to=USD&amount=1000`**

Réponse :
```json
{
  "from": "EUR", "to": "USD", "amount": 1000,
  "exchangeRate": 1.08, "convertedAmount": 1080.0,
  "fee": 5.4, "finalAmount": 1074.6
}
```

#### 9.3 — Convertir le wallet 1 en USD
**`POST /api/advanced/exchange/1?targetCurrency=USD&amount=1000`**

Réponse :
```json
{
  "walletId": 1, "sourceCurrency": "EUR", "targetCurrency": "USD",
  "sourceAmount": 1000, "exchangeRate": 1.08,
  "fee": 5.4, "convertedAmount": 1074.6, "newBalance": ...
}
```

> **Règle** : Frais de 0.5% appliqués. Historique tracé avec `CURRENCY_EXCHANGE`.

#### 9.4 — Transfert cross-devise entre wallets
**`POST /api/advanced/exchange/cross-transfer?senderWalletId=1&recipientWalletId=2&amount=500`**

> Convertit automatiquement de la devise de l'expéditeur vers celle du destinataire avec frais.

#### 9.5 — ❌ Devise non supportée
**`POST /api/advanced/exchange/1?targetCurrency=XYZ&amount=100`**

→ ERREUR 400 : `"Unsupported target currency: XYZ"`

---

## 🆕 ÉTAPE 10 : Règle Métier Avancée — Programme de Fidélité (Loyalty Rewards)

#### 10.1 — Voir le statut fidélité du wallet 1
**`GET /api/advanced/loyalty/1`**

Réponse :
```json
{
  "walletId": 1, "currentPoints": 0, "currentTier": "BRONZE",
  "tierMultiplier": 1.0, "pointValue": 0.1, "cashEquivalent": 0.0,
  "nextTier": "SILVER", "pointsToNextTier": 500,
  "tierBenefits": {
    "BRONZE": "Base points (x1.0)",
    "SILVER": "1.5x points + priority support",
    "GOLD": "2x points + reduced fees + priority support",
    "PLATINUM": "3x points + zero fees + VIP support + exclusive offers"
  }
}
```

#### 10.2 — Gagner des points (transaction de 5000)
**`POST /api/advanced/loyalty/1/earn?transactionAmount=5000`**

Réponse :
```json
{
  "walletId": 1, "transactionAmount": 5000,
  "basePoints": 500, "tierMultiplier": 1.0,
  "earnedPoints": 500, "totalPoints": 500,
  "currentTier": "SILVER", "tierUpgraded": true, "previousTier": "BRONZE"
}
```

> **Règle** : 10 points par 100 EUR. Multiplicateur selon tier. Upgrade automatique à 500 points → SILVER.

#### 10.3 — Gagner plus de points (tier SILVER = x1.5)
**`POST /api/advanced/loyalty/1/earn?transactionAmount=10000`**

→ Avec SILVER, le multiplicateur est x1.5. Base = 1000 pts → Earned = 1500 pts.

#### 10.4 — Échanger des points contre du cash
**`POST /api/advanced/loyalty/1/redeem?points=200`**

Réponse :
```json
{
  "walletId": 1, "pointsRedeemed": 200,
  "cashValue": 20.0, "remainingPoints": 1800,
  "newBalance": ..., "currentTier": "SILVER"
}
```

> **Règle** : 1 point = 0.10 EUR. Le cash est crédité directement au wallet.

#### 10.5 — ❌ Points insuffisants
**`POST /api/advanced/loyalty/1/redeem?points=999999`**

→ ERREUR 400 : `"Insufficient loyalty points. Available: 1800, Requested: 999999"`

---

## 🆕 ÉTAPE 11 : Règle Métier Avancée — Détection de Fraude

#### 11.1 — Analyse de fraude du wallet 1
**`GET /api/advanced/fraud/1`**

Réponse :
```json
{
  "walletId": 1, "customerId": 1,
  "fraudRiskScore": 15, "fraudRiskLevel": "LOW",
  "recommendedAction": "CLEAR - No suspicious activity detected.",
  "alerts": [],
  "totalAlertsCount": 0,
  "totalTransactionsAnalyzed": 12,
  "transactionStats": {
    "totalDeposits": 3, "totalWithdrawals": 2,
    "totalSends": 2, "totalReceives": 3,
    "totalExchanges": 1
  }
}
```

#### 11.2 — Simuler des transactions rapides puis re-analyser
Faire plusieurs dépôts rapides :
```
POST /api/wallets/1/deposit?amount=100
POST /api/wallets/1/deposit?amount=200
POST /api/wallets/1/deposit?amount=300
```

Puis : **`GET /api/advanced/fraud/1`**

→ Si les transactions sont trop rapides (< 30s), alerte `VELOCITY_ANOMALY` apparaît.

#### Algorithmes de détection :

| Algorithme | Description | Seuil |
|-----------|-------------|-------|
| **Velocity Check** | Fréquence des transactions | > 10/heure ou < 30s entre 2 |
| **Amount Anomaly** | Montants inhabituels | > 5x moyenne ou > 25 000 |
| **Round-Trip** | Pattern aller-retour (blanchiment) | SEND puis RECEIVE même montant |
| **Dormant Account** | Activité soudaine après dormance | Grosses transactions après inactivité |
| **Currency Abuse** | Trop d'échanges de devises | > 3 échanges récents |

| Score | Niveau | Action |
|-------|--------|--------|
| 0–19 | LOW | CLEAR |
| 20–49 | MEDIUM | MONITOR |
| 50–79 | HIGH | FLAG_FOR_REVIEW |
| 80+ | CRITICAL | BLOCK_ACCOUNT |

---

## 🆕 ÉTAPE 12 : Modèle IA Python Avancé (Microservice Flask)

### Architecture du modèle

```
ai-service/ (Python Flask - Port 5000)
├── app.py          → API REST Flask
├── model.py        → Modèle ML Ensemble
├── requirements.txt
└── Dockerfile
```

**Algorithme** : Ensemble Voting (soft) de 3 modèles :
- **Random Forest** (poids 3) — 100 arbres, profondeur max 10
- **Gradient Boosting** (poids 2) — 100 estimateurs, learning rate 0.1
- **Logistic Regression** (poids 1) — régularisation C=1.0

**12 features d'entrée** :
`wallet_balance`, `total_asset_value`, `total_token_value`, `total_transactions`,
`late_payments`, `repayment_rate`, `asset_coverage_ratio`, `avg_transaction_amount`,
`transaction_frequency`, `balance_volatility`, `loyalty_points`, `account_age_days`

#### 12.1 — Vérifier que le service IA est UP
**`GET http://localhost:5000/health`**

```json
{
  "status": "UP", "service": "equa-ai-service",
  "model_trained": true, "model_version": "2.0.0"
}
```

#### 12.2 — Infos du modèle
**`GET http://localhost:5000/api/ai/model-info`**

→ Retourne les feature importances, l'algorithme, la version.

#### 12.3 — Prédiction de risque
**`POST http://localhost:5000/api/ai/predict`**

Body :
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

Réponse :
```json
{
  "credit_score": 587,
  "default_probability": 0.1823,
  "risk_level": "MEDIUM",
  "recommendation": "REVIEW - Moderate risk. Score: 587. Additional verification recommended.",
  "max_allowed_transaction": 31500.0,
  "model_predictions": {
    "ensemble": 0.1823,
    "random_forest": 0.15,
    "gradient_boosting": 0.21,
    "logistic_regression": 0.19
  },
  "model_version": "2.0.0"
}
```

#### 12.4 — Explication détaillée de la prédiction
**`POST http://localhost:5000/api/ai/explain`** (même body)

→ Retourne `feature_contributions`, `top_risk_factors`, et une explication textuelle.

#### 12.5 — Prédiction par lot (batch)
**`POST http://localhost:5000/api/ai/predict/batch`**

Body : tableau JSON de plusieurs clients.

#### 12.6 — Re-entraîner le modèle
**`POST http://localhost:5000/api/ai/train`**

→ Re-entraîne sur 5000 données synthétiques. Retourne accuracy, AUC-ROC, feature importance.

---

## 🆕 ÉTAPE 13 : Analytics & Export CSV (Valeurs Ajoutées)

#### 13.1 — Rapport analytique complet
**`GET /api/analytics/report`**

```json
{
  "reportDate": "...",
  "totalWallets": 2,
  "balanceStatistics": { "totalBalance": 25000, "averageBalance": 12500, ... },
  "statusDistribution": { "ACTIVE": 2 },
  "currencyDistribution": { "EUR": 1, "USD": 1 },
  "loyaltyTierDistribution": { "SILVER": 1, "BRONZE": 1 },
  "totalTransactions": 25,
  "topWalletsByBalance": [ ... ]
}
```

#### 13.2 — Analytics d'un wallet spécifique
**`GET /api/analytics/wallet/1`**

→ Détail : balance, tokens, assets, net worth, breakdown des transactions.

#### 13.3 — Export CSV de tous les wallets
**`GET /api/analytics/export/wallets`**

→ Télécharge un fichier `wallets_export.csv` avec toutes les données.

#### 13.4 — Export CSV des transactions d'un wallet
**`GET /api/analytics/export/transactions/1`**

→ Télécharge `transactions_wallet_1.csv`.

---

## 🆕 ÉTAPE 14 : WebSocket — Notifications Temps Réel

**Connexion** : `ws://localhost:8080/ws` (SockJS + STOMP)

**Channels disponibles** :
| Topic | Description |
|-------|-------------|
| `/topic/wallet/{id}` | Notifications pour un wallet spécifique |
| `/topic/transactions` | Toutes les transactions en temps réel |
| `/topic/fraud-alerts` | Alertes de fraude |

Exemple avec un client JavaScript :
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, () => {
    stompClient.subscribe('/topic/transactions', (msg) => {
        console.log('Transaction:', JSON.parse(msg.body));
    });
    stompClient.subscribe('/topic/fraud-alerts', (msg) => {
        console.log('FRAUD ALERT:', JSON.parse(msg.body));
    });
});
```

---

## 🆕 Architecture Microservices Complète

```
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY (:8088)                        │
│                  Spring Cloud Gateway                           │
│         Route: /api/wallets/** → wallet-service                 │
│         Route: /api/tokens/**  → wallet-service                 │
│         Route: /api/assets/**  → wallet-service                 │
│         Route: /api/advanced/**→ wallet-service                 │
│         Route: /api/analytics/**→ wallet-service                │
│         Route: /api/ai/**      → ai-service                    │
└────────────────────────┬────────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
┌────────▼──────┐ ┌──────▼──────┐ ┌──────▼──────┐
│ EUREKA SERVER │ │   WALLET    │ │ AI SERVICE  │
│   (:8761)     │ │  SERVICE    │ │  (Python)   │
│  Discovery    │ │  (:8080)    │ │  (:5000)    │
│  Dashboard    │ │ Spring Boot │ │ Flask + ML  │
│               │ │ + JPA + WS  │ │ Ensemble    │
└───────────────┘ └──────┬──────┘ └─────────────┘
                         │
                  ┌──────▼──────┐
                  │ PostgreSQL  │
                  │  (:5432)    │
                  │  equadb     │
                  └─────────────┘
```

### URLs des services :

| Service | URL | Description |
|---------|-----|-------------|
| **Eureka Dashboard** | http://localhost:8761 | Service discovery UI |
| **API Gateway** | http://localhost:8088 | Point d'entrée unique |
| **Wallet Service** | http://localhost:8080 | API REST + Swagger |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | Documentation interactive |
| **AI Service** | http://localhost:5000 | Modèle ML Python |
| **AI Health** | http://localhost:5000/health | Health check IA |
| **WebSocket** | ws://localhost:8080/ws | Notifications temps réel |

---

## 🐳 Lancer TOUT en Docker

### Commande unique pour démarrer la stack complète :
```bash
docker-compose up --build
```

### Services démarrés :
```
equa-postgres   → PostgreSQL 16      → :5432
equa-eureka     → Eureka Server      → :8761
equa-wallet     → Wallet Service     → :8080
equa-ai         → AI Service Python  → :5000
equa-gateway    → API Gateway        → :8088
```

### Ordre de démarrage (automatique via depends_on) :
1. **PostgreSQL** (healthcheck: pg_isready)
2. **Eureka Server** (healthcheck: /actuator/health)
3. **AI Service** (healthcheck: /health)
4. **Wallet Service** (depends on: postgres + eureka)
5. **Gateway** (depends on: eureka + wallet + ai)

### Commandes utiles :
```bash
# Démarrer tout
docker-compose up --build

# Démarrer en arrière-plan
docker-compose up --build -d

# Voir les logs
docker-compose logs -f wallet-service
docker-compose logs -f ai-service
docker-compose logs -f eureka-server
docker-compose logs -f gateway

# Arrêter tout
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v

# Reconstruire un seul service
docker-compose up --build wallet-service
```

---

## 📂 Structure Complète du Projet

```
equa/
├── src/main/java/com/rayen/
│   ├── Application.java                          → Main + @EnableScheduling + @EnableDiscoveryClient
│   ├── config/
│   │   ├── OpenApiConfig.java                    → Swagger documentation
│   │   └── WebSocketConfig.java                  → WebSocket STOMP configuration
│   └── walletManagement/
│       ├── entity/
│       │   ├── Wallet.java                       → Entité JPA (balance, currency, loyalty, relations)
│       │   ├── Token.java                        → Entité JPA (ManyToOne → Wallet)
│       │   └── Asset.java                        → Entité JPA (ManyToOne → Wallet)
│       ├── model/
│       │   ├── WalletDTO.java                    → DTO avec currency, loyaltyPoints, loyaltyTier
│       │   ├── TokenDTO.java
│       │   ├── AssetDTO.java
│       │   ├── TransferRequest.java
│       │   └── RiskAssessmentDTO.java
│       ├── repository/
│       │   ├── WalletRepository.java             → Custom queries JPA
│       │   ├── TokenRepository.java
│       │   └── AssetRepository.java
│       ├── service/
│       │   ├── WalletService.java                → CRUD + dépôt/retrait/transfert
│       │   ├── TokenService.java                 → CRUD + anti-volatilité + transfert
│       │   ├── AssetService.java                 → CRUD + collatéral 10%
│       │   ├── RiskAssessmentService.java        → Scoring IA Java (régression logistique)
│       │   ├── CurrencyExchangeService.java      → ★ Multi-devises + conversion
│       │   ├── LoyaltyRewardsService.java        → ★ Programme fidélité + tiers
│       │   ├── FraudDetectionService.java        → ★ Détection de fraude
│       │   ├── AnalyticsService.java             → ★ Rapports + @Scheduled
│       │   └── NotificationService.java          → ★ WebSocket notifications
│       └── controller/
│           ├── WalletController.java             → REST CRUD + métier + risk
│           ├── TokenController.java              → REST CRUD + transfert
│           ├── AssetController.java              → REST CRUD + collatéral
│           ├── AdvancedBusinessController.java   → ★ Exchange + Loyalty + Fraud
│           ├── AnalyticsExportController.java    → ★ Analytics + CSV export
│           └── GlobalExceptionHandler.java
│
├── ai-service/                                   → ★ Microservice Python IA
│   ├── app.py                                    → API Flask (predict, explain, train, score)
│   ├── model.py                                  → Ensemble ML (RF + GB + LR)
│   ├── requirements.txt
│   └── Dockerfile
│
├── eureka-server/                                → ★ Service Discovery
│   ├── src/.../EurekaServerApplication.java
│   ├── src/.../application.yml
│   ├── pom.xml
│   └── Dockerfile
│
├── gateway/                                      → ★ API Gateway
│   ├── src/.../GatewayApplication.java
│   ├── src/.../application.yml
│   ├── pom.xml
│   └── Dockerfile
│
├── docker-compose.yml                            → 5 services orchestrés
├── Dockerfile                                    → Build wallet-service
├── pom.xml                                       → Maven + Spring Cloud
├── .gitignore
├── README.md
└── README-WALLET-TEST.md                         → Ce fichier
```

---

## 📖 Compréhension du Code — Guide Développeur

### Couche Entity (JPA)
Les entités utilisent **Lombok** (`@Data`, `@Builder`) et **JPA** (`@Entity`, `@Table`).
- `Wallet` : Entité principale avec `@OneToMany` vers Token et Asset. Contient `balance`, `currency`, `loyaltyPoints`, `loyaltyTier`, et `transactionHistory` (`@ElementCollection`).
- `Token` : `@ManyToOne` vers Wallet. Représente un jeton avec `conversionRate` et `totalSupply`.
- `Asset` : `@ManyToOne` vers Wallet. Représente un actif avec `assetType` et gestion du collatéral.

### Couche Repository
Interfaces JPA `extends JpaRepository<Entity, Long>` avec des méthodes custom :
- `findByCustomerId`, `findByPublicKey`, `findLowBalanceWallets` (Wallet)
- `findByWalletWalletId`, `getTotalConvertedValueByWalletId` (Token)
- `findByOwnerId`, `findByAssetType`, `getTotalAssetValueByWalletId` (Asset)

### Couche Service — Logique Métier
| Service | Responsabilité |
|---------|---------------|
| `WalletService` | CRUD + deposit/withdraw/transfer + validation (max 50K, solde min, wallet actif) |
| `TokenService` | CRUD + anti-volatilité (taux 0.5–2.0) + transfert avec conversion |
| `AssetService` | CRUD + collatéral 10% auto + revalorisation + transfert collatéral |
| `RiskAssessmentService` | Credit score (300–850) + sigmoïde + classification |
| `CurrencyExchangeService` | 8 devises + taux EUR base + frais 0.5% + cross-transfer |
| `LoyaltyRewardsService` | Points (10/100) + tiers (BRONZE→PLATINUM) + échange cash |
| `FraudDetectionService` | 5 algorithmes (velocity, amount, round-trip, dormant, currency) |
| `AnalyticsService` | Rapports @Scheduled (5min) + stats globales + wallet analytics |
| `NotificationService` | WebSocket STOMP → `/topic/wallet/{id}`, `/topic/fraud-alerts` |

### Couche Controller — REST API
Chaque controller utilise `@RestController` + `@Tag` (Swagger) + `@Operation` pour documenter chaque endpoint.

### Pattern de conversion DTO ↔ Entity
Tous les services utilisent des méthodes privées `convertToDTO()` et `convertToEntity()` pour isoler la couche de présentation de la couche de persistance.

### Gestion des erreurs
`GlobalExceptionHandler` capture les `RuntimeException` et retourne des réponses structurées `{ error, status }`.

---

## 🔀 Git Workflow

```bash
# Initialiser (déjà fait)
git init

# Voir le statut
git status

# Ajouter tous les fichiers
git add .

# Premier commit
git commit -m "feat: wallet management microservice with full CRUD, 3 advanced business rules, AI scoring, Eureka, Gateway, Docker"

# Créer une branche feature
git checkout -b feature/wallet-management

# Pousser vers le remote
git remote add origin <url>
git push -u origin feature/wallet-management

# Merge dans main
git checkout main
git merge feature/wallet-management
git push origin main
```

### Convention de commits :
- `feat:` — nouvelle fonctionnalité
- `fix:` — correction de bug
- `docs:` — documentation
- `refactor:` — refactoring sans changement fonctionnel
- `test:` — ajout de tests

---

## 📝 Résumé des 13+ Règles Métiers

| # | Règle | Service | Type |
|---|-------|---------|------|
| 1 | Max transaction 50 000 | WalletService | Sécurité |
| 2 | Solde minimum 10 | WalletService | Sécurité |
| 3 | Wallet ACTIVE requis | WalletService | Validation |
| 4 | Collatéral 10% auto | AssetService | Finance |
| 5 | Ajustement collatéral | AssetService | Finance |
| 6 | Transfert collatéral | AssetService | Finance |
| 7 | Anti-volatilité [0.5, 2.0] | TokenService | Régulation |
| 8 | Audit complet (transactionHistory) | Tous | Traçabilité |
| 9 | Scoring IA (300–850) | RiskAssessmentService | IA |
| 10 | Recommandation auto (APPROVE/REVIEW/DENY) | RiskAssessmentService | IA |
| 11 | **Multi-devises + frais 0.5%** | CurrencyExchangeService | ★ Avancé |
| 12 | **Fidélité (points + tiers BRONZE→PLATINUM)** | LoyaltyRewardsService | ★ Avancé |
| 13 | **Détection de fraude (5 algorithmes)** | FraudDetectionService | ★ Avancé |

## 3 Valeurs Ajoutées

| # | Valeur Ajoutée | Description |
|---|---------------|-------------|
| 1 | **WebSocket temps réel** | Notifications STOMP pour transactions et alertes fraude |
| 2 | **Analytics schedulé** | Rapports automatiques toutes les 5 min + dashboard API |
| 3 | **Export CSV** | Téléchargement des données wallets et transactions en CSV |
