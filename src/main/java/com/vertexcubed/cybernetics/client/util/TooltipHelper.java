package com.vertexcubed.cybernetics.client.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class TooltipHelper {

    /**
     * Formats tooltips that use "_" as means of highlighting text. Also wraps it with a certain length given.
     */
    public static List<Component> processTooltip(MutableComponent in, ChatFormatting primaryColor, ChatFormatting secondaryColor, int length) {
        String[] splits = in.getString().split("\n");
        List<String> text = new ArrayList<>();
        for (String split : splits) {
            text.addAll(List.of(WordUtils.wrap(split, length).split("\n"))); //TODO: write custom wrapper to prevent _Highlighted text_ being broken up
        }


        List<Component> out = new ArrayList<>();


        boolean addUnderscoreToBeginning = false;
        for(String line : text) {
            int carriageIdx = line.indexOf("\r"); // chop off carriage return characters on windows
            if(carriageIdx != -1) line = line.substring(0, carriageIdx);
            if(addUnderscoreToBeginning) {
                line = "_" + line;
                addUnderscoreToBeginning = false;
            }
            int numUnderscores = 0;
            for(int i = 0; i < line.length(); i++) {
                if(line.charAt(i) == '_') numUnderscores++;
            }

            String[] parts = line.split("_");
            if(numUnderscores % 2 != 0) {
                addUnderscoreToBeginning = true;
            }
            MutableComponent lineComponent = Component.empty();
            for(int i = 0; i < parts.length; i++) {
                lineComponent.append(Component.literal(parts[i]).withStyle(i % 2 == 0 ? primaryColor : secondaryColor));
            }
            out.add(lineComponent);
        }

        return out;
    }

    //This should probably only be called on the client
    public static Component getDisplayNameList(ItemStack[] stacks) {
        long gameTimeSeconds = Minecraft.getInstance().level.getGameTime() / 20;
        int index = (int) (gameTimeSeconds % stacks.length);
        return stacks[index].getHoverName();
    }
}