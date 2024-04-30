/* package dev.darkxx.ffa.guilds;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class GuildsImpl {

    private final JavaPlugin plugin;
    private final File dataFolder;

    public GuildsImpl(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "guilds");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void createGuild(Player player, String name) {
        File guildFile = new File(dataFolder, "guilds/" + name + ".yml");
        if (!guildFile.exists()) {
            try {
                guildFile.getParentFile().mkdirs();
                guildFile.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
                config.set("Name", name);
                config.set("Owner", player.getUniqueId().toString());
                config.set("Members." + player.getUniqueId(), "Leader");
                config.set("Kills", 0);
                config.save(guildFile);
                player.sendMessage("Guild '" + name + "' created successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
                player.sendMessage("Failed to create guild.");
            }
        } else {
            player.sendMessage("Guild with that name already exists!");
        }
    }

    public void invite(Player inviter, Player invitedPlayer) {
        String guildName = getGuildName(inviter);
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            String ownerUUID = config.getString("Owner");
            String inviterRole = config.getString("Members." + inviter.getUniqueId());
            if (ownerUUID != null && ownerUUID.equals(inviter.getUniqueId().toString()) ||
                    inviterRole != null && (inviterRole.equals("Leader") || inviterRole.equals("Admin"))) {
                config.set("InvitedPlayers." + invitedPlayer.getUniqueId(), true);
                try {
                    config.save(guildFile);
                    inviter.sendMessage("Invitation sent to " + invitedPlayer.getName() + ".");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    inviter.sendMessage("Failed to send invitation.");
                }
            } else {
                inviter.sendMessage("You don't have permission to invite players.");
            }
        } else {
            inviter.sendMessage("Guild not found.");
        }
    }

    public void disband(Player player) {
        String guildName = getGuildName(player);
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            String ownerUUID = config.getString("Owner");
            if (ownerUUID != null && ownerUUID.equals(player.getUniqueId().toString())) {
                guildFile.delete();
                player.sendMessage("Guild '" + guildName + "' disbanded successfully.");
            } else {
                player.sendMessage("You don't have permission to disband the guild.");
            }
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void leave(Player player) {
        String guildName = getGuildName(player);
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            String ownerUUID = config.getString("Owner");
            if (ownerUUID != null && !ownerUUID.equals(player.getUniqueId().toString())) {
                config.set("Members." + player.getUniqueId(), null);
                try {
                    config.save(guildFile);
                    player.sendMessage("You left the guild '" + guildName + "'.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    player.sendMessage("Failed to leave the guild.");
                }
            } else {
                player.sendMessage("You can't leave your own guild. Disband it instead.");
            }
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void acceptInvite(Player player, String guildName) {
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            if (config.getBoolean("InvitedPlayers." + player.getUniqueId())) {
                config.set("InvitedPlayers." + player.getUniqueId(), null);
                config.set("Members." + player.getUniqueId(), "Member");
                try {
                    config.save(guildFile);
                    player.sendMessage("You've joined the guild '" + guildName + "'.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    player.sendMessage("Failed to join the guild.");
                }
            } else {
                player.sendMessage("You haven't been invited to that guild.");
            }
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void denyInvite(Player player, String guildName) {
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            if (config.getBoolean("InvitedPlayers." + player.getUniqueId())) {
                config.set("InvitedPlayers." + player.getUniqueId(), null);
                try {
                    config.save(guildFile);
                    player.sendMessage("Invite denied.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    player.sendMessage("Failed to deny invite.");
                }
            } else {
                player.sendMessage("You haven't been invited to that guild.");
            }
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void kick(Player kicker, Player kickedPlayer) {
        String guildName = getGuildName(kickedPlayer);
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            String ownerUUID = config.getString("Owner");
            String kickerRole = config.getString("Members." + kicker.getUniqueId());
            String kickedPlayerRole = config.getString("Members." + kickedPlayer.getUniqueId());
            if (ownerUUID != null && ownerUUID.equals(kicker.getUniqueId().toString()) ||
                    kickerRole != null && (kickerRole.equals("Leader") || kickerRole.equals("Admin"))) {
                if (kickedPlayerRole != null) {
                    config.set("Members." + kickedPlayer.getUniqueId(), null);
                    try {
                        config.save(guildFile);
                        kicker.sendMessage("Player " + kickedPlayer.getName() + " has been kicked from the guild.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        kicker.sendMessage("Failed to kick player from the guild.");
                    }
                } else {
                    kicker.sendMessage("Player is not a member of the guild.");
                }
            } else {
                kicker.sendMessage("You don't have permission to kick players from this guild.");
            }
        } else {
            kicker.sendMessage("Guild not found.");
        }
    }

    public void promote(Player promoter, Player promotedPlayer, String role) {
        String guildName = getGuildName(promotedPlayer);
        if (!role.equalsIgnoreCase("Leader") && !role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Member")) {
            promoter.sendMessage("Invalid role.");
            return;
        }

        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            String ownerUUID = config.getString("Owner");
            String promoterRole = config.getString("Members." + promoter.getUniqueId());
            String promotedPlayerRole = config.getString("Members." + promotedPlayer.getUniqueId());
            if (ownerUUID != null && ownerUUID.equals(promoter.getUniqueId().toString()) ||
                    promoterRole != null && promoterRole.equals("Leader")) {
                if (promotedPlayerRole != null) {
                    config.set("Members." + promotedPlayer.getUniqueId(), role);
                    try {
                        config.save(guildFile);
                        promoter.sendMessage(promotedPlayer.getName() + " has been promoted to " + role + ".");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        promoter.sendMessage("Failed to promote player.");
                    }
                } else {
                    promoter.sendMessage("Player is not a member of the guild.");
                }
            } else {
                promoter.sendMessage("You don't have permission to promote players in this guild.");
            }
        } else {
            promoter.sendMessage("Guild not found.");
        }
    }

    public void demote(Player demoter, Player demotedPlayer, String role) {
        String guildName = getGuildName(demotedPlayer);
        if (!role.equalsIgnoreCase("Leader") && !role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Member")) {
            demoter.sendMessage("Invalid role.");
            return;
        }

        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            String ownerUUID = config.getString("Owner");
            String demoterRole = config.getString("Members." + demoter.getUniqueId());
            String demotedPlayerRole = config.getString("Members." + demotedPlayer.getUniqueId());
            if (ownerUUID != null && ownerUUID.equals(demoter.getUniqueId().toString()) ||
                    demoterRole != null && demoterRole.equals("Leader")) {
                if (demotedPlayerRole != null) {
                    config.set("Members." + demotedPlayer.getUniqueId(), role);
                    try {
                        config.save(guildFile);
                        demoter.sendMessage(demotedPlayer.getName() + " has been demoted to " + role + ".");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        demoter.sendMessage("Failed to demote player.");
                    }
                } else {
                    demoter.sendMessage("Player is not a member of the guild.");
                }
            } else {
                demoter.sendMessage("You don't have permission to demote players in this guild.");
            }
        } else {
            demoter.sendMessage("Guild not found.");
        }
    }

    public void rename(Player owner, String newName) {
        String guildName = getGuildName(owner);
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            File newGuildFile = new File(dataFolder, "guilds/" + newName + ".yml");
            if (!newGuildFile.exists()) {
                guildFile.renameTo(newGuildFile);
                owner.sendMessage("Guild renamed to '" + newName + "'.");
            } else {
                owner.sendMessage("A guild with that name already exists.");
            }
        } else {
            owner.sendMessage("Guild not found.");
        }
    }

    public void sendChatMessage(Player player, String message) {
        String guildName = getGuildName(player);
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            Set<String> memberKeys = config.getConfigurationSection("Members").getKeys(false);
            for (String memberKey : memberKeys) {
                UUID memberId = UUID.fromString(memberKey);
                Player member = plugin.getServer().getPlayer(memberId);
                if (member != null) {
                    member.sendMessage("[Guild] " + player.getName() + ": " + message);
                }
            }
        } else {
            player.sendMessage("Guild not found.");
        }
    }

    public void sendGuildInfo(Player player, String guildName) {
        File guildFile = new File(dataFolder, "guilds/" + guildName + ".yml");
        if (guildFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(guildFile);
            String ownerName = config.getString("Owner");
            Set<String> memberKeys = config.getConfigurationSection("Members").getKeys(false);
            StringBuilder info = new StringBuilder();
            info.append("Guild: ").append(guildName).append("\n");
            info.append("Owner: ").append(ownerName).append("\n");
            info.append("Members: ");
            for (String memberKey : memberKeys) {
                String memberName = plugin.getServer().getOfflinePlayer(UUID.fromString(memberKey)).getName();
                String role = config.getString("Members." + memberKey);
                info.append(memberName).append(" (").append(role).append("), ");
            }
            player.sendMessage(info.toString());
        } else {
            player.sendMessage("Guild not found.");
        }
    }
}
*/