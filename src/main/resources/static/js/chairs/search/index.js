import { configureSearch} from "../../utils/configureSearch.js";
import { searchChairs } from "./searchChairs.js";
import { buildChairsCards } from "./buildChairsCards.js";

const resultsInfo = document.querySelector("#results");
const section = document.querySelector(".products.search");

export async function getAndBuildChairs() {
    const urlParams = new URLSearchParams(window.location.search);

    const availableParams = {
        "min_price": urlParams.get("min_price"),
        "max_price": urlParams.get("max_price"),
        "min_length": urlParams.get("min_length"),
        "max_length": urlParams.get("max_length"),
        "min_width": urlParams.get("min_width"),
        "max_width": urlParams.get("max_width"),
        "min_height": urlParams.get("min_height"),
        "max_height": urlParams.get("max_height"),
        "min_rating": urlParams.get("min_rating"),
        "max_rating": urlParams.get("max_rating"),
        "name": urlParams.get("name"),
    }

    const providedParams = Object.fromEntries(Object.entries(availableParams).filter(([key, value]) => value !== null && value !== ""));
    providedParams["page"] = 1;
    providedParams["limit"] = 2;

    await fetchChairs(providedParams);
}

let fetchedResults = 0;
async function fetchChairs(data) {
    const response = await searchChairs(data);

    if (response === null) {
        console.log("Error fetching chairs");
        return;
    }

    buildChairsCards(response);

    fetchedResults += response.entitiesInPage.length;
    resultsInfo.innerHTML = `<strong>Resultados encontrados:</strong> ${response.totalResults}`;
    if (response.totalResults > fetchedResults) {
        const button = document.createElement("button");
        button.classList.add("action__button");
        button.addEventListener("click", async () => {
            data["page"] = parseInt(data["page"]) + 1;
            await fetchChairs(data);
            button.remove();
        });
        button.innerText = "Carregar mais";
        section.appendChild(button);
    }
}

window.onload = async () => {
    await getAndBuildChairs();

}