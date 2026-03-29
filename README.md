# EQUA

## Esprit-PIDEV-4INFINI -2026-EquaToken

This project was developed as part of the PIDEV – 4th Year Engineering Program at Esprit School of Engineering (Academic Year 2025–2026).

---

## Overview

EQUA is a decentralized microfinance ecosystem designed to deliver accessible, secure, and low-cost financial services to underserved populations.

The platform leverages tokenization and blockchain technology to provide:

- Micro-loans
- Savings solutions
- Peer-to-peer payments
- Investment opportunities

Unlike traditional financial systems, EQUA focuses on financial inclusion, enabling users without access to banks to participate in a transparent and efficient digital economy.

A key innovation of the platform is the dual-wallet system, allowing users to interact with both:

- EQUA Token Wallet
- Dinar Wallet (Fiat Wallet)

---

### Features

#### User Authentication & Management
- Secure registration and login
- Role-based access control

#### Dual Wallet System
- EQUA Token Wallet for blockchain operations
- Dinar Wallet for fiat (TND) storage and conversion

#### Token Conversion
- Convert TND ↔ EQUA tokens seamlessly
- Stable entry point for non-crypto users

#### Micro-Loans
- Request and receive loans via smart contracts
- Automated repayment tracking

#### Savings Management
- Secure digital savings
- Protection against theft and loss

#### P2P Transactions
- Instant, low-cost transfers between users

#### Blockchain Transparency
- Immutable transaction history
- Public transaction observation

#### Investment Access
- Tokenized assets (real estate, equities, etc.)

#### Smart Contract Automation
- Loan issuance and repayment enforcement
- Transparent financial logic

---

## Tech Stack

- **Java**: 21.0.9
- **Spring Boot**: 3.2.1
- **Maven**: 3.8.8
- **Database**: PostgreSQL 16
- **Docker**: For containerized database
- **Swagger/OpenAPI**: API documentation

---

## Architecture

EQUA follows a modular microservices-inspired architecture within a single project, combining the simplicity of a monolith with the scalability of microservices.

**Shared Components**

- **Shared Database**  
  Single database instance

- **Shared Kernel**  
  Common utilities (DTOs, enums, helpers)  
  Cross-cutting concerns (logging, validation, exceptions)

**Layered Design (Inside Each Service)**

Each module follows clean architecture principles:

- **Presentation Layer**: REST API controllers (per service)
- **Application Layer**: Use cases, services
- **Domain Layer**: Core business models and rules
- **Infrastructure Layer**: Data access, external APIs, repositories

**Communication**

- **Intra-service communication**  
  Direct method calls (since same project)

- **Inter-service communication**  
  Through interfaces / service contracts

**Benefits**

- Microservices mindset without deployment complexity
- Scalable: can be split into real microservices later
- Clear separation of concerns
- Optimized performance (no network overhead)

---

## Contributors

- RAYEN AMEUR
- NADHMI ROUISSI
- CHAHINE SAADELLAOUI
- SEMER REBHI
- SAFWEN HABOUBI

## 📚 Project Resources

- **Miro Board**: [https://miro.com/app/board/uXjVGGvURDc=/?share_link_id=969033901344](https://miro.com/app/board/uXjVGGvURDc=/?share_link_id=969033901344)
- **Canva Presentation**: [https://www.canva.com/design/DAG__gdQ8Zk/Pnvcg-baYSi5G3TQ7o7i-g/edit?utm_content=DAG__gdQ8Zk&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton](https://www.canva.com/design/DAG__gdQ8Zk/Pnvcg-baYSi5G3TQ7o7i-g/edit?utm_content=DAG__gdQ8Zk&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)
- **Report (Google Docs)**: [https://docs.google.com/document/d/1SJ-QzxcbxZDwQ7bfMLdtrK59frejipB1qcXVu3yWqw8/edit?usp=sharing](https://docs.google.com/document/d/1SJ-QzxcbxZDwQ7bfMLdtrK59frejipB1qcXVu3yWqw8/edit?usp=sharing)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/rayenamer/equa
cd equa
```

### 2. Start the Database

The project uses PostgreSQL running in Docker. Start it with:

```bash
docker-compose up -d
```

**What this does**:

- Downloads PostgreSQL 16 Alpine image (if not already downloaded)
- Creates a container named `equa-postgres`
- Starts PostgreSQL on port `5433`
- Runs in detached mode (background)

**Verify it's running**:

```bash
docker ps
```

You should see `equa-postgres` in the list.

### 3. Add this file (.env) to your project

```bash
CONTACT RAYEN FOR FILE
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

### 6. Verify the Application

Open your browser and visit:

```
http://localhost:8081/swagger-ui/index.html
```

---

### Essential Commands

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Start database
docker-compose up -d

# Stop database
docker-compose down

# View logs
docker-compose logs -f postgres

# Check running containers
docker ps
```

### Important URLs

- **Application**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html

---

### Code Reviews

Rayen will accept merge requests only if the build job is successful and the code does not negatively impact the project’s quality or performance.

---

## ✅ Checklist for First Time Dev

- [ ] Install Java 21
- [ ] Install Maven 3.8.8
- [ ] Install Docker and Docker Compose
- [ ] Clone the repository
- [ ] Create the .env file locally
- [ ] Start PostgreSQL: `docker-compose up -d`
- [ ] Build project: `mvn clean install`
- [ ] Run application: `mvn spring-boot:run`
- [ ] Access Swagger: http://localhost:8081/swagger-ui/index.html
- [ ] Create your first feature branch
- [ ] Read the architecture section
- [ ] Understand the layer responsibilities

**Happy Coding! 🚀**

---

## Acknowledgment

We would like to thank:

- **Esprit School of Engineering** for providing the academic framework.
- Our instructors and mentors, **Mr. Aymen Esselmi, Mr. Mohamed Rjab, and Mr. Saadaoui Skander**, for their guidance, support, and passion in coaching us throughout this project.

---

## Final Note

EQUA was more than an academic project; it is a proof of our relentless pursuit of growth, of showing up with discipline and clarity.  
We thank our future selves for the glimpses of the vision that keeps us going.
