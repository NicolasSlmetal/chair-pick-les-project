import { post } from "../http/api.js";

const dialog = document.querySelector("dialog.status__dialog");
const okButton = dialog.querySelector("button");

export async function updatePassword(data) {
    const response = await post("reset-password", data);
    if (response.status === 200) {
        dialog.querySelector("h2").textContent = "Senha alterada com sucesso!";
        dialog.querySelector("p").textContent = "Você já pode fazer login com a nova senha.";
        dialog.showModal();
        okButton.addEventListener("click", () => {
            window.location.href = "/login";
        });
        return;
    }
    const errorMessage = await response.text();
    dialog.querySelector("h2").textContent = "Erro ao alterar senha";
    dialog.querySelector("p").textContent = errorMessage;
    dialog.showModal();
    okButton.addEventListener("click", () => {
        dialog.close();
    });
}