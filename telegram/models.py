from pydantic import BaseModel

class RegistrationRequest(BaseModel):
    username: str
    password: str

class LoginRequest(BaseModel):
    username: str
    password: str

class ConnectBankRequest(BaseModel):
    bankLogin: str
    bankPassword: str
    bank: str 