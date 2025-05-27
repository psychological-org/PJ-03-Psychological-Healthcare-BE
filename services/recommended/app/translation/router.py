from fastapi import APIRouter, HTTPException
from .schemas import TextInput, TranslatedResponse
from .service import translate_vi_to_en

translation_router = APIRouter()

@translation_router.post("", response_model=TranslatedResponse)
async def translate(payload: TextInput):
    try:
        return await translate_vi_to_en(payload.text)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))