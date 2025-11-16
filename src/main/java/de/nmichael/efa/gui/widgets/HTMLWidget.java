/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.widgets;

import java.util.Vector;

import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeDouble;
import de.nmichael.efa.core.items.ItemTypeFile;
import de.nmichael.efa.core.items.ItemTypeInteger;
import de.nmichael.efa.util.International;

public class HTMLWidget extends Widget {

    public static final String PARAM_WIDTH          = "Width";
    public static final String PARAM_HEIGHT         = "Height";
    public static final String PARAM_SCALE          = "Scale";
    public static final String PARAM_URL            = "Url";

    public HTMLWidget() {
        super("Html", International.getString("HTML-Widget"), true, true);

        addParameterInternal(new ItemTypeInteger(PARAM_WIDTH, 200, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, "",
                International.getString("Breite")));

        addParameterInternal(new ItemTypeInteger(PARAM_HEIGHT, 50, 1, Integer.MAX_VALUE, false,
                IItemType.TYPE_PUBLIC, "",
                International.getString("Höhe")));

        addParameterInternal(new ItemTypeDouble(PARAM_SCALE, 1, 0.1, 10, false,
                IItemType.TYPE_PUBLIC, "",
                International.getString("Skalierung")));

        addParameterInternal(new ItemTypeFile(PARAM_URL, "",
                International.getString("HTML-Seite"),
                International.getString("HTML-Seite"),
                null,ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                IItemType.TYPE_PUBLIC, "",
                "URL"));
    }

   

    public void setSize(int width, int height) {
        ((ItemTypeInteger)getParameterInternal(PARAM_WIDTH)).setValue(width);
        ((ItemTypeInteger)getParameterInternal(PARAM_HEIGHT)).setValue(height);
    }

    public int getWidth() {
        return ((ItemTypeInteger)getParameterInternal(PARAM_WIDTH)).getValue();
    }

    public int getHeight() {
        return ((ItemTypeInteger)getParameterInternal(PARAM_HEIGHT)).getValue();
    }
    
    public double getScale() {
    	IItemType iscale = getParameterInternal(PARAM_SCALE);
        final double scale = (iscale != null ? ((ItemTypeDouble)iscale).getValue() : 1.0);
        return scale;
    }

    public String getUrl() {
        return ((ItemTypeFile)getParameterInternal(PARAM_URL)).getValue(); 	
    }

	@Override
	public Vector<WidgetInstance> createInstances() {
		Vector <WidgetInstance> returnList = new Vector <WidgetInstance>();
		HTMLWidgetInstance wi = new HTMLWidgetInstance();
		wi.setHeight(getHeight());
		wi.setScale(getScale());
		wi.setUpdateInterval(getUpdateInterval());
		wi.setUrl(this.getUrl());
		wi.setWidth(getWidth());
		returnList.add(wi);
		return returnList;
	}
    
    

}
