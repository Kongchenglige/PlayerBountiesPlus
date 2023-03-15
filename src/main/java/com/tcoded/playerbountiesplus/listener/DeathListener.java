package com.tcoded.playerbountiesplus.listener;

import com.tcoded.playerbountiesplus.PlayerBountiesPlus;
import com.tcoded.playerbountiesplus.hook.clan.AbstractClanHook;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public class DeathListener implements Listener {

    private PlayerBountiesPlus plugin;

    public DeathListener(PlayerBountiesPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            HashMap<UUID, Integer> bounties = this.plugin.getBounties();
            UUID victimId = victim.getUniqueId();
            Integer bounty = bounties.get(victimId);

            // Bounty check
            if (bounty == null || bounty == 0) {
                System.out.println("debug: no bounty");
                return;
            }

            // Clan check
            AbstractClanHook clanHook = this.plugin.getClanHook();
            String clanIdVictim = clanHook.getClanId(victim);
            if (clanIdVictim != null && clanIdVictim.equals(clanHook.getClanId(killer))) {
                killer.sendMessage(ChatColor.GREEN + "The player you killed has an active bounty but you are in the same clan as they are!");
                return;
            }

            // Apply rewards!
            this.plugin.getVaultHook().addMoney(killer, bounty);
            this.plugin.getServer().broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD +
                    String.format("%s claimed the bounty that was placed on %s worth %s", killer.getName(), victim.getName(), bounty));

            // Remove bounty
            bounties.remove(victimId);
            this.plugin.saveBounties();
        }
    }

}