document.addEventListener('DOMContentLoaded', () => {
    // 1. Seleciona os elementos
    const linkAbrir = document.getElementById('abrirModalTermos');
    const botaoFechar = document.getElementById('fecharModalTermos');
    const modal = document.getElementById('modalTermos');

    // 2. Função para abrir o modal
    linkAbrir.addEventListener('click', (e) => {
        // Previne o comportamento padrão do link (que seria tentar navegar)
        e.preventDefault();
        // Adiciona a classe 'visivel' para mostrar o overlay e o modal
        modal.classList.add('visivel');
    });

    // 3. Função para fechar o modal
    botaoFechar.addEventListener('click', () => {
        // Remove a classe 'visivel' para esconder o overlay e o modal
        modal.classList.remove('visivel');
    });

    // BÔNUS: Fechar ao clicar fora do modal (no overlay)
    modal.addEventListener('click', (e) => {
        // Verifica se o clique foi diretamente no elemento 'modal-overlay' e não em um de seus filhos
        if (e.target.id === 'modalTermos') {
            modal.classList.remove('visivel');
        }
    });

    // BÔNUS: Fechar ao pressionar a tecla ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && modal.classList.contains('visivel')) {
            modal.classList.remove('visivel');
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    // ===== Elementos =====
    const form = document.getElementById('form-cadastro');
    const nome = document.getElementById('nome');
    const email = document.getElementById('email');
    const senha = document.getElementById('senha');
    const confirmarSenha = document.getElementById('confirmar-senha');
    const showPassword = document.getElementById('show-password');
    const msg = document.getElementById('msg');
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
        linkAbrir.addEventListener('click', (e) => {
            e.preventDefault();
            modal.classList.add('visivel');
        });

        botaoFechar.addEventListener('click', () => {
            modal.classList.remove('visivel');
        });

        modal.addEventListener('click', (e) => {
            if (e.target.id === 'modalTermos') {
                modal.classList.remove('visivel');
            }
        });

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
            msg && (msg.style.display = 'none');

            const erros = validar();
            if (erros.length) {
                showMsg(erros[0], 'erro');
                return;
            }

            const payload = {
                nome: nome.value.trim(),
                email: email.value.trim(),
                senha: senha.value,
                funcao: 'USUARIO' // padrão por enquanto
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

                // Sucesso
                showMsg('Cadastro criado com sucesso! Você já pode entrar.', 'ok');
                form.reset();
            } catch (err) {
                showMsg(err.message || 'Falha ao cadastrar.', 'erro');
            } finally {
                setLoading(false);
            }
        });
    }
});