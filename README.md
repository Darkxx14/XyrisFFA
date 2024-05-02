# FFA

## Overview

FFA is a next-generation plugin designed for PvP/FFA servers, offering a robust API and a bunch of features.

## Requirements
To use the FFA plugin, you need to use Minecraft version 1.19 or higher, Java version 17 or above, Spigot or any of its forks, and You need PlaceholderAPI to use this plugin.

#  Features
* Arenas System, Includes cool arenas for ranked users.

* Next Gen Combat Log System
* Customizable Death Messages
* PlaceholderAPI Support
* Guilds (TODO)
* Arena Regeneration (TODO)
* Multi Worlds (TODO)
* Advanced Kit System
* Kit layouts (TODO)
* Next Generation Settings Menu
* Spawn/Lobby Items
* Advanced Stats System Powered by SQLITE and MYSQL (TODO)
* WorldGuard Integration
* Health Indicators
* Quick Respawn
* LaunchPads, And More
____
### Necessary Commands
- Broadcast
- Fly
- Heal
- Message
- Reply
- Nickname
- Ping
- Rules
- Settings, Yes there are even more which are not listed here.
___

Everything is editable via the `config.yml` file.

## Placeholders
- %ffa_kills% | Returns the kills of a player.
- %ffa_deaths% | Returns the deaths of a player.
- %ffa_kdr% | Returns the KDR of a player.
- %ffa_streak% | Returns the kill streak of a player.
- %ffa_maxstreak% | Returns the max kill streak of a player.
- %ffa_combat_timer% | Returns the combat timer of a player.
- %ffa_nickname% | Returns the nickname of a player.

### Utility Placeholders
- %ffa_player_<RegionName>% | Returns the player count of the specified WorldGuard region.
- %ffa_isbusy_<RegionName>% | Checks if the specified WorldGuard region has 8+ players; if yes, return "[Busy]".
- %ffa_settings_olddamagetilt% | Returns the OldDamageTilt setting status of a player.
- %ffa_settings_privatemessages% | Returns the PrivateMessages setting status of a player.
- %ffa_settings_autogg% | Returns the AutoGG setting status of a player.
- %ffa_settings_mentionsound% | Returns the MentionSound setting status of a player.
- %ffa_settings_quickrespawn% | Returns the QuickRespawn setting status of a player.

## Reporting Issues
Please feel free to report any glitches or bugs via our [Discord server](https://discord.gg/XBmrFsg5eR).

## Download
To get the FFA plugin, Purchase it for a small fee of **$0.99** from our [Discord server](https://discord.gg/XBmrFsg5eR). Alternatively, compile it yourself using the provided guide below.

## Compiling
To compile the FFA plugin, follow these steps:
1. Use [BuildTools by SpigotMC](https://www.spigotmc.org/wiki/buildtools/) to build CraftBukkit version 1.19.4, which is supported by FFA plugin.
2. When running BuildTools, it will automatically add the necessary dependencies (CraftBukkit/Bukkit) to your local Maven repository.
3. Compile the project using the command `mvn clean package`. The output plugin JAR file will be located in the `target/` directory.

Note: Java 17 is required to build FFA.

### License
The FFA plugin is licensed under [GNU General Public License](https://github.com/Darkxx14/FFA?tab=GPL-3.0-1-ov-file) (GPL).
