import { $ } from "../consts.js";
import { get } from "../http/api.js";

const searchButton = document.querySelector(".home_search");
const searchInput = document.querySelector("#name_search");

searchButton.addEventListener("click", () => {
    const nameValue = searchInput.value;
    const data = {}
    if (nameValue) {
        data["name"] = nameValue;
    }
    const form = document.querySelector("form#main");
    if (form) {
        const formData = new FormData(form);
        const objectFormData = Object.fromEntries(formData);
        const nonEmptyFields = Object.keys(objectFormData)
        .filter(key => objectFormData[key] !== undefined && objectFormData[key] !== null && objectFormData[key])

        nonEmptyFields.forEach(key => data[key] = objectFormData[key]);
        if (data["min_rating"] == "0" && data["max_rating"] == "5") {
            delete data["min_rating"];
            delete data["max_rating"];
        }

        const categorySelector = document.querySelector("#category");
        if (categorySelector) {
            const categories = $("#category").select2("data")
            if (categories.length > 0) {
                 data["categories"] = categories.map(category => category.text).join(",")
            }
            delete data["category"];
        }
    }
    if (Object.keys(data).length > 0) {
        searchButton.href = `/search?${new URLSearchParams(data)}`;
    }
})

export function configureSearch() {

    $('#name_search').autocomplete({
        source: async function (request, response) {
            const term = request.term.toLowerCase();
            const url = `chairs/search?name=${term}&limit=4`
            const result = [];
            const responseAPI = await get(url);
            if (responseAPI.status == 200) {
                const body = await responseAPI.json();
                const entities = body.entitiesInPage;
                result.push(...entities.map(entity => ({
                    id: entity.id,
                    name: entity.name,
                    price: entity.price
                })))
            }
            if (result.length === 0) {
                result.push({
                    label: "Nenhum resultado encontrado",
                    notFound: true,
                });
            }

            response(result);
        },

        minLength: 1,
        html: true,
    }).data("ui-autocomplete")._renderItem = function (ul, item) {

        if (item.notFound) {
            return $("<li>")
                .append(item.label)
                .appendTo(ul);
        }

        let product = {
            id: item.id,
            image: `/images/chairs/${item.id}`,
            name: item.name,
            price: item.price.toLocaleString("pt-BR", {minimumFractionDigits: 2, maximumFractionDigits: 2})

        }

        return $("<li class='search_item'>")
            .append(`<img src="${product.image}" style="width: 50px; height: 50px; border-radius: 50%; margin-right: 10px;">${product.name} - R$ ${product.price}
        `)
            .on("click", function () {

                window.location.href = `/chairs/${product.id}`;
            })
            .hover(function () {
                $(this).css("background-color", "var(--blue-color)");
            }, function () {
                $(this).css("background-color", "white");
            })
            .css("cursor", "pointer")
            .css("display", "flex")
            .css("align-items", "center")
            .css("padding", "10px")
            .css("transition", "0.5s")
            .appendTo(ul);
    };
}
