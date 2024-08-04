package org.yeji778.noita;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketCore {
    private float distance;
    private final int frequency;
    private final double angle;
    private final Player player;
    private EnumWrappers.Particle particle;
    private final int particleCount;
    private final Staffs staffs;
    private final ProtocolManager protocolManager;

    public PacketCore(float distance, int frequency, double angle, Staffs staffs) {
        this.distance = distance;
        this.frequency = frequency;
        this.angle = angle;
        this.particle = EnumWrappers.Particle.valueOf(staffs.getParticle().name());
        this.particleCount = staffs.getParticleCount();
        this.player = staffs.getPlayer();
        this.staffs = staffs;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public int getFrequency() {
        return frequency;
    }

    public double getAngle() {
        return angle;
    }

    public float getDistance() {
        return distance;
    }

    public void setParticle(EnumWrappers.Particle particle) {
        this.particle = particle;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void fires(List<Location> startLocations, Vector baseDirection) {
        List<Location> initialLocations = (startLocations != null && !startLocations.isEmpty()) ? startLocations : Arrays.asList(player.getEyeLocation());

        Vector[] directions = new Vector[frequency];
        for (int i = 0; i < frequency; i++) {
            double currentAngle = Math.toRadians((i - (frequency - 1) / 2.0) * angle);
            directions[i] = rotateVector(baseDirection, currentAngle);
        }

        double interval = distance / particleCount;
        List<Location> finalLocations = new ArrayList<>();

        Bukkit.getScheduler().runTaskAsynchronously(Noita.getInstance(), () -> {
            List<PacketContainer> packets = new ArrayList<>();
            for (Location initialLocation : initialLocations) {
                for (int i = 0; i < particleCount; i++) {
                    for (Vector particleDirection : directions) {
                        Location particleLocation = initialLocation.clone().add(particleDirection.clone().normalize().multiply(interval * i));
                        packets.add(createParticlePacket(particleLocation, particleDirection));
                    }
                }
                for (Vector particleDirection : directions) {
                    finalLocations.add(initialLocation.clone().add(particleDirection.clone().normalize().multiply(interval * particleCount)));
                }
            }
            sendPackets(player, packets);
            staffs.updateLastParticleLocations(finalLocations, directions);
        });
    }

    private Vector rotateVector(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = vector.getX() * cos - vector.getZ() * sin;
        double z = vector.getX() * sin + vector.getZ() * cos;
        return new Vector(x, vector.getY(), z);
    }

    private PacketContainer createParticlePacket(Location location, Vector direction) {
        PacketContainer packet = protocolManager.createPacket(com.comphenix.protocol.PacketType.Play.Server.WORLD_PARTICLES);
        packet.getParticles().write(0, particle);
        packet.getFloat().write(0, (float) location.getX());
        packet.getFloat().write(1, (float) location.getY());
        packet.getFloat().write(2, (float) location.getZ());
        packet.getFloat().write(3, (float) direction.getX());
        packet.getFloat().write(4, (float) direction.getY());
        packet.getFloat().write(5, (float) direction.getZ());
        packet.getFloat().write(6, 0.1f);
        packet.getIntegers().write(0, 0);
        return packet;
    }

    private void sendPackets(Player player, List<PacketContainer> packets) {
        try {
            for (PacketContainer packet : packets) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector getPerpendicularEndDirection(List<Location> lastParticleLocations) {
        if (lastParticleLocations == null || lastParticleLocations.isEmpty()) {
            return player.getEyeLocation().getDirection();
        }

        Location start = lastParticleLocations.get(0);
        Location end = lastParticleLocations.get(lastParticleLocations.size() - 1);
        Vector endDirection = end.toVector().subtract(start.toVector()).normalize();
        return new Vector(endDirection.getZ(), endDirection.getY(), -endDirection.getX()).normalize();
    }
}
