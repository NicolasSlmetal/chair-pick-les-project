import { post } from '../../http/api.js';
import { parseErrorMessages } from '../../utils/errorMessage.js';

const dialog = document.querySelector("dialog");
const button = dialog.querySelector("button");
button.addEventListener("click", () => dialog.close());

export async function postCreditCard(data) {
    const response = await post(`customers/${data.customer_id}/credit-cards`, data);

    if (response.status !== 201) {
        const errorJson = await response.json();
        const p = dialog.querySelector("p");
        p.textContent = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }

    window.location.href = `/customers/${data.customer_id}/credit-cards`;
}