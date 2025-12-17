package org.example.controleequipamentosspring.repository;

import org.example.controleequipamentosspring.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    // Busca equipamentos cujo tempo de reserva já passou E estão marcados como ocupados
    List<Equipment> findByReservedUntilBeforeAndOccupiedTrue(java.time.LocalDateTime now);

    // Conta quantas reservas ativas um usuário tem
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.bookedBy.id = :userId AND e.occupied = true")
    long countActiveReservations(Long userId);
}
