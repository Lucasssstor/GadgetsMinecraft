package fr.latvdemomo.pluginonecube.Gadget;

import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;


public class CustomItem {
    /* Process to add a new item:
     *  - Create a new Class with its name, that extends Gadget
     *  - Create the method in CustomItem to give the ItemStack
     *  - Add its name to the GadgetNames file
     *  - Add the item to the Gadget.getGatget() method
     */ 
    public static ItemStack frozenSnowball(int n, int level) {
        ItemStack snowball = new ItemStack(Material.SNOW_BALL, n);
        ItemMeta snowballMeta = snowball.getItemMeta();
        snowballMeta.setDisplayName("§b Boule de neige givrée");
        List<String> loreList = FrozenSnowBall.getLoreAtLevel(level);
        snowballMeta.setLore(loreList);
        snowball.setItemMeta(snowballMeta);
        return snowball;
    }

    public static ItemStack ironAxe(int n, int level) {
        if (n==0) return null;
        ItemStack ironAxe = new ItemStack(Material.IRON_AXE, n);
        ItemMeta ironAxeMeta = ironAxe.getItemMeta();
        ironAxeMeta.setDisplayName("§e Hache de foudre");
        List<String> loreList = ironAxeClass.getLoreAtLevel(level);
        ironAxeMeta.setLore(loreList);
        ironAxeMeta.spigot().setUnbreakable(true);
        ironAxe.setDurability((short) 0);
        ironAxe.setItemMeta(ironAxeMeta);
        return ironAxe;
    }

    public static ItemStack staseRock(int n, int level) {
        if (n<=0) return null;
        ItemStack staseRock = new ItemStack(Material.CLAY_BALL, n);
        ItemMeta staseRockMeta = staseRock.getItemMeta();
        staseRockMeta.setDisplayName("§7 Pierre de Stase");
        staseRockMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        List<String> loreList = new ArrayList<>();
        loreList.add("Utiliser pour entrer en stase: ");
        loreList.add("vous ne pouvez plus bouger, ");
        loreList.add("mais vous renvoyez les dégâts subis !");
        loreList.add("");
        loreList.add("§6Améliorations:");
        switch (level) {
            case 1:
                loreList.add("•Pourcentage de dégâts[§630%§5/60%/80%]");
                break;
            case 2:
                loreList.add("•Pourcentage de dégâts[30%/§660%§5/80%]");
                break;
            case 3:
                loreList.add("•Pourcentage de dégâts[30%/60%/§680%§5]");
                break;
        }
        staseRockMeta.setLore(loreList);
        staseRock.setItemMeta(staseRockMeta);
        return staseRock;

    }

    public static ItemStack magicClock(int n, int level) {
        if (n<=0) return null;
        ItemStack magicClock = new ItemStack(Material.WATCH, n);
        ItemMeta magicClockMeta = magicClock.getItemMeta();
        
        magicClockMeta.setDisplayName("§6 Montre magique");
        List<String> loreList = new ArrayList<>();
        loreList.add("Utiliser une fois pour commencer ");
        loreList.add("à voyager dans le temps. ");
        loreList.add("Ré-utiliser pour revenir au point ");
        loreList.add("de départ plus rapidement.");
        loreList.add("");
        loreList.add("§6Améliorations:");
        switch (level) {
            case 1:
                loreList.add("•Durée max: [§65s§5/8s/11s]");
                break;
            case 2:
                loreList.add("•Durée max: [5s/§68s§5/11s]");
                break;
            case 3:
                loreList.add("•Durée max: [5s/8s/§611s§5]");
                break;
        }
        
        magicClockMeta.setLore(loreList);
        magicClock.setItemMeta(magicClockMeta);
        return magicClock;
    }

    public static ItemStack autoTurret(int n, int level) {
        if (n<=0) return null;
        
        ItemStack skull = turretHead(n);
        ItemMeta skullMeta = skull.getItemMeta();
        
        skullMeta.setDisplayName("§6 Tourelle automatique");
        List<String> loreList = AutoTurret.getLoreAtLevel(level);
        
        skullMeta.setLore(loreList);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack turretHead(int n) {
        // Getting the appropriated skull (https://www.spigotmc.org/threads/tutorial-skulls.135083/#post-1432132)
        String url = "https://textures.minecraft.net/texture/fa2c3e79d5f35a9dcab19e43c3e3a6519e426b64a61213cd2f1d28b57036f6";

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta skullMeta = skull.getItemMeta();
        
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField = null;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
          } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        profileField.setAccessible(true);

        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack hotPotato(int n, int level) {
        if (n<=0) return null;
        ItemStack hotPotato = new ItemStack(Material.POTATO_ITEM, n);
        ItemMeta hotPotatoMeta = hotPotato.getItemMeta();
        hotPotatoMeta.setDisplayName("§4Patate chaude !");
        List<String> loreList = HotPotato.getLoreAtLevel(level);
        hotPotatoMeta.setLore(loreList);
        hotPotato.setItemMeta(hotPotatoMeta);
        return hotPotato;
    }

}
