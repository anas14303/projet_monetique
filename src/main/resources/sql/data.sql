-- Insertion des rôles initiaux
INSERT INTO paiement_monetique.role (name) VALUES 
    ('ROLE_USER'),
    ('ROLE_MODERATOR'),
    ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Création d'un utilisateur admin par défaut
INSERT INTO paiement_monetique.utilisateur (nom, email)
SELECT 'Administrateur', 'admin@example.com'
WHERE NOT EXISTS (SELECT 1 FROM paiement_monetique.utilisateur WHERE email = 'admin@example.com');

-- Attribution du rôle admin à l'utilisateur admin
INSERT INTO paiement_monetique.user_role (user_id, role_id)
SELECT u.id, r.id 
FROM paiement_monetique.utilisateur u, paiement_monetique.role r 
WHERE u.email = 'admin@example.com' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Insertion d'un commerçant de test
INSERT INTO paiement_monetique.commercant (nom, ville, secteur)
SELECT 'Boutique Test', 'Casablanca', 'Commerce Général'
WHERE NOT EXISTS (SELECT 1 FROM paiement_monetique.commercant WHERE nom = 'Boutique Test');

-- Insertion d'un terminal de test lié au commerçant de test
INSERT INTO paiement_monetique.terminal (commercant_id, modele, statut)
SELECT c.id, 'Ingenico iCT250', 'ACTIF'
FROM paiement_monetique.commercant c 
WHERE c.nom = 'Boutique Test' 
AND NOT EXISTS (
    SELECT 1 
    FROM paiement_monetique.terminal t 
    JOIN paiement_monetique.commercant c2 ON t.commercant_id = c2.id 
    WHERE c2.nom = 'Boutique Test'
);
