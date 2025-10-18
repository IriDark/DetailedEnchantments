package github.iri.anotherday.client.model;

import com.mojang.blaze3d.vertex.*;
import github.iri.anotherday.client.animations.*;
import github.iri.anotherday.registries.entities.*;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.*;

public class SquirrelModel<T extends Squirrel> extends HierarchicalModel<T>{
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart rightEar;
	private final ModelPart leftEar;
	private final ModelPart tail;
	private final ModelPart rightLeg;
	private final ModelPart rightFoot;
	private final ModelPart leftLeg;
	private final ModelPart leftFoot;
	private final ModelPart rightLeg2;
	private final ModelPart group;
	private final ModelPart leftLeg2;
	private final ModelPart group2;

	public SquirrelModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.head = this.body.getChild("head");
		this.rightEar = this.head.getChild("rightEar");
		this.leftEar = this.head.getChild("leftEar");
		this.tail = this.body.getChild("tail");
		this.rightLeg = this.body.getChild("rightLeg");
		this.rightFoot = this.rightLeg.getChild("rightFoot");
		this.leftLeg = this.body.getChild("leftLeg");
		this.leftFoot = this.leftLeg.getChild("leftFoot");
		this.rightLeg2 = this.root.getChild("rightLeg2");
		this.group = this.rightLeg2.getChild("group");
		this.leftLeg2 = this.root.getChild("leftLeg2");
		this.group2 = this.leftLeg2.getChild("group2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 1.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -3.0F, -5.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(2.0F, -3.0F, -5.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).mirror().addBox(-4.0F, -3.0F, -5.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -2.0F, -7.0F));

		PartDefinition rightEar = head.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(0, -2).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -3.0F, 0.0F));

		PartDefinition leftEar = head.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(0, -2).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -3.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, 14).addBox(-2.0F, -6.5F, 0.5F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(24, 25).addBox(-2.0F, -6.5F, 3.5F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 1.5F));

		PartDefinition rightLeg = body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(8, 32).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 2.0F, -5.0F));

		PartDefinition rightFoot = rightLeg.addOrReplaceChild("rightFoot", CubeListBuilder.create().texOffs(5, 35).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));

		PartDefinition leftLeg = body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(8, 32).mirror().addBox(-0.5F, 0.0F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.5F, 2.0F, -5.0F));

		PartDefinition leftFoot = leftLeg.addOrReplaceChild("leftFoot", CubeListBuilder.create().texOffs(5, 35).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 3.0F, 0.0F));

		PartDefinition rightLeg2 = root.addOrReplaceChild("rightLeg2", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -3.0F, 2.0F));

		PartDefinition group = rightLeg2.addOrReplaceChild("group", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -1.0F));

		PartDefinition leftLeg2 = root.addOrReplaceChild("leftLeg2", CubeListBuilder.create().texOffs(0, 24).mirror().addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -3.0F, 2.0F));

		PartDefinition group2 = leftLeg2.addOrReplaceChild("group2", CubeListBuilder.create().texOffs(28, 0).mirror().addBox(-1.0F, 0.0F, -3.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 3.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Squirrel pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xScale = 1 + Mth.cos(pAgeInTicks * 0.5f) / 65.0F;
        this.head.zScale = 1 + Mth.cos(pAgeInTicks * 0.5f) / 65.0F;

        this.tail.xRot = Mth.cos(pAgeInTicks * 0.025F) / 5.0F;
        this.body.xRot = Mth.cos(pAgeInTicks * 0.015F) / 30.0F;

        this.animateWalk(SquirrelAnimations.Walk, pLimbSwing, pLimbSwingAmount, pEntity.isBaby() ? 0.5f : 2, pAgeInTicks);
        if(pEntity.onClimbable()) this.animateWalk(SquirrelAnimations.Climb, pLimbSwing, pLimbSwingAmount, 2, pAgeInTicks);
	}

    @Override
    public ModelPart root(){
        return this.root;
    }
}