package com.mammb.jpa.fluent.boot.owner;

import com.mammb.jpa.fluent.boot.model.NamedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "types")
public class PetType extends NamedEntity {

}
