# Changelog: #

## 1.0.1 ##

* Fix an issue with absolute paths used as a backup path
* Various other small bug fixes

## 1.0.0 ##

* ForgeBackup is now a **coremod** and must be placed in your coremods folder.
* Different compression types have been implemented.
  * Firstly, `tar.gz` and `tar.bz2`. `tar.gz` is the new default on non-Windows systems.
  * There is a new git-based system. Please don't enable this if you do not have git installed on your pc, as you will not be able to restore backups older than the most recent one if you do. This will be resolved in a future version.
* There is now an update reminder facility. This is disabled by default.

## 0.4.1 ##

* ForgeEssentials compatibility. We provide a module to FE which overrides their built-in backup module. This means you'll only have one active backup module at any time.

## 0.4.0 ##

* Many bug fixes, especially around notifying players of things.
* Archival backups. These are long term backups which have their own folder and can optionally backup more than the regular backups do. These backups are for longer term storage if needed and are run daily, or on startup if one hasn't been run today yet, and a certain number of daily and weekly backups can be kept. Weekly backups are backups that were taken on Sunday.


<!--
vim: filetype=markdown
-->
