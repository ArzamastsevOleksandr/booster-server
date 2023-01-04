package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class VocabularyEntryListDto {
    private List<VocabularyEntryDto> list;
}
