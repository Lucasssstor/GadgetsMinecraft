package fr.latvdemomo.pluginonecube.Gadget;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.latvdemomo.pluginonecube.Animation;
import fr.latvdemomo.pluginonecube.Main;
import fr.latvdemomo.pluginonecube.PPlayer.PPlayer;



public class MagicClock extends Gadget{
    private static final int maxMagicClockLevel = 3;

    public MagicClock(int level, ItemStack item) {
        super(level, item, maxMagicClockLevel);
    }

    public static void magicClockUse(PPlayer pPlayer, ItemStack item) {
        Player player = pPlayer.getPlayer();
        if (pPlayer.getMagicClockLocation() == null) {
            // Case of the 1st use
            
            // We enchant the clock 
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            item.setItemMeta(itemMeta);
            
            // We set the teleport location for the councerned player
            player.playSound(player.getLocation(), Sound.SILVERFISH_HIT, 1.0f, 1.0f);
            pPlayer.setMagicClockLocation(player.getLocation());
            player.sendMessage("§6" + Main.getPlugin().getConfig().getString("MagicClockTimeTravelBegin"));
            MagicClock.runClockAnimation(pPlayer);
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (pPlayer.getMagicClockLocation() != null) {
                        // What runs after the end of the effect
                        Location l = pPlayer.getMagicClockLocation();

                        player.sendMessage("§6" + Main.getPlugin().getConfig().getString("MagicClockTimeTravelEnd"));
                        MagicClock.removeOneMagicClock(pPlayer);
                        player.teleport(l);
                        pPlayer.setMagicClockLocation(null);
                        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
                        Animation.magicClockComeback((float)l.getX(), (float)l.getY(), (float)l.getZ());
                        return;
                    }
                }
            }, 20*(2+3*pPlayer.getGadgetLevel("magicClock")));
        } else {
            // Case of the second use
            Location l = pPlayer.getMagicClockLocation();
            player.sendMessage("§6" + Main.getPlugin().getConfig().getString("MagicClockTimeTravelEnd"));
            MagicClock.removeOneMagicClock(pPlayer);
            player.teleport(l);
            player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
            pPlayer.setMagicClockLocation(null);
            Animation.magicClockComeback((float)l.getX(), (float)l.getY(), (float)l.getZ());
        }
    }

    public static boolean removeOneMagicClock(PPlayer pPlayer) {
        // Finds an enchanted watch stack, and turns it into a non-enchanted watch stack, removing 1
        Player player = pPlayer.getPlayer();
        Inventory inventory = player.getInventory();

        for (int k=0; k<inventory.getSize(); k++) {
            ItemStack i = inventory.getItem(k);
            if (i != null && i.getType().equals(Material.WATCH) && i.hasItemMeta()) {
                ItemMeta watchItemMeta = i.getItemMeta();
                if (watchItemMeta.hasEnchant(Enchantment.DURABILITY)) {
                    // We found an enchanted watch stack

                    if (i.getAmount() == 1) {
                        inventory.setItem(k, new ItemStack(Material.AIR));
                        return true;
                    }

                    watchItemMeta.removeEnchant(Enchantment.DURABILITY);
                    i.setItemMeta(watchItemMeta);
                    i.setAmount(i.getAmount()-1);
                    inventory.setItem(k, i);
                    pPlayer.getPlayer().updateInventory();
                    return true;
                }
            }
        }
        return false;
    }

    public static void runClockAnimation(PPlayer pPlayer) {
        if (pPlayer.getMagicClockLocation() != null) {
            float x = (float)pPlayer.getPlayer().getLocation().getX();
            float y = (float)pPlayer.getPlayer().getLocation().getY();
            float z = (float)pPlayer.getPlayer().getLocation().getZ();
            Animation.goldenTrack(x, y, z);

            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    runClockAnimation(pPlayer);
                }
            }, 4L);
        } else {

        }
    }
}
