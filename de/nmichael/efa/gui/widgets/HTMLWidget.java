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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.core.items.ItemTypeDouble;
import de.nmichael.efa.core.items.ItemTypeFile;
import de.nmichael.efa.core.items.ItemTypeInteger;
import de.nmichael.efa.core.items.ItemTypeBoolean;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.HttpCachedFetcher;

public class HTMLWidget extends Widget {

    public static final String PARAM_WIDTH          = "Width";
    public static final String PARAM_HEIGHT         = "Height";
    public static final String PARAM_SCALE          = "Scale";
    public static final String PARAM_URL            = "Url";
    public static final String PARAM_USE_HTTP_CACHE = "UseHttpCache";

    private JScrollPane scrollPane = new JScrollPane();
    private JEditorPane htmlPane;
    private HTMLUpdater htmlUpdater;

    public HTMLWidget() {
        super("Html", International.getString("HTML-Widget"), true);

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

        addParameterInternal(new ItemTypeBoolean(PARAM_USE_HTTP_CACHE, true,
                IItemType.TYPE_PUBLIC, "",
                International.getString("UseHttpCache")));
    }

    void construct() {
        IItemType iscale = getParameterInternal(PARAM_SCALE);
        final double scale = (iscale != null ? ((ItemTypeDouble)iscale).getValue() : 1.0);
        htmlPane = new JEditorPane() {
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform old = g2d.getTransform();
                g2d.scale(scale, scale);
                super.paint(g2d);
                g2d.setTransform(old);
            }
        };

        htmlPane.setContentType("text/html");
        if (Daten.isEfaFlatLafActive()) {
            htmlPane.putClientProperty("html.disable", Boolean.TRUE); 
        	htmlPane.setFont(htmlPane.getFont().deriveFont(Font.PLAIN,14));
        }
        htmlPane.setEditable(false);
        // following hyperlinks is automatically "disabled" (if no HyperlinkListener is taking care of it)
        // But we also need to disable submiting of form data:
        HTMLEditorKit kit = (HTMLEditorKit)htmlPane.getEditorKit();
        kit.setAutoFormSubmission(false);

        if (getWidth() > 0 && getHeight() > 0) {
            scrollPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
        }
        scrollPane.getViewport().add(htmlPane, null);
        if (htmlUpdater == null) {
            htmlUpdater = new HTMLUpdater();
        }
        IItemType icache = getParameterInternal(PARAM_USE_HTTP_CACHE);
        boolean useCache = (icache != null ? ((ItemTypeBoolean)icache).getValue() : true);
        htmlUpdater.setUseHttpCaching(useCache);
        htmlUpdater.setPage(getParameterInternal(PARAM_URL).toString(), getUpdateInterval());
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

    public JComponent getComponent() {
        return scrollPane;
    }

    public void stop() {
        if (htmlUpdater != null) {
            htmlUpdater.stopHTML();
        }
    }

    public void runWidgetWarnings(int mode, boolean actionBegin, LogbookRecord r) {
        // nothing to do
    }

    private class HTMLUpdater {

        private final java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "HTMLWidget.HtmlUpdater");
            t.setDaemon(true);
            return t;
        });
        private volatile String url = null;
        private volatile int updateIntervalInSeconds = 24*3600;
        private volatile ScheduledFuture<?> future;
        private final HttpCachedFetcher fetcher = new HttpCachedFetcher();
        private volatile boolean useHttpCaching = true;

        private void schedule() {
            if (future != null) {
                future.cancel(false);
            }
            int interval = (updateIntervalInSeconds <= 0 ? 24*3600 : updateIntervalInSeconds);
            future = scheduler.scheduleAtFixedRate(this::updateOnceSafe, 0, interval, java.util.concurrent.TimeUnit.SECONDS);
        }

        private void updateOnceSafe() {
            try {
                updateOnce();
            } catch (Throwable e) {
                Logger.logdebug(new Exception(e));
            }
        }

        private void updateOnce() {
            String u = this.url;
            if (u == null || u.trim().isEmpty()) {
                return;
            }
            u = EfaUtil.correctUrl(u);
            final String urlToLoad = u;
            try {
                java.net.URL urlObj = new java.net.URL(urlToLoad);
                String protocol = urlObj.getProtocol();
                // For local files or unsupported protocols, delegate to JEditorPane directly on EDT
                if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            htmlPane.setPage(urlObj);
                        } catch (IOException ee) {
                            htmlPane.setText(International.getString("FEHLER") + ": "
                                    + International.getMessage("Kann Adresse '{url}' nicht öffnen: {message}", urlToLoad, ee.toString()));
                        }
                    });
                    return;
                }

                if (useHttpCaching) {
                    // Use HttpCachedFetcher for HTTP(S) with conditional requests
                    try {
                        HttpCachedFetcher.FetchResult res = fetcher.fetch();
                        if (res.isNotModified()) {
                            Logger.log(Logger.INFO, Logger.MSG_GENERIC, "HTMLWidget: Content not modified (304) for " + urlToLoad);
                            return;
                        }
                        if (res.isOk() && res.body != null) {
                            String charset = (res.charset != null ? res.charset : "UTF-8");
                            java.io.Reader reader = new java.io.InputStreamReader(new java.io.ByteArrayInputStream(res.body), charset);
                            HTMLEditorKit kit = new HTMLEditorKit();
                            final HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
                            doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
                            doc.setBase(res.baseUrl);
                            try {
                                kit.read(reader, doc, 0);
                            } finally {
                                try { reader.close(); } catch (Exception ignore) {}
                            }
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    htmlPane.setDocument(doc);
                                } catch (Exception ee) {
                                    htmlPane.setText(International.getString("FEHLER") + ": "
                                            + International.getMessage("Kann Adresse '{url}' nicht öffnen: {message}", urlToLoad, ee.toString()));
                                }
                            });
                        } else {
                            final String msg = "HTTP " + res.httpStatus + " for " + urlToLoad;
                            SwingUtilities.invokeLater(() -> htmlPane.setText(International.getString("FEHLER") + ": " + msg));
                        }
                    } catch (UnsupportedOperationException uoe) {
                        // Should not happen because we guard by protocol above, but just in case
                        SwingUtilities.invokeLater(() -> htmlPane.setText(International.getString("FEHLER") + ": " + uoe.getMessage()));
                    }
                } else {
                    // No ETag-based caching: let JEditorPane handle the HTTP URL directly
                    SwingUtilities.invokeLater(() -> {
                        try {
                            htmlPane.setPage(urlObj);
                        } catch (IOException ee) {
                            htmlPane.setText(International.getString("FEHLER") + ": "
                                    + International.getMessage("Kann Adresse '{url}' nicht öffnen: {message}", urlToLoad, ee.toString()));
                        }
                    });
                }
            } catch (Exception ee) {
                SwingUtilities.invokeLater(() -> {
                    htmlPane.setText(International.getString("FEHLER") + ": "
                            + International.getMessage("Kann Adresse '{url}' nicht öffnen: {message}", urlToLoad, ee.toString()));
                });
            }
        }

        public synchronized void setUseHttpCaching(boolean use) {
            this.useHttpCaching = use;
        }

        public synchronized void setPage(String url, int updateIntervalInSeconds) {
            this.url = url;
            if (updateIntervalInSeconds <= 0) {
                updateIntervalInSeconds = 24*3600;
            }
            this.updateIntervalInSeconds = updateIntervalInSeconds;
            // set URL in fetcher (resets validators internally)
            fetcher.setUrl(this.url);
            schedule();
        }

        public synchronized void stopHTML() {
            try {
                if (future != null) {
                    future.cancel(true);
                }
            } catch (Exception ignore) {}
            scheduler.shutdownNow();
        }

    }


}
