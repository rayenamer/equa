with open('/home/rayounouna/equa/equaClient/src/app/Business/mouvements/mouvements.component.html', 'r') as f:
    content = f.read()

prefix = """<div class="mouvements-page">

    @if (noBusiness) {
    <!-- Business Creation Form -->
    <div class="business-creation-overlay animate-in" style="display: flex; justify-content: center; align-items: center; min-height: 70vh;">
        <div class="form-card" style="max-width: 500px; width: 100%;">
            <div class="form-card-header">
                <h2>Créer votre EQUA Business</h2>
                <p>Ouvrez votre wallet d'entreprise et commencez à gérer votre comptabilité analytique sur la blockchain.</p>
            </div>
            
            <form class="mouvement-form" (ngSubmit)="createFirstBusiness()">
                <div class="form-grid">
                    <div class="form-group full">
                        <label>Nom de l'entreprise *</label>
                        <input type="text" [(ngModel)]="newBusinessForm.name" name="bName" class="form-control" placeholder="Ex: Tech Solutions Inc." required />
                    </div>
                    <div class="form-group">
                        <label>Secteur d'activité</label>
                        <input type="text" [(ngModel)]="newBusinessForm.industry" name="bIndustry" class="form-control" placeholder="Ex: IT, Consulting, Retail..." />
                    </div>
                    <div class="form-group">
                        <label>Numéro d'immatriculation (Optionnel)</label>
                        <input type="text" [(ngModel)]="newBusinessForm.registrationNumber" name="bReg" class="form-control" placeholder="SIRET / RC..." />
                    </div>
                </div>
                
                <div class="form-actions" style="margin-top: 1.5rem; justify-content: flex-end;">
                    <button type="submit" class="btn-primary" [disabled]="!newBusinessForm.name">
                        Créer mon espace Business
                    </button>
                </div>
            </form>
        </div>
    </div>
    } @else {
"""

suffix = """
    }
</div>
"""

content = content.replace('<div class="mouvements-page">', prefix)

# find the last </div> and replace it with suffix
idx = content.rfind('</div>')
if idx != -1:
    content = content[:idx] + suffix

with open('/home/rayounouna/equa/equaClient/src/app/Business/mouvements/mouvements.component.html', 'w') as f:
    f.write(content)
