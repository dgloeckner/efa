package de.nmichael.efa.gui.widgets;

import org.json.JSONObject;

class WeatherApiParser {

    public static WeatherCurrent parseFromWeatherApi(JSONObject json) {
        JSONObject current = json.getJSONObject("current");
        JSONObject condition = current.getJSONObject("condition");

        WeatherCurrent wd = new WeatherCurrent();
        wd.setTemperature(current.getDouble("temp_c"));
        wd.setWeatherApiCode(condition.getInt("code"));
        wd.setDescription(condition.getString("text"));
        wd.setOpenMeteoCode(-1); // nicht vorhanden

        return wd;
    }
    
}    
