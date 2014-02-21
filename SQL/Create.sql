/* Create Table */
/* Script sql permettant de créer les tables de la base de donées, A executer après avoir dropper les tables en faisant appel au fichier drop.sql si vous ne passez pas par le fichier AutoCreate */
/* ALARME */
CREATE TABLE ALARME(
	NUMALARME integer NOT NULL,
	ETAT_ALARME varchar(10) NOT NULL,
	TYPE_ALARM varchar(50) NOT NULL,
	REF_VIDEO char(10),
	CONSTRAINT pk_alarme PRIMARY KEY ( NUMALARME ),
	CONSTRAINT ch_etat_alarme CHECK (ETAT_ALARME IN ('ENABLE', 'DISABLE')) /* Contrainte verifiant si la colonne ETAT_ALARME de la table ALARME est egale à ENABLE ou DISABLE seulement*/
	--Edit: Cette contrainte est inutile étant donné que la liste est non exhaustive CONSTRAINT ch_type_alarme CHECK (TYPE_ALARM IN ('PASSAGE MULTIPLE', 'PAS_SORTI', 'PAS_ENTRE', 'OUVERTURE_PORTE_TROP_LONG'))  /* Contrainte verifiant si la colonne TYPE_ALARM de la table ALARME est egale à la liste enumérée seulement*/
);

/* BADGE */
CREATE TABLE BADGE(
	NUMBADGE integer NOT NULL,
	ETATBADGE varchar(10) NOT NULL,
	CONSTRAINT pk_badge PRIMARY KEY ( NUMBADGE ),
	CONSTRAINT ch_etat_badge CHECK (ETATBADGE IN ('ENABLE', 'DISABLE', 'PERDU', 'VOLE')) /* Contrainte verifiant si la colonne ETATBADGE de la table BADGE est egale à la liste enumérée seulement*/
);

/* GROUPE */
CREATE TABLE GROUPE(
	ID_GROUPEPERS integer NOT NULL,
	NOMGROUPEPERS varchar(50) NOT NULL,
	CONSTRAINT pk_groupe PRIMARY KEY ( ID_GROUPEPERS ) 
);

/* GROUPE_BATIMENTS */
CREATE TABLE GROUPE_BATIMENTS(
	ID_GROUPEBAT integer NOT NULL,
	NOMGROUPEBAT varchar(10) NOT NULL,
	CONSTRAINT pk_groupe_batiment PRIMARY KEY ( ID_GROUPEBAT )
);

/* JOUR_FERIE */
CREATE TABLE JOUR_FERIE(
	DATE_FERIE date NOT NULL,
	CONSTRAINT pk_jour_ferie PRIMARY KEY ( DATE_FERIE )
);

/* BATIMENT */
CREATE TABLE BATIMENT(
	CODE_BATIMENT varchar(10) NOT NULL, /*    /!\    de 3 a 10 lettres    /!\    */
	ID_GROUPEBAT integer,
	ADRESSE varchar(50) NOT NULL,
	CONSTRAINT pk_batiment PRIMARY KEY ( CODE_BATIMENT ),
	CONSTRAINT check_code_bat CHECK (REGEXP_LIKE(CODE_BATIMENT, '[a-zA-Z0-9]{3}[a-zA-Z0-9]{0,4}\-[a-zA-Z0-9]{1,2}$')), /* Contrainte verifiant si la colonne CODE_BATIMENT de la table BATIMENT est egale à l'expression réguliere ( numéro de tel de type XXXxxxx-Xx) */
	CONSTRAINT fk_batiment_id_groupebat FOREIGN KEY (ID_GROUPEBAT) REFERENCES GROUPE_BATIMENTS ( ID_GROUPEBAT )
);

/* POINT_ACCES */
CREATE TABLE POINT_ACCES(
	CODEPOINTACCES varchar(4) NOT NULL,
	CODE_BATIMENT  varchar(10) NOT NULL,
	TYPE_ACCES varchar(9) NOT NULL,
	ETAT_POINT_ACCES varchar(6) NOT NULL,
	STATUT_POINT_ACCES varchar(10) NOT NULL,
	ROLE char(1) NOT NULL,
	CONSTRAINT check_type_acces CHECK (TYPE_ACCES in ('ENTER', 'EXIT', 'ENTEREXIT')), /* Contrainte verifiant si la colonne TYPE_ACCES de la table POINT_ACCES est egale à la liste enumérée seulement*/
	CONSTRAINT check_role_acces CHECK (ROLE in ('E', 'I')), /* Contrainte verifiant si la colonne ROLE de la table POINT_ACCES est egale à la liste enumérée seulement*/
	CONSTRAINT check_etat_pt_acces CHECK (ETAT_POINT_ACCES in ('OUVERT', 'FERME')), /* Contrainte verifiant si la colonne ETAT_POINT_ACCES de la table POINT_ACCES est egale à la liste enumérée seulement*/
	CONSTRAINT check_statut_pt_acces CHECK (STATUT_POINT_ACCES in ('ACTIF', 'EN PANNE', 'EN ATTENTE', 'BLOQUE', 'DEBLOQUE')), /* Contrainte verifiant si la colonne STATUT_POINT_ACCES de la table POINT_ACCES est egale à la liste enumérée seulement*/
	CONSTRAINT pk_point_acces PRIMARY KEY (CODEPOINTACCES, CODE_BATIMENT),
	CONSTRAINT fk_point_acces_co_bt FOREIGN KEY (CODE_BATIMENT) REFERENCES BATIMENT( CODE_BATIMENT )
);

/* PLAGE_SEMAINE */
CREATE TABLE PLAGE_SEMAINE(
	LIBELLE_PLAGE_SEMAINE varchar(50) NOT NULL,
	SEMAINE_DEBUT  integer NOT NULL,
	SEMAINE_FIN integer NOT NULL,
	CONSTRAINT pk_plage_semaine PRIMARY KEY ( LIBELLE_PLAGE_SEMAINE ),
	CONSTRAINT check_plage_semaine_d CHECK (SEMAINE_DEBUT BETWEEN 1 and 52), /* Contrainte verifiant si la colonne SEMAINE_DEBUT de la table PLAGE_SEMAINE contient uniquement des entiers entre 1 et 52*/
	CONSTRAINT check_plage_semaine_f CHECK (SEMAINE_FIN BETWEEN 1 and 52) /* Contrainte verifiant si la colonne SEMAINE_FIN de la table PLAGE_SEMAINE contient uniquement des entiers entre 1 et 52*/
);

/* PLAGE_HORAIRE */
CREATE TABLE PLAGE_HORAIRE(
	LIBELLE_PLAGE_HORAIRE varchar(50) NOT NULL,
	HORAIRE_DEBUT date NOT NULL,
	HORAIRE_FIN date NOT NULL,
	CONSTRAINT pk_plage_horaire PRIMARY KEY ( LIBELLE_PLAGE_HORAIRE ),
	CONSTRAINT check_horaire_d CHECK ((HORAIRE_DEBUT BETWEEN  to_date('01/01/2000 00:00:00','DD/MM/YYYY HH24:MI:SS') and to_date('01/01/2000 23:59:59','DD/MM/YYYY HH24:MI:SS'))), /* Contrainte verifiant si la colonne HORAIRE_DEBUT de la table PLAGE_HORAIRE contient la valeur de date comprise entre '01/01/2000 00:00:00' et 01/01/2000 23:59:59' pour pouvoir gerer les horaires*/
	CONSTRAINT check_horaire_f CHECK ((HORAIRE_FIN BETWEEN  to_date('01/01/2000 00:00:00','DD/MM/YYYY HH24:MI:SS') and to_date('01/01/2000 23:59:59','DD/MM/YYYY HH24:MI:SS'))) /* Contrainte verifiant si la colonne HORAIRE_FIN de la table PLAGE_HORAIRE contient la valeur de date comprise entre '01/01/2000 00:00:00' et 01/01/2000 23:59:59' pour pouvoir gerer les horaires */
);

/* PAPIER_IDENTITE 
CREATE TABLE PAPIER_IDENTITE(
	NUM_CARTE_ID varchar(50) NOT NULL,
	DATE_DELIVRANCE date NOT NULL,
	LIEU_DELIVRANCE varchar(25) NOT NULL,
	CONSTRAINT pk_papier_identite PRIMARY KEY ( NUM_CARTE_ID )
); --> Ne sert a rien si ce n'est ˆ alourdir le code*/

/* PERSONNE */
CREATE TABLE PERSONNE(
	ID_PERSONNE integer NOT NULL,
	NOM varchar(10) NOT NULL,
	PRENOM varchar(10) NOT NULL,
	DATE_NAISSANCE date NOT NULL,
	LIEU_NAISSANCE varchar(50) NOT NULL,
	BUREAU varchar(10),
	TELEPHONE varchar(14),
	EMAIL varchar(50),
	NUM_CARTE_ETU integer,
	FILIERE varchar(25),
	ANNEE_PROMO integer,
	/*NUM_CARTE_ID varchar(50),*/
	CONSTRAINT pk_personne PRIMARY KEY (ID_PERSONNE),
	/*CONSTRAINT fk_perso_carteID FOREIGN KEY (NUM_CARTE_ID) REFERENCES PAPIER_IDENTITE ( NUM_CARTE_ID ),*/
	CONSTRAINT check_tel_type CHECK (REGEXP_LIKE(TELEPHONE, '([0-9][0-9][.]){4}[0-9][0-9]')), /* Contrainte verifiant si la colonne TELEPHONE de la table PERSONNE est egale à l'expression réguliere ( numéro de tel de type xx.xx.xx.xx.xx) */
	CONSTRAINT check_mail CHECK (REGEXP_LIKE(EMAIL, '^[a-zA-Z0-9._%-]+@[a-zA-Z0-9._%-]+\.[a-zA-Z]{2,4}$')) /* Contrainte verifiant si la colonne EMAIL de la table PERSONNE est egale à l'expression réguliere ( email de type x@x.x) */
);

/* ACCES */
CREATE TABLE ACCES(
	NUMACCES integer NOT NULL,
	ID_PERSONNE integer NOT NULL,
	CONSTRAINT pk_acces PRIMARY KEY ( NUMACCES ),
	CONSTRAINT fk_acces_id_personne FOREIGN KEY (ID_PERSONNE) REFERENCES PERSONNE ( ID_PERSONNE )
);

/* ADMINISTRATEUR */
CREATE TABLE  ADMINISTRATEUR(
	ID_GROUPEPERS integer NOT NULL,
	ID_PERSONNE integer NOT NULL,
	CONSTRAINT pk_administrateur PRIMARY KEY ( ID_GROUPEPERS, ID_PERSONNE ),
	CONSTRAINT fk_administrateur1 FOREIGN KEY (ID_PERSONNE) REFERENCES PERSONNE ( ID_PERSONNE ),
	CONSTRAINT fk_administrateur2 FOREIGN KEY (ID_GROUPEPERS) REFERENCES GROUPE ( ID_GROUPEPERS )
);

/* AFFECTATION */
CREATE TABLE AFFECTATION(
	ID_PERSONNE integer NOT NULL,
	NUMBADGE integer NOT NULL,
	DATE_AFFECTATION date NOT NULL,
	DATE_FIN_AFFECTATION date,
	CONSTRAINT pk_affectation PRIMARY KEY ( ID_PERSONNE, NUMBADGE ),
	CONSTRAINT fk_affectation_id_personne FOREIGN KEY (ID_PERSONNE) REFERENCES PERSONNE ( ID_PERSONNE ),
	CONSTRAINT fk_affectation_numbadge FOREIGN KEY (NUMBADGE) REFERENCES BADGE ( NUMBADGE )
);

/* ALARME_ACCES */
CREATE TABLE ALARME_ACCES(
	NUMALARME integer NOT NULL,
	NUMACCES integer NOT NULL,
	CONSTRAINT pk_alarme_acces PRIMARY KEY ( NUMALARME, NUMACCES ),
	CONSTRAINT fk_alarme_acces_numalarme FOREIGN KEY (NUMALARME) REFERENCES ALARME ( NUMALARME ),
	CONSTRAINT fk_alarme_acces_numacces FOREIGN KEY (NUMACCES) REFERENCES ACCES ( NUMACCES )
);

/* PERIODE_ACCES */
CREATE TABLE PERIODE_ACCES(
	LIBELLE_PLAGE_ACCES varchar(50) NOT NULL,
	LIBELLE_PLAGE_HORAIRE varchar(50) NOT NULL,
	FERIE char(4) NOT NULL, /*boolean*/
	OUVRE char(4) NOT NULL, /*boolean*/
	CONSTRAINT pk_periode_acces PRIMARY KEY (LIBELLE_PLAGE_ACCES),
	CONSTRAINT check_PERIODE_ACCES_FERIE CHECK (FERIE in ('vrai', 'faux')), /* Contrainte verifiant si la colonne FERIE de la table PERIODE_ACCES est egale à la liste enumérée seulement*/
	CONSTRAINT check_PERIODE_ACCES_OUVRE CHECK (OUVRE in ('vrai', 'faux')), /* Contrainte verifiant si la colonne OUVRE de la table PERIODE_ACCES est egale à la liste enumérée seulement*/
	CONSTRAINT fk_periode_acces_libelle FOREIGN KEY (LIBELLE_PLAGE_HORAIRE) REFERENCES PLAGE_HORAIRE (LIBELLE_PLAGE_HORAIRE)
);

/* AUTORISATION */
CREATE TABLE AUTORISATION(
	ID_GROUPEBAT integer NOT NULL,
	ID_GROUPEPERS integer NOT NULL,
	LIBELLE_PLAGE_ACCES varchar(50) NOT NULL,
	CONSTRAINT pk_autorisarion PRIMARY KEY ( ID_GROUPEBAT, ID_GROUPEPERS, LIBELLE_PLAGE_ACCES ),
	CONSTRAINT fk_autorisation_id_groupebat FOREIGN KEY (ID_GROUPEBAT) REFERENCES GROUPE_BATIMENTS( ID_GROUPEBAT ),
	CONSTRAINT fk_autorisation_id_groupepers FOREIGN KEY (ID_GROUPEPERS) REFERENCES GROUPE( ID_GROUPEPERS ),
	CONSTRAINT fk_autorisation_libelle FOREIGN KEY (LIBELLE_PLAGE_ACCES) REFERENCES PERIODE_ACCES( LIBELLE_PLAGE_ACCES )
);

/* ENTREE */
CREATE TABLE ENTREE(
	NUMACCES integer NOT NULL,
	CODE_BATIMENT varchar(10) NOT NULL,
	CODEPOINTACCES varchar(4) NOT NULL,
	ETAT_ENTREE varchar(10) NOT NULL,
	DATE_ENTREE date NOT NULL,
	CONSTRAINT pk_entree PRIMARY KEY ( NUMACCES, CODE_BATIMENT, CODEPOINTACCES ),
	CONSTRAINT ch_etat_entree CHECK (ETAT_ENTREE IN ('ENABLE', 'DISABLE')), /* Contrainte verifiant si la colonne ETAT_ENTREE de la table ENTREE est egale à la liste enumérée seulement*/
	CONSTRAINT fk_entree_numacces FOREIGN KEY (NUMACCES) REFERENCES ACCES( NUMACCES ),
	CONSTRAINT fk_entree_codepointacces FOREIGN KEY (CODEPOINTACCES, CODE_BATIMENT) REFERENCES POINT_ACCES( CODEPOINTACCES, CODE_BATIMENT )
);

/* MEMBRE */
CREATE TABLE MEMBRE(
	ID_GROUPEPERS integer NOT NULL,
	ID_PERSONNE integer NOT NULL,
	CONSTRAINT pk_membre PRIMARY KEY ( ID_GROUPEPERS, ID_PERSONNE ),
	CONSTRAINT fk_membre_id_groupepers FOREIGN KEY (ID_GROUPEPERS) REFERENCES GROUPE( ID_GROUPEPERS ),
	CONSTRAINT fk_membre_id_personne FOREIGN KEY (ID_PERSONNE) REFERENCES PERSONNE( ID_PERSONNE )
);

/* SORTIE */
CREATE TABLE SORTIE(
	NUMACCES integer,
	CODE_BATIMENT varchar(10) NOT NULL,
	CODEPOINTACCES varchar(4) NOT NULL,
	ETAT_SORTIE varchar(10) NOT NULL,
	DATE_SORTIE date NOT NULL,
	CONSTRAINT pk_sortie PRIMARY KEY ( NUMACCES,CODEPOINTACCES,CODE_BATIMENT),
	CONSTRAINT check_etat_sortie CHECK (ETAT_SORTIE in ('ENABLE','DISABLE')), /* Contrainte verifiant si la colonne ETAT_SORTIE de la table SORTIE est egale à la liste enumérée seulement*/
	CONSTRAINT fk_sortie_codepointacces FOREIGN KEY (CODEPOINTACCES, CODE_BATIMENT) REFERENCES POINT_ACCES (CODEPOINTACCES, CODE_BATIMENT),
	CONSTRAINT fk_sortie_numacces FOREIGN KEY (NUMACCES) REFERENCES ACCES (NUMACCES)
);

/* SALLE */
CREATE TABLE SALLE(
	CODE_BATIMENT  varchar(10) NOT NULL,
	NUMERO_SALLE integer NOT NULL,
	TYPE_SALLE varchar(7) NOT NULL,
	CAPACITE integer,
	CONSTRAINT pk_salle PRIMARY KEY ( CODE_BATIMENT,NUMERO_SALLE),
	CONSTRAINT check_type_salle CHECK (TYPE_SALLE in ('CM', 'TD', 'TP', 'REUNION', 'BUREAU', 'AUTRE')), /* Contrainte verifiant si la colonne TYPE_SALLE de la table SALLE est egale à la liste enumérée seulement*/
	CONSTRAINT check_capacite CHECK (CAPACITE > 0), /* Contrainte verifiant si la colonne CAPACITE de la table SALLE contient des entier uniquement positifs*/
	CONSTRAINT fk_salle_code_batiment FOREIGN KEY (CODE_BATIMENT) REFERENCES BATIMENT (CODE_BATIMENT)
);

/* RESERVATION */
CREATE TABLE RESERVATION(
	ID_GROUPEPERS  integer NOT NULL,
	CODE_BATIMENT  varchar(10) NOT NULL,
	NUMERO_SALLE integer NOT NULL,
	LIBELLE_PLAGE_SEMAINE varchar(50) NOT NULL,
	LIBELLE_PLAGE_HORAIRE varchar(50) NOT NULL,
	DATE_RESA date NOT NULL,
	JOUR_SEMAINE integer NOT NULL,
	CONSTRAINT pk_reservation PRIMARY KEY ( ID_GROUPEPERS,CODE_BATIMENT,NUMERO_SALLE,LIBELLE_PLAGE_SEMAINE,LIBELLE_PLAGE_HORAIRE),
	CONSTRAINT fk_reservation1 FOREIGN KEY (LIBELLE_PLAGE_HORAIRE) REFERENCES PLAGE_HORAIRE (LIBELLE_PLAGE_HORAIRE),
	CONSTRAINT fk_reservation2 FOREIGN KEY (LIBELLE_PLAGE_SEMAINE) REFERENCES PLAGE_SEMAINE (LIBELLE_PLAGE_SEMAINE),
	CONSTRAINT fk_reservation3 FOREIGN KEY (CODE_BATIMENT,NUMERO_SALLE) REFERENCES SALLE (CODE_BATIMENT,NUMERO_SALLE),
	CONSTRAINT fk_reservation4 FOREIGN KEY (ID_GROUPEPERS) REFERENCES GROUPE (ID_GROUPEPERS),
	CONSTRAINT check_jour_semaine CHECK (JOUR_SEMAINE BETWEEN 1 and 7) /* Contrainte verifiant si la colonne JOUR_SEMAINE de la table RESERVATION contient des entiers uniquement compris entre 1 et 7 ( 1 pour lundi... 7 pour dimanche) */
);

/* PLAGE */
CREATE TABLE PLAGE(
	LIBELLE_PLAGE_SEMAINE varchar(50) NOT NULL,
	LIBELLE_PLAGE_ACCES varchar(50) NOT NULL,
	CONSTRAINT pk_plage PRIMARY KEY ( LIBELLE_PLAGE_SEMAINE,LIBELLE_PLAGE_ACCES ),
	CONSTRAINT fk_plage_libelle_plage_acces FOREIGN KEY (LIBELLE_PLAGE_ACCES) REFERENCES PERIODE_ACCES (LIBELLE_PLAGE_ACCES),
	CONSTRAINT fk_plage_libelle_plage_semaine FOREIGN KEY (LIBELLE_PLAGE_SEMAINE) REFERENCES PLAGE_SEMAINE (LIBELLE_PLAGE_SEMAINE)
);




/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
/*****************************************************************/


/* Create Sequence */
/* Script sql permettant de créer les sequences de la base de donées, A executer après avoir create les tables en faisant appel au fichier Sequence.sql si vous ne passez pas par le fichier AutoCreate */
DROP SEQUENCE acces_sequence;
DROP SEQUENCE personne_sequence;
DROP SEQUENCE papier_identite_sequence;
DROP SEQUENCE groupe_batiments_sequence;
DROP SEQUENCE groupe_sequence;
DROP SEQUENCE badge_sequence;
DROP SEQUENCE alarme_sequence;

CREATE SEQUENCE alarme_sequence
START WITH 1
INCREMENT BY 1;

CREATE SEQUENCE badge_sequence
START WITH 1
INCREMENT BY 1;

CREATE SEQUENCE groupe_sequence
START WITH 1
INCREMENT BY 1;

CREATE SEQUENCE groupe_batiments_sequence
START WITH 1
INCREMENT BY 1;

CREATE SEQUENCE papier_identite_sequence
START WITH 1
INCREMENT BY 1;

CREATE SEQUENCE personne_sequence
START WITH 1
INCREMENT BY 1;

CREATE SEQUENCE acces_sequence
START WITH 1
INCREMENT BY 1;