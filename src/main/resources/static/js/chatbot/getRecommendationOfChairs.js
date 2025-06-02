import { get} from "../http/api.js";

export async function getRecommendationOfChairs(prompt) {
    const response = await get(`chatbot/chairs?prompt=${prompt}`);

    return response;
}