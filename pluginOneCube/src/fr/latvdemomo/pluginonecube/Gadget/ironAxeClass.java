package fr.latvdemomo.pluginonecube.Gadget;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ironAxeClass {
    private int level, amount;
    private static final int maxIronAxeLevel = 3;
    public ironAxeClass(int level, int amount) {
        this.level = level;
        this.amount = amount;
    }

    public static final int getMaxIronAxeLevel() {
        return maxIronAxeLevel;
    }

    public static List<String> getLoreAtLevel(int level) {
        List<String> loreList = new ArrayList<>();
        loreList.add("Une hache puissante qui crée des éclairs à");
        loreList.add(" l'impact.");
        loreList.add("");
        loreList.add("§6Améliorations:");
        switch (level) {
            case 1:
                loreList.add("•Utilisations[§63§5/4/5]");
                break;
            case 2:
                loreList.add("•Utilisations[3/§64§5/5]");
                break;
            case 3:
                loreList.add("•Utilisations[3/4/§65§5]");
                break;
        }
        return loreList;
    }

    public void onUse(Entity entityHit, Player damager) {
        damager.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2*20, this.level*2));
        entityHit.getWorld().strikeLightning(entityHit.getLocation());
        damager.getInventory().setItemInHand(CustomItem.ironAxe(amount-1, 1));
    }
}
