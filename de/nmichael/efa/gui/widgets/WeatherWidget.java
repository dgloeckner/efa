/**
 * 
 */
package de.nmichael.efa.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.json.JSONObject;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeLongLat;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.core.items.ItemTypeStringList;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.gui.ImagesAndIcons;
import de.nmichael.efa.gui.util.RoundedBorder;
import de.nmichael.efa.gui.util.RoundedPanel;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

/**
 * 
 */
public class WeatherWidget extends Widget {

	private static final String PARAM_SHOWWEATHER         = "ShowWeather";
	private static final String PARAM_LATITUDE            = "WeatherLatitude";
	private static final String PARAM_LONGITUDE           = "WeatherLongitude";
	private static final String PARAM_CAPTION			  = "WeatherCaption";
	private static final String PARAM_TEMPERATURESCALE    = "TemperatureScale";
	private static final String TEMP_CELSIUS              = "CELSIUS";
	private static final String TEMP_FAHRENHEIT           = "FAHRENHEIT";
	private static final String PARAM_SPEEDSCALE	      = "WindSpeedScale";
	private static final String SPEEDSCALE_MPH	      	  = "mph";
	private static final String SPEEDSCALE_KMH		      = "kmh";
	
    
    private static final String PARAM_WEATHER_SOURCE	  = "WeatherSource";
    private static final String WEATHER_SOURCE_OPENMETEO  = "WeatherSourceOpenMeteoFree";
    private static final String WEATHER_SOURCE_WEATHERAPI = "WeatherSourceWeatherApi";
    
    private static final String PARAM_WEATHER_LAYOUT	  = "WeatherLayout";
    private static final String WEATHER_LAYOUT_CURRENT	  = "WeatherLayoutLayoutCurrent"; 
    private static final String WEATHER_LAYOUT_FORECAST   = "WeatherLayoutForecast";
    
    
    private JPanel mainPanel = new JPanel();
    private RoundedPanel roundPanel = new RoundedPanel();
    private RoundedPanel titlePanel = new RoundedPanel();
    private JLabel titleLabel = new JLabel();
    private JLabel curWeather_temp = new JLabel();
    private JLabel curWeather_icon = new JLabel();
    private JLabel curWeather_minTemp = new JLabel();
    private JLabel curWeather_maxTemp = new JLabel();
    private JLabel curWeather_wind = new JLabel();
    		
    
	/**
	 * @param name
	 * @param description
	 * @param ongui
	 * @param showRefreshInterval
	 */
	public WeatherWidget() {
		
    	super(International.getString("Wetter"), "Wetter", International.getString("Wetter"), true, true);

    	IItemType item = null;
    	
        /*addParameterInternal(new ItemTypeBoolean(PARAM_SHOWWEATHER, false,
                IItemType.TYPE_PUBLIC, "",
                International.getString("Wetterdaten anzeigen") +
                " (" + International.getString("Internetverbindung erforderlich") + ")"));*/
       
        addHeader("WeatherWidgetLocationHeader",IItemType.TYPE_PUBLIC, "", International.getString("Wetter Daten"), 3);  
        
        addParameterInternal(new ItemTypeStringList(PARAM_WEATHER_SOURCE, WEATHER_SOURCE_OPENMETEO, 
        		new String[] {WEATHER_SOURCE_OPENMETEO, WEATHER_SOURCE_WEATHERAPI},
        		new String[] {International.getString("OpenMeteo free API (Europe/North America)"),
        					  International.getString("WeatherAPI")},
        		IItemType.TYPE_PUBLIC,"",
        		International.getString("Quelle für Wetterdaten")));        
        
        
        addParameterInternal(item=new ItemTypeString(PARAM_CAPTION,
                "Dummy",
                IItemType.TYPE_PUBLIC, "",
                International.getString("Beschriftung")), 20, 0);


        addParameterInternal(item=new ItemTypeLongLat(PARAM_LATITUDE,
                ItemTypeLongLat.ORIENTATION_NORTH,52,25,9,
                IItemType.TYPE_PUBLIC, "",
                International.getString("geographische Breite")));
        
        addParameterInternal(new ItemTypeLongLat(PARAM_LONGITUDE,
                ItemTypeLongLat.ORIENTATION_EAST,13,10,15,
                IItemType.TYPE_PUBLIC, "",
                International.getString("geographische Länge")));
        
        
        addParameterInternal(item=new ItemTypeStringList(PARAM_WEATHER_LAYOUT, WEATHER_LAYOUT_CURRENT, 
        		new String[] {WEATHER_LAYOUT_CURRENT, WEATHER_LAYOUT_FORECAST},
        		new String[] {International.getString("Aktuelles Wetter"),
        					  International.getString("Vorhersage")},
        		IItemType.TYPE_PUBLIC,"",
        		International.getString("Layout")), 20, 0);  

        addParameterInternal(new ItemTypeStringList(PARAM_TEMPERATURESCALE, TEMP_CELSIUS,
                new String[] { TEMP_CELSIUS, TEMP_FAHRENHEIT },
                new String[] { International.getString("Celsius"),
                               International.getString("Fahrenheit")
                },
                IItemType.TYPE_PUBLIC, "",
                International.getString("Temperaturskala")));
   
        addParameterInternal(new ItemTypeStringList(PARAM_SPEEDSCALE, SPEEDSCALE_KMH,
                new String[] { SPEEDSCALE_KMH, SPEEDSCALE_MPH },
                new String[] { International.getString("km/h"),
                               International.getString("mph")
                },
                IItemType.TYPE_PUBLIC, "",
                International.getString("Windgeschwindigkeit-Skala")));        
        
        
        super.setEnabled(true);
        super.setPosition(IWidget.POSITION_CENTER);
        
	}


	@Override
	public void runWidgetWarnings(int mode, boolean actionBegin, LogbookRecord r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void construct() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());		
		roundPanel = new RoundedPanel();
		roundPanel.setLayout(new GridBagLayout());
		roundPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());
		roundPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		roundPanel.setBorder(new RoundedBorder(Daten.efaConfig.getToolTipForegroundColor()));

		mainPanel.add(roundPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));

		titlePanel = new RoundedPanel();
		titlePanel.setLayout(new GridBagLayout());
		titlePanel.setBackground(Daten.efaConfig.getToolTipHeaderBackgroundColor());
		titlePanel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		
		titleLabel.setText(getWeatherCaption());
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		titleLabel.setForeground(titlePanel.getForeground());
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		
		titlePanel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));

		roundPanel.add(titlePanel, new GridBagConstraints(0, 0, /*3*/4, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		roundPanel.setMinimumSize(new Dimension(240,120));;
		//mainPanel.setPreferredSize(new Dimension(240,140));

		WeatherDataForeCast wdf = getWeather(this.getWeatherSource(), this.getWeatherLongitude(), this.getWeatherLatitude());
		
		if (getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT)) {
			addCurrentWeather(wdf);
		} else {
			addForeCast(wdf);
		}
		
		
	}


	private void addCurrentWeather(WeatherDataForeCast wdf) {
		String tempLabel=getTempLabel();
				
		curWeather_temp = new JLabel();
		curWeather_temp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_temp.setFont(mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize()+10)));
		curWeather_temp.setFont(curWeather_temp.getFont().deriveFont(Font.BOLD));
		curWeather_temp.setText(wdf.getCurrentWeather().getTemperature()+tempLabel);
		curWeather_temp.setHorizontalTextPosition(SwingConstants.LEFT);
		
		curWeather_icon.setIcon(WeatherIcons.getWeatherIconForCode(wdf.getCurrentWeather().getIconCode(), 64, wdf.getCurrentWeather().getIsDay()==1, true));
		
		double minTemp = getMinTempToday(wdf, DataTypeDate.today());
		double maxTemp = getMaxTempToday(wdf, DataTypeDate.today());
		
		curWeather_minTemp.setText("Min: "+minTemp+tempLabel);
		curWeather_minTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_minTemp.setHorizontalTextPosition(SwingConstants.RIGHT);
		
		curWeather_maxTemp.setText("Max: "+maxTemp+tempLabel);
		curWeather_maxTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_maxTemp.setHorizontalTextPosition(SwingConstants.RIGHT);
		
		curWeather_wind.setText(International.getString("Wind")+": "
				+International.getString(wdf.getCurrentWeather().getWindDirectionText())+" "
				+International.getString("mit")+" "
				+wdf.getCurrentWeather().getWindSpeed() + getWeatherSpeedScale());
		curWeather_wind.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_wind.setHorizontalTextPosition(SwingConstants.CENTER);
		
		roundPanel.add(curWeather_temp, new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,4,2,4), 0, 0));

		roundPanel.add(curWeather_icon, new GridBagConstraints(1, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,4,0,4), 0, 0));

		roundPanel.add(curWeather_minTemp, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(4,4,0,4), 0, 0));
		roundPanel.add(curWeather_maxTemp, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,4,0,4), 0, 0));
		roundPanel.add(curWeather_wind,    new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(0,2,0,2), 0, 0));
	}

	private double getMinTempToday(WeatherDataForeCast wdf, DataTypeDate date) {
		return -5.0;
	}

	private double getMaxTempToday(WeatherDataForeCast wdf, DataTypeDate date) {
		return 20.0;
	}
	
	
	private void addForeCast(WeatherDataForeCast wd) {
		String tempLabel=getTempLabel();
		roundPanel.add(addForeCastPanel("14:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_116_48), "28"+tempLabel),
				 new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4,2,2,18), 0, 0));
		roundPanel.add(addForeCastPanel("16:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_113_48), "24"+tempLabel),
				 new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,0,2,18), 0, 0));
		roundPanel.add(addForeCastPanel("18:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_320_48), "20"+tempLabel),
				 new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,0,2,18), 0, 0));
		roundPanel.add(addForeCastPanel("20:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_386_48), "18"+tempLabel),
				 new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,0,2,4), 0, 0));
	}
	
	private JPanel addForeCastPanel(String time, ImageIcon image, String temp) {
		JPanel myPanel= new JPanel();
		JLabel topLabel = new JLabel();
		JLabel centerLabel= new JLabel(image);
		JLabel bottomLabel = new JLabel();
		
		myPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		myPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());
		
		topLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		bottomLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		
		topLabel.setText(time);
		topLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		topLabel.setFont(topLabel.getFont().deriveFont(Font.BOLD));
		/* centerLabel.setIcon(image);
		   centerLabel.setText("");
		   centerLabel.setIconTextGap(0);
		*/
		bottomLabel.setText(temp);
		bottomLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		
/*		
		myPanel.setLayout(new GridBagLayout());
		myPanel.add(topLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,3,2,3), 0, 0));
		myPanel.add(centerLabel,      new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2,3,2,3), 0, 0));
		myPanel.add(bottomLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,3,2,), 0, 0));
*/
		myPanel.setLayout(new BorderLayout(0,0));
		myPanel.add(topLabel,  BorderLayout.NORTH );
		myPanel.add(centerLabel, BorderLayout.CENTER);
		myPanel.add(bottomLabel, BorderLayout.SOUTH);

		return myPanel;
		
	}
	
	@Override
	public JComponent getComponent() {
		// TODO Auto-generated method stub
		return mainPanel;
	}
	
	private String getWeatherCaption() {
		return ((ItemTypeString)getParameterInternal(PARAM_CAPTION)).getValue();
	}
	
	private String getWeatherLongitude() {
		return getLongLat(PARAM_LONGITUDE);
	}

	private String getLongLat(String Name) {
		ItemTypeLongLat val = (ItemTypeLongLat)getParameterInternal(Name);
		if (val != null) {
			int[] coords = val.getValueCoordinates();
			return coords[0]+"."+coords[1];
		}
		return null;		
	}
	
	private String getWeatherLatitude() {
		return getLongLat(PARAM_LATITUDE);
	}
	
	private String getWeatherLayout() {
		return ((ItemTypeStringList)getParameterInternal(PARAM_WEATHER_LAYOUT)).toString();
	}
	
	private String getWeatherSource() {
		return ((ItemTypeStringList)getParameterInternal(PARAM_WEATHER_SOURCE)).toString();
	}
	
	private String getWeatherTempScale() {
		return ((ItemTypeStringList)getParameterInternal(PARAM_TEMPERATURESCALE)).toString();
	}
	
	private String getWeatherSpeedScale() {
		return ((ItemTypeStringList)getParameterInternal(PARAM_SPEEDSCALE)).toString();
	}
	
	
	
	private String getTempLabel() {
		return (getWeatherTempScale().equals(TEMP_CELSIUS) ? " °C" : " °F");
	}
	
	private WeatherDataForeCast getWeather(String source, String longitude, String latitude) {
		
		if (source.equals(WEATHER_SOURCE_OPENMETEO)) {
			 //String urlStr = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current_weather=true";
			 String urlStr = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude
					 + "&hourly=temperature_2m,weather_code,wind_speed_10m,wind_direction_10m,uv_index,is_day"
					 + "&t=temperature_2m,is_day,weather_code,wind_speed_10m,wind_direction_10m"
					 + (this.getWeatherTempScale().equals(TEMP_FAHRENHEIT) ? "&temperature_unit=fahrenheit" : "")
					 + (this.getWeatherSpeedScale().equals(SPEEDSCALE_MPH) ? "&wind_speed_unit=mph" : "")
					 + (this.getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT) ? "&current_weather=true" : "");
			 try {
	            URL url = new URL(urlStr);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");

	            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String inputLine;
	            StringBuilder response = new StringBuilder();

	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            // JSON parsen
	            JSONObject json = new JSONObject(response.toString());
	            
	            return OpenMeteoApiParser.parseFromOpenMeteo(json);
	            
			 } catch (Exception e) {
				 Logger.logdebug(e);
			 }
		}
		return null;
	}
	

}
