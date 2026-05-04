import os

replacements_html = {
    "Mouvements Comptables": "Accounting Movements",
    "Comptabilité analytique & gestion des opérations": "Analytical accounting & operations management",
    "Total Entrant": "Total Inbound",
    "Total Sortant": "Total Outbound",
    "Solde": "Balance",
    "Comptabilité Analytique": "Analytical Accounting",
    "Nouveau Mouvement": "New Movement",
    "Importer Transactions": "Import Transactions",
    "Rechercher un mouvement...": "Search for a movement...",
    "Tous": "All",
    "Entrant": "Inbound",
    "Sortant": "Outbound",
    "Mouvements en attente (À classifier)": "Pending movements (To be classified)",
    "mouvement(s) requiert votre attention": "movement(s) require your attention",
    "Classifier": "Classify",
    '<span class="col-date">Date</span>': '<span class="col-date">Date</span>',
    '<span class="col-libelle">Libellé</span>': '<span class="col-libelle">Description</span>',
    '<span class="col-compte">Compte</span>': '<span class="col-compte">Account</span>',
    '<span class="col-categorie">Catégorie</span>': '<span class="col-categorie">Category</span>',
    '<span class="col-type">Type</span>': '<span class="col-type">Type</span>',
    '<span class="col-montant">Montant</span>': '<span class="col-montant">Amount</span>',
    '<span class="col-statut">Statut</span>': '<span class="col-statut">Status</span>',
    "Validé": "Validated",
    "En attente": "Pending",
    "✕ Annulé": "✕ Cancelled",
    "Annulé": "Cancelled",
    "Aucun mouvement trouvé": "No movement found",
    "Répartition par catégorie": "Breakdown by category",
    "Entrant :": "Inbound :",
    "Sortant :": "Outbound :",
    "Mouvement enregistré avec succès !": "Movement recorded successfully!",
    "Ajouter un nouveau mouvement": "Add a new movement",
    "Renseignez les informations du mouvement comptable à enregistrer.": "Fill in the details of the accounting movement to record.",
    "<label>Date *</label>": "<label>Date *</label>",
    "Type de mouvement *": "Movement type *",
    "Libellé *": "Description *",
    "Ex: Vente produit, Achat fournitures...": "Ex: Product sale, Supply purchase...",
    "Le libellé est requis": "Description is required",
    "Compte comptable *": "Accounting account *",
    "Sélectionner un compte": "Select an account",
    "Le compte est requis": "Account is required",
    "Catégorie analytique *": "Analytical category *",
    "Sélectionner une catégorie": "Select a category",
    "La catégorie est requise": "Category is required",
    "Montant (EQUA) *": "Amount (EQUA) *",
    "Le montant est requis": "Amount is required",
    "<label>Statut</label>": "<label>Status</label>",
    "Description / Pièce justificative": "Description / Supporting document",
    "Notes, numéro de facture, références...": "Notes, invoice number, references...",
    "Réinitialiser": "Reset",
    "Enregistrer le mouvement": "Record movement",
    "Transaction importée en attente avec succès !": "Transaction imported as pending successfully!",
    "Transactions de votre Wallet": "Your Wallet Transactions",
    "Sélectionnez les transactions blockchain à importer en tant que mouvements comptables.": "Select blockchain transactions to import as accounting movements.",
    '<span class="col-libelle">Hash de Transaction</span>': '<span class="col-libelle">Transaction Hash</span>',
    '<span class="col-type">Sens</span>': '<span class="col-type">Direction</span>',
    '<span class="col-statut">Action</span>': '<span class="col-statut">Action</span>',
    "Entrante": "Inbound",
    "Sortante": "Outbound",
    "Ajouter": "Add",
    "Aucune transaction trouvée sur ce wallet": "No transaction found on this wallet",
    "Classifier le mouvement": "Classify movement",
    "Annuler": "Cancel",
    "Confirmer la classification": "Confirm classification"
}

replacements_ts = {
    "'601 – Achats matières'": "'601 - Materials purchases'",
    "'606 – Fournitures bureau'": "'606 - Office supplies'",
    "'613 – Locations'": "'613 - Rentals'",
    "'616 – Primes assurances'": "'616 - Insurance premiums'",
    "'641 – Charges de personnel'": "'641 - Staff costs'",
    "'695 – Impôts sur bénéfices'": "'695 - Income taxes'",
    "'701 – Ventes marchandises'": "'701 - Sales of goods'",
    "'706 – Prestations services'": "'706 - Provision of services'",
    "'707 – Ventes produits'": "'707 - Sales of products'",
    "'741 – Subventions exploitation'": "'741 - Operating subsidies'",
    "'Chiffre d\\'affaires'": "'Turnover'",
    "'Charges opérationnelles'": "'Operating expenses'",
    "'Charges générales'": "'General expenses'",
    "'Exceptionnel'": "'Exceptional'"
}

def process_file(filepath, replacements):
    with open(filepath, 'r') as f:
        content = f.read()
    
    for k, v in replacements.items():
        content = content.replace(k, v)
        
    with open(filepath, 'w') as f:
        f.write(content)

base_dir = '/home/rayounouna/equa/equaClient/src/app/Business/mouvements'
process_file(f'{base_dir}/mouvements.component.html', replacements_html)
process_file(f'{base_dir}/mouvements.component.ts', replacements_ts)
