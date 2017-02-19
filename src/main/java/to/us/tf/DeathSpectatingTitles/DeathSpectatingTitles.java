package to.us.tf.DeathSpectatingTitles;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import to.us.tf.DeathSpectating.events.DeathSpectatingEvent;
import to.us.tf.DeathSpectating.tasks.SpectateTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private List<String> deathSubTitles = new ArrayList<>(Arrays.asList("Respawning in {0}", "Score: {1}", "Score: {1}, Respawning in {0}"));

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

    @EventHandler
    void onSpectate(DeathSpectatingEvent event)
    {
        new BukkitRunnable()
        {
            SpectateTask spectateTask = event.getSpectateTask();
            String unformattedTitle = getRandomString(deathTitles);
            String unformattedSubTitle = getRandomString(deathSubTitles);
            int score = spectateTask.getPlayer().getTotalExperience();

            @Override
            public void run()
            {
                if (spectateTask.getTicks() < 2)
                    this.cancel();
                int seconds = (int)spectateTask.getTicks() / 20;
                String title = formatter(unformattedTitle, seconds, score);
                String subTitle = formatter(unformattedSubTitle, seconds, score);
                spectateTask.getPlayer().sendTitle(title, subTitle, 0, 20, 2); //Could use paper's more robust Title API
            }
        }.runTaskTimer(this, 2L, 10L);
    }
}
