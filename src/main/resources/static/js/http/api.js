export const API_URL = "https://90d0-2804-7f0-b800-fbf8-9fd-8786-9525-c75a.ngrok-free.app"

export async function get(endpoint, expectedStatus = 200) {
    const response = await fetch(`${API_URL}/${endpoint}`);
    return response;
}

export async function post(endpoint, body, expectedStatus = 201) {
    const response = await fetch(`${API_URL}/${endpoint}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body), 
    });
    return response;
}

export async function put(endpoint, body, expectedStatus = 200) {
    const response = await fetch(`${API_URL}/${endpoint}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
    });
    return response;
}

export async function httpDelete(endpoint, expectedStatus = 204) {
    const response = await fetch(`${API_URL}/${endpoint}`, {
        method: "DELETE",
    });
    return response;
}

export async function patch(endpoint, body, expectedStatus = 200) {
    const response = await fetch(`${API_URL}/${endpoint}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
    });
    return response;
}