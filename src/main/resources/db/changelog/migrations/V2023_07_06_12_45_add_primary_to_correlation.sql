DO
$$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'curs_request_correlation_id_key'
              AND conrelid = 'curs_request'::regclass
        ) THEN
            EXECUTE 'ALTER TABLE curs_request ADD CONSTRAINT curs_request_correlation_id_key PRIMARY KEY (correlation_id)';
        END IF;
    END;
$$;