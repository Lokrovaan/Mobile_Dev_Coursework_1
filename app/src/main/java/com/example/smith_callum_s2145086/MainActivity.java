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
import android.widget.ViewFlipper;
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

//Callum Smith - S2145086
public class MainActivity extends AppCompatActivity implements OnClickListener {
    private ViewSwitcher switcher;
    private Handler updateUIHandler = null;
    private final static int RETRIEVE_OBSERVATION = 1;
    private final static int RETRIEVE_3_DAY_WEATHER = 2;
    private TextView observationDisplay;
    private TextView threeDayWeatherDisplay;
    private String result;
    private Button glasgowObservationButton;
    private Button glasgowButton;
    private Button londonButton;
    private Button newYorkButton;
    private Button omanButton;
    private Button mauritiusButton;
    private Button bangladeshButton;
    private Button toHomeButton;
    private Button toLocationsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switcher = (ViewSwitcher) findViewById(R.id.myVSwitcher);
        createUpdateUiHandler();

        // Set up the links to the graphical components
        switcher = (ViewSwitcher) findViewById(R.id.myVSwitcher);
        observationDisplay = (TextView) findViewById(R.id.observationDisplay);
        threeDayWeatherDisplay = (TextView) findViewById(R.id.threeDayDataDisplay);
        glasgowObservationButton = (Button) findViewById(R.id.currentGlasgowWeatherButton);
        glasgowButton = (Button) findViewById(R.id.glasgowButton);
        londonButton = (Button) findViewById(R.id.londonButton);
        newYorkButton = (Button) findViewById(R.id.newYorkButton);
        omanButton = (Button) findViewById(R.id.omanButton);
        mauritiusButton = (Button) findViewById(R.id.mauritiusButton);
        bangladeshButton = (Button) findViewById(R.id.bangladeshButton);
        toHomeButton = (Button) findViewById(R.id.toHomeButton);
        toLocationsButton = (Button) findViewById(R.id.toLocationsButton);

        //Set listener for buttons
        glasgowButton.setOnClickListener(this);
        londonButton.setOnClickListener(this);
        newYorkButton.setOnClickListener(this);
        omanButton.setOnClickListener(this);
        mauritiusButton.setOnClickListener(this);
        bangladeshButton.setOnClickListener(this);
        toHomeButton.setOnClickListener(this);
        toLocationsButton.setOnClickListener(this);
        glasgowObservationButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == glasgowObservationButton) {
            new Thread(new RetrieveObservation()).start();
        } else if (v == glasgowButton) {
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
        } else if (v == toHomeButton) {
            switcher.showPrevious();
        } else if (v == toLocationsButton) {
            switcher.showNext();
        }
    }

    private void createUpdateUiHandler() {
        if (updateUIHandler == null) {
            updateUIHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    // Check which thread sent the message
                    if (msg.what == RETRIEVE_OBSERVATION) {
                        observationDisplay.setText((String) msg.obj);
                    } else if (msg.what == RETRIEVE_3_DAY_WEATHER) {
                        threeDayWeatherDisplay.setText((String) msg.obj);
                    }
                }
            };
        }
    }

    class RetrieveObservation implements Runnable {
        String url = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/2648579";


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
                            String[] titleArray = temp.split("-");
                            String day = titleArray[0].trim();
                            String time = titleArray[1].split(":")[0] + titleArray[1].split(":")[1].trim();
                            String summary = titleArray[1].split(":")[2].split(",")[0].trim();

                            Log.d("MyTag", "Day is " + day +
                                    "\nWeather is " + summary +
                                    "\nTime is " + time);
                            observation.setDay(day);
                            observation.setTime(time);
                            observation.setSummary(summary);
                        } else if (xpp.getName().equalsIgnoreCase("description") && useTitleAndDescription) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            Log.d("MyTag", "description is " + temp);

                            //split <description> into multiple weather data variables
                            String[] descArray = temp.split(",");
                            Log.d("MyTag", "descArray is " + descArray.length + " entries long");

                            int count = 0;
                            for (String j : descArray) {
                                if (descArray[count].contains("Temperature")) {
                                    String currentTemp = descArray[count].trim();
                                    observation.setCurrentTemperature(currentTemp);
                                    Log.d("MyTag", "maxTemp is " + currentTemp);
                                    count++;
                                } else if (descArray[count].contains("Wind Direction")) {
                                    String windDirection = descArray[count].trim();
                                    observation.setWindDirection(windDirection);
                                    Log.d("MyTag", "windDirection is " + windDirection);
                                    count++;
                                } else if (descArray[count].contains("Wind Speed")) {
                                    String windSpeed = descArray[count].trim();
                                    observation.setWindSpeed(windSpeed);
                                    Log.d("MyTag", "windSpeed is " + windSpeed);
                                    count++;
                                } else if (descArray[count].contains("Visibility")) {
                                    String visibility = descArray[count].trim();
                                    observation.setVisibility(visibility);
                                    Log.d("MyTag", "visibility is " + visibility);
                                    count++;
                                } else if (descArray[count].contains("Pressure")) {
                                    String pressure = descArray[count].trim();
                                    observation.setPressure(pressure);
                                    Log.d("MyTag", "pressure is " + pressure);
                                    count++;
                                } else if (descArray[count].contains("Humidity")) {
                                    String humidity = descArray[count].trim();
                                    observation.setHumidity(humidity);
                                    Log.d("MyTag", "humidity is " + humidity);
                                    count++;
                                } else {
                                    count++;
                                }
                            }
                            //Check if any variables weren't assigned and give them a value so they aren't null
                            if (observation.getDay() == null) {
                                observation.setDay("Day Unavailable");
                            }
                            if (observation.getTime() == null) {
                                observation.setTime("Time Unavailable");
                            }
                            if (observation.getSummary() == null) {
                                observation.setSummary("Summary unavailable");
                            }
                            if (observation.getCurrentTemperature() == null) {
                                observation.setCurrentTemperature("Current Temperature Unavailable");
                            }
                            if (observation.getWindDirection() == null) {
                                observation.setWindDirection("Wind Direction Unavailable");
                            }
                            if (observation.getWindSpeed() == null) {
                                observation.setWindSpeed("Wind Speed Unavailable");
                            }
                            if (observation.getVisibility() == null) {
                                observation.setVisibility("Visibility Unavailable");
                            }
                            if (observation.getPressure() == null) {
                                observation.setPressure("Pressure Unavailable");
                            }
                            if (observation.getPressureStatus() == null) {
                                observation.setPressureStatus("Pressure Status Unavailable");
                            }
                            if (observation.getHumidity() == null) {
                                observation.setHumidity("Humidity Unavailable");
                            }
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
                            Log.d("MyTag", "descArray is " + descArray.length + " entries long");

                            int count = 0;
                            for (String j : descArray) {
                                if (descArray[count].contains("Maximum Temperature")) {
                                    String maxTemp = descArray[count].trim();
                                    dayOfWeather.setMaxTemp(maxTemp);
                                    Log.d("MyTag", "maxTemp is " + maxTemp);
                                    count++;
                                } else if (descArray[count].contains("Minimum Temperature")) {
                                    String minTemp = descArray[count].trim();
                                    dayOfWeather.setMinTemp(minTemp);
                                    Log.d("MyTag", "minTemp is " + minTemp);
                                    count++;
                                } else if (descArray[count].contains("Wind Direction")) {
                                    String windDirection = descArray[count].trim();
                                    dayOfWeather.setWindDirection(windDirection);
                                    Log.d("MyTag", "windDirection is " + windDirection);
                                    count++;
                                } else if (descArray[count].contains("Wind Speed")) {
                                    String windSpeed = descArray[count].trim();
                                    dayOfWeather.setWindSpeed(windSpeed);
                                    Log.d("MyTag", "windSpeed is " + windSpeed);
                                    count++;
                                } else if (descArray[count].contains("Visibility")) {
                                    String visibility = descArray[count].trim();
                                    dayOfWeather.setVisibility(visibility);
                                    Log.d("MyTag", "visibility is " + visibility);
                                    count++;
                                } else if (descArray[count].contains("Pressure")) {
                                    String pressure = descArray[count].trim();
                                    dayOfWeather.setPressure(pressure);
                                    Log.d("MyTag", "pressure is " + pressure);
                                    count++;
                                } else if (descArray[count].contains("Humidity")) {
                                    String humidity = descArray[count].trim();
                                    dayOfWeather.setHumidity(humidity);
                                    Log.d("MyTag", "humidity is " + humidity);
                                    count++;
                                } else if (descArray[count].contains("UV Risk")) {
                                    String uvRisk = descArray[count].trim();
                                    dayOfWeather.setUVRisk(uvRisk);
                                    Log.d("MyTag", "uvRisk is " + uvRisk);
                                    count++;
                                } else if (descArray[count].contains("Pollution")) {
                                    String pollution = descArray[count].trim();
                                    dayOfWeather.setPollution(pollution);
                                    Log.d("MyTag", "pollution is " + pollution);
                                    count++;
                                } else if (descArray[count].contains("Sunrise")) {
                                    String sunriseTime = descArray[count].trim();
                                    dayOfWeather.setSunriseTime(sunriseTime);
                                    Log.d("MyTag", "sunriseTime is " + sunriseTime);
                                    count++;
                                } else if (descArray[count].contains("Sunset")) {
                                    String sunsetTime = descArray[count].trim();
                                    dayOfWeather.setSunsetTime(sunsetTime);
                                    Log.d("MyTag", "sunsetTime is " + sunsetTime);
                                    count++;
                                } else {
                                    count++;
                                }
                            }
                            //Check if any variables weren't assigned and give them a value so they aren't null
                            if (dayOfWeather.getDay() == null) {
                                dayOfWeather.setDay("Day Unavailable");
                            }
                            if (dayOfWeather.getWeather() == null) {
                                dayOfWeather.setWeather("Weather Status Unavailable");
                            }
                            if (dayOfWeather.getMaxTemp() == null) {
                                dayOfWeather.setMaxTemp("Maximum Temperature unavailable");
                            }
                            if (dayOfWeather.getMinTemp() == null) {
                                dayOfWeather.setMinTemp("Minimum Temperature Unavailable");
                            }
                            if (dayOfWeather.getWindDirection() == null) {
                                dayOfWeather.setWindDirection("Wind Direction Unavailable");
                            }
                            if (dayOfWeather.getWindSpeed() == null) {
                                dayOfWeather.setWindSpeed("Wind Speed Unavailable");
                            }
                            if (dayOfWeather.getVisibility() == null) {
                                dayOfWeather.setVisibility("Visibility Unavailable");
                            }
                            if (dayOfWeather.getPressure() == null) {
                                dayOfWeather.setPressure("Pressure Unavailable");
                            }
                            if (dayOfWeather.getHumidity() == null) {
                                dayOfWeather.setHumidity("Humidity Unavailable");
                            }
                            if (dayOfWeather.getUVRisk() == null) {
                                dayOfWeather.setUVRisk("UV Risk Unavailable");
                            }
                            if (dayOfWeather.getPollution() == null) {
                                dayOfWeather.setPollution("Pollution Unavailable");
                            }
                            if (dayOfWeather.getSunriseTime() == null) {
                                dayOfWeather.setSunriseTime("Sunrise Time Unavailable");
                            }
                            if (dayOfWeather.getSunsetTime() == null) {
                                dayOfWeather.setSunsetTime("Sunset Time Unavailable");
                            }
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
            message.what = RETRIEVE_3_DAY_WEATHER;
            message.obj = threeDayForecast;

            // Send message to main thread Handler
            updateUIHandler.sendMessage(message);
        }
    }
}