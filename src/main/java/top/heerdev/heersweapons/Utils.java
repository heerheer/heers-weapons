package top.heerdev.heersweapons;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static List<LivingEntity> getNearbyMonsters(Level world, BlockPos origin) {
        return world.getEntitiesOfClass(LivingEntity.class, new AABB(origin).inflate(3, 3, 3), livingEntity -> livingEntity instanceof Enemy || livingEntity instanceof Mob);
    }

    public static <T> T random(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
