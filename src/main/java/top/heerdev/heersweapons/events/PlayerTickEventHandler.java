package top.heerdev.heersweapons.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.heerdev.heersweapons.BelVethMercy;
import top.heerdev.heersweapons.UsingTickable;
import top.heerdev.heersweapons.Utils;

import java.util.List;

public class PlayerTickEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            Player player = event.player;

            if(player.getServer().getTickCount() % 20 ==0){
                //System.out.println("tick: " + player.getServer().getTickCount() + player.isUsingItem());
            }

            if ( player.getMainHandItem().getItem() instanceof UsingTickable tickable) {
                tickable.tick(player.level(), player, player.getServer().getTickCount());
            }
        }
    }
}
