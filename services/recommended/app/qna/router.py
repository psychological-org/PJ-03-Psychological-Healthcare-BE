from fastapi import APIRouter, HTTPException
from .schemas import QuestionRequest, AnswerResponse
from .service import ask_gemini

qna_router = APIRouter()

@qna_router.post("", response_model=AnswerResponse)
async def ask(question: QuestionRequest):
    try:
        return await ask_gemini(question.negative_level)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))