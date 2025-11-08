package de.nmichael.efa.gui.widgets;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.International;

public class WeatherRendererCurrentClassic extends WeatherRenderer {

	public static void renderWeather(WeatherDataForeCast wdf, JPanel roundPanel, WeatherWidget ww) {

		JLabel curWeather_temp = new JLabel();
		JLabel curWeather_icon = new JLabel();
		JLabel curWeather_minTemp = new JLabel();
		JLabel curWeather_maxTemp = new JLabel();
		JLabel curWeather_wind = new JLabel();
		
		String tempLabel = ww.getTempLabel(false);
		
		curWeather_temp = new JLabel();
		curWeather_temp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_temp.setFont(
				roundPanel.getFont().deriveFont((float) (Daten.efaConfig.getValueEfaDirekt_BthsFontSize() + 10)));
		curWeather_temp.setFont(curWeather_temp.getFont().deriveFont(Font.BOLD));
		curWeather_temp.setText(wdf.getCurrentWeather().getTemperature() + ww.getTempLabel(true));
		curWeather_temp.setHorizontalTextPosition(SwingConstants.LEFT);

		curWeather_icon.setIcon(WeatherIcons.getWeatherIconForCode(wdf.getCurrentWeather().getIconCode(), 64,
				wdf.getCurrentWeather().getIsDay() == 1, false));
		curWeather_icon.setToolTipText(wdf.getCurrentWeather().getDescription());
		double minTemp = wdf.getDaily().getTemperature_2m_min();
		double maxTemp = wdf.getDaily().getTemperature_2m_max();

		curWeather_minTemp.setText(International.getString("Min: ")+ minTemp + tempLabel);
		curWeather_minTemp.setIconTextGap(4);
		curWeather_minTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_minTemp.setHorizontalTextPosition(SwingConstants.RIGHT);

		curWeather_maxTemp.setText(International.getString("Max: ")+ maxTemp + tempLabel);
		curWeather_maxTemp.setIconTextGap(4);
		curWeather_maxTemp.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_maxTemp.setHorizontalTextPosition(SwingConstants.RIGHT);

		curWeather_wind.setText(International.getString("Wind") + ": "
				+ International.getString(wdf.getCurrentWeather().getWindDirectionText()) + " "
				+ International.getString("mit") + " " + wdf.getCurrentWeather().getWindSpeed()
				+ ww.getWeatherSpeedScale());
		curWeather_wind.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		curWeather_wind.setHorizontalTextPosition(SwingConstants.CENTER);
		
		// Build the main panel view

		roundPanel.add(getLocationHeader(ww), new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));		
		
		roundPanel.add(curWeather_temp, new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.VERTICAL, new Insets(2, 6, 2, 4), 0, 0));

		roundPanel.add(curWeather_icon, new GridBagConstraints(1, 1, 1, 2, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.VERTICAL, new Insets(0, 4, 0, 4), 0, 0));

		roundPanel.add(curWeather_maxTemp, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0));
		roundPanel.add(curWeather_minTemp, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0));
		
		roundPanel.add(curWeather_wind, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.VERTICAL, new Insets(0, 2, 0, 2), 0, 0));
	}	
	
}
