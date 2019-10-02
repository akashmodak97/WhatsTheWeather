package com.akash.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView textView2;
    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpsURLConnection urlConnection=null;
            try{

                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data= reader.read();
                while(data!= -1){
                    char current = (char) data;
                    result += current;
                    data= reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }



        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weatherInfo);
                String message = "";
                for(int i=0 ; i<jsonArray.length();i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description= jsonPart.getString("description");
                    if(!main.equals("") && !description.equals(""))
                        message+= main+ ": " + description+"\r\n";
                }
                //message+="Temperature:"+temp+"°C";
                if (!message.equals(""))
                    textView2.setText(message);
                else
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    public void getWeather(View view){
        try {
            String place = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + place + "&appid=b6907d289e10d714a6e88b30761fae22");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        textView2 = findViewById(R.id.textView2);

    }
}
