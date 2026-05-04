import os

replacements_html = {
    "Analyse Financière": "Financial Analysis",
    "Ratios financiers & états financiers de l'entreprise": "Financial ratios & company financial statements",
    "Données : Exercice": "Data: Fiscal Year",
    "Ratios Financiers": "Financial Ratios",
    "États Financiers": "Financial Statements",
    "Ratios de Liquidité": "Liquidity Ratios",
    "Ratios de Solvabilité": "Solvency Ratios",
    "Ratios de Rentabilité": "Profitability Ratios",
    "Bon": "Good",
    "Moyen": "Average",
    "Risque": "At Risk",
    "bon' ? 'Bon'": "bon' ? 'Good'",
    "moyen' ? 'Moyen'": "moyen' ? 'Average'",
    "Risque'": "At Risk'",
    "Seuil :": "Threshold :",
    "Compte de Résultat": "Income Statement",
    "Produits · Charges · Résultat": "Revenues · Expenses · Net Income",
    "PRODUITS D'EXPLOITATION (Classe 7)": "OPERATING REVENUES",
    "Chiffre d'affaires": "Turnover",
    "Subventions d'exploitation": "Operating subsidies",
    "Sous-total produits": "Subtotal revenues",
    "CHARGES D'EXPLOITATION (Classe 6)": "OPERATING EXPENSES",
    "Achats & variations stocks": "Purchases & inventory changes",
    "Charges de personnel": "Staff costs",
    "Autres charges": "Other expenses",
    "Sous-total charges": "Subtotal expenses",
    "Résultat d'exploitation": "Operating income"
}

replacements_ts = {
    "Liquidité Générale": "General Liquidity",
    "Recettes Totales / Dépenses Totales": "Total Revenues / Total Expenses",
    "Capacité à couvrir les dépenses avec les entrées.": "Ability to cover expenses with inflows.",
    "Couverture des Charges": "Expense Coverage",
    "Trésorerie Nette / Charges Opérationnelles": "Net Cash / Operating Expenses",
    "Solde de trésorerie disponible pour couvrir les charges régulières.": "Available cash balance to cover regular expenses.",
    "Marge Nette": "Net Margin",
    "(Recettes - Dépenses) / Chiffre d\\'Affaires": "(Revenues - Expenses) / Turnover",
    "Pourcentage de bénéfice net par rapport au chiffre d\\'affaires.": "Percentage of net profit compared to turnover."
}

def process_file(filepath, replacements):
    with open(filepath, 'r') as f:
        content = f.read()
    for k, v in replacements.items():
        content = content.replace(k, v)
    with open(filepath, 'w') as f:
        f.write(content)

base_dir = '/home/rayounouna/equa/equaClient/src/app/Business/finance'
process_file(f'{base_dir}/finance.component.html', replacements_html)
process_file(f'{base_dir}/finance.component.ts', replacements_ts)

base_dir2 = '/home/rayounouna/equa/equaClient/src/app/Business/how-it-works'
repl_how_html = {
    "Comment fonctionne EQUA Business ?": "How does EQUA Business work?",
    "Un système de gestion comptable et financière intégré à votre activité sur EQUA": "An accounting and financial management system integrated into your activity on EQUA",
    "Prêt à commencer ?": "Ready to get started?",
    "Accédez à vos mouvements comptables ou consultez votre état financier directement depuis le menu.": "Access your accounting movements or view your financial statements directly from the menu.",
    "Voir les mouvements": "View movements",
    "Analyse financière": "Financial analysis"
}
repl_how_ts = {
    "Gérez votre entreprise": "Manage your business",
    "Accédez à une vue complète de votre activité. Vos transactions effectuées via la plateforme EQUA sont enregistrées automatiquement comme mouvements comptables.": "Access a complete view of your activity. Your transactions via the EQUA platform are automatically recorded as accounting movements.",
    "Classifiez vos mouvements": "Classify your movements",
    "Les mouvements entrants et sortants non classifiés apparaissent dans une section dédiée. Assignez-leur un compte comptable et une catégorie analytique en un clic.": "Unclassified inbound and outbound movements appear in a dedicated section. Assign them an accounting account and an analytical category in one click.",
    "Ajoutez manuellement": "Add manually",
    "Enregistrez tout mouvement comptable manuellement : libellé, type (entrant/sortant), compte, catégorie et montant. Idéal pour les opérations en dehors de la plateforme.": "Record any accounting movement manually: description, type (inbound/outbound), account, category, and amount. Ideal for operations outside the platform.",
    "Prenez des décisions éclairées": "Make informed decisions",
    "Consultez vos ratios financiers (liquidité, solvabilité, rentabilité) calculés automatiquement depuis vos mouvements. Votre compte de résultat est généré en temps réel.": "Consult your financial ratios (liquidity, solvency, profitability) automatically calculated from your movements. Your income statement is generated in real-time."
}

process_file(f'{base_dir2}/how-it-works.component.html', repl_how_html)
process_file(f'{base_dir2}/how-it-works.component.ts', repl_how_ts)

base_dir3 = '/home/rayounouna/equa/equaClient/src/app/Business/info'
repl_info = {
    "Informations de l'Entreprise": "Company Information",
    "Consultez les détails et l'enregistrement de votre business dans l'écosystème EQUA.": "Consult details and registration of your business in the EQUA ecosystem.",
    "Chargement des informations...": "Loading information...",
    "Nom de l'entreprise": "Company name",
    "Secteur d'activité": "Industry sector",
    "Non renseigné": "Not provided",
    "Numéro d'immatriculation": "Registration number",
    "Statut": "Status",
    "Actif": "Active",
    "ID de l'entreprise (Base de données)": "Company ID (Database)",
    "Aucune information d'entreprise disponible.": "No company information available."
}
process_file(f'{base_dir3}/info.component.html', repl_info)

base_dir4 = '/home/rayounouna/equa/equaClient/src/app/Business/homepage'
repl_home = {
    "Chargement de votre espace...": "Loading your space...",
    "Créer votre EQUA Business": "Create your EQUA Business",
    "Ouvrez votre wallet d'entreprise et commencez à gérer votre comptabilité analytique sur la blockchain.": "Open your business wallet and start managing your analytical accounting on the blockchain.",
    "Nom de l'entreprise *": "Company name *",
    "Ex: Tech Solutions Inc.": "Ex: Tech Solutions Inc.",
    "Secteur d'activité": "Industry sector",
    "Ex: IT, Consulting, Retail...": "Ex: IT, Consulting, Retail...",
    "Numéro d'immatriculation (Optionnel)": "Registration number (Optional)",
    "Créer mon espace Business": "Create my Business space"
}
process_file(f'{base_dir4}/homepage.component.html', repl_home)
