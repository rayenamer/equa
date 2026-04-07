Navigation Flow
/                        ← main landing (pages/home)
/blockchain              ← blockchain homepage
/blockchain/transactions ← blockchain sub-page
/blockchain/nodes        ← blockchain sub-page

/forum                   ← forum homepage  
/forum/posts             ← forum sub-page
/forum/posts/42          ← forum sub-page detail
To go from /blockchain/transactions to /forum/posts, the user must navigate to /forum first (or directly to /forum/posts via a link/button). There's no way to access Forum sub-components while staying inside the Blockchain module.

Practical Example — Navbar
In your main app.component.html, you have a global navbar that switches between modules:
html<!-- app.component.html -->
<nav>
  <a routerLink="/">Home</a>
  <a routerLink="/blockchain">Blockchain</a>
  <a routerLink="/forum">Forum</a>
  <a routerLink="/loan">Loan</a>
  <a routerLink="/wallet">Wallet</a>
  <a routerLink="/user">User</a>
  <a routerLink="/financial-market">Financial Market</a>
</nav>

<router-outlet></router-outlet>  <!-- modules load here -->

Inside Blockchain — local subnav
Then inside BlockChain/homepage/homepage.component.html, you have a local navbar for blockchain sub-pages only:
html<!-- blockchain/homepage/homepage.component.html -->
<nav>
  <a routerLink="/blockchain">Overview</a>
  <a routerLink="/blockchain/transactions">Transactions</a>
  <a routerLink="/blockchain/nodes">Nodes</a>
</nav>

<router-outlet></router-outlet>  <!-- blockchain sub-pages load here -->

Updated blockchain.routes.ts
typescriptexport const BLOCKCHAIN_ROUTES: Routes = [
  {
    path: '',
    component: HomepageComponent,   // acts as layout with <router-outlet>
    children: [
      { path: '', component: BlockchainOverviewComponent },
      { path: 'transactions', loadComponent: () => import('./transactions/transactions.component').then(m => m.TransactionsComponent) },
      { path: 'nodes', loadComponent: () => import('./nodes/nodes.component').then(m => m.NodesComponent) },
    ]
  }
];

Visual Summary
AppComponent  (global navbar + <router-outlet>)
│
├── /blockchain → HomepageComponent (local subnav + <router-outlet>)
│                  ├── /blockchain              → OverviewComponent
│                  ├── /blockchain/transactions → TransactionsComponent
│                  └── /blockchain/nodes        → NodesComponent
│
├── /forum      → HomepageComponent (local subnav + <router-outlet>)
│                  ├── /forum                  → ForumOverviewComponent
│                  ├── /forum/posts            → PostsComponent
│                  └── /forum/posts/42         → PostDetailComponent
│
└── /wallet     → HomepageComponent (local subnav + <router-outlet>)
                   ├── /wallet                 → WalletOverviewComponent
                   └── /wallet/balance         → BalanceComponent
So the global navbar handles module switching, and each module's homepage handles its own internal navigation via its local <router-outlet>.