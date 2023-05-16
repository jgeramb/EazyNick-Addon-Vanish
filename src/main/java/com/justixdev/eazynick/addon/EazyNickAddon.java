package com.justixdev.eazynick.addon;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.events.PlayerNickEvent;
import com.justixdev.eazynick.utilities.ActionBarUtils;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class EazyNickAddon extends JavaPlugin implements Listener {

    private EazyNick eazyNick;

    @Override
    public void onEnable() {
        this.eazyNick = EazyNick.getInstance();

        Bukkit.getPluginManager().registerEvents(this, this);

        new AsyncTask(new AsyncTask.AsyncRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(currentPlayer -> currentPlayer.getMetadata("vanished").stream().anyMatch(MetadataValue::asBoolean))
                        .filter(currentPlayer -> !eazyNick.getUtils().getNickedPlayers().containsKey(currentPlayer.getUniqueId()))
                        .forEach(currentPlayer -> eazyNick.getActionBarUtils().sendActionBar(currentPlayer, "§fYou are currently §cVANISHED"));
            }
        }, 1000L, 1000L).run();
    }

    @EventHandler
    public void onPlayerNick(PlayerNickEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        ActionBarUtils actionBarUtils = eazyNick.getActionBarUtils();
        Player player = event.getPlayer();

        new AsyncTask(new AsyncTask.AsyncRunnable() {
            @Override
            public void run() {
                if (eazyNick.isEnabled()
                        && utils.getNickedPlayers().containsKey(player.getUniqueId())
                        && player.isOnline()
                        && !utils.getWorldsWithDisabledActionBar().contains(player.getWorld().getName().toUpperCase()))
                    actionBarUtils.sendActionBar(player, "§fYou are currently §cNICKED§f" + (player.getMetadata("vanished").stream().anyMatch(MetadataValue::asBoolean) ? ", §cVANISHED" : ""));
                else {
                    if (player != null)
                        actionBarUtils.sendActionBar(player, "");

                    cancel();
                }
            }
        }, 1000L, 1000L).run();
    }

}
