-- Clear all existing data to avoid conflicts
TRUNCATE TABLE paiement, carte_bancaire, terminal, commercant, utilisateur CASCADE;

-- Insert test users
INSERT INTO utilisateur (id, nom, email, role, mot_de_passe, actif)
VALUES
(1, 'Admin User', 'admin@example.com', 'ADMIN', '{bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true),
(2, 'John Doe', 'john@example.com', 'USER', '{bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true),
(3, 'Jane Smith', 'jane@example.com', 'USER', '{bcrypt}$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true);

-- Insert merchants
INSERT INTO commercant (id, nom, ville, secteur)
VALUES
(1, 'Boutique Test', 'Paris', 'Alimentation'),
(2, 'Magasin Demo', 'Lyon', 'Electronique'),
(3, 'Restaurant Le Petit Bistrot', 'Marseille', 'Restauration'),
(4, 'Librairie des Arts', 'Toulouse', 'Culture');

-- Insert terminals
INSERT INTO terminal (id, modele, statut, commercant_id)
VALUES
(1, 'INGENICO', 'ACTIF', 1),
(2, 'VERIFONE', 'ACTIF', 2),
(3, 'INGENICO', 'EN_PANNE', 3),
(4, 'SUREPOS', 'ACTIF', 4);

-- Insert bank cards (using masked numbers only for security)
INSERT INTO carte_bancaire (id, numero_masque, type, date_expiration, utilisateur_id, actif)
VALUES
(1, '************1111', 'VISA', '2025-12-31', 2, true),
(2, '************4444', 'MASTERCARD', '2024-10-31', 3, true),
(3, '************5555', 'VISA_ELECTRON', '2023-12-31', 3, false),
(4, '************9999', 'MASTERCARD', '2026-05-31', 2, true);

-- Insert payments
INSERT INTO paiement (id, montant, date, statut, utilisateur_id, commercant_id, carte_id, type_carte, commentaire)
VALUES
(1, 150.50, '2023-10-15 14:30:00', 'REUSSI', 2, 1, 1, 'VISA', 'Paiement réussi'),
(2, 89.99, '2023-10-16 10:15:00', 'REUSSI', 3, 2, 2, 'MASTERCARD', 'Paiement réussi'),
(3, 42.75, '2023-10-17 19:45:00', 'ECHOUE', 2, 3, 1, 'VISA', 'Fonds insuffisants'),
(4, 125.00, '2023-10-18 12:20:00', 'REUSSI', 3, 4, 2, 'MASTERCARD', 'Paiement en ligne'),
(5, 65.30, '2023-10-19 16:10:00', 'EN_ATTENTE', 2, 1, 4, 'MASTERCARD', 'En attente de validation');
