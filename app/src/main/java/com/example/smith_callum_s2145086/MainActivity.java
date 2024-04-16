// Name                 Callum Smith
// Student ID           S2145086
// Programme of Study   BSc Computing
package com.example.smith_callum_s2145086;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
    private ViewSwitcher switcher;
    private Handler updateUIHandler = null;
    private final static int RETRIEVE_OBSERVATION = 1;
    private final static int RETRIEVE_3_DAY_WEATHER = 2;
    private TextView rawDataDisplay;
    private String result;
    private Button glasgowButton;
    private Button londonButton;
    private Button newYorkButton;
    private Button omanButton;
    private Button mauritiusButton;
    private Button bangladeshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switcher = (ViewSwitcher) findViewById(R.id.myVSwitcher);
        createUpdateUiHandler();

        // Set up the links to the graphical components
        switcher = (ViewSwitcher) findViewById(R.id.myVSwitcher);
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
        if (v == glasgowButton) {
            String glasgowCode = "2648579";
            new Thread(new RetrieveThreeDayWeather(glasgowCode)).start();
        } else if (v == newYorkButton) {
            String newYorkCode = "5128581";
            new Thread(new RetrieveThreeDayWeather(newYorkCode)).start();
        } else if (v == londonButton) {
            String londonCode = "2643743";
            new Thread(new RetrieveThreeDayWeather(londonCode)).start();
        } else if (v == omanButton) {
            String omanCode = "287286";
            new Thread(new RetrieveThreeDayWeather(omanCode)).start();
        } else if (v == mauritiusButton) {
            String mauritiusCode = "934154";
            new Thread(new RetrieveThreeDayWeather(mauritiusCode)).start();
        } else if (v == bangladeshButton) {
            String bangladeshCode = "1185241";
            new Thread(new RetrieveThreeDayWeather(bangladeshCode)).start();
        }
    }

    private void createUpdateUiHandler() {
        if (updateUIHandler == null) {
            updateUIHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    // Check which thread sent the message
                    if (msg.what == RETRIEVE_OBSERVATION) {
                        //TODO update UI with observation
                    } else if (msg.what == RETRIEVE_3_DAY_WEATHER) {
                        //TODO update UI with 3 day weather info
                    }
                }
            };
        }
    }

    class RetrieveObservation implements Runnable {
        String url = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/";

        public RetrieveObservation(String rssCode) {
            url = url + rssCode;
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
            Observation observation = null;
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
                            observation = new Observation();
                        } else if (xpp.getName().equalsIgnoreCase("title") && useTitleAndDescription) {
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
                            observation.setDay(day);
                            observation.setSummary(weather);
                        } else if (xpp.getName().equalsIgnoreCase("description") && useTitleAndDescription) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            Log.d("MyTag", "description is " + temp);

                            //split <description> into multiple weather data variables
                            String[] descArray = temp.split(",");
                            //TODO change to String.contains and iterate through String array to add data to weather object
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

                            Log.d("MyTag", "maxTemp is " + maxTemp +
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

                            observation.setCurrentTemperature(maxTemp);
                            observation.setWindDirection(windDirection);
                            observation.setWindSpeed(windSpeed);
                            observation.setVisibility(visibility);
                            observation.setPressure(pressure);
                            observation.setHumidity(humidity);
                        }
                    } else if (eventType == XmlPullParser.END_TAG) // Found an end tag
                    {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.d("MyTag", "Day of Weather parsing completed!");
                        }
                    }
                    eventType = xpp.next(); // Get the next event before looping again
                } // End of while
            } catch (XmlPullParserException ae1) {
                Log.e("MyTag", "Parsing error" + ae1.toString());
            } catch (IOException ae1) {
                Log.e("MyTag", "IO error during parsing");
            } catch (NullPointerException ae1) {
                Log.e("MyTag", "Null Pointer Exception");
            }
            Log.d("MyTag", "End of document reached");

            //Prepare data to be sent to main thread
            String observationString = observation.toString();

            // Build message object
            Message message = new Message(); // Set message type
            message.what = RETRIEVE_OBSERVATION;
            message.obj = observationString;

            // Send message to main thread Handler
            updateUIHandler.sendMessage(message);
        }
    }

    //Runnable class that carries out the act of retrieving, parsing and sending 3-day weather data to the main thread.
    class RetrieveThreeDayWeather implements Runnable {
        String url = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/";

        public RetrieveThreeDayWeather(String rssCode) {
            url = url + rssCode;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";
            LinkedList<DayOfWeather> dayOfWeatherList;

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
                        } else if (xpp.getName().equalsIgnoreCase("title") && useTitleAndDescription) {
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
                        } else if (xpp.getName().equalsIgnoreCase("description") && useTitleAndDescription) {
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

                            Log.d("MyTag", "maxTemp is " + maxTemp +
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
            } catch (NullPointerException ae1) {
                Log.e("MyTag", "Null Pointer Exception");
            }
            Log.d("MyTag", "End of document reached");

            //Prepare data to be sent to main thread
            String threeDayForecast = "";
            for (DayOfWeather d : dayOfWeatherList) {
                threeDayForecast += d.toString();
            }

            // Build message object
            Message message = new Message(); // Set message type
            message.what = RETRIEVE_WEATHER;
            message.obj = threeDayForecast;

            // Send message to main thread Handler
            updateUIHandler.sendMessage(message);
        }
    }
}