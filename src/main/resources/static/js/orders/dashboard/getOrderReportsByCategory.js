import { get } from "../../http/api.js"


export async function getOrderReportsByCategory(startDate, endDate) {
    const response = await get(`admin/categories/reports?startDate=${startDate}&endDate=${endDate}`);
    return response;
}