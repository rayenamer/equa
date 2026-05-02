with open('/home/rayounouna/equa/equaClient/src/app/Business/homepage/homepage.component.html', 'r') as f:
    content = f.read()

new_content = """<div class="business-layout">

  <div class="content-wrapper">
    <app-blockchain-sidebar [navItems]="navItems"></app-blockchain-sidebar>
    <main class="page-content">
      @if (isLoading) {
        <div class="loading-state">Chargement de votre espace...</div>
      } @else if (noBusiness) {
        <!-- Business Creation Form -->
        <div class="business-creation-overlay animate-in" style="display: flex; justify-content: center; align-items: center; min-height: 70vh;">
            <div class="form-card" style="max-width: 500px; width: 100%; border-radius: 12px; background: rgba(30, 41, 59, 0.5); backdrop-filter: blur(10px); padding: 2rem; border: 1px solid rgba(255, 255, 255, 0.1);">
                <div class="form-card-header" style="text-align: center; margin-bottom: 2rem;">
                    <h2>Créer votre EQUA Business</h2>
                    <p style="color: #94a3b8; font-size: 0.95rem; margin-top: 0.5rem;">Ouvrez votre wallet d'entreprise et commencez à gérer votre comptabilité analytique sur la blockchain.</p>
                </div>
                
                <form class="mouvement-form" (ngSubmit)="createFirstBusiness()">
                    <div class="form-grid" style="display: grid; gap: 1rem;">
                        <div class="form-group full">
                            <label style="display: block; margin-bottom: 0.5rem; font-size: 0.9rem; color: #cbd5e1;">Nom de l'entreprise *</label>
                            <input type="text" [(ngModel)]="newBusinessForm.name" name="bName" style="width: 100%; background: rgba(15, 23, 42, 0.6); border: 1px solid rgba(255,255,255,0.1); padding: 0.75rem 1rem; border-radius: 8px; color: white;" placeholder="Ex: Tech Solutions Inc." required />
                        </div>
                        <div class="form-group">
                            <label style="display: block; margin-bottom: 0.5rem; font-size: 0.9rem; color: #cbd5e1;">Secteur d'activité</label>
                            <input type="text" [(ngModel)]="newBusinessForm.industry" name="bIndustry" style="width: 100%; background: rgba(15, 23, 42, 0.6); border: 1px solid rgba(255,255,255,0.1); padding: 0.75rem 1rem; border-radius: 8px; color: white;" placeholder="Ex: IT, Consulting, Retail..." />
                        </div>
                        <div class="form-group">
                            <label style="display: block; margin-bottom: 0.5rem; font-size: 0.9rem; color: #cbd5e1;">Numéro d'immatriculation (Optionnel)</label>
                            <input type="text" [(ngModel)]="newBusinessForm.registrationNumber" name="bReg" style="width: 100%; background: rgba(15, 23, 42, 0.6); border: 1px solid rgba(255,255,255,0.1); padding: 0.75rem 1rem; border-radius: 8px; color: white;" placeholder="SIRET / RC..." />
                        </div>
                    </div>
                    
                    <div class="form-actions" style="margin-top: 2rem; display: flex; justify-content: stretch;">
                        <button type="submit" style="width: 100%; padding: 0.8rem; border-radius: 8px; border: none; background: #60a5fa; color: white; cursor: pointer; font-weight: bold;" [disabled]="!newBusinessForm.name">
                            Créer mon espace Business
                        </button>
                    </div>
                </form>
            </div>
        </div>
      } @else {
        <router-outlet></router-outlet>
      }
    </main>
  </div>
</div>
"""

with open('/home/rayounouna/equa/equaClient/src/app/Business/homepage/homepage.component.html', 'w') as f:
    f.write(new_content)
