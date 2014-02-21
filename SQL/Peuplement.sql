/* Peuplement Table */

/* Script sql permettant de peupler les tables de la base de donées, A executer après le fichier create.sql si vous ne passez pas par le fichier AutoCreate */

ALTER SESSION SET NLS_DATE_FORMAT='dd-mm-yyyy HH24:MI:SS';
-- alarme: numalarme number, etat_alarme varchar2, type_alarm varchar2, ref_video char
/* Exemple : ajout de l'alarme 1 dans un etat activé en detection "passage multiple" ayant pour reference de video 111 */
--insert into alarme values(alarme_sequence.nextval,'ENABLE','PASSAGE MULTIPLE','111'); 
--insert into alarme values(alarme_sequence.nextval,'DISABLE','PASSAGE MULTIPLE', null);
--insert into alarme values(alarme_sequence.nextval,'ENABLE','PAS_SORTI','112');
--insert into alarme values(alarme_sequence.nextval,'DISABLE','PAS_SORTI', null);
--insert into alarme values(alarme_sequence.nextval,'ENABLE','PAS_ENTRE','113');
--insert into alarme values(alarme_sequence.nextval,'DISABLE','PAS_ENTRE', null);
--insert into alarme values(alarme_sequence.nextval,'ENABLE','OUVERTURE_PORTE_TROP_LONG', null);
--insert into alarme values(alarme_sequence.nextval,'DISABLE','OUVERTURE_PORTE_TROP_LONG','121');


-- badge: numbadge number, etatbadge varchar2
/* Exemple : Badge numéro 1 en etat activé */
insert into badge values(badge_sequence.nextval,'ENABLE'); 
insert into badge values(badge_sequence.nextval,'ENABLE');
insert into badge values(badge_sequence.nextval,'ENABLE');
insert into badge values(badge_sequence.nextval,'ENABLE');
insert into badge values(badge_sequence.nextval,'ENABLE');
/* Exemple : Badge numéro 6 en etat desactivé */
insert into badge values(badge_sequence.nextval,'DISABLE');

insert into badge values(badge_sequence.nextval,'ENABLE');

insert into badge values(badge_sequence.nextval,'ENABLE');
insert into badge values(badge_sequence.nextval,'ENABLE');
insert into badge values(badge_sequence.nextval,'ENABLE');
insert into badge values(badge_sequence.nextval,'ENABLE');



-- groupe: id_groupepers number, nomgroupepers varchar2
insert into groupe values(groupe_sequence.nextval,'AdministrateursInfo'); 
insert into groupe values(groupe_sequence.nextval,'EtudiantsM1');
/* Exemple : Groupe de personne numero 3 ayant pour nom DLST */
insert into groupe values(groupe_sequence.nextval,'DLST');
insert into groupe values(groupe_sequence.nextval,'UFRIMAG');
insert into groupe values(groupe_sequence.nextval,'Visiteur');
insert into groupe values(groupe_sequence.nextval,'EtudiantSTAPS');

-- groupe_batiments: id_groupebat number, nomgroupebat varchar2
insert into groupe_batiments values(groupe_batiments_sequence.nextval,'UFRIMAG');
/* Exemple : Groupe de bâtiment 2 ayant pour nom DLST*/
insert into groupe_batiments values(groupe_batiments_sequence.nextval,'DLST'); 

insert into groupe_batiments values(groupe_batiments_sequence.nextval,'UFRAPS'); 

-- jour_ferie: date_ferie date
/* Exemple : Ajout d'un jour ferié, le premier janvier 2013*/
insert into jour_ferie values(to_date('01-01-2013', 'dd-mm-yyyy')); 
insert into jour_ferie values(to_date('11-01-2013', 'dd-mm-yyyy'));
insert into jour_ferie values(to_date('14-07-2013', 'dd-mm-yyyy'));

-- batiment : code_batiment varchar2, id_groupebat number, adresse varchar2
insert into batiment values('DLST-A',2,'75 rue scientifique, 38400 Saint-Martin-d''Heres');
insert into batiment values('DLST-B',2,'75 rue scientifique, 38400 Saint-Martin-d''Heres');
insert into batiment values('DLST-C',2,'75 rue scientifique, 38400 Saint-Martin-d''Heres');
insert into batiment values('UFR-A',1,'7 rue de la chimie, 38400 Saint-Martin-d''Heres');
insert into batiment values('UFR-B',1,'7 rue de la chimie, 38400 Saint-Martin-d''Heres');

insert into batiment values('STAPS-A',3,'1741 Rue de la Piscine  38610 Gières');
insert into batiment values('STAPS-B',3,'1741 Rue de la Piscine  38610 Gières');
insert into batiment values('STAPS-C',3,'1741 Rue de la Piscine  38610 Gières');


-- point_acces: codepointacces varchar2, code_batiment  varchar7, type_acces varchar2, etat_point_acces varchar2, statut_point_acces varchar2, role, 
/* Exemple : Ajout du point d'acces numero 001 en type entré/sortie étant ouvert, actif et place en exterieur*/
insert into point_acces values('001','DLST-A','ENTEREXIT','OUVERT','ACTIF','E'); 
insert into point_acces values('002','DLST-B','ENTEREXIT','FERME','ACTIF','E');
insert into point_acces values('003','UFR-A','ENTEREXIT','OUVERT','EN ATTENTE','E');
insert into point_acces values('101','DLST-C','EXIT','FERME','ACTIF','E');
insert into point_acces values('102','DLST-C','ENTER','FERME','ACTIF','E');
insert into point_acces values('101','DLST-A','ENTEREXIT','OUVERT','ACTIF','E');
insert into point_acces values('201','DLST-A','EXIT','FERME','ACTIF','E');
insert into point_acces values('202','UFR-A','ENTEREXIT','OUVERT','EN ATTENTE','E');
insert into point_acces values('001','UFR-B','ENTEREXIT','OUVERT','ACTIF','E');

insert into point_acces values('001','STAPS-A','ENTER','FERME','ACTIF','E');
insert into point_acces values('002','STAPS-A','EXIT','FERME','ACTIF','E');
insert into point_acces values('007','STAPS-A','ENTEREXIT','FERME','ACTIF','E');
insert into point_acces values('101','STAPS-B','ENTER','OUVERT','ACTIF','E');
insert into point_acces values('102','STAPS-B','EXIT','FERME','ACTIF','E');
insert into point_acces values('107','STAPS-B','ENTEREXIT','OUVERT','ACTIF','E');
insert into point_acces values('207','STAPS-C','ENTEREXIT','FERME','ACTIF','E');


-- plage_semaine: libelle_plage_semaine varchar2, semaine_debut integer,semaine_fin integer
/* Exemple : plage semaine ayant pour libelle semstre1 et s'étalent de la semaine 30 à la 52*/
insert into plage_semaine values('semestre1',30,52); 
insert into plage_semaine values('semestre2',1,29);
insert into plage_semaine values('Projet',2,5);
insert into plage_semaine values('AnneeComplete',1,52);

-- plage_horaire: libelle_plage_horaire varchar2, horaire_debut integer, horaire_fin integer
/* Exemple : plage horaire ayant pour nom ABD commençant à 09h02 et finissant à 14h02 */
insert into plage_horaire values('demiJourneeAM',to_date('01-01-2000 7:30:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 12:00:00','DD-MM-YYYY HH24:MI:SS')); 
insert into plage_horaire values('demiJourneePM',to_date('01-01-2000 13:30:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 18:30:00','DD-MM-YYYY HH24:MI:SS'));
insert into plage_horaire values('journeePleine',to_date('01-01-2000 7:30:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 18:30:00','DD-MM-YYYY HH24:MI:SS'));
insert into plage_horaire values('PermanenceFerie',to_date('01-01-2000 11:30:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 13:30:00','DD-MM-YYYY HH24:MI:SS'));
insert into plage_horaire values('ABD',to_date('01-01-2000 14:00:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 15:00:00','DD-MM-YYYY HH24:MI:SS'));
insert into plage_horaire values('PC',to_date('01-01-2000 08:00:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 10:00:00','DD-MM-YYYY HH24:MI:SS'));
insert into plage_horaire values('TPABD',to_date('01-01-2000 16:00:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 17:00:00','DD-MM-YYYY HH24:MI:SS'));

-- papier_identite: num_carte_id varchar2, date_delivrance date, lieu_delivrance integer
/* Exemple : papier d'identité numéro 1 étant delivré le 1 er janvier 2005 à Grenoble */
/*
insert into papier_identite values(papier_identite_sequence.nextval,to_date('01-01-2005', 'dd-mm-yyyy'),'Grenoble'); 
insert into papier_identite values(papier_identite_sequence.nextval,to_date('01-04-2005', 'dd-mm-yyyy'),'Meylan');
insert into papier_identite values(papier_identite_sequence.nextval,to_date('01-08-2005', 'dd-mm-yyyy'),'Eybens');
*/
-- personne: id_personne integer, nom varchar2, prenom varchar2, date_naissance date, lieu_naissance varchar2, bureau varchar2, 
--telephone varchar2, email varchar2, num_carte_etu integer, filiere varchar2, annee_promo integer, num_carte_id varchar2

insert into personne values(personne_sequence.nextval,'Dupont', 'David', to_date('01-04-1991', 'dd-mm-yyyy'),'ST-Martin-d''hères',null,null,null,'1111112','M1MIAGE','2012');
insert into personne values(personne_sequence.nextval,'Dupont', 'Loic', to_date('01-04-1992', 'dd-mm-yyyy'),'ST-Martin-d''hères',null,null,null,'1111113','M1MIAGE','2012');
/* Exemple : Personne numéro 3 ayant pour nom prénom Martin lucas, date de naissance : 01/04/1985 à dijon, bureau 102, tel : 0648..., et pour mail..*/
insert into personne values(personne_sequence.nextval,'Martin', 'Lucas', to_date('01-04-1985', 'dd-mm-yyyy'),'Dijon','102','06.48.15.48.26','lucas.martin@gmail.com',null,null,null); 
insert into personne values(personne_sequence.nextval,'Martin', 'Leo', to_date('01-04-1985', 'dd-mm-yyyy'),'Grenoble','102','06.48.15.48.26','lucas.martin@gmail.com',null,null,null);
insert into personne values(personne_sequence.nextval,'Dupond', 'Julien', to_date('01-04-1985', 'dd-mm-yyyy'),'Lyon','102','06.48.15.48.26','lucas.martin@gmail.com',null,null,null);
insert into personne values(personne_sequence.nextval,'Dupond', 'Randy', to_date('01-04-1985', 'dd-mm-yyyy'),'Lyon','102','06.48.15.48.26','lucas.martin@gmail.com',null,null,null);

insert into personne values(personne_sequence.nextval,'Marsh', 'Stan', to_date('07-07-2000', 'dd-mm-yyyy'),'South Park',null,'06.07.08.09.10','stan.marsh@gmail.com',20815677,'L1STAPS',2012);
insert into personne values(personne_sequence.nextval,'Nicolas', 'Jeremy', to_date('01-07-1980', 'dd-mm-yyyy'),'Atlanta',null,'06.07.08.09.10','Jerem@gmail.com',21589065,'L1STAPS',2012);
insert into personne values(personne_sequence.nextval,'Jeremy', 'Nicolas', to_date('15-08-1991', 'dd-mm-yyyy'),'Paris',null,'06.07.08.09.10','Nico@gmail.com',20812476,'M1MIAGE',2012);
insert into personne values(personne_sequence.nextval,'Marsh', 'Randy', to_date('01-01-1961', 'dd-mm-yyyy'),'Denver','107','06.07.08.09.10','marsh.randy@gmail.com',null,null,null);

-- acces: numacces, Id_Personne
-- numacces : 1
/* Exemple : acces numero 1 pour la personne 1 ( Dupont David)*/
--insert into acces values(acces_sequence.nextval,1);
--insert into acces values(acces_sequence.nextval,1);
---- numacces : 2
--insert into acces values(acces_sequence.nextval,2);
--insert into acces values(acces_sequence.nextval,2);
---- numacces : 3
--insert into acces values(acces_sequence.nextval,3);
--insert into acces values(acces_sequence.nextval,3);
---- numacces : 4
--insert into acces values(acces_sequence.nextval,4);
--insert into acces values(acces_sequence.nextval,4);
---- numacces : 5
--insert into acces values(acces_sequence.nextval,5);
--insert into acces values(acces_sequence.nextval,5);

-- administrateur: id_groupepers number, id_personne number


-- id_groupepers : 1
/* Exemple : David dupont est affecté au groupe de personne 1   */
insert into administrateur values(1,1); 
-- id_groupepers : 2
insert into administrateur values(1,2);

-- affectation: id_personne number, numbadge number, date_affectation date, date_fin_affectation date (optional)
/* Exemple : la personne 1 à un le badge numero 1 de^puis le 16/01/2013 pour une durée indeterminée */
insert into affectation values(1,1,to_date('16-01-2013', 'dd-mm-yyyy'),null); 
insert into affectation values(2,2,to_date('16-01-2013', 'dd-mm-yyyy'),null);
insert into affectation values(3,3,to_date('16-01-2013', 'dd-mm-yyyy'),null);
insert into affectation values(4,4,to_date('17-01-2013', 'dd-mm-yyyy'),null);
insert into affectation values(5,5,to_date('17-01-2013', 'dd-mm-yyyy'),to_date('25-01-2013', 'dd-mm-yyyy'));

insert into affectation values(7,8,to_date('01-01-2013', 'dd-mm-yyyy'),null);
insert into affectation values(8,9,to_date('24-01-2013', 'dd-mm-yyyy'),null);
insert into affectation values(9,10,to_date('20-01-2013', 'dd-mm-yyyy'),null);
insert into affectation values(10,11,to_date('01-01-2013', 'dd-mm-yyyy'),null);



-- alarme_acces: numalarme number, numacces number
/* Exemple : l'alarme 1 pour l'acces 1*/
--insert into alarme_acces values(1,1); 
--insert into alarme_acces values(2,2);
--insert into alarme_acces values(3,1);
--insert into alarme_acces values(4,3);
--insert into alarme_acces values(5,1);

-- periode_acces : libelle_plage_acces varchar2, libelle_plage_horaire varchar2, ferie char, ouvre char
/* Exemple : periode d'acces ayant pour nom 'Acces Batiment UFR' .. */
insert into periode_acces values('Acces Batiment UFR','journeePleine','faux','vrai');
insert into periode_acces values('Acces Batiment DLST jour ferie','PermanenceFerie','vrai','vrai');
insert into periode_acces values('Acces Batiment DLST jour ouvre','journeePleine','faux','vrai');

insert into periode_acces values('Acces Batiment UFRAPS','journeePleine','faux','vrai');

-- plage : libelle_plage_semaine varchar2, libelle_plage_acces varchar2
insert into plage values('semestre1','Acces Batiment UFR');
insert into plage values('semestre2','Acces Batiment UFR');
insert into plage values('semestre1','Acces Batiment DLST jour ouvre');

insert into plage values('AnneeComplete','Acces Batiment UFRAPS');

-- autorisation : id_groupebat number, id_groupepers number, libelle_plage_acces varchar2
insert into autorisation values(2,3,'Acces Batiment DLST jour ferie');
insert into autorisation values(1,4,'Acces Batiment UFR');
insert into autorisation values(2,3,'Acces Batiment DLST jour ouvre');
insert into autorisation values(1,2,'Acces Batiment UFR');
insert into autorisation values(2,1,'Acces Batiment DLST jour ouvre');
insert into autorisation values(2,5,'Acces Batiment DLST jour ferie');

insert into autorisation values(3,6,'Acces Batiment UFRAPS');

-- membre : id_groupepers number, id_personne number
insert into membre values(1,1);
insert into membre values(2,1);
insert into membre values(2,2);
insert into membre values(2,3);
insert into membre values(1,4);
insert into membre values(5,5);
insert into membre values(5,6);

insert into membre values(6,7);
insert into membre values(6,8);
insert into membre values(2,9);
insert into membre values(1,10);

-- entree : numacces number, code_batiment varchar2, codepointacces varchar2, etat_entree varchar2, date_entree date
--insert into entree values(1,'UFR-A','202','ENABLE',to_date('16-01-2013 15:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into entree values(3,'UFR-B','001','ENABLE',to_date('17-01-2013 11:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into entree values(5,'UFR-A','202','ENABLE',to_date('16-01-2013 13:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into entree values(7,'DLST-A','001','ENABLE',to_date('10-01-2013 15:30:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into entree values(9,'DLST-C','102','ENABLE',to_date('11-01-2013 12:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--
--
---- sortie : numacces number, code_batiment varchar2, codepointacces varchar2, etat_sortie varchar2, date_sortie date
--insert into sortie values(2,'UFR-A','202','ENABLE',to_date('16-01-2013 17:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into sortie values(4,'UFR-B','001','ENABLE',to_date('17-01-2013 15:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into sortie values(6,'UFR-A','202','ENABLE',to_date('16-01-2013 13:30:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into sortie values(8,'DLST-A','001','ENABLE',to_date('10-01-2013 17:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--insert into sortie values(10,'DLST-C','101','ENABLE',to_date('11-01-2013 13:00:00', 'dd-mm-yyyy HH24:MI:SS'));

-- salle : code_batiment varchar2, numero_salle number, type_salle varchar2, capacite number
insert into salle values('UFR-A',1,'CM',30);
insert into salle values('DLST-A',2,'TD',15);
insert into salle values('DLST-C',20,'TP',40);

-- reservation : id_groupepers number, code_batiment varchar7, numero_salle number, libelle_plage_semaine varchar2, libelle_plage_horaire varchar2, date_resa date, jour_semaine number
insert into reservation values(1,'UFR-A',1,'semestre1','ABD',to_date('09-01-2013', 'dd-mm-yyyy'),7);
insert into reservation values(2,'UFR-A',1,'semestre2','PC',to_date('11-01-2013', 'dd-mm-yyyy'),6);
insert into reservation values(1,'DLST-C',20,'Projet','ABD',to_date('03-01-2013', 'dd-mm-yyyy'),4);
insert into reservation values(1,'DLST-C',20,'Projet','TPABD',to_date('03-01-2013', 'dd-mm-yyyy'),2);
insert into reservation values(1,'DLST-C',20,'Projet','PC',to_date('03-01-2013', 'dd-mm-yyyy'),3);

