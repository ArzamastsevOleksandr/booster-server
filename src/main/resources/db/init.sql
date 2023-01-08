CREATE TABLE note
(
    id      BIGINT PRIMARY KEY,
    content TEXT
);

CREATE SEQUENCE note_id_sequence
    CACHE 50
    OWNED BY note.id;

CREATE TABLE vocabulary_entry
(
    id          BIGINT PRIMARY KEY,
    name        text NOT NULL,
    description TEXT
);

CREATE SEQUENCE vocabulary_entry_id_sequence
    CACHE 50
    OWNED BY vocabulary_entry.id;
