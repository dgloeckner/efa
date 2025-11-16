package de.nmichael.efa.gui.widgets;

import java.util.Vector;

import de.nmichael.efa.core.items.IItemFactory;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeDouble;
import de.nmichael.efa.core.items.ItemTypeFile;
import de.nmichael.efa.core.items.ItemTypeInteger;
import de.nmichael.efa.core.items.ItemTypeItemList;
import de.nmichael.efa.core.items.ItemTypeLongLat;
import de.nmichael.efa.core.items.ItemTypeString;
import de.nmichael.efa.core.items.ItemTypeStringList;
import de.nmichael.efa.gui.EfaGuiUtils;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

public class HTMLWidgetMulti extends Widget implements IWidget, IItemFactory {


    public static final String PARAM_WIDTH          = "Width";
    public static final String PARAM_HEIGHT         = "Height";
    public static final String PARAM_SCALE          = "Scale";
    public static final String PARAM_URL            = "Url";

	private static final String PARAM_HTML_PAGELIST = "MultiHtmlPageList";
	
	private ItemTypeItemList htmlPageList;

    
    public HTMLWidgetMulti() {
        super("Html (Multi)", International.getString("HTML-Widget (Multi)"), true, true);

		addParameterInternal(htmlPageList = new ItemTypeItemList(PARAM_HTML_PAGELIST, new Vector<IItemType[]>(), this,
				IItemType.TYPE_PUBLIC, "",
				International.getString("HTML-Seiten")));
		htmlPageList.setShortDescription(International.getString("HTML-Seiten"));		
		htmlPageList.setRepeatTitle(true);
		
    }

   

    /*public void setSize(int width, int height) {
        ((ItemTypeInteger)getParameterInternal(PARAM_WIDTH)).setValue(width);
        ((ItemTypeInteger)getParameterInternal(PARAM_HEIGHT)).setValue(height);
    }*/

    public int getWidth(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeInteger)list.getItem(i, PARAM_WIDTH)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return 50;
        }
    }

    public int getHeight(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeInteger)list.getItem(i, PARAM_HEIGHT)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return 50;
        }
    }
    
    public double getScale(ItemTypeItemList list, int i) {
        try {
        	IItemType iscale = ((IItemType)list.getItem(i, PARAM_SCALE));
            final double scale = (iscale != null ? ((ItemTypeDouble)iscale).getValue() : 1.0);
            return scale;
        } catch(Exception e) {
            Logger.logdebug(e);
            return 1.0;
        }    	
    }

    public String getUrl(ItemTypeItemList list, int i) {
        try {
            return ((ItemTypeString)list.getItem(i, PARAM_URL)).getValue();
        } catch(Exception e) {
            Logger.logdebug(e);
            return "";
        }	
    }

	@Override
	public Vector<WidgetInstance> createInstances() {
		Vector <WidgetInstance> returnList = new Vector <WidgetInstance>();
		
		ItemTypeItemList myWList=this.getHtmlPageList();
		if (myWList==null) {
			return returnList;
		}		
		
		for (int i = 0; i < myWList.size(); i++) {

			HTMLWidgetInstance wi = new HTMLWidgetInstance();
			wi.setHeight(getHeight(myWList,i));
			wi.setScale(getScale(myWList,i));
			wi.setUpdateInterval(getUpdateInterval());
			wi.setUrl(this.getUrl(myWList,i));
			wi.setWidth(getWidth(myWList,i));
			returnList.add(wi);
		}
		return returnList;
	}
    
	/*
	 * getDefaultItems is the factory for all location elements
	 */
	public IItemType[] getDefaultItems(String itemName) {
		//which Item do we want to get the elements from?
		if (itemName.endsWith(PARAM_HTML_PAGELIST)) {
			
            ItemTypeItemList item = (ItemTypeItemList)getParameterInternal(PARAM_HTML_PAGELIST);
            int i = item.size()+1;
			
            // build the GUI
           
            IItemType[] items = new IItemType[4];
            i=0;
            items[i] = new ItemTypeInteger(PARAM_WIDTH, 200, 1, Integer.MAX_VALUE, false,
                    IItemType.TYPE_PUBLIC, "", International.getString("Breite"));
            items[i++].setPadding(10,0,0,0);

            items[i++] = new ItemTypeInteger(PARAM_HEIGHT, 50, 1, Integer.MAX_VALUE, false,
                    IItemType.TYPE_PUBLIC, "", International.getString("Höhe"));
            items[i++] = new ItemTypeDouble(PARAM_SCALE, 1, 0.1, 10, false,
                    IItemType.TYPE_PUBLIC, "", International.getString("Skalierung"));

            items[i++] = new ItemTypeFile(PARAM_URL, "",
                    International.getString("HTML-Seite"),
                    International.getString("HTML-Seite"),
                    null,ItemTypeFile.MODE_OPEN,ItemTypeFile.TYPE_FILE,
                    IItemType.TYPE_PUBLIC, "",
                    "URL");

            return items;
		}
		return null;
	}	
	
    ItemTypeItemList getHtmlPageList() {
        return (ItemTypeItemList)getParameterInternal(PARAM_HTML_PAGELIST);
    }	
    
}
