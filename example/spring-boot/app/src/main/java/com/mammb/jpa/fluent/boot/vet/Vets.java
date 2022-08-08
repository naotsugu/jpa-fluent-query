package com.mammb.jpa.fluent.boot.vet;

import java.util.ArrayList;
import java.util.List;

//@XmlRootElement
public class Vets {

    private List<Vet> vets;

    //@XmlElement
    public List<Vet> getVetList() {
        if (vets == null) {
            vets = new ArrayList<>();
        }
        return vets;
    }

}