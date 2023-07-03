ALTER TABLE curs_request
    ADD IF NOT EXISTS request_date DATE,
    ADD IF NOT EXISTS correlation_id varchar,
    ADD IF NOT EXISTS status_id bigint,
    ADD CONSTRAINT fk_status
        FOREIGN KEY (status_id) REFERENCES status (id);