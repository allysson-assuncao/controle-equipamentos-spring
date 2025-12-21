const BASE_URL = "http://localhost:8080/api";

const api = {
    // Login / Cadastro simplificado
    login: async (nome, senha) => {
        const response = await fetch(`${BASE_URL}/users/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nome, senha })
        });
        if (!response.ok) throw new Error("Erro no login");
        return await response.json();
    },

    // Buscar equipamentos
    getEquipments: async () => {
        const response = await fetch(`${BASE_URL}/equipments`);
        return await response.json();
    },

    // Buscar estatísticas de usuários
    getStats: async () => {
        const response = await fetch(`${BASE_URL}/users/stats`);
        return await response.json();
    },

    // Criar equipamento
    createEquipment: async (nome) => {
        await fetch(`${BASE_URL}/equipments`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nome })
        });
    },

    // Reservar
    reserve: async (equipId, userId, minutes) => {
        // Nota: endpoints com @RequestParam usam URLSearchParams ou query string
        const response = await fetch(`${BASE_URL}/equipments/${equipId}/reserve?userId=${userId}&minutes=${minutes}`, {
            method: "POST"
        });
        if (!response.ok) throw new Error("Falha ao reservar");
    },

    // Liberar
    release: async (equipId) => {
        await fetch(`${BASE_URL}/equipments/${equipId}/release`, {
            method: "POST"
        });
    }
};
