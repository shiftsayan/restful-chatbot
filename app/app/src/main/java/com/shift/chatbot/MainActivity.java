package com.shift.chatbot;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {
    // View Elements
	private TextView textView;
	private ImageButton micButton;
	private boolean micButtonActive = true;
	// RESTful Elements
    private String url = "http://192.168.0.107:5000/chatbot/randomstring"; // change '192.168.0.XXX' to server's IP address and 'randomstring' to User ID
    private RequestQueue queue;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ArrayList<String> result;
    private String requestBody;
    // TTS Elements
	private TextToSpeech tts;
    HashMap<String, String> params = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initialize View Elements
		setContentView(R.layout.activity_main);
        getActionBar().hide();
		textView = (TextView)findViewById(R.id.textView);
		micButton = (ImageButton)findViewById(R.id.micbutton);
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (micButtonActive) {
                    recognizeVoiceInput();
                }
            }
        });
		// Initialize RESTful Elements
		queue = Volley.newRequestQueue(this);
        // Initialize TTS Elements
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }
                        @Override
                        public void onError(String utteranceId) {

                        }
                        @Override
                        public void onDone(String utteranceId) {
                            micButtonActive = true;
                        }
                    });
                }
            }
        });
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id");
	}

	// Default Google STT
	private void recognizeVoiceInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.default_text));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.incompatible_device),
					Toast.LENGTH_SHORT).show();
		}
	}

    // Get Response From Voice Input
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    // Get Voice Input Text
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    requestBody = result.get(0);
                    // Prevent Further Voice Inputs
                    micButtonActive = false;
                    // Get Response via POST Request to Flask Server
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    textView.setText(response); // Display Text
                                    tts.speak(response, TextToSpeech.QUEUE_ADD, params); // Speak Text
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    textView.setText(getString(R.string.bad_response)); // Display Text
                                    tts.speak(getString(R.string.bad_response), TextToSpeech.QUEUE_ADD, params); // Speak Text
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "text; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uue) {
                                Log.d("VOLLEY", getString(R.string.unsupported_encoding));
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            try {
                                responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            } catch (Exception e) {
                                responseString = getString(R.string.bad_response);
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };
                    queue.add(stringRequest);
                }
                break;
            }

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
