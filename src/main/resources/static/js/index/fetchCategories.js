import { get } from "../http/api.js";

export async function fetchCategories() {
    const response = await get("categories");
    return response;
}