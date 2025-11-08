package de.nmichael.efa.gui.widgets;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.nmichael.efa.Daten;

public class WeatherRendererForeCastSimple extends WeatherRenderer {

	public static void renderWeather(WeatherDataForeCast wdf, JPanel roundPanel, WeatherWidget ww) {
		String tempLabel = ww.getTempLabel(true);
		
		// Build the main panel view

		roundPanel.add(getLocationHeader(ww), new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));		
		
		roundPanel.add(
				addForeCastPanel("14:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_116_48), 
						"28" + tempLabel, roundPanel),
						new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 4), 0, 0));
		roundPanel.add(
				addForeCastPanel("15:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_113_48), 
						"24" + tempLabel, roundPanel),
						new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 4), 0, 0));
		roundPanel.add(
				addForeCastPanel("16:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_320_48), 
						"20" + tempLabel, roundPanel),
						new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 4), 0, 0));
		roundPanel.add(
				addForeCastPanel("17:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_386_1_48), 
						"18" + tempLabel, roundPanel),
						new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 4), 0, 0));
	}

	

	private static JPanel addForeCastPanel(String time, ImageIcon weatherIcon, String temp, JPanel roundPanel) {
		JPanel myPanel = initializePanel(roundPanel);
		JLabel timeLabel = initializeLabel(roundPanel);
		JLabel weatherIconLabel = initializeLabel(roundPanel);
		JLabel tempLabel = initializeLabel(roundPanel);
		
		myPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		myPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());

		timeLabel.setText(time);
		timeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD));
		timeLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		weatherIconLabel.setIcon(weatherIcon);
		weatherIconLabel.setIconTextGap(0);
		weatherIconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		tempLabel.setText(temp);
		tempLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		tempLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		
   	    myPanel.add(timeLabel,   		new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 		new Insets(1,3,1,3), 0, 0));
   	    myPanel.add(weatherIconLabel, 	new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 	    new Insets(1,3,1,3), 0, 0)); 
   	    myPanel.add(tempLabel, 			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 		new Insets(1,3,1,3), 0, 0));
   	    
/*
		myPanel.setLayout(new BorderLayout(0, 0));
		myPanel.add(timeLabel, BorderLayout.NORTH);
		myPanel.add(weatherIconLabel, BorderLayout.CENTER);
		myPanel.add(tempLabel, BorderLayout.SOUTH);
*/
		return myPanel;

	}
	

	
	
	
}
