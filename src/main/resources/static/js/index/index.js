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

const searchButton = document.querySelector("#search");
searchButton.addEventListener("click", () => {
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    window.location.href = `search.html?${new URLSearchParams(data)}`;
});

const cleanButton = document.querySelector("#clean");
cleanButton.addEventListener("click", () => {
    form.reset();
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