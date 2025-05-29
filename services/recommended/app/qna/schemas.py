from pydantic import BaseModel

class QuestionRequest(BaseModel):
    negative_level: float

class AnswerResponse(BaseModel):
    answer: str
