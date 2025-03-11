import { httpDelete } from "../http/api.js";
import { parseErrorMessages } from "../utils/errorMessage.js";

const dialog = document.querySelector("dialog#error__modal");
const button = dialog.querySelector("button");
button.addEventListener("click", () => {
    dialog.close()
});

export async function removeCreditCard(customerId, creditCardId) {
    const response = await httpDelete(`customers/${customerId}/credit-cards/${creditCardId}`);
    if (response.status !== 204) {
        const errorJson = await response.json();
        const p = dialog.querySelector("p");
        p.innerText = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }

    window.location.reload();
}