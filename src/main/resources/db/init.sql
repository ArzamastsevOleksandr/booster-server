-- todo: benchmark the sequence cache value
CREATE SEQUENCE note_id_sequence CACHE 50;

CREATE TABLE note
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('note_id_sequence'),
    content      TEXT,
    last_seen_at TIMESTAMP
);

ALTER SEQUENCE note_id_sequence OWNED BY note.id;


CREATE SEQUENCE word_id_sequence CACHE 50;

CREATE TABLE word
(
    id   BIGINT PRIMARY KEY DEFAULT nextval('word_id_sequence'),
    name TEXT NOT NULL
);

ALTER SEQUENCE word_id_sequence OWNED BY word.id;

CREATE INDEX word_name_idx ON word (name);


CREATE SEQUENCE vocabulary_entry_id_sequence CACHE 50;

CREATE TABLE vocabulary_entry
(
    id                    BIGINT PRIMARY KEY DEFAULT nextval('vocabulary_entry_id_sequence'),
    word_id               BIGINT  NOT NULL,
    description           TEXT,
    correct_answers_count INTEGER NOT NULL   DEFAULT 0,
    last_seen_at          TIMESTAMP
);

ALTER SEQUENCE vocabulary_entry_id_sequence OWNED BY vocabulary_entry.id;

ALTER TABLE vocabulary_entry
    ADD CONSTRAINT vocabulary_entry__word_id__fk FOREIGN KEY (word_id) REFERENCES word (id);

ALTER TABLE vocabulary_entry
    ADD CONSTRAINT correct_answers_count_check CHECK (correct_answers_count >= 0);

CREATE TABLE vocabulary_entry_synonym
(
    vocabulary_entry_id BIGINT,
    word_id             BIGINT
);

ALTER TABLE vocabulary_entry_synonym
    ADD CONSTRAINT vocabulary_entry_synonym__pk PRIMARY KEY (vocabulary_entry_id, word_id);

ALTER TABLE vocabulary_entry_synonym
    ADD CONSTRAINT vocabulary_entry_id__vocabulary_entry__fk
        FOREIGN KEY (vocabulary_entry_id) REFERENCES vocabulary_entry (id);

ALTER TABLE vocabulary_entry_synonym
    ADD CONSTRAINT word_id__word__fk
        FOREIGN KEY (word_id) REFERENCES word (id);

