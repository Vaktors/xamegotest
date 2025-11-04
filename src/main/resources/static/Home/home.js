document.addEventListener("DOMContentLoaded", () => {
    const navButtons = document.querySelector(".nav-buttons");

    const token   = localStorage.getItem("authToken");
    const nome    = localStorage.getItem("userName")  || "Você";
    const email   = localStorage.getItem("userEmail") || "";
    const fotoUrl = localStorage.getItem("userFoto")  || "/Home/img/default-avatar.png";

    function renderVisitante() {
        if (!navButtons) return;
        navButtons.innerHTML = `
            <button class="btnRoxo" id="ajudar">Quero ajudar</button>
            <a href="../login/index.html">
                <button class="btnBranco" id="entrar">Entrar</button>
            </a>
        `;
    }

    function renderUsuarioLogado() {
        if (!navButtons) return;
        navButtons.innerHTML = `
            <button class="btnRoxo" id="ajudar">Quero ajudar</button>

            <div id="userMenuWrapper" style="
                position: relative;
                font-family: 'Manrope', sans-serif;
            ">
                <button id="userButton" style="
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    background-color: #fff;
                    border: 1px solid #00000033;
                    border-radius: 12px;
                    padding: 10px 16px;
                    cursor: pointer;
                    font-size: 16px;
                    font-weight: 500;
                    line-height: 1;
                ">
                    <div style="
                        width: 32px;
                        height: 32px;
                        border-radius: 50%;
                        background-color: #4a35a4;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        overflow: hidden;
                        flex-shrink: 0;
                        color: #fff;
                        font-size: 14px;
                        font-weight: 600;
                        border: 2px solid #fff;
                        box-shadow: 0 0 0 2px #4a35a4;
                    ">
                        ${
                            fotoUrl
                                ? `<img src="${fotoUrl}" alt="foto perfil" style="width:100%;height:100%;object-fit:cover;">`
                                : `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>`
                        }
                    </div>
                    <span style="color:#1E1E1E; max-width:120px; text-overflow:ellipsis; white-space:nowrap; overflow:hidden;">
                        ${nome}
                    </span>
                    <span style="
                        border: solid #1E1E1E;
                        border-width: 0 2px 2px 0;
                        display: inline-block;
                        padding: 3px;
                        transform: rotate(45deg);
                        margin-left:4px;
                    "></span>
                </button>

                <div id="userDropdown" style="
                    position: absolute;
                    top: calc(100% + 8px);
                    right: 0;
                    min-width: 200px;
                    background:#fff;
                    border:1px solid #eee;
                    border-radius:12px;
                    box-shadow:0 12px 24px rgba(0,0,0,0.08);
                    padding:12px;
                    display:none;
                    z-index:9999;
                    font-size:15px;
                    line-height:1.4;
                    color:#1E1E1E;
                ">
                    <div style="padding:8px 10px; border-bottom:1px solid #eee;">
                        <div style="font-weight:600;">${nome}</div>
                        <div style="font-size:13px; color:#555;">${email}</div>
                    </div>

                    <button id="meuPerfilBtn" style="
                        all:unset;
                        width:100%;
                        display:block;
                        padding:10px;
                        cursor:pointer;
                        border-radius:8px;
                        font-weight:500;
                        box-sizing:border-box;
                    ">Meu perfil</button>

                    <button id="sairBtn" style="
                        all:unset;
                        width:100%;
                        display:block;
                        padding:10px;
                        cursor:pointer;
                        border-radius:8px;
                        font-weight:600;
                        color:#d10;
                        box-sizing:border-box;
                    ">Sair</button>
                </div>
            </div>
        `;

        // depois de injetar o HTML dinamicamente, precisamos ligar os eventos:
        const userButton = document.getElementById("userButton");
        const dropdown   = document.getElementById("userDropdown");
        const sairBtn    = document.getElementById("sairBtn");
        const perfilBtn  = document.getElementById("meuPerfilBtn");

        // abre/fecha dropdown ao clicar no botão do usuário
        userButton.addEventListener("click", (e) => {
            e.stopPropagation();
            const visible = dropdown.style.display === "block";
            dropdown.style.display = visible ? "none" : "block";
        });

        // fecha dropdown clicando fora
        document.addEventListener("click", () => {
            dropdown.style.display = "none";
        });

        // futuro: ir pra página de perfil
        perfilBtn.addEventListener("click", () => {
            alert("TODO: navegar para tela de perfil 😺");
        });

        // logout
        sairBtn.addEventListener("click", async () => {
            try {
                const tk = localStorage.getItem("authToken");
                if (tk) {
                    await fetch("/api/auth/logout", {
                        method: "POST",
                        headers: {
                            "Authorization": "Bearer " + tk
                        }
                    });
                }
            } catch (err) {
                console.warn("Falha ao avisar logout pro backend:", err);
            }

            // limpa sessão local
            localStorage.removeItem("authToken");
            localStorage.removeItem("userName");
            localStorage.removeItem("userEmail");
            localStorage.removeItem("userFoto");

            // recarrega pra voltar estado visitante
            window.location.reload();
        });
    }

    // decide qual estado usar
    if (token && token.trim() !== "") {
        renderUsuarioLogado();
    } else {
        renderVisitante();
    }
});
