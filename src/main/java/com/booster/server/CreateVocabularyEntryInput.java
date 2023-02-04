package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class CreateVocabularyEntryInput {
    private String name;
    private String description;
    private Set<String> synonyms = new HashSet<>();
}
