
let timeoutId;
export function debounce(func, delay) {

    return async function() {
        if (timeoutId) {
            clearTimeout(timeoutId);
        }
        timeoutId = setTimeout(async () => {
            await func.apply(this);
        }, delay);
    }
}