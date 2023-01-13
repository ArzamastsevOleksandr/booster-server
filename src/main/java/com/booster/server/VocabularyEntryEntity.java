package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Table(name = "vocabulary_entry")
@Entity
@Setter
@Getter
@Accessors(chain = true)
public class VocabularyEntryEntity {

    @Id
    @SequenceGenerator(name = "vocabulary_entry_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vocabulary_entry_id_sequence")
    private Long id;

    @ManyToOne
    private WordEntity word;
    private String description;
    private Integer correctAnswersCount = 0;
    private LocalDateTime lastSeenAt = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VocabularyEntryEntity that = (VocabularyEntryEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
