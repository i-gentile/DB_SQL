SET search_path TO ubd_20232;
SET datestyle = DMY;

CREATE VIEW INVOICES AS
SELECT b.iban, b.name_owner, v.date, d.name_dog, vc.name_vaccine, vc.price, v.comments
FROM VISIT v 
JOIN VACCINATION vv ON vv.id_visit = v.id_visit 
JOIN VACCINE vc ON vc.id_vaccine = vv.id_vaccine
JOIN DOG d ON d.id_dog = v.id_dog 
JOIN BANK_ACCOUNT b ON b.id_owner = d.id_owner 
where EXTRACT('Year' FROM date) = EXTRACT('Year' FROM CURRENT_DATE) and b.id_account in
(select b2.id_account from BANK_ACCOUNT b2
where b2.id_owner = d.id_owner ORDER BY PRIORITY LIMIT 1) ORDER BY v.date DESC;