package com.dudblockman.psipherals.items;

import com.teamwizardry.librarianlib.features.base.item.ItemModBow;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.entity.EntitySpellProjectile;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.tool.IPsimetalTool;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPsimetalBow extends ItemModBow implements IPsimetalTool {
    public ItemPsimetalBow(String name) {
        super(name);
        this.setMaxStackSize(1);
        this.setMaxDamage(575);
        this.addPropertyOverride(new ResourceLocation("pull"), (stack, world, entity) -> entity == null || entity.getActiveItemStack().getItem() != this ? 0 : (stack.getMaxItemUseDuration() - entity.getItemInUseCount()) / 20F);
    }

    public static void castSpell(EntityPlayer player, ItemStack stack, Vec3d pos, EntityArrow arrow) {
        PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
        ItemStack playerCad = PsiAPI.getPlayerCAD(player);
        if (stack.getItem() instanceof ItemPsimetalBow) {
            ItemPsimetalBow bow = (ItemPsimetalBow) stack.getItem();
            if (!playerCad.isEmpty()) {

                ItemStack bullet = bow.getBulletInSocket(stack, bow.getSelectedSlot(stack));
                if (bullet.getItemDamage() != 1) {
                    //ItemStack itemstack = yourbulletthinghere;
                    NBTTagCompound entityCmp = arrow.getEntityData();
                    NBTTagCompound bulletCmp = new NBTTagCompound();
                    //itemstack.writeToNBT(bulletCmp);
                    entityCmp.setTag("rpsideas-spellimmune", bulletCmp);
                }

                ItemCAD.cast(player.world, player, data, bullet, playerCad, 5, 10, 0.05F, (SpellContext context) -> {
                    context.tool = stack;
                });
                float radiusVal = 0.2f;
                AxisAlignedBB region = new AxisAlignedBB(player.posX - radiusVal, player.posY + player.eyeHeight - radiusVal, player.posZ - radiusVal, player.posX + radiusVal, player.posY + player.eyeHeight + radiusVal, player.posZ + radiusVal);

                List<EntitySpellProjectile> spells = player.world.getEntitiesWithinAABB(EntitySpellProjectile.class, region, (e)->((e != null)&&(e.context.caster == player) && (e.ticksExisted <= 1)));
                for (EntitySpellProjectile spell : spells) {
                    spell.startRiding(arrow, true);
                }
                /*ISpellAcceptor spellContainer = ISpellAcceptor.acceptor(bullet);
                Spell spell = spellContainer.getSpell();
                SpellContext spellcontext = new SpellContext().setPlayer(player).setSpell(spell);
                spellcontext.tool = stack;

                ItemStack colorizer = ((ICAD) playerCad.getItem()).getComponentInSlot(playerCad, EnumCADComponent.DYE);
                EntitySpellProjectile projectile = null;

                switch (bullet.getItemDamage()) {
                    case 1: // Basic
                        break;
                    case 3: // Projectile
                        projectile = new EntitySpellProjectile(spellcontext.caster.getEntityWorld(), spellcontext.caster);
                        break;
                    case 5: // Loopcast
                        break;
                    case 7: // Circle
                        break;
                    case 9: // Grenade
                        projectile = new EntitySpellGrenade(spellcontext.caster.getEntityWorld(), spellcontext.caster);
                        break;
                    case 11: // Charge
                        projectile = new EntitySpellCharge(spellcontext.caster.getEntityWorld(), spellcontext.caster);
                        break;
                    case 13: // Mine
                        projectile = new EntitySpellMine(spellcontext.caster.getEntityWorld(), spellcontext.caster);
                        break;
                }

                if (projectile != null) {
                    projectile.setInfo(spellcontext.caster, colorizer, stack);
                    projectile.context = spellcontext;
                    projectile.getEntityWorld().spawnEntity(projectile);
                    projectile.startRiding(arrow, true);
                } else {
                    ItemCAD.cast(player.world, player, data, bullet, playerCad, 5, 10, 0.05F, (SpellContext context) -> {
                        context.tool = stack;
                    });
                }*/
            }
        }

    }
    private ItemStack findAmmo(EntityPlayer player)
    {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
        //super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
        // Code from ItemBow
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = this.findAmmo(entityplayer);

            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag)
            {
                if (itemstack.isEmpty())
                {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getArrowVelocity(i);

                if ((double)f >= 0.1D)
                {
                    boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow && ((ItemArrow) itemstack.getItem()).isInfinite(itemstack, stack, entityplayer));

                    if (!worldIn.isRemote)
                    {
                        ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                        entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);

                        if (f == 1.0F)
                        {
                            entityarrow.setIsCritical(true);
                        }

                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                        if (j > 0)
                        {
                            entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                        if (k > 0)
                        {
                            entityarrow.setKnockbackStrength(k);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
                        {
                            entityarrow.setFire(100);
                        }

                        stack.damageItem(1, entityplayer);

                        if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW))
                        {
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }


                        //Spellcasting Logic here

                        castSpell(entityplayer, stack, new Vec3d(entityplayer.posX, entityplayer.posY, entityplayer.posZ), entityarrow);

                        worldIn.spawnEntity(entityarrow);
                    }

                    worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                    if (!flag1 && !entityplayer.capabilities.isCreativeMode)
                    {
                        itemstack.shrink(1);

                        if (itemstack.isEmpty())
                        {
                            entityplayer.inventory.deleteStack(itemstack);
                        }
                    }

                    entityplayer.addStat(StatList.getObjectUseStats(this));
                }
            }
        }
    }
    public static void regenPsi(ItemStack stack, Entity entityIn, boolean isSelected) {
        if (entityIn instanceof EntityPlayer && stack.getItemDamage() > 0 && !isSelected) {
            EntityPlayer player = (EntityPlayer) entityIn;
            PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
            int regenTime = NBTHelper.getInt(stack, TAG_REGEN_TIME, 0);

            if (!data.overflowed && regenTime % 80 == 0 && (float) data.getAvailablePsi() / (float) data.getTotalPsi() > 0.5F) {
                data.deductPsi(600, 5, true);
                stack.setItemDamage(stack.getItemDamage() - 1);
            }
            NBTHelper.setInt(stack, TAG_REGEN_TIME, regenTime + 1);
        }
    }
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        regenPsi(stack, entityIn, isSelected);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        String componentName = TooltipHelper.local(ISocketable.getSocketedItemName(stack, "psimisc.none"));
        TooltipHelper.addToTooltip(tooltip, "psimisc.spellSelected", componentName);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repairItem) {
        return OreDictionary.containsMatch(false, OreDictionary.getOres("ingotPsi"), repairItem) || super.getIsRepairable(toRepair, repairItem);
    }

    @Override
    public boolean requiresSneakForSpellSet(ItemStack stack) {
        return false;
    }

    public int getItemEnchantability() {
        return 1;
    }
}
