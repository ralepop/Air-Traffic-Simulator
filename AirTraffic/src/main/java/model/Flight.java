package model;

import exceptions.MyException;

import java.time.LocalTime;

/*
* Klasa koja sadrzi informacije o jednom letu.
* */

public class Flight {

    private Airport startAirport;
    private Airport endAirport;
    private LocalTime departureTime;
    private int flightDuration;

    public Flight(Airport startAirport, Airport endAirport, LocalTime departureTime, int flightDuration) throws MyException {

        if (flightDuration <= 0 || departureTime == null) {
            throw new MyException("Trajanje leta ne moze biti nepozitivno.");
        }

        this.startAirport = startAirport;
        this.endAirport = endAirport;
        this.departureTime = departureTime;
        this.flightDuration = flightDuration;
    }

    private LocalTime calculateETA() {
        return departureTime.plusMinutes(flightDuration);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(startAirport.getCode()).append(" ").append(departureTime).append(" -> ")
                .append(endAirport.getCode()).append(" ").append(calculateETA());
        return sb.toString();
    }

    public Airport getStartAirport() {
        return startAirport;
    }

    public Airport getEndAirport() {
        return endAirport;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public int getFlightDuration() {
        return flightDuration;
    }

    public LocalTime getArrivalTime() {
        return calculateETA();
    }
}
