package com.sandymandy.pleasurecraft.item.items;

import com.sandymandy.pleasurecraft.entity.base.tamable.SettlementGirlEntityAI;
import com.sandymandy.pleasurecraft.settlement.Settlement;
import com.sandymandy.pleasurecraft.util.managers.SettlementManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;

public class SettlementRecruitmentToken extends Item {

    public SettlementRecruitmentToken(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(user.getWorld().isClient()) return ActionResult.SUCCESS;

        if(!(entity instanceof SettlementGirlEntityAI girl)) {
            user.sendMessage(Text.translatable("item.pleasurecraft.settlement_recruitment_token.use_on_entity_invalid_entity").formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        if(!girl.isOwner(user)){
            user.sendMessage(Text.translatable("item.pleasurecraft.settlement_recruitment_token.use_on_entity_is_not_owner").formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        UUID settlementId = getSettlementId(stack);
        if(settlementId == null) {
            user.sendMessage(Text.translatable("item.pleasurecraft.settlement_recruitment_token.use_on_entity_settlement_no_assigned_overlay").formatted(Formatting.RED), true);
            user.sendMessage(Text.translatable("item.pleasurecraft.settlement_recruitment_token.use_on_entity_settlement_no_assigned_chat"), false);
            return ActionResult.FAIL;
        }

        ServerWorld world = (ServerWorld) user.getWorld();
        SettlementManager manager = SettlementManager.get(world);
        Settlement settlement = manager.getSettlement(settlementId);

        if(settlement == null){
            user.sendMessage(Text.translatable("item.pleasurecraft.settlement_recruitment_token.use_on_entity_settlement_no_longer_exists").formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        if (girl.hasSettlement()) {
            if(settlement.getId().equals(girl.getSettlement().getId())) {
                user.sendMessage(Text.literal(girl.getGirlDisplayName() + " is already in " + settlement.getName() + "!").formatted(Formatting.YELLOW), true);
                return ActionResult.FAIL;
            }

            Settlement oldSettlement = girl.getSettlement();
            oldSettlement.removeMember(girl);
            user.sendMessage(Text.literal(girl.getGirlDisplayName() + " left " + oldSettlement.getName()).formatted(Formatting.GRAY), false);
        }

        settlement.addMember(girl);
        manager.markDirty();

        user.sendMessage(Text.literal(girl.getGirlDisplayName() + " joined " + settlement.getName() + "!").formatted(Formatting.GREEN), true);

        stack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    public static UUID getSettlementId(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component != null && component.contains("SettlementId")) {
            Optional<UUID> id = component.copyNbt().get("SettlementId", Uuids.CODEC);
            if(id.isPresent()) {
                return id.get();
            }
        }
        return null;
    }

    public static void setSettlementInfo(ItemStack stack, UUID settlementId){
        NbtCompound nbt = new NbtCompound();
        nbt.put("SettlementId", Uuids.CODEC, settlementId);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

}
