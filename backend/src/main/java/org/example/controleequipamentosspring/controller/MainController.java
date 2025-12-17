package org.example.controleequipamentosspring.controller;

import org.example.controleequipamentosspring.model.Equipment;
import org.example.controleequipamentosspring.model.User;
import org.example.controleequipamentosspring.repository.EquipmentRepository;
import org.example.controleequipamentosspring.repository.UserRepository;
import org.example.controleequipamentosspring.service.EquipmentService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite acesso de qualquer frontend (para facilitar teste local)
public class MainController {

    private final EquipmentService service;
    private final UserRepository userRepo;
    private final EquipmentRepository equipRepo;

    public MainController(EquipmentService service, UserRepository userRepo, EquipmentRepository equipRepo) {
        this.service = service;
        this.userRepo = userRepo;
        this.equipRepo = equipRepo;
    }

    // --- Equipamentos ---
    @GetMapping("/equipments")
    public List<Equipment> getAllEquipments() {
        return service.findAll();
    }

    @PostMapping("/equipments")
    public Equipment createEquipment(@RequestBody Equipment equip) {
        return equipRepo.save(equip);
    }

    @PostMapping("/equipments/{id}/reserve")
    public Equipment reserve(@PathVariable Long id, @RequestParam Long userId, @RequestParam int minutes) {
        return service.reserve(id, userId, minutes);
    }

    @PostMapping("/equipments/{id}/release")
    public void release(@PathVariable Long id) {
        service.release(id);
    }

    // --- Usuários ---
    @PostMapping("/users/login")
    public User loginOrRegister(@RequestBody Map<String, String> payload) {
        String nome = payload.get("nome");
        String senha = payload.get("senha");

        // Lógica simplificada: acha ou cria
        return userRepo.findByNome(nome)
                .map(u -> {
                    if (!u.getSenha().equals(senha)) throw new RuntimeException("Senha incorreta");
                    return u;
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setNome(nome);
                    newUser.setSenha(senha); // Adicionar criptografica depois (BCrypt)
                    return userRepo.save(newUser);
                });
    }

    // Endpoint para a Tabela de Estatísticas dos Usuários
    @GetMapping("/users/stats")
    public List<Map<String, Object>> getUserStats() {
        return userRepo.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("nome", u.getNome());
            map.put("ativas", equipRepo.countActiveReservations(u.getId()));
            return map;
        }).collect(Collectors.toList());
    }
}
