import { post } from '../../http/api.js';
import { parseErrorMessages } from '../../utils/errorMessage.js';

const dialog = document.querySelector("dialog");
const button = dialog.querySelector("button");
button.addEventListener("click", () => dialog.close());

export async function postAddress(address) {
    const response = await post(`customers/${address.customerId}/addresses`, address);
    if (response.status !== 201) {
        const errorJson = await response.json();
        const p = dialog.querySelector("p");
        p.textContent = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }
    const lastActivity = window.history;
    if (lastActivity) {
        lastActivity.back();
        return;
    }

    window.location.href = "/";
}