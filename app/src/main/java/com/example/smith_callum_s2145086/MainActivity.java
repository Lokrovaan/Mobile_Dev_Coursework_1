// Name                 Callum Smith
// Student ID           S2145086
// Programme of Study   BSc Computing
package com.example.smith_callum_s2145086;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private TextView rawDataDisplay;
    private Button startButton;
    private String result;
    private String url1 = "";
    private String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643123";
    private LinkedList<DayOfWeather> dayOfWeatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        dayOfWeatherList = new LinkedList<DayOfWeather>();
        // More Code goes here
    }

    public void onClick(View aview) {
        startProgress();
    }

    public void startProgress() {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";

            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
            int i = result.indexOf(">");
            result = result.substring(i + 1);
            Log.e("MyTag - cleaned", result);

            //Parse cleaned RSS Feed data
            DayOfWeather dayOfWeather = null;
            boolean useTitleAndDescription = false;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) // Found a start tag
                    {   // Check which start Tag we have as we'd do different things
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.d("MyTag", "Day of Weather found!");
                            useTitleAndDescription = true;
                            dayOfWeather = new DayOfWeather();
                        }
                        else if (xpp.getName().equalsIgnoreCase("title") && useTitleAndDescription) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            Log.d("MyTag", "title is " + temp);
                            dayOfWeather.setTitle(temp);
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")&& useTitleAndDescription) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            Log.d("MyTag", "description is " + temp);
                            dayOfWeather.setDescription(temp);
                        }
                    } else if (eventType == XmlPullParser.END_TAG) // Found an end tag
                    {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.d("MyTag", "Day of Weather parsing completed!");
                            dayOfWeatherList.add(dayOfWeather);
                        }
                    }
                    eventType = xpp.next(); // Get the next event before looping again
                } // End of while
            } catch (XmlPullParserException ae1) {
                Log.e("MyTag", "Parsing error" + ae1.toString());
            } catch (IOException ae1) {
                Log.e("MyTag", "IO error during parsing");
            } catch (NullPointerException ae1){
                Log.e("MyTag", "Null Pointer Exception");
            }

            Log.d("MyTag", "End of document reached");
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    String threeDayForecast = "";
                    for (DayOfWeather d : dayOfWeatherList) {
                        threeDayForecast += d.toString();
                        rawDataDisplay.setText(threeDayForecast);
                    }
                }
            });
        }
    }
}