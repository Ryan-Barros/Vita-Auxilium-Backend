package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedUuidV7
    @Column(name = "id_categoria", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name_categoria", nullable = false, unique = true)
    private String nameCategoria;
}
