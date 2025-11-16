import httpx
from models import RegistrationRequest, LoginRequest, ConnectBankRequest

AUTH_SERVICE_BASE_URL = "http://localhost:8080/api/v1/auth"
BANK_API_SERVICE_BASE_URL = "http://localhost:8082/api/v1/bank"

async def register_user(request: RegistrationRequest):
    async with httpx.AsyncClient() as client:
        response = await client.post(f"{AUTH_SERVICE_BASE_URL}/register", json=request.dict())
        response.raise_for_status()
        return response.text

async def login_user(request: LoginRequest):
    async with httpx.AsyncClient() as client:
        response = await client.post(f"{AUTH_SERVICE_BASE_URL}/login", json=request.dict())
        response.raise_for_status()
        return response.json()

async def connect_bank(request: ConnectBankRequest, token: str):
    headers = {"Authorization": f"Bearer {token}"}
    async with httpx.AsyncClient() as client:
        response = await client.post(f"{AUTH_SERVICE_BASE_URL}/connect-bank", json=request.dict(), headers=headers)
        response.raise_for_status()

async def get_accounts(token: str):
    headers = {"Authorization": f"Bearer {token}"}
    async with httpx.AsyncClient() as client:
        response = await client.get(f"{BANK_API_SERVICE_BASE_URL}/accounts", headers=headers)
        response.raise_for_status()
        return response.json()

async def get_transactions(token: str):
    headers = {"Authorization": f"Bearer {token}"}
    async with httpx.AsyncClient() as client:
        response = await client.get(f"{BANK_API_SERVICE_BASE_URL}/transactions", headers=headers)
        response.raise_for_status()
        return response.json() 