export {};
// 가격에 쉼표를 추가하고 소수점을 제거하는 함수
export function formatPriceElement(priceElement) {
    const value = parseFloat(priceElement.textContent.replace('₩', '').replace(/,/g, ''));
    if (!isNaN(value)) {
        priceElement.textContent = `₩${value.toLocaleString('ko-KR')}`;
    }
}

export function formatPricesInDocument(selector) {
    const elements = document.querySelectorAll(selector);
    elements.forEach(el => {
        const value = parseFloat(el.dataset.price || el.textContent.replace('₩', '').replace(/,/g, ''));
        if (!isNaN(value)) {
            el.textContent = `₩${Math.round(value).toLocaleString('ko-KR')}`;
        }
    });
}
