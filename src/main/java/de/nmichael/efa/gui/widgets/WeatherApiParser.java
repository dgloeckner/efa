package de.nmichael.efa.gui.widgets;

import org.json.JSONObject;

class WeatherApiParser {

    public static WeatherDataCurrent parseFromWeatherApi(JSONObject json) {
        JSONObject current = json.getJSONObject("current");
        JSONObject condition = current.getJSONObject("condition");

        WeatherDataCurrent wd = new WeatherDataCurrent();
        wd.setTemperature(current.getDouble("temp_c"));
        wd.setWeatherApiCode(condition.getInt("code"));
        wd.setDescription(condition.getString("text"));
        wd.setOpenMeteoCode(-1); // nicht vorhanden

        return wd;
    }
    
}    
