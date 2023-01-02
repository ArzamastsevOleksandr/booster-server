package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CreateVocabularyEntryInput {
    private String name;
    private String description;
}
