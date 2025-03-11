import { dialog, dialogCancelButton, dialogConfirmButton } from "../consts.js";
import { removeCreditCard } from "./deleteCreditCard.js";

var selectedCreditCardId = null;
const customerId = document.querySelector("input#customer_id").value;

export function prepareDialogButtons() {
    dialogCancelButton.addEventListener("click", (event) => {
        dialog.close();
    });

    dialogConfirmButton.addEventListener("click", async (event) => {
        dialog.close();
        await remove(selectedCreditCardId);
    });

}

function prepareActionButton(id = "", menuId) {

    const mapSwitchDisplay = {
        "none": "flex",
        "flex": "none"
    }
    const actionsButtons = document.querySelectorAll(`main img${id.length > 1 ? `#${id}` : ""}`);
    actionsButtons.forEach(actionButton => {

        const parent = actionButton.parentElement;
        const menu = parent.querySelector(`#${menuId}`);
        const rectangle = actionButton.getBoundingClientRect();
        actionButton.addEventListener("click", () => {
            removeMenuOnClickOutside(menuId);
            menu.style.display = mapSwitchDisplay[menu.style.display ? menu.style.display : "none"];
            menu.style.top = `${rectangle.top + window.scrollY}px`;
            menu.style.left = `${rectangle.left + window.scroll}px`;
        });
    })


}

export function prepareRemoveButton() {
    const removeButtons = document.querySelectorAll("main .action__button.danger");
    console.log(removeButtons);
    removeButtons.forEach((button) => {
        if (button.classList.contains("disabled")) {
            return;
        }
        button.addEventListener("click", () => {
            const id = button.parentElement.parentElement.querySelector("input[type='hidden']").value;
            const number = button.parentElement.parentElement.querySelectorAll("td")[0].innerText;
            selectedCreditCardId = id;
            console.log({ id, number });
            const p = dialog.querySelector("p");
            p.innerText = `Tem certeza que deseja remover o cartão \"${number}\"?`;
            dialog.showModal();
        });
    });
}


window.onload = () => {
    prepareActionButton("more_actions", "actions_options");
    document.addEventListener("click", (event) => {
        const isMenu = event.target.closest("div#actions_options");
        const isActionButton = event.target.closest("img#more_actions");
        if (!isMenu && !isActionButton) {
            removeMenuOnClickOutside("actions_options");
        }
    });

    document.addEventListener("click", (event) => {
        const isMenu = event.target.closest("div#search_menu");
        const isActionButton = event.target.closest("img#search_options");
        if (!isMenu && !isActionButton) {
            removeMenuOnClickOutside("search_menu");
        }
    });

    prepareDialogButtons();
    prepareRemoveButton();
}

function removeMenuOnClickOutside(id) {
    const menus = document.querySelectorAll(`main #${id}`);
    console.log("Clicked")
    menus.forEach(menu => menu.style.display = "none")

}

async function remove(creditCardId) {
    await removeCreditCard(customerId, creditCardId);
}