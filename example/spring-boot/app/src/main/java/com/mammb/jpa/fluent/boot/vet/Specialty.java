package com.mammb.jpa.fluent.boot.vet;

import com.mammb.jpa.fluent.boot.model.NamedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "specialties")
public class Specialty extends NamedEntity {

}