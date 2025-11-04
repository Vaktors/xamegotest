document.getElementById("resetRequestForm")?.addEventListener("submit", (e) => {
    e.preventDefault();

    const email = document.getElementById("resetEmail").value.trim();

    if (!email) return;

    alert("Se o e-mail estiver cadastrado, enviaremos o link de redefinição.");
});
