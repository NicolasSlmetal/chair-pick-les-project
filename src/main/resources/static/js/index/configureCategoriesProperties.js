import { fetchCategories } from "./fetchCategories.js";
import { $ } from "../consts.js";
import { getPaginationStyles } from "./getPaginationStyles.js"

const categoriesWithProducts = new Set();
const divProductsByCategory = $(".all_products_by_category");

export async function configureCategoriesProperties() {
    const response = await fetchCategories();

    if (response.status === 200) {
        const categories = await response.json();
        let completedCategories = 0;
        const totalCategories = categories.length;

        categories.forEach(category => {
            const categorySection = document.createElement("section");
            categorySection.classList.add("category_container");
            const categoryTitle = document.createElement("h2");
            categoryTitle.textContent = `Linha ${category.name}`;
            categorySection.appendChild(categoryTitle);

            const rowSection = document.createElement("section");
            rowSection.classList.add("row", "bg_second", "category_" + category.id);

            const paginationContainer = document.createElement("div");
            const paginationClass = "pagination_" + category.id;
            paginationContainer.classList.add(paginationClass, "bg_second", "products_pagination_container");

            categorySection.appendChild(rowSection);
            categorySection.appendChild(paginationContainer);
            const hr = document.createElement("hr");
            categorySection.appendChild(hr);

            divProductsByCategory.append(categorySection);

            $("." + paginationClass).pagination({
                dataSource: `/chairs/search?categories=${category.name}`,
                pageSize: 4,
                locator: 'entitiesInPage',
                alias: {
                    pageNumber: 'page',
                    pageSize: 'limit',
                },
                pageNumber: 1,
                totalNumberLocator: response => response.totalResults,
                ...getPaginationStyles(),

                callback: function (data) {
                    completedCategories++;

                    if (data.length === 0) {
                        categorySection.remove();
                    } else {
                        categoriesWithProducts.add(category.name);

                        const container = $(".category_" + category.id);
                        container.empty();

                        data.forEach(product => {
                            const cardAnchor = document.createElement("a");
                            cardAnchor.classList.add("product_card");
                            cardAnchor.href = `/chairs/${product.id}`;

                            const img = document.createElement("img");
                            img.src = `/images/chairs/${product.id}`;
                            img.alt = product.name;
                            img.classList.add("product_image");
                            cardAnchor.appendChild(img);

                            const title = document.createElement("h3");
                            title.innerText = product.name;
                            cardAnchor.appendChild(title);

                            const price = document.createElement("p");
                            price.innerText = `R$ ${product.price.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
                            cardAnchor.appendChild(price);

                            container.append(cardAnchor);
                        });
                    }


                    if (completedCategories === totalCategories) {
                        $('.select2').select2({
                            placeholder: "Selecione uma ou mais categorias",
                            allowClear: true,
                            width: "100%",
                            language: {
                                noResults: () => `Categoria não encontrada`
                            },
                            data: Array.from(categoriesWithProducts).map(category => ({
                                id: category,
                                text: category
                            })),
                            selectionCssClass: "multi_selected"
                        });
                    }
                }
            });
        });
    }
}
