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
import android.widget.ViewFlipper;

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
    private ViewFlipper flip;
    private TextView rawDataDisplay;
    private String result;
    private Button glasgowButton;
    private Button londonButton;
    private Button newYorkButton;
    private Button omanButton;
    private Button mauritiusButton;
    private Button bangladeshButton;
    private String glasgowCode = "2648579";
    private String londonCode = "2643743";
    private String newYorkCode = "5128581";
    private String omanCode = "287286";
    private String mauritiusCode = "934154";
    private String bangladeshCode = "1185241";

    private String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/";
    private LinkedList<DayOfWeather> dayOfWeatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the links to the graphical components
        flip = (ViewFlipper) findViewById(R.id.myVFlip);
        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        glasgowButton = (Button) findViewById(R.id.glasgowButton);
        londonButton = (Button) findViewById(R.id.londonButton);
        newYorkButton = (Button) findViewById(R.id.newYorkButton);
        omanButton = (Button) findViewById(R.id.omanButton);
        mauritiusButton = (Button) findViewById(R.id.mauritiusButton);
        bangladeshButton = (Button) findViewById(R.id.bangladeshButton);

        //Set listener for buttons
        glasgowButton.setOnClickListener(this);
        londonButton.setOnClickListener(this);
        newYorkButton.setOnClickListener(this);
        omanButton.setOnClickListener(this);
        mauritiusButton.setOnClickListener(this);
        bangladeshButton.setOnClickListener(this);

        // More Code goes here
    }

    public void onClick(View v) {
        if (v == glasgowButton)
            retrieveWeatherRSS(glasgowCode);
        else if (v == newYorkButton) {
            retrieveWeatherRSS(newYorkCode);
        } else if (v == londonButton) {
            retrieveWeatherRSS(londonCode);
        } else if (v == omanButton) {
            retrieveWeatherRSS(omanCode);
        } else if (v == mauritiusButton) {
            retrieveWeatherRSS(mauritiusCode);
        } else if (v == bangladeshButton) {
            retrieveWeatherRSS(bangladeshCode);
        }
    }

    public void retrieveWeatherRSS(String rssCode) {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource+rssCode)).start();
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
            dayOfWeatherList = new LinkedList<>();
            DayOfWeather dayOfWeather = null;
            boolean useTitleAndDescription = false;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));
                //resets result for next PullParser input
                result = "";
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
                            Log.d("MyTag", "title is " + temp);

                            //split <title> into day and weather data
                            String[] titleArray = temp.split(",");
                            String summary = titleArray[0];
                            String day = summary.split(":")[0].trim();
                            String weather = summary.split(":")[1].trim();

                            Log.d("MyTag", "Day is " + day +
                                    "\nWeather is " + weather);
                            dayOfWeather.setDay(day);
                            dayOfWeather.setWeather(weather);
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")&& useTitleAndDescription) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            Log.d("MyTag", "description is " + temp);

                            //split <description> into multiple weather data variables
                            String[] descArray = temp.split(",");

                            String maxTemp = descArray[0].trim();
                            String minTemp = descArray[1].trim();
                            String windDirection = descArray[2].trim();
                            String windSpeed = descArray[3].trim();
                            String visibility = descArray[4].trim();
                            String pressure = descArray[5].trim();
                            String humidity = descArray[6].trim();
                            String uvRisk = descArray[7].trim();
                            String pollution = descArray[8].trim();
                            String sunriseTime = descArray[9].trim();
                            String sunsetTime = descArray[10].trim();

                            Log.d("MyTag", "maxTemp is " + maxTemp+
                                    "\nminTemp is " + minTemp +
                                    "\nwindDirection is " + windDirection +
                                    "\nwindSpeed is " + windSpeed +
                                    "\nvisibility is " + visibility +
                                    "\npressure is " + pressure +
                                    "\nhumidity is " + humidity +
                                    "\nuvRisk is " + uvRisk +
                                    "\npollution is " + pollution +
                                    "\nsunriseTime is " + sunriseTime +
                                    "\nsunsetTime is " + sunsetTime);

                            dayOfWeather.setMaxTemp(maxTemp);
                            dayOfWeather.setMinTemp(minTemp);
                            dayOfWeather.setWindDirection(windDirection);
                            dayOfWeather.setWindSpeed(windSpeed);
                            dayOfWeather.setVisibility(visibility);
                            dayOfWeather.setPressure(pressure);
                            dayOfWeather.setHumidity(humidity);
                            dayOfWeather.setUVRisk(uvRisk);
                            dayOfWeather.setPollution(pollution);
                            dayOfWeather.setSunriseTime(sunriseTime);
                            dayOfWeather.setSunsetTime(sunsetTime);

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