package de.jubyte.clansystem;

import de.jubyte.clansystem.commands.ClanCommand;
import de.jubyte.clansystem.listener.PlayerJoinListener;
import de.jubyte.clansystem.storage.Storage;
import de.jubyte.clansystem.storage.clan.ClanCache;
import de.jubyte.clansystem.storage.clanmember.ClanMemberCache;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClanSystem extends JavaPlugin {

    private static ClanSystem PLUGIN;

    private Storage storage;

    private ClanCache clanCache;
    private ClanMemberCache clanMemberCache;

    @Override
    public void onEnable() {
        PLUGIN = this;

        this.storage = new Storage();
        storage.createConnection();

        clanCache = new ClanCache();
        clanMemberCache = new ClanMemberCache();

        loadCommands();
        loadListener();

        sendMessage("Enabled");
    }

    @Override
    public void onDisable() {
        if(storage.isConnected())
            storage.deleteConnection();

        sendMessage("Disabled");
    }

    public void loadCommands() {
        getCommand("clan").setExecutor(new ClanCommand());
    }

    public void loadListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
    }

    private void sendMessage(String status) {
        Bukkit.getConsoleSender().sendMessage("§7================================================");
        Bukkit.getConsoleSender().sendMessage("§6ClanSystem §7| §eStatus: " + status);
        Bukkit.getConsoleSender().sendMessage("§6ClanSystem §7| §eVersion: §5" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§6ClanSystem §7| §eAuthors: §6" + getDescription().getAuthors());
        Bukkit.getConsoleSender().sendMessage("§7================================================");
    }

    public static ClanSystem getPLUGIN() {
        return PLUGIN;
    }

    public Storage getStorage() {
        return storage;
    }

    public ClanCache getClanCache() {
        return clanCache;
    }

    public ClanMemberCache getClanMemberCache() {
        return clanMemberCache;
    }

}
