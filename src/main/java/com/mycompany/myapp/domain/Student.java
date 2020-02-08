package com.mycompany.myapp.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Student.
 */
@Entity
@Table(name = "student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model")
    private String model;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "index_no")
    private String indexNo;

    @ManyToMany
    @JoinTable(name = "student_tutor",
               joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "tutor_id", referencedColumnName = "id"))
    private Set<Tutor> tutors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public Student model(String model) {
        this.model = model;
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLastName() {
        return lastName;
    }

    public Student lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public Student indexNo(String indexNo) {
        this.indexNo = indexNo;
        return this;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public Set<Tutor> getTutors() {
        return tutors;
    }

    public Student tutors(Set<Tutor> tutors) {
        this.tutors = tutors;
        return this;
    }

    public Student addTutor(Tutor tutor) {
        this.tutors.add(tutor);
        tutor.getStudents().add(this);
        return this;
    }

    public Student removeTutor(Tutor tutor) {
        this.tutors.remove(tutor);
        tutor.getStudents().remove(this);
        return this;
    }

    public void setTutors(Set<Tutor> tutors) {
        this.tutors = tutors;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        return id != null && id.equals(((Student) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Student{" +
            "id=" + getId() +
            ", model='" + getModel() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", indexNo='" + getIndexNo() + "'" +
            "}";
    }
}
