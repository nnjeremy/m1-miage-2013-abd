/*AutoCreate DataBase*/

/*Script pour SQLPLUS uniquement, permettant de dropper, creer et peupler automatiquement la base de donnée en faisant appel aux differents fichiers ci-dessous*/

CLEAR SCREEN;

PROMPT ------------------------------------;
PROMPT SUPPRESSION DES TABLES;
PROMPT ------------------------------------;

START Drop.sql;

PROMPT ------------------------------------;
PROMPT FIN SUPPRESSION DES TABLES;
PROMPT ------------------------------------;

PROMPT ------------------------------------;
PROMPT CREATION DES TABLES;
PROMPT ------------------------------------;

START Create.sql;

PROMPT ------------------------------------;
PROMPT FIN CREATION DES TABLES;
PROMPT ------------------------------------;

PROMPT ------------------------------------;
PROMPT CERATION DES TRIGGERS;
PROMPT ------------------------------------;

START Trigger.sql;

PROMPT ------------------------------------;
PROMPT FIN CREATION DES TRIGGERS;
PROMPT ------------------------------------;

PROMPT ------------------------------------;
PROMPT SEQUENCE DES TABLES;
PROMPT ------------------------------------;

START Peuplement.sql;

PROMPT ------------------------------------;
PROMPT FIN SEQUENCE DES TABLES;
PROMPT ------------------------------------;

commit;