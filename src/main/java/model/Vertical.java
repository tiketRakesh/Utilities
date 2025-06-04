package model;

public enum Vertical {
    ACCOMMODATION("ACCOMMODATION"),
    B2B_AFFILIATE("B2B_AFFILIATE"),
    B2B_CORPORATE("B2B_CORPORATE"),
    TTD("TTD"),
    FLIGHT("FLIGHT"),
    PLATFORM("PLATFORM"),
    NFT("NFT"),
    POR("POR"),
    PRICING("PRICING"),
    PROMO("PROMO"),
    GROWTH("GROWTH");

    private String name;

    Vertical(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
