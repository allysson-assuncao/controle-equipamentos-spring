document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const nome = document.getElementById('nome').value;
    const senha = document.getElementById('senha').value;
    const errorMsg = document.getElementById('errorMsg');

    try {
        const user = await api.login(nome, senha);
        // Salva sessão no navegador (simulando cookie/token)
        localStorage.setItem('user', JSON.stringify(user));
        window.location.href = 'dashboard.html';
    } catch (err) {
        errorMsg.textContent = "Erro: Verifique sua senha.";
        errorMsg.classList.remove('hidden');
    }
});