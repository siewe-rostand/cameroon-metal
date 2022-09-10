package com.siewe.pos.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
}
