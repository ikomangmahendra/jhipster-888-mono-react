package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A Provider.
 */
@Entity
@Table(name = "provider")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Provider implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "provider")
    private String provider;

    @Column(name = "abn")
    private String abn;

    @Column(name = "contact_person")
    private String contactPerson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Provider id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvider() {
        return this.provider;
    }

    public Provider provider(String provider) {
        this.setProvider(provider);
        return this;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAbn() {
        return this.abn;
    }

    public Provider abn(String abn) {
        this.setAbn(abn);
        return this;
    }

    public void setAbn(String abn) {
        this.abn = abn;
    }

    public String getContactPerson() {
        return this.contactPerson;
    }

    public Provider contactPerson(String contactPerson) {
        this.setContactPerson(contactPerson);
        return this;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Provider)) {
            return false;
        }
        return getId() != null && getId().equals(((Provider) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Provider{" +
            "id=" + getId() +
            ", provider='" + getProvider() + "'" +
            ", abn='" + getAbn() + "'" +
            ", contactPerson='" + getContactPerson() + "'" +
            "}";
    }
}
