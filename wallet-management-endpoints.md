# WalletManagement API Specifications

## Gestion Wallet

Feature	Subfeature	Endpoint	Description
Gestion Wallet	Gestion Crud	"GET /api/v1/wallets"	Liste tous les wallets.
Gestion Wallet	Gestion Crud	"GET /api/v1/wallets/{walletId}"	Récupère les détails d'un wallet par `walletId`.
Gestion Wallet	Gestion Crud	"GET /api/v1/wallets/me"	Récupère le wallet de l'utilisateur authentifié.
Gestion Wallet	Gestion Crud	"POST /api/v1/wallets"	Crée un wallet pour l'utilisateur connecté avec un DeviseWallet initialisé.
Gestion Wallet	Conversion	"POST /api/v1/wallets/convert"	Convertit le solde en dinars du wallet de l'utilisateur connecté en tokens EQUA selon le taux de conversion défini.

## DeviseWallet Multi-Currency

Feature	Subfeature	Endpoint	Description
Gestion Wallet	DeviseWallet	"GET /api/v1/wallets/me/devise-wallet"	Récupère le DeviseWallet du wallet de l'utilisateur authentifié.
Gestion Wallet	DeviseWallet	"POST /api/v1/wallets/{walletId}/funds"	Ajoute des fonds à un wallet dans la devise spécifiée.
Gestion Wallet	DeviseWallet	"POST /api/v1/wallets/{walletId}/funds/remove"	Retire des fonds d'un wallet dans la devise spécifiée.
Gestion Wallet	DeviseWallet	"POST /api/v1/wallets/{walletId}/convert"	Convertit un montant d'une devise à une autre dans le DeviseWallet du wallet cible, en appliquant des frais de conversion.
Gestion Wallet	DeviseWallet	"POST /api/v1/wallets/transfer"	Déplace un montant d'une devise d'un wallet à une autre devise dans le DeviseWallet d'un wallet destinataire.

## Loyalty & Rewards

Feature	Subfeature	Endpoint	Description
Gestion Wallet	Loyalty	"POST /api/v1/wallets/{walletId}/loyalty/redeem"	Transforme des points de fidélité en solde du wallet.
Gestion Wallet	Rewards	"POST /api/v1/wallets/{walletId}/rewards"	Applique une récompense au wallet (points bonus, cashback, boost de tier).

## Analytics

Feature	Subfeature	Endpoint	Description
Gestion Wallet	Analytics	"GET /api/v1/wallets/analytics"	Calcule et retourne les KPI globaux pour tous les wallets.
Gestion Wallet	Analytics	"GET /api/v1/wallets/{walletId}/analytics"	Calcule et retourne les KPI spécifiques pour un wallet.

## Gamification

Feature	Subfeature	Endpoint	Description
Gestion Wallet	Gamification	"GET /api/v1/wallets/{walletId}/achievements"	Récupère les succès/défis débloqués pour un wallet.
Gestion Wallet	Gamification	"GET /api/v1/wallets/{walletId}/challenges"	Récupère les challenges complétés pour un wallet.

## Fraud & Risk

Feature	Subfeature	Endpoint	Description
Gestion Wallet	Fraud	"GET /api/v1/wallets/{walletId}/fraud"	Retourne le niveau de risque frauduleux actuel du wallet.
