package de.nmichael.efa.gui.widgets;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.nmichael.efa.Daten;

public class WeatherRendererForeCastComplex extends WeatherRenderer {
	public static void renderWeather(WeatherDataForeCast wdf, JPanel roundPanel, WeatherWidgetInstance ww) {
		String tempLabel = ww.getTempLabel(false);
		
		int startY=1;
		// Build the main panel view
		int hourlyIndex = wdf.getHourly().getIndexForCurrentTime();

		roundPanel.add(getLocationHeader(ww.getCaption()), new GridBagConstraints(0, 0, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));		
		
				addForeCastPanel(WeatherRenderer.getHourlyHourRendering(wdf, hourlyIndex), 
						WeatherRenderer.getHourlyWeatherIcon(wdf,hourlyIndex),
						WeatherRenderer.getHourlyDescription(wdf,hourlyIndex),
						WeatherRenderer.getHourlyTemp(wdf, hourlyIndex, tempLabel), 
						WeatherRenderer.getHourlyUVIndexVal(wdf, hourlyIndex), 
						WeatherRenderer.getHourlyUVIndexIcon(wdf, hourlyIndex),
						WeatherRenderer.getHourlyRain(wdf, hourlyIndex)+" mm",
						WeatherRenderer.getHourlyRainPercentage(wdf, hourlyIndex)+" %", roundPanel, startY);

				startY=startY+2;
				hourlyIndex++;
				addForeCastPanel(WeatherRenderer.getHourlyHourRendering(wdf, hourlyIndex), 
						WeatherRenderer.getHourlyWeatherIcon(wdf,hourlyIndex),
						WeatherRenderer.getHourlyDescription(wdf,hourlyIndex),
						WeatherRenderer.getHourlyTemp(wdf, hourlyIndex, tempLabel), 
						WeatherRenderer.getHourlyUVIndexVal(wdf, hourlyIndex), 
						WeatherRenderer.getHourlyUVIndexIcon(wdf, hourlyIndex),
						WeatherRenderer.getHourlyRain(wdf, hourlyIndex)+" mm",
						WeatherRenderer.getHourlyRainPercentage(wdf, hourlyIndex)+" %", roundPanel, startY);
				
				startY=startY+2;
				hourlyIndex++;		
				addForeCastPanel(WeatherRenderer.getHourlyHourRendering(wdf, hourlyIndex), 
						WeatherRenderer.getHourlyWeatherIcon(wdf,hourlyIndex),
						WeatherRenderer.getHourlyDescription(wdf,hourlyIndex),
						WeatherRenderer.getHourlyTemp(wdf, hourlyIndex, tempLabel), 
						WeatherRenderer.getHourlyUVIndexVal(wdf, hourlyIndex), 
						WeatherRenderer.getHourlyUVIndexIcon(wdf, hourlyIndex),
						WeatherRenderer.getHourlyRain(wdf, hourlyIndex)+" mm",
						WeatherRenderer.getHourlyRainPercentage(wdf, hourlyIndex)+" %", roundPanel, startY);
	}

	

	private static void addForeCastPanel(String time, ImageIcon weatherIcon, String weatherDescription, String temp, String uvIndex, ImageIcon uvIndexLevel, String rain, String rainPercentage, JPanel roundPanel, int startY) {
		//JPanel myPanel = initializePanel(roundPanel);
		JLabel timeLabel = initializeLabel(roundPanel);
		JLabel weatherIconLabel = initializeLabel(roundPanel);
		JLabel tempLabel = initializeLabel(roundPanel);
		JLabel uvIndexLabel = initializeLabel(roundPanel);
		JLabel rainLabel = initializeLabel(roundPanel);
		JLabel rainIcon = initializeLabel(roundPanel);
		JLabel rainLabelPercent = initializeLabel(roundPanel);
		
		//myPanel.setForeground(Daten.efaConfig.getToolTipForegroundColor());
		//myPanel.setBackground(Daten.efaConfig.getToolTipBackgroundColor());

		timeLabel.setText(time);
		timeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD));
		timeLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		weatherIconLabel.setIcon(weatherIcon);
		weatherIconLabel.setIconTextGap(0);
		weatherIconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		weatherIconLabel.setToolTipText(weatherDescription);
		tempLabel.setText(temp);
		tempLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		tempLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		uvIndexLabel.setIcon(uvIndexLevel);
		uvIndexLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		uvIndexLabel.setIconTextGap(4);
		uvIndexLabel.setText(uvIndex);
		uvIndexLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		
		rainIcon.setIcon(WeatherIcons.getIcon(WeatherIcons.IMAGE_RAIN));
		rainIcon.setIconTextGap(0);
		rainIcon.setHorizontalTextPosition(SwingConstants.RIGHT);		
		
		rainLabel.setText(rain);
		rainLabel.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());

		rainLabelPercent.setText(rainPercentage);
		rainLabelPercent.setForeground(Daten.efaConfig.getToolTipHeaderForegroundColor());
		rainLabelPercent.setHorizontalTextPosition(SwingConstants.CENTER);

		
   	    roundPanel.add(timeLabel,   		new GridBagConstraints(0, startY, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 		new Insets(4,0,0,2), 0, 0));
   	    roundPanel.add(weatherIconLabel, 	new GridBagConstraints(1, startY, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 	    new Insets(4,2,0,2), 0, 0));
   	    
   	    roundPanel.add(tempLabel, 			new GridBagConstraints(4, startY, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 		new Insets(4,4,0,4), 0, 0));	
   	    roundPanel.add(uvIndexLabel, 		new GridBagConstraints(5, startY, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 	new Insets(4,4,3,4), 0, 0));
   		
   	    roundPanel.add(rainIcon, 			new GridBagConstraints(3, startY+1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.VERTICAL, 	new Insets(0,2,0,0), 0, 0));
   	    roundPanel.add(rainLabel, 			new GridBagConstraints(4, startY+1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.VERTICAL, 		new Insets(0,2,0,5), 0, 0));
   		roundPanel.add(rainLabelPercent, 	new GridBagConstraints(5, startY+1, 1, 1, 0.0, 1.0, GridBagConstraints.EAST, 	GridBagConstraints.VERTICAL, 		new Insets(0,2,4,2), 0, 0));

   	    /*
		myPanel.setLayout(new BorderLayout(0, 0));
		myPanel.add(timeLabel, BorderLayout.NORTH);
		myPanel.add(weatherIconLabel, BorderLayout.CENTER);
		myPanel.add(tempLabel, BorderLayout.SOUTH);
*/
		//return myPanel;

	}
	

}
