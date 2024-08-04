package org.yeji778.noita;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class HitboxParticle {

    private final Player player;
    private final Particle particle;
    private final double damageRadius;  // 伤害半径
    private final double damage;  // 伤害量

    public HitboxParticle(Player player, Particle particle, double damageRadius, double damage) {
        this.player = player;
        this.particle = particle;
        this.damageRadius = damageRadius;
        this.damage = damage;
    }

    // 显示粒子并检查碰撞
    public void showParticle(Location location, Vector direction) {
        // 显示粒子
        player.getWorld().spawnParticle(particle, location, 0, direction.getX(), direction.getY(), direction.getZ(), 0.1);
        // 检查碰撞
        Location clone = location.clone();
        Bukkit.getScheduler().runTask(Noita.getInstance(), () -> {
            Collection<Entity> nearbyEntities = clone.getWorld().getNearbyEntities(clone, damageRadius, damageRadius, damageRadius);
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity && entity != player) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    clone.setY(clone.getY()+1);
                    if (livingEntity.getLocation().distanceSquared(clone) <= damageRadius * damageRadius) {
                        livingEntity.damage(damage, player);
                    }
                }
            }
        });
    }
}
