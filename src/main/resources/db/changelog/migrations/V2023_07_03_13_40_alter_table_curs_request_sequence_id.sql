ALTER TABLE curs_request
    ALTER COLUMN id DROP DEFAULT,
    ALTER COLUMN id SET DEFAULT nextval('curs_request_seq');