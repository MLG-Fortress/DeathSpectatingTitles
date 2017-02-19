package to.us.tf.DeathSpectatingTitles;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import to.us.tf.DeathSpectating.events.DeathSpectatingEvent;
import to.us.tf.DeathSpectating.tasks.SpectateTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2/19/2017.
 *
 * @author RoboMWM
 */
public class DeathSpectatingTitles extends JavaPlugin implements Listener
{
    private FileConfiguration config = getConfig();
    private List<String> deathTitles = new ArrayList<>(Arrays.asList("You died!", "Game over!"));
    private List<String> deathSubTitles = new ArrayList<>(Arrays.asList("Respawning in {0}", "Score: &e{1}", "Score: &e{1}&f, Respawning in {0}"));
    private Map<Player, Integer> scores = new HashMap<>();

    public void onEnable()
    {
        config.addDefault("titles", deathTitles);
        config.addDefault("subTitles", deathSubTitles);

        config.options().copyDefaults(true);
        saveConfig();

        deathTitles = config.getStringList("titles");
        deathSubTitles = config.getStringList("subTitles");

        getServer().getPluginManager().registerEvents(this, this);
    }

    private String formatter(String stringToFormat, Object... formatees)
    {
        return formatter(MessageFormat.format(stringToFormat, formatees));
    }

    private String formatter(String stringToFormat)
    {
        return ChatColor.translateAlternateColorCodes('&', stringToFormat);
    }

    private String getRandomString(List<String> list)
    {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onDeathStoreScore(PlayerDeathEvent event)
    {
        scores.put(event.getEntity(), event.getEntity().getTotalExperience());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onQuitEvent(PlayerQuitEvent event)
    {
        //Cleanup just in case
        scores.remove(event.getPlayer());
    }

    @EventHandler
    void onSpectate(DeathSpectatingEvent event)
    {
        new BukkitRunnable()
        {
            SpectateTask spectateTask = event.getSpectateTask();
            String unformattedTitle = getRandomString(deathTitles);
            String unformattedSubTitle = getRandomString(deathSubTitles);
            int score = scores.remove(spectateTask.getPlayer());

            @Override
            public void run()
            {
                if (spectateTask.getTicks() < 2 || !spectateTask.getPlayer().hasMetadata("DEAD"))
                {
                    this.cancel();
                    return;
                }
                int seconds = (int)spectateTask.getTicks() / 20;
                String title = formatter(unformattedTitle, seconds, score);
                String subTitle = formatter(unformattedSubTitle, seconds, score);
                spectateTask.getPlayer().sendTitle(title, subTitle, 0, 20, 2); //Could use paper's more robust Title API
            }
        }.runTaskTimer(this, 2L, 5L);
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent event)
    {
        event.getPlayer().getPlayer().resetTitle();
    }
}
