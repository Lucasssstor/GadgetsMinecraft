package fr.latvdemomo.pluginonecube;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class Animation {
    // Can maybe create a method to send 
    public static void stase(Float x, Float y, Float z, int duration) {
        for (float y_=0; y_<2; y_+=0.1 ) {
            float dist = (y_ * (-y_ +2)) + 1;
            PacketPlayOutWorldParticles packet1 = new PacketPlayOutWorldParticles(EnumParticle.SPELL_MOB,true, x+dist, y+y_, z+dist, 0, 0, 0, 0, 1);
            PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(EnumParticle.SPELL_MOB,true, x+dist, y+y_, z-dist, 0, 0, 0, 0, 1);
            PacketPlayOutWorldParticles packet3 = new PacketPlayOutWorldParticles(EnumParticle.SPELL_MOB,true, x-dist, y+y_, z-dist, 0, 0, 0, 0, 1);
            PacketPlayOutWorldParticles packet4 = new PacketPlayOutWorldParticles(EnumParticle.SPELL_MOB,true, x-dist, y+y_, z+dist, 0, 0, 0, 0, 1);
            for(Player online : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet1);
                ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet2);
                ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet3);
                ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet4);
            }
        }   
        if (duration > 0) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Animation.stase(x, y, z, duration-1);
                }
            }, 10L);
        }
    }

    public static void frozenSnowballHit(float x, float y, float z) {
        for (double i=0; i<=360; i+=30) {
            Float x_ = (float) Math.cos(Math.toRadians(i));
            Float z_ = (float) Math.sin(Math.toRadians(i));
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_NORMAL,true, x+2*x_, y+2, z+2*z_, 0, 0, 0, 0, 1);
            for(Player online : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    public static void goldenTrack(float x, float y, float z) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE,true, x+1, y+1, z+1, 0, 0, 0, 0, 1);
        PacketPlayOutWorldParticles packet1 = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE,true, x+1, y+1, z-1, 0, 0, 0, 0, 1);
        PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE,true, x-1, y+1, z-1, 0, 0, 0, 0, 1);
        PacketPlayOutWorldParticles packet3 = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE,true, x-1, y+1, z+1, 0, 0, 0, 0, 1);
        for(Player online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet1);
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet2);
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet3);
        }
    }

    public static void magicClockComeback(float x, float y, float z) {
        for (float i=0; i<2; i+=0.1) {
            goldenTrack(x, y-1+i, z);
        }
    }

    public static void LargeExplosion(float x, float y, float z) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_HUGE,true, x+1, y+1, z+1, 0, 0, 0, 0, 1);
        for(Player online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static void Circle(float x, float y, float z, float radius, Player player) {
        Collection<Entity> near = player.getWorld().getNearbyEntities(new Location(player.getWorld(), x, y, z), 100, 100, 100);
        for (double i=0; i<360; i+=6) {
            float x_ = (float)Math.cos(Math.toRadians(i));
            float z_ = (float)Math.sin(Math.toRadians(i));
            for (Entity e: near) {
                if (e instanceof Player)
                    ((Player)e).playEffect(new Location(player.getWorld(), x+radius*x_, y, z+radius*z_), Effect.COLOURED_DUST, null);
            }
        }
    }
}
