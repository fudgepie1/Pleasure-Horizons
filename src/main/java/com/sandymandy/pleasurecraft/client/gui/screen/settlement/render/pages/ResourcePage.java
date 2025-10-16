package com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.pages;

import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.SettlementPage;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets.IconButtonComponent;
import com.sandymandy.pleasurecraft.client.gui.screen.settlement.render.componets.LabelComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class ResourcePage extends SettlementPage {
    public ResourcePage(){
                addComponent(new SettlementPage()
                        .addComponent(new LabelComponent(20, 20, Text.literal("Zoom me!")))
                        .addComponent(new IconButtonComponent(120, 60, new ItemStack(Items.DIAMOND),
                                btn -> client.player.sendMessage(Text.literal("Clicked diamond!"), false))));
    }

    @Override
    public void tick() {
        super.tick();
    }
}
