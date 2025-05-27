import os
from dotenv import load_dotenv
import httpx

load_dotenv()
HF_TOKEN = os.getenv("HF_TOKEN")
HEADERS = {
    "Authorization": f"Bearer {HF_TOKEN}",
    "Content-Type": "application/json"
}

async def post_to_hf(model_id: str, payload: dict):
    url = f"https://api-inference.huggingface.co/models/{model_id}"
    async with httpx.AsyncClient() as client:
        resp = await client.post(url, headers=HEADERS, json={"inputs": payload})
    resp.raise_for_status()
    return resp.json()