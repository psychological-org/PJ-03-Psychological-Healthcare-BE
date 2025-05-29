from pydantic import BaseModel

class TranslatedResponse(BaseModel):
    translation: str

class EmotionScore(BaseModel):
    label: str
    score: float

class EmotionOutput(BaseModel):
    emotions: list[EmotionScore]
    negativity_score: float