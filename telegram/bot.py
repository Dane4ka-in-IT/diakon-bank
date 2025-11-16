import logging
from telegram.ext import Application, CommandHandler, MessageHandler, filters
from handlers import (
    start,
    register_conversation,
    login_conversation,
    logout,
    connect_bank_conversation,
    balance,
    transactions_conversation,
    analytics,
    ask_question_conversation,
)
import os
from dotenv import load_dotenv

load_dotenv()

TELEGRAM_BOT_TOKEN = os.environ.get("TELEGRAM_BOT_TOKEN")

def setup_bot(application: Application):
    """Add handlers to the application."""
    application.add_handler(CommandHandler("start", start))
    application.add_handler(register_conversation)
    application.add_handler(login_conversation)
    application.add_handler(CommandHandler("logout", logout))
    application.add_handler(connect_bank_conversation)
    application.add_handler(MessageHandler(filters.Regex("^💰 Баланс$"), balance))
    application.add_handler(transactions_conversation)
    application.add_handler(MessageHandler(filters.Regex("^📊 Аналитика$"), analytics))
    application.add_handler(ask_question_conversation)