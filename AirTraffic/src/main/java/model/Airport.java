package model;

/*
* Klasa koja sadrzi informacije o jednom aerodromu.
* */

import exceptions.MyException;

public class Airport {

    private final String name;
    private final String code;
    private final int coordX;
    private final int coordY;

    public Airport(String name, String code, int coordX, int coordY) throws MyException {
        if (name == null || name.isBlank()) {
            throw new MyException("Naziv aerodroma ne moze biti prazan.");
        } else if (code == null || !code.matches("[A-Z]{3}")) {
            throw new MyException("Jedinstveni kod aerodroma nije validan.");
        } else if (coordX > 180 || coordX < -180 || coordY > 90 || coordY < -90) {
            throw new MyException("Opseg vrednosti za x osu je [-180, 180], a za y osu [-90, 90]");
        }

        this.name = name;
        this.code = code;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ").append(code).append(" ").append("(")
                .append(coordX).append(", ").append("y").append(")");
        return sb.toString();
    }

}
