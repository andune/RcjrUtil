package com.rcjrrjcr.bukkitplugins.util.economyinterface;

import org.bukkit.entity.Player;

import com.nijikokun.register.payment.Method.MethodAccount;
import com.nijikokun.register.payment.methods.EE17;
import com.rcjrrjcr.bukkitplugins.util.RcjrPlugin;

public class EconEssentials implements IEconHandler {

	private RcjrPlugin origin;
	/*
	 * Essentials is phasing out support for it's "native" economy. So I don't expect this util class
	 * to be around forever.  But rather than try to update it to support Essential 2.2.22+, I'll just
	 * leverage @nijikokun's Register plug in to get past compile errors (and it will probably work
	 * too, but I haven't tested).  -morganm 5/28/2011
	 */
	private EE17 registerEE17;
	
	public EconEssentials(RcjrPlugin origin) {
		this.origin = origin;
		registerEE17 = new EE17();
	}

	@Override
	public double getBalance(Player player) {
		return registerEE17.getAccount(player.getName()).balance();
	}

	@Override
	public boolean deduct(Player player, Integer cost) {
		MethodAccount a = registerEE17.getAccount(player.getName());
		if(!a.hasEnough(cost)) return false;
		a.subtract(cost);
		return true;		
	}

	@Override
	public double getBalance(String playerName) {
		return getBalance(origin.getServer().getPlayer(playerName));
	}

	@Override
	public boolean deduct(String playerName, Integer cost) {
		return deduct(origin.getServer().getPlayer(playerName),cost);
	}

	@Override
	public void add(Player player, Integer cost) {
		registerEE17.getAccount(player.getName()).add(cost);
	}

	@Override
	public void add(String playerName, Integer cost) {
		add(origin.getServer().getPlayer(playerName),cost);
	}

}
