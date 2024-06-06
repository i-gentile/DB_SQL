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
	phone VARCHAR(255),
	address VARCHAR(255) NOT NULL,
	num_visits SMALLINT NOT NULL DEFAULT 0,
	num_doses SMALLINT NOT NULL DEFAULT 0,
	CONSTRAINT PK_OWNER PRIMARY KEY (id_owner));

CREATE TABLE DOG (
	id_dog SMALLINT NOT NULL,
	name_dog VARCHAR(255) NOT NULL,
	breed VARCHAR(255) NOT NULL,
	birth DATE NOT NULL,
	death DATE,
	sex VARCHAR(255) NOT NULL,
	color VARCHAR(255) NOT NULL,
	fur VARCHAR (255) NOT NULL,
	id_owner SMALLINT NOT NULL,
	CONSTRAINT PK_DOG PRIMARY KEY (id_dog),
	CONSTRAINT CHECK_DEATH CHECK (death IS NULL OR birth < death),
	CONSTRAINT FK_OWNER_DOG FOREIGN KEY (id_owner) REFERENCES OWNER(id_owner) ON UPDATE CASCADE,
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
	CONSTRAINT reason CHECK (reason IN ('vaccination', 'follow-up', 'illness')));

CREATE TABLE PRESCRIPTION (
	id_visit SMALLINT NOT NULL,
	id_drug SMALLINT NOT NULL,
	dose SMALLINT NOT NULL,
	duration SMALLINT NOT NULL CHECK( duration > 0),
	CONSTRAINT PK_PRESCRIPTION PRIMARY KEY (id_visit, id_drug),
	CONSTRAINT FK_VISIT_PRESCRIPTION FOREIGN KEY(id_visit) REFERENCES VISIT(id_visit) ON UPDATE CASCADE,
	CONSTRAINT FK_DRUG_PRESCRIPTION FOREIGN KEY(id_drug) REFERENCES DRUG(id_drug) ON UPDATE CASCADE,
	CONSTRAINT CHECK_DOSE CHECK (dose IN (1, 2, 3)));

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
	CONSTRAINT FK_TEST_DOG_TEST FOREIGN KEY(id_test) REFERENCES TEST(id_test) ON UPDATE CASCADE,
	CONSTRAINT FK_VISIT_DOG_TEST FOREIGN KEY(id_visit) REFERENCES VISIT(id_visit) ON UPDATE CASCADE);

CREATE TABLE BANK_ACCOUNT (
	id_account SERIAL NOT NULL,
	name_owner VARCHAR(255) NOT NULL,
	iban VARCHAR(34) UNIQUE NOT NULL,
	priority SMALLINT NOT NULL,
	id_owner SMALLINT NOT NULL,
	CONSTRAINT PK_ACCOUNT PRIMARY KEY (id_account),
	CONSTRAINT FK_OWNER_ACCOUNT FOREIGN KEY (id_owner) REFERENCES OWNER(id_owner) ON UPDATE CASCADE,
	CONSTRAINT CHECK_PRIORITY CHECK (priority IN (1, 2, 3, 4, 5)),
	CONSTRAINT CHECK_IBAN_FORMAT CHECK (iban ~* '^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$'));

CREATE TABLE REPORT_DRUG (
	id_drug SMALLINT, 
	name_drug VARCHAR(255),
	format VARCHAR(255),
	"type" VARCHAR(255),
	doses INTEGER,
	alive_dogs INTEGER,
	visits INTEGER,
	main_reason VARCHAR(255),
	owners INTEGER,
	id_owner SMALLINT,
	name_owner VARCHAR(255),
	CONSTRAINT PK_REPORT_DRUG PRIMARY KEY(id_drug));

CREATE TYPE REPORT_DRUG_TYPE AS (
	t_id_drug SMALLINT, 
	t_name_drug VARCHAR(255),
	t_format VARCHAR(255),
	t_type VARCHAR(255),
	t_doses INTEGER,
	t_alive_dogs INTEGER,
	t_visits INTEGER,
	t_main_reason VARCHAR(255),
	t_owners INTEGER,
	t_id_owner SMALLINT,
	t_name_owner VARCHAR(255));
	
COMMIT;