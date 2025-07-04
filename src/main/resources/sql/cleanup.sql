-- Désactiver temporairement les contraintes de clé étrangère
SET session_replication_role = 'replica';

-- Supprimer les tables inutiles (anciennes versions)
DROP TABLE IF EXISTS paiement_monetique.roles CASCADE;
DROP TABLE IF EXISTS paiement_monetique.user_roles CASCADE;
DROP TABLE IF EXISTS paiement_monetique.utilisateurs CASCADE;
DROP TABLE IF EXISTS paiement_monetique.utilisateurs_roles CASCADE;

-- Supprimer les tables actuelles (elles seront recréées par schema.sql)
DROP TABLE IF EXISTS paiement_monetique.role CASCADE;
DROP TABLE IF EXISTS paiement_monetique.user_role CASCADE;
DROP TABLE IF EXISTS paiement_monetique.utilisateur CASCADE;
DROP TABLE IF EXISTS paiement_monetique.commercant CASCADE;
DROP TABLE IF EXISTS paiement_monetique.terminal CASCADE;
DROP TABLE IF EXISTS paiement_monetique.carte_bancaire CASCADE;
DROP TABLE IF EXISTS paiement_monetique.paiement CASCADE;

-- Réactiver les contraintes de clé étrangère
SET session_replication_role = 'origin';

-- Vérifier les tables restantes (devrait être vide)
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'paiement_monetique'
ORDER BY table_name;
