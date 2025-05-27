from fastapi import APIRouter, HTTPException
from .schemas import TranslatedResponse, EmotionOutput
from .service import analyze_emotion

emotion_router = APIRouter()

@emotion_router.post("", response_model=EmotionOutput)
async def emotion(payload: TranslatedResponse):
    try:
        return await analyze_emotion(payload)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
