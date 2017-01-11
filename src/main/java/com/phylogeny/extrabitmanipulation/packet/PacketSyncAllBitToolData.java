package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.extendedproperties.BitToolSettingsPlayerProperties;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSyncAllBitToolData implements IMessage
{
	private int modelAreaMode, modelSnapMode, sculptMode, direction, shapeTypeCurved, shapeTypeFlat, sculptSemiDiameter, wallThickness;
	private boolean modelGuiOpen, targetBitGridVertexes, sculptHollowShapeWire, sculptHollowShapeSpade, openEnds, offsetShape;
	private ItemStack setBitWire, setBitSpade;
	
	public PacketSyncAllBitToolData() {}
	
	public PacketSyncAllBitToolData(int modelAreaMode, int modelSnapMode, boolean modelGuiOpen, int sculptMode, int direction, int shapeTypeCurved,
			int shapeTypeFlat, boolean targetBitGridVertexes, int sculptSemiDiameter, boolean sculptHollowShapeWire,
			boolean sculptHollowShapeSpade, boolean openEnds, int wallThickness, ItemStack setBitWire, ItemStack setBitSpade, boolean offsetShape)
	{
		this.modelAreaMode = modelAreaMode;
		this.modelSnapMode = modelSnapMode;
		this.modelGuiOpen = modelGuiOpen;
		this.sculptMode = sculptMode;
		this.direction = direction;
		this.shapeTypeCurved = shapeTypeCurved;
		this.shapeTypeFlat = shapeTypeFlat;
		this.targetBitGridVertexes = targetBitGridVertexes;
		this.sculptSemiDiameter = sculptSemiDiameter;
		this.sculptHollowShapeWire = sculptHollowShapeWire;
		this.sculptHollowShapeSpade = sculptHollowShapeSpade;
		this.openEnds = openEnds;
		this.wallThickness = wallThickness;
		this.setBitWire = setBitWire;
		this.setBitSpade = setBitSpade;
		this.offsetShape = offsetShape;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(modelAreaMode);
		buffer.writeInt(modelSnapMode);
		buffer.writeBoolean(modelGuiOpen);
		buffer.writeInt(sculptMode);
		buffer.writeInt(direction);
		buffer.writeInt(shapeTypeCurved);
		buffer.writeInt(shapeTypeFlat);
		buffer.writeBoolean(targetBitGridVertexes);
		buffer.writeInt(sculptSemiDiameter);
		buffer.writeBoolean(sculptHollowShapeWire);
		buffer.writeBoolean(sculptHollowShapeSpade);
		buffer.writeBoolean(openEnds);
		buffer.writeInt(wallThickness);
		ItemStackHelper.stackToBytes(buffer, setBitWire);
		ItemStackHelper.stackToBytes(buffer, setBitSpade);
		buffer.writeBoolean(offsetShape);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		modelAreaMode = buffer.readInt();
		modelSnapMode = buffer.readInt();
		modelGuiOpen = buffer.readBoolean();
		sculptMode = buffer.readInt();
		direction = buffer.readInt();
		shapeTypeCurved = buffer.readInt();
		shapeTypeFlat = buffer.readInt();
		targetBitGridVertexes = buffer.readBoolean();
		sculptSemiDiameter = buffer.readInt();
		sculptHollowShapeWire = buffer.readBoolean();
		sculptHollowShapeSpade = buffer.readBoolean();
		openEnds = buffer.readBoolean();
		wallThickness = buffer.readInt();
		setBitWire = ItemStackHelper.stackFromBytes(buffer);
		setBitSpade = ItemStackHelper.stackFromBytes(buffer);
		offsetShape = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSyncAllBitToolData, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncAllBitToolData message, final MessageContext ctx)
		{
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ClientHelper.getPlayer();
					BitToolSettingsPlayerProperties sculptProp = BitToolSettingsPlayerProperties.get(player);
					if (sculptProp != null)
					{
						sculptProp.modelAreaMode = message.modelAreaMode;
						sculptProp.modelSnapMode = message.modelSnapMode;
						sculptProp.modelGuiOpen = message.modelGuiOpen;
						sculptProp.direction = message.direction;
						sculptProp.shapeTypeCurved = message.shapeTypeCurved;
						sculptProp.shapeTypeFlat = message.shapeTypeFlat;
						sculptProp.targetBitGridVertexes = message.targetBitGridVertexes;
						sculptProp.sculptSemiDiameter = message.sculptSemiDiameter;
						sculptProp.sculptHollowShapeWire = message.sculptHollowShapeWire;
						sculptProp.sculptHollowShapeSpade = message.sculptHollowShapeSpade;
						sculptProp.openEnds = message.openEnds;
						sculptProp.setBitWire = message.setBitWire;
						sculptProp.setBitSpade = message.setBitSpade;
						sculptProp.offsetShape = message.offsetShape;
					}
				}
			});
			return null;
		}
		
	}
	
}