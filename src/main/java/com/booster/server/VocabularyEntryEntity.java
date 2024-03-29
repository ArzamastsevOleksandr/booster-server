package com.booster.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Table(name = "vocabulary_entry")
@Entity
@Setter
@Getter
@Accessors(chain = true)
public class VocabularyEntryEntity {

    @Id
    @SequenceGenerator(name = "vocabulary_entry__id__sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vocabulary_entry__id__sequence")
    private Long id;

    @ManyToOne
    private WordEntity word;
    private String description;
    private Integer correctAnswersCount = 0;
    private LocalDateTime lastSeenAt = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN);

    @ManyToMany
    @JoinTable(name = "vocabulary_entry_synonym",
            joinColumns = @JoinColumn(name = "vocabulary_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    private Set<WordEntity> synonyms = new HashSet<>();

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
