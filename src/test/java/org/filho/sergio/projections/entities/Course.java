package org.filho.sergio.projections.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import java.util.UUID;

@Data
@Entity
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private Long views;

    @Column(nullable = false, unique = true, length = 42)
    private String uuid;

    @Column
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @PrePersist
    public void beforeSave() {
        uuid = UUID.randomUUID().toString();
    }

}
