package am2.proxy;

import static am2.defs.IDDefs.GUI_ARCANE_DECONSTRUCTOR;
import static am2.defs.IDDefs.GUI_ARCANE_RECONSTRUCTOR;
import static am2.defs.IDDefs.GUI_ARMOR_INFUSION;
import static am2.defs.IDDefs.GUI_ASTRAL_BARRIER;
import static am2.defs.IDDefs.GUI_CRYSTAL_MARKER;
import static am2.defs.IDDefs.GUI_ESSENCE_REFINER;
import static am2.defs.IDDefs.GUI_FLICKER_HABITAT;
import static am2.defs.IDDefs.GUI_INSCRIPTION_TABLE;
import static am2.defs.IDDefs.GUI_KEYSTONE;
import static am2.defs.IDDefs.GUI_KEYSTONE_CHEST;
import static am2.defs.IDDefs.GUI_KEYSTONE_LOCKABLE;
import static am2.defs.IDDefs.GUI_OBELISK;
import static am2.defs.IDDefs.GUI_OCCULUS;
import static am2.defs.IDDefs.GUI_RIFT;
import static am2.defs.IDDefs.GUI_RUNE_BAG;
import static am2.defs.IDDefs.GUI_SPELL_BOOK;
import static am2.defs.IDDefs.GUI_SPELL_CUSTOMIZATION;
import static am2.defs.IDDefs.GUI_SPELL_SEALED_DOOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import am2.ArsMagica2;
import am2.api.blocks.IKeystoneLockable;
import am2.api.math.AMVector3;
import am2.api.power.IPowerNode;
import am2.api.spell.SpellComponent;
import am2.armor.ArmorHelper;
import am2.armor.infusions.GenericImbuement;
import am2.blocks.render.TileArcaneReconstructorRenderer;
import am2.blocks.render.TileBlackAuremRenderer;
import am2.blocks.render.TileCelestialPrismRenderer;
import am2.blocks.render.TileCraftingAltarRenderer;
import am2.blocks.render.TileCrystalMarkerRenderer;
import am2.blocks.render.TileEssenceConduitRenderer;
import am2.blocks.render.TileEverstoneRenderer;
import am2.blocks.render.TileFlickerHabitatRenderer;
import am2.blocks.render.TileKeystoneChestRenderer;
import am2.blocks.render.TileKeystoneReceptacleRenderer;
import am2.blocks.render.TileLecternRenderer;
import am2.blocks.render.TileObeliskRenderer;
import am2.blocks.render.TileRuneRenderer;
import am2.blocks.tileentity.TileEntityArcaneDeconstructor;
import am2.blocks.tileentity.TileEntityArcaneReconstructor;
import am2.blocks.tileentity.TileEntityArmorImbuer;
import am2.blocks.tileentity.TileEntityAstralBarrier;
import am2.blocks.tileentity.TileEntityBlackAurem;
import am2.blocks.tileentity.TileEntityCelestialPrism;
import am2.blocks.tileentity.TileEntityCraftingAltar;
import am2.blocks.tileentity.TileEntityCrystalMarker;
import am2.blocks.tileentity.TileEntityEssenceConduit;
import am2.blocks.tileentity.TileEntityEverstone;
import am2.blocks.tileentity.TileEntityFlickerHabitat;
import am2.blocks.tileentity.TileEntityGroundRuneSpell;
import am2.blocks.tileentity.TileEntityInscriptionTable;
import am2.blocks.tileentity.TileEntityKeystoneChest;
import am2.blocks.tileentity.TileEntityKeystoneRecepticle;
import am2.blocks.tileentity.TileEntityLectern;
import am2.blocks.tileentity.TileEntityObelisk;
import am2.blocks.tileentity.TileEntitySpellSealedDoor;
import am2.commands.ConfigureAMUICommand;
import am2.defs.AMSounds;
import am2.defs.BindingsDefs;
import am2.defs.BlockDefs;
import am2.defs.EntityManager;
import am2.defs.ItemDefs;
import am2.extensions.RiftStorage;
import am2.gui.AMGuiHelper;
import am2.gui.GuiArcaneDeconstructor;
import am2.gui.GuiArcaneReconstructor;
import am2.gui.GuiArmorImbuer;
import am2.gui.GuiAstralBarrier;
import am2.gui.GuiCrystalMarker;
import am2.gui.GuiFlickerHabitat;
import am2.gui.GuiInscriptionTable;
import am2.gui.GuiKeystone;
import am2.gui.GuiKeystoneChest;
import am2.gui.GuiKeystoneLockable;
import am2.gui.GuiObelisk;
import am2.gui.GuiOcculus;
import am2.gui.GuiRiftStorage;
import am2.gui.GuiSpellBook;
import am2.gui.GuiSpellCustomization;
import am2.gui.GuiSpellSealedDoor;
import am2.handler.BakingHandler;
import am2.items.ItemKeystone;
import am2.items.ItemSpellBase;
import am2.items.ItemSpellBook;
import am2.lore.ArcaneCompendium;
import am2.models.ArsMagicaModelLoader;
import am2.models.CullfaceModelLoader;
import am2.models.SpecialRenderModelLoader;
import am2.packet.AMNetHandler;
import am2.packet.AMPacketProcessorClient;
import am2.particles.AMParticleIcons;
import am2.particles.ParticleManagerClient;
import am2.power.PowerNodeEntry;
import am2.power.PowerTypes;
import am2.proxy.gui.ItemRenderer;
import am2.proxy.tick.ClientTickHandler;
import am2.spell.component.Telekinesis;
import am2.texture.SpellIconManager;
import am2.utils.InventoryUtilities;
import am2.utils.RenderUtils;
import am2.utils.SpellUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
	public ClientTickHandler clientTickHandler;
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case GUI_OCCULUS: return new GuiOcculus(player);
		case GUI_SPELL_CUSTOMIZATION: return new GuiSpellCustomization(player);
		case GUI_RIFT: return new GuiRiftStorage(player, RiftStorage.For(player));
		case GUI_SPELL_BOOK: 
			ItemStack bookStack = player.getHeldItemMainhand();
			if (bookStack.getItem() == null || !(bookStack.getItem() instanceof ItemSpellBook)){
				return null;
			}
			ItemSpellBook item = (ItemSpellBook)bookStack.getItem();
			return new GuiSpellBook(player.inventory, bookStack, item.ConvertToInventory(bookStack));
		case GUI_OBELISK: return new GuiObelisk((TileEntityObelisk)world.getTileEntity(new BlockPos(x, y, z)), player);
		case GUI_CRYSTAL_MARKER: return new GuiCrystalMarker(player, (TileEntityCrystalMarker)te);		
		case GUI_INSCRIPTION_TABLE: return new GuiInscriptionTable(player.inventory, (TileEntityInscriptionTable)world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_ARMOR_INFUSION: return new GuiArmorImbuer(player, (TileEntityArmorImbuer) world.getTileEntity(new BlockPos(x, y, z)));
		case GUI_KEYSTONE:
			ItemStack keystoneStack = player.getHeldItemMainhand();
			if (keystoneStack.getItem() == null || !(keystoneStack.getItem() instanceof ItemKeystone)){
				return null;
			}
			ItemKeystone keystone = (ItemKeystone)keystoneStack.getItem();

			int runeBagSlot = InventoryUtilities.getInventorySlotIndexFor(player.inventory, ItemDefs.runeBag);
			ItemStack runeBag = null;
			if (runeBagSlot > -1)
				runeBag = player.inventory.getStackInSlot(runeBagSlot);

			return new GuiKeystone(player.inventory, player.getHeldItemMainhand(), runeBag, keystone.ConvertToInventory(keystoneStack), runeBag == null ? null : ItemDefs.runeBag.ConvertToInventory(runeBag), runeBagSlot);
		case GUI_KEYSTONE_LOCKABLE:
			if (!(te instanceof IKeystoneLockable)){
				return null;
			}
			return new GuiKeystoneLockable(player.inventory, (IKeystoneLockable<?>)te);
		case GUI_SPELL_SEALED_DOOR: return new GuiSpellSealedDoor(player.inventory, (TileEntitySpellSealedDoor)te);
		case GUI_KEYSTONE_CHEST: return new GuiKeystoneChest(player.inventory, (TileEntityKeystoneChest)te);
		case GUI_RUNE_BAG: break; //TODO
		case GUI_FLICKER_HABITAT: return new GuiFlickerHabitat(player, (TileEntityFlickerHabitat) te);
		case GUI_ARCANE_DECONSTRUCTOR: return new GuiArcaneDeconstructor(player.inventory, (TileEntityArcaneDeconstructor) te);
		case GUI_ARCANE_RECONSTRUCTOR: return new GuiArcaneReconstructor(player.inventory, (TileEntityArcaneReconstructor) te);
		case GUI_ASTRAL_BARRIER: return new GuiAstralBarrier(player.inventory, (TileEntityAstralBarrier) te);
		case GUI_ESSENCE_REFINER: break; //TODO
		}
		return super.getClientGuiElement(ID, player, world, x, y, z);
	}
	@Override
	public void preInit() {
		super.preInit();
		
		OBJLoader.INSTANCE.addDomain("arsmagica2");
		
		AMParticleIcons.instance.toString();
		SpellIconManager.INSTANCE.toString();
		
		ClientRegistry.registerKeyBinding(BindingsDefs.iceBridge);
		ClientRegistry.registerKeyBinding(BindingsDefs.ENDER_TP);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraftingAltar.class, new TileCraftingAltarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityObelisk.class, new TileObeliskRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCelestialPrism.class, new TileCelestialPrismRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlackAurem.class, new TileBlackAuremRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLectern.class, new TileLecternRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeystoneRecepticle.class, new TileKeystoneReceptacleRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalMarker.class, new TileCrystalMarkerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlickerHabitat.class, new TileFlickerHabitatRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeystoneChest.class, new TileKeystoneChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEssenceConduit.class, new TileEssenceConduitRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGroundRuneSpell.class, new TileRuneRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEverstone.class, new TileEverstoneRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcaneReconstructor.class, new TileArcaneReconstructorRenderer());
		
		ModelLoaderRegistry.registerLoader(new ArsMagicaModelLoader());
		ModelLoaderRegistry.registerLoader(new CullfaceModelLoader());
		ModelLoaderRegistry.registerLoader(new SpecialRenderModelLoader());
		
		MinecraftForge.EVENT_BUS.register(new ArsMagicaModelLoader());
		MinecraftForge.EVENT_BUS.register(clientTickHandler);
		MinecraftForge.EVENT_BUS.register(ItemRenderer.instance);
		MinecraftForge.EVENT_BUS.register(new BakingHandler());
		
		ArsMagica2.config.clientInit();
		new AMSounds();
		EntityManager.instance.registerRenderers();
		blocks.preInitClient();
		
		ClientCommandHandler.instance.registerCommand(new ConfigureAMUICommand());
	}
	
	@Override
	public void initHandlers() {
		particleManager = new ParticleManagerClient();
		packetProcessor = new AMPacketProcessorClient();
		clientTickHandler = new ClientTickHandler();
	}
	
	/**
	 * Proxied compendium unlocks.  Do not call directly - use the CompendiumUnlockHandler instead.
	 */
	@Override
	public void unlockCompendiumEntry(String id){
		ArcaneCompendium.For(Minecraft.getMinecraft().thePlayer).unlockEntry(id);
	}
	
	@Override
	public void init() {
		super.init();
		BlockDefs.initClient();
		ItemDefs.initClient();
	}
	
	@Override
	public void renderGameOverlay() {
		clientTickHandler.renderOverlays();
	}
	
	@Override
	public void setTrackedLocation(AMVector3 location){
		clientTickHandler.setTrackLocation(location.toVec3D());
	}

	@Override
	public void setTrackedPowerCompound(NBTTagCompound compound){
		clientTickHandler.setTrackData(compound);
	}

	@Override
	public boolean hasTrackedLocationSynced(){
		return clientTickHandler.getHasSynced();
	}

	@Override
	public PowerNodeEntry getTrackedData(){
		return clientTickHandler.getTrackData();
	}
	
	/**
	 * Proxied compendium unlocks.  Do not call directly - use the CompendiumUnlockHandler instead.
	 */
	@Override
	public void unlockCompendiumCategory(String id){
		ArcaneCompendium.For(Minecraft.getMinecraft().thePlayer).unlockCategory(id);
	}
	
	@Override
	public boolean setMouseDWheel(int dwheel){
		if (dwheel == 0) return false;

		ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
		if (stack == null) return false;

		boolean store = checkForTKMove(stack);
		if (!store && stack.getItem() instanceof ItemSpellBook){
			store = Minecraft.getMinecraft().thePlayer.isSneaking();
		}

		if (store){
			clientTickHandler.setDWheel(dwheel / 120, Minecraft.getMinecraft().thePlayer.inventory.currentItem, Minecraft.getMinecraft().thePlayer.isHandActive());
			return true;
		}else{
			clientTickHandler.setDWheel(0, -1, false);
		}
		return false;
	}

	private boolean checkForTKMove(ItemStack stack){
		if (stack.getItem() instanceof ItemSpellBook){
			ItemStack activeStack = ((ItemSpellBook)stack.getItem()).GetActiveItemStack(stack);
			if (activeStack != null)
				stack = activeStack;
		}
		if (stack.getItem() instanceof ItemSpellBase && stack.hasTagCompound() && Minecraft.getMinecraft().thePlayer.isHandActive()){
			for (SpellComponent component : SpellUtils.getComponentsForStage(stack, -1)){
				if (component instanceof Telekinesis){
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void drawPowerOnBlockHighlight(EntityPlayer player, RayTraceResult target, float partialTicks){
		
		if (Minecraft.getMinecraft().thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null &&
				(Minecraft.getMinecraft().thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemDefs.magitechGoggles)
					|| ArmorHelper.isInfusionPreset(Minecraft.getMinecraft().thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD), GenericImbuement.magitechGoggleIntegration)){
			if (target.getBlockPos() == null)
				return;
			TileEntity te = player.worldObj.getTileEntity(target.getBlockPos());
			if (te != null && te instanceof IPowerNode){
				ArsMagica2.proxy.setTrackedLocation(new AMVector3(target.getBlockPos()));
			}else{
				ArsMagica2.proxy.setTrackedLocation(AMVector3.zero());
			}

			if (ArsMagica2.proxy.hasTrackedLocationSynced()){
				PowerNodeEntry data = ArsMagica2.proxy.getTrackedData();
				Block block = player.worldObj.getBlockState(target.getBlockPos()).getBlock();
				float yOff = 0.5f;
				if (data != null){
					GlStateManager.pushAttrib();
					for (PowerTypes type : ((IPowerNode<?>)te).getValidPowerTypes()){
						float pwr = data.getPower(type);
						float pct = pwr / ((IPowerNode<?>)te).getCapacity() * 100;
						AMVector3 offset = new AMVector3(target.getBlockPos().getX() + 0.5, target.getBlockPos().getX() + 0.5, target.getBlockPos().getZ() + 0.5).sub(
								new AMVector3((player.prevPosX - (player.prevPosX - player.posX) * partialTicks),
										(player.prevPosY - (player.prevPosY - player.posY) * partialTicks) + player.getEyeHeight(),
										(player.prevPosZ - (player.prevPosZ - player.posZ) * partialTicks)));
						offset = offset.normalize();
						if (target.getBlockPos().getY() <= player.posY + player.getEyeHeight()){
							RenderUtils.drawTextInWorldAtOffset(String.format("%s%.2f (%.2f%%)", type.getChatColor(), pwr, pct),
									target.getBlockPos().getX() - (player.prevPosX - (player.prevPosX - player.posX) * partialTicks) + 0.5f - offset.x,
									target.getBlockPos().getY() + yOff - (player.prevPosY - (player.prevPosY - player.posY) * partialTicks) + block.getBoundingBox(player.worldObj.getBlockState(target.getBlockPos()), player.worldObj, target.getBlockPos()).maxY * 0.8f,
									target.getBlockPos().getZ() - (player.prevPosZ - (player.prevPosZ - player.posZ) * partialTicks) + 0.5f - offset.z,
									0xFFFFFF);
							yOff += 0.12f;
						}else{
							RenderUtils.drawTextInWorldAtOffset(String.format("%s%.2f (%.2f%%)", type.getChatColor(), pwr, pct),
									target.getBlockPos().getX() - (player.prevPosX - (player.prevPosX - player.posX) * partialTicks) + 0.5f - offset.x,
									target.getBlockPos().getY() - yOff - (player.prevPosY - (player.prevPosY - player.posY) * partialTicks) - block.getBoundingBox(player.worldObj.getBlockState(target.getBlockPos()), player.worldObj, target.getBlockPos()).maxY * 0.2f,
									target.getBlockPos().getZ() - (player.prevPosZ - (player.prevPosZ - player.posZ) * partialTicks) + 0.5f - offset.z,
									0xFFFFFF);
							yOff -= 0.12f;
						}
					}
					GlStateManager.disableAlpha();
					GlStateManager.popAttrib();
				}
			}
		}
	}
	
	@Override
	public void requestPowerPathVisuals(IPowerNode<?> node, EntityPlayerMP player){
		AMNetHandler.INSTANCE.syncPowerPaths(node, player);
	}
	
	@Override
	public void flashManaBar() {
		AMGuiHelper.instance.flashManaBar();
	}

	@Override
	public void receivePowerPathVisuals(HashMap<PowerTypes, ArrayList<LinkedList<Vec3d>>> paths){
		powerPathVisuals = paths;
	}

	@Override
	public HashMap<PowerTypes, ArrayList<LinkedList<Vec3d>>> getPowerPathVisuals(){
		return powerPathVisuals;
	}
	
	@Override
	public EntityPlayer getLocalPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}
