package com.example.smith_callum_s2145086;

public class DayOfWeather {
    private String title;
    private String description;

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return "Day of Weather: " + title
                + "\nDescription: " + description + "\n";
    }
}
