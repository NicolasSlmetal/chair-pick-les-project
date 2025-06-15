import { put } from '../../http/api.js';
import { parseErrorMessages } from '../../utils/errorMessage.js';

const dialog = document.querySelector("dialog");
const button = dialog.querySelector("button");
button.addEventListener("click", () => {
    dialog.close();
});

export async function updateChair(id, data) {
    const response = await put(`admin/chairs/${id}`, data, 200);

    if (response.status !== 200) {
        const p = dialog.querySelector("p");
        const errorJson = await response.json();
        p.innerText = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }

    window.location.href = "/admin/chairs";
}