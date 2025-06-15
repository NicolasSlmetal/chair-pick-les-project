import { post } from '../../http/api.js';
import { parseErrorMessages } from '../../utils/errorMessage.js';

const dialog = document.querySelector('dialog');
const button = dialog.querySelector('button');
button.addEventListener('click', () => {
    dialog.close();
});

export async function createItem(data) {
    const response = await post(`admin/chairs/${data.chairId}/items`, data);
    if (response.status !== 201) {
        const p = dialog.querySelector("p");
        const errorJson = await response.json();
        p.innerText = parseErrorMessages(errorJson.message);
        dialog.showModal();
        return;
    }

    window.location.href = `/admin/chairs`;
}