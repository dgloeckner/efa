package de.nmichael.efa.gui.widgets;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.nmichael.efa.Daten;
import de.nmichael.efa.gui.util.RoundedPanel;

public abstract class WeatherRenderer {

	public static void renderWeather(WeatherDataForeCast wdf, JPanel roundPanel, WeatherWidget ww) {

	}
	
	protected static JPanel initializePanel(JPanel mainPanel) {
		JPanel ret = new JPanel();
		ret.setOpaque(false);
		ret.setForeground(mainPanel.getForeground());
		ret.setLayout(new GridBagLayout());		
		return ret;
	}
	
	protected static JLabel initializeLabel(JPanel mainPanel) {
		JLabel ret = new JLabel();
		ret.setOpaque(false);
		ret.setForeground(mainPanel.getForeground());
		return ret;
	}	
	
	protected static JPanel getLocationHeader(String caption, Boolean isError) {
		RoundedPanel titlePanel = new RoundedPanel();
		titlePanel.setLayout(new GridBagLayout());
		titlePanel.setBackground(isError ? Daten.efaConfig.getErrorBackgroundColor() : Daten.efaConfig.getToolTipHeaderBackgroundColor());
		titlePanel.setForeground(isError ? Daten.efaConfig.getErrorForegroundColor() : Daten.efaConfig.getToolTipHeaderForegroundColor());
	
		JLabel titleLabel = new JLabel();
		titleLabel.setText(caption);
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		titleLabel.setForeground(titlePanel.getForeground());
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		
		titlePanel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		
		return titlePanel;
	}	
	
	protected static JPanel getLocationHeader(String caption) {
		return getLocationHeader(caption, false);
	}
	
}
