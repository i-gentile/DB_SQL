DROP SCHEMA IF EXISTS ubd_20232 CASCADE;

CREATE SCHEMA ubd_20232 AUTHORIZATION postgres;

GRANT ALL ON SCHEMA ubd_20232 TO postgres;

SET search_path TO ubd_20232;

BEGIN WORK;

SET TRANSACTION READ WRITE;

SET datestyle = DMY;

CREATE TABLE OWNER (
	id_owner SMALLINT NOT NULL,
	name_owner VARCHAR(255) NOT NULL,
	phone VARCHAR(255) NOT NULL,
	address VARCHAR(255) NOT NULL,
	CONSTRAINT PK_OWNER PRIMARY KEY (id_owner));

CREATE TABLE DOG (
	id_dog SMALLINT NOT NULL,
	name_dog VARCHAR(255) NOT NULL,
	breed VARCHAR(255) NOT NULL,
	birth DATE,
	death DATE,
	sex VARCHAR(255) NOT NULL,
	color VARCHAR(255) NOT NULL,
	fur VARCHAR (255) NOT NULL,
	id_owner SMALLINT NOT NULL,
	CONSTRAINT PK_DOG PRIMARY KEY (id_dog),
	CONSTRAINT FK_OWNER_DOG FOREIGN KEY (id_owner) REFERENCES OWNER(id_owner) ON UPDATE CASCADE,
	CONSTRAINT CHECK_DEATH CHECK (death IS NULL OR birth < death),
	CONSTRAINT sex CHECK (sex IN ('M','F')),
	CONSTRAINT fur CHECK (fur IN ('Short', 'Medium', 'Long')));

CREATE TABLE DRUG (
	id_drug SMALLINT NOT NULL,
	name_drug VARCHAR(255) UNIQUE NOT NULL,
	format VARCHAR(255) NOT NULL,
	type VARCHAR(255) NOT NULL,
	ean_code BIGINT UNIQUE NOT NULL,
	CONSTRAINT PK_DRUG PRIMARY KEY(id_drug));
	
CREATE TABLE LABORATORY (
	id_laboratory SMALLINT NOT NULL,
	name_laboratory VARCHAR(255) NOT NULL,
	address VARCHAR(255) NOT NULL,
	phone VARCHAR(255) NOT NULL,
	num_employees VARCHAR(255) NOT NULL,
	production_capacity INT,
	country VARCHAR(255),
	CONSTRAINT PK_LABORATORY PRIMARY KEY(id_laboratory),
	CONSTRAINT num_employees CHECK (num_employees IN ('1-500', '501-1000', '+1000')),
	CONSTRAINT production_capacity CHECK (production_capacity BETWEEN 100 AND 10000));

CREATE TABLE VACCINE (
	id_vaccine SMALLINT NOT NULL,
	name_vaccine VARCHAR(255) UNIQUE NOT NULL,
	periodicity SMALLINT DEFAULT NULL,
	id_laboratory INT NOT NULL,
	price DECIMAL(4,2) NOT NULL DEFAULT 0.00,
	CONSTRAINT PK_VACCINE PRIMARY KEY(id_vaccine),
	CONSTRAINT FK_LABORATORY FOREIGN KEY (id_laboratory) REFERENCES LABORATORY(id_laboratory) ON UPDATE CASCADE);

CREATE TABLE TEST(
	id_test SMALLINT NOT NULL,
	type_test VARCHAR(255) NOT NULL,
	CONSTRAINT PK_TEST PRIMARY KEY(id_test));

CREATE TABLE VISIT(
	id_visit SMALLINT NOT NULL,
	id_dog SMALLINT NOT NULL,
	date DATE NOT NULL,
	reason VARCHAR(255) NOT NULL,
	id_veterinary SMALLINT NOT NULL,
	comments VARCHAR(255),
	CONSTRAINT PK_VISIT PRIMARY KEY(id_visit),
	CONSTRAINT FK_DOG_VISIT FOREIGN KEY (id_dog) REFERENCES DOG(id_dog) ON UPDATE CASCADE,
	CONSTRAINT CHECK_REASON CHECK (reason IN ('vaccination', 'follow-up', 'illness')));

CREATE TABLE PRESCRIPTION (
	id_visit SMALLINT NOT NULL,
	id_drug SMALLINT NOT NULL,
	dose SMALLINT NOT NULL,
	duration SMALLINT NOT NULL,
	CONSTRAINT PK_PRESCRIPTION PRIMARY KEY (id_visit, id_drug),
	CONSTRAINT FK_VISIT_PRESCRIPTION FOREIGN KEY(id_visit) REFERENCES VISIT(id_visit) ON UPDATE CASCADE,
	CONSTRAINT FK_DRUG_PRESCRIPTION FOREIGN KEY(id_drug) REFERENCES DRUG(id_drug) ON UPDATE CASCADE,
	CONSTRAINT CHECK_DOSE CHECK (dose IN (1, 2, 3)),
	CONSTRAINT CHECK_DURATION CHECK (duration > 0));

CREATE TABLE VACCINATION (
	id_visit SMALLINT NOT NULL,
	id_vaccine SMALLINT NOT NULL,
	CONSTRAINT PK_VACCINATION PRIMARY KEY(id_visit, id_vaccine),
	CONSTRAINT FK_VISIT_VACCINATION FOREIGN KEY(id_visit) REFERENCES VISIT(id_visit) ON UPDATE CASCADE,
	CONSTRAINT FK_VACCINE_VACCINATION FOREIGN KEY(id_vaccine) REFERENCES VACCINE(id_vaccine) ON UPDATE CASCADE);

CREATE TABLE DOG_TEST (
	id_test SMALLINT NOT NULL,
	id_visit SMALLINT NOT NULL,
	CONSTRAINT PK_DOG_TEST PRIMARY KEY(id_test, id_visit),
	CONSTRAINT FK_VISIT FOREIGN KEY(id_visit) REFERENCES VISIT(id_visit) ON UPDATE CASCADE,
	CONSTRAINT FK_TEST_DOG_TEST FOREIGN KEY(id_test) REFERENCES TEST(id_test) ON UPDATE CASCADE);
		
COMMIT;

BEGIN WORK;

-- Ex. 1
CREATE TABLE BANK_ACCOUNT (
    id_account SMALLINT NOT NULL, 
    name_owner VARCHAR (255) NOT NULL,
    iban VARCHAR(34) UNIQUE NOT NULL, 
    priority INT NOT NULL,
    id_owner SMALLINT NOT NULL,
    CONSTRAINT PK_BANK_ACCOUNT PRIMARY KEY (id_account),
    CONSTRAINT iban CHECK (iban ~ '^[A-Z]{2}\d{2}[A-Z0-9]{1,30}$'),
    CONSTRAINT priority CHECK (priority BETWEEN 1 AND 5),
    CONSTRAINT FK_OWNER_DOG FOREIGN KEY (id_owner) REFERENCES OWNER (id_owner) ON UPDATE CASCADE
);

--2.1
ALTER TABLE DOG
ALTER COLUMN birth SET NOT NULL,
ADD CONSTRAINT birth CHECK (birth <= CURRENT_DATE),
DROP CONSTRAINT CHECK_DEATH,
ADD CONSTRAINT CHECK_DEATH CHECK (((death IS NULL OR birth < death) AND death <= CURRENT_DATE));


--2.2
ALTER TABLE OWNER
ADD COLUMN email_owner VARCHAR (255),
ADD CONSTRAINT CHECK_EMAIL CHECK (email_owner IS NULL OR email_owner ~ '^[A-Za-z0-9._]+@[A-Za-z0-9]+\.[A-Za-z0-9]+$');


--2.3
ALTER TABLE PRESCRIPTION
ADD CONSTRAINT CHECK_MAX_DURATION CHECK (dose IN (1,2) OR duration <=8);


--2.4
ALTER TABLE LABORATORY
ADD CONSTRAINT CHECK_LAB_NAME UNIQUE (name_laboratory),
DROP COLUMN country;

COMMIT;

--EX. 2

--1
SELECT o.name_owner, d.color, COUNT(*) AS number_of_dogs
FROM OWNER o
JOIN DOG d ON o.id_owner = d.id_owner
GROUP BY o.id_owner, o.name_owner, d.color
HAVING COUNT(*) > 1
ORDER BY o.name_owner, d.color;

--2
SELECT o.name_owner, o.phone, d.name_dog, v.date
FROM OWNER o
JOIN DOG d ON o.id_owner = d.id_owner
JOIN VISIT v ON d.id_dog = v.id_dog
WHERE v.reason = 'follow-up' AND v.date = (
	SELECT MAX(v2.date)
	FROM VISIT v2
	WHERE v2.id_dog = d.id_dog AND v2.reason = 'follow-up'
	)
ORDER BY v.date DESC, o.name_owner;

--3
 CREATE OR REPLACE VIEW vaccines_january AS
SELECT o.name_owner, SUM(v.price) AS total_spent, ba.iban
FROM OWNER o
JOIN DOG d ON o.id_owner = d.id_owner
JOIN VISIT vi ON d.id_dog = vi.id_dog
JOIN VACCINATION va ON vi.id_visit = va.id_visit
JOIN VACCINE v ON va.id_vaccine = v.id_vaccine
JOIN BANK_ACCOUNT ba ON o.id_owner = ba.id_owner
WHERE EXTRACT(MONTH FROM vi.date) = 1 AND EXTRACT(YEAR FROM vi.date)
= 2024 AND ba.priority = 1
GROUP BY o.id_owner, o.name_owner, ba.iban
ORDER BY total_spent DESC;


--EX. 3
SELECT id_visit, comments
FROM VISIT
	WHERE id_visit IN (
	SELECT v.id_visit
	FROM VISIT v
	JOIN DOG_TEST dt ON v.id_visit = dt.id_visit
	JOIN TEST t ON dt.id_test = t.id_test
	WHERE t.type_test = 'Urinalysis'
	AND EXISTS (
		SELECT *
		FROM VISIT prev_v
		JOIN VACCINATION vac ON prev_v.id_visit = vac.id_visit
		JOIN VACCINE vaci ON vac.id_vaccine = vaci.id_vaccine
		JOIN LABORATORY lab ON vaci.id_laboratory = lab.id_laboratory
		WHERE v.id_dog = prev_v.id_dog AND prev_v.date < v.date AND
		name_laboratory = 'Animal Health'
	)
);


UPDATE VISIT
SET comments = 'Call urgently.'
WHERE id_visit IN (
	SELECT v.id_visit
	FROM VISIT v
	JOIN DOG_TEST dt ON v.id_visit = dt.id_visit
	JOIN TEST t ON dt.id_test = t.id_test
	WHERE t.type_test = 'Urinalysis'
	AND EXISTS (
		SELECT *
		FROM VISIT prev_v
		JOIN VACCINATION vac ON prev_v.id_visit = vac.id_visit
		JOIN VACCINE vaci ON vac.id_vaccine = vaci.id_vaccine
		JOIN LABORATORY lab ON vaci.id_laboratory = lab.id_laboratory
		WHERE v.id_dog = prev_v.id_dog AND prev_v.date < v.date AND
		name_laboratory = 'Animal Health'
	)
);