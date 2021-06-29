package de.jubyte.clansystem.storage;

public enum ClanRoleEnum {

    CREATOR("Ersteller", 1),
    MEMBER("Member", 2);

    private final String name;
    private final int iD;

    ClanRoleEnum(String name, int iD) {
        this.name = name;
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public int getiD() {
        return iD;
    }
}
