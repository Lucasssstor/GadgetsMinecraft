package fr.latvdemomo.pluginonecube;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import fr.latvdemomo.pluginonecube.Gadget.AutoTurret;
import fr.latvdemomo.pluginonecube.Gadget.CustomItem;
import fr.latvdemomo.pluginonecube.Gadget.FrozenSnowBall;
import fr.latvdemomo.pluginonecube.Gadget.HotPotato;
import fr.latvdemomo.pluginonecube.Gadget.MagicClock;
import fr.latvdemomo.pluginonecube.Gadget.ironAxeClass;
import fr.latvdemomo.pluginonecube.PPlayer.PPlayer;
import fr.latvdemomo.pluginonecube.PPlayer.PServer;


public class PluginListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PServer pServer = Main.getPServer();
        if (pServer.getPPlayer(player) == null) pServer.addPPlayer(new PPlayer(player));

        player.sendRawMessage("Salut, " + event.getPlayer().getName());;
        

        PPlayer pPlayer = Main.getPServer().getPPlayer(player);
        player.getInventory().addItem(CustomItem.frozenSnowball(5, pPlayer.getGadgetLevel("frozenSnowball")));
        player.getInventory().addItem(CustomItem.ironAxe(3, pPlayer.getGadgetLevel("ironAxe")));
        player.getInventory().addItem(CustomItem.staseRock(1, pPlayer.getGadgetLevel("staseRock")));
        player.getInventory().addItem(CustomItem.magicClock(1, pPlayer.getGadgetLevel("magicClock")));
        player.getInventory().addItem(CustomItem.autoTurret(1, pPlayer.getGadgetLevel("autoTurret")));

        player.updateInventory();

        player.sendRawMessage("Amuse-toi bien !");;
    }

    @EventHandler
    public void onSnowballThrow(ProjectileLaunchEvent event) {
        if (! (event.getEntity() instanceof Snowball || !(event.getEntity().getShooter() instanceof Player))) return;
    
        Player player = (Player) event.getEntity().getShooter();

        ItemStack itemInHand = (ItemStack) player.getItemInHand();

        if (itemInHand == null || !itemInHand.hasItemMeta() || !itemInHand.getItemMeta().hasDisplayName() || !itemInHand.getItemMeta().hasLore()) return;

        if (itemInHand.getItemMeta().getDisplayName().equals("§b Boule de neige givrée")) {
            int level = 0;
            for (int i=0; i<=FrozenSnowBall.getMaxSnowBallLevel(); i++) {
                if (FrozenSnowBall.getLoreAtLevel(i).equals(itemInHand.getItemMeta().getLore())) {
                    level = i;
                }
            }
            if (level == 0) return;
            event.getEntity().setCustomName("icedSnowball" + "[" + level + "]");

        }

            
        
    }

    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent event)
    {
        Entity damaged = event.getEntity();
        Entity damageEntity = event.getDamager();

        if (!(damaged instanceof Player) || !(damageEntity instanceof Snowball)) return;

        int level = 0;
        for (int i=0; i<=FrozenSnowBall.getMaxSnowBallLevel(); i++) {
            if (damageEntity.getCustomName().equals("icedSnowball[" + i + "]")) level=i;
        }
        if (level==0) return;
        
        Snowball snowball = (Snowball)damageEntity;
        ProjectileSource entityThrower = snowball.getShooter();
        if(!(entityThrower instanceof Player)) return;
        
        Player playerHit = (Player)damaged;
        Animation.frozenSnowballHit((float)playerHit.getLocation().getX(), (float)playerHit.getLocation().getY(), (float)playerHit.getLocation().getZ());

        playerHit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 4*20, level));
        playerHit.playSound(playerHit.getLocation(), Sound.SPIDER_IDLE, 1.0f, 1.0f);
        
        ItemStack casque = playerHit.getInventory().getHelmet();
        playerHit.getInventory().setHelmet(new ItemStack(Material.ICE));
        playerHit.sendRawMessage("Tu as été touché par une §b§nBoule de neige givrée !");
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (casque !=null && casque.getType()!=Material.ICE) {
                    playerHit.getInventory().setHelmet(casque);
                    playerHit.sendRawMessage("L'effet de la boule de la §b§nBoule de neige givrée §rprend fin, tu as récupéré ton casque.");
                }  else {
                    playerHit.getInventory().setHelmet(new ItemStack(Material.AIR));
                    playerHit.sendRawMessage("L'effet de la boule de la §b§nBoule de neige givrée §rprend fin.");
                }
            }      
        }, 4*20L);
    }

    @EventHandler
    public void onArmorStandProtection(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand && !((ArmorStand)event.getEntity()).isVisible()) {
            Location aSLocation = event.getEntity().getLocation();
            for (PPlayer pPlayer: Main.getPServer().getPPlayers()) {
                if (pPlayer.isOwnerOfAutoTurretAtLocation(aSLocation)) {
                    pPlayer.getAutoTurretAt(aSLocation).onHit();
                }
            }

            if (event.getDamager() instanceof Arrow) event.getDamager().remove();
            event.setCancelled(true);
        }
    }
    
    // ironAxe
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        ItemStack heldItem = damager.getItemInHand();
        if (heldItem == null) return;
        
        if (heldItem.getType() != Material.IRON_AXE || !heldItem.hasItemMeta()) return;
        
        ItemMeta heldItemMeta = heldItem.getItemMeta();

        if (!heldItemMeta.hasLore() || !heldItemMeta.hasDisplayName()) return;

        int amount = heldItem.getAmount();

        int level = 0;
        for (int i=1; i<=ironAxeClass.getMaxIronAxeLevel(); i++) {
            if (heldItemMeta.getLore().equals(ironAxeClass.getLoreAtLevel(i))) level=i;
        }
        
        if (level == 0) return;

        if (heldItemMeta.getDisplayName().equals("§e Hache de foudre")) {
            Entity entityHit =  event.getEntity();
            new ironAxeClass(level, amount).onUse(entityHit, damager);
        }
    }

    // HotPotato
    @EventHandler
    public void onHitByPotato(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (damager.getItemInHand() == null || damager.getItemInHand().getType() != Material.POTATO_ITEM) return;
        ItemStack potato = damager.getItemInHand();
        
        if (!potato.hasItemMeta() || !potato.getItemMeta().hasLore() || !potato.getItemMeta().hasDisplayName()) return;
        ItemMeta potatoMeta = potato.getItemMeta();

        int level = 0;
        for (int i=0; i<=HotPotato.getMaxHotPotatoLevel(); i++) {
            if (potatoMeta.getLore().equals(HotPotato.getLoreAtLevel(i))) level = i;
        }
        if (level==0) return;
        int count = potato.getAmount();
        if (count == 0) {
            damager.setItemInHand(new ItemStack(Material.AIR));
        } else {
            potato.setAmount(count-1);
            damager.setItemInHand(potato);
        }
        new HotPotato(level, count).onHotPotatoUse(level, victim, damager);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        // Pierre de Stase
        if (item != null && item.getType().equals(Material.CLAY_BALL) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§7 Pierre de Stase"))
        {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 250));
                player.getInventory().setItemInHand(CustomItem.staseRock(player.getItemInHand().getAmount()-1, 1));
                player.sendMessage("§7Vous entrez en §6stase §7!");
                Animation.stase((float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 2*5);
            }
        }

        // Montre magique
        if (item != null && item.getType().equals(Material.WATCH) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6 Montre magique"))
        {
            if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
            PPlayer pPlayer = Main.getPServer().getPPlayer(player);
            MagicClock.magicClockUse(pPlayer, item);
        }

        // Tourelle automatique
        if (item != null && item.getType().equals(Material.SKULL_ITEM) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6 Tourelle automatique"))
        {   
            if (action == Action.RIGHT_CLICK_AIR) {
                player.sendMessage("§6" + Main.getPlugin().getConfig().getString("AutoTurretOnAirClick"));
                return;
            }

            if (action != Action.RIGHT_CLICK_BLOCK) return;
            event.setCancelled(true);
            Block blockClicked = event.getClickedBlock();
            PPlayer pPlayer = Main.getPServer().getPPlayer(player);
            AutoTurret.autoTurretUse(pPlayer, item, blockClicked);
        }
        
    }

    @EventHandler
    public void onRain(WeatherChangeEvent event) {
        if (Main.getPServer().getWeatherLock()) event.setCancelled(true);
    }

    @EventHandler
    void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        if (!item.hasItemMeta()) return ;
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta.getDisplayName().equals("§6 Montre magique") && !itemMeta.getEnchants().equals(Collections.EMPTY_MAP)) {
            event.setCancelled(true);
            player.sendMessage("§cVous ne pouvez pas lâcher la §6montre magique §cpendant son utilisation !");
        }
    }

    @EventHandler
    void onContainerDrop(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item != null && !item.hasItemMeta()) return ;
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals("§6 Montre magique") && !itemMeta.getEnchants().equals(Collections.EMPTY_MAP)) {
            event.setCancelled(true);
            player.sendMessage("§cVous ne pouvez pas vous débarasser de la §6montre magique §cpendant son utilisation !");
        }
    }

    @EventHandler
    void onPotatoDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType()==Material.POTATO || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        ItemMeta potatoMeta = item.getItemMeta();
        boolean valid = false;
        for (int i=0; i<=HotPotato.getMaxHotPotatoLevel(); i++) {
            if (potatoMeta.getDisplayName().equals(HotPotato.getInventoryHotPotato(i).getItemMeta().getDisplayName())) {
                valid=true;
            }
        }
        if (valid) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        PPlayer dead = Main.getPServer().getPPlayer(event.getEntity());
        dead.setMagicClockLocation(null);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (PotionEffect effect: player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SLOW) && effect.getAmplifier()> 200) {
               player.setVelocity(new Vector(0, 0, 0));
               Location l = new Location(player.getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
               player.teleport(l);
               }
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageByEntityEvent event) {
        
        if (!(event.getEntity() instanceof Player) || (!(event.getDamager() instanceof LivingEntity || event.getDamager() instanceof Projectile)))
            return;
        
        Player playerHurt = (Player) event.getEntity();
        LivingEntity damager;

        if (event.getDamager() instanceof LivingEntity) {
            damager = (LivingEntity) event.getDamager();
        } else {
            if (! (((Projectile) event.getDamager()).getShooter() instanceof LivingEntity)) {
                event.setCancelled(true);
                return;
            }
            damager = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        }
        for (PotionEffect effect: playerHurt.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SLOW) && effect.getAmplifier()> 200) {
                double damage = event.getDamage();
                
                if (damager instanceof Player) {
                    ((Player) damager).playSound(damager.getLocation(), Sound.BAT_DEATH, 1, 1);
                    ((Player) damager).sendMessage("Vous venez d'attaquer un joueur protégé par une §7Pierre de Stase.");
                }
                    
                damager.damage(damage);
                playerHurt.playSound(playerHurt.getLocation(), Sound.ANVIL_BREAK, 1, 1);
                event.setCancelled(true);
            }
        }
    
}

}
