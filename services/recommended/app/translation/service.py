from app.shared.hf_client import post_to_hf
from .schemas import TranslatedResponse

MODEL_ID = "Helsinki-NLP/opus-mt-vi-en"

async def translate_vi_to_en(text: str) -> TranslatedResponse:
    print("Input text:", text)
    try:
        data = await post_to_hf(MODEL_ID, text)
        print("Raw response from HuggingFace:", data)

        if isinstance(data, list) and len(data) > 0:
            if "translation_text" in data[0]:
                translation = data[0]["translation_text"]
            else:
                print("Key 'translation_text' not found in response item:", data[0])
                translation = "[Missing 'translation_text']"
        else:
            print("Unexpected response structure:", data)
            translation = "[Unexpected response structure]"

    except Exception as e:
        print("Error during translation:", repr(e))
        translation = "[Error occurred]"

    return TranslatedResponse(translation=translation)

