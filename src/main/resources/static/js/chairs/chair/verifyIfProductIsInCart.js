import { get } from "../../http/api.js";
import {renderDefaultOptions, renderOptionsForProductInCart } from "./buttonOptionsRender.js";

export async function verifyIfProductIsInCart(chairId, customerId) {
    if (!customerId) {
        renderDefaultOptions();
        return;
    }
    const payload = {
        chairId: chairId,
        customerId: customerId.value
    }
    try{

        const response = await get(`customers/${payload.customerId}/cart/chair/${payload.chairId}`);
        if (response.status === 403) {
            renderDefaultOptions();
            return;
        }
        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("text/html")) {
            renderDefaultOptions();
            return;
        }

        if (response.status === 200 && contentType.includes("application/json")) {
            console.log({response});
            const json = await response.json();
            if (json.length === 0) {
                renderDefaultOptions();
                return;
            }
            renderOptionsForProductInCart();
            return;
        }

        if (response.status !== 200) {
            renderDefaultOptions();
            return;
        }
    } catch (error) {
        console.log("Error verifying if product is in cart", error);
        renderDefaultOptions();
    }
}