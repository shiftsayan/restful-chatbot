'''
Run RESTful Flask server to receive requests from client
'''

from flask import Flask, request
from flask_restful import Api, Resource, reqparse

from bot import bot

app = Flask(__name__)
api = Api(app)

class Res(Resource):

    def get(self, uid):
        pass

    def post(self, uid):
        response = bot.get_response(request.data)
        if response.confidence < 0.5:
            return "I am not trained to respond to statements like these."
        return str(response)

    def put(self, uid):
        pass

    def delete(self, uid):
        pass

api.add_resource(Res, "/chatbot/<string:uid>")
app.run(host='0.0.0.0', debug=True) # remove debug=True when finally deploying
