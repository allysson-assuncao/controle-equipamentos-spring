package org.example.controleequipamentosspring.service;

import org.example.controleequipamentosspring.model.Equipment;
import org.example.controleequipamentosspring.model.User;
import org.example.controleequipamentosspring.repository.EquipmentRepository;
import org.example.controleequipamentosspring.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepo;
    private final UserRepository userRepo;

    public EquipmentService(EquipmentRepository equipmentRepo, UserRepository userRepo) {
        this.equipmentRepo = equipmentRepo;
        this.userRepo = userRepo;
    }

    public List<Equipment> findAll() {
        return equipmentRepo.findAll();
    }

    @Transactional
    public Equipment reserve(Long equipmentId, Long userId, int minutes) {
        Equipment equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (equipment.isOccupied()) {
            throw new RuntimeException("Equipamento já está ocupado!");
        }

        equipment.setOccupied(true);
        equipment.setBookedBy(user);
        equipment.setReservedUntil(LocalDateTime.now().plusMinutes(minutes));
        equipment.setTotalUses(equipment.getTotalUses() + 1);

        return equipmentRepo.save(equipment);
    }

    @Transactional
    public void release(Long equipmentId) {
        Equipment equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        // Zera o estado
        equipment.setOccupied(false);
        equipment.setBookedBy(null);
        equipment.setReservedUntil(null);

        equipmentRepo.save(equipment);
    }

    // Roda automaticamente a cada 10 segundos para verificar expirações.
    // Ocorre no servidor, independentemente de ter usuários online ou não.
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkExpirations() {
        List<Equipment> expired = equipmentRepo.findByReservedUntilBeforeAndOccupiedTrue(LocalDateTime.now());
        for (Equipment e : expired) {
            System.out.println("Auto-liberando equipamento: " + e.getNome());
            e.setOccupied(false);
            e.setBookedBy(null);
            e.setReservedUntil(null);
            equipmentRepo.save(e);
        }
    }
}
