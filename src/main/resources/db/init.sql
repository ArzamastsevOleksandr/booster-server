CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

CREATE SEQUENCE IF NOT EXISTS note__id__sequence CACHE 50 INCREMENT 50;

CREATE TABLE IF NOT EXISTS note
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('note__id__sequence'),
    content      TEXT,
    last_seen_at TIMESTAMP
);

ALTER SEQUENCE note__id__sequence OWNED BY note.id;

CREATE SEQUENCE IF NOT EXISTS word__id__sequence CACHE 50 INCREMENT 50;

CREATE TABLE IF NOT EXISTS word
(
    id   BIGINT PRIMARY KEY DEFAULT nextval('word__id__sequence'),
    name TEXT NOT NULL
);

ALTER SEQUENCE word__id__sequence OWNED BY word.id;

CREATE INDEX CONCURRENTLY IF NOT EXISTS word__name__idx ON word (name);

CREATE SEQUENCE IF NOT EXISTS vocabulary_entry__id__sequence CACHE 50 INCREMENT 50;

CREATE TABLE IF NOT EXISTS vocabulary_entry
(
    id                    BIGINT PRIMARY KEY DEFAULT nextval('vocabulary_entry__id__sequence'),
    word_id               BIGINT  NOT NULL,
    description           TEXT,
    correct_answers_count INTEGER NOT NULL   DEFAULT 0,
    last_seen_at          TIMESTAMP
);

ALTER SEQUENCE vocabulary_entry__id__sequence OWNED BY vocabulary_entry.id;

ALTER TABLE vocabulary_entry
    ADD CONSTRAINT vocabulary_entry__word_id__fk FOREIGN KEY (word_id) REFERENCES word (id);

ALTER TABLE vocabulary_entry
    ADD CONSTRAINT vocabulary_entry__correct_answers_count__positive_or_zero CHECK (correct_answers_count >= 0);

CREATE TABLE vocabulary_entry_synonym
(
    vocabulary_entry_id BIGINT,
    word_id             BIGINT
);

ALTER TABLE vocabulary_entry_synonym
    ADD CONSTRAINT vocabulary_entry_synonym__pk PRIMARY KEY (vocabulary_entry_id, word_id);

ALTER TABLE vocabulary_entry_synonym
    ADD CONSTRAINT vocabulary_entry_synonym__vocabulary_entry_id__fk
        FOREIGN KEY (vocabulary_entry_id) REFERENCES vocabulary_entry (id);

ALTER TABLE vocabulary_entry_synonym
    ADD CONSTRAINT vocabulary_entry_synonym__word_id__fk
        FOREIGN KEY (word_id) REFERENCES word (id);
