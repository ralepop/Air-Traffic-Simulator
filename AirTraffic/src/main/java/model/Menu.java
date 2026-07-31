package model;

/*
*
* */

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class Menu extends Panel {

    private Panel airportsListPanel;
    private Panel flightsListPanel;

    private ScrollPane airportsScrollPane;
    private ScrollPane flightsScrollPane;

    private int rowHeight;
    private int titleHeight;

    // TODO: mozda ih promeniti na 4
    private int airportRows = 5;
    private int flightRows = 5;
    private int gap = 5;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public void setVisibleRows(int aRows, int fRows) {
        airportRows = Math.max(aRows, 1);
        flightRows = Math.max(fRows, 1);

        airportsScrollPane.setPreferredSize(new Dimension(1, calculateHeightRows(airportRows)));
        flightsScrollPane.setPreferredSize(new Dimension(1, calculateHeightRows(flightRows)));

        validate();
        repaint();
    }

    public Menu() {

        setLayout(new GridLayout(1, 2, 2, 0));

        // Levi panel za aerodrome
        Panel airports = new Panel(new BorderLayout());
        airports.setBackground(Color.LIGHT_GRAY);
        Label airportsLabel = new Label("Airports");
        airportsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        airports.add(airportsLabel, BorderLayout.NORTH);

        // TODO: mozda izmeniti vgap: 6
        airportsListPanel = new Panel(new GridLayout(0, 4, 4, 4));
        airportsListPanel.add(new Label("Name"));
        airportsListPanel.add(new Label("Code"));
        airportsListPanel.add(new Label("X coord"));
        airportsListPanel.add(new Label("Y coord"));

        Panel leftPanel = new Panel(new BorderLayout());
        leftPanel.add(airportsListPanel, BorderLayout.NORTH);
        airportsScrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        airportsScrollPane.add(leftPanel);
        airports.add(airportsScrollPane, BorderLayout.CENTER);
        add(airports);

        // Desni panel za letove
        Panel flights = new Panel(new BorderLayout());
        flights.setBackground(Color.LIGHT_GRAY);
        Label flightsLabel = new Label("Flights");
        flightsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        flights.add(flightsLabel, BorderLayout.NORTH);

        // TODO: mozda izmeniti vgap: 6
        flightsListPanel = new Panel(new GridLayout(0, 4, 4, 4));
        flightsListPanel.add(new Label("Origin"));
        flightsListPanel.add(new Label("Destination"));
        flightsListPanel.add(new Label("Time"));
        flightsListPanel.add(new Label("Duration"));

        Panel rightPanel = new Panel(new BorderLayout());
        rightPanel.add(flightsListPanel, BorderLayout.NORTH);
        flightsScrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        flightsScrollPane.add(rightPanel);
        flights.add(flightsScrollPane, BorderLayout.CENTER);
        add(flights);

        FontMetrics row = getFontMetrics(new Font("Arial", Font.PLAIN, 16));
        rowHeight = Math.max(row.getHeight(), 20);
        titleHeight = getFontMetrics(new Font("Arial", Font.BOLD, 16)).getHeight() + 5;

        setVisibleRows(airportRows, flightRows);
    }

    // Funkcija koja racuna trenutnu visinu za redove
    public int calculateHeightRows(int n) {
        return titleHeight + (n + 1) * (rowHeight + gap) + 5;
    }

    // Funkcija koja dodaje aerodrom u listu
    public void addAirport(Airport airport) {
        airportsListPanel.add(new Label(airport.getName()));
        airportsListPanel.add(new Label(airport.getCode()));
        airportsListPanel.add(new Label(Integer.toString(airport.getCoordX())));
        airportsListPanel.add(new Label(Integer.toString(airport.getCoordY())));
        airportRows++;

        airportsListPanel.validate();
        validate();
        repaint();
    }

    // Funkcija koja dodaje let u listu
    public void addFlight(Flight flight) {

        flightsListPanel.add(new Label(flight.getStartAirport().getCode()));
        flightsListPanel.add(new Label(flight.getEndAirport().getCode()));
        flightsListPanel.add(new Label(flight.getDepartureTime().format(formatter)));
        flightsListPanel.add(new Label(String.valueOf(flight.getFlightDuration())));
        flightRows++;

        flightsListPanel.validate();
        validate();
        repaint();
    }

    // Funkcija koja refreshuje informacije o aerodromima i letovima
    public void reloadMenu(Model model) {

        airportRows = 0;
        flightRows = 0;

        airportsListPanel.removeAll();
        flightsListPanel.removeAll();

        airportsListPanel.add(new Label("Name"));
        airportsListPanel.add(new Label("Code"));
        airportsListPanel.add(new Label("X"));
        airportsListPanel.add(new Label("Y"));

        flightsListPanel.add(new Label("Origin"));
        flightsListPanel.add(new Label("Destination"));
        flightsListPanel.add(new Label("Time"));
        flightsListPanel.add(new Label("Duration"));

        for (Airport a : model.getAirportList()) {
            addAirport(a);
        }

        for (Flight f : model.getFlightList()) {
            addFlight(f);
        }

        validate();
        repaint();
    }
}
