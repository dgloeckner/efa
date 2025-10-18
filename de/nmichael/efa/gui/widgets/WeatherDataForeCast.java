package de.nmichael.efa.gui.widgets;

public class WeatherDataForeCast {
    private double latitude;
    private double longitude;
    private double elevation;
    private WeatherCurrent currentWeather;
    private HourlyUnits hourlyUnits;
    private HourlyData hourly;
	public HourlyData getHourly() {
		return hourly;
	}
	public void setHourly(HourlyData hourly) {
		this.hourly = hourly;
	}
	public HourlyUnits getHourlyUnits() {
		return hourlyUnits;
	}
	public void setHourlyUnits(HourlyUnits hourlyUnits) {
		this.hourlyUnits = hourlyUnits;
	}
	public WeatherCurrent getCurrentWeather() {
		return currentWeather;
	}
	public void setCurrentWeather(WeatherCurrent currentWeather) {
		this.currentWeather = currentWeather;
	}
	public double getElevation() {
		return elevation;
	}
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}


