package model;

public enum Environment {
    GK("GK"),
    PREPROD("PREPROD"),
    PROD("PROD");

    private String name;

    Environment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
