package com.booster.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
// TODO: research fluent+Jackson internals and why isCorrect isn`t mapped
@Accessors(chain = true, fluent = true)
@Getter(onMethod = @__(@JsonProperty))
public class UpdateCorrectAnswersCountInput {
    private Long id;
    private Boolean correct;
}
