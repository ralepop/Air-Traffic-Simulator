package model;

/*
 * Klasa koja cuva sve informacije o aerodromima i letovima.
 * Radi sa CSV fajlovima.
 * */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exceptions.MyException;

import java.awt.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Model {

    private List<Airport> airportList = new ArrayList<>();
    private List<Flight> flightList = new ArrayList<>();
    private Map<String, Airport> byCodeAirport = new HashMap<>();
    private Map<Point, Airport> byXYCoord = new HashMap<>();

    private int cntExc = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public List<Airport> getAirportList() {
        return airportList;
    }

    public List<Flight> getFlightList() {
        return flightList;
    }

    // Funkcija koja dodaje aerodrom u Model
    public void addAirport(Airport airport) throws MyException {

        if (byCodeAirport.containsKey(airport.getCode())) {
            throw new MyException("Aerodrom " + airport.getCode() + " vec postoji.");
        }

        Point coord = new Point(airport.getCoordX(), airport.getCoordY());

        if (byXYCoord.containsKey(coord)) {
            throw new MyException("Aerodrom sa ovim koordinatama vec postoji.");
        }

        airportList.add(airport);
        byCodeAirport.put(airport.getCode(), airport);
        byXYCoord.put(coord, airport);
    }

    // Funckija koja dodaje novi let u Model
    public void addFlight(Flight flight) throws MyException {

        String startAirport = flight.getStartAirport().getCode();
        String endAirport = flight.getEndAirport().getCode();

        if (!byCodeAirport.containsKey(startAirport)) {
            throw new MyException("Aerodrom " + startAirport + " ne postoji.");
        } else if (!byCodeAirport.containsKey(endAirport)) {
            throw new MyException("Aerodrom " + endAirport + " ne postoji.");
        }

        if (startAirport.equals(endAirport)) {
            throw new MyException("Aerodromi moraju biti razliciti.");
        }

        flightList.add(flight);
    }

    public Airport getAirportByCode(String code) throws MyException {
        Airport airport = byCodeAirport.get(code);
        if (airport == null) {
            throw new MyException("Aerodrom " + code + " ne postoji.");
        }
        return airport;
    }

    public void saveToCSV(File file) throws IOException {

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {

            out.println("# AIRPORTS");
            out.println("CODE,NAME,X,Y");
            for (Airport a : airportList) {
                out.printf("%s,%s,%d,%d%n", a.getCode(), a.getName(), a.getCoordX(), a.getCoordY());
            }

            out.println("# FLIGHTS");
            out.println("FROM,TO,DEPARTURE,DURATION");
            for (Flight fl : flightList) {
                out.printf("%s,%s,%s,%d%n", fl.getStartAirport().getCode(), fl.getEndAirport().getCode(), fl.getDepartureTime(), fl.getFlightDuration());
            }
        }
    }

    public void saveToJSON(File file) throws IOException {

        JsonObject root = new JsonObject();

        JsonArray airports = new JsonArray();
        for (Airport a : airportList) {
            JsonObject obj = new JsonObject();
            obj.addProperty("code", a.getCode());
            obj.addProperty("name", a.getName());
            obj.addProperty("x", a.getCoordX());
            obj.addProperty("y", a.getCoordY());
            airports.add(obj);
        }
        root.add("airports", airports);

        JsonArray flights = new JsonArray();
        for (Flight fl : flightList) {
            JsonObject obj = new JsonObject();
            obj.addProperty("from", fl.getStartAirport().getCode());
            obj.addProperty("to", fl.getEndAirport().getCode());
            obj.addProperty("departure", fl.getDepartureTime().format(formatter));
            obj.addProperty("duration", fl.getFlightDuration());
            flights.add(obj);
        }
        root.add("flights", flights);

        try (FileWriter writer = new FileWriter(file)) {
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, writer);
        }
    }

    public void loadFromFile(File file) throws IOException {

        refreshAirports();
        refreshFlights();

        if (file.getName().endsWith(".csv")) {
            loadCSV(file);
        } else if (file.getName().endsWith(".json")) {
            loadJSON(file);
        } else {
            throw new IOException("Pogresan format.");
        }
    }

    private void loadCSV(File file) throws IOException {

        // Ucitavanje aerodroma
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // Preskacemo # AIRPORTS
            br.readLine(); // Preskacemo CODE,NAME,X,Y

            String line;

            // Obradjujemo aerodrome
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("# FLIGHTS")) {
                    br.readLine(); // Preskacemo FROM,TO,DEPARTURE,DURATION
                    break;
                }

                try {
                    String[] str = line.split(",");

                    if (str.length == 4) {

                        Airport airport = new Airport(str[1], str[0], Integer.parseInt(str[2]), Integer.parseInt(str[3]));
                        addAirport(airport);

                    } else {
                        throw new MyException("Broj argumenata nije validan.");
                    }
                } catch (MyException | NumberFormatException e) {
                    cntExc++;
                }
            }

            // Obradjujemo letove
            while ((line = br.readLine()) != null) {
                line = line.trim();

                try {

                    String[] str = line.split(",");

                    if (str.length == 4) {

                        if (!str[0].matches("[A-Z]{3}")) {
                            throw new MyException("Start aerodrom nije validan.");
                        }
                        if (!str[1].matches("[A-Z]{3}")) {
                            throw new MyException("End aerodrom nije validan.");
                        }
                        if (str[2].isBlank()) {
                            throw new MyException("Vreme polaska nije validno.");
                        }

                        LocalTime departure = LocalTime.parse(str[2], formatter);

                        Airport startAirport = byCodeAirport.get(str[0]);
                        if (startAirport == null) {
                            throw new MyException("Aerodrom " + str[0] + " ne postoji.");
                        }

                        Airport endAirport = byCodeAirport.get(str[1]);
                        if (endAirport == null) {
                            throw new MyException("Aerodrom " + str[1] + " ne postoji.");
                        }

                        Flight flight = new Flight(startAirport, endAirport, departure, Integer.parseInt(str[3]));
                        addFlight(flight);

                    } else {
                        throw new MyException("Broj argumenata nije validan.");
                    }

                } catch (MyException | NumberFormatException | DateTimeParseException e) {
                    cntExc++;
                }
            }
        }
    }

    // TODO: odradi ukoliko JSON fajl nema ocekivanu strukturu
    // ukoliko je npr. napisano airport umesto airports
    // u trenutnom kodu ovo vraca NullPointerException
    private void loadJSON(File file) throws IOException {

        /* Gson
         * JsonParser.parseReader(reader) -> vraca JsonElement
         * .getAsJsonObject() -> vraca objekat
         * object.getAsJsonArray("airports") -> izvlaci niz po kljucu
         * */

        try (FileReader reader = new FileReader(file)) {

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // prolazimo za aerodrome
            JsonArray airports = root.getAsJsonArray("airports");
            for (JsonElement el : airports) {
                JsonObject obj = el.getAsJsonObject();

                try {

                    Airport airport = new Airport(obj.get("name").getAsString(), obj.get("code").getAsString(),
                            obj.get("x").getAsInt(), obj.get("y").getAsInt());
                    addAirport(airport);

                } catch (MyException e) {
                    cntExc++;
                }
            }

            // prolazimo za letove
            JsonArray flights = root.getAsJsonArray("flights");

            for (JsonElement el : flights) {
                JsonObject obj = el.getAsJsonObject();

                try {

                    String startCode = obj.get("from").getAsString();
                    String endCode = obj.get("to").getAsString();
                    String departure = obj.get("departure").getAsString();
                    int duration = obj.get("duration").getAsInt();

                    if (departure.isBlank()) {
                        throw new MyException("Vreme polaska nije validno.");
                    }

                    LocalTime departureTime = LocalTime.parse(departure, formatter);

                    Airport startAirport = byCodeAirport.get(startCode);
                    if (startAirport == null) {
                        throw new MyException("Aerodrom " + startCode + " ne postoji.");
                    }

                    Airport endAirport = byCodeAirport.get(endCode);
                    if (endAirport == null) {
                        throw new MyException("Aerodrom " + endCode + " ne postoji.");
                    }

                    Flight flight = new Flight(startAirport, endAirport, departureTime, duration);
                    addFlight(flight);

                } catch (MyException | DateTimeParseException e) {
                    cntExc++;
                }
            }
        }

    }

    public void checkExceptions() throws MyException {
        if (cntExc > 0) {
            int tmp = cntExc;
            cntExc = 0;
            throw new MyException(tmp + " redova nije dodato, zbog nevalidnosti.");
        }
    }

    // Funkcija koja brise sve aerodrome iz modela
    public void refreshAirports() {
        airportList.clear();
        byCodeAirport.clear();
        byXYCoord.clear();
        cntExc = 0;
    }

    // Funkcija koja brise sve letove iz modela
    public void refreshFlights() {
        flightList.clear();
    }
}