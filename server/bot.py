'''
Initializes the chatbot from the SQLite database
'''

from chatterbot import ChatBot

bot = ChatBot(
    "John Doe",
    storage_adapter='chatterbot.storage.SQLStorageAdapter',
    database='./db.sqlite3',
    read_only=True
)
