package de.jubyte.clansystem.utils;

import de.jubyte.clansystem.ClanSystem;
import de.jubyte.clansystem.storage.clan.ClanEntry;
import de.jubyte.clansystem.storage.clanmember.ClanMemberPlayer;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ScoreBoardManager {

    private static ScoreBoardManager instance;
    private final Scoreboard scoreboard;
    private final HashMap<UUID, String> teams;

    public ScoreBoardManager() {
        scoreboard = new Scoreboard();
        teams = new HashMap<>();
    }

    public void registerTeam(Player player, String prefix, String suffix, String displayName, EnumChatFormat chatColor, int level) {
        String team = level + player.getUniqueId().toString().substring(1, 8);
        if(scoreboard.getTeam(team) != null) {
            scoreboard.removeTeam(scoreboard.getTeam(team));
        }
        ScoreboardTeam scoreboardTeam = scoreboard.createTeam(team);

        scoreboardTeam.setPrefix(new ChatComponentText(prefix));
        scoreboardTeam.setSuffix(new ChatComponentText(suffix));
        scoreboardTeam.setDisplayName(new ChatComponentText(displayName));
        scoreboardTeam.setColor(chatColor);

        teams.put(player.getUniqueId(), team);
        update();
    }

    public void update() {
        for(Player players : Bukkit.getOnlinePlayers()) {
            if(!scoreboard.getTeam(teams.get(players.getUniqueId())).getPlayerNameSet().contains(players.getName())) {
                scoreboard.getTeam(teams.get(players.getUniqueId())).getPlayerNameSet().add(players.getName());
            }
            sendPacket(new PacketPlayOutScoreboardTeam(scoreboard.getTeam(teams.get(players.getUniqueId())), 1));
            sendPacket(new PacketPlayOutScoreboardTeam(scoreboard.getTeam(teams.get(players.getUniqueId())), 0));
        }
    }

    public static void playerSetClanTag(Player player) {
        ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
        if(clanMemberPlayer.playerHasClan()) {
            ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByID(clanMemberPlayer.getClanID());
            getInstance().registerTeam(player, "§aTester ┃ ", " §8[§e" + clanEntry.getClanTag() + "§8]", "§aTester ┃ ", EnumChatFormat.GREEN, 0);
        } else {
            getInstance().registerTeam(player, "§aTester ┃ ", "", "§aTester ┃ §a", EnumChatFormat.GREEN, 0);
        }
    }

    private void sendPacket(Packet<?> packet) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            CraftPlayer player = (CraftPlayer) players;
            player.getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static ScoreBoardManager getInstance() {
        if(instance == null) {
            instance = new ScoreBoardManager();
        }
        return instance;
    }
}
