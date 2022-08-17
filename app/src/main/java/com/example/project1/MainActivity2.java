package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
    TextView tv_ayah, tv_surahName;
    Button btn_play;
    EditText et_surahNum, et_ayahNum;
    RequestQueue queue;
    MediaPlayer mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Objects.requireNonNull(getSupportActionBar()).hide();


//        String url = "https://api.alquran.cloud/v1/ayah/x:x/ar.alafasy";
        queue = Volley.newRequestQueue(MainActivity2.this);
        et_surahNum = findViewById(R.id.et_surahNum);
        et_ayahNum = findViewById(R.id.et_ayahNum);
        tv_ayah = findViewById(R.id.tv_ayah);
        tv_surahName = findViewById(R.id.tv_surahName);
        btn_play = findViewById(R.id.btn_playMedia);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mPlayer != null) {
                        mPlayer.stop();
                    }
                    startMedia("https://api.alquran.cloud/v1/ayah/" + et_surahNum.getText().toString() + ":" + et_ayahNum.getText().toString() + "/ar.alafasy");

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    public void startMedia(String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject data, nameObj;
                        String ayah, audio, name;
                        try {
                            data = response.getJSONObject("data");
                            nameObj = data.getJSONObject("surah");

                            audio = data.getString("audio");
                            ayah = data.getString("text");
                            name = nameObj.getString("englishName");

                            mPlayer = new MediaPlayer();
                            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            mPlayer.setDataSource(audio);
                            mPlayer.prepare(); // might take long! (for buffering, etc)
                            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mPlayer.stop();
                                    mPlayer.release();
                                    mPlayer = null;
                                }
                            });
                            mPlayer.start();
                            tv_ayah.setText(ayah);
                            tv_surahName.setText(name);

                        } catch (JSONException | IOException e) {
                            Toast.makeText(getApplicationContext(), "[1]That didn't work!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }
}