/**
 * 
 */
package de.nmichael.efa.gui.widgets;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeLongLat;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.core.items.ItemTypeStringList;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.gui.util.RoundedBorder;
import de.nmichael.efa.gui.util.RoundedPanel;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

/**
 * 
 */
public class WeatherWidget extends Widget {

	private static final String PARAM_SHOWWEATHER = "ShowWeather";
	private static final String PARAM_LATITUDE = "WeatherLatitude";
	private static final String PARAM_LONGITUDE = "WeatherLongitude";
	private static final String PARAM_CAPTION = "WeatherCaption";
	private static final String PARAM_TEMPERATURESCALE = "TemperatureScale";
	private static final String TEMP_CELSIUS = "CELSIUS";
	private static final String TEMP_FAHRENHEIT = "FAHRENHEIT";
	private static final String PARAM_SPEEDSCALE = "WindSpeedScale";
	private static final String SPEEDSCALE_MPH = "mph";
	private static final String SPEEDSCALE_KMH = "kmh";

	private static final String PARAM_WEATHER_SOURCE = "WeatherSource";
	private static final String WEATHER_SOURCE_OPENMETEO = "WeatherSourceOpenMeteoFree";
	private static final String WEATHER_SOURCE_WEATHERAPI = "WeatherSourceWeatherApi";

	private static final String PARAM_WEATHER_LAYOUT = "WeatherLayout";
	private static final String WEATHER_LAYOUT_CURRENT_CLASSIC = "WeatherLayoutLayoutCurrentClassic";
	private static final String WEATHER_LAYOUT_CURRENT_WIND = "WeatherLayoutLayoutCurrentWind";
	private static final String WEATHER_LAYOUT_CURRENT_UVINDEX = "WeatherLayoutLayoutCurrentUVIndex";
	
	private static final String WEATHER_LAYOUT_FORECASTSIMPLE = "WeatherLayoutForecastSimple";
	private static final String WEATHER_LAYOUT_FORECASTCOMPLEX = "WeatherLayoutForecastComplex";

	private volatile JPanel mainPanel = new JPanel();
	private RoundedPanel roundPanel = new RoundedPanel();
	private WeatherUpdater weatherUpdater;


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


		super.setEnabled(true);
		super.setPosition(IWidget.POSITION_CENTER);

	}

	@Override
	public void runWidgetWarnings(int mode, boolean actionBegin, LogbookRecord r) {
		// Nothing to do here
	}

	@Override
	public void stop() {
        try {
        	// stopHTML also lets the thread die, and efaBths is responsible to set up a new thread.
        	weatherUpdater.stopRunning();
        } catch(Exception eignore) {
            // nothing to do, might not be initialized
        }
	}

	@Override
	public void construct() {
		Logger.log(Logger.DEBUG, "constructing Weatherpanel ");

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setName("WeatherWidget-MainPanel");
		roundPanel = new RoundedPanel();
		
		roundPanel.setLayout(new GridBagLayout());
		roundPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());
		roundPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		roundPanel.setBorder(new RoundedBorder(Daten.efaConfig.getToolTipForegroundColor()));
		roundPanel.setName("WeatherWidget-RoundPanel");
		
		mainPanel.add(roundPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		addInfoPanel();
		roundPanel.setMinimumSize(new Dimension(240, 120));
		roundPanel.revalidate();

	   	try {
	   		weatherUpdater = new WeatherUpdater(roundPanel, this);
	   		weatherUpdater.start();
            
        } catch(Exception e) {
            Logger.log(e);
        }		


	}

	private void addInfoPanel() {
		JTextArea errorLabel1= new JTextArea();
		errorLabel1.setFont(
				mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize())));
		errorLabel1.setFont(errorLabel1.getFont().deriveFont(Font.BOLD));
		errorLabel1.setText(International.getString("Ermittle Wetterdaten..."));
		errorLabel1.setLineWrap(true);
		errorLabel1.setOpaque(false);
		errorLabel1.setEditable(false);
		
		JPanel titlePanel = WeatherRenderer.getLocationHeader(this);
		titlePanel.setBackground(Daten.efaConfig.getErrorHeaderBackgroundColor());
		titlePanel.setForeground(Daten.efaConfig.getErrorHeaderForegroundColor());
		
		// Build the main panel view

		roundPanel.add(titlePanel, new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));	
		
		roundPanel.add(errorLabel1, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
	}



	@Override
	public JComponent getComponent() {
		// TODO Auto-generated method stub
		return mainPanel;
	}

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

	public String getTempLabel(boolean withUnit) {
		if (!withUnit) {
			return "°";
		} else {
			return (getWeatherTempScale().equals(TEMP_CELSIUS) ? "°C" : "°F");
		}
	}

		/**
		 * The WeatherUpdate obtains Weather Data in a separate thread, so that
		 * the time for getting weather data does not affect the main thread,
		 * and efaBoathouse is still ready for interaction with the user.
		 */
	   class WeatherUpdater extends Thread {

	        volatile boolean keepRunning = true;
	        private JPanel panel;
	        private	JPanel innerPanel;
	        private WeatherWidget ww = null;
	        private WeatherDataForeCast wdf = null;
	        private long lastWeatherUpdate = 0;
	        private boolean firstRun=true;
	        
	        public WeatherUpdater(JPanel thePanel, WeatherWidget ww) {
	        	this.panel=thePanel;
	        	this.ww = ww;
	        }

	        private boolean needsToUpdateWeather() {
	        	return (this.wdf == null || (System.currentTimeMillis() >= lastWeatherUpdate+(ww.getUpdateInterval()*1000)));
	        }
	        
	        public void run() {
	        	this.setName("WeatherWidget.WeatherUpdater");
	            
	            while (keepRunning) {
	            	
	            	try {
		            	
	            		if (needsToUpdateWeather()) {
	            			wdf = getWeather(ww.getWeatherSource(), ww.getWeatherLongitude(), ww.getWeatherLatitude());
	            			Logger.log(Logger.DEBUG, "Wetterdaten geholt");
	            		}
	            		
	            		
	        			if (firstRun && wdf != null && wdf.getStatus() == true) {
		            		
	        				innerPanel = new JPanel();
		            		innerPanel.setLayout(new GridBagLayout());
		            		innerPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());
		            		innerPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		            		innerPanel.setBorder(BorderFactory.createEmptyBorder());
		            		innerPanel.setName("WeatherWidget-InnerPanel");

		            		if (getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT_CLASSIC)) {
	        					WeatherRendererCurrentClassic.renderWeather(wdf, innerPanel, ww);
	        				} else if (getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT_WIND)) {
	        					WeatherRendererCurrentWind.renderWeather(wdf, innerPanel, ww);
	        				} else if (getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT_UVINDEX)) {
	        					WeatherRendererCurrentUVIndex.renderWeather(wdf, innerPanel, ww);
	        				} else if (getWeatherLayout().equals(WEATHER_LAYOUT_FORECASTSIMPLE)){
	        					WeatherRendererForeCastSimple.renderWeather(wdf, innerPanel, ww);
	        				} else {
	        					WeatherRendererForeCastComplex.renderWeather(wdf, innerPanel, ww);
	        				}
	            			Logger.log(Logger.DEBUG, "Wetterdaten gerendert in innerpanel");
	        			} else {
		            		innerPanel = new JPanel();
		            		innerPanel.setLayout(new GridBagLayout());
		            		innerPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());
		            		innerPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		            		innerPanel.setBorder(BorderFactory.createEmptyBorder());
		            		innerPanel.setName("WeatherWidget-InnerPanel");
	        				
	        				addError(wdf);
	        				firstRun=true;
	            			Logger.log(Logger.DEBUG, "ErrorPanel dargestellt");
	        			}
		            	
	        			innerPanel.invalidate();
		            	//Use invokelater as swing threadsafe ways
		            	SwingUtilities.invokeLater(new UpdateWeatherRunner(this.panel, innerPanel));
		
            			Logger.log(Logger.DEBUG, "Warte auf update");
		            	//wait until next full minute plus one sec. this is more accurate than just waiting 60.000 msec
		            	//from a random offset.
		           		long waitTime=EfaUtil.getMilliSecondsToFullMinute()+1000;
		                Thread.sleep(waitTime);

	            	} catch (InterruptedException e) {
	                	//This is when the thread gets interrupted when it is sleeping.
	                	EfaUtil.foo();            
	                } catch (Exception e) {
	                	Throwable t = e.getCause();
	                	if (t.getClass().getName().equalsIgnoreCase("java.lang.InterruptedException")) {
	                		EfaUtil.foo();
	                	} else {
	                		Logger.logdebug(e);
	                	}
	                }
		                
	            }
	        }
	        
	        public synchronized void stopRunning() {
	            keepRunning = false;
	            interrupt(); // wake up thread
	        }


	    	private WeatherDataForeCast getWeather(String source, String longitude, String latitude) {

	    		if (source.equals(WEATHER_SOURCE_OPENMETEO)) {

	    			try {

	    				return fetchMeteoWeather(longitude, latitude);

	    			} catch (Exception e) {
	    				Logger.logdebug(e);
	    			}
	    		}
	    		return null;
	    	}

	    	private WeatherDataForeCast fetchMeteoWeather(String longitude, String latitude) {

	    		String urlStr = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude
	    				+ "&daily=weather_code,sunshine_duration,uv_index_max,uv_index_clear_sky_max,precipitation_sum,temperature_2m_max,temperature_2m_min,wind_speed_10m_max"
	    				+ "&hourly=temperature_2m,weather_code,wind_speed_10m,wind_direction_10m,uv_index,is_day,precipitation,precipitation_probability"
	    				+ "&t=temperature_2m,is_day,weather_code,wind_speed_10m,wind_direction_10m"
	    				+ (ww.getWeatherTempScale().equals(TEMP_FAHRENHEIT) ? "&temperature_unit=fahrenheit" : "")
	    				+ (ww.getWeatherSpeedScale().equals(SPEEDSCALE_MPH) ? "&wind_speed_unit=mph" : "")
	    				+ "&current_weather=true"
	    				+ "&timezone=GMT&forecast_days=1&forecast_hours=24&temporal_resolution=hourly_3"
	    				//+ "error"
	    				;

	    		try {

	    			String response = fetchJSonFromURL(urlStr);
	    			JSONObject json = new JSONObject(response.toString());

	    			return OpenMeteoApiParser.parseFromOpenMeteo(json);

	    		} catch (Exception e) {
	    			WeatherDataForeCast tmp=new WeatherDataForeCast();
	    			tmp.setStatus(false);
	    			tmp.setStatusMessage(International.getString("Fehler beim Abruf der Wetterdaten.")+"\n\n"+ e.getMessage());
	    			Logger.logdebug(e);
	    			return tmp;
	    		}
	    	}

	    	private String fetchJSonFromURL(String urlStr) throws Exception {
	    		HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
	    		conn.setRequestMethod("GET");
	    		conn.setConnectTimeout(5000);//max 5 seconds for connect
	    		conn.setReadTimeout(10000); // max 10 seconds for reading data

	    		try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
	    			StringBuilder sb = new StringBuilder();
	    			String line;
	    			while ((line = reader.readLine()) != null)
	    				sb.append(line);
	    			
	    			int status = conn.getResponseCode();
	    			if (status != HttpURLConnection.HTTP_OK) {
	    				throw new RuntimeException("WebServer Reply Status " + status + sb.toString());
	    			}

	    			return sb.toString();
	    		}
	    	}	 
	    	
	    	private void addError(WeatherDataForeCast wdf) {
	    		JTextArea errorLabel1= new JTextArea();
	    		errorLabel1.setBackground(Daten.efaConfig.getErrorBackgroundColor());
	    		errorLabel1.setForeground(Daten.efaConfig.getErrorForegroundColor());
	    		errorLabel1.setFont(
	    				mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize())));
	    		errorLabel1.setFont(errorLabel1.getFont().deriveFont(Font.BOLD));
	    		errorLabel1.setText(International.getString("Fehler beim Abruf der Wetterdaten."));
	    		errorLabel1.setToolTipText(wdf.getStatusMessage());
	    		errorLabel1.setLineWrap(true);
	    		errorLabel1.setOpaque(false);
	    		errorLabel1.setEditable(false);
	    		
	    		innerPanel.setBackground(Daten.efaConfig.getErrorBackgroundColor());
	    		innerPanel.setForeground(Daten.efaConfig.getErrorForegroundColor());
	    		innerPanel.setBorder(new RoundedBorder(Daten.efaConfig.getErrorForegroundColor()));
	    		
	    		JPanel titlePanel = WeatherRenderer.getLocationHeader(ww);
	    		titlePanel.setBackground(Daten.efaConfig.getErrorHeaderBackgroundColor());
	    		titlePanel.setForeground(Daten.efaConfig.getErrorHeaderForegroundColor());
	    		
	    		// Build the main panel view

	    		innerPanel.add(titlePanel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
	    			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));	
	    		
	    		innerPanel.add(errorLabel1, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
	    				GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
	    	}	   
	        
	    }

	    private class UpdateWeatherRunner implements Runnable {
	        
	    	private JPanel uwrPanel=null;
	    	private JPanel uwrInnerPanel=null;
	    	
	    	public UpdateWeatherRunner(JPanel targetPanel, JPanel innerPanel) {
	    		this.uwrPanel = targetPanel;
	    		this.uwrInnerPanel = innerPanel;
    			Logger.log(Logger.DEBUG, "UpdateRunner Constructor");
	    	}
	    	
	    	public void run() {
	    		try {
	    			uwrPanel.removeAll();
	    			uwrPanel.add(uwrInnerPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
	    					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	    			uwrPanel.revalidate();
	    			Logger.log(Logger.DEBUG, "UpdateRunner updater");

        			
	    		} catch (Exception e){
	    			Logger.log(e);
	    		}
	    	}
	    }
	
	
	
}
