package com.example.larisa.liketimisoara;

/**
 * Attraction type.
 */

public enum AttractionType {

    HOTEL("Hoteluri", "Hotels"),
    PARC("Parcuri", "Parks"),
    PIATA("Piețe", "Squares"),
    MUZEU("Muzee", "Museums"),
    CAFENEA("Cafenele și baruri", "Cafes and bars"),
    PENSIUNE("Pensiuni", "Guest houses"),
    HOSTEL("Hosteluri", "Hostels"),
    CLUB("Cluburi și pub-uri", "Clubs and pubs"),
    RESTAURANT("Restaurante", "Restaurants"),
    CATEDRALA("Biserici și catedrale", "Churches and cathedrals"),
    BAZIN_INOT( "Bazine de înot", "Swimming pools"),
    SALA_FITNESS("Săli de fitness", "Fitness centers"),
    TEREN_TENIS("Terenuri de tenis", "Tennis courts"),
    SALA_SQUASH("Squash", "Squash centers"),
    COMPANIE_TAXI("Taxi", "Taxi"),
    INCHIRIERI_AUTO("Închiriere mașini", "Rent a car"),
    STATIE_BICICLETE("Stații bicilete Velo TM", "Velo TM bike stations"),
    ALTE_OBIECTIVE("Alte", "Others"),
    FESTIVAL("Festival", "Festival");

    private String roName;
    private String enName;

    AttractionType(String roName, String enName) {
        this.roName = roName;
        this.enName = enName;
    }

    public String getRoName() {
        return roName;
    }

    public String getEnName() {
        return enName;
    }
}
