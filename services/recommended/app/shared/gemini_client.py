import os
import httpx
from dotenv import load_dotenv

load_dotenv()  # chỉ để dev local; khi deploy, bạn export ENV trực tiếp

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
# GEMINI_API_KEY = "AIzaSyBNptuTG36MQIV3K302Hk_prZfcqE_NsYY"
if not GEMINI_API_KEY:
    raise RuntimeError("Missing GEMINI_API_KEY")

# Free-tier model
BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
MODEL_ID  = "gemini-2.0-flash"  # model free-tier

async def post_to_gemini_free(payload: dict) -> dict:
    """
    Gọi endpoint generateContent?key=...
    """
    url = f"{BASE_URL}/{MODEL_ID}:generateContent?key={GEMINI_API_KEY}"
    headers = {"Content-Type": "application/json"}
    async with httpx.AsyncClient() as client:
        resp = await client.post(url, headers=headers, json=payload)
        resp.raise_for_status()
        return resp.json()
