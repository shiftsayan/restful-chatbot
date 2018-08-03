# RESTful Chatbot

This is a web-based chatbot built using `chatterbot` and `flask-RESTful`, along with accompanying Android application. It also uses Google's Speech to Text and Text to Speech APIs for voice interaction.

### Dependancies

You will need a compatible Android device (SDK version >= 15) and the following packages installed for Python 2.7:

* `flask`, `flask-RESTful`
* `chatterbot`

### Use

* Ensure the Android phone and device hosting the server are on the same network.
* Find the host's IP address (shouldn't be `127.0.0.1` because that is `localhost`).
* Update this IP address on line 40 of `MainActivity.java` instead of `192.168.0.XXX` in the `app` directory.
* Install the APK on an Android phone using Android Studio.
* Run the server by executing `server.py` in the `server` directory.
