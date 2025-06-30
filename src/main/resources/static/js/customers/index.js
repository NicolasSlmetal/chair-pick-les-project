import { dialog, dialogCancelButton, dialogConfirmButton } from "../consts.js";
import { remove } from "./deleteCustomer.js";

var selectedClientId = null;

function setupClearButton() {
    const clearButton = document.querySelector("main #clear");
    clearButton.addEventListener("click", () => {
        document.querySelectorAll("main input").forEach(input => input.value = "");
        document.querySelector("select").value = "none";
    });
}

function setupSearchButton() {
    const searchButton = document.querySelector("main #search");
    searchButton.addEventListener("click", async () => {
        const inputs = document.querySelectorAll("main input, main select");
        let querySearch = "?";

        inputs.forEach(input => {
            if (input.value === "" || input.value === "none") {
                return;
            }

            if (input.id === "cpf") {
                const unformattedCpf = input.value.replace(/\D/g, "");
                querySearch += `${input.id}=${unformattedCpf}&`;
                return;
            }
            if (input.id === "phone") {
                const splitPhone = input.value.split(" ");
                const ddd = splitPhone[0].replace("(", "").replace(")", "");
                const phone = splitPhone[1].replace("-", "").replace("_", "");
                const unformattedPhone = phone.replace(/\D/g, "");
                if (unformattedPhone.length < 1) {
                    return;
                }
                querySearch += `phone_ddd=${ddd}&phone=${unformattedPhone}&`;
                return;
            }

            querySearch += `${input.id}=${input.value}&`;

        });
        querySearch = querySearch.replace("birthdate", "born_date");
        querySearch = querySearch.replace("male", "m");
        querySearch = querySearch.replace("female", "f");
        querySearch = querySearch.slice(0, -1);
        window.location.href = `/admin/customers${querySearch}`;
    });
}
export function prepareDialogButtons() {
    dialogCancelButton.addEventListener("click", (event) => {
        dialog.close();
    });

    dialogConfirmButton.addEventListener("click", async (event) => {
        dialog.close();
        await removeCustomer(selectedClientId);
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
    const removeButtons = document.querySelectorAll("main #remove_button");

    removeButtons.forEach((button) => {
        if (button.classList.contains("disabled")) {
            return;
        }
        button.addEventListener("click", () => {
            const id = button.getAttribute("entity-id");
            const name = button.getAttribute("entity-name");
            selectedClientId = id;
            const p = dialog.querySelector("p");
            p.innerText = "Tem certeza que deseja remover o cliente " + name + "?";
            dialog.showModal();
        });
    });
}


window.onload = () => {

    prepareActionButton("search_options", "search_menu");
    prepareActionButton("more_actions", "actions_options");
    setupClearButton();
    setupSearchButton();
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

async function removeCustomer(customerId) {
    await remove(`${customerId}`);
}