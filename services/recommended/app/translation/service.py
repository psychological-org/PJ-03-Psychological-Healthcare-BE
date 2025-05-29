from app.shared.hf_client import post_to_hf
from .schemas import TranslatedResponse

MODEL_ID = "Helsinki-NLP/opus-mt-vi-en"

async def translate_vi_to_en(text: str) -> TranslatedResponse:
    data = await post_to_hf(MODEL_ID, text)
    translation = data[0].get("translation_text", "")
    return TranslatedResponse(translation=translation)