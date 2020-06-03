package knightminer.ceramics.registration;

import knightminer.ceramics.registration.object.BlockItemObject;
import knightminer.ceramics.registration.object.EnumObject;
import knightminer.ceramics.registration.object.WallBuildingBlockObject;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDeferredRegister extends RegisterWrapper<Block> {

  private final DeferredRegister<Item> itemRegister;
  public BlockDeferredRegister(String modID) {
    super(ForgeRegistries.BLOCKS, modID);
    this.itemRegister = new DeferredRegister<>(ForgeRegistries.ITEMS, modID);
  }

  @Override
  public void register(IEventBus bus) {
    super.register(bus);
    itemRegister.register(bus);
  }

  /* Blocks with no items */

  /**
   * Registers a block with the block registry
   * @param name   Block ID
   * @param block  Block supplier
   * @param <B>    Block class
   * @return  Block registry object
   */
  public <B extends Block> RegistryObject<B> register(final String name, final Supplier<? extends B> block) {
    return register.register(name, block);
  }

  /**
   * Registers a block with the block registry
   * @param name   Block ID
   * @param props  Block properties
   * @return  Block registry object
   */
  public RegistryObject<Block> register(final String name, final Block.Properties props) {
    return register(name, () -> new Block(props));
  }


  /* Block item pairs */

  /**
   * Registers a block with the block registry, using the function for the BlockItem
   * @param name   Block ID
   * @param block  Block supplier
   * @param item   Function to create a BlockItem from a Block
   * @param <B>    Block class
   * @return  Block item registry object pair
   */
  public <B extends Block> BlockItemObject<B> register(final String name, final Supplier<? extends B> block, final Function<? super B, ? extends BlockItem> item) {
    RegistryObject<B> blockObj = register(name, block);
    return new BlockItemObject<>(blockObj, itemRegister.register(name, () -> item.apply(blockObj.get())));
  }

  /**
   * Registers a block with the block registry, using the given item properties
   * @param name   Block ID
   * @param block  Block supplier
   * @param props  Item properties
   * @param <B>    Block class
   * @return  Block item registry object pair
   */
  public <B extends Block> BlockItemObject<B> register(final String name, final Supplier<? extends B> block, final Item.Properties props) {
    return register(name, block, (b) -> new BlockItem(b, props));
  }

  /**
   * Registers a block with the block registry, using the given item properties
   * @param name        Block ID
   * @param blockProps  Block properties
   * @param itemProps   Item properties
   * @return  Block item registry object pair
   */
  public BlockItemObject<Block> register(final String name, final Block.Properties blockProps, final Item.Properties itemProps) {
    return register(name, () -> new Block(blockProps), itemProps);
  }


  /* Specialty */

  /**
   * Registers a block with slab, stairs, and walls
   * @param name      Name of the block
   * @param props     Block properties
   * @param itemProps Item properties
   * @return  WallBuildingBlockObject class that returns different block types
   */
  public WallBuildingBlockObject registerBuilding(final String name, Block.Properties props, Item.Properties itemProps) {
    BlockItemObject<Block> blockObj = register(name, props, itemProps);
    return new WallBuildingBlockObject(blockObj,
                                       register(name + "_slab", () -> new SlabBlock(props), itemProps),
                                       register(name + "_stairs", () -> new StairsBlock(() -> blockObj.get().getDefaultState(), props), itemProps),
                                       register(name + "_wall", () -> new WallBlock(props), itemProps)
    );
  }

  /**
   * Registers a block with slab, stairs, and walls
   * @param values    Enum values to use for this block
   * @param name      Name of the block
   * @param supplier  Function to get a block for the given enum value
   * @param itemProps Item properties
   * @return  EnumObject mapping between different block types
   */
  @SuppressWarnings("unchecked")
  public <T extends Enum<T> & IStringSerializable, B extends Block> EnumObject<T,B> registerEnum(final T[] values, final String name, Function<T,? extends B> supplier, Item.Properties itemProps) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    Map<T, Supplier<? extends B>> map = new EnumMap<>((Class<T>)values[0].getClass());
    for (T value : values) {
      map.put(value, register(value.getName() + "_" + name, () -> supplier.apply(value), itemProps));
    }
    return new EnumObject<>(map);
  }
}