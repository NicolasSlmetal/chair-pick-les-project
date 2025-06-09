const activateOptions = document.querySelectorAll(".option.success");
const deactivateOptions = document.querySelectorAll(".option.danger");
const statusChangeDialog = document.querySelector("#status_change_dialog");
const statusChangeDialogCancelButton = statusChangeDialog.querySelector("#cancel_button");
statusChangeDialogCancelButton.onclick = () => {
    statusChangeDialog.close();
}
const statusChangeDialogConfirmButton = statusChangeDialog.querySelector("#confirm_button");


activateOptions.forEach((option) => {
    option.addEventListener("click", () => {
        const row = option.closest("tr");
        const id = row.querySelector("input[name='product_id'").value
        const name = row.querySelector("td:nth-child(1)").innerText;
        const errorMessage = statusChangeDialog.querySelector("p#error");
        errorMessage.innerText = "";
        const action = () => {
            const reason = statusChangeDialog.querySelector("input[name='reason']").value;
            if (!reason) {
                errorMessage.innerText = "Por favor, informe o motivo da ativação";
                return;
            }
            errorMessage.innerText = "";
            console.log(`Activating product ${id}`);
        }
        const p = statusChangeDialog.querySelector("p#message");
        p.innerText = `Ativação do produto ${name}`;
        statusChangeDialog.showModal();
        statusChangeDialogConfirmButton.onclick = action;

    });
});

deactivateOptions.forEach((option) => {
    option.addEventListener("click", () => {
        const row = option.closest("tr");
        const id = row.querySelector("input[name='product_id'").value
        const name = row.querySelector("td:nth-child(1)").innerText;
        const errorMessage = statusChangeDialog.querySelector("p#error");
        errorMessage.innerText = "";
        const action = () => {
            const reason = statusChangeDialog.querySelector("input[name='reason']").value;
            if (!reason) {
                errorMessage.innerText = "Por favor, informe o motivo da desativação";
                return;
            }
            errorMessage.innerText = "";
            console.log(`Deactivating product ${id}`);
        }
        const p = statusChangeDialog.querySelector("p#message");
        p.innerText = `Desativação do produto ${name}`;
        statusChangeDialog.showModal();
        statusChangeDialogConfirmButton.onclick = action;

    });

});

function prepareActionButton(id = "", menuId) {
    const mapSwitchDisplay = {
        "none": "flex",
        "flex": "none"
    }
    const actionsButtons = document.querySelectorAll(`main img${id.length > 1 ? `.${id}` : ""}`);
    actionsButtons.forEach((actionButton) => {
        const parent = actionButton.parentElement;
        const menu = parent.querySelector(`.${menuId}`);
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

    prepareActionButton("actions_button", "menu");
    document.addEventListener("click", (event) => {
        const isMenu = event.target.closest("div.menu");
        const isActionButton = event.target.closest("img.actions_button");
        if (!isMenu && !isActionButton) {
            removeMenuOnClickOutside("menu");
        }
    });
};

function removeMenuOnClickOutside(id) {
    const menu = document.querySelectorAll(`main .${id}`);
    menu.forEach((menu) => {
        menu.style.display = "none";
    });
}
