package com.example.smith_callum_s2145086;

//Callum Smith - S2145086
public class Observation {
    //Retrieved from <title> tag in RSS Feed
    private String day;
    private String time;
    private String summary;

    //Retrieved from <description> tag in RSS Feed
    private String currentTemperature;
    private String windDirection;
    private String windSpeed;
    private String humidity;
    private String pressure;
    private String pressureStatus;
    private String visibility;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(String currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getPressureStatus() {
        return pressureStatus;
    }

    public void setPressureStatus(String pressureStatus) {
        this.pressureStatus = pressureStatus;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return day + "\n" +
                time + "\n" +
                summary + "\n" +
                currentTemperature + "\n" +
                windDirection + "\n" +
                windSpeed + "\n" +
                humidity + "\n" +
                pressure + "\n" +
                pressureStatus + "\n" +
                visibility + "\n" +
                "\n";
    }
}
