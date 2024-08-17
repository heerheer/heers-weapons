package top.heerdev.heersweapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

public class BelVethMercy extends Item implements UsingTickable {

    public static final String ID = "belveth_mercy";

    private int BASE_ATTACK_DAMAGE = 8;
    private float BASE_ATTACK_SPEED = -2.4f;
    private int attackDamage = BASE_ATTACK_DAMAGE;
    private float attackSpeed = BASE_ATTACK_SPEED;

    private int leftTimes = 0;


    private Multimap<Attribute, AttributeModifier> defaultModifiers;

    public BelVethMercy(Properties pProperties) {
        super(pProperties.defaultDurability(0));
    }

    // 获取当前紫化度
    public int getPurpleLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt("purple") : 0;
    }

    // 增加武器紫化度
    public void addPurpleLevel(ItemStack stack, int change) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("purple", getPurpleLevel(stack) + change);
        this.attackSpeed = BASE_ATTACK_SPEED + 0.01f * getPurpleLevel(stack);
    }

    // 获取技能可以斩击的次数
    private int getAttackCounts(ItemStack pStack) {
        return 10 + this.getPurpleLevel(pStack) / 20;
    }



    // 获取一个living被攻击应该造成的伤害
    public int getExtraDamageOnLiving(LivingEntity entity) {
        CompoundTag nbt = entity.getPersistentData();
        int times = nbt.getCompound("heersweapons").getInt("belveth_mercy");
        System.out.println("extra times：" + times);
        return times / 2;
    }

    // 更新一个生物对于belveth的额外伤害次数
    public void updateLiving(LivingEntity entity) {
        CompoundTag nbt = entity.getPersistentData();
        int times = nbt.getCompound("heersweapons").getInt("belveth_mercy");
        if(times == 0){
            CompoundTag tag = new CompoundTag();
            tag.putInt("belveth_mercy", 1);
            nbt.put("heersweapons", tag);
        }else{
            nbt.getCompound("heersweapons").putInt("belveth_mercy", times + 1);
        }
        entity.save(nbt);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        if (pLevel.isClientSide()) {
            return super.use(pLevel, pPlayer, pUsedHand);
        }

        if (pUsedHand == InteractionHand.MAIN_HAND) {
            if (!pPlayer.getCooldowns().isOnCooldown(this)) {

                pPlayer.getCooldowns().addCooldown(this, 20 * 10);
                leftTimes = getAttackCounts(pPlayer.getItemInHand(pUsedHand));
                System.out.println("一共攻击:" + leftTimes);
            }
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }


    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double) attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return this.attackDamage;
    }


    @Override
    public int getUseDuration(ItemStack pStack) {
        return 2 * this.getAttackCounts(pStack);
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }


    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        return super.getTooltipImage(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.translatable("item.heersweapons.belveth_mercy.description").withStyle(ChatFormatting.GRAY));

        for (int i = 1; i < 4; i++) {
            pTooltipComponents.add(Component.translatable("item.heersweapons.belveth_mercy.skill." + i + ".title").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));

            if (Screen.hasShiftDown())
                pTooltipComponents.add(Component.translatable("item.heersweapons.belveth_mercy.skill." + i + ".description",
                        Component.literal(String.valueOf(getAttackCounts(pStack))).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)
                ).withStyle(ChatFormatting.WHITE));
            else
                pTooltipComponents.add(Component.translatable("item.heersweapons.belveth_mercy.skill." + i + ".description_simplified"
                        ,Component.literal(String.valueOf(getAttackCounts(pStack))).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)
                ).withStyle(ChatFormatting.WHITE));
        }
        pTooltipComponents.add(Component.empty());
        pTooltipComponents.add(Component.translatable("item.heersweapons.belveth_mercy.purple", getPurpleLevel(pStack), Component.literal(String.valueOf(getPurpleLevel(pStack))).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public void tick(Level pLevel, LivingEntity player, int tick) {
        if (tick % 2 != 0 || leftTimes <= 0) {
            return;
        }

        List<LivingEntity> monsters = Utils.getNearbyMonsters(pLevel, player.blockPosition());
        if (!monsters.isEmpty()) {
            LivingEntity living = Utils.random(monsters);
            DamageSource source = living.damageSources().playerAttack((Player) player);
            int defaultInvulnerableTime = living.invulnerableTime;
            living.invulnerableTime = 0;
            living.hurt(source, 2 + getExtraDamageOnLiving(living));
            updateLiving(living); // 增加攻击计数，每2次会额外造成1点伤害

            //重置无敌时间
            living.invulnerableTime = defaultInvulnerableTime;

        }
        leftTimes--;
    }
}
