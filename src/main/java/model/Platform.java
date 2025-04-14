package model;

public enum Platform {
    APP("APP"),
    DWEB("DWEB"),
    API("API"),
    PANEL("PANEL");

    private String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
