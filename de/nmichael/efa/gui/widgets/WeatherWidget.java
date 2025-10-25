/**
 * 
 */
package de.nmichael.efa.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.json.JSONObject;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeLongLat;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.core.items.ItemTypeStringList;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.gui.util.RoundedBorder;
import de.nmichael.efa.gui.util.RoundedPanel;
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
	
	private static final String WEATHER_LAYOUT_FORECAST = "WeatherLayoutForecast";

	private JPanel mainPanel = new JPanel();
	private RoundedPanel roundPanel = new RoundedPanel();
	private RoundedPanel titlePanel = new RoundedPanel();
	private JLabel titleLabel = new JLabel();


	/**
	 * @param name
	 * @param description
	 * @param ongui
	 * @param showRefreshInterval
	 */
	public WeatherWidget() {

		super(International.getString("Wetter"), "Wetter", International.getString("Wetter"), true, true);

		IItemType item = null;

		/*
		 * addParameterInternal(new ItemTypeBoolean(PARAM_SHOWWEATHER, false,
		 * IItemType.TYPE_PUBLIC, "", International.getString("Wetterdaten anzeigen") +
		 * " (" + International.getString("Internetverbindung erforderlich") + ")"));
		 */

		addHeader("WeatherWidgetLocationHeader", IItemType.TYPE_PUBLIC, "", International.getString("Wetter Daten"), 3);

		addParameterInternal(new ItemTypeStringList(PARAM_WEATHER_SOURCE, WEATHER_SOURCE_OPENMETEO,
				new String[] { WEATHER_SOURCE_OPENMETEO, WEATHER_SOURCE_WEATHERAPI },
				new String[] { International.getString("OpenMeteo free API (Europe/North America)"),
						International.getString("WeatherAPI") },
				IItemType.TYPE_PUBLIC, "", International.getString("Quelle für Wetterdaten")));

		addParameterInternal(item = new ItemTypeString(PARAM_CAPTION, "Dummy", IItemType.TYPE_PUBLIC, "",
				International.getString("Beschriftung")), 20, 0);

		addParameterInternal(item = new ItemTypeLongLat(PARAM_LATITUDE, ItemTypeLongLat.ORIENTATION_NORTH, 52, 25, 9,
				IItemType.TYPE_PUBLIC, "", International.getString("geographische Breite")));

		addParameterInternal(new ItemTypeLongLat(PARAM_LONGITUDE, ItemTypeLongLat.ORIENTATION_EAST, 13, 10, 15,
				IItemType.TYPE_PUBLIC, "", International.getString("geographische Länge")));

		addParameterInternal(item = new ItemTypeStringList(PARAM_WEATHER_LAYOUT, WEATHER_LAYOUT_CURRENT_UVINDEX,
				new String[] { WEATHER_LAYOUT_CURRENT_CLASSIC, WEATHER_LAYOUT_CURRENT_WIND, WEATHER_LAYOUT_CURRENT_UVINDEX, WEATHER_LAYOUT_FORECAST },
				new String[] { International.getString("Aktuelles Wetter (Klassisch)"), International.getString("Aktuelles Wetter (Wind)"), International.getString("Aktuelles Wetter (UV-Index)"), International.getString("Vorhersage") },
				IItemType.TYPE_PUBLIC, "", International.getString("Layout")), 20, 0);

		addParameterInternal(new ItemTypeStringList(PARAM_TEMPERATURESCALE, TEMP_CELSIUS,
				new String[] { TEMP_CELSIUS, TEMP_FAHRENHEIT },
				new String[] { International.getString("Celsius"), International.getString("Fahrenheit") },
				IItemType.TYPE_PUBLIC, "", International.getString("Temperaturskala")));

		addParameterInternal(new ItemTypeStringList(PARAM_SPEEDSCALE, SPEEDSCALE_KMH,
				new String[] { SPEEDSCALE_KMH, SPEEDSCALE_MPH },
				new String[] { International.getString("km/h"), International.getString("mph") }, IItemType.TYPE_PUBLIC,
				"", International.getString("Windgeschwindigkeit-Skala")));

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

		mainPanel.add(roundPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		titlePanel = new RoundedPanel();
		titlePanel.setLayout(new GridBagLayout());
		titlePanel.setBackground(Daten.efaConfig.getToolTipHeaderBackgroundColor());
		titlePanel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		titleLabel.setText(getWeatherCaption());
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		titleLabel.setForeground(titlePanel.getForeground());
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));

		titlePanel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));


		roundPanel.add(titlePanel, new GridBagConstraints(0, 0,  (getWeatherLayout().equals(WEATHER_LAYOUT_FORECAST) ? 4 : 3), 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		roundPanel.setMinimumSize(new Dimension(240, 120));
		// mainPanel.setPreferredSize(new Dimension(240,140));

		WeatherDataForeCast wdf = getWeather(this.getWeatherSource(), this.getWeatherLongitude(),
				this.getWeatherLatitude());

		if (wdf.getStatus() == true) {
			if (getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT_CLASSIC)) {
				addCurrentWeather(wdf);
				roundPanel.add(titlePanel, new GridBagConstraints(0, 0,  3 /*4*/, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
			} else if (getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT_WIND)) {
				addCurrentWeather2(wdf);
				roundPanel.add(titlePanel, new GridBagConstraints(0, 0,  3 /*4*/, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
			} else if (getWeatherLayout().equals(WEATHER_LAYOUT_CURRENT_UVINDEX)) {
				roundPanel.add(titlePanel, new GridBagConstraints(0, 0,  3 /*4*/, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
				addCurrentWeather3(wdf);
			} else {
				roundPanel.add(titlePanel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
				addForeCast(wdf);
			}
		} else {
			addError(wdf);
		}

	}

	private void addCurrentWeather(WeatherDataForeCast wdf) {

		JLabel curWeather_temp = new JLabel();
		JLabel curWeather_icon = new JLabel();
		JLabel curWeather_minTemp = new JLabel();
		JLabel curWeather_maxTemp = new JLabel();
		JLabel curWeather_wind = new JLabel();
		
		String tempLabel = getTempLabel(false);
		
		curWeather_temp = new JLabel();
		curWeather_temp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_temp.setFont(
				mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize() + 10)));
		curWeather_temp.setFont(curWeather_temp.getFont().deriveFont(Font.BOLD));
		curWeather_temp.setText(wdf.getCurrentWeather().getTemperature() + getTempLabel(true));
		curWeather_temp.setHorizontalTextPosition(SwingConstants.LEFT);

		curWeather_icon.setIcon(WeatherIcons.getWeatherIconForCode(wdf.getCurrentWeather().getIconCode(), 64,
				wdf.getCurrentWeather().getIsDay() == 1, false));
		curWeather_icon.setToolTipText(wdf.getCurrentWeather().getDescription());
		double minTemp = wdf.getDaily().getTemperature_2m_min();
		double maxTemp = wdf.getDaily().getTemperature_2m_max();

		curWeather_minTemp.setText("Min: "+ minTemp + tempLabel);
		curWeather_minTemp.setIconTextGap(4);
		curWeather_minTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_minTemp.setHorizontalTextPosition(SwingConstants.RIGHT);

		curWeather_maxTemp.setText("Max: "+ maxTemp + tempLabel);
		curWeather_maxTemp.setIconTextGap(4);
		curWeather_maxTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_maxTemp.setHorizontalTextPosition(SwingConstants.RIGHT);

		curWeather_wind.setText(International.getString("Wind") + ": "
				+ International.getString(wdf.getCurrentWeather().getWindDirectionText()) + " "
				+ International.getString("mit") + " " + wdf.getCurrentWeather().getWindSpeed()
				+ getWeatherSpeedScale());
		curWeather_wind.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_wind.setHorizontalTextPosition(SwingConstants.CENTER);

		roundPanel.add(curWeather_temp, new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));

		roundPanel.add(curWeather_icon, new GridBagConstraints(1, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 4, 0, 4), 0, 0));

		roundPanel.add(curWeather_maxTemp, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0));
		roundPanel.add(curWeather_minTemp, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0));
		roundPanel.add(curWeather_wind, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.VERTICAL, new Insets(0, 2, 0, 2), 0, 0));
	}
	
	private void addCurrentWeather2(WeatherDataForeCast wdf) {

		JLabel curWeather_temp = new JLabel();
		JLabel curWeather_icon = new JLabel();
		JLabel curWeather_minTemp = new JLabel();
		JLabel curWeather_maxTemp = new JLabel();
		JLabel curWeather_sunshine = new JLabel();
		JLabel curWeather_rain = new JLabel();
		JLabel curWeather_sunshineUnit = new JLabel();
		JLabel curWeather_rainUnit = new JLabel();
		
		JLabel curWeather_wind = new JLabel();
		JPanel pnlMinMaxSunRain=new JPanel();
		pnlMinMaxSunRain.setOpaque(false);
		pnlMinMaxSunRain.setForeground(mainPanel.getForeground());
		pnlMinMaxSunRain.setLayout(new GridBagLayout());

		double minTemp = wdf.getDaily().getTemperature_2m_min();
		double maxTemp = wdf.getDaily().getTemperature_2m_max();
		double sunshine = wdf.getDaily().getSunshine_duration()/60/60;
		double rain = wdf.getDaily().getPrecipitation_sum();
		
		String tempLabel = getTempLabel(false);
		
		curWeather_temp = new JLabel();
		curWeather_temp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_temp.setFont(
				mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize() + 10)));
		curWeather_temp.setFont(curWeather_temp.getFont().deriveFont(Font.BOLD));
		curWeather_temp.setText(wdf.getCurrentWeather().getTemperature() + getTempLabel(false));

		curWeather_icon.setIcon(WeatherIcons.getWeatherIconForCode(wdf.getCurrentWeather().getIconCode(), 64,
				wdf.getCurrentWeather().getIsDay() == 1, false));
		curWeather_icon.setToolTipText(wdf.getCurrentWeather().getDescription());

		curWeather_minTemp.setText(minTemp + tempLabel);
		curWeather_minTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		curWeather_maxTemp.setText(maxTemp + tempLabel);
		curWeather_maxTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		DecimalFormat df = new DecimalFormat("#.#");
		curWeather_sunshine.setText(df.format(sunshine));
		curWeather_sunshine.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		curWeather_sunshineUnit.setText("h");
		curWeather_sunshineUnit.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		df = new DecimalFormat("#");
		curWeather_rain.setText(df.format(rain)); 
		curWeather_rain.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		curWeather_rainUnit.setText("mm");
		curWeather_rainUnit.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_rainUnit.setFont(
				mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize() -4)));

		/*	roundpanel
		 *  | HEADER                                             |	
		 * 	|WeatherIcon|CurrentTemp|pnlMinMaxSunRain            |
		 *  | WIND                                               |
		 * 
		 * pnlMinMaxSunRain
		 *  |IMG_Max |lblMax|IMG_Sunshine|lblSunshine|lblSunUnit | 
		 *  |IMG_Min |lblMin|IMG_Rain    |lblRain    |lblRainUnit|
		 * 
		 */
		pnlMinMaxSunRain.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_MAX)), 
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 4, 2, 2), 0, 0));
		pnlMinMaxSunRain.add(curWeather_maxTemp, 
				new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST,GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));
		
		pnlMinMaxSunRain.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_SUN)), 
				new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 12, 2, 2), 0, 0));
		pnlMinMaxSunRain.add(curWeather_sunshine, 
				new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));		
		pnlMinMaxSunRain.add(curWeather_sunshineUnit, 
				new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));		
		
		
		pnlMinMaxSunRain.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_MIN)), 
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 4, 2, 2), 0, 0));
		pnlMinMaxSunRain.add(curWeather_minTemp, 
				new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));		
		
		pnlMinMaxSunRain.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_RAIN)), 
				new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 12, 2, 2), 0, 0));
		pnlMinMaxSunRain.add(curWeather_rain, 
				new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));		
		pnlMinMaxSunRain.add(curWeather_rainUnit, 
				new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(2, 2, 2, 2), 0, 0));		
		
		
		curWeather_wind.setText(International.getString("Wind") + ": "
				+ International.getString(wdf.getCurrentWeather().getWindDirectionText()) + " "
				+ International.getString("mit") + " " + wdf.getCurrentWeather().getWindSpeed()
				+ getWeatherSpeedScale());
		curWeather_wind.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_wind.setHorizontalTextPosition(SwingConstants.CENTER);

		roundPanel.add(curWeather_icon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));

		roundPanel.add(curWeather_temp, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 4), 0, 0));

		roundPanel.add(pnlMinMaxSunRain, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(0, 4, 0, 4), 0, 0));

		/*roundPanel.add(curWeather_maxTemp, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0));
		roundPanel.add(curWeather_minTemp, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0));*/
		roundPanel.add(curWeather_wind, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.VERTICAL, new Insets(0, 2, 0, 2), 0, 0));
	}

	private JPanel initializePanel() {
		JPanel ret = new JPanel();
		ret.setOpaque(false);
		ret.setForeground(mainPanel.getForeground());
		ret.setLayout(new GridBagLayout());		
		return ret;
	}
	
	private void addCurrentWeather3(WeatherDataForeCast wdf) {

		JLabel curWeather_temp = new JLabel();
		JLabel curWeather_icon = new JLabel();
		JLabel curWeather_minTemp = new JLabel();
		JLabel curWeather_maxTemp = new JLabel();
		JLabel curWeather_sunshine = new JLabel();
		JLabel curWeather_rain = new JLabel();
		JLabel curWeather_sunshineUnit = new JLabel();
		JLabel curWeather_rainUnit = new JLabel();
		JLabel curWeather_uvindex = new JLabel();
		JLabel curWeather_wind = new JLabel();

		JPanel pnlSunshine = initializePanel();
		JPanel pnlUV = initializePanel();
		JPanel pnlRain = initializePanel();
		JPanel pnlMinMax=initializePanel();


		double minTemp = wdf.getDaily().getTemperature_2m_min();
		double maxTemp = wdf.getDaily().getTemperature_2m_max();
		double sunshine = wdf.getDaily().getSunshine_duration()/60/60;
		double rain = wdf.getDaily().getPrecipitation_sum();
		double uvindex = wdf.getDaily().getUv_index_max();
		
		String tempLabel = " "+getTempLabel(true);
		
		curWeather_temp = new JLabel();
		curWeather_temp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_temp.setFont(
				mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize() + 6)));
		curWeather_temp.setFont(curWeather_temp.getFont().deriveFont(Font.BOLD));
		curWeather_temp.setText(wdf.getCurrentWeather().getTemperature() +" "+ getTempLabel(true));

		curWeather_icon.setIcon(WeatherIcons.getWeatherIconForCode(wdf.getCurrentWeather().getIconCode(), 64,
				wdf.getCurrentWeather().getIsDay() == 1, false));
		curWeather_icon.setToolTipText(wdf.getCurrentWeather().getDescription());

		curWeather_minTemp.setText(minTemp + tempLabel);
		curWeather_minTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		curWeather_maxTemp.setText(maxTemp + tempLabel);
		curWeather_maxTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		DecimalFormat df = new DecimalFormat("#.#");
		curWeather_sunshine.setText(df.format(sunshine));
		curWeather_sunshine.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		curWeather_sunshineUnit.setText("h");
		curWeather_sunshineUnit.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		df = new DecimalFormat("#.#");
		curWeather_uvindex.setText(df.format(uvindex));
		curWeather_uvindex.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_uvindex.setHorizontalAlignment(SwingConstants.RIGHT);
		
		df = new DecimalFormat("#");
		curWeather_rain.setText(df.format(rain)); 
		curWeather_rain.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		
		curWeather_rainUnit.setText("mm");
		curWeather_rainUnit.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_rainUnit.setFont(
				mainPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize() -4)));

		/*	roundpanel
		 *  | HEADER                                   |	
		 * 	|curtemp |  weatherIcon | pnlMinMax  
		 *  |sunshine|   uvindex    | rain             |
		 * 
		 * 
		 */
		//Min max Temp
		pnlMinMax.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_MAX)), 
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(2, 0, 2, 4), 0, 0));
		pnlMinMax.add(curWeather_maxTemp, 
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHEAST,GridBagConstraints.VERTICAL, new Insets(2, 0, 2, 0), 0, 0));
		pnlMinMax.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_MIN)), 
				new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(2, 0, 2, 4), 0, 0));
		pnlMinMax.add(curWeather_minTemp, 
				new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(2, 0, 2, 0), 0, 0));		
		
		//Sunshine hours
		JLabel lblSunIcon=new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_SUN));
		lblSunIcon.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblSunIcon.setIconTextGap(0);
		pnlSunshine.add(lblSunIcon, 
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 2, 2), 0, 0));
		pnlSunshine.add(curWeather_sunshine, 
				new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(0, 2, 2, 2), 0, 0));		
		pnlSunshine.add(curWeather_sunshineUnit, 
				new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,GridBagConstraints.VERTICAL, new Insets(0, 2, 2, 2), 0, 0));		
		
		// uv index
		pnlUV.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_UV_INDEX)), 
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 2, 2), 0, 0));
		pnlUV.add(curWeather_uvindex, 
				new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(0, 2, 2, 2), 0, 0));	
		JLabel uvi = new JLabel(wdf.getDaily().getUv_index_icon());
		uvi.setIconTextGap(0);
		uvi.setHorizontalTextPosition(SwingConstants.RIGHT);
		pnlUV.add(uvi, 
				new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(0, 0, 2, 2), 0, 0));		
		
		// rain
		pnlRain.add(new JLabel (WeatherIcons.getIcon(WeatherIcons.IMAGE_RAIN)), 
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 2, 2, 4), 0, 0));
		pnlRain.add(curWeather_rain, 
				new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(0, 2, 2, 2), 0, 0));		
		pnlRain.add(curWeather_rainUnit, 
				new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,GridBagConstraints.VERTICAL, new Insets(0, 2, 2, 2), 0, 0));		
				
		//wind just a label 
		curWeather_wind.setText(International.getString("Wind") + ": "
				+ International.getString(wdf.getCurrentWeather().getWindDirectionText()) + " "
				+ International.getString("mit") + " " + wdf.getCurrentWeather().getWindSpeed()
				+ getWeatherSpeedScale());
		curWeather_wind.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_wind.setHorizontalTextPosition(SwingConstants.CENTER);
		
		//first row
		roundPanel.add(curWeather_temp,  new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 2), 0, 0));

		roundPanel.add(curWeather_icon,  new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 5, 0, 5), 0, 0));
		
		roundPanel.add(pnlMinMax,        new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 4), 0, 0));
		
		// second row
		roundPanel.add(pnlSunshine,      new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL, new Insets(2, 2, 0, 0), 0, 0));

		roundPanel.add(pnlUV,            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 0, 0, 0), 0, 0));

		roundPanel.add(pnlRain,          new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.VERTICAL, new Insets(2, 0, 0, 2), 0, 0));

		/*roundPanel.add(curWeather_wind, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 12, 0, 6), 0, 0));
		*/
		

		
	}
	
	
	
	private void addForeCast(WeatherDataForeCast wdf) {
		String tempLabel = getTempLabel(true);
		roundPanel.add(
				addForeCastPanel("14:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_116_48), "28" + tempLabel),
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(4, 2, 2, 18), 0, 0));
		roundPanel.add(
				addForeCastPanel("16:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_113_48), "24" + tempLabel),
				new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 0, 2, 18), 0, 0));
		roundPanel.add(
				addForeCastPanel("18:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_320_48), "20" + tempLabel),
				new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 0, 2, 18), 0, 0));
		roundPanel.add(
				addForeCastPanel("20:00", WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_386_48), "18" + tempLabel),
				new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 0, 2, 4), 0, 0));
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
		
		roundPanel.setBackground(Daten.efaConfig.getErrorBackgroundColor());
		roundPanel.setForeground(Daten.efaConfig.getErrorForegroundColor());
		roundPanel.setBorder(new RoundedBorder(Daten.efaConfig.getErrorForegroundColor()));
		
		titlePanel.setBackground(Daten.efaConfig.getErrorHeaderBackgroundColor());
		titlePanel.setForeground(Daten.efaConfig.getErrorHeaderForegroundColor());
		titleLabel.setForeground(titlePanel.getForeground());
		
		roundPanel.add(errorLabel1, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
	}

	private JPanel addForeCastPanel(String time, ImageIcon image, String temp) {
		JPanel myPanel = new JPanel();
		JLabel topLabel = new JLabel();
		JLabel centerLabel = new JLabel(image);
		JLabel bottomLabel = new JLabel();

		myPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		myPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());

		topLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		bottomLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		topLabel.setText(time);
		topLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		topLabel.setFont(topLabel.getFont().deriveFont(Font.BOLD));
		/*
		 * centerLabel.setIcon(image); centerLabel.setText("");
		 * centerLabel.setIconTextGap(0);
		 */
		bottomLabel.setText(temp);
		bottomLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		/*
		 * myPanel.setLayout(new GridBagLayout()); myPanel.add(topLabel, new
		 * GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
		 * GridBagConstraints.BOTH, new Insets(2,3,2,3), 0, 0));
		 * myPanel.add(centerLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
		 * GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new
		 * Insets(2,3,2,3), 0, 0)); myPanel.add(bottomLabel, new GridBagConstraints(0,
		 * 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new
		 * Insets(2,3,2,), 0, 0));
		 */
		myPanel.setLayout(new BorderLayout(0, 0));
		myPanel.add(topLabel, BorderLayout.NORTH);
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

	private String getWeatherTempScale() {
		return ((ItemTypeStringList) getParameterInternal(PARAM_TEMPERATURESCALE)).toString();
	}

	private String getWeatherSpeedScale() {
		return ((ItemTypeStringList) getParameterInternal(PARAM_SPEEDSCALE)).toString();
	}

	private String getTempLabel(boolean withUnit) {
		if (!withUnit) {
			return "°";
		} else {
			return (getWeatherTempScale().equals(TEMP_CELSIUS) ? "°C" : "°F");
		}
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
				+ "&hourly=temperature_2m,weather_code,wind_speed_10m,wind_direction_10m,uv_index,is_day"
				+ "&t=temperature_2m,is_day,weather_code,wind_speed_10m,wind_direction_10m"
				+ (this.getWeatherTempScale().equals(TEMP_FAHRENHEIT) ? "&temperature_unit=fahrenheit" : "")
				+ (this.getWeatherSpeedScale().equals(SPEEDSCALE_MPH) ? "&wind_speed_unit=mph" : "")
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

}
