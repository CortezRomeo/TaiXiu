![logo](https://i.imgur.com/o9Mucfz.png)

Starting March 8th, 2024, TaiXiu by Thuong Nguyen (Cortez Romeo) transitions to an open-source model under the GNU GPL 3.0 license. If you're a developer, I kindly request that you contribute via pull requests rather than creating numerous forks. Let's ensure updates are accessible to all!

# Description
Tai Xiu is a game in the form of betting. In the game, there are three 6-sided dice numbered from 1-6. The dealer (system) will take the sum of the 03 dice that appear to give a final number to the player.

The player's task is to predict whether the number is Tai or Xiu. Xiu is the sum of 3 dice from 3-10, and Tai is 11-18. If the guess is correct, the player will win and receive money.

# System requirements
This software runs on [Spigot](https://www.spigotmc.org/) and NMS.
Spigot forks without compiled NMS code are not supported.
Officially supported servers are [spigot](https://www.spigotmc.org/) and [paper](https://papermc.io/).
It is required to use [**Java 11**](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or newer.

# Plugin requirements
- [Vault](https://www.spigotmc.org/resources/vault.34315/)
- One economy plugin
-- Economy plugins: [iConomy](http://dev.bukkit.org/server-mods/iconomy) 4,5,6, [BOSEconomy](http://dev.bukkit.org/server-mods/boseconomy) 6 & 7, EssentialsEcon, 3Co, [MultiCurrency](http://dev.bukkit.org/server-mods/multicurrency), [MineConomy](http://dev.bukkit.org/server-mods/mineconomy), [eWallet](http://dev.bukkit.org/server-mods/ewallet), [EconXP](http://dev.bukkit.org/server-mods/econxp/), [CurrencyCore](http://dev.bukkit.org/server-mods/currency/), [CraftConomy](http://dev.bukkit.org/server-mods/craftconomy/), AEco, [Gringotts](http://dev.bukkit.org/server-mods/gringotts/), [BetterEconomy](https://www.spigotmc.org/resources/bettereconomy.96690/)

# Soft-depend plugins
You might need these plugins to utilize my plugin resources totally.
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
	-   **%taixiu_phien%** - Get the current session number
	-   **%taixiu_result_phien_{session_number}%** - Get the result of the session {session_number}
	-   **%taixiu_resultformat_phien_{session_number}%** - Get the format result of the session {session_number} (based on message.yml)
	-   **%taixiu_taiplayers_phien_{session_number}%** - List the players who bet on Tai
	-   **%taixiu_xiuplayers_phien_{session_number}%** - List the players who bet on Xiu
	-   **%taixiu_taiplayers_bet_phien_{session_number}%** - Get the total money of Tai
	-   **%taixiu_xiuplayers_bet_phien_{session_number}%** - Get the total money of Xiu
	-   **%taixiu_totalbet_phien_{session_number}%** - Get the total money of the session

**Attention:** Change **{session_number}** to **current** if you want to get the last session.
- [Floodgate API (GeyserMC)](https://geysermc.org/download#floodgate)
	- This will let you be able to [use Bedrock forms](https://wiki.geysermc.org/geyser/forms/) which I already have set up for this plugin.

# Main features
- Automatically updating files if there is a new update. 
- Configable messages, gui, etc..
- Supporting API.
- Supporting GUI
- Supporting Hex Color
- Supporting BossBar
- Supporting to delete old databases

# Contact

[![Discord Server](https://discord.com/api/guilds/1187827789664096267/widget.png?style=banner3)](https://discord.gg/XdJfN2X)


# 3rd party libraries
- [JetBrains Java Annotations](https://mvnrepository.com/artifact/org.jetbrains/annotations)
- [ConfigUpdater](https://github.com/tchristofferson/Config-Updater)
- [XSeries](https://github.com/CryptoMorin/XSeries)
- [NBTEditor](https://github.com/BananaPuncher714/NBTEditor)

# Special Thanks To
[<img src="https://user-images.githubusercontent.com/21148213/121807008-8ffc6700-cc52-11eb-96a7-2f6f260f8fda.png" alt="" width="150">](https://www.jetbrains.com)

Jetbrains supports TaiXiu with their [Open Source Licenses](https://www.jetbrains.com/opensource/).