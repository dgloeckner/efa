package de.nmichael.efa;

import io.github.bekoenig.getdown.data.Application;
import io.github.bekoenig.getdown.data.EnvConfig;
import io.github.bekoenig.getdown.data.Resource;
import io.github.bekoenig.getdown.net.Connector;
import io.github.bekoenig.getdown.net.Downloader;
import io.github.bekoenig.getdown.util.ProgressObserver;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EfaGetdownLauncher {

    public static void main(String[] args) {
        // Always start Swing from EDT
        SwingUtilities.invokeLater(() -> new EfaGetdownLauncher().start(args));
    }

    private void start(String[] args) {
        SplashUI ui = new SplashUI();
        ui.showSplash("Starte …", 0);

        Thread worker = new Thread(() -> {
            int exitCode = 0;
            try {
                runGetdown(args, ui);
            } catch (Throwable t) {
                t.printStackTrace();
                ui.showError("Update / Start fehlgeschlagen:\n" + t.getMessage());
                exitCode = 1;
            } finally {
                if (exitCode == 0) {
                    ui.close();
                } else {
                    // leave window open on error; user closes manually
                }
                // In a real app you might delay System.exit to let user read the error
                if (exitCode == 0) {
                    System.exit(0);
                }
            }
        }, "getdown-launcher-worker");

        worker.setDaemon(false);
        worker.start();
    }

    private void runGetdown(String[] args, SplashUI ui) throws Exception {
        // 1) Resolve EnvConfig (appdir, appbase, appid, etc.)
        List<EnvConfig.Note> notes = new ArrayList<>();
        EnvConfig env = EnvConfig.create(args, notes);
        if (env == null) {
            throw new IllegalStateException("EnvConfig.create() returned null (ungültiges appdir?)");
        }

        // Optional: log / inspect notes if you want
        for (EnvConfig.Note note : notes) {
            System.out.println("EnvConfig: " + note.level + " - " + note.message);
        }

        // 2) Create Application with default Connector
        Connector connector = Connector.DEFAULT;
        Application app = new Application(env);

        // 3) Init basic config (reads getdown.txt etc.)
        ui.showSplash("Lade Konfiguration …", 5);
        app.init(true); // checkPlatform = true

        // 4) Verify metadata & determine if update is needed
        ui.showSplash("Prüfe Metadaten …", 10);
        Application.StatusDisplay statusDisplay = messageKey ->
                ui.showSplash(resolveMessage(messageKey), 10);

        boolean needsUpdate = app.verifyMetadata(statusDisplay);

        // 5) Verify resources and collect what to download/install
        int[] alreadyValid = new int[1];
        Set<Resource> unpacked = new HashSet<>();
        Set<Resource> toInstall = new HashSet<>();
        Set<Resource> toDownload = new HashSet<>();

        ProgressObserver verifyProgress = percent ->
                ui.showSplash("Prüfe vorhandene Dateien …", 10 + percent / 2);

        app.verifyResources(verifyProgress, alreadyValid, unpacked, toInstall, toDownload);

        // 6) Download missing / invalid resources if needed
        if (needsUpdate || !toDownload.isEmpty()) {
            ui.showSplash("Lade Aktualisierung …", 60);

            Downloader downloader = new Downloader(connector) {
                @Override
                protected void downloadProgress(int percent, long remaining) {
                    // Called from worker threads – bounce to EDT
                    SwingUtilities.invokeLater(() ->
                            ui.showSplash("Lade Aktualisierung …", 60 + percent / 2));
                }

                @Override
                protected void downloadFailed(Resource rsrc, Exception cause) {
                    cause.printStackTrace();
                    SwingUtilities.invokeLater(() -> ui.showError(
                            "Download fehlgeschlagen für: " +
                                    (rsrc != null ? rsrc.getPath() : "<unbekannt>")
                                    + "\n" + cause.getMessage()));
                }
            };

            int maxConcurrent = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);
            boolean ok = downloader.download(toDownload, maxConcurrent);
            if (!ok) {
                throw new IOException("Download abgebrochen.");
            }

            // Re-verify, so that the new files get marked valid / installed
            alreadyValid[0] = 0;
            unpacked.clear();
            toInstall.clear();
            toDownload.clear();

            app.verifyResources(verifyProgress, alreadyValid, unpacked, toInstall, toDownload);
        }

        // 7) Unpack resources that need it
        ui.showSplash("Entpacke Dateien …", 85);
        app.unpackResources(
                percent -> ui.showSplash("Entpacke Dateien …", 85 + percent / 10),
                unpacked
        );

        // 8) Everything ready – launch application
        ui.showSplash("Starte Anwendung …", 95);

        Process child = app.createProcess(true); // true => use optimum JVM args if configured

        // Important: do NOT wait for the child here, Getdown-style launchers usually exit
        // so the actual app becomes the “main” process.
        // If you want to wait, you could do:
        // child.waitFor();
        ui.showSplash("Anwendung gestartet.", 100);
    }

    /**
     * Super-simple message resolver. In production you’d use MessageUtil / .properties.
     */
    private String resolveMessage(String key) {
        // Keys from Getdown are like "m.checking", "m.updating_metadata" etc.
        return key;
    }

    // ------------------------------------------------------------------------
    //  Simple splash UI
    // ------------------------------------------------------------------------

    private static final class SplashUI {
        private final JFrame frame;
        private final JProgressBar progressBar;
        private final JLabel statusLabel;

        SplashUI() {
            frame = new JFrame();
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            JLabel logoLabel = new JLabel();
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Load logo from classpath
            ImageIcon icon = loadLogo();
            if (icon != null) {
                logoLabel.setIcon(icon);
                // optional fixed size
                frame.setPreferredSize(new Dimension(400, 300));
            } else {
                logoLabel.setText("Meine App");
                logoLabel.setFont(logoLabel.getFont().deriveFont(Font.BOLD, 24f));
                frame.setPreferredSize(new Dimension(300, 200));
            }

            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(false);
            progressBar.setPreferredSize(new Dimension(100, 6));

            statusLabel = new JLabel(" ");
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            statusLabel.setFont(statusLabel.getFont().deriveFont(12f));

            JPanel bottom = new JPanel();
            bottom.setLayout(new BorderLayout(5, 5));
            bottom.add(progressBar, BorderLayout.CENTER);
            bottom.add(statusLabel, BorderLayout.SOUTH);
            bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(logoLabel, BorderLayout.CENTER);
            frame.getContentPane().add(bottom, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
        }

        void showSplash(String status, int percent) {
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
            statusLabel.setText(status);
            progressBar.setValue(Math.max(0, Math.min(100, percent)));
        }

        void showError(String message) {
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
            statusLabel.setText("<html><body style='text-align:center;'>" +
                    message.replace("\n", "<br>") +
                    "</body></html>");
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
        }

        void close() {
            frame.setVisible(false);
            frame.dispose();
        }

        private ImageIcon loadLogo() {
            // Hard-coded resource path; adjust to your actual location
            try {
                URL url = getClass().getClassLoader().getResource("launcher-logo.png");
                if (url == null) {
                    return null;
                }
                Image img = new ImageIcon(url).getImage();
                // Optional scaling
                Image scaled = img.getScaledInstance(256, 256, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
