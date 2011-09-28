package com.rcjrrjcr.bukkitplugins.util.permissionsinterface;

import org.bukkit.plugin.Plugin;

import com.rcjrrjcr.bukkitplugins.util.RcjrPlugin;


public final class PermFactory
{
	private static IPermHandler pHandler;
	public final static IPermHandler getInstance(PermPlugin pluginType,Plugin plugin,RcjrPlugin origin) throws Exception
	{
		IPermHandler newHandler;
		
		if (pluginType == PermPlugin.Perm3x)
		{
			newHandler = new Perm3xHandler(plugin,origin);
		}
		else if (pluginType == PermPlugin.PermYeti)
		{
			newHandler = new PermYetiHandler(plugin,origin);
		}
		else if (pluginType == PermPlugin.GroupManager)
		{
			newHandler = new GroupManagerHandler(plugin,origin);
		}
		else if (pluginType == PermPlugin.CACHE)
		{
			newHandler = new PermCacheOnly(plugin,origin);
		}
		else
		{
			throw new Exception("Permission Interface was unable to be created!");
		}
		
		if(pHandler != null)
		{
			newHandler.setCache(pHandler.getAddCache(), pHandler.getRemCache());
			newHandler.flushCache();
		}
		
		pHandler = newHandler;
		return newHandler;
	}
}
