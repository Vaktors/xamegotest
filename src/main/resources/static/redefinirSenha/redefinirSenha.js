document.addEventListener("DOMContentLoaded", () => {
    
    const form = document.getElementById("newPasswordForm");
    const senha = document.getElementById("senha");
    const confirmar = document.getElementById("confirmarSenha");
    const erro = document.getElementById("erroSenha");

    // Mostrar / ocultar senha
    document.querySelectorAll(".toggle-pass").forEach(btn => {
        btn.addEventListener("click", () => {
            const target = document.getElementById(btn.dataset.input);
            target.type = target.type === "password" ? "text" : "password";
        });
    });

    // Validação das senhas
    form.addEventListener("submit", (e) => {
        if (senha.value.trim() === "" || confirmar.value.trim() === "") return;

        if (senha.value !== confirmar.value) {
            e.preventDefault();
            erro.style.display = "block";
            confirmar.style.borderColor = "#D32F2F";
        } else {
            erro.style.display = "none";
            confirmar.style.borderColor = "";
            // Aqui você pode enviar para o backend ou redirecionar
            console.log("Enviar redefinição para o backend...");
        }
    });

    // Some a mensagem ao digitar novamente
    confirmar.addEventListener("input", () => {
        erro.style.display = "none";
        confirmar.style.borderColor = "";
    });

});
document.querySelectorAll('.toggle-pass').forEach(icon => {
    icon.addEventListener('click', () => {
        const input = document.getElementById(icon.dataset.target);

        if (input.type === "password") {
            input.type = "text";
            icon.src = "img/eye-on.svg"; // ícone olho aberto
        } else {
            input.type = "password";
            icon.src = "img/eye-off.svg"; // ícone olho fechado
        }
    });
});
