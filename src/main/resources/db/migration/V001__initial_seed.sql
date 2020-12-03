DROP TABLE IF EXISTS reservation;

CREATE TABLE person (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  external_id VARCHAR(48) NOT NULL DEFAULT UUID(),
  email VARCHAR(100) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE reservation (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  external_id VARCHAR(48) NOT NULL DEFAULT UUID(),
  arrival_date INT NOT NULL,
  departure_date INT NOT NULL,
  status smallint(2) NOT NULL,
  modified_at DATETIME,
  cancelled_at DATETIME,
  person_id BIGINT(20) NOT NULL,
  CONSTRAINT person_fk FOREIGN KEY (person_id) REFERENCES person (id),
  PRIMARY KEY (id)
);

DELIMITER $$
CREATE PROCEDURE REGISTER_RESERVATION(IN arrival_date_in INT, IN departure_date_in INT, IN person_id_in BIGINT(20), IN active_status_in SMALLINT(2), OUT reservation_id BIGINT(20))
BEGIN
    DECLARE reservations INT;

    DECLARE exit handler for sqlexception
    BEGIN
         -- ERROR
      ALTER TABLE reservation ENABLE KEYS;
      ROLLBACK;
    END;

    DECLARE exit handler for sqlwarning
      BEGIN
         -- WARNING
      ALTER TABLE reservation ENABLE KEYS;
      ROLLBACK;
    END;

    START TRANSACTION;
    ALTER TABLE reservation DISABLE KEYS;

    SELECT count(*) INTO reservations FROM reservation WHERE arrival_date < departure_date_in AND arrival_date_in < departure_date AND status = active_status_in;
    IF(reservations = 0) THEN
        INSERT INTO reservation VALUES(null, UUID(), arrival_date_in, departure_date_in, active_status_in, null, null, person_id_in);
        SELECT LAST_INSERT_ID() INTO reservation_id;

        ALTER TABLE reservation ENABLE KEYS;
        COMMIT;
    ELSE
        SET reservation_id = 0;

        ALTER TABLE reservation ENABLE KEYS;
        ROLLBACK;
    END IF;
END $$