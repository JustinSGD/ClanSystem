package de.jubyte.clansystem.storage.clan;

import de.jubyte.clansystem.ClanSystem;

public class ClanEntry {

    private int clanID;
    private String clanName;
    private String clanTag;

    public ClanEntry(int clanID, String clanName, String clanTag) {
        this.clanID = clanID;
        this.clanName = clanName;
        this.clanTag = clanTag;
    }

    public String getClanName() {
        return clanName;
    }

    public String getClanTag() {
        return clanTag;
    }

    public int getClanID() {
        return clanID;
    }

    public static boolean clanNameExists(String clanName) {
        return !ClanSystem.getPLUGIN().getStorage().getClanCollection().find().where("ClanName", clanName).execute().isEmpty();
    }

    public static boolean clanTagExists(String clanTag) {
        return !ClanSystem.getPLUGIN().getStorage().getClanCollection().find().where("ClanTag", clanTag).execute().isEmpty();
    }

    public static void createClan(String clanName, String clanTag) {
        ClanSystem.getPLUGIN().getStorage().getClanCollection().insert()
                .set("ClanName", clanName)
                .set("ClanTag", clanTag)
                .executeAsync();
    }

    public void setClanName(String oldName, String newName) {
        this.clanName = clanName;
        ClanSystem.getPLUGIN().getStorage().getClanCollection().update()
                .where("ClanName", oldName)
                .set("ClanName", newName)
                .executeAsync();
    }

    public void setClanTag(String oldName, String newName) {
        this.clanTag = clanTag;
        ClanSystem.getPLUGIN().getStorage().getClanCollection().update()
                .where("ClanTag", oldName)
                .set("ClanTag", newName)
                .executeAsync();
    }

    public void deleteClan() {
        ClanSystem.getPLUGIN().getStorage().getClanCollection().delete()
                .where("ClanID", clanID)
                .execute();
        this.clanID = 0;
    }
}
