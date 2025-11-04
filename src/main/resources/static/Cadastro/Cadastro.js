document.addEventListener('DOMContentLoaded', () => {
    // ===== Elementos =====
    const form = document.getElementById('form-cadastro');
    const nome = document.getElementById('nome');
    const email = document.getElementById('email');
    const senha = document.getElementById('senha');
    const confirmarSenha = document.getElementById('confirmar-senha');
    const showPassword = document.getElementById('show-password');
    const btnSubmit = document.querySelector('.btn-submit');

    // ===== Mostrar/ocultar senha =====
    if (showPassword) {
        showPassword.addEventListener('change', () => {
            const tipo = showPassword.checked ? 'text' : 'password';
            senha.type = tipo;
            confirmarSenha.type = tipo;
        });
    }

    // ===== Modal de Termos =====
    const linkAbrir = document.getElementById('abrirModalTermos');
    const botaoFechar = document.getElementById('fecharModalTermos');
    const modal = document.getElementById('modalTermos');

    if (linkAbrir && botaoFechar && modal) {
        // abre modal
        linkAbrir.addEventListener('click', (e) => {
            e.preventDefault();
            modal.classList.add('visivel');
        });

        // fecha modal no botão fechar
        botaoFechar.addEventListener('click', () => {
            modal.classList.remove('visivel');
        });

        // fecha clicando fora (overlay)
        modal.addEventListener('click', (e) => {
            if (e.target.id === 'modalTermos') {
                modal.classList.remove('visivel');
            }
        });

        // fecha com ESC
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.classList.contains('visivel')) {
                modal.classList.remove('visivel');
            }
        });
    }

    // ===== Utils =====
    function setLoading(loading) {
        if (btnSubmit) {
            btnSubmit.disabled = loading;
            btnSubmit.setAttribute('aria-busy', String(loading));
            if (loading) {
                btnSubmit.dataset._label = btnSubmit.textContent;
                btnSubmit.textContent = 'Enviando...';
            } else {
                btnSubmit.textContent = btnSubmit.dataset._label || 'Criar a minha conta';
            }
        }
    }

    function showMsg(text) {
        const aviso = document.getElementById('avisoConteudo');
        const texto = document.getElementById('avisoTexto');

        if (!aviso || !texto) return;

        // Define o texto da mensagem
        texto.textContent = text;

        // Mostra o aviso (caso esteja oculto)
        aviso.style.display = 'flex';

        // Animação suave de exibição
        aviso.style.opacity = '0';
        setTimeout(() => {
            aviso.style.transition = 'opacity 0.3s ease';
            aviso.style.opacity = '1';
        }, 10);
    }

    // validação básica
    function validar() {
        const erros = [];
        if (!nome.value.trim()) erros.push('Informe seu nome.');
        if (!email.value.trim()) erros.push('Informe um e-mail.');
        if (!senha.value) erros.push('Informe uma senha.');
        if (senha.value && senha.value.length < 6) erros.push('A senha deve ter pelo menos 6 caracteres.');
        if (senha.value !== confirmarSenha.value) erros.push('As senhas não coincidem.');
        return erros;
    }

    // ===== Submit =====
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const erros = validar();
            if (erros.length) {
                showMsg(erros[0]); // primeira msg de erro
                return;
            }

            // <-- AQUI estava o erro
            const payload = {
                nome: nome.value.trim(),
                email: email.value.trim(),
                senha: senha.value,
                roleNome: 'ROLE_USUARIO'
            };

            try {
                setLoading(true);

                const resp = await fetch('/api/usuarios/cadastrar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const texto = await resp.text();
                if (!resp.ok) {
                    throw new Error(texto || 'Erro ao cadastrar.');
                }

                // sucesso
                showMsg('Cadastro criado com sucesso! Você já pode entrar.');
                form.reset();
            } catch (err) {
                showMsg(err.message || 'Falha ao cadastrar.');
            } finally {
                setLoading(false);
            }
        });
    }
});
