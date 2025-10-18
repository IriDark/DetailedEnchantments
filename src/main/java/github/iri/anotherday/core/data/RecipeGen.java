package github.iri.anotherday.core.data;

import net.minecraft.data.*;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraftforge.common.crafting.conditions.*;
import org.jetbrains.annotations.*;

import java.util.function.*;

public class RecipeGen extends RecipeProvider implements IConditionBuilder{

    public RecipeGen(PackOutput pOutput){
        super(pOutput);
    }

    public static void stairs(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pStairs, ItemLike pMaterial){
        stairBuilder(pStairs, Ingredient.of(pMaterial)).unlockedBy(getHasName(pMaterial), has(pMaterial)).save(pFinishedRecipeConsumer);
    }

    public static void fence(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pFence, ItemLike pMaterial){
        fenceBuilder(pFence, Ingredient.of(pMaterial)).unlockedBy(getHasName(pMaterial), has(pMaterial)).save(pFinishedRecipeConsumer);
    }

    public static void fenceGate(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pFenceGate, ItemLike pMaterial){
        fenceGateBuilder(pFenceGate, Ingredient.of(pMaterial)).unlockedBy(getHasName(pMaterial), has(pMaterial)).save(pFinishedRecipeConsumer);
    }

    public static void cutterResultFromBase(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeCategory pCategory, ItemLike pResult, ItemLike pMaterial, int pCount){
        stonecutterResultFromBase(pFinishedRecipeConsumer, pCategory, pResult, pMaterial, pCount);
    }

    @Override
    public void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter){

    }
}