package de.jubyte.clansystem.listener;

import de.jubyte.clansystem.ClanSystem;
import de.jubyte.clansystem.storage.clanmember.ClanMemberPlayer;
import de.jubyte.clansystem.utils.ScoreBoardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
        if(!clanMemberPlayer.getName().equalsIgnoreCase(player.getName()))
            clanMemberPlayer.setName(player.getName());
        ScoreBoardManager.playerSetClanTag(player);
    }

}
