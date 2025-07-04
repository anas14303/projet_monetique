-- Connect to the paiement_monetique database
\c paiement_monetique

-- List all tables in the current database
\dt

-- Check if the utilisateur table exists
SELECT EXISTS (
   SELECT FROM information_schema.tables 
   WHERE table_schema = 'paiement_monetique' 
   AND table_name = 'utilisateur'
) AS table_exists;
