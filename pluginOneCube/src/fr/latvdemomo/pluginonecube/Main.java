package fr.latvdemomo.pluginonecube;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.latvdemomo.pluginonecube.Commands.GadgetCommands;
import fr.latvdemomo.pluginonecube.PPlayer.PServer;

public class Main extends JavaPlugin{
    private static Plugin plugin;
    private static PServer pServer;
    @Override
    public void onEnable(){
        plugin = this;
        pServer = new PServer();

        getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PluginListeners(), this);
        getCommand("gadgetUpgrade").setExecutor(new GadgetCommands());
        getCommand("gadgetGive").setExecutor(new GadgetCommands());
        System.out.println("Le plugin OneCubeTest est lancé.");
    }
    

    public static Plugin getPlugin() {
        return plugin;
      }
    
      public static PServer getPServer() {
        return pServer;
      }
}
