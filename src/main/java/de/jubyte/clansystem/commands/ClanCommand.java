package de.jubyte.clansystem.commands;

import de.jubyte.clansystem.ClanSystem;
import de.jubyte.clansystem.storage.ClanRoleEnum;
import de.jubyte.clansystem.storage.clan.ClanEntry;
import de.jubyte.clansystem.storage.claninvite.ClanInvitePlayer;
import de.jubyte.clansystem.storage.clanmember.ClanMemberPlayer;
import de.jubyte.clansystem.utils.ScoreBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ClanCommand implements CommandExecutor {

    private final String prefix = "§8[§6Clan§8] ";
    private final Map<Player, ClanInvitePlayer> clanInviteMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 3) {
            if(commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if(strings[0].equalsIgnoreCase("create")) {
                    String clanName = strings[1];
                    ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
                    if(!clanMemberPlayer.playerHasClan()) {
                        if (!ClanEntry.clanNameExists(clanName)) {
                            String clanTag = strings[2];
                            if (!ClanEntry.clanTagExists(clanTag)) {
                                if (strings[2].length() <= 5) {
                                    ClanEntry.createClan(clanName, clanTag);
                                    player.sendMessage(prefix + "§7Du hast den Clan §e" + clanName + " §7mit dem Clantag §e" + clanTag + " §aerstellt§7.");
                                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(ClanSystem.getPLUGIN(), () -> {
                                        ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByName(clanName);
                                        clanMemberPlayer.setRole(ClanRoleEnum.CREATOR.getiD());
                                        clanMemberPlayer.setClanID(clanEntry.getClanID());
                                        ScoreBoardManager.playerSetClanTag(player);
                                    }, 20);
                                }
                            } else {
                                player.sendMessage(prefix + "§7Der §eClanname §7existiert bereits.");
                            }
                        } else {
                            player.sendMessage(prefix + "§7Der §eClantag §7existiert bereits.");
                        }
                    } else {
                        player.sendMessage(prefix + "§7Du bist bereits in einem §eClan§7.");
                    }
                } else {
                    sendHelpMessage(player);
                }
            }
        } else if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("tinfo")) {
                String clanTag = strings[1];
                if(ClanEntry.clanTagExists(clanTag)) {
                    ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByTag(clanTag);
                    commandSender.sendMessage("§7=====§8[§6Clan§8-§6Informations§8]§7=====");
                    commandSender.sendMessage(" §7ClanName: §e" + clanEntry.getClanName() +"\n" +
                            " §7ClanTag: §e" + clanEntry.getClanTag() +"\n" +
                            " §7" + ClanRoleEnum.CREATOR.getName() + "§7: §e" + ClanMemberPlayer.getCreatorNameByClanID(clanEntry.getClanID()));
                    StringBuilder members = new StringBuilder();
                    boolean first = true;
                    for(String playerName : ClanMemberPlayer.getClanMembersNameByClanID(clanEntry.getClanID())) {
                        if(!first) {
                            members.append("§8, §e");
                        }
                        members.append(playerName);
                        first = false;
                    }
                    commandSender.sendMessage(" §7" + ClanRoleEnum.MEMBER.getName() + "§7: §e" + members);
                    commandSender.sendMessage("§7=====§8[§6Clan§8-§6Informations§8]§7=====");
                }
            } else if(strings[0].equalsIgnoreCase("invite")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
                    if (clanMemberPlayer.playerHasClan()) {
                        Player targetPlayer = Bukkit.getPlayer(strings[1]);
                        if (targetPlayer != null) {
                            ClanMemberPlayer clanMemberTargetPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(targetPlayer.getUniqueId());
                            if (!clanMemberTargetPlayer.playerHasClan()) {
                                ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByID(clanMemberPlayer.getClanID());
                                clanInviteMap.put(targetPlayer, new ClanInvitePlayer(clanEntry.getClanName(), player));
                                player.sendMessage(prefix + "§7Du hast §e" + targetPlayer.getName() + " §7in dein Clan §aeingeladen§7.");
                                targetPlayer.sendMessage(prefix + "§e" + player.getName() + " §7hat dich in den Clan §e" + clanEntry.getClanName() + " §aeingalden§7." +
                                        " §7Mit §e/clan accept " + clanEntry.getClanName() + " §7trittst du dem Clan bei.");
                                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(ClanSystem.getPLUGIN(), () -> {
                                    if (clanInviteMap.containsKey(targetPlayer)) {
                                        clanInviteMap.remove(targetPlayer, new ClanInvitePlayer(clanEntry.getClanName(), player));
                                        if (targetPlayer.isOnline()) {
                                            targetPlayer.sendMessage(prefix + "§7Die Clan Anfrage vom §e" + clanEntry.getClanName() + " §7Clan ist §cabgelaufen§7.");
                                        }
                                    }
                                }, (20 * 60) * 5);
                            } else {
                                player.sendMessage(prefix + "§7Der Spieler §e" + targetPlayer.getName() + " §7ist bereits in einem §eClan§7.");
                            }
                        } else {
                            player.sendMessage(prefix + "§7Der Spieler §e" + strings[1] + " §7ist §coffline§7.");
                        }
                    }
                }
            } else if(strings[0].equalsIgnoreCase("accept")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if(clanInviteMap.containsKey(player)) {
                        ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
                        if(!clanMemberPlayer.playerHasClan()) {
                            for(Map.Entry<Player, ClanInvitePlayer> entry : clanInviteMap.entrySet()) {
                                if(entry.getKey() == player) {
                                    if(entry.getValue().getClanName().equalsIgnoreCase(strings[1])) {
                                        if(ClanEntry.clanNameExists(strings[1])) {
                                            ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByName(entry.getValue().getClanName());
                                            clanMemberPlayer.setRole(ClanRoleEnum.MEMBER.getiD());
                                            clanMemberPlayer.setClanID(clanEntry.getClanID());
                                            clanInviteMap.remove(player);
                                            player.sendMessage(prefix + "§7Du bist dem Clan §e" + clanEntry.getClanName() + " §abeigetreten§7.");
                                            Player inviter = entry.getValue().getInviter();
                                            if (inviter != null) {
                                                inviter.sendMessage(prefix + "§e" + player.getName() + " §7ist dem Clan §abeigetreten§7.");
                                            }
                                            ScoreBoardManager.playerSetClanTag(player);
                                        } else {
                                            player.sendMessage(prefix + "§7Der Clan §e" + strings[1] + " §7existiert §cnicht§7.");
                                        }
                                    } else {
                                        player.sendMessage(prefix + "§7Du hast §ckeine §eClaneinladung §7vom §e" + strings[1] + " §7Clan.");
                                    }
                                }
                            }
                        } else {
                            player.sendMessage(prefix + "§7Du bist bereits in einem §eClan§7.");
                        }
                    } else {
                        player.sendMessage(prefix + "§7Du hast §ckeine §eClaneinladung§7.");
                    }
                }
            } else {
                sendHelpMessage(commandSender);
            }
        } else if(strings.length == 1) {
            if(strings[0].equalsIgnoreCase("delete")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
                    if(clanMemberPlayer.playerHasClan()) {
                        int clanID = clanMemberPlayer.getClanID();
                        ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByID(clanMemberPlayer.getClanID());
                        if (clanMemberPlayer.getRole() == ClanRoleEnum.CREATOR.getiD()) {
                            String claName;
                            clanMemberPlayer.deleteClanPlayer(player);
                            player.sendMessage(prefix + "§7Du hast den Clan §e" + clanEntry.getClanName() + " §cgelöscht§7.");
                            claName = clanEntry.getClanName();
                            for (String playerName : ClanMemberPlayer.getClanMembersNameByClanID(clanEntry.getClanID())) {
                                Player clanPlayer = Bukkit.getPlayer(playerName);
                                if (clanPlayer != null) {
                                    clanPlayer.sendMessage(prefix + "§7Der Clan §e" + claName + " §7wurde §cgelöscht§7.");
                                    clanMemberPlayer.deleteClanPlayer(clanPlayer);
                                    ScoreBoardManager.playerSetClanTag(clanPlayer);
                                }
                            }
                            clanEntry.deleteClan();
                            ClanMemberPlayer.deleteClanPlayers(clanID);
                            ScoreBoardManager.playerSetClanTag(player);
                        } else {
                            player.sendMessage(prefix + "§7Der §eClan §7gehört §cnicht §7dir.");
                        }
                    } else {
                        player.sendMessage(prefix + "§7Du bist in §ckeinem §eClan§7.");
                    }
                }
            } else if(strings[0].equalsIgnoreCase("leave")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
                    if (clanMemberPlayer.playerHasClan()) {
                        if (clanMemberPlayer.getRole() == ClanRoleEnum.MEMBER.getiD()) {
                            ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByID(clanMemberPlayer.getClanID());
                            player.sendMessage(prefix + "§7Du hast den Clan §e" + clanEntry.getClanName() + " §cverlassen§7.");
                            clanMemberPlayer.deleteClanPlayer(player);
                            ScoreBoardManager.playerSetClanTag(player);
                        } else {
                            player.sendMessage(prefix + "§7Um den Clan zu verlassen, gib §e/clan delete §7ein.");
                        }
                    } else {
                        player.sendMessage(prefix + "§7Du bist in §ckeinem §7Clan.");
                    }
                }
            } else if(strings[0].equalsIgnoreCase("info")) {
                if(commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    ClanMemberPlayer clanMemberPlayer = ClanSystem.getPLUGIN().getClanMemberCache().getClanMemberByUUID(player.getUniqueId());
                    if(clanMemberPlayer.playerHasClan()) {
                        ClanEntry clanEntry = ClanSystem.getPLUGIN().getClanCache().getClanByID(clanMemberPlayer.getClanID());
                        commandSender.sendMessage("§7=====§8[§6Clan§8-§6Informations§8]§7=====");
                        commandSender.sendMessage(" §7ClanName: §e" + clanEntry.getClanName() + "\n" +
                                " §7ClanTag: §e" + clanEntry.getClanTag() + "\n" +
                                " §7" + ClanRoleEnum.CREATOR.getName() + "§7: §e" + ClanMemberPlayer.getCreatorNameByClanID(clanEntry.getClanID()));
                        StringBuilder members = new StringBuilder();
                        boolean first = true;
                        for (String playerName : ClanMemberPlayer.getClanMembersNameByClanID(clanEntry.getClanID())) {
                            if (!first) {
                                members.append("§8, §e");
                            }
                            members.append(playerName);
                            first = false;
                        }
                        commandSender.sendMessage(" §7" + ClanRoleEnum.MEMBER.getName() + "§7: §e" + members);
                        commandSender.sendMessage("§7=====§8[§6Clan§8-§6Informations§8]§7=====");
                    } else {
                        player.sendMessage(prefix + "§7Du bist in §ckeinem §eClan§7.");
                    }
                }
            } else {
                sendHelpMessage(commandSender);
            }
        } else {
            sendHelpMessage(commandSender);
        }
        return false;
    }

    public void sendHelpMessage(CommandSender commandSender) {
        commandSender.sendMessage(prefix + "§f=====§8[§6Clan§8-§6Help§8]§7===== ");
        commandSender.sendMessage(" §e/clan create <Name> <Tag> §8- §7Erstelle dein eigenen Clan\n" +
                " §e/clan tinfo <Tag> §8- §7Hole dir Claninformationen über den Clantag\n" +
                " §e/clan delete §8- §7Lösche dein eigenen Clan\n" +
                " §e/clan invite <Spieler> §8- §7Lade Spieler in deinen Clan ein\n" +
                " §e/clan accept <Clanname> §8- §7Akzeptiere eine Claneinladung\n" +
                " §e/clan leave §8- §7Verlasse deinen Clan");
        commandSender.sendMessage(prefix + "§f=====§8[§6Clan§8-§6Help§8]§7===== ");
    }
}
