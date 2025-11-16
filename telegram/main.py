import logging
import asyncio
from bot import setup_bot
from database import init_models
import os
from dotenv import load_dotenv
from telegram.ext import ApplicationBuilder


async def main():
    load_dotenv()
    logging.basicConfig(
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        level=logging.INFO,
    )

    await init_models()

    TELEGRAM_BOT_TOKEN = os.environ.get("TELEGRAM_BOT_TOKEN")
    if not TELEGRAM_BOT_TOKEN:
        logging.error("TELEGRAM_BOT_TOKEN is not set")
        return

    application = ApplicationBuilder().token(TELEGRAM_BOT_TOKEN).build()
    setup_bot(application)

    async with application:
        await application.start()
        await application.updater.start_polling()
        await asyncio.Event().wait()


if __name__ == "__main__":
    asyncio.run(main()) 