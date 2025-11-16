from telegram import Update, InlineKeyboardButton, InlineKeyboardMarkup, ReplyKeyboardMarkup
from telegram.ext import (
    ContextTypes,
    ConversationHandler,
    CommandHandler,
    MessageHandler,
    filters,
    CallbackQueryHandler,
)
from models import RegistrationRequest, LoginRequest, ConnectBankRequest
import api
import persistence
import logging
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import io
import llm

# User data keys
TOKEN = "token"

# Conversation states
(
    ASK_USERNAME,
    ASK_PASSWORD,
    ASK_BANK_LOGIN,
    ASK_BANK_PASSWORD,
    ASK_BANK,
    TRANSACTION_PAGE,
    ASK_QUESTION,
) = range(7)

async def delete_messages(context: ContextTypes.DEFAULT_TYPE, message_ids):
    for message_id in message_ids:
        try:
            await context.bot.delete_message(
                chat_id=context.user_data["chat_id"], message_id=message_id
            )
        except Exception as e:
            logging.warning(f"Failed to delete message {message_id}: {e}")

async def is_user_logged_in(context: ContextTypes.DEFAULT_TYPE, user_id: int):
    if TOKEN in context.user_data:
        return True
    
    token = await persistence.get_token(user_id)
    if token:
        context.user_data[TOKEN] = token
        return True
    return False

async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    keyboard = [
        ["💰 Баланс", "📜 История"],
        ["📊 Аналитика", "🤖 Задать вопрос"],
    ]
    reply_markup = ReplyKeyboardMarkup(keyboard, resize_keyboard=True)
    await update.message.reply_text(
        "Добро пожаловать в Diakon Bank Bot!\n"
        "Выберите одну из опций в меню или используйте следующие команды:\n\n"
        "/register - Зарегистрировать нового пользователя\n"
        "/login - Войти в свой аккаунт\n"
        "/logout - Выйти из своего аккаунта\n"
        "/connect_bank - Подключить банковский аккаунт",
        reply_markup=reply_markup,
    )

# Registration Conversation
async def register_start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text("Пожалуйста, введите желаемый логин:")
    return ASK_USERNAME

async def register_username(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["username"] = update.message.text
    await update.message.reply_text("Пожалуйста, введите ваш пароль:")
    return ASK_PASSWORD

async def register_password(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["password"] = update.message.text
    
    request = RegistrationRequest(
        username=context.user_data["username"],
        password=context.user_data["password"],
    )
    
    try:
        response = await api.register_user(request)
        await update.message.reply_text(response)
    except Exception as e:
        await update.message.reply_text(f"Ошибка при регистрации: {e}")
        
    return ConversationHandler.END

# Login Conversation
async def login_start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["chat_id"] = update.message.chat_id
    context.user_data["messages_to_delete"] = [update.message.message_id]
    
    message = await update.message.reply_text("Пожалуйста, введите ваш логин:")
    context.user_data["messages_to_delete"].append(message.message_id)
    return ASK_USERNAME

async def login_username(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["messages_to_delete"].append(update.message.message_id)
    context.user_data["username"] = update.message.text
    message = await update.message.reply_text("Пожалуйста, введите ваш пароль:")
    context.user_data["messages_to_delete"].append(message.message_id)
    return ASK_PASSWORD

async def login_password(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["messages_to_delete"].append(update.message.message_id)
    context.user_data["password"] = update.message.text
    
    request = LoginRequest(
        username=context.user_data["username"],
        password=context.user_data["password"],
    )
    
    try:
        response = await api.login_user(request)
        token = response["token"]
        context.user_data[TOKEN] = token
        await persistence.save_token(update.message.from_user.id, token)
        message = await update.message.reply_text("Вы успешно вошли в систему!")
        context.user_data["messages_to_delete"].append(message.message_id)
        await delete_messages(context, context.user_data["messages_to_delete"])
        context.user_data["messages_to_delete"] = []
        
        await update.message.reply_text(
            "Вы можете использовать следующие команды:\n"
            "/logout - Выйти из своего аккаунта\n"
            "/connect_bank - Подключить банковский аккаунт"
        )
    except Exception as e:
        message = await update.message.reply_text(f"Ошибка при входе: {e}")
        context.user_data["messages_to_delete"].append(message.message_id)
        await delete_messages(context, context.user_data["messages_to_delete"])
        context.user_data["messages_to_delete"] = []
        
    return ConversationHandler.END

# Logout Command
async def logout(update: Update, context: ContextTypes.DEFAULT_TYPE):
    user_id = update.message.from_user.id
    if TOKEN in context.user_data:
        del context.user_data[TOKEN]
    
    await persistence.clear_token(user_id)
    await update.message.reply_text("Вы вышли из системы.")

# Connect Bank Conversation
async def connect_bank_start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    if not await is_user_logged_in(context, update.message.from_user.id):
        await update.message.reply_text("Вы должны быть авторизованы, чтобы подключить банк.")
        return ConversationHandler.END

    context.user_data["chat_id"] = update.message.chat_id
    context.user_data["messages_to_delete"] = [update.message.message_id]
        
    message = await update.message.reply_text("Пожалуйста, введите ваш логин от банка:")
    context.user_data["messages_to_delete"].append(message.message_id)
    return ASK_BANK_LOGIN

async def connect_bank_login(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["messages_to_delete"].append(update.message.message_id)
    context.user_data["bank_login"] = update.message.text
    message = await update.message.reply_text("Пожалуйста, введите ваш пароль от банка:")
    context.user_data["messages_to_delete"].append(message.message_id)
    return ASK_BANK_PASSWORD
    
async def connect_bank_password(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["messages_to_delete"].append(update.message.message_id)
    context.user_data["bank_password"] = update.message.text
    message = await update.message.reply_text("Пожалуйста, введите банк (VBANK, SBANK, ABANK):")
    context.user_data["messages_to_delete"].append(message.message_id)
    return ASK_BANK

async def connect_bank_name(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["messages_to_delete"].append(update.message.message_id)
    bank_name = update.message.text.upper()
    if bank_name not in ["VBANK", "SBANK", "ABANK"]:
        message = await update.message.reply_text("Неверный банк. Пожалуйста, введите один из: VBANK, SBANK, ABANK")
        context.user_data["messages_to_delete"].append(message.message_id)
        return ASK_BANK

    request = ConnectBankRequest(
        bankLogin=context.user_data["bank_login"],
        bankPassword=context.user_data["bank_password"],
        bank=bank_name,
    )

    try:
        await api.connect_bank(request, context.user_data[TOKEN])
        message = await update.message.reply_text("Банк успешно подключен!")
        context.user_data["messages_to_delete"].append(message.message_id)
        await delete_messages(context, context.user_data["messages_to_delete"])
        context.user_data["messages_to_delete"] = []
    except Exception as e:
        message = await update.message.reply_text(f"Ошибка при подключении банка: {e}")
        context.user_data["messages_to_delete"].append(message.message_id)
        await delete_messages(context, context.user_data["messages_to_delete"])
        context.user_data["messages_to_delete"] = []
        
    return ConversationHandler.END


async def cancel(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text("Операция отменена.")
    return ConversationHandler.END

register_conversation = ConversationHandler(
    entry_points=[CommandHandler("register", register_start)],
    states={
        ASK_USERNAME: [MessageHandler(filters.TEXT & ~filters.COMMAND, register_username)],
        ASK_PASSWORD: [MessageHandler(filters.TEXT & ~filters.COMMAND, register_password)],
    },
    fallbacks=[CommandHandler("cancel", cancel)],
)

login_conversation = ConversationHandler(
    entry_points=[CommandHandler("login", login_start)],
    states={
        ASK_USERNAME: [MessageHandler(filters.TEXT & ~filters.COMMAND, login_username)],
        ASK_PASSWORD: [MessageHandler(filters.TEXT & ~filters.COMMAND, login_password)],
    },
    fallbacks=[CommandHandler("cancel", cancel)],
)

connect_bank_conversation = ConversationHandler(
    entry_points=[CommandHandler("connect_bank", connect_bank_start)],
    states={
        ASK_BANK_LOGIN: [MessageHandler(filters.TEXT & ~filters.COMMAND, connect_bank_login)],
        ASK_BANK_PASSWORD: [MessageHandler(filters.TEXT & ~filters.COMMAND, connect_bank_password)],
        ASK_BANK: [MessageHandler(filters.TEXT & ~filters.COMMAND, connect_bank_name)],
    },
    fallbacks=[CommandHandler("cancel", cancel)],
)


# Get Accounts
async def balance(update: Update, context: ContextTypes.DEFAULT_TYPE):
    if not await is_user_logged_in(context, update.message.from_user.id):
        await update.message.reply_text("Вы должны быть авторизованы.")
        return

    try:
        accounts = await api.get_accounts(context.user_data[TOKEN])
        if not accounts:
            await update.message.reply_text("Счета не найдены.")
            return
            
        message = "Ваши счета:\n"
        for acc in accounts:
            message += f"- {acc.get('nickname', 'N/A')}: {acc.get('balance', 'N/A')} {acc.get('currency', '')}\n"
        await update.message.reply_text(message)
    except Exception as e:
        await update.message.reply_text(f"Ошибка при получении счетов: {e}")

async def history_start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    if not await is_user_logged_in(context, update.message.from_user.id):
        await update.message.reply_text("Вы должны быть авторизованы.")
        return ConversationHandler.END

    try:
        transactions = await api.get_transactions(context.user_data[TOKEN])
        if not transactions:
            await update.message.reply_text("Транзакции не найдены.")
            return ConversationHandler.END

        context.user_data["transactions"] = transactions
        context.user_data["page"] = 0
        
        await send_transaction_page(update, context)
        return TRANSACTION_PAGE

    except Exception as e:
        await update.message.reply_text(f"Ошибка при получении транзакций: {e}")
        return ConversationHandler.END

async def send_transaction_page(update: Update, context: ContextTypes.DEFAULT_TYPE):
    transactions = context.user_data["transactions"]
    page = context.user_data["page"]
    page_size = 10
    start_index = page * page_size
    end_index = start_index + page_size

    message = "Ваши транзакции:\n"
    for trans in transactions[start_index:end_index]:
        date_str = trans.get('bookingDateTime', 'N/A')
        if date_str != 'N/A':
            date_str = date_str.split('T')[0]
            
        message += f"- {trans.get('transactionInformation', 'N/A')}: {trans.get('amount', 'N/A')} {trans.get('currency', '')} в {date_str}\n"

    keyboard = []
    row = []
    if page > 0:
        row.append(InlineKeyboardButton("⬅️ Назад", callback_data="prev_page"))
    if end_index < len(transactions):
        row.append(InlineKeyboardButton("Вперед ➡️", callback_data="next_page"))
    keyboard.append(row)

    reply_markup = InlineKeyboardMarkup(keyboard)
    
    if update.callback_query:
        await update.callback_query.edit_message_text(text=message, reply_markup=reply_markup)
    else:
        await update.message.reply_text(text=message, reply_markup=reply_markup)

async def transaction_page_callback(update: Update, context: ContextTypes.DEFAULT_TYPE):
    query = update.callback_query
    await query.answer()

    page_direction = query.data
    if page_direction == "next_page":
        context.user_data["page"] += 1
    elif page_direction == "prev_page":
        context.user_data["page"] -= 1

    await send_transaction_page(update, context)
    return TRANSACTION_PAGE
    
transactions_conversation = ConversationHandler(
    entry_points=[MessageHandler(filters.Regex("^📜 История$"), history_start)],
    states={
        TRANSACTION_PAGE: [CallbackQueryHandler(transaction_page_callback)],
    },
    fallbacks=[CommandHandler("cancel", cancel)],
)

def is_salary(transaction: dict) -> bool:
    """Checks if a transaction is income based on keywords."""
    category = transaction.get('transactionInformation', '').lower()
    return 'зарплата' in category or 'salary' in category or 'доход' in category or 'проценты по депозиту' in category


def generate_analytics_image(expenses_data, income_data):
    """Generates a PNG image with doughnut charts for expenses and income."""

    def process_data(data, top_n=5):
        if not data:
            return {}
        sorted_data = sorted(data.items(), key=lambda item: item[1], reverse=True)
        main_categories = dict(sorted_data[:top_n])
        other_sum = sum(item[1] for item in sorted_data[top_n:])
        if other_sum > 0:
            main_categories['Другое'] = other_sum
        return main_categories

    expenses_chart_data = process_data(expenses_data)
    income_chart_data = process_data(income_data)

    if not expenses_chart_data and not income_chart_data:
        return None

    fig, axes = plt.subplots(1, 2, figsize=(20, 10))
    fig.patch.set_facecolor('#2c2f48')
    text_color = 'white'
    colors = ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#FFD700', '#ADFF2F', '#FF69B4', '#1E90FF', '#9932CC', '#F4A460', '#808080']

    def create_doughnut_chart(ax, data, title):
        if not data:
            ax.text(0.5, 0.5, f'Нет данных о {title.lower()}', ha='center', va='center', color=text_color, fontsize=14)
            ax.axis('off')
            ax.set_title(title, color=text_color, fontsize=20, weight='bold')
            return

        labels = data.keys()
        sizes = data.values()

        wedges, texts, autotexts = ax.pie(
            sizes,
            autopct='%1.1f%%',
            startangle=90,
            colors=colors,
            pctdistance=0.85,
            wedgeprops=dict(width=0.3, edgecolor='#2c2f48')
        )
        
        plt.setp(autotexts, size=12, weight="bold", color="white")

        ax.set_title(title, color=text_color, fontsize=20, weight='bold', pad=20)
        
        total = sum(sizes)
        ax.text(0, 0, f'Всего:\n{total:,.2f} RUB',
                ha='center', va='center', fontsize=16, color=text_color, weight='bold')

        legend = ax.legend(wedges, labels,
                  title="Категории",
                  loc="upper center",
                  bbox_to_anchor=(0.5, -0.05),
                  prop={'size': 12},
                  ncol=2
                 )
        legend.get_frame().set_facecolor('#f0f0f0')
        plt.setp(legend.get_texts(), color='black')
        plt.setp(legend.get_title(), color='black')

    create_doughnut_chart(axes[0], expenses_chart_data, 'Расходы')
    create_doughnut_chart(axes[1], income_chart_data, 'Доходы')

    plt.tight_layout(pad=3.0)

    buf = io.BytesIO()
    plt.savefig(buf, format='png', facecolor=fig.get_facecolor(), bbox_inches='tight')
    buf.seek(0)
    plt.close(fig)
    return buf


async def analytics(update: Update, context: ContextTypes.DEFAULT_TYPE):
    if not await is_user_logged_in(context, update.message.from_user.id):
        await update.message.reply_text("Вы должны быть авторизованы.")
        return

    try:
        await update.message.reply_text("🎨 Собираю данные для аналитики, минуточку...")
        transactions = await api.get_transactions(context.user_data[TOKEN])
        if not transactions:
            await update.message.reply_text("Недостаточно данных для построения аналитики. Сначала должны быть транзакции.")
            return

        expenses = {}
        income = {}
        for t in transactions:
            amount = t.get('amount', 0)
            category = t.get('transactionInformation', 'Прочее')
            if is_salary(t):
                income[category] = income.get(category, 0) + abs(amount)
            else:
                expenses[category] = expenses.get(category, 0) + abs(amount)
        
        if not expenses and not income:
            await update.message.reply_text("Не найдено транзакций с расходами или доходами.")
            return

        image_buffer = generate_analytics_image(expenses, income)
        
        if image_buffer:
            await update.message.reply_photo(photo=image_buffer, caption="📊 Ваша финансовая аналитика готова!")
            image_buffer.close()
        else:
            await update.message.reply_text("Не удалось сгенерировать изображение аналитики.")

    except Exception as e:
        logging.error(f"Error in analytics: {e}")
        await update.message.reply_text(f"Произошла ошибка при построении аналитики: {e}")


async def ask_question_start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    """Starts the ask question conversation."""
    if not await is_user_logged_in(context, update.message.from_user.id):
        await update.message.reply_text("Вы должны быть авторизованы.")
        return ConversationHandler.END

    await update.message.reply_text("Пожалуйста, задайте свой вопрос:")
    return ASK_QUESTION


async def handle_question(update: Update, context: ContextTypes.DEFAULT_TYPE):
    """Handles the user's question, gets data, and calls the LLM."""
    question = update.message.text
    await update.message.reply_text("🤖 Думаю над вашим вопросом...")

    try:
        transactions = await api.get_transactions(context.user_data[TOKEN])
        if not transactions:
            await update.message.reply_text("У вас еще нет транзакций для анализа.")
            return ConversationHandler.END

        response = await llm.get_llm_response(question, transactions)
        await update.message.reply_text(response)

    except Exception as e:
        logging.error(f"Error handling question: {e}")
        await update.message.reply_text(f"Произошла ошибка: {e}")

    return ConversationHandler.END


ask_question_conversation = ConversationHandler(
    entry_points=[MessageHandler(filters.Regex("^🤖 Задать вопрос$"), ask_question_start)],
    states={
        ASK_QUESTION: [MessageHandler(filters.TEXT & ~filters.COMMAND, handle_question)],
    },
    fallbacks=[CommandHandler("cancel", cancel)],
) 