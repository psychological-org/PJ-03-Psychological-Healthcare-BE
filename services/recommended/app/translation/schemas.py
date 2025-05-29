from pydantic import BaseModel

class TextInput(BaseModel):
    text: str
    
class TranslatedResponse(BaseModel):
    translation: str
    
