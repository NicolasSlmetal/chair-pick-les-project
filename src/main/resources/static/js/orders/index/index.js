
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
            if (menu.style.display !== "none") {
                actionButton.classList.add("disabled")
            } else {
                actionButton.classList.remove("disabled");
            }
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
        const actionButton = menu.parentElement.querySelector("img#actions_button");
        if (actionButton) {
            actionButton.classList.remove("disabled");
        }
    });
}
