const codeInputs = document.querySelectorAll('.code-input');

if (codeInputs.length > 0) {
    codeInputs.forEach((input, index) => {
        input.addEventListener('keydown', (e) => {
            // Permite apenas a tecla "Backspace" e números
            if (e.key === 'Backspace') {
                setTimeout(() => {
                    if (index > 0) {
                       codeInputs[index - 1].focus();
                    }
                }, 10);
            } else if (!/^[0-9]$/.test(e.key)) {
                e.preventDefault();
            } else {
                // Limpa o campo antes de preencher e move para o próximo
                input.value = '';
                setTimeout(() => {
                    if (index < codeInputs.length - 1) {
                       codeInputs[index + 1].focus();
                    }
                }, 10);
            }
        });

        input.addEventListener('paste', (e) => {
            e.preventDefault();
            const pasteData = e.clipboardData.getData('text');
            const codeArray = pasteData.split('');
            codeInputs.forEach((box, i) => {
                if (codeArray[i] && /^[0-9]$/.test(codeArray[i])) {
                    box.value = codeArray[i];
                }
            });

            const lastIndexToFocus = Math.min(codeInputs.length - 1, pasteData.length - 1);
            codeInputs[lastIndexToFocus].focus();
        });
    });
}
