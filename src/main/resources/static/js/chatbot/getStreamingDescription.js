import { get} from "../http/api.js";

export async function getStreamingDescription(prompt, chairId) {
    const response = await get(`chatbot/chairs/description?prompt=${prompt}&chairId=${chairId}`);

    return response;
}