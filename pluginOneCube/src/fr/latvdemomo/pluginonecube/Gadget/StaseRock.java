package fr.latvdemomo.pluginonecube.Gadget;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StaseRock extends Gadget {
    private static final int maxStaseRockLevel = 3;

    public StaseRock(int level) {
        super(level, new ItemStack(Material.CLAY_BALL, 1), maxStaseRockLevel);
    }
}
