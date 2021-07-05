package de.jubyte.clansystem.storage.clan;

import de.jubyte.clansystem.ClanSystem;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;

import java.util.concurrent.TimeUnit;

public class ClanCache {

    private final Cache<ClanEntry> clanEntryCache;


    public ClanCache() {
        clanEntryCache = new ArrayCache<ClanEntry>()
                .setExpireAfterAccess(30, TimeUnit.MINUTES)
                .setMaxSize(250)
                .registerQuery("byID", new CacheQuery<ClanEntry>() {
                    @Override
                    public ClanEntry load(Object[] identifiers) {
                        int iD = (int) identifiers[0];
                        DatabaseCollection collection = ClanSystem.getPLUGIN().getStorage().getClanCollection();
                        QueryResultEntry resultEntry = collection.find().where("ClanID", iD).execute().first();

                        if(resultEntry == null) {
                            return null;
                        }
                        return new ClanEntry(iD, resultEntry.getString("ClanName"), resultEntry.getString("ClanTag"));
                    }
                    @Override
                    public boolean check(ClanEntry clanEntry, Object[] objects) {
                        return clanEntry.getClanID() == (int) objects[0];
                    }
                })
                .registerQuery("byTag", new CacheQuery<ClanEntry>() {
                    @Override
                    public ClanEntry load(Object[] identifiers) {
                        String clanTag = (String) identifiers[0];
                        DatabaseCollection collection = ClanSystem.getPLUGIN().getStorage().getClanCollection();
                        QueryResultEntry resultEntry = collection.find().where("ClanTag", clanTag).execute().first();

                        if(resultEntry == null) return null;
                        return new ClanEntry(resultEntry.getInt("ClanID"), resultEntry.getString("ClanName"), clanTag);
                    }
                    @Override
                    public boolean check(ClanEntry clanEntry, Object[] objects) {
                        return clanEntry.getClanTag().equalsIgnoreCase((String) objects[0]) && clanEntry.getClanID() > 0;
                    }
                })
                .registerQuery("byName", new CacheQuery<ClanEntry>() {
                    @Override
                    public ClanEntry load(Object[] identifiers) {
                        String clanName = (String) identifiers[0];
                        DatabaseCollection collection = ClanSystem.getPLUGIN().getStorage().getClanCollection();
                        QueryResultEntry resultEntry = collection.find().where("ClanName", clanName).execute().first();
                        if(resultEntry == null) {
                            return null;
                        }
                        System.out.println("ClanID: " + resultEntry.getInt("ClanID"));
                        System.out.println("Clanname: " + clanName);
                        System.out.println("ClanTag: " + resultEntry.getString("ClanTag"));
                        return new ClanEntry(resultEntry.getInt("ClanID"), clanName, resultEntry.getString("ClanTag"));
                    }
                    @Override
                    public boolean check(ClanEntry clanEntry, Object[] objects) {
                        System.out.println("Name: " + clanEntry.getClanName());
                        System.out.println("Object: " + objects[0]);
                        System.out.println(clanEntry.getClanName().equals(objects[0]));
                        return clanEntry.getClanName().equalsIgnoreCase((String) objects[0]) && clanEntry.getClanID() > 0;
                    }
                });
    }

    public ClanEntry getClanByName(String clanName) {
        return clanEntryCache.get("byName", clanName);
    }

    public ClanEntry getClanByTag(String clanTag) {
        return clanEntryCache.get("byTag", clanTag);
    }

    public ClanEntry getClanByID(int clanID) {
        return clanEntryCache.get("byID", clanID);
    }
}
