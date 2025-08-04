from app.shared.hf_client import post_to_hf
from .schemas import EmotionOutput, EmotionScore, TranslatedResponse

MODEL_ID = "bhadresh-savani/distilbert-base-uncased-emotion"
NEGATIVE_LABELS = {"sadness", "anger", "fear"}

async def analyze_emotion(translation: TranslatedResponse) -> EmotionOutput:
    data = await post_to_hf(MODEL_ID, translation.translation)
    items = data[0]
    total = sum(x["score"] for x in items)
    neg = sum(x["score"] for x in items if x["label"] in NEGATIVE_LABELS)
    scores = [EmotionScore(label=x["label"], score=x["score"]) for x in items]
    return EmotionOutput(emotions=scores, negativity_score=round(neg/total, 4))



