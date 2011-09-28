package com.rcjrrjcr.bukkitplugins.util.permissionsinterface;

import java.util.LinkedHashSet;

import org.bukkit.plugin.Plugin;

//import com.nijiko.permissions.Control;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.rcjrrjcr.bukkitplugins.util.RcjrPlugin;


public class PermYetiHandler implements IPermHandler {
	private Permissions plugin;
	private RcjrPlugin origin;
	private PermissionHandler permHandle;
	private LinkedHashSet<PermissionData> permAddCache;
	private LinkedHashSet<PermissionData> permRemCache;
	
	public PermYetiHandler(Plugin plugin,RcjrPlugin origin)
	{
		setPlugin(plugin);
		this.origin = origin;
		permAddCache = new LinkedHashSet<PermissionData>();
		permRemCache = new LinkedHashSet<PermissionData>();
	}
	@Override
	public boolean hasPerm(String world,String playerName, String perm) {
		if(origin.active==null||!(origin.active.isPermActive()))
		{
//			System.out.println(origin.active==null);
//			System.out.println(!(origin.active.isPermActive()));
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
		if(origin.active==null||!(origin.active.isPermActive()))
		{
			PermissionData pData = new PermissionData();
			pData.setWorld(world);
			pData.setPlayerName(playerName);
			pData.setNode(perm);
			permAddCache.add(pData);
			return;
		}
		permHandle.addUserPermission(world, playerName.toLowerCase(), perm);
//		permHandle.setCacheItem(world, playerName.toLowerCase(), perm, true);
		return;
	}

	@Override
	public void removePerm(String world,String playerName, String perm) {
		if(origin.active==null||!(origin.active.isPermActive()))
		{
			PermissionData pData = new PermissionData();
			pData.setWorld(world);
			pData.setPlayerName(playerName);
			pData.setNode(perm);
			permRemCache.add(pData);
			return;
		}
		permHandle.removeUserPermission(world, playerName.toLowerCase(), perm);
//		permHandle.setCacheItem(world, playerName.toLowerCase(), perm, false);
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
	
	@Override
	public void flushCache() {
		if(origin.active==null||!(origin.active.isPermActive())) return;
		for(PermissionData pAdd : permAddCache)
		{
			addPerm(pAdd.getWorld(),pAdd.getPlayerName(),pAdd.getNode());
			permAddCache.remove(pAdd);
		}
		for(PermissionData pAdd : permRemCache)
		{
			removePerm(pAdd.getWorld(),pAdd.getPlayerName(),pAdd.getNode());
			permRemCache.remove(pAdd);
		}
		return;
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
	
	/* Permissions 2.7 has no concept of temporary permissions, so we just use the real thing.
	 * 
	 * (non-Javadoc)
	 * @see com.rcjrrjcr.bukkitplugins.util.permissionsinterface.IPermHandler#addTemporaryPerm(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addTemporaryPerm(String world, String playerName, String perm) {
		addPerm(world, playerName, perm);
	}
	@Override
	public void removeTemporaryPerm(String world, String playerName, String perm) {
		removePerm(world, playerName, perm);
	}	
}
