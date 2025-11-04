document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    const emailInput = document.getElementById("email");
    const senhaInput = document.getElementById("senha");
    const mensagem = document.getElementById("mensagem");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        mensagem.classList.remove("erro", "sucesso");
        mensagem.innerText = "";

        let erro = false;

        if (!emailInput.value.trim()) {
            document.getElementById("erro-email").innerText = "Informe seu e-mail";
            emailInput.parentElement.classList.add("erro");
            erro = true;
        } else {
            document.getElementById("erro-email").innerText = "";
            emailInput.parentElement.classList.remove("erro");
        }

        if (!senhaInput.value.trim()) {
            document.getElementById("erro-senha").innerText = "Informe sua senha";
            senhaInput.parentElement.classList.add("erro");
            erro = true;
        } else {
            document.getElementById("erro-senha").innerText = "";
            senhaInput.parentElement.classList.remove("erro");
        }

        if (erro) return;

        try {
            const resp = await fetch("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: emailInput.value,
                    senha: senhaInput.value
                })
            });

            if (resp.ok) {
                const data = await resp.json();
                // data = { token, tipo, usuario, email, fotoPerfilUrl }

                // guarda no localStorage pro resto do site usar
                localStorage.setItem("authToken", data.token);
                localStorage.setItem("authTipo", data.tipo); // "Bearer"
                localStorage.setItem("userNome", data.usuario);
                localStorage.setItem("userEmail", data.email);
                localStorage.setItem("userFoto", data.fotoPerfilUrl); // <- NOVO

                mensagem.classList.add("sucesso");
                mensagem.innerText = "Login realizado com sucesso!";

                // redireciona para Home
                window.location.href = "../Home/index.html";

            } else if (resp.status === 401) {
                const body = await resp.json().catch(() => ({}));
                mensagem.classList.add("erro");

                if (body && body.erro) {
                    mensagem.innerText = body.erro;
                } else {
                    mensagem.innerText = "Credenciais inválidas.";
                }
            } else if (resp.status === 403) {
                mensagem.classList.add("erro");
                mensagem.innerText = "Sua conta está bloqueada.";
            } else {
                mensagem.classList.add("erro");
                mensagem.innerText = "Erro inesperado ao tentar entrar.";
            }
        } catch (err) {
            console.error("Erro na requisição /api/auth/login:", err);
            mensagem.classList.add("erro");
            mensagem.innerText = "Erro de conexão. Tente novamente em alguns instantes.";
        }
    });
});
