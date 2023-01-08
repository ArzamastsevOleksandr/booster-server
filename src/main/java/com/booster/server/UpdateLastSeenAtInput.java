package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
public class UpdateLastSeenAtInput {
    private List<Long> ids;
    private LocalDateTime localDateTime;
}
