package fr.latvdemomo.pluginonecube.Gadget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.latvdemomo.pluginonecube.Main;

public class HotPotato extends Gadget{
    private static final int maxHotPotatoLevel = 4;
    private int ticksLeftBeforeExplosion;

    public HotPotato(int level, int amount) {
        super(level, CustomItem.hotPotato(amount, level), maxHotPotatoLevel);
        this.ticksLeftBeforeExplosion = 5*20;
    }
    
    public static int getMaxHotPotatoLevel() {
        return maxHotPotatoLevel;
    }

    public void onHotPotatoUse(int level, Player victim, Player user) {
        int indiceFirstEmpty;
        indiceFirstEmpty = getFirstEmptySlot(victim.getInventory());
        if (indiceFirstEmpty == -1) {
            user.sendMessage("§c" + Main.getPlugin().getConfig().getString("InventoryFullError"));
            user.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1));
            return;
        }
        ItemStack givenPotato = getInventoryHotPotato(level);
        victim.getInventory().setItem(indiceFirstEmpty, givenPotato);
        victim.sendMessage(user.getName() + " vient de vous donner une " + givenPotato.getItemMeta().getDisplayName() + ".");
        victim.sendMessage("Débarassez-vous en §4vite§r !");
        if (level >= 2) victim.setFireTicks(20);
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                potatoStillInInventory(victim, level, user);
            }
        }, 10);
    }

    public static int getFirstEmptySlot(Inventory inventory) {
        for (int i=0; i<inventory.getSize(); i++) {
            if (inventory.getItem(i)==null) {
                return i;
            }
        }
        return -1;
    }

    public void potatoStillInInventory(Player victim, int level, Player attacker) {
        this.ticksLeftBeforeExplosion -= 10;
        if (this.ticksLeftBeforeExplosion<=0) {
            removeHotPotatoesInInventory(victim.getInventory());
            victim.getWorld().createExplosion(victim.getLocation().getX(),victim.getLocation().getY(), victim.getLocation().getZ(),8.0f, false, false);
            victim.sendMessage("La patate de " + attacker.getName() + " a explosé !");
            attacker.sendMessage("Votre §4Patate Chaude§r a explosé dans l'inventaire de " + victim.getName());
            return;
        }

        if (level >= 2) victim.setFireTicks(20);

        boolean duplicationDone = false;
        if (level >= 3) {
            
            Inventory inv = victim.getInventory();
            for (int ancientSlot: getAllHotPotatoes(inv)) {
                int newSlot = getRandomDeepSlot(inv);
                ItemStack itemAtAncientSlot = inv.getItem(ancientSlot);
                ItemStack itemAtNewSlot = inv.getItem(newSlot);
                inv.setItem(ancientSlot, itemAtNewSlot);
                inv.setItem(newSlot, itemAtAncientSlot);
                if (level >= 4 && !duplicationDone && getAllHotPotatoes(victim.getInventory()).size() <= 2) {
                    int iFirst = getFirstEmptySlot(inv);
                    if(iFirst != -1) inv.setItem(iFirst, itemAtAncientSlot);
                    duplicationDone = true;
                }
            }
        }
        
        if (getAllHotPotatoes(victim.getInventory()).size() != 0) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    potatoStillInInventory(victim, level, attacker);
                }
            }, 10);
        } else {
            victim.sendMessage("Vous vous êtes bien débarassé de la §4patate chaude§r !");
        }
    }

    public static Set<Integer> getAllHotPotatoes(Inventory inventory) {
        Set<Integer> indexSet = new HashSet<>();
        for (int k=0; k<inventory.getSize(); k++) {
            ItemStack i = inventory.getItem(k);
            if (i != null && i.getType().equals(Material.POTATO_ITEM) && i.hasItemMeta()) {
                ItemMeta potatoMeta = i.getItemMeta();
                if (potatoMeta.hasDisplayName()) {
                    for (int l=1; l<=HotPotato.getMaxHotPotatoLevel(); l++) {
                        if (potatoMeta.getDisplayName().equals("§4Patate chaude [Niveau "+ l + "]")) {
                            indexSet.add(k);
                        }
                    }
                }
            }
        }
        return indexSet;
    }

    public static void removeHotPotatoesInInventory(Inventory inventory) {
        for (int k=0; k<inventory.getSize(); k++) {
            ItemStack i = inventory.getItem(k);
            if (i != null && i.getType().equals(Material.POTATO_ITEM) && i.hasItemMeta()) {
                ItemMeta potatoMeta = i.getItemMeta();
                if (potatoMeta.hasDisplayName()) {
                    for (int l=1; l<=HotPotato.getMaxHotPotatoLevel(); l++) {
                        if (potatoMeta.getDisplayName().equals("§4Patate chaude [Niveau "+ l + "]")) {
                            inventory.setItem(k, new ItemStack(Material.AIR));
                        }
                            
                    }
                }
            }
        }
    }

    public static int getRandomDeepSlot(Inventory inv) {
        Random rd = new Random();
        return rd.nextInt(inv.getSize()-9)+9;
    }

    public static ItemStack getInventoryHotPotato(int level) {
        ItemStack potato = new ItemStack(Material.POTATO_ITEM, 1);
        ItemMeta potatoMeta = potato.getItemMeta();
        potatoMeta.setDisplayName("§4Patate chaude [Niveau " + level + "]");
        potato.setItemMeta(potatoMeta);
        return potato;
    }

    public static List<String> getLoreAtLevel(int level) {
        List<String> loreList = new ArrayList<>();
        loreList.add("Frappez un adversaire avec cette patate ");
        loreList.add("pour la lui donner.");
        loreList.add("Elle explose après un court instant !");
        loreList.add("");
        loreList.add("§6Améliorations:");
        switch (level) {
            case 1:
                loreList.add("Niveau 1:");
                loreList.add(ChatColor.STRIKETHROUGH + "Met l'adversaire en feu");
                loreList.add(ChatColor.STRIKETHROUGH + "La patate change d'emplacement");
                loreList.add(ChatColor.STRIKETHROUGH + "La patate se multiplie");
                break;
            case 2:
                loreList.add("Niveau 2:");
                loreList.add("Met l'adversaire en feu");
                loreList.add(ChatColor.STRIKETHROUGH + "La patate change d'emplacement");
                loreList.add(ChatColor.STRIKETHROUGH + "La patate se multiplie");
                break;
            case 3:
                loreList.add("Niveau 3:");
                loreList.add("Met l'adversaire en feu");
                loreList.add("La patate change d'emplacement");
                loreList.add(ChatColor.STRIKETHROUGH + "La patate se multiplie");
                break;
            case 4:
                loreList.add("Niveau 4:");
                loreList.add("Met l'adversaire en feu");
                loreList.add("La patate change d'emplacement");
                loreList.add("La patate se multiplie");
                break;
        }
        return loreList;
    }

}
