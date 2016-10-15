package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.capability.IBitToolSettingsHandler;
import com.phylogeny.extrabitmanipulation.capability.BitToolSettingsHandler;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSyncAllSculptingData implements IMessage
{
	private int modelAreaMode, modelSnapMode, sculptMode, direction, shapeTypeCurved, shapeTypeFlat, sculptSemiDiameter, wallThickness;
	private boolean modelGuiOpen, targetBitGridVertexes, sculptHollowShapeWire, sculptHollowShapeSpade, openEnds;
	private ItemStack setBitWire, setBitSpade;
	
	public PacketSyncAllSculptingData() {}
	
	public PacketSyncAllSculptingData(int modelAreaMode, int modelSnapMode, boolean modelGuiOpen, int sculptMode, int direction, int shapeTypeCurved,
			int shapeTypeFlat, boolean targetBitGridVertexes, int sculptSemiDiameter, boolean sculptHollowShapeWire,
			boolean sculptHollowShapeSpade, boolean openEnds, int wallThickness, ItemStack setBitWire, ItemStack setBitSpade)
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
	}
	
	public static class Handler implements IMessageHandler<PacketSyncAllSculptingData, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncAllSculptingData message, final MessageContext ctx)
		{
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = Minecraft.getMinecraft().thePlayer;
					IBitToolSettingsHandler cap = BitToolSettingsHandler.getCapability(player);
					if (cap != null)
					{
						cap.setModelAreaMode(message.modelAreaMode);
						cap.setModelSnapMode(message.modelSnapMode);
						cap.setModelGuiOpen(message.modelGuiOpen);
						cap.setSculptMode(message.sculptMode);
						cap.setDirection(message.direction);
						cap.setShapeTypeCurved(message.shapeTypeCurved);
						cap.setShapeTypeFlat(message.shapeTypeFlat);
						cap.setBitGridTargeted(message.targetBitGridVertexes);
						cap.setSculptSemiDiameter(message.sculptSemiDiameter);
						cap.setShapeHollowWire(message.sculptHollowShapeWire);
						cap.setShapeHollowSpade(message.sculptHollowShapeSpade);
						cap.setEndsOpen(message.openEnds);
						cap.setBitStackWire(message.setBitWire);
						cap.setBitStackSpade(message.setBitSpade);
					}
				}
			});
			return null;
		}
		
	}
	
}