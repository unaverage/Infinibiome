package weightedgpa.infinibiome.internal.generators.interchunks.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.common.IPlantable;
import weightedgpa.infinibiome.api.Infinibiome;
import weightedgpa.infinibiome.api.dependency.DependencyInjector;
import weightedgpa.infinibiome.api.posdata.PosDataHelper;
import weightedgpa.infinibiome.internal.generators.utils.condition.ConditionHelper;

import java.util.List;
import java.util.Random;
import java.util.Set;

public final class JungleSmallGen extends TreeGenBase {
    private static final BlockState LOG = Blocks.JUNGLE_LOG.getDefaultState();
    private static final BlockState LEAF = Blocks.JUNGLE_LEAVES.getDefaultState();
    private static final IPlantable SAPLING = (IPlantable) Blocks.JUNGLE_SAPLING;

    public JungleSmallGen(DependencyInjector di) {
        super(di, Infinibiome.MOD_ID + ":jungleSmall");

        config = this.<TreeFeatureConfig>initConfig()
            .setFeature(
                Feature.NORMAL_TREE
            )
            .setConfigFunc(
                (pos, height, random) -> new TreeFeatureConfig.Builder(
                    new SimpleBlockStateProvider(LOG),
                    new SimpleBlockStateProvider(LEAF),
                    new BlobFoliagePlacer(2, 0)
                )
                .baseHeight(height)
                .trunkHeight(height-3)
                .foliageHeight(3)
                .decorators(
                    ImmutableList.of(new CocoaTreeDecoratorFixed(0.1F))
                )
                .ignoreVines()
                .setSapling(
                    SAPLING
                )
                .build()
            )
            .setHeightFunc(TreeHelper.SMALL_TREE_HEIGHT)
            .setIsolationRadius(TreeHelper.COMMON_ISOLATION_RADIUS)
            .onlyGrowIn1x1Config()
            .setWithCommonDensity()
            .setRegionRate(
                TreeHelper.COMMON_REGION_RATE * 2
            )
            .addExtraConditions(
                ConditionHelper.onlyInTemperature(
                    di,
                    PosDataHelper.HOT_INTERVAL
                ),
                ConditionHelper.onlyInHumidity(
                    di,
                    PosDataHelper.WET_INTERVAL
                )
            );
    }

    //changed vanilla code so its always mature
    @SuppressWarnings("all")
    private static class CocoaTreeDecoratorFixed extends TreeDecorator {
        private final double field_227417_b_;

        CocoaTreeDecoratorFixed(double p_i225868_1_) {
            super(TreeDecoratorType.COCOA);
            this.field_227417_b_ = p_i225868_1_;
        }

        @Override
        public void func_225576_a_(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
            if (!(p_225576_2_.nextFloat() >= this.field_227417_b_)) {
                int i = p_225576_3_.get(0).getY();
                p_225576_3_.stream().filter((p_227418_1_) -> {
                    return p_227418_1_.getY() - i <= 2;
                }).forEach((p_227419_5_) -> {
                    for(Direction direction : Direction.Plane.HORIZONTAL) {
                        if (p_225576_2_.nextFloat() <= 0.25F) {
                            Direction direction1 = direction.getOpposite();
                            BlockPos blockpos = p_227419_5_.add(direction1.getXOffset(), 0, direction1.getZOffset());
                            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos)) {
                                BlockState blockstate = Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, 2).with(CocoaBlock.HORIZONTAL_FACING, direction);
                                this.func_227423_a_(p_225576_1_, blockpos, blockstate, p_225576_5_, p_225576_6_);
                            }
                        }
                    }

                });
            }
        }

        public <T> T serialize(DynamicOps<T> p_218175_1_) {
            return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(
                Registry.TREE_DECORATOR_TYPE.getKey(this.field_227422_a_).toString()), p_218175_1_.createString("probability"), p_218175_1_.createFloat(
                (float) this.field_227417_b_))))).getValue();
        }
    }
}
