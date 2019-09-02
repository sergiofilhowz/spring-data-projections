package org.filho.sergio.projections.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 42)
    private String uuid;

    @Column
    private String name;

    @OneToMany(mappedBy = "author")
    private List<Course> courses;

    @PrePersist
    public void beforeSave() {
        uuid = UUID.randomUUID().toString();
    }

}
