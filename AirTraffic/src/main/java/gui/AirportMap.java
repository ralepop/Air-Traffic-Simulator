package gui;

import model.Airport;
import model.Model;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class AirportMap extends Canvas {

    private final Model model;
    private final InactivityTimer inactivityTimer;
    private Airport selectedAirport = null;
    private boolean blink = false;
    private Timer timer;
    private final int SIZE = 16;

    public AirportMap(Model model, InactivityTimer inactivityTimer) {

        this.model = model;
        this.inactivityTimer = inactivityTimer;

        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                Airport airport = findAirport(e.getX(), e.getY());

                if (airport == null) {
                    return;
                }

                if (selectedAirport == airport) {
                    selectedAirport = null;

                    if (inactivityTimer != null) {
                        // nastavljamo tajmer
                        inactivityTimer.setPaused(false);
                    }
                } else {
                    selectedAirport = airport;

                    if (inactivityTimer != null) {
                        // pauziramo tajmer
                        inactivityTimer.setPaused(true);
                    }
                }

                repaint();
            }
        });

        // daemon (ne sprecavamo zatvaranje programa)
        timer = new Timer(true);

        // na svakih 500 milisekundi
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (selectedAirport != null) {
                    blink = !blink;
                    EventQueue.invokeLater(() -> repaint());
                }
            }
        }, 0, 500);
    }

    @Override
    public void paint(Graphics g) {

        int width = getWidth();
        int height = getHeight();

        for (Airport a : model.getAirportList()) {

            if (!a.isVisible()) {
                continue;
            }

            int x = convertX(a.getCoordX(), width);
            int y = convertY(a.getCoordY(), height);

            if (selectedAirport == a) {
                if (blink) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GRAY);
                }
            } else {
                g.setColor(Color.GRAY);
            }

            g.fillRect(x, y, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawString(a.getCode(), x + SIZE + 3, y + SIZE);
        }
    }

    // Skaliranje piksela sirine
    private int convertX(int x, int width) {
        return (x + 180) * (width - SIZE) / 360;
    }

    // Skaliranje piksela visine
    private int convertY(int y, int height) {
        return (90 - y) * (height - SIZE) / 180;
    }

    private Airport findAirport(int mouseX, int mouseY) {

        int width = getWidth();
        int height = getHeight();

        for (Airport a : model.getAirportList()) {

            if (!a.isVisible()) {
                continue;
            }

            int x = convertX(a.getCoordX(), width);
            int y = convertY(a.getCoordY(), height);

            Rectangle r = new Rectangle(x, y, SIZE, SIZE);

            if (r.contains(mouseX, mouseY)) {
                return a;
            }
        }

        return null;
    }

    public boolean airportSelected() {
        return selectedAirport != null;
    }

}
