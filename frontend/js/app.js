// Verifica sessão
const userJson = localStorage.getItem('user');
if (!userJson) window.location.href = 'index.html';
const currentUser = JSON.parse(userJson);

// Exibe nome no topo
document.getElementById('userNameDisplay').textContent = `Olá, ${currentUser.nome}`;

// Função de Logout
window.logout = () => {
    localStorage.removeItem('user');
    window.location.href = 'index.html';
};

// --- Renderização ---
function renderEquipments(list) {
    const tbody = document.getElementById('equipList');
    tbody.innerHTML = list.map(equip => {
        const isOccupied = equip.occupied;
        const isMine = equip.bookedBy && equip.bookedBy.id === currentUser.id;

        // Formatação de data simples
        let timeDisplay = '-';
        if (equip.reservedUntil) {
            const date = new Date(equip.reservedUntil);
            timeDisplay = date.toLocaleTimeString();
        }

        // Definição de Classes e Botões dinâmicos
        let statusBadge = isOccupied
            ? `<span class="bg-red-100 text-red-800 text-xs font-bold px-2 py-1 rounded">OCUPADO</span>`
            : `<span class="bg-green-100 text-green-800 text-xs font-bold px-2 py-1 rounded">LIVRE</span>`;

        let rowClass = "hover:bg-gray-50 transition";
        if (isMine) rowClass = "bg-blue-50 border-l-4 border-blue-500";

        // Botões de Ação
        let actionBtn = '';
        if (!isOccupied) {
            actionBtn = `
                <div class="flex gap-1">
                    <select id="time-${equip.id}" class="text-sm border rounded p-1">
                        <option value="1">1 min</option>
                        <option value="30">30 min</option>
                        <option value="60">1 h</option>
                    </select>
                    <button onclick="handleReserve(${equip.id})" class="text-xs bg-green-500 text-white px-2 py-1 rounded hover:bg-green-600">
                        Reservar
                    </button>
                </div>`;
        } else if (isMine) {
            actionBtn = `
                <button onclick="handleRelease(${equip.id})" class="text-xs bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600 font-bold">
                    Devolver
                </button>`;
        } else {
            actionBtn = `<span class="text-gray-400 text-xs italic">Indisponível</span>`;
        }

        return `
            <tr class="${rowClass}">
                <td class="p-4 font-medium">${equip.nome} <span class="text-xs text-gray-400 block">Usos: ${equip.totalUses}</span></td>
                <td class="p-4">${statusBadge}</td>
                <td class="p-4 text-sm text-gray-600">${equip.bookedBy ? equip.bookedBy.nome : '-'}</td>
                <td class="p-4 text-sm font-mono">${timeDisplay}</td>
                <td class="p-4">${actionBtn}</td>
            </tr>
        `;
    }).join('');
}

function renderStats(list) {
    const ul = document.getElementById('userStatsList');
    ul.innerHTML = list.map(u => `
        <li class="p-4 flex justify-between items-center">
            <span class="text-gray-700">${u.nome}</span>
            <span class="bg-gray-200 text-gray-800 text-xs font-bold px-2 py-1 rounded-full">
                ${u.ativas} ativos
            </span>
        </li>
    `).join('');
}

// --- Ações ---
window.addEquipment = async () => {
    const input = document.getElementById('newEquipName');
    if(!input.value) return;
    await api.createEquipment(input.value);
    input.value = '';
    loadData(); // Atualiza na hora
};

window.handleReserve = async (equipId) => {
    const minutes = document.getElementById(`time-${equipId}`).value;
    try {
        await api.reserve(equipId, currentUser.id, minutes);
        loadData();
    } catch (e) { alert("Erro ao reservar"); }
};

window.handleRelease = async (equipId) => {
    try {
        await api.release(equipId);
        loadData();
    } catch (e) { alert("Erro ao devolver"); }
};

// --- Lógica Principal de Polling ---
async function loadData() {
    // Faz as duas requisições em paralelo
    const [equipments, stats] = await Promise.all([
        api.getEquipments(),
        api.getStats()
    ]);
    renderEquipments(equipments);
    renderStats(stats);
}

// Carrega na abertura
loadData();

// HTTP POLLING: Atualiza a cada 2 segundos SEM recarregar a página
setInterval(loadData, 2000);