package com.sandymandy.pleasurecraft.screen;

import com.sandymandy.pleasurecraft.networking.C2S.InventoryButtonC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

import java.util.List;

public class InventoryButtonRegistry {
    public static final List<InventoryButtonAction> BUTTONS_LEFT = List.of(
            new InventoryButtonAction(Text.literal("Break Up"), 0,(girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "breakUp"));
            }),

            new InventoryButtonAction(Text.literal("Sound Test"), 1,(girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "testSound"));
            }),

            new InventoryButtonAction(Text.literal("Set Base Here"), 1,(girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "setBase"));
            }),

            new InventoryButtonAction(Text.literal("Go To Base"), 1,(girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "goToBase"));
            })
    );

    public static final List<InventoryButtonAction> BUTTONS_RIGHT = List.of(

            new InventoryButtonAction(Text.literal("Sit"), 2,(girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "sit"));
            }),

            new InventoryButtonAction(Text.literal("Follow Me"), 3,(girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "follow"));
            }),

            new InventoryButtonAction(Text.literal("Strip"), 4,(girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "stripOrDressup"));
            }),

            new InventoryButtonAction(Text.literal("Talk"),4 , (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "talk"));
            })
//            new InventoryButtonAction(Text.literal("Test Anim 1"), 0,(girl, player) -> {
//                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "testAnim1"));
//            })
    );
}