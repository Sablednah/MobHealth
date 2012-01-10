Mob Health
==========

This is just a simple plugin to let you know the damage you just caused to a mob, and how much health it has left.

Now supports Spout! (Optional)

### Configuration

Configuration has just two settings at the moment

	`usePermissions: false`
	If true then the permission node MobHealth.show is needed to enable for a player.

	`disableSpout: false`
	Force messages to display in chat even if spout is detected.

### Commands

Only one command at present.

	`/MobHealth reload`
	Reloads current configuration.
	
### Permissions

	`MobHealth.show`
	If usePermissions is true then only players with this node with see damage messages.
	
	`MobHealth.commands`
	Only players with this can use the reload command.
	
	`MobHealth.*` 
	Wildcard for all ModHealth permissions.
	