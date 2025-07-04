-- Insertion des rôles initiaux
INSERT INTO paiement_monetique.role (name) VALUES 
    ('ROLE_USER'),
    ('ROLE_MODERATOR'),
    ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Création d'un utilisateur admin par défaut (mot de passe: admin123)
INSERT INTO paiement_monetique.utilisateur (username, email, password, full_name)
SELECT 'admin', 'admin@example.com', '$2a$10$gSAhZrxMllrLwjCNZ7KtP.92J1mYVqX3pzJX4kQ5X5v5Y5V5v5X5u', 'Administrateur Système'
WHERE NOT EXISTS (SELECT 1 FROM paiement_monetique.utilisateur WHERE username = 'admin');

-- Attribution du rôle admin à l'utilisateur admin
INSERT INTO paiement_monetique.user_role (user_id, role_id)
SELECT u.id, r.id 
FROM paiement_monetique.utilisateur u, paiement_monetique.role r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Insertion d'un commerçant de test
INSERT INTO paiement_monetique.commercant (nom, ville, adresse, telephone)
SELECT 'Boutique Test', 'Casablanca', '123 Avenue des FAR', '+212600000000'
WHERE NOT EXISTS (SELECT 1 FROM paiement_monetique.commercant WHERE nom = 'Boutique Test');

-- Insertion d'un terminal de test
INSERT INTO paiement_monetique.terminal (numero_serie, modele, date_installation, statut)
SELECT 'TERM-001', 'Ingenico iCT250', CURRENT_DATE, 'ACTIF'
WHERE NOT EXISTS (SELECT 1 FROM paiement_monetique.terminal WHERE numero_serie = 'TERM-001');
