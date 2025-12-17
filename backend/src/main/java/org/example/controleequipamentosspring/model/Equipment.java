package org.example.controleequipamentosspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "equipments")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // true = ocupado, false = livre
    private boolean occupied = false;

    private LocalDateTime reservedUntil;

    private int totalUses = 0;

    @ManyToOne // Vários equipamentos podem ser reservados por um usuário (um de cada vez)
    @JoinColumn(name = "user_id")
    private User bookedBy;
}
