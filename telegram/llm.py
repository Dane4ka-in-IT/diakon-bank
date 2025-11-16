from telegram import Update
from telegram.ext import ContextTypes
import os
import httpx
from dotenv import load_dotenv
import asyncio
import logging

load_dotenv()

LLM_API_BASE = os.getenv("LLM_API_BASE")
LLM_API_KEY = os.getenv("LLM_API_KEY")
LLM_MODEL = os.getenv("LLM_MODEL")

async def get_llm_response(question: str, transactions: list) -> str:
    """
    Sends a question and transaction data to the LLM and returns the response.
    Retries up to 5 times on failure.
    """
    if not LLM_API_BASE or not LLM_API_KEY:
        return "LLM service is not configured. Please set LLM_API_BASE and LLM_API_KEY in the .env file."

    url = f"{LLM_API_BASE}/chat/completions"
    
    headers = {
        "Authorization": f"Bearer {LLM_API_KEY}",
        "Content-Type": "application/json",
    }

    prompt = (
        "Ты — финансовый ассистент. Твоя задача — помочь пользователю с анализом его транзакций. "
        "Отвечай на русском языке. Ответ должен быть кратким и по существу. "
        "Не используй markdown или любое другое форматирование. Только обычный текст.\n\n"
        "Транзакции:\n"
        f"{transactions}\n\n"
        "Вопрос:\n"
        f"{question}"
    )

    data = {
        "model": LLM_MODEL,
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.5,
        "top_p": 0.5
    }

    last_error = ""
    max_retries = 5
    for attempt in range(max_retries):
        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(url, headers=headers, json=data, timeout=30.0)
                response.raise_for_status()
                return response.json()["choices"][0]["message"]["content"]
        except httpx.HTTPStatusError as e:
            last_error = f"Error communicating with the LLM service: {e.response.status_code} {e.response.text}"
            logging.warning(f"Attempt {attempt + 1}/{max_retries} failed: {last_error}")
        except Exception as e:
            last_error = f"An unexpected error occurred: {e}"
            logging.warning(f"Attempt {attempt + 1}/{max_retries} failed: {last_error}")

        if attempt < max_retries - 1:
            await asyncio.sleep(2)  # Wait for 2 seconds before retrying

    return f"Failed to get a response after {max_retries} attempts. Last error: {last_error}" "temp"