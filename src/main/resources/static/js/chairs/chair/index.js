import { configureSearch } from "../../utils/configureSearch.js";
import { countCart } from "../../utils/countCart.js";
import { addItemToCartPost } from "./addItemToCartPost.js";
import { verifyIfProductIsInCart } from "./verifyIfProductIsInCart.js";


const chairId = document.querySelector("#chair_id").value;
const customerId = document.querySelector("#authenticated_customer_id");


await verifyIfProductIsInCart(chairId, customerId);

const idButtonActionMap = {
    "buy": async () => {
        if (!customerId) {
            window.location.href = "/login";
            return;
        }
        const customerIdValue = customerId.value;
        const payload = {
            customerId: customerIdValue,
            chairId: chairId
        }
        const response = await addItemToCartPost(payload);
        if (response.status !== 201) {
            return;
        }
        window.location.href = `/customers/${customerIdValue}/cart/confirm`;
    },
    "add_to_cart": async () => {
        if (!customerId) {
            window.location.href = "/login";
            return;
        }
        const customerIdValue = customerId.value;
        const payload = {
            customerId: customerIdValue,
            chairId: chairId
        }
        const response = await addItemToCartPost(payload);
        if (response.status !== 201) {
            return;
        }
        window.location.href = `/customers/${customerIdValue}/cart`;
    },
    "continue_shopping": () => {
        if (!customerId) {
            window.location.href = "/login";
            return;
        }
        const customerIdValue = customerId.value;
        window.location.href = `/customers/${customerIdValue}/cart`;
    }
}

const productActions = document.querySelector("#action__product");
const buttons = productActions.querySelectorAll("button");

buttons.forEach(button => {
    if (idButtonActionMap.hasOwnProperty(button.id)) {
        button.addEventListener("click", idButtonActionMap[button.id]);
    }
});

configureSearch();

countCart();