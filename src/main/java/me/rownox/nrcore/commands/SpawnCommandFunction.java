package me.rownox.nrcore.commands;

import me.rownox.nrcore.NRCore;
import me.rownox.nrcore.utils.ConfigUtils;
import me.rownox.nrcore.utils.command.CommandCore;
import me.rownox.nrcore.utils.command.CommandFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import static me.rownox.nrcore.utils.PlayerUtils.checkPerms;
import static me.rownox.nrcore.utils.PlayerUtils.healPlr;

public class SpawnCommandFunction extends CommandCore implements CommandFunction {
    private final String spawnType;

    public SpawnCommandFunction(String type) {
        super(type+"spawn", null, type+"spawn");
        this.spawnType = type;
        setDefaultFunction(this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        final NRCore ROWCORE = NRCore.getInstance();
        final FileConfiguration CONFIG = NRCore.getInstance().config;

        if (args.length >= 1) {
            if (!checkPerms(sender, "spawn", ".others")) return;
            Player target = Bukkit.getPlayer(args[0]);
            target.teleport(ConfigUtils.spawn);
            target.playSound(target.getLocation(), Sound.valueOf(CONFIG.getString("teleport.sound")), 1, 1);

        }

        if (sender instanceof Player p) {

            if (spawnType.equals("set")) {

                if (!checkPerms(p,"spawn.set")) return;

                Location loc = p.getLocation();
                ConfigUtils.setSpawn(loc);

            } else {

                p.sendMessage(ChatColor.DARK_AQUA + "You'll be teleported to spawn in " + ChatColor.AQUA + CONFIG.getString("teleport.delay") + " Seconds" + ChatColor.DARK_AQUA + " Do not move.");
                p.setMetadata("goingToSpawn", new FixedMetadataValue(ROWCORE, true));
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if (p.hasMetadata("goingToSpawn")){
                            p.removeMetadata("goingToSpawn", ROWCORE);

                            p.teleport(ConfigUtils.spawn);
                            p.sendMessage(ChatColor.DARK_AQUA + "Teleportation complete.");

                            p.playSound(p.getLocation(), Sound.valueOf(CONFIG.getString("teleport.sound")), 1, 1);
                        }
                    }
                }.runTaskLater(ROWCORE, 5 * 20);
            }
        }
    }
}
