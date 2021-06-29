package de.jubyte.clansystem.storage.clanmember;

import de.jubyte.clansystem.ClanSystem;
import de.jubyte.clansystem.storage.ClanRoleEnum;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClanMemberPlayer {

    private UUID uuid;
    private String name;
    private int clanID;
    private int role;

    public ClanMemberPlayer(UUID uuid, String name, int clanID, int role) {
        this.uuid = uuid;
        this.name = name;
        this.clanID = clanID;
        this.role = role;
    }

    public int getClanID() {
        return clanID;
    }

    public String getName() {
        return name;
    }

    public int getRole() {
        return role;
    }

    public static boolean playerExists(UUID uuid) {
        return !ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().find().where("UUID", uuid).execute().isEmpty();
    }

    public static void createClanMember(UUID uuid, String name) {
        if(!playerExists(uuid)) {
            ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().insert()
                    .set("UUID", uuid)
                    .set("Name", name)
                    .set("ClanID", 0)
                    .set("Role", 0)
                    .execute();
        }
    }

    public void setClanID(int clanID) {
        this.clanID = clanID;
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().update()
                .set("ClanID", clanID)
                .where("UUID", uuid)
                .execute();
    }

    public void setRole(int role) {
        this.role = role;
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().update()
                .set("Role", role)
                .where("UUID", uuid)
                .execute();
    }

    public boolean playerHasClan() {
        return getClanID() != 0;
    }

    public static void deleteClanPlayers(int iD) {
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().update()
                .set("Role", 0)
                .where("ClanID", iD)
                .where("Role", ClanRoleEnum.MEMBER.getiD())
                .execute();
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().update()
                .set("ClanID", 0)
                .where("ClanID", iD)
                .execute();
    }

    public void deleteClanPlayer(Player player) {
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().update()
                .set("Role", 0)
                .where("UUID", player.getUniqueId())
                .execute();
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().update()
                .set("ClanID", 0)
                .where("UUID", player.getUniqueId())
                .execute();
        this.role = 0;
        this.clanID = 0;
    }

    public static List<String> getClanMembersNameByClanID(int clanID) {
        ArrayList<String> arrayList = new ArrayList<>();
        for(QueryResultEntry clanMembers : ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().find().where("ClanID", clanID).where("Role", ClanRoleEnum.MEMBER.getiD()).execute().asList()) {
            arrayList.add(clanMembers.getString("Name"));
        }
        return arrayList;
    }

    public static String getCreatorNameByClanID(int clanID) {
        QueryResult result = ClanSystem.getPLUGIN().getStorage().getClanMembersCollection()
                .find()
                .where("ClanID", clanID)
                .where("Role", ClanRoleEnum.CREATOR.getiD())
                .limit(1)
                .execute();
        if(!result.isEmpty()) {
            return result.first().getString("Name");
        }
        return null;
    }

    public void setName(String name) {
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().update()
                .where("Name", this.name)
                .set(name)
                .execute();
        this.name = name;
    }
}
