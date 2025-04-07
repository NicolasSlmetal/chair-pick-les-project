const actionsButtonDiv = document.querySelector("#action__product");

export function renderDefaultOptions() {
    const buyButton = document.createElement("button");
    buyButton.id = "buy";
    buyButton.classList.add("product_action")
    buyButton.innerText = "Comprar";
    const addToCartButton = document.createElement("button");
    addToCartButton.id = "add_to_cart";
    addToCartButton.classList.add("product_action")
    addToCartButton.innerText = "Adicionar ao carrinho";

    actionsButtonDiv.appendChild(buyButton);
    actionsButtonDiv.appendChild(addToCartButton);
}

export function renderOptionsForProductInCart() {
    const p = "Você já possui este produto no carrinho";
    const pElement = document.createElement("p");
    pElement.innerText = p;

    const continueShoppingButton = document.createElement("button");
    continueShoppingButton.id = "continue_shopping";
    continueShoppingButton.classList.add("product_action")
    continueShoppingButton.innerText = "Ir para o carrinho";

    actionsButtonDiv.appendChild(pElement);
    actionsButtonDiv.appendChild(continueShoppingButton);
}
