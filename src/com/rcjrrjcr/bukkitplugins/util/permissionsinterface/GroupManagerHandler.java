package com.rcjrrjcr.bukkitplugins.util.permissionsinterface;

import java.util.LinkedHashSet;
import java.util.logging.Logger;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.rcjrrjcr.bukkitplugins.util.RcjrPlugin;



public class GroupManagerHandler implements IPermHandler {
	private static final Logger log = Logger.getLogger(GroupManagerHandler.class.toString());

	private GroupManager plugin;
	private WorldsHolder dataHolder;
	private RcjrPlugin origin;
	
	/** Not entirely sure how these caches are used.  They are changed when permissions are added or
	 * removed, and appear to be setup to be preserved across Permission system reloads (in PermFactory).
	 * But I can't see that they're actually referenced or used anywhere, so maybe this is a half-coded
	 * feature and not actually used at this time?   -morganm 5/28/11
	 * 
	 * Update: My guess is the intent here is to make sure people don't accidentally get "stuck" with
	 * permissions they shouldn't have or losing access to ones they should (such as rented abilities).
	 * The caches appear to be setup to protect against an admin making file changes behind-the-scenes
	 * and then reloading permissions. I believe these caches would replay the BAB add/remove permission
	 * events in flushCache() to be sure everything was kosher after a reload. -morganm
	 */
	private LinkedHashSet<PermissionData> permAddCache;
	private LinkedHashSet<PermissionData> permRemCache;

	public GroupManagerHandler(Plugin plugin, RcjrPlugin origin)
	{
		setPlugin(plugin);
		this.origin = origin;
		permAddCache = new LinkedHashSet<PermissionData>();
		permRemCache = new LinkedHashSet<PermissionData>();
	}
	@Override
	public void setPlugin(Plugin plugin) {
		if(plugin instanceof GroupManager)
		{
			this.plugin = (GroupManager) plugin;
			dataHolder = this.plugin.getWorldsHolder();
		}
		return;		
	}


	@Override
	public boolean hasPerm(String world, String playerName, String perm) {
		if(origin.active==null||!(origin.active.isPermActive()))
		{
			System.out.println("BuyAbilities: Plugin inactive!");
			return false;
		}
//		log.fine("DEBUG: GroupManageHandler, world = "+world+", playerName = "+playerName+", perm = "+perm);
		
		boolean ret = false;
		Player p = origin.getServer().getPlayer(playerName);
		if( p == null ) {
			log.warning("hasPerm(): player object for "+playerName+" is null, couldn't check permisions");
		}
		else
			ret = dataHolder.getWorldPermissions(world).has(p, perm);
		
		return ret;
	}


	@Override
	public void addPerm(String world, String playerName, String perm)
	{	
		if(origin.active==null||!(origin.active.isPermActive()))
		{
			PermissionData pData = new PermissionData();
			pData.setWorld(world);
			pData.setPlayerName(playerName);
			pData.setNode(perm);
			permAddCache.add(pData);
			return;
		}
		dataHolder.getWorldData(world).getUser(playerName).addPermission(perm);
	}


	@Override
	public void removePerm(String world, String playerName, String perm) {
		if(origin.active==null||!(origin.active.isPermActive()))
		{
			PermissionData pData = new PermissionData();
			pData.setWorld(world);
			pData.setPlayerName(playerName);
			pData.setNode(perm);
			permRemCache.add(pData);
			return;
		}
		dataHolder.getWorldData(world).getUser(playerName).removePermission(perm);
	}


	@Override
	public void setPerm(String world, String playerName, String perm,
			boolean hasPerm)
	{
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
		if(origin.active==null||!(origin.active.isPermActive())) return false;
		return dataHolder.getWorldPermissions(world).inGroup(playerName, group);
	}


	@Override
	public String[] listGroups(String world, String playerName) {
		if(origin.active==null||!(origin.active.isPermActive())) return null;
		String[] groups;
		Group[] groupCollection = (Group[])dataHolder.getWorldData(world).getGroupList().toArray();
		Integer size = 	groupCollection.length;
		groups =  new String[size];
		int ctr;
		for(ctr=0;ctr<size;ctr++)
		{
			groups[ctr] = groupCollection[ctr].getName();
		}
		return groups;
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
	
	/* Group manager has no concept of temporary permissions, so we just use the real thing.
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
