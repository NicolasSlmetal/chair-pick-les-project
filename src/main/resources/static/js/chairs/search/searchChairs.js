

export async function searchChairs(data){
    const searchParams = new URLSearchParams(data);
    const endpoint = `chairs/search?${searchParams.toString()}`;
    const response = await fetch(endpoint);
    if (response.status !== 200) {
        return null;
    }
    const chairs = await response.json();
    return chairs;

}