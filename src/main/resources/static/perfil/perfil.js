document.addEventListener('DOMContentLoaded', () => {
  // ---------- helpers ----------
  const qs  = (s, r = document) => r.querySelector(s);
  const qsa = (s, r = document) => Array.from(r.querySelectorAll(s));

  // transforma um span.value em input/select inline
  function spanToInput(span) {
    const key = span.dataset.key;
    const locked = span.dataset.locked === 'true';
    const text = span.textContent.trim();

    // E-mail bloqueado
    if (locked) {
      const input = document.createElement('input');
      input.className = 'inline-input';
      input.type = 'text';
      input.value = text;
      input.disabled = true;
      input.setAttribute('aria-disabled', 'true');
      span.replaceWith(input);
      return input;
    }

    // gênero vira select com opções
    if (key === 'genero') {
      const select = document.createElement('select');
      select.className = 'inline-input';
      ['Masculino', 'Feminino', 'Outro', 'Prefiro não informar'].forEach(opt => {
        const o = document.createElement('option');
        o.value = opt; o.textContent = opt;
        if (opt.toLowerCase() === text.toLowerCase()) o.selected = true;
        select.appendChild(o);
      });
      span.replaceWith(select);
      return select;
    }

    // demais textos → input
    const input = document.createElement('input');
    input.className = 'inline-input';
    input.type = 'text';
    // placeholder assume o texto antigo (como você pediu)
    input.placeholder = text;
    input.value = text; // manter valor visível enquanto edita
    span.replaceWith(input);
    return input;
  }

  // volta input/select para span.value
  function inputToSpan(input, key) {
    const span = document.createElement('span');
    span.className = 'value';
    span.dataset.key = key;

    if (input.tagName === 'SELECT') {
      span.textContent = input.value || input.options[input.selectedIndex]?.text || '';
    } else {
      span.textContent = input.value.trim() || input.placeholder || '';
      if (input.disabled) span.dataset.locked = 'true';
    }
    input.replaceWith(span);
    return span;
  }

  function enterEdit(section) {
    const grid = qs(`[data-grid="${section}"]`);
    const spans = qsa('.value', grid);
    const inputs = spans.map(spanToInput);

    // troca botões
    const hdr = qs(`.button-group[data-section="${section}"]`);
    qs('.btn-edit', hdr).hidden = true;
    qs('.btn-confirm', hdr).hidden = false;
    qs('.btn-cancel', hdr).hidden = false;

    // devolve função de saída
    return {
      confirm() {
        qsa('input.inline-input, select.inline-input', grid).forEach(inp => {
          const key = inp.closest('.field').querySelector('[data-key]')?.dataset.key
                   || inp.closest('.field').querySelector('label')?.textContent?.toLowerCase();
          // tenta recuperar key correto
        });
        qsa('.field', grid).forEach(field => {
          const inp = field.querySelector('input.inline-input, select.inline-input');
          if (!inp) return;
          const keyEl = field.querySelector('[data-key]'); // antigo span (já trocado)
          const key = keyEl ? keyEl.dataset.key : field.querySelector('label')?.textContent?.toLowerCase();
          inputToSpan(inp, key);
        });
        qs('.btn-edit', hdr).hidden = false;
        qs('.btn-confirm', hdr).hidden = true;
        qs('.btn-cancel', hdr).hidden = true;
      },
      cancel() {
        // simplesmente descarta mudanças e volta os placeholders antigos
        qsa('.field', grid).forEach(field => {
          const inp = field.querySelector('input.inline-input, select.inline-input');
          if (!inp) return;
          const keyEl = field.querySelector('[data-key]');
          const key = keyEl ? keyEl.dataset.key : field.querySelector('label')?.textContent?.toLowerCase();

          // cria span com texto original (placeholder/seleção)
          const span = document.createElement('span');
          span.className = 'value';
          span.dataset.key = key;
          if (inp.tagName === 'SELECT') {
            span.textContent = inp.options[inp.selectedIndex]?.text || '';
          } else {
            span.textContent = inp.placeholder || inp.value || '';
            if (inp.disabled) span.dataset.locked = 'true';
          }
          inp.replaceWith(span);
        });
        qs('.btn-edit', hdr).hidden = false;
        qs('.btn-confirm', hdr).hidden = true;
        qs('.btn-cancel', hdr).hidden = true;
      }
    };
  }

  // controla cada seção
  const states = {}; // {pessoais: {confirm, cancel}, endereco: {...}}
  qsa('.btn-edit').forEach(btn => {
    btn.addEventListener('click', () => {
      const sec = btn.dataset.section;
      states[sec]?.cancel?.(); // se já estiver em edição, reseta
      states[sec] = enterEdit(sec);
    });
  });
  qsa('.btn-confirm').forEach(btn => {
    btn.addEventListener('click', () => {
      const sec = btn.dataset.section;
      states[sec]?.confirm?.();
      states[sec] = null;
      // aqui futuramente você dispara PATCH/PUT pro backend
    });
  });
  qsa('.btn-cancel').forEach(btn => {
    btn.addEventListener('click', () => {
      const sec = btn.dataset.section;
      states[sec]?.cancel?.();
      states[sec] = null;
    });
  });
});


document.getElementById("perfilAvatar").addEventListener("click", () => {
    document.getElementById("perfilDropdown").classList.toggle("show");
});

// Fechar quando clicar fora
document.addEventListener("click", function(e) {
    const menu = document.getElementById("perfilDropdown");
    if (!menu.contains(e.target)) {
        menu.classList.remove("show");
    }
});

// Botão de sair (chame a lógica que quiser aqui)
document.getElementById("logoutBtn").addEventListener("click", () => {
    alert("Sair da conta");
});

(() => {
  const modal   = document.getElementById('modalDelete');
  const openBtn = document.getElementById('openDeleteModal');
  const confirmBtn = document.getElementById('confirmDeleteBtn');

  function openModal() {
    modal.classList.add('show');
    // foca no botão de cancelar (primeiro com [data-close]) para acessibilidade
    const cancel = modal.querySelector('[data-close]');
    cancel && cancel.focus();
  }

  function closeModal() {
    modal.classList.remove('show');
    openBtn && openBtn.focus();
  }

  // Abrir
  openBtn?.addEventListener('click', (e) => {
    e.preventDefault();
    openModal();
  });

  // Fechar por qualquer [data-close] dentro do modal
  modal.addEventListener('click', (e) => {
    if (e.target.matches('[data-close]')) {
      closeModal();
    }
    // fechar se clicar fora da caixinha
    if (e.target === modal) {
      closeModal();
    }
  });

  // ESC fecha
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && modal.classList.contains('show')) {
      closeModal();
    }
  });

  // Confirmar deleção (aqui só front; depois plugamos no backend)
  confirmBtn?.addEventListener('click', async () => {
    // TODO: chamar seu endpoint real de exclusão de conta
    // Ex.: await fetch('/api/usuarios/me', { method: 'DELETE', headers: { Authorization: `Bearer ${token}` } })

    // Por enquanto, só demonstração:
    console.log('Solicitar exclusão de conta…');
    closeModal();
    // Você pode redirecionar depois de excluir:
    // window.location.href = '/';
  });
})();