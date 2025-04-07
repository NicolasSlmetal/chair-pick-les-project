import { post } from "../http/api.js";

export async function postLogin(data) {
    const body = await post("login", data, 200);
    if (body.status !== 200) {
        return;
    }

    const json = await body.json();
    if (json.role == "ADMIN") {
        window.location.href = "/admin/customers";
    }

    const lastActivity = window.history;
    if (!lastActivity) {
        window.location.href = "/";
        return;
    }

    lastActivity.back();

}