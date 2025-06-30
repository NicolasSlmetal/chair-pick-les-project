import { configureSearch } from "../utils/configureSearch.js";
import { $ } from "../consts.js";
import { productCards } from "./consts.js"
import { countCart } from "../utils/countCart.js";
import { configureCategoriesProperties } from "./configureCategoriesProperties.js";

const paginationGlobal = $.fn.pagination;
const form = document.querySelector("form");
form.addEventListener("submit", (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    console.log(data);
});

const nameSearchInput = document.querySelector("#name_search");

const searchButton = document.querySelector("#search");
searchButton.addEventListener("click", () => {
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    const name = nameSearchInput.value;
    if (name) {
        data["name"] = name;
    }
    const nonEmptyKeys = Object.keys(data).filter(key => data[key] !== undefined && data[key] !== "" && data[key] !== null);
    const filteredObject = {}
    nonEmptyKeys.forEach(key => filteredObject[key] = data[key]);
    if (filteredObject["min_rating"] == "0" && filteredObject["max_rating"] == "5") {
        delete filteredObject["min_rating"];
        delete filteredObject["max_rating"];
    }
    const selectedCategories = $("#category").select2('data');
    if (selectedCategories && selectedCategories.length > 0) {
        filteredObject["categories"] = selectedCategories.map(cat => cat.text).join(",");

    }
    delete filteredObject["category"]

    if (Object.keys(filteredObject).length > 0) {
        window.location.href = `/search?${new URLSearchParams(filteredObject)}`;
        return;
    }
    window.location.href = `/search`;
});

const cleanButton = document.querySelector("#clean");
cleanButton.addEventListener("click", () => {
    form.reset();
    $("#category").val(null).trigger('change');
    const min_rating = document.querySelector("#range_min_rating");
    const input_min_rating = document.querySelector("#min_rating");
    const input_max_rating = document.querySelector("#max_rating");
    const max_rating = document.querySelector("#range_max_rating");
    min_rating.innerHTML = `Mínimo: ${input_min_rating.value}`;
    max_rating.innerHTML = `Máximo: ${input_max_rating.value}`;
});

$(document).ready(function() {

    $("#products_pagination_container").pagination({
        dataSource: "/chairs/search",
        pageSize: 8,

        locator: 'entitiesInPage',
        alias: {
            pageNumber: 'page',
            pageSize: 'limit',
        },
        pageNumber: 1,
        totalNumberLocator: function(response) {
            return response.totalResults;
        },
        style: {
            className: "pagination"
        },
        callback: function(data, pagination) {
            const container = $(".products__container");
            container.empty();
            if (data.length === 0) {
                container.append("<p>Nenhum produto encontrado.</p>");
                return;
            }
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
    })

    configureCategoriesProperties();
    configureSearch();
});

countCart();