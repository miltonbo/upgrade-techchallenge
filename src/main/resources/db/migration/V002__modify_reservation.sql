DELIMITER $$
CREATE PROCEDURE MODIFY_RESERVATION(IN arrival_date_in INT, IN departure_date_in INT, IN active_status_in SMALLINT(2), IN reservation_id_in BIGINT(20), OUT success smallint(2))
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

    SELECT count(*) INTO reservations FROM reservation WHERE arrival_date < departure_date_in AND arrival_date_in < departure_date AND id != reservation_id_in AND status = active_status_in;
    IF(reservations = 0) THEN
        UPDATE reservation SET arrival_date = arrival_date_in, departure_date = departure_date_in, modified_at = CURDATE() WHERE id = reservation_id_in;
        SET success = 0;

        ALTER TABLE reservation ENABLE KEYS;
        COMMIT;
    ELSE
        SET success = 1;

        ALTER TABLE reservation ENABLE KEYS;
        ROLLBACK;
    END IF;
END $$