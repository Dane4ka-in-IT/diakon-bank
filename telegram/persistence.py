from sqlalchemy import select, delete
from database import async_session, UserToken


async def save_token(user_id, token):
    async with async_session() as session:
        async with session.begin():
            user_token = await session.get(UserToken, user_id)
            if user_token:
                user_token.token = token
            else:
                user_token = UserToken(user_id=user_id, token=token)
                session.add(user_token)


async def get_token(user_id):
    async with async_session() as session:
        result = await session.execute(select(UserToken).filter_by(user_id=user_id))
        user_token = result.scalar_one_or_none()
        return user_token.token if user_token else None


async def clear_token(user_id):
    async with async_session() as session:
        async with session.begin():
            await session.execute(delete(UserToken).filter_by(user_id=user_id)) 