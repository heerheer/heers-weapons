package top.heerdev.heersweapons.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.heerdev.heersweapons.BelVethMercy;
import top.heerdev.heersweapons.HeersWeapons;

public class EntityDeathEventHandler {
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        Entity source = event.getSource().getEntity();

        if (source instanceof Player player && (entity instanceof Enemy)) {

            // 获取玩家使用的物品
            ItemStack itemInMainHand = player.getMainHandItem();

            if (itemInMainHand.getItem() instanceof BelVethMercy mercy) {

                mercy.addPurpleLevel(itemInMainHand, 1);
            }
        }
    }
    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        Entity source = event.getSource().getEntity();

        if (source instanceof Player player && (entity instanceof Enemy)) {

            // 获取玩家使用的物品
            ItemStack itemInMainHand = player.getMainHandItem();
            // 替换 "BelVethMercy" 为你的物品的实际名称或注册名
            if (itemInMainHand.getItem() instanceof BelVethMercy mercy) { // 这里仅为示例，请替换为你的物品

                mercy.addPurpleLevel(itemInMainHand, 1);
            }
        }
    }
}
