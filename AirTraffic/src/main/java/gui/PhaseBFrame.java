package gui;

import model.Airport;
import model.Model;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PhaseBFrame extends Frame {

    private MainFrame mainFrame;
    private AirportMap airportMap;

    public PhaseBFrame(Model model, MainFrame mainFrame, InactivityTimer inactivityTimer) {
        super("Airport Map");
        this.mainFrame = mainFrame;

        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Mapa aerodroma
        airportMap = new AirportMap(model, inactivityTimer);
        add(airportMap, BorderLayout.CENTER);

        // Lista aerodroma
        Panel rightPanel = new Panel(new BorderLayout());
        rightPanel.setBackground(Color.LIGHT_GRAY);

        Label filterLabel = new Label("Airports List", Label.CENTER);
        filterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(filterLabel, BorderLayout.NORTH);

        // Panel sa listom checkboxova
        Panel listPanel = new Panel(new GridLayout(0, 1, 2, 2));
        for (Airport a : model.getAirportList()) {

            String info = String.format("%s (%s) [%d, %d]", a.getName(), a.getCode(), a.getCoordX(), a.getCoordY());
            Checkbox cb = new Checkbox(info, a.isVisible());

            cb.addItemListener(e -> {

                a.setVisible(e.getStateChange() == ItemEvent.SELECTED);
                airportMap.repaint();
            });

            listPanel.add(cb);
        }

        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.add(listPanel);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(300, 700));
        add(rightPanel, BorderLayout.EAST);

        // Donji panel sa Back button
        Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        Button backButton = new Button("Back");
        backButton.addActionListener(e -> closeAndReturn());
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Zatvaranje prozora na X
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAndReturn();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void closeAndReturn() {
        this.dispose();
        mainFrame.setVisible(true);
    }
}