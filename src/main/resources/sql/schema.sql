-- Table des rôles
CREATE TABLE IF NOT EXISTS paiement_monetique.role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20)
);

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS paiement_monetique.utilisateur (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Table de jointure user_role
CREATE TABLE IF NOT EXISTS paiement_monetique.user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES paiement_monetique.utilisateur (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES paiement_monetique.role (id) ON DELETE CASCADE
);

-- Table des commerçants
CREATE TABLE IF NOT EXISTS paiement_monetique.commercant (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    adresse TEXT,
    telephone VARCHAR(20)
);

-- Table des terminaux
CREATE TABLE IF NOT EXISTS paiement_monetique.terminal (
    id BIGSERIAL PRIMARY KEY,
    numero_serie VARCHAR(50) NOT NULL UNIQUE,
    modele VARCHAR(100),
    date_installation DATE,
    statut VARCHAR(20) DEFAULT 'ACTIF'
);

-- Table des cartes bancaires
CREATE TABLE IF NOT EXISTS paiement_monetique.carte_bancaire (
    id BIGSERIAL PRIMARY KEY,
    numero_carte VARCHAR(16) NOT NULL,
    date_expiration DATE NOT NULL,
    code_securite VARCHAR(4) NOT NULL,
    titulaire VARCHAR(100) NOT NULL,
    type_carte VARCHAR(20) NOT NULL
);

-- Table des paiements
CREATE TABLE IF NOT EXISTS paiement_monetique.paiement (
    id BIGSERIAL PRIMARY KEY,
    montant DECIMAL(10, 2) NOT NULL,
    date TIMESTAMP NOT NULL,
    statut VARCHAR(20) NOT NULL,
    type_carte VARCHAR(20) NOT NULL,
    commentaire TEXT,
    commercant_id BIGINT NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    carte_id BIGINT NOT NULL,
    FOREIGN KEY (commercant_id) REFERENCES paiement_monetique.commercant (id),
    FOREIGN KEY (utilisateur_id) REFERENCES paiement_monetique.utilisateur (id),
    FOREIGN KEY (carte_id) REFERENCES paiement_monetique.carte_bancaire (id)
);
