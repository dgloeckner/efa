package de.nmichael.efa.gui.widgets;

import java.util.Vector;

import de.nmichael.efa.core.items.IItemFactory;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeFile;
import de.nmichael.efa.core.items.ItemTypeInteger;
import de.nmichael.efa.core.items.ItemTypeItemList;
import de.nmichael.efa.core.items.ItemTypeLongLat;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.core.items.ItemTypeStringList;
import de.nmichael.efa.gui.EfaGuiUtils;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

public class WeatherWidgetMulti extends Widget implements IItemFactory {

	private static final String PARAM_WEATHER_SOURCE 		= "MultiWeatherSource";
	private static final String WEATHER_SOURCE_OPENMETEO 	= WeatherWidget.WEATHER_SOURCE_OPENMETEO;
	private static final String WEATHER_SOURCE_WEATHERAPI 	= WeatherWidget.WEATHER_SOURCE_WEATHERAPI;
	private static final String PARAM_TEMPERATURESCALE = "MultiTemperatureScale";
	private static final String PARAM_SPEEDSCALE = "MultiWindSpeedScale";
	private static final String SPEEDSCALE_MPH = WeatherWidget.SPEEDSCALE_MPH;
	private static final String SPEEDSCALE_KMH = WeatherWidget.SPEEDSCALE_KMH;
	private static final String TEMP_CELSIUS = WeatherWidget.TEMP_CELSIUS;
	private static final String TEMP_FAHRENHEIT = WeatherWidget.TEMP_FAHRENHEIT;
	private static final String PARAM_WEATHER_LOCATIONLIST 	= "MultiWeatherLocationList";
	
	//parameters per location item
	
	private static final String PARAM_LATITUDE 				= "MultiWeatherLatitude";
	private static final String PARAM_LONGITUDE 			= "MultiWeatherLongitude";
	private static final String PARAM_CAPTION 				= "MultiWeatherCaption";
	
	private static final String PARAM_POPUPEXECCOMMAND    	= "MultiPopupExecCommand";
	private static final String PARAM_HTMLPOPUPURL        	= "MultiHtmlPopupUrl";
	private static final String PARAM_HTMLPOPWIDTH        	= "MultiHtmlPopupWidth";
	private static final String PARAM_HTMLPOPHEIGHT       	= "MultiHtmlPopupHeight";
	

	private static final String PARAM_WEATHER_LAYOUT 		= "MultiWeatherLayout";
	private static final String WEATHER_LAYOUT_CURRENT_CLASSIC = WeatherWidget.WEATHER_LAYOUT_CURRENT_CLASSIC;
	private static final String WEATHER_LAYOUT_CURRENT_WIND = WeatherWidget.WEATHER_LAYOUT_CURRENT_WIND;
	private static final String WEATHER_LAYOUT_CURRENT_UVINDEX = WeatherWidget.WEATHER_LAYOUT_CURRENT_UVINDEX;
	
	private static final String WEATHER_LAYOUT_FORECASTSIMPLE = WeatherWidget.WEATHER_LAYOUT_FORECASTSIMPLE;
	private static final String WEATHER_LAYOUT_FORECASTCOMPLEX = WeatherWidget.WEATHER_LAYOUT_FORECASTCOMPLEX;

	private ItemTypeItemList locationList;


	/**
	 * @param name
	 * @param description
	 * @param ongui
	 * @param showRefreshInterval
	 */
	public WeatherWidgetMulti() {

		super(International.getString("Wetter (Multi)"), "MultiWetter", International.getString("Wetter (Multi)"), true, true);

		addHeader("MultiWeatherWidgetLocationHeader", IItemType.TYPE_PUBLIC, "", International.getString("Wetter Daten"), 3);
		
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

		addParameterInternal(locationList = new ItemTypeItemList(PARAM_WEATHER_LOCATIONLIST, new Vector<IItemType[]>(), this,
				IItemType.TYPE_PUBLIC, "",
				International.getString("Wetter-Orte")));
	    locationList.setShortDescription(International.getString("Wetter-Orte"));		
		locationList.setRepeatTitle(true);
		super.setEnabled(true);
		super.setPosition(IWidget.POSITION_CENTER);

	}

	

	/*
	 * getDefaultItems is the factory for all location elements
	 */
	public IItemType[] getDefaultItems(String itemName) {
		//which Item do we want to get the elements from?
		if (itemName.endsWith(PARAM_WEATHER_LOCATIONLIST)) {
			
            ItemTypeItemList item = (ItemTypeItemList)getParameterInternal(PARAM_WEATHER_LOCATIONLIST);
            int i = item.size()+1;
			
            // build the GUI
            
            IItemType[] items = new IItemType[9];
            i=0;
            items[i] = new ItemTypeString(PARAM_CAPTION, "Dummy", IItemType.TYPE_PUBLIC, "",
            				International.getString("Beschriftung"));
            items[i++].setPadding(0,0,0,10);

            items[i++] = new ItemTypeLongLat(PARAM_LATITUDE, ItemTypeLongLat.ORIENTATION_NORTH, 52, 25, 9,
            				IItemType.TYPE_PUBLIC, "", International.getString("geographische Breite"));
            items[i++] = new ItemTypeLongLat(PARAM_LONGITUDE, ItemTypeLongLat.ORIENTATION_EAST, 13, 10, 15,
            				IItemType.TYPE_PUBLIC, "", International.getString("geographische Länge"));

            items[i] = new ItemTypeStringList(PARAM_WEATHER_LAYOUT, WEATHER_LAYOUT_CURRENT_UVINDEX,
            				new String[] { WEATHER_LAYOUT_CURRENT_CLASSIC, WEATHER_LAYOUT_CURRENT_WIND, WEATHER_LAYOUT_CURRENT_UVINDEX, WEATHER_LAYOUT_FORECASTSIMPLE, WEATHER_LAYOUT_FORECASTCOMPLEX },
            				new String[] { International.getString("Aktuelles Wetter (Klassisch)"), 
            						International.getString("Aktuelles Wetter (Wind)"), 
            						International.getString("Aktuelles Wetter (UV-Index)"), 
            						International.getString("Vorhersage (einfach)"),
            						International.getString("Vorhersage (komplex)") },
            				IItemType.TYPE_PUBLIC, "", International.getString("Layout"));
            items[i++].setPadding(0, 0, 20, 0);

            items[i++] = EfaGuiUtils.createDescription("WidgetMeteoHTMLPOPUP",IItemType.TYPE_PUBLIC, "", International.getString("Bei Mausklick auf das Astro/Meteo-Widget kann eine HMTL-Seite angezeigt werden."), 3,20,3);
                    
            items[i++] = new ItemTypeFile(PARAM_HTMLPOPUPURL, "",
                            International.getString("HTML-Seite"),
                            International.getString("HTML-Seite"),
                            null,ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                            IItemType.TYPE_PUBLIC, "",
                            International.getString("HTML-Popup") + ": " +
                            International.getString("HTML-Seite"));
            items[i++] = new ItemTypeInteger(PARAM_HTMLPOPWIDTH, 400, 1, Integer.MAX_VALUE, false,
                            IItemType.TYPE_PUBLIC, "",
                            International.getString("HTML-Popup") + ": " +
                            International.getString("Breite"));
            items[i++] = new ItemTypeInteger(PARAM_HTMLPOPHEIGHT, 200, 1, Integer.MAX_VALUE, false,
                            IItemType.TYPE_PUBLIC, "",
                            International.getString("HTML-Popup") + ": " +
                            International.getString("Höhe"));
            items[i++] = new ItemTypeString(PARAM_POPUPEXECCOMMAND, "",
                            IItemType.TYPE_PUBLIC, "",
                            International.getMessage("Auszuführendes Kommando vor {event}",
                            International.getString("Popup")));
            return items;
		}
		return null;
	}	
	
    ItemTypeItemList getLocationList() {
        return (ItemTypeItemList)getParameterInternal(PARAM_WEATHER_LOCATIONLIST);
    }	
	

	@Override
    public Vector <WidgetInstance> createInstances(){
		Vector <WidgetInstance> returnList = new Vector <WidgetInstance>();
		
		ItemTypeItemList myWList=this.getLocationList();
		if (myWList==null) {
			return returnList;
		}
		
		for (int i = 0; i < myWList.size(); i++) {
			WeatherWidgetInstance wwi = new WeatherWidgetInstance();

			wwi.setUpdateInterval(this.getUpdateInterval());
			wwi.setSource(this.getWeatherSource());
			wwi.setSpeedScale(this.getWeatherSpeedScale());
			wwi.setTempScale(this.getWeatherTempScale());

			wwi.setCaption(this.getWeatherCaption(myWList,i));
			wwi.setLatitude(this.getWeatherLatitude(myWList,i));
			wwi.setLayout(this.getWeatherLayout(myWList,i));
			wwi.setLongitude(this.getWeatherLongitude(myWList,i));
	
			wwi.setHtmlPopupHeight(this.getHtmlPopupHeight(myWList,i));
			wwi.setHtmlPopupURL(this.getHtmlPopupUrl(myWList,i));
			wwi.setHtmlPopupWidth(this.getHtmlPopupWidth(myWList,i));
			wwi.setPopupExecCommand(this.getPopupExecCommand(myWList,i));
		
			returnList.add(wwi);
		}
		
		return returnList;
	};    
    

	public String getWeatherCaption(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeString)list.getItem(i, PARAM_CAPTION)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return "";
        }
	}

	private String getWeatherLongitude(ItemTypeItemList list, int i) {
		return getLongLat(PARAM_LONGITUDE, list, i);
	}

	private String getLongLat(String theName, ItemTypeItemList list, int i) {
		ItemTypeLongLat val;
        try {
        	val = (ItemTypeLongLat)list.getItem(i, theName);
        } catch(Exception e) {
            Logger.logdebug(e);
            return "";
        }
        
		if (val != null) {
			int[] coords = val.getValueCoordinates();
			return coords[0] + "." + coords[1];
		}
		return null;
	}

	private String getWeatherLatitude(ItemTypeItemList list, int i) {
		return getLongLat(PARAM_LATITUDE, list, i);
	}

	private String getWeatherLayout(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeStringList)list.getItem(i, PARAM_WEATHER_LAYOUT)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
        	return "";
        }
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

    public String getPopupExecCommand(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeString)list.getItem(i, PARAM_POPUPEXECCOMMAND)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return "";
        }
    }

    public String getHtmlPopupUrl(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeString)list.getItem(i, PARAM_HTMLPOPUPURL)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return "";
        }    
    }

    public int getHtmlPopupWidth(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeInteger)list.getItem(i, PARAM_HTMLPOPWIDTH)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return 100;
        }
    }

    public int getHtmlPopupHeight(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeInteger)list.getItem(i, PARAM_HTMLPOPHEIGHT)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return 100;
        }        
    }
	

	

}
