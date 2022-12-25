package fr.latvdemomo.pluginonecube.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.plugin.Plugin;


import fr.latvdemomo.pluginonecube.Main;
import fr.latvdemomo.pluginonecube.Gadget.Gadget;
import fr.latvdemomo.pluginonecube.Gadget.GadgetNames;
import fr.latvdemomo.pluginonecube.PPlayer.PPlayer;

public class GadgetCommands implements CommandExecutor{
    private Plugin plugin = Main.getPlugin(Main.class);
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // TODO Auto-generated method stub
        switch (cmd.getName().toLowerCase()) {
            case "gu":
            case "gadgetupgrade":
                if (args.length < 2) {
                    sendErrorMessage(sender, plugin.getConfig().getString("GadgetUpgradeUsage"));
                    return true;
                } else if (args.length == 2) {
                    gadgetUpgrade(sender, Main.getPServer().getPPlayer(args[0]), args[1], "1");
                } else {
                    gadgetUpgrade(sender, Main.getPServer().getPPlayer(args[0]), args[1], args[2]);
                }
                return true;
            case "gadgetgive":
                if (args.length < 2) {
                    sendErrorMessage(sender, plugin.getConfig().getString("GadgetGiveUsage"));
                    return true;
                } else if (args.length == 2) {
                    gadgetGive(sender, Main.getPServer().getPPlayer(args[0]), args[1], "1");
                } else {
                    gadgetGive(sender, Main.getPServer().getPPlayer(args[0]), args[1], args[2]);
                }
                return true;
        }
        return true;
    }
    
    public void sendErrorMessage(CommandSender sender, String error) {
        sender.sendMessage("§c"+error);
    }

    public void gadgetUpgrade(CommandSender sender, PPlayer pPlayer, String gadgetName, String amount) {
        if (pPlayer == null) {
            sendErrorMessage(sender, plugin.getConfig().getString("PlayerNotFoundError"));
            return;
        }
        
        if (!GadgetNames.isValid(gadgetName)) {
            sendErrorMessage(sender, plugin.getConfig().getString("GadgetNotFoundError"));
            return;
        }
        int finalLevel;
        try {
            finalLevel = Integer.parseInt(amount);
        } catch (NumberFormatException error) {
            sendErrorMessage(sender, plugin.getConfig().getString("GadgetUpgradeUsage"));
            return;
        }

        pPlayer.setGadgetLevel(gadgetName, finalLevel);
        sender.sendMessage("§aLe gadget §6" + gadgetName + "§a de §6" + pPlayer.getName() + " §aest maintenant au niveau §6" + pPlayer.getGadgetLevel(gadgetName));
    }

    public void gadgetGive(CommandSender sender, PPlayer pPlayer, String gadgetName, String amount) {
        if (pPlayer == null) {
            sendErrorMessage(sender, plugin.getConfig().getString("PlayerNotFoundError"));
            return;
        }
        
        if (!GadgetNames.isValid(gadgetName)) {
            sendErrorMessage(sender, plugin.getConfig().getString("GadgetNotFoundError"));
            return;
        }
        int finalLevel;
        try {
            finalLevel = Integer.parseInt(amount);
        } catch (NumberFormatException error) {
            sendErrorMessage(sender, plugin.getConfig().getString("GadgetGiveUsage"));
            return;
        }

        
        pPlayer.getPlayer().getInventory().addItem(Gadget.getGadget(gadgetName, 1, finalLevel));
        pPlayer.getPlayer().sendMessage("§aVous venez de recevoir votre gadget.");
    }
}
