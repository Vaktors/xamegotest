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