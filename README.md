Mob Health
==========

This is just a simple plugin to let you know the damage you just caused to a mob (including other players), and how much health it has left.  

Now supports Spout! (_Optional_)

Mob Health now has plenty of options to control how messages are shown,when they are shown (and more importantly when they are *not* shown), and what is displayed - with your own custom messages.


### Configuration

	usePermissions: false
If true then the permission node MobHealth.show is needed to enable for a player.

	disableSpout: false
Force messages to display in chat even if spout is detected.

	enableEasterEggs: false
Turns on 'extra chat features'.  (A Basic profanity filter - and message when people mention trigger keywords.)

    disablePlayers: false
Disable notifications for player hits.

    disableMonsters: false
Disable notifications for 'monster' hits.

    disableAnimals: false
Disable notifications for 'animal' hits.

    damageDisplayType: [1|2|3|4]
1. display damage inflicted.  
2. display damage taken.
2. display damage inflicted (-amount resisted)
4. display damage taken (+amount resisted)

    hideNoDammage: false
Hide notifications that inflict 0 damage.  Custom Egg and Snowball notifications are exempt.


### Commands

	/MobHealth reload
Reloads current configuration.
	
	/MobHealth toggle [player]
Toggle notifications. [player] is ignored via chat and mandatory via the console.


### Permissions

	MobHealth.* 
Wildcard for all ModHealth permissions.

	MobHealth.show
If usePermissions is true then only players with this node with see damage messages.
	
	MobHealth.command.*
Give player all MobHealth commands (and all future commands).
MobHealth.commands also works for this to allow legacy compatibility.

	MobHealth.command.toggle
Give player MobHealth toggle command.

	MobHealth.command.reload
Give player MobHealth reload command.


### Changelog
3.1:  Initial MobArena support.

3.0:  Permissions rewrite.

2.5:  New Bukkit event format.
      Support for Likeaboss Boss entities.  

2.4:  "eleven" message trigger changed to trigger list in lang.yml.  You can now have your own silly phrase trigger whenever you want!
      Implemented flags to hide damage notifications if target is player, animal or monster.
      Implemented various options for displaying damage in notifications.
      Option to hide notifications that inflict '0' real damage.
      
2.3:  /MobHealth toggle  option added.  Toggles display of notifications per player. 

2.2:  Custom messages for Egg and Snowball hits.
      Just add these lines to lang.yml
      chatMessageEgg: 
      chatMessageSnowball:
      spoutEggTitle:
      spoutEggMessage:
      spoutSnowballTitle:
      spoutSnowballMessage:
 
2.1:  Chat colour fixes.

2.0:  Finished config file.  Mob names now configurable in lang.yml.

1.9:  As requested, text for notification/messages is now stored in a config file - lang.yml

1.8:  Changed spout notification icon to bow for projectile hits.

1.7:  Version Checker.

1.6:  Now works for Projectiles!

1.5:  Fixed:  Displays Player name instead of 'unknOwn' when attacking other players.

1.4:  Disabled bonus chat features. Added config option to re-enable them.

1.3:  Added config option to disable spout notification.

1.2:  Added Permissions to optionally control who sees damage notifications.

1.1:  Added Spout notification.

1.0:  First release.


To Do
=====
* <del>Detect change in health to  get **actual** damage dealt.</del>
* <del>Add config to allow damage display type.</del>
    * <del>Damage inflicted - e.g. 6 damage.</del>
    * <del>Damage taken - e.g. 4 damage.</del>
    * <del>Damage taken + damage absorbed - e.g. 4(+2) damage.</del>
* <del>Config option to ignore notifications for 0 damage</del>
* <del>Exception for eggs and/or snowballs for above option</del>
* Heroes detection/integration
* Spells damage detection.


Known Bugs/Conflicts
====================
* Ender Dragon damage not notified. Bukkit have pushed a fix through for 1.1 - working on it now.
* Plugins that alter mobs health levels usually aren't detected by my plugin.  So far I know the following cause issues.
    * <del>Likeaboss - I have details form the author and I'm working on adding support.</del> Done Thanks bm01
    * Heroes.  Once Heroes for 1.1 is out I'll look over the API.
    * <del>MobArena.  Again once a 1.1 build is out I'll look over the API.</del>  Initial Suport in place.
* Spells.  Bukkit have added a damage reason for spell damage now.  Once plugins start using it I will look at reporting spell damage.
