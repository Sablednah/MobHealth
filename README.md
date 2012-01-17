Mob Health
==========

This is just a simple plugin to let you know the damage you just caused to a mob (including other players), and how much health it has left.

Now supports Spout! (Optional)

### Configuration

Configuration now has three settings.

	usePermissions: false
If true then the permission node MobHealth.show is needed to enable for a player.

	disableSpout: false
Force messages to display in chat even if spout is detected.

	enableEasterEggs: false
Turns on 'extra chat features'.  (A Basic profanity filter - and message when people mention 11/eleven.)


### Commands

Only one command at present.

	/MobHealth reload
Reloads current configuration.
	
### Permissions

	MobHealth.show
If usePermissions is true then only players with this node with see damage messages.
	
	MobHealth.commands
Only players with this can use the reload command.
	
	MobHealth.* 
Wildcard for all ModHealth permissions.

### Changelog
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