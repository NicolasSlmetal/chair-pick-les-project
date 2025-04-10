import { configureSearch } from "../utils/configureSearch.js";
import { $ } from "../consts.js";
import { productCards } from "./consts.js"
import { countCart } from "../utils/countCart.js";


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
    $('.select2').select2({
        placeholder: "Selecione uma ou mais categorias",
        allowClear: true,
        width: "100%",
        language: {
            noResults: function (){

                return `Categoria não encontrada`;
            }

        },
        selectionCssClass: "multi_selected",

    })
    configureSearch();
});

countCart();