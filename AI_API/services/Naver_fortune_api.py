from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime
from typing import Literal

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from selenium import webdriver
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager

BASE_URL = (
    "https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&ssc=tab.nx.all&"
    "query=%EC%98%A4%EB%8A%98%EC%9D%98+%EC%9A%B4%EC%84%B8"
)

Gender = Literal["m", "f"]
CalendarType = Literal["solar", "lunarGeneral", "lunarLeap"]

SELECTOR_GENDER = (
    "#fortune_birthCondition > div.tb_box > div.system_select_box > div.gender._togglePanelSelect"
)
SELECTOR_CALENDAR = (
    "#fortune_birthCondition > div.tb_box > div.system_select_box > div.year._togglePanelSelect"
)
SELECTOR_DATE_TRIGGER = (
    "#fortune_birthCondition > div.tb_box > div.pop_select_box._dateCustomSelect > a > span > "
    "span.birth_date._trigger_text"
)
VISIBLE_DATE_POPUP = "div.select_box._root[aria-hidden='false']"
SELECTOR_SUBMIT = "#fortune_birthCondition > div.tb_box > button"
RESULT_PANEL = "#fortune_birthResult > div:nth-child(3) > dl:nth-child(3)"


class FortuneRequest(BaseModel):
    gender: Gender = Field(description='"m"=남성, "f"=여성')
    calendar_type: CalendarType = Field(
        description='"solar"=양력, "lunarGeneral"=음력 평달, "lunarLeap"=음력 윤달'
    )
    birth_date: str = Field(description="YYYY-MM-DD 형식의 생년월일")
    headless: bool = Field(default=True, description="브라우저 창 표시 여부")
    wait_seconds: int = Field(default=10, ge=1, le=30, description="요소 탐색 최대 대기 시간(초)")


@dataclass
class FortuneResult:
    title: str
    date_text: str
    summary: str


def parse_birth_date(date_text: str) -> tuple[int, int, int]:
    try:
        parsed = datetime.strptime(date_text, "%Y-%m-%d").date()
    except ValueError as exc:
        raise ValueError("생년월일은 YYYY-MM-DD 형식이어야 합니다.") from exc

    if parsed.year < 1940:
        raise ValueError("네이버 위젯은 1940년 이후 생년월일만 지원합니다.")

    return parsed.year, parsed.month, parsed.day


def build_driver(*, headless: bool) -> webdriver.Chrome:
    options = webdriver.ChromeOptions()
    if headless:
        options.add_argument("--headless=new")
    options.add_argument("--disable-gpu")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1280,900")

    service = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=service, options=options)


def click_dropdown_option(
    driver: webdriver.Chrome,
    wait: WebDriverWait,
    container_selector: str,
    option_value: str,
) -> None:
    trigger_selector = f"{container_selector} > div > a"
    trigger = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, trigger_selector)))
    driver.execute_script("arguments[0].scrollIntoView({block: 'center'});", trigger)
    trigger.click()

    option_selector = f"{container_selector} ul.group_list li[data-kgs-option='{option_value}']"
    option = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, option_selector)))
    driver.execute_script("arguments[0].scrollIntoView({block: 'center'});", option)
    option.click()


def pick_birth_date(
    driver: webdriver.Chrome,
    wait: WebDriverWait,
    year: int,
    month: int,
    day: int,
) -> None:
    trigger = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, SELECTOR_DATE_TRIGGER)))
    driver.execute_script("arguments[0].scrollIntoView({block: 'center'});", trigger)
    trigger.click()
    popup = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, VISIBLE_DATE_POPUP)))

    groups = popup.find_elements(By.CSS_SELECTOR, "div.group_select._list_root")
    if len(groups) < 3:
        raise RuntimeError("날짜 선택 UI를 찾지 못했습니다.")

    for value, group in zip((year, month, day), groups[:3]):
        selector = f"li[data-value='{value}'] a"
        option = group.find_element(By.CSS_SELECTOR, selector)
        driver.execute_script("arguments[0].scrollIntoView({block: 'center'});", option)
        option.click()

    driver.find_element(By.TAG_NAME, "body").send_keys(Keys.ESCAPE)
    try:
        wait.until(EC.invisibility_of_element_located((By.CSS_SELECTOR, VISIBLE_DATE_POPUP)))
    except TimeoutException:
        driver.execute_script(
            "arguments[0].setAttribute('aria-hidden', 'true'); arguments[0].style.display='none';",
            popup,
        )


def click_button(driver: webdriver.Chrome, wait: WebDriverWait, selector: str) -> None:
    button = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, selector)))
    driver.execute_script("arguments[0].scrollIntoView({block: 'center'});", button)
    button.click()


def extract_result(wait: WebDriverWait) -> FortuneResult:
    panel = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, RESULT_PANEL)))
    title = panel.find_element(By.CSS_SELECTOR, "strong").text.strip()
    date_text = panel.find_element(By.CSS_SELECTOR, "span.result_date").text.strip()
    summary = panel.find_element(By.CSS_SELECTOR, "p").text.strip()
    return FortuneResult(title=title, date_text=date_text, summary=summary)


def fetch_today_fortune(
    gender: Gender,
    calendar_type: CalendarType,
    birth_date: str,
    *,
    headless: bool = True,
    wait_seconds: int = 10,
) -> FortuneResult:
    year, month, day = parse_birth_date(birth_date)

    driver = build_driver(headless=headless)
    wait = WebDriverWait(driver, wait_seconds)

    try:
        driver.get(BASE_URL)
        click_dropdown_option(driver, wait, SELECTOR_GENDER, gender)
        click_dropdown_option(driver, wait, SELECTOR_CALENDAR, calendar_type)
        pick_birth_date(driver, wait, year, month, day)

        click_button(driver, wait, SELECTOR_SUBMIT)
        return extract_result(wait)
    except TimeoutException as exc:
        raise TimeoutException("페이지 요소를 제때 찾지 못했습니다. 네트워크 상태를 확인하세요.") from exc
    finally:
        driver.quit()


app = FastAPI(title="Naver Fortune API", version="1.0.0")


@app.post("/fortune")
def get_fortune(payload: FortuneRequest):
    try:
        result = fetch_today_fortune(
            gender=payload.gender,
            calendar_type=payload.calendar_type,
            birth_date=payload.birth_date,
            headless=payload.headless,
            wait_seconds=payload.wait_seconds,
        )
    except (ValueError, RuntimeError, TimeoutException) as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc

    return {
        "date": result.date_text,
        "title": result.title,
        "summary": result.summary,
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("6_Naver_fortune_api:app", host="0.0.0.0", port=8000, reload=False)