package de.nmichael.efa.gui.widgets;

import java.util.List;

public class WeatherDataHourly {
	private List<String> time;
    private List<Double> temperature2m;
    private List<Integer> weatherCode;
    private List<Double> windSpeed10m;
    private List<Integer> windDirection10m;
    private List<Double> uvIndex;
    private List<Integer> isDay;
    private List<Double> precipitation;
    private List<Double> precipitationProb;
    
	public List<Integer> getIsDay() {
		return isDay;
	}
	public void setIsDay(List<Integer> isDay) {
		this.isDay = isDay;
	}
	public List<Double> getUvIndex() {
		return uvIndex;
	}
	public void setUvIndex(List<Double> uvIndex) {
		this.uvIndex = uvIndex;
	}
	public List<Integer> getWindDirection10m() {
		return windDirection10m;
	}
	public void setWindDirection10m(List<Integer> windDirection10m) {
		this.windDirection10m = windDirection10m;
	}
	public List<Double> getWindSpeed10m() {
		return windSpeed10m;
	}
	public void setWindSpeed10m(List<Double> windSpeed10m) {
		this.windSpeed10m = windSpeed10m;
	}
	public List<Integer> getWeatherCode() {
		return weatherCode;
	}
	public void setWeatherCode(List<Integer> weatherCode) {
		this.weatherCode = weatherCode;
	}
	public List<Double> getTemperature2m() {
		return temperature2m;
	}
	public void setTemperature2m(List<Double> temperature2m) {
		this.temperature2m = temperature2m;
	}
	public List<String> getTime() {
		return time;
	}
	public void setTime(List<String> time) {
		this.time = time;
	}
	public List<Double> getPrecipitation() {
		return precipitation;
	}
	public void setPrecipitation(List<Double> precipitation) {
		this.precipitation = precipitation;
	}
	public List<Double> getPrecipitationProb() {
		return precipitationProb;
	}
	public void setPrecipitationProb(List<Double> precipitationProb) {
		this.precipitationProb = precipitationProb;
	}  
}
