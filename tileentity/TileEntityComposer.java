package be.hydroen.chimicraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntityComposer extends TileEntity implements IInventory
{
	private ItemStack[] contents = new ItemStack[27];
	private String customName;
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		if(compound.hasKey("CustomName", Constants.NBT.TAG_STRING))
		{
			this.customName = compound.getString("CustomName");
		}
		
		NBTTagList nbttaglist = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for(int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			
			if(j >= 0 && j < this.contents.length)
			{
				this.contents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		if(this.hasCustomInventoryName())
		{
			compound.setString("CustomName", this.customName);
		}
		
		NBTTagList nbttaglist = new NBTTagList();
		
		for(int i = 0; i < this.contents.length; ++i)
		{
			if(this.contents[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				this.contents[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		} 
		
		compound.setTag("Items", nbttaglist);
	}

	@Override
	public int getSizeInventory() 
	{
		return this.contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) 
	{
		return this.contents[slotIndex];
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) 
	{
		if(this.contents[slotIndex] != null)
		{
			ItemStack itemstack;
			
			if(this.contents[slotIndex].stackSize <= amount)
			{
				itemstack = this.contents[slotIndex];
				this.contents[slotIndex] = null;
				this.markDirty();
				
				return itemstack;
			}
			else
			{
				itemstack = this.contents[slotIndex].splitStack(amount);
				
				if(this.contents[slotIndex].stackSize == 0)
				{
					this.contents[slotIndex] = null;
				}
				
				this.markDirty();
				
				return itemstack;
			}
		}
		else
		{
			return null;
		}
		
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) 
	{
		if(this.contents[slotIndex] != null)
		{
			ItemStack itemstack = this.contents[slotIndex];
			this.contents[slotIndex] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack stack) 
	{
		this.contents[slotIndex] = stack;
		
		if(stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		this.markDirty();
	}

	@Override
	public String getInventoryName() 
	{
		return this.hasCustomInventoryName() ? this.customName : "tile.composer";
	}

	public void setCustomName(String customName)
	{
		this.customName = customName;
	}
	
	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) 
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord +0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() 
	{
		
	}

	@Override
	public void closeInventory() 
	{
		
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack stack) 
	{	//Option pour entonnoir, permet de dire ou(slot) doit aller quoi(charbon, ...) 
		return true;
	}
}
