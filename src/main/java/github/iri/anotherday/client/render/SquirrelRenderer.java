package github.iri.anotherday.client.render;

import com.mojang.blaze3d.vertex.*;
import github.iri.anotherday.*;
import github.iri.anotherday.client.model.*;
import github.iri.anotherday.registries.entities.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.*;
import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class SquirrelRenderer extends MobRenderer<Squirrel, SquirrelModel<Squirrel>>{
    protected static final ResourceLocation TEXTURE = AnotherDay.loc("textures/entity/squirrel.png");

    public SquirrelRenderer(EntityRendererProvider.Context context){
        super(context, new SquirrelModel<>(SquirrelModel.createBodyLayer().bakeRoot()), 0.35F);
    }

    @Override
    public void render(Squirrel pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight){
        if(pEntity.isBaby()){
            pMatrixStack.scale(0.7f, 0.7f, 0.7f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Squirrel pEntity){
        return TEXTURE;
    }
}