export function createModal(titleText, messageText) {
    const dialog = document.createElement("dialog");
    dialog.setAttribute("id", "advice-modal");
    dialog.setAttribute("class", "large_dialog");
    const title = document.createElement("h2");
    title.innerText = titleText;
    dialog.appendChild(title);
    const message = document.createElement("p");
    dialog.appendChild(message);
    message.innerText = messageText;

    const button = document.createElement("button");
    button.innerText = "Ok";
    button.setAttribute("class", "action__button");
    button.addEventListener("click", async () => {
        dialog.close();
    });
    dialog.appendChild(button);
    document.body.appendChild(dialog);
    dialog.showModal();

}