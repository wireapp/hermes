-- Enable generating UUIDs for older databases
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- for versions >= 13, we should use gen_random_uuid() as per https://www.postgresql.org/docs/current/functions-uuid.html

-- BEFORE insert function for trigger

CREATE OR REPLACE FUNCTION set_only_created() RETURNS TRIGGER
AS
$BODY$
BEGIN
    new.created := NOW();
    RETURN new;
END;
$BODY$
    LANGUAGE plpgsql;

-- BEFORE insert function for trigger

CREATE OR REPLACE FUNCTION set_created() RETURNS TRIGGER
AS
$BODY$
BEGIN
    new.created := NOW();
    new.updated := new.created;
    RETURN new;
END;
$BODY$
    LANGUAGE plpgsql;

-- BEFORE update function for trigger

CREATE OR REPLACE FUNCTION set_updated() RETURNS TRIGGER
AS
$BODY$
BEGIN
    new.updated := NOW();
    RETURN new;
END;
$BODY$
    LANGUAGE plpgsql;
