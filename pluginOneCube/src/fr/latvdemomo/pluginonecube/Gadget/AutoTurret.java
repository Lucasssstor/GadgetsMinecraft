package fr.latvdemomo.pluginonecube.Gadget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.latvdemomo.pluginonecube.Animation;
import fr.latvdemomo.pluginonecube.Main;
import fr.latvdemomo.pluginonecube.PPlayer.PPlayer;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntitySkull;




public class AutoTurret extends Gadget {
    private int health, portee, ticksLeftToLive, attackSpeed;
    private Location headLocation;
    private Player owner;
    private ArmorStand healthDisplay;
    private World world;
    private static final int maxAutoTurretLevel = 3;


    public AutoTurret(int level, Location location, Player owner) {
        super(level, CustomItem.autoTurret(1, level), maxAutoTurretLevel);
        this.health = 3;
        this.portee = getPorteeByLevel(level);
        this.headLocation = location;
        this.ticksLeftToLive = setTicksToLiveOnSpawn(level);
        this.owner = owner;
        this.world = owner.getWorld();
        this.attackSpeed = setAttackSpeed(level);

        updateHealthDisplay();
        lookForAttack();
    }

    public static final int getMaxAutoTurretLevel() {
        return maxAutoTurretLevel;
    }

    public Location getLocation() {
        return headLocation;
    }

    public void updateHealthDisplay() {
        if (this.healthDisplay != null) {
            this.healthDisplay.remove();
        }
        Animation.Circle((float)(this.headLocation.getX()+0.5), (float)this.headLocation.getY(), (float)this.headLocation.getZ()+0.5f, (float)this.portee, owner);
        Location asLoc = new Location(world, headLocation.getX()+0.5, headLocation.getY()-1.75, headLocation.getZ()+0.5);

        ArmorStand armorStand = (ArmorStand)world.spawnEntity(asLoc, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomName("§cTourelle de " + owner.getDisplayName() + ": " + "§4" + this.health + "♥" + " §8" + this.ticksLeftToLive/20 + "⧗");
        armorStand.setCustomNameVisible(true);
        this.healthDisplay = armorStand;
    }

    public void lookForAttack() {
        updateHealthDisplay();
        this.ticksLeftToLive -= this.attackSpeed;
        if (this.ticksLeftToLive <= 0 || this.health <= 0) {
            onBreak();
            return;
        }
        
        Collection<Entity> near = owner.getWorld().getNearbyEntities(this.headLocation, (double)this.portee, (double)this.portee, (double)this.portee);
        // We're going to shoot the 1st Player seen, or the furthest Entity
        Location whereToShoot = null;
        for(Entity entity : near) {
            if (entity instanceof LivingEntity && !entity.equals(owner) && !(entity instanceof ArmorStand)) {
                whereToShoot = entity.getLocation();
                whereToShoot.setY(whereToShoot.getY()+1);
            }
            if(entity instanceof Player && !entity.equals(owner)) {
                break;
            }
        }
        
        if (whereToShoot != null) {
            Location spawnLocation = this.headLocation.clone();
            spawnLocation.setY(headLocation.getY()+1);
            changeOrientation(headLocation, whereToShoot, owner.getWorld());
            fireArrowTowardsLocation(spawnLocation, whereToShoot);
        } 

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                lookForAttack();
            }
        }, this.attackSpeed);
    }

    public void changeOrientation(Location blockToChange, Location locationToFace, World world) {
        BlockFace facing = BlockFace.NORTH;


        double x = locationToFace.getX()-blockToChange.getX();
        double z = locationToFace.getZ()-blockToChange.getZ();
        double angle;

        x = x/(Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)));
        z = z/(Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)));

        if (z>=0) {
            angle = Math.acos(x);
        } else {
            angle = -Math.acos(x);
        }

        angle = Math.toDegrees(angle)+180;

        if (angle >= 22.5 && angle < 67.5) facing = BlockFace.NORTH_WEST;
        if (angle >= 67.5 && angle < 112.5) facing = BlockFace.NORTH;
        if (angle >= 112.5 && angle < 157.5) facing = BlockFace.NORTH_EAST;
        if (angle >= 157.5 && angle < 202.5) facing = BlockFace.EAST;
        if (angle >= 202.5 && angle < 247.5) facing = BlockFace.SOUTH_EAST;
        if (angle >= 247.5 && angle < 292.5) facing = BlockFace.SOUTH;
        if (angle >= 292.5 && angle < 337.5) facing =BlockFace.SOUTH_WEST;
        if (angle >= 337.5 || angle < 22.5) facing = BlockFace.WEST;
        

        Block b = world.getBlockAt(blockToChange);
        Skull skull = (Skull) b.getState();
        skull.setRotation(facing);
        skull.update();
    }

    public void fireArrowTowardsLocation(Location source, Location destination) {
        Arrow arrow = owner.launchProjectile(Arrow.class);
        arrow.teleport(source);
        Vector arrowVector = new Vector(destination.getX()-source.getX(), destination.getY()-source.getY()+1, destination.getZ()-source.getZ());
        arrow.setVelocity(arrowVector.normalize().multiply(2));
        arrow.getLocation().setDirection(arrowVector);
        arrow.setCustomName("turretArrow");
    }

    public void onHit() {
        this.health --;
        updateHealthDisplay();
        owner.playSound(headLocation, Sound.ANVIL_LAND, 1.0f, 1.0f);
    }

    public void onBreak() {

        Block b = this.owner.getWorld().getBlockAt(this.headLocation);
        b.setType(Material.AIR);
        b.setType(Material.SKULL);

        BlockState skullState = b.getState();
        ((Directional) skullState.getData()).setFacingDirection(BlockFace.DOWN);
        skullState.update();

        TileEntitySkull skullTile = (TileEntitySkull) ((CraftWorld)b.getWorld()).getHandle().getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));

        skullTile.setSkullType(0);
        Animation.LargeExplosion((float)headLocation.getX(), (float)headLocation.getY(), (float)headLocation.getZ());
        owner.playSound(headLocation, Sound.EXPLODE, 1.0f, 1.0f);
        owner.sendMessage("§0" + Main.getPlugin().getConfig().getString("AutoTurretDestroyedMessage"));
        this.healthDisplay.remove();
        this.healthDisplay = null;
        b.getState().update();
    }

    public int setAttackSpeed(int level) {
        switch (level) {
            case 1:
                return 20;
            case 2:
                return 15;
            case 3:
                return 10;
        }
        return 5;
    }

    public int getPorteeByLevel(int level) {
        switch (level) {
            case 1:
                return 10;
            case 2:
                return 15;
            case 3:
                return 20;
        }
        return 20;
    }

    public int setTicksToLiveOnSpawn(int level) {
        switch (level) {
            case 1:
                return 20*10;
            case 2:
                return 20*15;
            case 3:
                return 20*20;
        }
        return 20*20;
    }

    public static void autoTurretUse(PPlayer pPlayer, ItemStack item, Block blockClicked) {
        Player player = pPlayer.getPlayer();
        World world = player.getWorld();

        int x = blockClicked.getX();
        int y = blockClicked.getY();
        int z = blockClicked.getZ();

        boolean placeable = true;
        for (int i=-1; i<2; i++) {
            for (int j=-1; j<2; j++) {
                if ((i!=j || i==0) && world.getBlockAt(x+i, y+1, z+j).getType() != Material.AIR) {
                    placeable = false;
                }
            }
        }

        if (!placeable || world.getBlockAt(x, y+2, z).getType() != Material.AIR) {
            player.sendMessage("§c" + Main.getPlugin().getConfig().getString("AutoTurretNoSpaceToDeploy"));
            return;
        }
        world.getBlockAt(x, y+1, z).setType(Material.FENCE);
        
        turnIntoBanner(x-1, y+1, z, world, BlockFace.WEST);
        turnIntoBanner(x+1, y+1, z, world, BlockFace.EAST);
        turnIntoBanner(x, y+1, z-1, world, BlockFace.NORTH);
        turnIntoBanner(x, y+1, z+1, world, BlockFace.SOUTH);

        
        Block b = world.getBlockAt(x, y+2, z);
        b.setType(Material.SKULL);
        String url = "https://textures.minecraft.net/texture/fa2c3e79d5f35a9dcab19e43c3e3a6519e426b64a61213cd2f1d28b57036f6";

        BlockState skullState = b.getState();
        ((Directional) skullState.getData()).setFacingDirection(BlockFace.DOWN);
        skullState.update();

        GameProfile profile = new GameProfile(UUID.fromString("fda4e8a0-4f40-4bd9-adbe-f53a051e5ce2"), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        TileEntitySkull skullTile = (TileEntitySkull) ((CraftWorld)b.getWorld()).getHandle().getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        
        skullTile.setGameProfile(profile);

        b.getState().update(true);

        ItemStack newStack = player.getItemInHand();
        if (newStack.getAmount() == 1) {
            player.setItemInHand(new ItemStack(Material.AIR));
        } else {
            newStack.setAmount(newStack.getAmount()-1);
            player.setItemInHand(newStack);
        }

        player.sendMessage("§a" + Main.getPlugin().getConfig().getString("AutoTurretOnDeploy"));
        
        int level = 1;
        for (int i=0; i<=AutoTurret.getMaxAutoTurretLevel(); i++) {
            if (item.getItemMeta().getLore().equals(AutoTurret.getLoreAtLevel(i))) level=i;
        }

        pPlayer.addAutoTurret(new AutoTurret(level, b.getLocation(), player));
    }

    public static List<String> getLoreAtLevel(int level) {
        List<String> loreList = new ArrayList<>();
        loreList.add("Une redoutable tourelle qui tire");
        loreList.add("des flèches sur les ennemis à portée.");
        loreList.add("");
        loreList.add("§6Améliorations:");
        switch (level) {
            case 1:
                loreList.add("•Cadence (par seconde): [§61§5/2/4]");
                loreList.add("•Portée: [§65blocks§5/10blocks/15blocks]");
                break;
            case 2:
                loreList.add("•Cadence (par seconde): [1/§62§5/4]");
                loreList.add("•¨Portée: [5blocks/§610blocks§5/15blocks]");
                break;
            case 3:
                loreList.add("•Cadence (par seconde): [1/2/§64§5]");
                loreList.add("•¨Portée: [5blocks/10blocks/§615blocks§5]");
                break;
        }
        return loreList;
    }

    public static void turnIntoBanner(int x, int y, int z, World world, BlockFace direction) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(Material.WALL_BANNER);
        BlockState bs = block.getState();
        org.bukkit.block.Banner banner = (org.bukkit.block.Banner)bs;
        ((Directional) banner.getData()).setFacingDirection(direction);
        banner.update();

        
    }
    

}
