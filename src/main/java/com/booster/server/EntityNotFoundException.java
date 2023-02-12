package com.booster.server;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final Class target;
    private final String attribute;
    private final Object attributeValue;

    public EntityNotFoundException(Class target, String attribute, Object attributeValue) {
        super("Entity not found [type=%s, notFoundBy=%s, attributeValue=%s]"
                .formatted(target.getSimpleName(), attribute, attributeValue));
        this.target = target;
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

}
