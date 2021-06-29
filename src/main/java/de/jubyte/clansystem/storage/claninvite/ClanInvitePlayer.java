package de.jubyte.clansystem.storage.claninvite;

import org.bukkit.entity.Player;

public class ClanInvitePlayer {

    private String clanName;
    private Player inviter;

    public ClanInvitePlayer(String clanName, Player inviter) {
        this.clanName = clanName;
        this.inviter = inviter;
    }

    public Player getInviter() {
        return inviter;
    }

    public String getClanName() {
        return clanName;
    }
}
