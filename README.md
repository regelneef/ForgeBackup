# ForgeBackup v0.4.1 #

ForgeBackup is a simple mod that sits and backs up your server or single player worlds as you play. No more corrupted worlds due to issues with mods or other issues with your computer, just restore a backup and most of your work will still be saved even if disaster should strike. The initial concept was based heavily on ideas gleaned from BukkitBackup ported to the Minecraft Forge API, however it has now taken on a life of it's own and implements many features not found in the original plugin.

## Installation: ##

Just download MinecraftForge and the appropriate version of this mod for your version of Minecraft from below and place this mod in your mods folder.

## Downloads: ##

### 1.4.6/1.4.7 ###

* [forgebackup-universal-1.4.7-0.4.1.37.jar][b37]

## Features: ##

* Perform automatic backups of SMP and SSP worlds.
* Perform manual backups using the /backup command. This command can be used by command blocks if you allow it in the config.
* Basic permissions support to allow anyone or only ops to run backups manually.
* ForgeBackup is only required to be downloaded on the server or SMP worlds. The same download is used for both the client (SSP) and the server.
* Ability to store backups anywhere via the configuration file.
* Automatically clean up old backups (disabled by default).
* Disable certain dimensions from being backed up. Don't backup large dimensions that you don't need or want to.
* Ability to totally configure what gets backed up. Backup mods, configuration and your world, or any other files or folders you may wish to.
* Archival backups. These are long term backups which have their own folder and can optionally backup more than the regular backups do. These backups are for longer term storage if needed and are run daily, or on startup if one hasn't been run today yet, and a certain number of daily and weekly backups can be kept. Weekly backups are backups that were taken on Sunday.

## Compatibility: ##

ForgeBackup has a very low surface area that actually touches Minecraft. This means it should be highly compatible with just about any mod out there that isn't also trying to do backups. However, there has been some need for specific patches and so below is a list of other mods we've specifically made changes to accomodate or that we know don't work with Forgebackup.

* ForgeEssentials: ForgeBackup is fully compatible. It overrides and hides the default Backups module from FE if it exists on the server. While ForgeBackup is compatible, there is currently no integration, so you won't be able to control ForgeBackup via ForgeEssentials. This compatibility requires atleast betabuild #59.

## Bug Reports: ##

Always happy to receive them, but I can't help you if you don't help me. If you
can't provide a log, then I probably can't help you. Please provide the console
output as well as the ForgeModLoader.log (typically either ForgeModLoader-client-0.log
or ForgeModLoader-server-0.log) from your minecraft folder. Please
[post issues on GitHub][gh-issues] if you are able. I'm also usually available on
[EsperNet IRC][irc] as monoxide.

## Permissions: ##

[ForgeBackup is an open source mod][gh]. It is released under [the MIT license][license].

## Changelog: ##

### 0.4.1 ###

* ForgeEssentials compatibility. We provide a module to FE which overrides their built-in backup module. This means you'll only have one active backup module at any time.

### 0.4.0 ###

* Many bug fixes, especially around notifying players of things.
* Archival backups. These are long term backups which have their own folder and can optionally backup more than the regular backups do. These backups are for longer term storage if needed and are run daily, or on startup if one hasn't been run today yet, and a certain number of daily and weekly backups can be kept. Weekly backups are backups that were taken on Sunday.

## Old Versions: ##

I'm not sure of the need for this section since this mod is only required on one side, either the server or the client. Old versions are here for those who want them though.

### 1.4.6/1.4.7 ###

* [forgebackup-universal-1.4.7-0.4.1.37.jar][b37]
* [forgebackup-universal-1.4.7-0.3.1.21.jar][b21]


  [b21]: http://bit.ly/12XW7gy
  [b37]: http://bit.ly/10VgQxJ

  [gh]: https://github.com/monoxide0184/ForgeBackup
  [gh-issues]: https://github.com/monoxide0184/ForgeBackup/issues
  [license]: https://github.com/monoxide0184/ForgeBackup/blob/master/LICENSE.md
  [irc]: http://esper.net/publicirc.php

