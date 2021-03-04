package org.miage.intervenantservice.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor // Obligatoire pour JPA !
public class Intervenant implements Serializable {
    
    private static final long serialVersionUID = 8765432L;

    @Id
    private String id;

    private String nom;
    private String prenom;
    private String commune;
    private String codepostal;
}
