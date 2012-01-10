Mob Health
==========

This is just a simple plugin to let you know the damage you just caused to a mob, and how much health it has left.

Now supports Spout! (Optional)

### Configuration

Configuration now has three settings.

	usePermissions: false
If true then the permission node MobHealth.show is needed to enable for a player.

	disableSpout: false
Force messages to display in chat even if spout is detected.

	disableSpout: false
Force messages to display in chat even if spout is detected.

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
1.5:  Fixed:  Displays Player name instead of 'unknOwn' when attacking other players.

1.4:  Disabled bonus chat features. Added config option to re-enable them.

1.3:  Added config option to disable spout notification.

1.2:  Added Permissions to optionally control who sees damage notifications.

1.1:  Added Spout notification.

1.0:  First release.