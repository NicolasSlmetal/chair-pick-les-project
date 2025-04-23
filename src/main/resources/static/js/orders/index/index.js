
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
