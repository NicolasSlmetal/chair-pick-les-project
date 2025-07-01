import { patch } from "../../http/api.js";
import { parseErrorMessages } from "../../utils/errorMessage.js";

const dialog = document.querySelector("dialog");
const button = dialog.querySelector("button");
button.addEventListener("click", () => {
    dialog.close()
});

export async function patchCustomer(customer) {
    const response = await patch(`customers/${customer.id}/alter-password`, customer);
    if (response.status !== 200) {
        const errorJson = await response.json();
        const p = dialog.querySelector("p");
        dialog.querySelector("h1").innerText = "Erro ao alterar senha";
        p.innerText = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }

    button.addEventListener("click", () => {
        window.location.href = "/customers/" + customer.id;
    });
    dialog.querySelector("h1").innerText = "Senha alterada";
    dialog.querySelector("p").innerText = "Você já pode fazer login com a nova senha.";
    dialog.showModal();

}