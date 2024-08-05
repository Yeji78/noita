package org.yeji778.noita;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Core  {

    private float distance;
    private final int frequency;
    private final double angle;
    private final Player player;
    private Particle particle;
    private final int particleCount;
    private final Staffs staffs;
    private static final double DEFAULT_DAMAGE_RADIUS = 1.5; // 默认的粒子碰撞箱半径
    private static final double DEFAULT_DAMAGE = 5.0; // 默认的伤害值

    // 构造函数，初始化粒子效果的相关参数
    public Core(float distance, int frequency, double angle, Staffs staffs) {
        this.distance = distance;
        this.frequency = frequency;
        this.angle = angle;
        this.particle = staffs.getParticle();
        this.particleCount = staffs.getParticleCount();
        this.player = staffs.getPlayer();
        this.staffs = staffs;
    }

    // 获取粒子效果的发射频率
    public int getFrequency() {
        return frequency;
    }

    // 获取粒子的发射角度
    public double getAngle() {
        return angle;
    }

    // 获取粒子的距离
    public float getDistance() {
        return distance;
    }

    // 设置粒子类型
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    // 设置粒子的距离
    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void fires(List<Location> startLocations, Vector baseDirection) {
        Location eyeLocation = player.getEyeLocation().clone();

        // 确定初始粒子发射位置
        List<Location> initialLocations = (startLocations != null && !startLocations.isEmpty()) ? startLocations : Arrays.asList(eyeLocation);

        // 计算发射方向
        Vector[] directions = new Vector[frequency];
        for (int i = 0; i < frequency; i++) {
            double currentAngle = Math.toRadians((i - (frequency - 1) / 2.0) * angle);
            directions[i] = rotateVector(baseDirection, currentAngle);
        }

        double interval = distance / particleCount;
        List<Location> finalLocations = new ArrayList<>();

        // 异步任务发射粒子
        Bukkit.getScheduler().runTaskAsynchronously(Noita.getInstance(), () -> {
            for (Location initialLocation : initialLocations) {
                for (int i = 0; i < particleCount; i++) {
                    double distanceTravelled = interval * i;
                    for (Vector direction : directions) {
                        Location particleLocation = initialLocation.clone().add(direction.clone().normalize().multiply(distanceTravelled));

                        // 创建具有碰撞检测的粒子，使用默认的碰撞箱半径和伤害值
                        HitboxParticle hitboxParticle = new HitboxParticle(player, particle, DEFAULT_DAMAGE_RADIUS, DEFAULT_DAMAGE);
                        hitboxParticle.showParticle(particleLocation, direction);
                    }
                }

                // 计算最后的粒子位置
                for (Vector direction : directions) {
                    finalLocations.add(initialLocation.clone().add(direction.clone().normalize().multiply(distance)));
                }
            }

            // 更新最后的粒子位置
            staffs.updateLastParticleLocations(finalLocations, directions);
        });
    }


    // 旋转向量，用于计算粒子发射方向
    private Vector rotateVector(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = vector.getX() * cos - vector.getZ() * sin;
        double z = vector.getX() * sin + vector.getZ() * cos;
        return new Vector(x, vector.getY(), z);
    }

    // 获取垂直方向的向量
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
