package de.jubyte.clansystem.storage.clanmember;

import de.jubyte.clansystem.ClanSystem;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClanMemberCache {

    private final Cache<ClanMemberPlayer> clanMemberPlayer;


    public ClanMemberCache() {
        clanMemberPlayer = new ArrayCache<ClanMemberPlayer>()
                .setExpireAfterAccess(30, TimeUnit.MINUTES)
                .setMaxSize(250)
                .registerQuery("byUUID", new CacheQuery<ClanMemberPlayer>() {
                    @Override
                    public ClanMemberPlayer load(Object[] identifiers) {
                        UUID uuid = (UUID) identifiers[0];
                        DatabaseCollection collection = ClanSystem.getPLUGIN().getStorage().getClanMembersCollection();
                        QueryResultEntry resultEntry = collection.find().where("UUID", uuid).execute().firstOrNull();

                        if (resultEntry == null) {
                            Player player = Bukkit.getPlayer(uuid);
                            if(player != null) {
                                insertClanMember(uuid, player.getName());
                                return new ClanMemberPlayer(uuid, player.getName(), 0, 0);
                            } else {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                insertClanMember(uuid, offlinePlayer.getName());
                                return new ClanMemberPlayer(uuid, offlinePlayer.getName(), 0, 0);
                            }
                        }
                        return new ClanMemberPlayer(uuid, resultEntry.getString("Name"), resultEntry.getInt("ClanID"), resultEntry.getInt("Role"));
                    }

                    @Override
                    public boolean check(ClanMemberPlayer o, Object[] objects) {
                        return false;
                    }
                });
    }
    private void insertClanMember(UUID uuid, String name) {
        ClanSystem.getPLUGIN().getStorage().getClanMembersCollection().insert()
                .set("UUID", uuid)
                .set("Name", name)
                .set("ClanID", 0)
                .set("Role", 0)
                .execute();
    }

    public ClanMemberPlayer getClanMemberByUUID(UUID uuid) {
        return clanMemberPlayer.get("byUUID", uuid);
    }
}