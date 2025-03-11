import { httpDelete } from "../http/api.js";
import { parseErrorMessages } from "../utils/errorMessage.js";

const dialog = document.querySelector("dialog.error__dialog");
const button = dialog.querySelector("button");
button.addEventListener("click", () => dialog.close());

export async function removeAddress(customerId, addressId) {
    const response = await httpDelete(`customers/${customerId}/addresses/${addressId}`);
    if (response.status !== 204) {
        const errorJson = await response.json();
        const p = dialog.querySelector("p");
        p.textContent = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }
    window.location.href = `/customers/${customerId}/addresses`;
}