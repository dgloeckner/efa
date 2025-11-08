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
		String tempLabel = ww.getTempLabel(true);
		
		int startY=1;
		// Build the main panel view

		roundPanel.add(getLocationHeader(ww.getCaption()), new GridBagConstraints(0, 0, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));		
		
		//roundPanel.add(
				addForeCastPanel("14:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_116_48),
						"Heiter bis wolkig",
						"28" + tempLabel, 
						"3.5", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_UV_INDEX_SEVERE),
						"3mm", "0 %", roundPanel,startY);
				/*,
						new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 4), 0, 0));*/
		//roundPanel.add(
				startY=startY+2;
				addForeCastPanel("15:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_113_48), 
						"Sonnig",
						"24" + tempLabel, 
						"3.5",
						WeatherIcons.getIcon(WeatherIcons.IMAGE_UV_INDEX_VERY_HIGH),
						"10mm","5 %", roundPanel, startY);
				/*,
						new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 4), 0, 0));*/
		//roundPanel.add(
	/*			startY=startY+2;

				addForeCastPanel("16:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_320_48),
						"leichter Regen", 
						"20" + tempLabel, 
						"3.5", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_UV_INDEX_HIGH),
						
						"50mm", "80 %",roundPanel, startY);/*,
						new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 4), 0, 0));*/
	/*	roundPanel.add(
				addForeCastPanel("17:00", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_WEATHER_386_1_48), 
						"18" + tempLabel, 
						"3.5", 
						WeatherIcons.getIcon(WeatherIcons.IMAGE_UV_INDEX_MEDIUM),
						"3mm"),
						new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
						new Insets(4, 2, 2, 18), 0, 0));
	*/
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

		
   	    roundPanel.add(timeLabel,   		new GridBagConstraints(0, startY, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 		new Insets(0,0,0,2), 0, 0));
   	    roundPanel.add(weatherIconLabel, 	new GridBagConstraints(1, startY, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 	    new Insets(0,2,0,2), 0, 0));
   	    
   	    roundPanel.add(tempLabel, 			new GridBagConstraints(2, startY, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 		new Insets(0,4,0,4), 0, 0));	
   	    roundPanel.add(uvIndexLabel, 		new GridBagConstraints(2, startY+1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 	new Insets(0,4,3,4), 0, 0));
   		
   	    roundPanel.add(rainIcon, 			new GridBagConstraints(3, startY, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.VERTICAL, 	new Insets(0,2,0,0), 0, 0));
   	    roundPanel.add(rainLabel, 			new GridBagConstraints(4, startY, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.VERTICAL, 		new Insets(0,2,0,5), 0, 0));
   		roundPanel.add(rainLabelPercent, 	new GridBagConstraints(5, startY, 1, 1, 0.0, 1.0, GridBagConstraints.EAST, 	GridBagConstraints.VERTICAL, 		new Insets(0,2,0,2), 0, 0));

   	    /*
		myPanel.setLayout(new BorderLayout(0, 0));
		myPanel.add(timeLabel, BorderLayout.NORTH);
		myPanel.add(weatherIconLabel, BorderLayout.CENTER);
		myPanel.add(tempLabel, BorderLayout.SOUTH);
*/
		//return myPanel;

	}
	

}
