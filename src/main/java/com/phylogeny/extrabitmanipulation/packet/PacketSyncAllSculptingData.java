package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.extendedproperties.SculptSettingsPlayerProperties;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSyncAllSculptingData implements IMessage
{
	private int rotation, shapeTypeCurved, shapeTypeFlat, sculptSemiDiameter, wallThickness;
	private boolean targetBitGridVertexes, sculptHollowShape, openEnds;
	private ItemStack setBitWire, setBitSpade;
	
	public PacketSyncAllSculptingData() {}
	
	public PacketSyncAllSculptingData(int rotation, int shapeTypeCurved, int shapeTypeFlat,
			boolean targetBitGridVertexes, int sculptSemiDiameter, boolean sculptHollowShape,
			boolean openEnds, int wallThickness, ItemStack setBitWire, ItemStack setBitSpade)
	{
		this.rotation = rotation;
		this.shapeTypeCurved = shapeTypeCurved;
		this.shapeTypeFlat = shapeTypeFlat;
		this.targetBitGridVertexes = targetBitGridVertexes;
		this.sculptSemiDiameter = sculptSemiDiameter;
		this.sculptHollowShape = sculptHollowShape;
		this.openEnds = openEnds;
		this.wallThickness = wallThickness;
		this.setBitWire = setBitWire;
		this.setBitSpade = setBitSpade;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(rotation);
		buffer.writeInt(shapeTypeCurved);
		buffer.writeInt(shapeTypeFlat);
		buffer.writeBoolean(targetBitGridVertexes);
		buffer.writeInt(sculptSemiDiameter);
		buffer.writeBoolean(sculptHollowShape);
		buffer.writeBoolean(openEnds);
		buffer.writeInt(wallThickness);
		ItemStackHelper.stackToBytes(buffer, setBitWire);
		ItemStackHelper.stackToBytes(buffer, setBitSpade);
	}

	
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		rotation = buffer.readInt();
		shapeTypeCurved = buffer.readInt();
		shapeTypeFlat = buffer.readInt();
		targetBitGridVertexes = buffer.readBoolean();
		sculptSemiDiameter = buffer.readInt();
		sculptHollowShape = buffer.readBoolean();
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
					SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
					if (sculptProp != null)
					{
						sculptProp.rotation = message.rotation;
						sculptProp.shapeTypeCurved = message.shapeTypeCurved;
						sculptProp.shapeTypeFlat = message.shapeTypeFlat;
						sculptProp.targetBitGridVertexes = message.targetBitGridVertexes;
						sculptProp.sculptSemiDiameter = message.sculptSemiDiameter;
						sculptProp.sculptHollowShape = message.sculptHollowShape;
						sculptProp.openEnds = message.openEnds;
						sculptProp.setBitWire = message.setBitWire;
						sculptProp.setBitSpade = message.setBitSpade;
					}
				}
			});
			return null;
		}
		
	}
	
}