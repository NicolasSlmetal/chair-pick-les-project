import { post } from "../http/api.js";

export async function postRequestResetPassword(email) {
    const response = await post("login/request-reset-password", email , 200);
    return response;
}