import { patchSwapStatus } from "./patchSwapStatus.js";
import { patchSwapReceived } from "./patchSwapReceived.js";
import { parseErrorMessages } from "../utils/errorMessage.js"

const orderId = document.querySelector("#orderId").value;
const dialog = document.querySelector("#confirm_action");
const swapConfirmDialog = document.querySelector("#confirm__swap__modal")
const confirmSwapButton = swapConfirmDialog.querySelector("#confirm__swap__button");
const cancelSwapButton = swapConfirmDialog.querySelector("#cancel__swap__button");
const cancelButton = dialog.querySelector("#cancel_button");
const errorDialog = document.querySelector("#error__modal");
const okButton = errorDialog.querySelector("button");
okButton.addEventListener("click", () => {
    errorDialog.close();
});

cancelSwapButton.onclick = () => {
    swapConfirmDialog.close();
}
cancelButton.onclick = () => {
    dialog.close();
}
const confirmButton = dialog.querySelector("#confirm_button");

const acceptButtons = document.querySelectorAll("button.accept");
const rejectButtons = document.querySelectorAll("button.reject");
const changeToSwappedButtons = document.querySelectorAll("button.change_to_swapped");

acceptButtons.forEach((button) => {
    button.addEventListener("click", () => {
        const row = button.closest("tr");
        const name = row.querySelector("td:nth-child(1)").innerText;
        const swapItemId = row.querySelector("input[name='swapItemId']").value;
        const action = async () => {
            dialog.close();
            await changeStatus(swapItemId, "IN_SWAP");
        }
        const p = dialog.querySelector("p");
        p.innerText = `Tem certeza que deseja aceitar a troca do produto ${name}?`;
        dialog.showModal();
        confirmButton.onclick = action;

    });
});

rejectButtons.forEach((button) => {
    button.addEventListener("click", () => {
        const row = button.closest("tr");
        const name = row.querySelector("td:nth-child(1)").innerText;
        const swapItemId = row.querySelector("input[name='swapItemId']").value;
        const action = async () => {
            dialog.close();
            await changeStatus(swapItemId, "SWAP_REPROVED");
        }
        const p = dialog.querySelector("p");
        p.innerText = `Tem certeza que deseja rejeitar a troca do produto ${name}?`;
        dialog.showModal();
        confirmButton.onclick = action;

    });
})

changeToSwappedButtons.forEach((button) => {
    button.addEventListener("click", () => {
        const row = button.closest("tr");
        const name = row.querySelector("td:nth-child(1)").innerText;
        const swapItemId = row.querySelector("input[name='swapItemId']").value;

        const action = async () => {
            swapConfirmDialog.close();
            const checkBoxModal = swapConfirmDialog.querySelector("input[type='checkbox']");
            const returnToStock = checkBoxModal.checked;
            const response = await patchSwapReceived(orderId, swapItemId, returnToStock);
            if (response.status !== 200) {
                const errorJson = await response.json();
                const errorMessage = parseErrorMessages(errorJson.message);
                const p = errorDialog.querySelector("p");
                p.innerText = errorMessage;
                errorDialog.showModal();
                return;
            }

            window.location.reload();
        }
        swapConfirmDialog.showModal();
        confirmSwapButton.onclick = action;

    });
});

function prepareActionButton(id = "", menuId) {
    const mapSwitchDisplay = {
        "none": "flex",
        "flex": "none"
    }
    const actionsButtons = document.querySelectorAll(`main img${id.length > 1 ? `#${id}` : ""}`);
    actionsButtons.forEach((actionButton) => {
        const parent = actionButton.parentElement;
        const menu = parent.querySelector(`#${menuId}`);
        const rectangle = actionButton.getBoundingClientRect();
        actionButton.addEventListener("click", () => {
            removeMenuOnClickOutside(menuId);
            menu.style.display = mapSwitchDisplay[menu.style.display ? menu.style.display : "none"];
            menu.style.top = `${rectangle.top + window.scrollY}px`;
            menu.style.left = `${rectangle.left + window.scroll}px`;
        });
    });
}

window.onload = () => {

    prepareActionButton("actions_button", "actions");
    document.addEventListener("click", (event) => {
        const isMenu = event.target.closest("div#actions");
        const isActionButton = event.target.closest("img#actions_button");
        if (!isMenu && !isActionButton) {
            removeMenuOnClickOutside("actions");
        }
    });
};

function removeMenuOnClickOutside(id) {
    const menu = document.querySelectorAll(`main #${id}`);
    menu.forEach((menu) => {
        menu.style.display = "none";
    });
}

async function changeStatus(swapId, status) {
    const response = await patchSwapStatus(orderId, swapId, status);
    if (response.status !== 200) {
        const errorJson = await response.json();
        const errorMessage = errorJson.message;
        console.error(errorMessage);
        return;
    }

    window.location.reload();
}