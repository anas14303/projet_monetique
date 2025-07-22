package com.monetique.paiement_appsb.model;

public enum TypeCarte {
    VISA_ELECTRON("Visa Electron"),
    VISA_CLASSIC("Visa Classic"),
    VISA_GOLD("Visa Gold"),
    VISA_PLATINUM("Visa Platinum"),
    VISA_INFINITE("Visa Infinite"),
    MASTERCARD_STANDARD("Mastercard Standard"),
    MASTERCARD_GOLD("Mastercard Gold"),
    MASTERCARD_PLATINUM("Mastercard Platinum"),
    MASTERCARD_WORLD_ELITE("Mastercard World Elite"),
    MAESTRO("Maestro"),
    AMERICAN_EXPRESS("American Express"),
    DISCOVER("Discover"),
    JCB("JCB"),
    UNIONPAY("UnionPay");

    private final String libelle;

    TypeCarte(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
