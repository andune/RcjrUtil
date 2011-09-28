package com.rcjrrjcr.bukkitplugins.util.permissionsinterface;

import java.util.LinkedHashSet;

import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.User;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.rcjrrjcr.bukkitplugins.util.RcjrPlugin;


public class Perm3xHandler implements IPermHandler {
	private Permissions plugin;
	private RcjrPlugin origin;
	private PermissionHandler permHandle;
	private LinkedHashSet<PermissionData> permAddCache;
	private LinkedHashSet<PermissionData> permRemCache;
	
	/* permTempAddCache keeps track of all temporary adds we do, so that if permissions are
	 * reloaded, we can safely re-apply said permissions.
	 */
	private LinkedHashSet<PermissionData> permTempAddCache;
	/* permTempAddCache keeps track of all temporary removes we do, so that if permissions are
	 * reloaded, we can safely re-apply said permissions.
	 */
	private LinkedHashSet<PermissionData> permTempRemCache;
	private boolean useTempPerms;
	
	public Perm3xHandler(Plugin plugin,RcjrPlugin origin)
	{
		setPlugin(plugin);
		this.origin = origin;
		permAddCache = new LinkedHashSet<PermissionData>();
		permRemCache = new LinkedHashSet<PermissionData>();
		permTempAddCache = new LinkedHashSet<PermissionData>();
		permTempRemCache = new LinkedHashSet<PermissionData>();
	}
	@Override
	public boolean hasPerm(String world,String playerName, String perm) {
		if(origin.active==null||!(origin.active.isPermActive()))
		{
			System.out.println("Permissions plugin inactive!");
			return false;
		}
		if(origin.getServer().getPlayer(playerName)==null)
		{
//			System.out.println("Player not online!");
			return false;
		}
		return permHandle.has(origin.getServer().getPlayer(playerName), perm);
	}

	@Override
	public void setPerm(String world, String playerName, String perm, boolean hasPerm) {
		if(hasPerm)
		{
			addPerm(world,playerName,perm);
		}
		else
		{
			removePerm(world,playerName,perm);
		}
	}

	@Override
	public void addPerm(String world,String playerName, String perm) {
		PermissionData pData = new PermissionData();
		pData.setWorld(world);
		pData.setPlayerName(playerName);
		pData.setNode(perm);
		
		// if tempPerms is enabled, then all permissions are set using the 3.x Transient permissions
		// system.  Otherwise, we invoke addUserPermissions() to actually permanently modify the user
		// permissions.
		if( useTempPerms ) {
			User user = permHandle.getUserObject(world, playerName);
			user.addTransientPermission(perm);
			permTempAddCache.add(pData);
		}
		else {
			permHandle.addUserPermission(world, playerName.toLowerCase(), perm);
			permAddCache.add(pData);
		}

	}

	@Override
	public void removePerm(String world,String playerName, String perm) {
		PermissionData pData = new PermissionData();
		pData.setWorld(world);
		pData.setPlayerName(playerName);
		pData.setNode(perm);
		
		// if tempPerms is enabled, then all permissions are set using the 3.x Transient permissions
		// system.  Otherwise, we invoke addUserPermissions() to actually permanently modify the user
		// permissions.
		if( useTempPerms ) {
			User user = permHandle.getUserObject(world, playerName);
			user.removeTransientPermission(perm);
			permTempRemCache.add(pData);
		}
		else {
			permHandle.removeUserPermission(world, playerName.toLowerCase(), perm);
			permRemCache.add(pData);
		}
	}

	@Override
	public boolean isInGroup(String world,String playerName, String group) {
		if(origin.active==null||!(origin.active.isPermActive())) return false;
		return permHandle.inGroup(world, playerName, group);
	}


	@Override
	public void setPlugin(Plugin plugin) {
		if(plugin instanceof Permissions)
		{
			this.plugin=(Permissions) plugin;
			this.permHandle = this.plugin.getHandler();
		}
		return;		
	}

	@Override
	public String[] listGroups(String world, String playerName) {
		if(origin.active==null||!(origin.active.isPermActive())) return null;
		return permHandle.getGroups(world,playerName);
	}
	
	/** This method is invoked by the permission Factory when it detects a new
	 * Permissions object being requested and it already has an old one.
	 * 
	 * As far as I can tell, the original intent here is to fire on Permission
	 * reload events and make sure the new permissions get applied.  However, the
	 * original code also flushed the cache after doing so, seemingly to me meaning
	 * that a future reload won't get the same cache.
	 * 
	 * I've re-purposed this method for Permissions 3.x, hopefully adhering to the
	 * original interface contract of applying the cache, but I don't remove the
	 * items from the cache.  This guarantees they should get passed on to the next
	 * handler during a reload and the same Permissions get applied again, which
	 * is important for Permissions 3.x Transient permissions. 
	 * 
	 * @author morganm
	 */
	@Override
	public void flushCache()
	{
		if(origin.active==null||!(origin.active.isPermActive())) return;
		
		for(PermissionData pAdd : permAddCache)
		{
			addPerm(pAdd.getWorld(),pAdd.getPlayerName(),pAdd.getNode());
		}

		for(PermissionData pAdd : permRemCache)
		{
			removePerm(pAdd.getWorld(),pAdd.getPlayerName(),pAdd.getNode());
		}
		
		for(PermissionData pAdd : permTempAddCache)
		{
			addTemporaryPerm(pAdd.getWorld(),pAdd.getPlayerName(),pAdd.getNode());
		}

		for(PermissionData pAdd : permTempRemCache)
		{
			removeTemporaryPerm(pAdd.getWorld(),pAdd.getPlayerName(),pAdd.getNode());
		}
	}
	
	@Override
	public LinkedHashSet<PermissionData> getAddCache() {
		return permAddCache;
	}
	@Override
	public LinkedHashSet<PermissionData> getRemCache() {
		return permRemCache;
	}
	@Override
	public void setCache(LinkedHashSet<PermissionData> addCache,
			LinkedHashSet<PermissionData> remCache) {
		permAddCache = addCache;
		permRemCache = remCache;
	}
	
	@Override
	public void addTemporaryPerm(String world, String playerName, String perm) {
		User user = permHandle.getUserObject(world, playerName);
		user.addTransientPermission(perm);
		
		PermissionData pData = new PermissionData();
		pData.setWorld(world);
		pData.setPlayerName(playerName);
		pData.setNode(perm);
		permTempAddCache.add(pData);
	}
	@Override
	public void removeTemporaryPerm(String world, String playerName, String perm) {
		User user = permHandle.getUserObject(world, playerName);
		user.removeTransientPermission(perm);
		
		PermissionData pData = new PermissionData();
		pData.setWorld(world);
		pData.setPlayerName(playerName);
		pData.setNode(perm);
		permTempRemCache.add(pData);
	}
	
}
