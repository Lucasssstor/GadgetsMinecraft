package fr.latvdemomo.pluginonecube.PPlayer;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class PServer {
    private boolean lockedWeather = true;
    private Set<PPlayer> pPlayers = new HashSet<>();
    
    public PPlayer getPPlayer(Player p) {
        for (PPlayer pPlayer: this.pPlayers) {
            if (pPlayer.getPlayer().getUniqueId().equals(p.getUniqueId())) return pPlayer;
        }
        return null;
    }

    public boolean getWeatherLock() {
        return this.lockedWeather;
    }

    public Set<PPlayer> getPPlayers() {
        return this.pPlayers;
    }

    public PPlayer getPPlayer(String playerName) {
        for (PPlayer pPlayer: this.pPlayers) {
            if (pPlayer.getPlayer().getName().equals(playerName)) return pPlayer;
        }
        return null;
    }

    public void addPPlayer(PPlayer pPlayer) {
        if (!this.pPlayers.contains(pPlayer)) pPlayers.add(pPlayer);
    }
}
