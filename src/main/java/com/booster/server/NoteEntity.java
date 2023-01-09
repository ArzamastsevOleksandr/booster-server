package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Table(name = "note")
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class NoteEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String content;
    private LocalDateTime lastSeenAt = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NoteEntity that = (NoteEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
