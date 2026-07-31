package gui;

import exceptions.MyException;
import model.Airport;
import model.Flight;
import model.Menu;
import model.Model;

import java.awt.*;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/*
* Klasa koja prikazuje sve informacije o aerodromima i letovima.
* */

public class MainFrame extends Frame {

    private Menu menu;
    private Model model = new Model();

    // avion
    private TextField airportName = new TextField(10);
    private TextField airportCode = new TextField(5);
    private TextField coordXField = new TextField(5);
    private TextField coordYField = new TextField(5);
    private Button addAirportButton = new Button("Add");

    // let
    private TextField startAirportField = new TextField(5);
    private TextField endAirportField = new TextField(5);
    private TextField departureTime = new TextField(5);
    private TextField durationField = new TextField(5);
    private Button addFlightButton = new Button("Add");

    private Button saveToFileButton = new Button("Save");
    private Button loadFromFileButton = new Button("Load");
    private Button refreshButton = new Button("Refresh");
    private Button phaseBButton = new Button("Phase B");
    private Button phaseCButton = new Button("Phase C");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private void showError(String msg) {
        Dialog errorDialog = new Dialog(this, "Greska", true);
        errorDialog.setLayout(new FlowLayout());
        errorDialog.add(new Label(msg));
        Button ok = new Button("Ok");
        ok.addActionListener(e -> errorDialog.dispose());
        errorDialog.add(ok);
        errorDialog.setSize(350, 120);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
    }

    public MainFrame() {
        super("Air Traffic Simulator");

        setSize(1000, 700);
        setLayout(new BorderLayout(10, 10));

        Panel airportPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        airportPanel.add(new Label("Name:"));
        airportPanel.add(airportName);
        airportPanel.add(new Label("Code:"));
        airportPanel.add(airportCode);
        airportPanel.add(new Label("X:"));
        airportPanel.add(coordXField);
        airportPanel.add(new Label("Y:"));
        airportPanel.add(coordYField);
        airportPanel.add(addAirportButton);

        Panel flightPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        flightPanel.add(new Label("Origin:"));
        flightPanel.add(startAirportField);
        flightPanel.add(new Label("Destination:"));
        flightPanel.add(endAirportField);
        flightPanel.add(new Label("Time:"));
        flightPanel.add(departureTime);
        flightPanel.add(new Label("Duration:"));
        flightPanel.add(durationField);
        flightPanel.add(addFlightButton);

        Panel actionPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(saveToFileButton);
        actionPanel.add(loadFromFileButton);
        actionPanel.add(refreshButton);
        actionPanel.add(phaseBButton);
        actionPanel.add(phaseCButton);

        Panel topPanel = new Panel(new GridLayout(3, 1, 5, 5));
        topPanel.add(airportPanel);
        topPanel.add(flightPanel);
        topPanel.add(actionPanel);

        add(topPanel, BorderLayout.NORTH);

        menu = new Menu();
        add(menu, BorderLayout.CENTER);

        // Zatvaranje prozora na X dugme
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        // Dugme za dodavanje aerodroma
        addAirportButton.addActionListener(e -> {

            try {

                String name = airportName.getText();
                String code = airportCode.getText();
                int coordX = Integer.parseInt(coordXField.getText());
                int coordY = Integer.parseInt(coordYField.getText());

                Airport airport = new Airport(name, code, coordX, coordY);
                model.addAirport(airport);
                menu.addAirport(airport);

                airportName.setText("");
                airportCode.setText("");
                coordXField.setText("");
                coordYField.setText("");

            } catch (MyException ex) {
                showError("Pogresan unos: " + (ex.getMessage() != null ? ex.getMessage() : "proverite format koordinata."));
            } catch (NumberFormatException ex) {
                showError("Pogresan unos: Koordinate moraju biti celi brojevi.");
            }

        });

        // Dugme za dodavanje letova
        addFlightButton.addActionListener(e -> {
            try {

                Airport startAirport = model.getAirportByCode(startAirportField.getText());
                Airport endAirport = model.getAirportByCode(endAirportField.getText());
                LocalTime departure = LocalTime.parse(departureTime.getText(), formatter);
                int duration = Integer.parseInt(durationField.getText());

                Flight flight = new Flight(startAirport, endAirport, departure, duration);
                model.addFlight(flight);
                menu.addFlight(flight);

                startAirportField.setText("");
                endAirportField.setText("");
                departureTime.setText("");
                durationField.setText("");

            } catch (MyException ex) {
                showError(ex.getMessage());
            } catch (NumberFormatException ex) {
                showError("Trajanje leta mora biti ceo broj.");
            } catch (DateTimeParseException ex) {
                showError("Vreme poletanja mora biti u formatu HH:mm.");
            }
        });

        // Dugme za cuvanje u fajl
        saveToFileButton.addActionListener(e -> {

            Dialog saveDialog = new Dialog(this, "Sacuvaj podatke", true);
            saveDialog.setLayout(new FlowLayout());

            Checkbox csvCheckbox = new Checkbox("CSV", false);
            Checkbox jsonCheckbox = new Checkbox("JSON", false);
            Button confirmButton = new Button("Sacuvaj");
            confirmButton.setEnabled(false);

            ItemListener checkboxListener = ie -> {
                boolean any = csvCheckbox.getState() || jsonCheckbox.getState();
                confirmButton.setEnabled(any);
            };

            csvCheckbox.addItemListener(checkboxListener);
            jsonCheckbox.addItemListener(checkboxListener);

            confirmButton.addActionListener(ae -> {

                try {

                    if (csvCheckbox.getState()) {
                        model.saveToCSV(new File("data.csv"));
                    }
                    if (jsonCheckbox.getState()) {
                        model.saveToJSON(new File("data.json"));
                    }

                } catch (IOException ex) {
                    showError("Doslo je do greske prilikom cuvanja fajla.");
                }

                saveDialog.dispose();
            });

            saveDialog.add(csvCheckbox);
            saveDialog.add(jsonCheckbox);
            saveDialog.add(confirmButton);

            saveDialog.setSize(300, 150);
            saveDialog.setLocationRelativeTo(this);

            saveDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    saveDialog.dispose();
                }
            });

            saveDialog.setVisible(true);
        });

        // Dugme za ucitavanje podataka iz fajla
        loadFromFileButton.addActionListener(e -> {

            FileDialog fd = new FileDialog(this, "Ucitaj fajl", FileDialog.LOAD);
            fd.setVisible(true);

            String filename = fd.getFile();
            String directory = fd.getDirectory();

            if (filename == null) {
                return;
            }

            File file = new File(directory, filename);

            try {

                model.loadFromFile(file);
                model.checkExceptions();
                menu.reloadMenu(model);

            } catch (IOException ex) {
                showError("Greska pri ucitavanju fajla: " + ex.getMessage());
            } catch (MyException ex) {
                showError(ex.getMessage());
            }
        });

        refreshButton.addActionListener(e -> {
            model.refreshAirports();
            model.refreshFlights();
            menu.reloadMenu(model);
        });

        setVisible(true);

        InactivityTimer inactivityTimer = new InactivityTimer(this);
        Thread inactivityThread = new Thread(inactivityTimer);
        inactivityThread.setDaemon(true);
        inactivityThread.start();

    }

    public static void main(String[] args) {
        new MainFrame();
    }

}
