-- todo: benchmark the sequence cache value
CREATE SEQUENCE note_id_sequence
    CACHE 50;

CREATE TABLE note
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('note_id_sequence'),
    content      TEXT,
    last_seen_at TIMESTAMP
);

ALTER SEQUENCE note_id_sequence OWNED BY note.id;


CREATE SEQUENCE vocabulary_entry_id_sequence
    CACHE 50;

CREATE TABLE vocabulary_entry
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('vocabulary_entry_id_sequence'),
    name         TEXT NOT NULL,
    description  TEXT,
    last_seen_at TIMESTAMP
);

ALTER SEQUENCE vocabulary_entry_id_sequence OWNED BY vocabulary_entry.id;
