package org.yeji778.noita;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static org.yeji778.noita.PlayerListener.startTime;

public class Staffs {
    private final Player player;
    private Particle particle;
    private float maxDistance;
    private int numberOfCores;
    private int particleCount = 50;
    private final Core[] cores;
    private int currentCoreIndex = 0;
    private List<Location> lastParticleLocations;
    private Vector lastEndDirection;

    public Staffs(Player player, Particle particle, float maxDistance, int numberOfCores) {
        this.player = player;
        this.particle = particle;
        this.maxDistance = maxDistance;
        this.numberOfCores = numberOfCores;
        this.cores = new Core[numberOfCores];
        this.lastParticleLocations = new ArrayList<>();
        this.lastEndDirection = player.getEyeLocation().getDirection();
    }

    public void setCores(Core inCore) {
        for (int i = 0; i < numberOfCores; i++) {
            if (cores[i] == null) {
                cores[i] = inCore;
                return;
            }
        }
        player.sendMessage("法杖核心位已满");
    }

    public void useNextCore() {
        if (currentCoreIndex < numberOfCores) {
            Core currentCore = cores[currentCoreIndex];
            if (currentCore != null) {
                Vector endDirection = currentCoreIndex > 0 ? cores[currentCoreIndex - 1].getPerpendicularEndDirection(lastParticleLocations) : lastEndDirection;
                currentCore.fires(lastParticleLocations, endDirection);
                lastEndDirection = endDirection; // 更新最后的方向
            } else {
                currentCoreIndex++;
                useNextCore();
            }
        } else {
            currentCoreIndex = 0;
            long elapsedTimeMillis= System.currentTimeMillis() - startTime;
            double elapsedTimeSeconds = elapsedTimeMillis / 1000.0;
            player.sendMessage(String.format("%.6f 秒", elapsedTimeSeconds));
        }
    }
    public void updateLastParticleLocations(List<Location> locations, Vector[] endDirections) {
        this.lastParticleLocations = (locations != null) ? new ArrayList<>(locations) : new ArrayList<>();
        if (endDirections != null && endDirections.length > 0) {
            this.lastEndDirection = endDirections[endDirections.length - 1]; // 更新最后的方向
        }
        currentCoreIndex++;
        useNextCore();
    }

    public Player getPlayer() {
        return player;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public void setParticleCount(int particleCount) {
        this.particleCount = particleCount;
    }

    public int getNumberOfCores() {
        return numberOfCores;
    }

    public void setNumberOfCores(int numberOfCores) {
        this.numberOfCores = numberOfCores;
    }

    public Core[] getCores() {
        return cores;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }
}
