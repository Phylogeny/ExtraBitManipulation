package com.phylogeny.extrabitmanipulation.armor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.google.common.primitives.Bytes;
import com.phylogeny.extrabitmanipulation.armor.model.cnpc.CustomNPCsModels;
import com.phylogeny.extrabitmanipulation.armor.model.mpm.MorePlayerModelsModels;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ModelMovingPart;
import com.phylogeny.extrabitmanipulation.reference.CustomNPCsReferences;
import com.phylogeny.extrabitmanipulation.reference.MorePlayerModelsReference;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.nbt.NBTTagCompound;

public class ModelPartConcealer
{
	private Set<ModelMovingPart> concealedParts = new HashSet<>();
	private Set<ModelMovingPart> concealedPartOverlays = new HashSet<>();
	private Set<ModelMovingPart> concealedPartsCombined = new HashSet<>();
	private Map<ModelMovingPart, ModelRenderer> concealedPartRenderers = new HashMap<>();
	
	public ModelPartConcealer() {}
	
	private ModelPartConcealer(byte[] concealedParts, byte[] concealedPartOverlays)
	{
		concealedPartsCombined.addAll(this.concealedParts = indexArrayToPartSet(concealedParts));
		concealedPartsCombined.addAll(this.concealedPartOverlays = indexArrayToPartSet(concealedPartOverlays));
	}
	
	private Set<ModelMovingPart> indexArrayToPartSet(byte[] parts)
	{
		return IntStream.range(0, parts.length).boxed().map(index -> ModelMovingPart.values()[parts[index]]).collect(Collectors.toSet());
	}
	
	public boolean isEmpty()
	{
		return concealedPartsCombined.isEmpty();
	}
	
	public boolean isFull()
	{
		return concealedParts.size() == ModelMovingPart.values().length && concealedPartOverlays.size() == ModelMovingPart.values().length;
	}
	
	private byte[] partsToByteArray(Set<ModelMovingPart> parts)
	{
		return Bytes.toArray(parts.stream().map(part -> (byte) part.ordinal()).collect(Collectors.toSet()));
	}
	
	public void saveToNBT(NBTTagCompound nbt)
	{
		savePartsToNBT(nbt, this.concealedParts, NBTKeys.ARMOR_CONCEALED_MODEL_PARTS);
		savePartsToNBT(nbt, this.concealedPartOverlays, NBTKeys.ARMOR_CONCEALED_MODEL_PART_OVERLAYS);
	}
	
	private void savePartsToNBT(NBTTagCompound nbt, Set<ModelMovingPart> parts, String key)
	{
		byte[] partsArray = partsToByteArray(parts);
		if (partsArray.length > 0)
			nbt.setByteArray(key, partsArray);
		else
			nbt.removeTag(key);
	}
	
	@Nullable
	public static ModelPartConcealer loadFromNBT(NBTTagCompound nbt)
	{
		if (!nbt.hasKey(NBTKeys.ARMOR_CONCEALED_MODEL_PARTS) && !nbt.hasKey(NBTKeys.ARMOR_CONCEALED_MODEL_PART_OVERLAYS))
			return null;
		
		byte[] concealedParts = nbt.getByteArray(NBTKeys.ARMOR_CONCEALED_MODEL_PARTS);
		byte[] concealedPartOverlays = nbt.getByteArray(NBTKeys.ARMOR_CONCEALED_MODEL_PART_OVERLAYS);
		return concealedParts.length > 0 || concealedPartOverlays.length > 0 ? new ModelPartConcealer(concealedParts, concealedPartOverlays).copy() : null;
	}
	
	public void merge(@Nullable ModelPartConcealer modelPartConcealer)
	{
		if (modelPartConcealer != null)
		{
			concealedParts.addAll(modelPartConcealer.concealedParts);
			concealedPartOverlays.addAll(modelPartConcealer.concealedPartOverlays);
			concealedPartsCombined.addAll(modelPartConcealer.concealedPartsCombined);
		}
	}
	
	public ModelPartConcealer copy()
	{
		return new ModelPartConcealer(partsToByteArray(concealedParts), partsToByteArray(concealedPartOverlays));
	}
	
	private Set<ModelMovingPart> getParts(boolean isOverlay)
	{
		return isOverlay ? concealedPartOverlays : concealedParts;
	}
	
	public boolean contains(ModelMovingPart part, boolean isOverlay)
	{
		return getParts(isOverlay).contains(part);
	}
	
	public void addOrRemove(int partIndex, boolean isOverlay, boolean remove)
	{
		ModelMovingPart part = ModelMovingPart.values()[partIndex];
		Set<ModelMovingPart> parts = getParts(isOverlay);
		if (remove)
			parts.remove(part);
		else
			parts.add(part);
	}
	
	public void restoreModelPartVisiblity(ModelBiped model)
	{
		concealedPartRenderers.keySet().forEach(part ->
		{
			ModelRenderer renderer = concealedPartRenderers.get(part);
			switch (part)
			{
				case HEAD:		model.bipedHead = renderer;
								break;
				case BODY:		model.bipedBody = renderer;
								break;
				case ARM_RIGHT:	model.bipedRightArm = renderer;
								break;
				case ARM_LEFT:	model.bipedLeftArm = renderer;
								break;
				case LEG_RIGHT:	model.bipedRightLeg = renderer;
								break;
				case LEG_LEFT:	model.bipedLeftLeg = renderer;
			}
		});
	}
	
	public ModelPartConcealer applyToModel(ModelBiped model)
	{
		concealedParts.forEach(part ->
		{
			switch (part)
			{
				case HEAD:		concealedPartRenderers.put(part, model.bipedHead);
								model.bipedHead = getEmptyModelRenderer(model, model.bipedHead, part);
								break;
				case BODY:		concealedPartRenderers.put(part, model.bipedBody);
								model.bipedBody = getEmptyModelRenderer(model, model.bipedBody, part);
								break;
				case ARM_RIGHT:	concealedPartRenderers.put(part, model.bipedRightArm);
								model.bipedRightArm = getEmptyModelRenderer(model, model.bipedRightArm, part);
								break;
				case ARM_LEFT:	concealedPartRenderers.put(part, model.bipedLeftArm);
								model.bipedLeftArm = getEmptyModelRenderer(model, model.bipedLeftArm, part);
								break;
				case LEG_RIGHT:	concealedPartRenderers.put(part, model.bipedRightLeg);
								model.bipedRightLeg = getEmptyModelRenderer(model, model.bipedRightLeg, part);
								break;
				case LEG_LEFT:	concealedPartRenderers.put(part, model.bipedLeftLeg);
								model.bipedLeftLeg = getEmptyModelRenderer(model, model.bipedLeftLeg, part);
			}
		});
		if (!(model instanceof ModelPlayer))
			return this;
		
		ModelPlayer modelPlayer = (ModelPlayer) model;
		concealedPartOverlays.forEach(part ->
		{
			switch (part)
			{
				case HEAD:		modelPlayer.bipedHeadwear.showModel = false;
								break;
				case BODY:		modelPlayer.bipedBodyWear.showModel = false;
								break;
				case ARM_RIGHT:	modelPlayer.bipedRightArmwear.showModel = false;
								break;
				case ARM_LEFT:	modelPlayer.bipedLeftArmwear.showModel = false;
								break;
				case LEG_RIGHT:	modelPlayer.bipedRightLegwear.showModel = false;
								break;
				case LEG_LEFT:	modelPlayer.bipedLeftLegwear.showModel = false;
			}
		});
		return this;
	}
	
	private ModelRenderer getEmptyModelRenderer(ModelBiped model, ModelRenderer renderer, ModelMovingPart part)
	{
		if (MorePlayerModelsReference.isLoaded && MorePlayerModelsModels.isModelRendererMPM(renderer))
		{
			ModelRenderer modelRenderer = MorePlayerModelsModels.getEmptyModelRenderer(model, renderer, part);
			if (part.ordinal() > 3)
				modelRenderer.rotationPointX += part == ModelMovingPart.LEG_LEFT ? 1.9 : -1.9;
			
			return modelRenderer;
		}
		if (CustomNPCsReferences.isLoaded && CustomNPCsModels.isModelRendererCNPC(renderer))
		{
			ModelRenderer modelRenderer = CustomNPCsModels.getEmptyModelRenderer(model, renderer, part);
			if (part.ordinal() > 3)
				modelRenderer.rotationPointX += part == ModelMovingPart.LEG_LEFT ? 1.9 : -1.9;
			
			return modelRenderer;
		}
		return new ModelRendererEmpty(renderer);
	}
	
	public static class ModelRendererEmpty extends ModelRenderer
	{
		private static final ModelBase MODEL_EMPTY = new ModelBase(){};
		
		public ModelRendererEmpty(ModelRenderer renderer)
		{
			super(MODEL_EMPTY);
			ModelBiped.copyModelAngles(renderer, this);
		}
		
		@Override
		public void render(float scale) {}
		
	}
}