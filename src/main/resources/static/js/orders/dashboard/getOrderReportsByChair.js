import { get } from "../../http/api.js"


export async function getOrderReportsByChair(startDate, endDate) {
    const response = await get(`admin/orders/reports?startDate=${startDate}&endDate=${endDate}`);
    return response;
}