package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class VocabularyEntryDto {
    private Long id;
    private String name;
    private String description;
    private Set<String> synonyms;
}
