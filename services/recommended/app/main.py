# app/main.py
from fastapi import FastAPI

# import các routers qua app.translation và app.emotion
from app.translation.router import translation_router
from app.emotion.router import emotion_router
from app.qna.router import qna_router

app = FastAPI()

app.include_router(translation_router, prefix="/api/v1/recommended/translate", tags=["translate"])
app.include_router(emotion_router,     prefix="/api/v1/recommended/emotion",   tags=["emotion"])
app.include_router(qna_router, prefix="/api/v1/recommended/qna", tags=["qna"])
