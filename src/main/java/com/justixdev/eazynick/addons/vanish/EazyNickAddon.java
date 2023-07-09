package com.justixdev.eazynick.addons.vanish;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.events.PlayerNickEvent;
import com.justixdev.eazynick.utilities.ActionBarUtils;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class EazyNickAddon extends JavaPlugin implements Listener {

    private EazyNick eazyNick;
    private SetupConfiguration setupConfiguration;

    @Override
    public void onEnable() {
        this.eazyNick = EazyNick.getInstance();
        this.setupConfiguration = new SetupConfiguration(getName());

        Bukkit.getPluginManager().registerEvents(this, this);

        new AsyncTask(new AsyncTask.AsyncRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(currentPlayer -> currentPlayer.getMetadata("vanished").stream().anyMatch(MetadataValue::asBoolean))
                        .filter(currentPlayer -> !eazyNick.getUtils().getNickedPlayers().containsKey(currentPlayer.getUniqueId()))
                        .forEach(currentPlayer -> eazyNick.getActionBarUtils().sendActionBar(
                                currentPlayer,
                                setupConfiguration.getConfigString("Messages.Vanished")
                        ));
            }
        }, 0L, 1000L).run();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
                        && !utils.getWorldsWithDisabledActionBar().contains(player.getWorld().getName().toUpperCase())) {
                    actionBarUtils.sendActionBar(
                            player,
                            player.getMetadata("vanished").stream().anyMatch(MetadataValue::asBoolean)
                                    ? setupConfiguration.getConfigString("Messages.NickedAndVanished")
                                    : setupConfiguration.getConfigString("Messages.Nicked")
                    );
                } else {
                    if (player.isOnline())
                        actionBarUtils.sendActionBar(player, "");

                    cancel();
                }
            }
        }, 0L, 1000L).run();
    }

}
