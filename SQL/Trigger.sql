/* INFO UTILES :
	Pour voir les triggers que vous avez crŽŽ, utiliser: SELECT TRIGGER_NAME FROM USER_TRIGGER;
*/	

/* Triggers Rudy */


/* TRIGGER 1 : Vérifier les restrictions sur les types de salles pour l'accès des personnes */
/* TRIGGER 5 : Un utilisateur ne peut sortir d’un bâtiment sans y être entré au préalable */
/* TRIGGER 6 : Utilisateur ne peut entrer dans un bâtiment en étant déjà entré dans un autre bâtiment */
/* TRIGGER 7 : Une personne ne peut pas faire un accès par un point d’accès n’autorisant pas ce type d’accès (exemple sortir par une porte d’entrée) */
/* TRIGGER 21 : Une personne ne peut sortir ou entrer par une porte que s’il en a l’autorisation.*/

/**/
CREATE OR REPLACE TRIGGER TR_Authorisation_Entree
BEFORE INSERT ON ENTREE
FOR EACH ROW
DECLARE
	typeAcces varchar(9);
	numAccesDerniereEntree integer;
	dateDerniereEntree date;
	horairedeb date;
	horairefin date;
	semdeb integer;
	semfin integer;
	newdate date;
	codebatDerniereEntree varchar(10);
	estsorti integer;
	requete varchar(400);
	estferie char(4);
	countferie integer;
BEGIN
	SELECT TYPE_ACCES into typeAcces FROM POINT_ACCES WHERE CODEPOINTACCES=:NEW.CODEPOINTACCES AND CODE_BATIMENT=:NEW.CODE_BATIMENT; --recuperation du type du point
	BEGIN
		SELECT E.NUMACCES into numAccesDerniereEntree FROM ACCES A, ENTREE E WHERE A.NUMACCES=E.NUMACCES AND A.ID_PERSONNE = (SELECT ID_PERSONNE FROM ACCES WHERE NUMACCES=:NEW.NUMACCES) AND E.ETAT_ENTREE='ENABLE' AND E.DATE_ENTREE=(SELECT GREATEST(DATE_ENTREE) FROM ENTREE E2 WHERE E.NUMACCES=E2.NUMACCES AND E.ETAT_ENTREE='ENABLE');
		
		requete := 'SELECT DATE_ENTREE FROM ENTREE WHERE NUMACCES=:numacces';
		EXECUTE IMMEDIATE (requete) into dateDerniereEntree USING numAccesDerniereEntree;
		
		requete := 'SELECT CODE_BATIMENT FROM ENTREE WHERE NUMACCES=:numacces';
		EXECUTE IMMEDIATE (requete) into codebatDerniereEntree USING numAccesDerniereEntree;
		
		requete := 'SELECT COUNT(*) FROM ACCES A, SORTIE S WHERE A.ID_PERSONNE = (SELECT ID_PERSONNE FROM ACCES WHERE NUMACCES='||:NEW.NUMACCES||') AND A.NUMACCES=S.NUMACCES AND S.CODE_BATIMENT='''||codebatDerniereEntree||''' AND S.DATE_SORTIE BETWEEN to_date('''||dateDerniereEntree||''', ''dd-mm-yyyy HH24:MI:SS'') AND to_date('''||:NEW.DATE_ENTREE||''', ''dd-mm-yyyy HH24:MI:SS'')';
		--il existe une sortie pour le bâtiment correspondant a la dernière entrée entre dateDerniereEntree et :new.DATE_ENTREE
		EXECUTE IMMEDIATE (requete) into estsorti;
	EXCEPTION
	--Si pas de données, c'est la premiere entrée donc on initialise les variables avec les valeurs OK
		WHEN NO_DATA_FOUND THEN
			estsorti := 1;
	END;
	BEGIN
		SELECT MIN(PH.HORAIRE_DEBUT), MAX(PH.HORAIRE_FIN) INTO horairedeb, horairefin FROM ACCES A, MEMBRE M, AUTORISATION AU, BATIMENT B, PERIODE_ACCES PA, PLAGE_HORAIRE PH  WHERE A.NUMACCES=:NEW.NUMACCES AND A.ID_PERSONNE=M.ID_PERSONNE AND M.ID_GROUPEPERS=AU.ID_GROUPEPERS AND AU.ID_GROUPEBAT=B.ID_GROUPEBAT AND B.CODE_BATIMENT=:NEW.CODE_BATIMENT AND AU.LIBELLE_PLAGE_ACCES=PA.LIBELLE_PLAGE_ACCES AND PA.LIBELLE_PLAGE_HORAIRE=PH.LIBELLE_PLAGE_HORAIRE;
		SELECT MIN(PS.SEMAINE_DEBUT), MAX(PS.SEMAINE_FIN) INTO semdeb, semfin FROM ACCES A, MEMBRE M, AUTORISATION AU, BATIMENT B, PERIODE_ACCES PA, PLAGE P, PLAGE_SEMAINE PS  WHERE A.NUMACCES=:NEW.NUMACCES AND A.ID_PERSONNE=M.ID_PERSONNE AND M.ID_GROUPEPERS=AU.ID_GROUPEPERS AND AU.ID_GROUPEBAT=B.ID_GROUPEBAT AND B.CODE_BATIMENT=:NEW.CODE_BATIMENT AND AU.LIBELLE_PLAGE_ACCES=PA.LIBELLE_PLAGE_ACCES AND AU.LIBELLE_PLAGE_ACCES=P.LIBELLE_PLAGE_ACCES AND P.LIBELLE_PLAGE_SEMAINE=PS.LIBELLE_PLAGE_SEMAINE;
		SELECT ferie into estferie FROM ACCES A, MEMBRE M, AUTORISATION AU, PERIODE_ACCES PA, BATIMENT B  WHERE A.NUMACCES=:NEW.NUMACCES AND A.ID_PERSONNE=M.ID_PERSONNE AND M.ID_GROUPEPERS=AU.ID_GROUPEPERS AND AU.ID_GROUPEBAT=B.ID_GROUPEBAT AND B.CODE_BATIMENT=:NEW.CODE_BATIMENT AND AU.LIBELLE_PLAGE_ACCES=PA.LIBELLE_PLAGE_ACCES;
		select count(*) into countferie from jour_ferie where date_ferie = to_date(to_char(:NEW.DATE_ENTREE,'dd-mm-yyyy'),'dd-mm-yyyy');
	EXCEPTION
	--Si pas de données, il n'y a pas d'autorisation donc on interdit l'entrée
	WHEN NO_DATA_FOUND THEN
		horairedeb := to_date('01/01/2000 23:59:59','DD/MM/YYYY HH24:MI:SS');
		horairefin := to_date('01/01/2000 00:00:00','DD/MM/YYYY HH24:MI:SS');
		semdeb := 52;
		semfin := 1;
		estferie := 'faux';
		countferie :=1;
	END;
	
	newdate := to_date('01/01/2000 '||to_char(:NEW.DATE_ENTREE,'HH24:MI:SS'),'DD/MM/YYYY HH24:MI:SS');
	IF (typeAcces = 'EXIT') THEN
		:NEW.ETAT_ENTREE := 'DISABLE';
		--Creation alarme: entré par sortie
		insert into alarme values(alarme_sequence.nextval,'ENABLE','ENTRE PAR UNE SORTIE', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES);
	ELSIF (TO_CHAR(:NEW.DATE_ENTREE, 'WW') >  semfin or TO_CHAR(:NEW.DATE_ENTREE, 'WW') < semdeb) THEN
		:NEW.ETAT_ENTREE := 'DISABLE';
		--Creation alarme: l'utilisateur n'a pas les autorisaions necessaires
		insert into alarme values(alarme_sequence.nextval,'ENABLE','ENTREE EN DEHORS DES PLAGES SEMAINES', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES); 
	ELSIF (newdate >  horairefin or newdate < horairedeb) THEN
		:NEW.ETAT_ENTREE := 'DISABLE';
		--Creation alarme: l'utilisateur n'a pas les autorisaions necessaires
		insert into alarme values(alarme_sequence.nextval,'ENABLE','ENTREE EN DEHORS DES PLAGES HORAIRES', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES); 
	ELSIF (estsorti = 0) THEN
		:NEW.ETAT_ENTREE := 'DISABLE';
		--Creation alarme: Pas sortie du dernier batiment dans lequel il est entré
		insert into alarme values(alarme_sequence.nextval,'ENABLE','DEJA ENTRE AILLEURS', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES); 
	ELSIF (estferie = 'faux' AND countferie != 0) THEN
		:NEW.ETAT_ENTREE := 'DISABLE';
		--Creation alarme: Entree un jour ferie
		insert into alarme values(alarme_sequence.nextval,'ENABLE','ENTREE JOUR FERIE', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES); 
	END IF;
END;
/
-- pour tester après peuplement
-- insert into acces values(acces_sequence.nextval,1);
-- delete from sortie where numacces=2;
-- insert into entree values(1,'UFR-A','202','ENABLE',to_date('11-01-2013 18:00:00', 'dd-mm-yyyy HH24:MI:SS'));
--SELECT COUNT(*) FROM ACCES A, SORTIE S WHERE A.ID_PERSONNE = (SELECT ID_PERSONNE FROM ACCES WHERE NUMACCES=11) AND A.NUMACCES=S.NUMACCES AND S.CODE_BATIMENT='UFR-A' AND S.DATE_SORTIE BETWEEN to_date('16-01-2013 15:00:00', 'dd-mm-yyyy HH24:MI:SS') AND to_date('16-01-2013 18:00:00', 'dd-mm-yyyy HH24:MI:SS');
/* 
 * SELECT ferie FROM ACCES A, MEMBRE M, AUTORISATION AU, PERIODE_ACCES PA, BATIMENT B  WHERE A.NUMACCES=7 AND A.ID_PERSONNE=M.ID_PERSONNE AND M.ID_GROUPEPERS=AU.ID_GROUPEPERS AND AU.ID_GROUPEBAT=B.ID_GROUPEBAT AND B.CODE_BATIMENT='DLST-A' AND AU.LIBELLE_PLAGE_ACCES=PA.LIBELLE_PLAGE_ACCES;
 * select * from jour_ferie where date_ferie = to_date(to_char(to_date('10-01-2013 17:00:00', 'dd-mm-yyyy HH24:MI:SS'),'dd-mm-yyyy'),'dd-mm-yyyy');
SELECT MIN(PH.HORAIRE_DEBUT), MAX(PH.HORAIRE_FIN) FROM ACCES A, MEMBRE M, AUTORISATION AU, PERIODE_ACCES PA, PLAGE_HORAIRE PH  WHERE A.NUMACCES=7 AND A.ID_PERSONNE=M.ID_PERSONNE AND M.ID_GROUPEPERS=AU.ID_GROUPEPERS AND AU.LIBELLE_PLAGE_ACCES=PA.LIBELLE_PLAGE_ACCES AND PA.LIBELLE_PLAGE_HORAIRE=PH.LIBELLE_PLAGE_HORAIRE;
SELECT PS.SEMAINE_DEBUT, PS.SEMAINE_FIN FROM ACCES A, MEMBRE M, AUTORISATION AU, BATIMENT B, PERIODE_ACCES PA, PLAGE P, PLAGE_SEMAINE PS  WHERE A.NUMACCES=7 AND A.ID_PERSONNE=M.ID_PERSONNE AND M.ID_GROUPEPERS=AU.ID_GROUPEPERS AND AU.ID_GROUPEBAT=B.ID_GROUPEBAT AND B.CODE_BATIMENT='DLST-A' AND AU.LIBELLE_PLAGE_ACCES=PA.LIBELLE_PLAGE_ACCES AND AU.LIBELLE_PLAGE_ACCES=P.LIBELLE_PLAGE_ACCES AND P.LIBELLE_PLAGE_SEMAINE=PS.LIBELLE_PLAGE_SEMAINE;

*
**/
CREATE OR REPLACE TRIGGER TR_Authorisation_Sortie
BEFORE INSERT ON SORTIE
FOR EACH ROW
DECLARE
	typeAcces varchar(9);
	numAccesDerniereEntree integer;
	dateDerniereEntree date;
	codebatDerniereEntree varchar(10);
	dejasorti integer;
	requete varchar(400);
BEGIN
	SELECT TYPE_ACCES into typeAcces FROM POINT_ACCES WHERE CODEPOINTACCES=:NEW.CODEPOINTACCES AND CODE_BATIMENT=:NEW.CODE_BATIMENT;
	BEGIN
		SELECT E.NUMACCES into numAccesDerniereEntree FROM ACCES A, ENTREE E WHERE A.NUMACCES=E.NUMACCES AND A.ID_PERSONNE = (SELECT ID_PERSONNE FROM ACCES WHERE NUMACCES=:NEW.NUMACCES) AND E.ETAT_ENTREE='ENABLE' AND E.DATE_ENTREE=(SELECT GREATEST(DATE_ENTREE) FROM ENTREE E2 WHERE E.NUMACCES=E2.NUMACCES AND E.ETAT_ENTREE='ENABLE');
		
		requete := 'SELECT DATE_ENTREE FROM ENTREE WHERE NUMACCES=:numacces';
		EXECUTE IMMEDIATE (requete) into dateDerniereEntree USING numAccesDerniereEntree;
		
		requete := 'SELECT CODE_BATIMENT FROM ENTREE WHERE NUMACCES=:numacces';
		EXECUTE IMMEDIATE (requete) into codebatDerniereEntree USING numAccesDerniereEntree;
		
		requete := 'SELECT COUNT(*) FROM ACCES A, SORTIE S WHERE A.ID_PERSONNE = (SELECT ID_PERSONNE FROM ACCES WHERE NUMACCES='||:NEW.NUMACCES||') AND A.NUMACCES=S.NUMACCES AND S.ETAT_SORTIE=''ENABLE'' AND S.CODE_BATIMENT='''||:NEW.CODE_BATIMENT||''' AND S.DATE_SORTIE BETWEEN to_date('''||dateDerniereEntree||''', ''dd-mm-yyyy HH24:MI:SS'') AND to_date('''||:NEW.DATE_SORTIE||''', ''dd-mm-yyyy HH24:MI:SS'')';
		--il existe déjà une sortie pour ce bâtiment entre dateDerniereEntree et :new.DATE_SORTIE
		EXECUTE IMMEDIATE (requete) into dejasorti;
	EXCEPTION
	-- Si pas de données, c'est qu'il n'y a pas eu d'entrée donc on initialise les variables avec les valeurs NON OK
		WHEN NO_DATA_FOUND THEN
			codebatDerniereEntree := :NEW.CODE_BATIMENT || 'NULL' ;
			dejasorti := 1;
	END;
	
	IF (typeAcces = 'ENTER') THEN
		:NEW.ETAT_SORTIE := 'DISABLE';
		--Creation alarme: sortie par entrée
		insert into alarme values(alarme_sequence.nextval,'ENABLE','SORTIE PAR UNE ENTREE', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES); 
	ELSIF (:NEW.CODE_BATIMENT != codebatDerniereEntree) THEN
		:NEW.ETAT_SORTIE := 'DISABLE';
		--CREATION alarme: PAS entré dans ce bâtiment!!
		insert into alarme values(alarme_sequence.nextval,'ENABLE','SORTIE SANS ENTREE', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES); 
	ELSIF (dejasorti > 0) THEN
		:NEW.ETAT_SORTIE := 'DISABLE';
		--Creation alarme: :  double-sortie -> PAS re-entré dans ce bâtiment!!
		insert into alarme values(alarme_sequence.nextval,'ENABLE','DOUBLE SORTIE', null);
		insert into alarme_acces values(alarme_sequence.currval,:NEW.NUMACCES); 
	ELSE
		:NEW.ETAT_SORTIE := 'ENABLE';
	END IF;
END;
/
--pour tester après peuplement
-- ALTER SESSION SET NLS_DATE_FORMAT='dd-mm-yyyy HH24:MI:SS';
-- insert into acces values(acces_sequence.nextval,1);
-- insert into sortie values(11,'UFR-A','202','ENABLE',to_date('16-01-2013 17:30:00', 'dd-mm-yyyy HH24:MI:SS'));
	

/* TRIGGER 17 :Les alarmes sont archivées définitivement (on ne peut pas supprimer d’alarme)*/
CREATE OR REPLACE TRIGGER TR_Supp_Alarme
BEFORE DELETE ON ALARME
FOR EACH ROW
BEGIN
	RAISE_APPLICATION_ERROR(-20024,'Il est interdit de supprimer une alarme');
END;
/
	



/* Fin Triggers Rudy */



/* Triggers Jérémy */

/*Si le groupe pers est un meta groupe alors il ne peut pas etre ref*/
CREATE OR REPLACE TRIGGER TR_Check_meta_Membre
BEFORE INSERT OR UPDATE ON MEMBRE
FOR EACH ROW
DECLARE
    groupeEstGroupeBat integer;
BEGIN

    SELECT COUNT(*) INTO groupeEstGroupeBat FROM GROUPE_BATIMENTS B, GROUPE G WHERE G.ID_GROUPEPERS = :NEW.ID_GROUPEPERS AND G.NOMGROUPEPERS = B.NOMGROUPEBAT ;
    IF ( groupeEstGroupeBat > 0 ) THEN
        RAISE_APPLICATION_ERROR(-20085,'Si le groupe pers est un meta groupe alors il ne peut pas etre ref');
    END IF;
END;
/

/*Si le groupe pers est un meta groupe alors il ne peut pas etre ref*/
CREATE OR REPLACE TRIGGER TR_Check_meta_ADMINISTRATEUR
BEFORE INSERT OR UPDATE ON ADMINISTRATEUR
FOR EACH ROW
DECLARE
    groupeEstGroupeBat integer;
BEGIN

    SELECT COUNT(*) INTO groupeEstGroupeBat FROM GROUPE_BATIMENTS B, GROUPE G WHERE G.ID_GROUPEPERS = :NEW.ID_GROUPEPERS AND G.NOMGROUPEPERS = B.NOMGROUPEBAT ;
    IF ( groupeEstGroupeBat > 0 ) THEN
        RAISE_APPLICATION_ERROR(-20085,'Si le groupe pers est un meta groupe alors il ne peut pas etre ref');
    END IF;
END;
/

/*Si le groupe pers est un meta groupe alors il ne peut pas etre ref*/
CREATE OR REPLACE TRIGGER TR_Check_meta_RESERVATION
BEFORE INSERT OR UPDATE ON RESERVATION
FOR EACH ROW
DECLARE
    groupeEstGroupeBat integer;
BEGIN

    SELECT COUNT(*) INTO groupeEstGroupeBat FROM GROUPE_BATIMENTS B, GROUPE G WHERE G.ID_GROUPEPERS = :NEW.ID_GROUPEPERS AND G.NOMGROUPEPERS = B.NOMGROUPEBAT ;
    IF ( groupeEstGroupeBat > 0 ) THEN
        RAISE_APPLICATION_ERROR(-20085,'Si le groupe pers est un meta groupe alors il ne peut pas etre ref');
    END IF;
END;
/

/*Si on supprime un batiment on supprime egalement les ligne qui avaient des references dessus*/
CREATE OR REPLACE TRIGGER TR_Suppr_Bat
BEFORE DELETE ON BATIMENT
FOR EACH ROW
BEGIN
	DELETE FROM RESERVATION WHERE CODE_BATIMENT=:OLD.CODE_BATIMENT;
	DELETE FROM SALLE WHERE CODE_BATIMENT=:OLD.CODE_BATIMENT;
	DELETE FROM SORTIE WHERE CODE_BATIMENT=:OLD.CODE_BATIMENT;
	DELETE FROM ENTREE WHERE CODE_BATIMENT=:OLD.CODE_BATIMENT;
	DELETE FROM POINT_ACCES WHERE CODE_BATIMENT=:OLD.CODE_BATIMENT;
END;
/

/*Delete cascade sur delete salle*/
CREATE OR REPLACE TRIGGER TR_Delete_Salle
BEFORE DELETE ON SALLE
FOR EACH ROW
BEGIN
	DELETE FROM RESERVATION WHERE NUMERO_SALLE=:OLD.NUMERO_SALLE;
END;
/


/*Si on supprime un groupe de batiments on supprime les lignes qui avaient des references dessus*/
CREATE OR REPLACE TRIGGER TR_Groupe_Bat
BEFORE DELETE ON GROUPE_BATIMENTS
FOR EACH ROW
BEGIN

	UPDATE BATIMENT SET ID_GROUPEBAT = null WHERE ID_GROUPEBAT=:OLD.ID_GROUPEBAT;

	DELETE FROM AUTORISATION WHERE ID_GROUPEBAT=:OLD.ID_GROUPEBAT;

	DELETE FROM GROUPE WHERE NOMGROUPEPERS=:OLD.NOMGROUPEBAT;

END;
/



/* TRIGGER 24 : Le code batiment doit etre compris entre 3 et 7 caractères */
CREATE OR REPLACE TRIGGER TR_codeBatiment
AFTER INSERT OR UPDATE ON BATIMENT
BEGIN
	FOR codeBatiment in (SELECT CODE_BATIMENT FROM BATIMENT )
	LOOP
		IF (length(codeBatiment.CODE_BATIMENT) < 3 OR length(codeBatiment.CODE_BATIMENT) > 10) THEN
			RAISE_APPLICATION_ERROR(-20024,'Le code batiment doit etre compris entre 3 et 10 caractères pour ' || codeBatiment.CODE_BATIMENT);
		END IF;
	END LOOP;
END;
/


/* TRIGGER 3 : Vérifier que si un badge est permanent il n’y a pas de date de fin de validité et sinon il y en a une. Seul les visiteurs ont des badges provisoires */
CREATE OR REPLACE TRIGGER TR_badgePermanent
AFTER INSERT OR UPDATE ON AFFECTATION
BEGIN
	FOR affectation IN (SELECT A.DATE_FIN_AFFECTATION, G.NOMGROUPEPERS, M.ID_PERSONNE FROM AFFECTATION A, MEMBRE M, GROUPE G WHERE A.ID_PERSONNE = M.ID_PERSONNE AND G.ID_GROUPEPERS = M.ID_GROUPEPERS )
	LOOP
		IF (affectation.DATE_FIN_AFFECTATION IS null AND affectation.NOMGROUPEPERS = 'Visiteur' ) THEN
			RAISE_APPLICATION_ERROR(-20031,'Un visiteur doit avoir un badge provisoire || ID_PERSONNE : ' || affectation.ID_PERSONNE || ' || NOMGROUPEPERS : ' || affectation.NOMGROUPEPERS || ' || DATE_FIN_AFFECTATION : ' || affectation.DATE_FIN_AFFECTATION);
		END IF;
		IF (affectation.DATE_FIN_AFFECTATION IS NOT null AND affectation.NOMGROUPEPERS != 'Visiteur' ) THEN
			RAISE_APPLICATION_ERROR(-20032,'Une personne non visiteur doit avoir un badge permanent || ID_PERSONNE : ' || affectation.ID_PERSONNE || ' || NOMGROUPEPERS : ' || affectation.NOMGROUPEPERS || ' || DATE_FIN_AFFECTATION : ' || affectation.DATE_FIN_AFFECTATION);
		END IF;
	END LOOP;
END;
/

/* TRIGGER 8 : Lors de la création d’une personne, un badge lui est attribué automatiquement. --> Gerer dans l'appli*/

/* TRIGGER 9 : Si une personne est supprimée, on désactive ses badges.
CREATE OR REPLACE TRIGGER TR_desactiveBadge
AFTER DELETE ON PERSONNE
FOR EACH ROW
BEGIN
	FOR affectation in ( SELECT * FROM AFFECTATION WHERE ID_PERSONNE = :OLD.ID_PERSONNE )
	LOOP
		UPDATE BADGE SET ETATBADGE = 'DISABLE' WHERE NUMBADGE = affectation.NUMBADGE AND BADGE.ETATBADGE = 'ENABLE';
	END LOOP;
END;
/ --> Gerer dans l'appli de maniere beaucoup plus simple puisqu'une personne est cense n'avoir qu'un seul badge actif*/

/* TRIGGER 19 : Une personne fait parti d’au moins un groupe */

/* TRIGGER 20 : Une personne possède un et un seul badge actif */
CREATE OR REPLACE TRIGGER TR_unSeulBadgeActif
BEFORE INSERT OR UPDATE ON AFFECTATION
DECLARE
	idPersonne integer;
	cpt integer;
BEGIN
	idPersonne := 0;
	cpt := 0;
	FOR affectation IN ( SELECT A.ID_PERSONNE, B.ETATBADGE FROM BADGE B, AFFECTATION A WHERE A.NUMBADGE = B.NUMBADGE ORDER BY A.ID_PERSONNE )
	LOOP 
		IF (idPersonne != affectation.ID_PERSONNE) THEN
			idPersonne := affectation.ID_PERSONNE;
			cpt := 0;
		END IF;

		IF (affectation.ETATBADGE = 'ENABLE') THEN
			cpt := cpt + 1;
		END IF;
		
		IF (cpt > 1) THEN
			RAISE_APPLICATION_ERROR(-20020,'La personne N°'|| affectation.ID_PERSONNE || ' possède plusieurs badges acitfs');
		END IF;
	END LOOP;
END;
/

/* Fin Triggers Jérémy */


/* Triggers Cyril */
/* TRIGGER 4 : Un point d'accès est rattaché à au moins un et au plus deux bâtiments (cas d’un point d’acc??s interface entre deux bâtiments) et porte un numéro de 3 chiffres, le
premier indiquant l'étage, les deux suivants, le numéro à cet étage.

CREATE OR REPLACE TRIGGER TR_PointAccesBat
AFTER UPDATE OR INSERT ON POINT_ACCES_BATIMENT
DECLARE
	nbPointAcces integer;
BEGIN

	SELECT Max(count(*)) into nbPointAcces FROM POINT_ACCES_BATIMENT P GROUP BY P.CODEPOINTACCES;
		
		IF (nbPointAcces>2) THEN
			RAISE_APPLICATION_ERROR(-20002,'Le tuple insere leve l erreur suivante : 1 point d acces ne peut etre relie à plus de 2 batiments');
		END IF;
		
	
END;
/
*/


/* PAS Testé TRIGGER 11 :Les autorisations des groupes de personnes relatives à un groupe de bâtiment ne doivent pas être plus
permissives que celles définies par le méta-groupe correspondant. */
CREATE OR REPLACE TRIGGER TR_autorisationGroupe
AFTER UPDATE OR INSERT ON AUTORISATION
DECLARE
	
	metasemaineDebut integer;
	metasemaineFin integer;
	metahoraireDebut date;
	metahoraireFin date;
	semaineDebut integer;
	semaineFin integer;
	horaireDebut date;
	horaireFin date;
	id_groupeBat integer :=-1;
	requete varchar(400);
BEGIN
	dbms_output.put_line('begin');
	FOR autorisation IN (SELECT * FROM AUTORISATION)
	LOOP
		BEGIN
			dbms_output.put_line('loop');
			IF (id_groupeBat <> autorisation.id_groupebat) THEN
				id_groupeBat := autorisation.id_groupebat;
				
				requete := 'SELECT MIN(PS.SEMAINE_DEBUT) FROM AUTORISATION A,PLAGE P,PLAGE_SEMAINE PS,GROUPE_BATIMENTS GB, GROUPE G WHERE A.ID_GROUPEBAT= ' || id_groupeBat || ' and A.ID_GROUPEBAT=GB.ID_GROUPEBAT and A.ID_GROUPEPERS=G.ID_GROUPEPERS and GB.NOMGROUPEBAT=G.NOMGROUPEPERS and A.LIBELLE_PLAGE_ACCES = P.LIBELLE_PLAGE_ACCES and P.LIBELLE_PLAGE_SEMAINE = PS.LIBELLE_PLAGE_SEMAINE' ;
				EXECUTE IMMEDIATE (requete) into metasemaineDebut;

				requete := 'SELECT MAX(PS.SEMAINE_FIN) FROM AUTORISATION A,PLAGE P,PLAGE_SEMAINE PS,GROUPE_BATIMENTS GB, GROUPE G WHERE A.ID_GROUPEBAT=' || id_groupeBat || ' and A.ID_GROUPEBAT=GB.ID_GROUPEBAT and A.ID_GROUPEPERS=G.ID_GROUPEPERS and GB.NOMGROUPEBAT=G.NOMGROUPEPERS and A.LIBELLE_PLAGE_ACCES = P.LIBELLE_PLAGE_ACCES and P.LIBELLE_PLAGE_SEMAINE = PS.LIBELLE_PLAGE_SEMAINE' ;
				EXECUTE IMMEDIATE (requete) into metasemaineFin;

				requete := 'SELECT MIN(PH.HORAIRE_DEBUT) FROM AUTORISATION A, PERIODE_ACCES PA, PLAGE_HORAIRE PH,GROUPE_BATIMENTS GB, GROUPE G WHERE A.ID_GROUPEBAT=' || id_groupeBat || 'and A.ID_GROUPEBAT=GB.ID_GROUPEBAT and A.ID_GROUPEPERS=G.ID_GROUPEPERS and GB.NOMGROUPEBAT=G.NOMGROUPEPERS and A.LIBELLE_PLAGE_ACCES = PA.LIBELLE_PLAGE_ACCES and PA.LIBELLE_PLAGE_HORAIRE= PH.LIBELLE_PLAGE_HORAIRE' ;
				EXECUTE IMMEDIATE (requete) into metahoraireDebut;

				requete := 'SELECT MAX(PH.HORAIRE_FIN) FROM AUTORISATION A, PERIODE_ACCES PA, PLAGE_HORAIRE PH,GROUPE_BATIMENTS GB, GROUPE G WHERE A.ID_GROUPEBAT=' || id_groupeBat || ' and A.ID_GROUPEBAT=GB.ID_GROUPEBAT and A.ID_GROUPEPERS=G.ID_GROUPEPERS and GB.NOMGROUPEBAT=G.NOMGROUPEPERS and A.LIBELLE_PLAGE_ACCES = PA.LIBELLE_PLAGE_ACCES and PA.LIBELLE_PLAGE_HORAIRE= PH.LIBELLE_PLAGE_HORAIRE' ;
				EXECUTE IMMEDIATE (requete) into metahoraireFin;
			END IF;
		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				metasemaineDebut := 52;
				metasemaineFin := 1;
				metahoraireDebut := to_date('01/01/2000 23:59:59','DD/MM/YYYY HH24:MI:SS');
				metahoraireFin := to_date('01/01/2000 00:00:00','DD/MM/YYYY HH24:MI:SS');
				dbms_output.put_line('no data found');
		END;
		BEGIN
			requete := 'SELECT MIN(PS.SEMAINE_DEBUT) FROM AUTORISATION A,PLAGE P,PLAGE_SEMAINE PS WHERE A.ID_GROUPEPERS =' || autorisation.ID_GROUPEPERS || ' and A.LIBELLE_PLAGE_ACCES = P.LIBELLE_PLAGE_ACCES and P.LIBELLE_PLAGE_SEMAINE = PS.LIBELLE_PLAGE_SEMAINE';
			EXECUTE IMMEDIATE (requete) into semaineDebut;

			requete := 'SELECT MAX(PS.SEMAINE_FIN) FROM AUTORISATION A,PLAGE P,PLAGE_SEMAINE PS WHERE A.ID_GROUPEPERS =' || autorisation.ID_GROUPEPERS || ' and A.LIBELLE_PLAGE_ACCES = P.LIBELLE_PLAGE_ACCES and P.LIBELLE_PLAGE_SEMAINE = PS.LIBELLE_PLAGE_SEMAINE';
			EXECUTE IMMEDIATE (requete) into semaineFin;

			requete := 'SELECT MIN(PH.HORAIRE_DEBUT) FROM AUTORISATION A,PLAGE P, PERIODE_ACCES PA, PLAGE_HORAIRE PH WHERE A.ID_GROUPEPERS =' || autorisation.ID_GROUPEPERS || ' and A.LIBELLE_PLAGE_ACCES = PA.LIBELLE_PLAGE_ACCES and PA.LIBELLE_PLAGE_HORAIRE= PH.LIBELLE_PLAGE_HORAIRE';
			EXECUTE IMMEDIATE (requete) into horaireDebut;

			requete := 'SELECT MAX(PH.HORAIRE_FIN) FROM AUTORISATION A,PLAGE P, PERIODE_ACCES PA, PLAGE_HORAIRE PH WHERE A.ID_GROUPEPERS =' || autorisation.ID_GROUPEPERS || ' and A.LIBELLE_PLAGE_ACCES = PA.LIBELLE_PLAGE_ACCES and PA.LIBELLE_PLAGE_HORAIRE= PH.LIBELLE_PLAGE_HORAIRE';
			EXECUTE IMMEDIATE (requete) into horaireFin;
		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				--Cette situation implique que la ligne que l'on vérifie n'est pas coherente avec le reste de la base
				semaineDebut := metasemaineDebut-1;
				semaineFin := metasemaineFin+1;
				horaireDebut := metahoraireDebut-1;
				horaireFin := metahoraireFin+1;
		END;
		
		dbms_output.put_line('if');
		IF (semaineDebut < metasemaineDebut or semaineFin > metasemaineFin or horaireDebut < metahoraireDebut or horaireFin > metahoraireFin) THEN
			--delete from AUTORISATION where ID_GROUPEBAT=autorisation.ID_GROUPEBAT AND ID_GROUPEPERS=autorisation.ID_GROUPEPERS AND LIBELLE_PLAGE_ACCES=autorisation.LIBELLE_PLAGE_ACCES;
			RAISE_APPLICATION_ERROR(-20007,'Les autorisations du groupe ' || autorisation.ID_GROUPEPERS || ' ne sont pas incluses dans les autorisations generales du groupe de batiment ' || autorisation.ID_GROUPEBAT);
		END IF;
	END LOOP;
END;
/
--pour tester après peuplement
-- insert into plage_horaire values('faux',to_date('01-01-2000 00:01:00','DD-MM-YYYY HH24:MI:SS'),to_date('01-01-2000 23:59:00','DD-MM-YYYY HH24:MI:SS'));
-- insert into periode_acces values('Accesfaux','faux','faux','vrai');
-- insert into plage values('semestre1','Accesfaux');
-- insert into autorisation values(2,2,'Accesfaux');
-- SELECT * FROM AUTORISATION A, GROUPE_BATIMENTS GB, GROUPE G WHERE A.ID_GROUPEBAT=GB.ID_GROUPEBAT AND A.ID_GROUPEPERS=G.ID_GROUPEPERS AND GB.NOMGROUPEBAT=G.NOMGROUPEPERS;


/* TRIGGER 14 :Un groupe de personne ne peut être supprimé
uniquement si le groupe est vide. 
CREATE OR REPLACE TRIGGER TR_verifGroupeVide
BEFORE DELETE ON GROUPE
FOR EACH ROW
DECLARE
	nbMembre integer;
BEGIN
	SELECT count(*) INTO nbMembre FROM MEMBRE WHERE IDGROUPE_PERS = :OLD.IDGROUPE_PERS;
	IF(nbMembre > 0) THEN
		RAISE_APPLICATION_ERROR(-20014, "Des personnes sont toujours membres de ce groupe");
	END IF;
END;
/ --> TRIGGER inutile: Verification dans code obligatoire*/

/* Fin Triggers Cyril */


/* Triggers Rémi */

/* TRIGGER 2 : Chaque bâtiment à une vocation unique: L’ensemble des salles du bâtiment sont de même type */
CREATE OR REPLACE TRIGGER TR_typeSalle
AFTER UPDATE OR INSERT ON SALLE
DECLARE
	codeBatiment varchar(7);
	typeSalle varchar(7);
BEGIN
	codeBatiment := ' ';
	FOR salle IN (SELECT * FROM SALLE ORDER BY CODE_BATIMENT)
	LOOP 
		IF (codeBatiment != salle.CODE_BATIMENT) THEN
			codeBatiment := salle.CODE_BATIMENT;
			typeSalle := salle.TYPE_SALLE;
		END IF;
		
		IF ((codeBatiment = salle.CODE_BATIMENT) AND (typeSalle != salle.TYPE_SALLE)) THEN
			RAISE_APPLICATION_ERROR(-20002,'Chaque bâtiment à une vocation unique: L’ensemble des salles du bâtiment sont de même type');
		END IF;
	END LOOP;
END;
/

/* TRIGGER 10 : Un groupe de bâtiment est associé à un méta-groupe portant le même nom (modéliser sous forme de groupe de personnes).
CREATE OR REPLACE TRIGGER TR_groupeBatEqualsGroupePers
AFTER INSERT ON GROUPE_BATIMENTS
FOR EACH ROW
DECLARE 
	nbGroupePers INTEGER;
BEGIN
	nbGroupePers := 0;
	SELECT count(*) INTO nbGroupePers FROM GROUPE WHERE NOMGROUPEPERS = :NEW.NOMGROUPEBAT;
	IF(nbGroupePers=0) THEN
		RAISE_APPLICATION_ERROR(-20010,'Un batiment doit avoir un groupe de personnes associé');
	END IF;
END;
/
*/


/* TRIGGER 13 : Lors de la suppression d’un groupe de bâtiment, on supprime toutes les autorisations et les réservations relatives au groupe */
CREATE OR REPLACE TRIGGER TR_deleteGroupeBat
BEFORE DELETE ON GROUPE_BATIMENTS
FOR EACH ROW
BEGIN
	DELETE FROM AUTORISATION WHERE ID_GROUPEBAT=:OLD.ID_GROUPEBAT;
	DELETE FROM RESERVATION WHERE CODE_BATIMENT IN (SELECT CODE_BATIMENT FROM BATIMENT WHERE ID_GROUPEBAT=:OLD.ID_GROUPEBAT);
	UPDATE BATIMENT SET ID_GROUPEBAT = null WHERE ID_GROUPEBAT = :OLD.ID_GROUPEBAT;
END;
/

/* TRIGGER 15 : Si l’on supprime une salle, on doit reloger les réservations dans d’autres salles si possible, et sinon on les supprime.*/


/* TRIGGER 16 : On ne peut modifier l’appartenance d’un bâtiment à un groupe de bâtiment que si l’ensemble des réservations relatives à ce
 bâtiment sont compatibles avec les contraintes d’autorisations du nouveau groupe de bâtiment. */

/* TRIGGER AUTRE : Suppression des rŽfŽrences de numacces*/
CREATE OR REPLACE TRIGGER TR_suppCascadeAcces
BEFORE DELETE ON ACCES
FOR EACH ROW
BEGIN
	DELETE FROM ENTREE WHERE NUMACCES = :OLD.NUMACCES;
	DELETE FROM SORTIE WHERE NUMACCES = :OLD.NUMACCES;
	DELETE FROM ALARME_ACCES WHERE NUMACCES = :OLD.NUMACCES;
END;
/



/* Fin Triggers Rémi */