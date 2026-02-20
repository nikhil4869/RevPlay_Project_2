package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "APP_ROLE")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }
}
