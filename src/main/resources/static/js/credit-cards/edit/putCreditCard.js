import { put } from '../../http/api.js';
import { parseErrorMessages } from "../../utils/errorMessage.js";

const dialog = document.querySelector("dialog");

export async function putCreditCard(creditCard) {
    const response = await put(`customers/${creditCard.customer_id}/credit-cards`, creditCard);
    const button = dialog.querySelector("button");
    button.addEventListener("click", () => {
        dialog.close()
    });

    if (response.status !== 200) {
        const errorJson = await response.json();
        const p = dialog.querySelector("p");
        p.innerText = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }

    window.location.href = `/customers/${creditCard.customer_id}/credit-cards`;
}