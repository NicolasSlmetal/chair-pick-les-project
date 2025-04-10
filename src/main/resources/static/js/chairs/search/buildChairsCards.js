const section = document.querySelector(".products.search");

export function buildChairsCards(response) {
    const chairs = response.entitiesInPage;
    for (const chair of chairs) {
        const card = document.createElement("a");
        card.classList.add("product_card");
        card.href = `/chairs/${chair.id}`;
        const image = document.createElement("img");
        image.src = `/images/chairs/${chair.id}`;
        image.alt = chair.name;
        image.classList.add("product_image");
        image.classList.add("search");
        card.appendChild(image);
        const div = document.createElement("div");
        div.classList.add("container_search");
        const name = document.createElement("h1");
        name.innerText = chair.name;
        div.appendChild(name);
        const price = document.createElement("h2");
        price.innerText = `R$ ${chair.price.toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
        div.appendChild(price);
        card.appendChild(div);
        section.appendChild(card);
    }
}