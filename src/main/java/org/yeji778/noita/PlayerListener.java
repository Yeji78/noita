package org.yeji778.noita;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    static long startTime;
    static Player player1;
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        player1 = player;

        if (item != null && item.getType() == Material.STICK) {
            startTime = System.currentTimeMillis();
            Staffs staffs = getStaffsForPlayer(player); // 需要实现此方法获取玩家对应的 Staffs 实例
            staffs.useNextCore();

        }
    }


    private Staffs getStaffsForPlayer(Player player) {
        Staffs staffs = new Staffs(player, Particle.FLAME, 10.0f, 10);
        Core core1 = new Core(2.0f, 3, 45, staffs);
        Core core3 = new Core(5.0f, 2, 10, staffs);
        Core core4 = new Core(5.0f, 7, 3, staffs);
        staffs.setCores(core1);
        staffs.setCores(core3);
        staffs.setCores(core4);
        return staffs;
    }
}



