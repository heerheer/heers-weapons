package top.heerdev.heersweapons;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface UsingTickable {

    void tick(Level pLevel, LivingEntity player, int tick);

}
