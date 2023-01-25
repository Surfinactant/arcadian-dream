/*
 * Copyright (c) 2022-2023 Maxmani and contributors.
 * Licensed under the EUPL-1.2 or later.
 */

package net.reimaden.arcadiandream.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.reimaden.arcadiandream.ArcadianDream;
import net.reimaden.arcadiandream.block.ModBlocks;
import net.reimaden.arcadiandream.compat.IRitualCraftingLocations;
import net.reimaden.arcadiandream.recipe.RitualCraftingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JEIRitualCraftingCategory implements IRecipeCategory<RitualCraftingRecipe>, IRitualCraftingLocations {

    private final IGuiHelper guiHelper;

    public static final Identifier UID = new Identifier(ArcadianDream.MOD_ID, "ritual_crafting");
    public static final Identifier TEXTURE = new Identifier(ArcadianDream.MOD_ID, "textures/gui/ritual_crafting.png");
    private static final Identifier MOON_ICON = new Identifier(ArcadianDream.MOD_ID, "textures/gui/moon.png");
    private static final Identifier DIMENSION_ICON = new Identifier(ArcadianDream.MOD_ID, "textures/gui/dimension.png");
    public static final Text TITLE = Text.translatable("arcadiandream.category.ritual_crafting");

    private final IDrawable background;
    private final IDrawable icon;

    public JEIRitualCraftingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.drawableBuilder(TEXTURE, 0, 0, 184, 184).setTextureSize(184, 184).build();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.RITUAL_SHRINE));
    }

    @Override
    public @NotNull RecipeType<RitualCraftingRecipe> getRecipeType() {
        return JEIPlugin.RITUAL_CRAFTING_TYPE;
    }

    @Override
    public @NotNull Text getTitle() {
        return TITLE;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, RitualCraftingRecipe recipe, @NotNull IFocusGroup focuses) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, ONBASHIRAS[i][0], ONBASHIRAS[i][1]).addIngredients(recipe.getIngredients().get(i));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT[0], OUTPUT_SLOT[1]).addItemStack(recipe.getOutput());
    }

    private Text moonPhaseTooltip(byte moonPhase) {
        Text tooltip;
        switch (moonPhase) {
            case 0 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_0");
            case 1 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_1");
            case 2 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_2");
            case 3 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_3");
            case 4 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_4");
            case 5 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + "..ritual_crafting.moon_phase_5");
            case 6 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_6");
            case 7 -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_7");
            default -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.moon_phase_invalid");
        }

        return Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.requirement", tooltip);
    }

    private Text dimensionTooltip(String dimension) {
        Text tooltip;
        switch (dimension) {
            case "overworld" -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.dimension_overworld");
            case "the_nether" -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.dimension_nether");
            case "the_end" -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.dimension_end");
            default -> tooltip = Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.dimension_unknown", dimension);
        }

        return Text.translatable(ArcadianDream.MOD_ID + ".ritual_crafting.requirement", tooltip);
    }

    @Override
    public void draw(RitualCraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
        IDrawable moon_icon = guiHelper.drawableBuilder(MOON_ICON, 0, 0, 16, 16).setTextureSize(16, 16).build();
        IDrawable dimension_icon = guiHelper.drawableBuilder(DIMENSION_ICON, 0, 0, 16, 16).setTextureSize(16, 16).build();

        if (getMoonPhase(recipe)) {
            moon_icon.draw(stack, MOON_SLOT[0], MOON_SLOT[1]);
        } else if (getDimension(recipe)) {
            dimension_icon.draw(stack, DIMENSION_SLOT[0], DIMENSION_SLOT[1]);
        }
    }

    @Override
    public @NotNull List<Text> getTooltipStrings(RitualCraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Text> tooltip = new ArrayList<>();

        if (getMoonPhase(recipe)) {
            if (mouseX >= MOON_SLOT[0] && mouseX <= MOON_SLOT[0] + 16 && mouseY >= MOON_SLOT[1] && mouseY <= MOON_SLOT[1] + 16) {
                tooltip.add(moonPhaseTooltip(recipe.getMoonPhase()));
            }
        } else if (getDimension(recipe)) {
            if (mouseX >= DIMENSION_SLOT[0] && mouseX <= DIMENSION_SLOT[0] + 16 && mouseY >= DIMENSION_SLOT[1] && mouseY <= DIMENSION_SLOT[1] + 16) {
                tooltip.add(dimensionTooltip(recipe.getDimension()));
            }
        }

        return tooltip;
    }

    private static boolean getDimension(RitualCraftingRecipe recipe) {
        return !recipe.getDimension().isEmpty();
    }

    private static boolean getMoonPhase(RitualCraftingRecipe recipe) {
        return recipe.getMoonPhase() != -1;
    }
}
