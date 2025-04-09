import { $ } from "../consts.js";

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
    }
    if (Object.keys(data).length > 0) {
        searchButton.href = `/search?${new URLSearchParams(data)}`;
    }
})

export function configureSearch() {
    var products = [
        {
            name: "Cadeira Gamer",
            price: "R$ 600,00",
            image: "https://a-static.mlcdn.com.br/1500x1500/cadeira-gamer-otello-preto-e-vermelho/magazineluiza/237019000/b3fc5faf334ec8354d4177f54df4e87a.jpg"
        },
        {
            name: "Cadeira de escritório",
            price: "R$ 400,00",
            image: "https://dcf83otphg8a2.cloudfront.net/Custom/Content/Products/98/88/988852_cadeira-escritorio-presidente-base-cromada-preta-mb-c730-20028_m1_637626398061654943.webp"
        },
        {
            name: "Cadeira de praia",
            price: "R$ 700,00",
            image: "https://minipreco.vtexassets.com/arquivos/ids/186643/83087b82-7358-4a8f-a923-d81191e74efd.jpg?v=638289344578430000"
        },
        {
            name: "Cadeira de escritório",
            price: "R$ 200,00",
            image: "https://moveissaara.com.br/wp-content/uploads/2018/09/cadeira-auditorio-saara-001-1.jpg"
        },
        {
            name: "Cadeira de escritório",
            price: "R$ 500,00",
            image: "https://www.longarinasmetalicas.com/assets/img/produtos/produto-28-cadeira-para-escritorio-elite-flex-plus-presidente-com-encosto-de-cabeca-assento-estofado-encosto-em-tela-cadeiras-home--239631.jpg"
        }
    ];
    $('#name_search').autocomplete({
        source: function (request, response) {
            const term = request.term.toLowerCase();
            const filtered = products.filter(product => product.name.toLowerCase().includes(term));
            const priceFiltered = products.filter(product => product.price.toLowerCase().includes(term));
            const result = [...filtered, ...priceFiltered];
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

        var product = products.find(product => product.name === item.label || product.price === item.price);

        return $("<li class='search_item'>")
            .append(`<img src="${product.image}" style="width: 50px; height: 50px; border-radius: 50%; margin-right: 10px;">${product.name} - ${product.price}
        `)
            .on("click", function () {
                if (window.location.href.includes("logged")) {
                    window.location.href = "product-logged.html";
                    return;
                }
                window.location.href = "product.html";
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
