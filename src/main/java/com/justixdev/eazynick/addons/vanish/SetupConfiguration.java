package com.justixdev.eazynick.addons.vanish;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SetupConfiguration {

    @Getter
    private File file;
    @Getter
    private YamlConfiguration configuration;

    public SetupConfiguration(String directoryName) {
        // create files
        File directory = new File(directoryName);

        if(!directory.exists()) {
            if(!directory.mkdir())
                return;
        }

        this.file = new File(directory, "setup.yml");

        if(!file.exists()) {
            try {
                if(!file.createNewFile())
                    return;
            } catch (IOException ignore) {
                return;
            }
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        // default values

        this.configuration.addDefault("Messages.Nicked", "&fYou are currently &cNICKED");
        this.configuration.addDefault("Messages.Vanished", "&fYou are currently &cVANISHED");
        this.configuration.addDefault("Messages.NickedAndVanished", "&fYou are currently &cNICKED&f, &cVANISHED");
        this.configuration.options().copyDefaults(true);

        try {
            this.configuration.save(this.file);
        } catch (IOException ignore) {
        }
    }

    public String getConfigString(String path) {
        return ChatColor.translateAlternateColorCodes(
                '&',
                Objects.requireNonNull(this.configuration.getString(path))
        );
    }

}
