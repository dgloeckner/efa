/**
 * 
 */
package de.nmichael.efa.gui.widgets;

import java.util.Vector;

import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeFile;
import de.nmichael.efa.core.items.ItemTypeInteger;
import de.nmichael.efa.core.items.ItemTypeLongLat;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.core.items.ItemTypeStringList;
import de.nmichael.efa.util.International;

/**
 * 
 */
public class WeatherWidget extends Widget {

	private static final String PARAM_LATITUDE = "WeatherLatitude";
	private static final String PARAM_LONGITUDE = "WeatherLongitude";
	private static final String PARAM_CAPTION = "WeatherCaption";
	private static final String PARAM_TEMPERATURESCALE = "TemperatureScale";
	public  static final String TEMP_CELSIUS = "CELSIUS";
	public  static final String TEMP_FAHRENHEIT = "FAHRENHEIT";
	private static final String PARAM_SPEEDSCALE = "WindSpeedScale";
	public static final String SPEEDSCALE_MPH = "mph";
	public static final String SPEEDSCALE_KMH = "kmh";

	private static final String PARAM_POPUPEXECCOMMAND    = "PopupExecCommand";
	private static final String PARAM_HTMLPOPUPURL        = "HtmlPopupUrl";
	private static final String PARAM_HTMLPOPWIDTH        = "HtmlPopupWidth";
	private static final String PARAM_HTMLPOPHEIGHT       = "HtmlPopupHeight";
	
	private static final String PARAM_WEATHER_SOURCE = "WeatherSource";
	public static final String WEATHER_SOURCE_OPENMETEO = "WeatherSourceOpenMeteoFree";
	public static final String WEATHER_SOURCE_WEATHERAPI = "WeatherSourceWeatherApi";

	private static final String PARAM_WEATHER_LAYOUT = "WeatherLayout";
	public static final String WEATHER_LAYOUT_CURRENT_CLASSIC = "WeatherLayoutLayoutCurrentClassic";
	public static final String WEATHER_LAYOUT_CURRENT_WIND = "WeatherLayoutLayoutCurrentWind";
	public static final String WEATHER_LAYOUT_CURRENT_UVINDEX = "WeatherLayoutLayoutCurrentUVIndex";
	public static final String WEATHER_LAYOUT_FORECASTSIMPLE = "WeatherLayoutForecastSimple";
	public static final String WEATHER_LAYOUT_FORECASTCOMPLEX = "WeatherLayoutForecastComplex";




	/**
	 * @param name
	 * @param description
	 * @param ongui
	 * @param showRefreshInterval
	 */
	public WeatherWidget() {

		super(International.getString("Wetter"), "Wetter", International.getString("Wetter"), true, true);

		addHeader("WeatherWidgetLocationHeader", IItemType.TYPE_PUBLIC, "", International.getString("Wetter Daten"), 3);
		
		addParameterInternal(new ItemTypeStringList(PARAM_WEATHER_SOURCE, WEATHER_SOURCE_OPENMETEO,
				new String[] { WEATHER_SOURCE_OPENMETEO, WEATHER_SOURCE_WEATHERAPI },
				new String[] { International.getString("OpenMeteo free API (Europe/North America)"),
						International.getString("WeatherAPI") },
				IItemType.TYPE_PUBLIC, "", International.getString("Quelle für Wetterdaten")));

		addParameterInternal(new ItemTypeStringList(PARAM_TEMPERATURESCALE, TEMP_CELSIUS,
				new String[] { TEMP_CELSIUS, TEMP_FAHRENHEIT },
				new String[] { International.getString("Celsius"), International.getString("Fahrenheit") },
				IItemType.TYPE_PUBLIC, "", International.getString("Temperaturskala")),10,0);

		addParameterInternal(new ItemTypeStringList(PARAM_SPEEDSCALE, SPEEDSCALE_KMH,
				new String[] { SPEEDSCALE_KMH, SPEEDSCALE_MPH },
				new String[] { International.getString("km/h"), International.getString("mph") }, IItemType.TYPE_PUBLIC,
				"", International.getString("Windgeschwindigkeit-Skala")));

		addHeader("WeatherWidgetLocationHeader"+"1", IItemType.TYPE_PUBLIC, "", International.getString("Ort"), 3);
		
		addParameterInternal(new ItemTypeString(PARAM_CAPTION, "Dummy", IItemType.TYPE_PUBLIC, "",
				International.getString("Beschriftung")),0,10);

		addParameterInternal(new ItemTypeLongLat(PARAM_LATITUDE, ItemTypeLongLat.ORIENTATION_NORTH, 52, 25, 9,
				IItemType.TYPE_PUBLIC, "", International.getString("geographische Breite")));

		addParameterInternal(new ItemTypeLongLat(PARAM_LONGITUDE, ItemTypeLongLat.ORIENTATION_EAST, 13, 10, 15,
				IItemType.TYPE_PUBLIC, "", International.getString("geographische Länge")));

		addParameterInternal(new ItemTypeStringList(PARAM_WEATHER_LAYOUT, WEATHER_LAYOUT_CURRENT_UVINDEX,
				new String[] { WEATHER_LAYOUT_CURRENT_CLASSIC, WEATHER_LAYOUT_CURRENT_WIND, WEATHER_LAYOUT_CURRENT_UVINDEX, WEATHER_LAYOUT_FORECASTSIMPLE, WEATHER_LAYOUT_FORECASTCOMPLEX },
				new String[] { International.getString("Aktuelles Wetter (Klassisch)"), 
						International.getString("Aktuelles Wetter (Wind)"), 
						International.getString("Aktuelles Wetter (UV-Index)"), 
						International.getString("Vorhersage (einfach)"),
						International.getString("Vorhersage (komplex)") },
				IItemType.TYPE_PUBLIC, "", International.getString("Layout")), 20, 0);

		
        addHeader("WidgetMeteoHTML",IItemType.TYPE_PUBLIC, "", International.getString("HTML-Seite anzeigen"), 3);  
        addDescription("WidgetMeteoHTMLPOPUP",IItemType.TYPE_PUBLIC, "", International.getString("Bei Mausklick auf das Astro/Meteo-Widget kann eine HMTL-Seite angezeigt werden."), 3,3,3);
        
        addParameterInternal(new ItemTypeFile(PARAM_HTMLPOPUPURL, "",
                International.getString("HTML-Seite"),
                International.getString("HTML-Seite"),
                null,ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_PUBLIC, "",
                International.getString("HTML-Popup") + ": " +
                International.getString("HTML-Seite")));
        addParameterInternal(new ItemTypeInteger(PARAM_HTMLPOPWIDTH, 400, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, "",
                International.getString("HTML-Popup") + ": " +
                International.getString("Breite")));
        addParameterInternal(new ItemTypeInteger(PARAM_HTMLPOPHEIGHT, 200, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, "",
                International.getString("HTML-Popup") + ": " +
                International.getString("Höhe")));
        addParameterInternal(new ItemTypeString(PARAM_POPUPEXECCOMMAND, "",
                IItemType.TYPE_PUBLIC, "",
                International.getMessage("Auszuführendes Kommando vor {event}",
                International.getString("Popup"))));



		super.setEnabled(true);
		super.setPosition(IWidget.POSITION_CENTER);

	}

	@Override
    public Vector <WidgetInstance> createInstances(){
		Vector <WidgetInstance> returnList = new Vector <WidgetInstance>();
		
		WeatherWidgetInstance wwi = new WeatherWidgetInstance();
		wwi.setUpdateInterval(this.getUpdateInterval());
		wwi.setCaption(this.getWeatherCaption());
		wwi.setLatitude(this.getWeatherLatitude());
		wwi.setLayout(this.getWeatherLayout());
		wwi.setLongitude(this.getWeatherLongitude());
		wwi.setSource(this.getWeatherSource());
		wwi.setSpeedScale(this.getWeatherSpeedScale());
		wwi.setTempScale(this.getWeatherTempScale());

		wwi.setHtmlPopupHeight(this.getHtmlPopupHeight());
		wwi.setHtmlPopupURL(this.getHtmlPopupUrl());
		wwi.setHtmlPopupWidth(this.getHtmlPopupWidth());
		wwi.setPopupExecCommand(this.getPopupExecCommand());
		
		returnList.add(wwi);
		
		return returnList;
	};
	
	public String getWeatherCaption() {
		return ((ItemTypeString) getParameterInternal(PARAM_CAPTION)).getValue();
	}

	private String getWeatherLongitude() {
		return getLongLat(PARAM_LONGITUDE);
	}

	private String getLongLat(String Name) {
		ItemTypeLongLat val = (ItemTypeLongLat) getParameterInternal(Name);
		if (val != null) {
			int[] coords = val.getValueCoordinates();
			return coords[0] + "." + coords[1];
		}
		return null;
	}

	private String getWeatherLatitude() {
		return getLongLat(PARAM_LATITUDE);
	}

	private String getWeatherLayout() {
		return ((ItemTypeStringList) getParameterInternal(PARAM_WEATHER_LAYOUT)).toString();
	}

	private String getWeatherSource() {
		return ((ItemTypeStringList) getParameterInternal(PARAM_WEATHER_SOURCE)).toString();
	}

	public String getWeatherTempScale() {
		return ((ItemTypeStringList) getParameterInternal(PARAM_TEMPERATURESCALE)).toString();
	}

	public String getWeatherSpeedScale() {
		return ((ItemTypeStringList) getParameterInternal(PARAM_SPEEDSCALE)).toString();
	}

    public String getPopupExecCommand() {
        return ((ItemTypeString)getParameterInternal(PARAM_POPUPEXECCOMMAND)).toString();
    }

    public String getHtmlPopupUrl() {
        return ((ItemTypeFile)getParameterInternal(PARAM_HTMLPOPUPURL)).toString();
    }

    public int getHtmlPopupWidth() {
        return ((ItemTypeInteger)getParameterInternal(PARAM_HTMLPOPWIDTH)).getValue();
    }

    public int getHtmlPopupHeight() {
        return ((ItemTypeInteger)getParameterInternal(PARAM_HTMLPOPHEIGHT)).getValue();
    }
	
}
