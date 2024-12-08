import time
import urllib.parse
import sys

from selenium.common import NoSuchElementException
from selenium.webdriver.support.wait import WebDriverWait
import json
from selenium import webdriver
from selenium.webdriver import Keys
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
import pandas as pd
import requests


results = []
product_info = dict()

def infinite_scroll(driver):
    # 무한 스크롤
    before_h = driver.execute_script('return window.scrollY')
    while True:
        driver.find_element(By.CSS_SELECTOR, 'body').send_keys(Keys.END)
        time.sleep(2)
        after_h = driver.execute_script('return window.scrollY')

        if after_h == before_h:
            break
        before_h = after_h


def save_dict(name, str_price, int_price, link, image_src):
    product_info = {
        "name": name,
        "price": str_price,
        "int_price": int_price,
        "link": link,
        "image_src": image_src
    }
    results.append(product_info)


def save_file():
    # JSON 파일로 저장
    try:
        file_name = "C:/Users/admin/Desktop/FindLP_Project/output.json"
        with open(file_name, "w", encoding="utf-8") as f:
            json.dump(results, f, ensure_ascii=False, indent=4)
    except Exception as e:
        print(f"json 파일 저장 중 오류가 발생했습니다: {e}")

    # results 데이터프레임 생성
    df = pd.DataFrame(results)


def danawa_crawling():
    dananwa_url = "https://www.danawa.com/"
    options = Options()
    options.headless = True
    options.add_experimental_option("detach", True)
    # options.add_argument("--headless")
    driver = webdriver.Chrome(options=options)
    driver.get(dananwa_url)
    search_shopping = driver.find_element(By.CSS_SELECTOR, '#AKCSearch')
    search_shopping.click()
    search_shopping.send_keys(query)
    search_shopping.send_keys(Keys.ENTER)
    query_encoded = urllib.parse.quote_plus(query)
    page_url = f"https://search.danawa.com/dsearch.php?query={query_encoded}&originalQuery={query_encoded}&checkedInfo=N&volumeType=allvs&page=1&limit=40&sort=saveDESC&list=list&boost=true&tab=goods&addDelivery=N&coupangMemberSort=&mode=simple&isInitTireSmartFinder=N&recommendedSort=N&defaultUICategoryCode=10248969&defaultPhysicsCategoryCode=224%7C49729%7C49740%7C0&defaultVmTab=11412&defaultVaTab=2376060&isZeroPrice=Y&quickProductYN=N&priceUnitSort=N&priceUnitSortOrder=A"
    driver.get(page_url)
    #time.sleep(3)  # 페이지가 완전히 로드될 때까지 대기 (선택적)

    infinite_scroll(driver)     # 무한 스크롤 처리

    # danawa 상품 정보 태그
    items = driver.find_elements(By.CSS_SELECTOR, '.main_prodlist .prod_main_info')

    for item in items:
        # if(item.find_element((By.CSS_SELECTOR, '')))
        name = item.find_element(By.CSS_SELECTOR, '.main_prodlist .prod_main_info .prod_name a').text

        try:
            str_price = item.find_element(By.CSS_SELECTOR, '.main_prodlist .prod_main_info .price_sect strong').text + "원"
            tmp_price = item.find_element(By.CSS_SELECTOR, '.main_prodlist .prod_main_info .price_sect strong').text
            int_price = int(tmp_price.replace(',', ''))
        except:
            pass  # 예외가 발생하면 int_price는 그대로 빈 문자열로 남게 됨
        link = item.find_element(By.CSS_SELECTOR, '.thumb_image > a').get_attribute('href')

        # 각 상품 별 이미지 가져오기 (CSS Selector 사용)
        try:
            image_src = item.find_element(By.CSS_SELECTOR, '.thumb_image > a > img').get_attribute('src')
        except NoSuchElementException:
            image_src = '이미지 없음'

        save_dict(name, str_price, int_price, link, image_src)
        save_file()

    return driver

# 프로그램 start
query = sys.argv[1]

danawa_driver = danawa_crawling()

danawa_driver.quit()
