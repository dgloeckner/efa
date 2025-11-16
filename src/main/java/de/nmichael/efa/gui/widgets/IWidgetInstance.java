package de.nmichael.efa.gui.widgets;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.nmichael.efa.data.LogbookRecord;

public interface IWidgetInstance {

    public void show(JPanel panel, int x, int y);
    public void show(JPanel panel, String orientation, boolean onMultiWidget);
    public void stop();
    
    public JComponent getComponent();
    public void runWidgetWarnings(int mode, boolean actionBegin, LogbookRecord r);
	
}
