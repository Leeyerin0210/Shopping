import os
import time
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
import re
import sys
import json 


def setup_driver():
    options = webdriver.ChromeOptions()
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.add_argument(
        'user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    )

    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    return driver

def save_to_sql_file(product_data, filename="products.sql"):
    """SQL 파일 생성 - 제품 데이터를 JSON으로 저장"""
    added_products = set()

    with open(filename, "w", encoding="utf-8") as file:
        file.write("-- SQL 명령어 파일\n")
        file.write("-- products 테이블에 데이터 삽입\n\n")

        for product in product_data:
            # 제품 중복 확인
            if product['id'] in added_products:
                print(f"[INFO] 이미 추가된 제품 ID: {product['id']} - {product['name']} 건너뜀.")
                continue

            # JSON 배열로 상세 이미지 데이터를 변환
            detail_images_json = json.dumps(product['detail_images'], ensure_ascii=False)

            product_insert = f"""
INSERT INTO product (id, name, price, main_img, detail_imgs)
VALUES (
    {product['id']},
    '{product['name'].replace("'", "''")}',
    {float(product['price'].replace(",", "").replace("원", "").strip())},
    '{product['thumbnail']}',
    '{detail_images_json.replace("'", "''")}'
);
"""
            file.write(product_insert)
            added_products.add(product['id'])

    print(f"[INFO] SQL 파일 생성 완료: {filename}")


def extract_detail_images_selenium(driver, detail_url):
    """Selenium을 사용하여 상세 페이지에서 이미지 URL 추출"""
    print(f"[INFO] 상세 페이지로 이동 중: {detail_url}")
    driver.get(detail_url)

    try:
        # 상세 페이지 로드 대기
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "product-detail-content-inside"))
        )
        print("[INFO] 상세 페이지 로드 완료.")

        # "더보기" 버튼 클릭
        try:
            see_more_button = WebDriverWait(driver, 5).until(
                EC.element_to_be_clickable((By.CLASS_NAME, "product-detail-seemore-btn"))
            )
            see_more_button.click()
            print("[INFO] '더보기' 버튼 클릭 성공.")
        except Exception as e:
            print(f"[WARN] '더보기' 버튼 클릭 실패 또는 버튼 없음: {str(e)}")

        # product-detail-content-inside 내 모든 이미지 추출
        content_inside = driver.find_element(By.CLASS_NAME, "product-detail-content-inside")

        # 모든 <img> 태그 선택
        image_elements = content_inside.find_elements(By.TAG_NAME, "img")

        image_urls = []
        for img in image_elements:
            src = img.get_attribute("src")
            if src:
                # 상대 경로 처리
                if src.startswith("https://"):
                    image_urls.append(src)
                elif src.startswith("//"):
                    image_urls.append("https:" + src)

        if not image_urls:
            print("[WARN] 상세 이미지가 없습니다. 이 제품은 제외됩니다.")
            return None  # 이미지가 없으면 None 반환

        print(f"[INFO] 총 {len(image_urls)}개의 이미지 URL을 수집했습니다.")
        return image_urls
    except Exception as e:
        print(f"[ERROR] 상세 페이지 크롤링 중 오류 발생: {str(e)}")
        return None  # 오류 발생 시 None 반환


def extract_product_id(link):
    """상세 페이지 링크에서 ID 추출"""
    match = re.search(r"/products/(\d+)", link)
    if match:
        return int(match.group(1))
    print(f"[WARN] 비정상적인 링크: {link}")
    return None


def search_coupang(driver, keyword):
    """검색 결과 목록 크롤링"""
    driver.get("https://www.coupang.com/")

    try:
        # 팝업 닫기
        try:
            close_button = WebDriverWait(driver, 5).until(
                EC.element_to_be_clickable((By.CLASS_NAME, "close"))
            )
            close_button.click()
        except:
            pass

        # 검색어 입력
        search_box = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.NAME, "q"))
        )
        search_box.send_keys(keyword)
        search_box.send_keys("\n")

        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, "search-product"))
        )

        print("[INFO] 페이지 스크롤 중...")
        last_height = driver.execute_script("return document.body.scrollHeight")
        while True:
            driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(2)
            new_height = driver.execute_script("return document.body.scrollHeight")
            if new_height == last_height:
                break
            last_height = new_height
        print("[INFO] 스크롤 완료. 모든 제품 로드.")

        products = driver.find_elements(By.CLASS_NAME, "search-product")
        print(f"[INFO] 총 {len(products)}개의 제품 발견.")

        results = []
        for idx, product in enumerate(products, 1):
            try:
                item = {}
                name_element = product.find_elements(By.CLASS_NAME, "name")
                if not name_element or not name_element[0].text.strip():
                    continue
                item['name'] = name_element[0].text.strip()

                price_element = product.find_elements(By.CLASS_NAME, "price-value")
                if not price_element or not price_element[0].text.strip():
                    continue
                item['price'] = price_element[0].text.strip()

                thumbnail_element = product.find_elements(By.CLASS_NAME, "search-product-wrap-img")
                if not thumbnail_element or not thumbnail_element[0].get_attribute("src"):
                    continue
                src = thumbnail_element[0].get_attribute("src")
                item['thumbnail'] = "https:" + src if src.startswith("//") else src

                ahref = product.find_elements(By.TAG_NAME, 'a')
                if not ahref:
                    continue
                item['link'] = ahref[0].get_attribute('href')

                # ID 추출 및 검증
                product_id = extract_product_id(item['link'])
                if not product_id:
                    print(f"[WARN] 유효하지 않은 제품 건너뜀: {item['name']}")
                    continue
                item['id'] = product_id

                results.append(item)
                print(f"[INFO] 제품 {idx}:\n  ID: {item['id']}\n  이름: {item['name']}\n  가격: {item['price']}")
            except Exception as e:
                print(f"[ERROR] 제품 {idx} 처리 중 오류 발생: {str(e)}")
                continue

        return results
    except Exception as e:
        print(f"[ERROR] 검색 결과 크롤링 중 오류 발생: {str(e)}")
        return []


def main():
    driver = setup_driver()

    keyword = input("검색할 키워드를 입력하세요: ")
    results = search_coupang(driver, keyword)

    if not results:
        print("[ERROR] 검색 결과가 없습니다.")
        driver.quit()
        return

    # 상세 페이지에서 이미지 가져오기
    valid_results = []
    for product in results:
        print(f"[INFO] '{product['name']}' 상세 페이지에서 이미지 크롤링 시작...")
        detail_images = extract_detail_images_selenium(driver, product['link'])

        if detail_images is None:
            print(f"[INFO] '{product['name']}'는 이미지가 없어 제외됩니다.")
            continue

        product['detail_images'] = detail_images
        valid_results.append(product)

    # SQL 파일 생성 (이미지가 있는 제품만 포함)
    if valid_results:
        save_to_sql_file(valid_results)
    else:
        print("[INFO] 유효한 제품이 없어 SQL 파일을 생성하지 않습니다.")

    input("[INFO] 모든 작업이 완료되었습니다. 브라우저를 닫으려면 Enter 키를 누르세요.")
    driver.quit()


if __name__ == "__main__":
    main()
