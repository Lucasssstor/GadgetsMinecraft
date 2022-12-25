package fr.latvdemomo.pluginonecube.Gadget;

import org.bukkit.inventory.ItemStack;

public abstract class Gadget {
    private int level;
    private ItemStack item;
    private int maxGadgetLevel;

    public Gadget(int level, ItemStack item, int maxLevel) {
        this.level = level;
        this.item = item;
        maxGadgetLevel = maxLevel;
    }

    public int getMaxGadgetLevel () {
        return maxGadgetLevel;
    }

    public int getLevel() {
        return this.level;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public static ItemStack getGadget(String gadgetName, int amount, int level) {
        switch(gadgetName.toLowerCase()) {
            case "frozensnowball":
                return CustomItem.frozenSnowball(amount, level);
            case "autoturret":
                return CustomItem.autoTurret(amount, level);
            case "magicclock":
                return CustomItem.magicClock(amount, level);
            case "staserock":
                return CustomItem.staseRock(amount, level);
            case "ironaxe":
                return CustomItem.ironAxe(amount, level);
            case "hotpotato":
                return CustomItem.hotPotato(amount, level);
        }
        return null;
    }

}
