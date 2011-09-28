package com.rcjrrjcr.bukkitplugins.util.permissionsinterface;

import java.util.LinkedHashSet;

import org.bukkit.plugin.Plugin;

import com.rcjrrjcr.bukkitplugins.util.RcjrPlugin;

public class PermCacheOnly implements IPermHandler {

	private LinkedHashSet<PermissionData> permRemCache;
	private LinkedHashSet<PermissionData> permAddCache;
	
	public PermCacheOnly(Plugin plugin,RcjrPlugin origin)
	{
		permAddCache = new LinkedHashSet<PermissionData>();
		permRemCache = new LinkedHashSet<PermissionData>();
	}
	@Override
	public void setPlugin(Plugin plugin) {

	}

	@Override
	public boolean hasPerm(String world, String playerName, String perm) {
		return false;
	}

	@Override
	public void addPerm(String world, String playerName, String perm) {
		PermissionData pData = new PermissionData();
		pData.setWorld(world);
		pData.setPlayerName(playerName);
		pData.setNode(perm);
		permAddCache.add(pData);
		return;
	}

	@Override
	public void removePerm(String world, String playerName, String perm) {
			PermissionData pData = new PermissionData();
			pData.setWorld(world);
			pData.setPlayerName(playerName);
			pData.setNode(perm);
			permRemCache.add(pData);
			return;
	}

	@Override
	public void setPerm(String world, String playerName, String perm,
			boolean hasPerm) {
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
	public boolean isInGroup(String world, String playerName, String group) {
		return false;
	}

	@Override
	public String[] listGroups(String world, String playerName) {
		return null;
	}

	@Override
	public void flushCache() {
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

	/* Temp permissions for Cache Only just use the real thing.
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
