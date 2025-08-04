import os
from dotenv import load_dotenv
import httpx
from tenacity import retry, stop_after_attempt, wait_fixed

load_dotenv()
HF_TOKEN = os.getenv("HF_TOKEN")
HEADERS = {
    "Authorization": f"Bearer {HF_TOKEN}",
    "Content-Type": "application/json"
}

@retry(stop=stop_after_attempt(3), wait=wait_fixed(2))
async def post_to_hf(model_id: str, payload: str):
    url = f"https://api-inference.huggingface.co/models/{model_id}"
    print(f"Sending request to {url} with payload: {payload}")
    async with httpx.AsyncClient(timeout=30.0) as client:
        try:
            resp = await client.post(url, headers=HEADERS, json={"inputs": payload})
            print("Status code:", resp.status_code)
            print("HF API response body:", resp.text)
            resp.raise_for_status()
            return resp.json()
        except httpx.ReadTimeout as e:
            print(f"Timeout error: Không nhận được phản hồi từ API. Chi tiết: {repr(e)}")
            raise
        except httpx.HTTPStatusError as e:
            print(f"HTTP error: {e.response.status_code} - {e.response.text}")
            raise
        except Exception as e:
            print(f"Lỗi không xác định: {repr(e)}")
            raise