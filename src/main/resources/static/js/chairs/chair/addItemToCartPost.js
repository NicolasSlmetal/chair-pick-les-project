import { post } from "../../http/api.js";

export async function addItemToCartPost(payload) {
    const response = await post(`customers/${payload.customerId}/cart`, payload.chairId);
    return response;
}