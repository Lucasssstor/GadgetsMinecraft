package fr.latvdemomo.pluginonecube.PPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.latvdemomo.pluginonecube.Gadget.AutoTurret;
import fr.latvdemomo.pluginonecube.Gadget.GadgetNames;

public class PPlayer {
    private Location magicClockLocation = null;
    private UUID playerUUID;
    private Map<String, Long> gadgetCds = new HashMap<>();
    private Map<String, Integer> gadgetLvls = new HashMap<>();
    @Nonnull 
    private Set<AutoTurret> autoTurrets = new HashSet<>();

    public PPlayer(Player player) {
        this.playerUUID = player.getUniqueId();
        for (GadgetNames gadget: GadgetNames.values()) {
            this.gadgetCds.put(gadget.getName(), System.currentTimeMillis()/1000);
            this.gadgetLvls.put(gadget.getName(), 1);
        }
        
    }

    public Set<AutoTurret> getAutoTurrets() {
        return this.autoTurrets;
    }

    public void addAutoTurret(AutoTurret aT) {
        this.autoTurrets.add(aT);
    }

    public boolean isOwnerOfAutoTurretAtLocation(Location location) {
        for (AutoTurret aTurret: this.autoTurrets) {
            if (aTurret.getLocation().distance(location)<=2) return true;
        }
        return false;
    }

    public AutoTurret getAutoTurretAt(Location location) {
        for (AutoTurret aTurret: this.autoTurrets) {
            if (aTurret.getLocation().distance(location)<=2) return aTurret;
        }
        return null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public String getName() {
        return this.getPlayer().getName();
    }

    public Location getMagicClockLocation() {
        return this.magicClockLocation;
    }

    public void setMagicClockLocation(Location l) {
        this.magicClockLocation = l;
    }

    public int getGadgetLevel(String gadgetName) {
        return this.gadgetLvls.get(gadgetName.toLowerCase());
    }

    public boolean setGadgetLevel(String gadgetName, int value) {
        if (this.gadgetLvls.containsKey(gadgetName.toLowerCase())) {
            this.gadgetLvls.put(gadgetName.toLowerCase(), value);
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(PPlayer other) {
        return this.getPlayer().getUniqueId().equals(other.getPlayer().getUniqueId());
    }

    public ItemStack getSimilarItem(ItemStack item) {
        Inventory inv = this.getPlayer().getInventory();
        while (inv.iterator().hasNext()) {
            ItemStack i = inv.iterator().next();
            if (i.getType().equals(item.getType()) && i.hasItemMeta() && item.hasItemMeta() && i.getItemMeta().getDisplayName() == item.getItemMeta().getDisplayName()) {
                return i;
            }

        }
        return null;
    }

    public void removeItems(ItemStack item, int amount, boolean removeEnchantments) {
        Inventory inventory = this.getPlayer().getInventory();

        if (!(getSimilarItem(item) == null)) {
            return;
        }

        item = getSimilarItem(item);
        int i = inventory.first(item);
        
        int newAmount = inventory.getItem(i).getAmount() - amount;
        ItemStack finalItem;

        if (newAmount == 0) {
            finalItem = new ItemStack(Material.AIR);
        } else {
            
            finalItem = inventory.getItem(i);
            if (removeEnchantments) {
                ItemMeta finalMeta = finalItem.getItemMeta();
                for (Enchantment ench: finalItem.getItemMeta().getEnchants().keySet()) {
                    finalMeta.removeEnchant(ench);
                }
                finalItem.setItemMeta(finalMeta);
            } 
            
            finalItem.setAmount(newAmount);
        }
        
        inventory.setItem(i, finalItem);

    }

}
