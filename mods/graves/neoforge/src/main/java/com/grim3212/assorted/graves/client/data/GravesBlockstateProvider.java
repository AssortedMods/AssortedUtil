package com.grim3212.assorted.graves.client.data;

import com.grim3212.assorted.graves.Constants;
import com.grim3212.assorted.graves.common.block.GraveBlock;
import com.grim3212.assorted.graves.common.block.GravesBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** The grave's model is 1.12's, a headstone over a slab of cover, with its UVs kept. */
public class GravesBlockstateProvider extends BlockStateProvider {

    public GravesBlockstateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Graves block states";
    }

    @Override
    protected void registerStatesAndModels() {
        Block grave = GravesBlocks.GRAVE.get();
        BlockModelBuilder model = this.models().getBuilder("block/grave")
                .parent(this.models().getExistingFile(this.mcLoc("block/block")))
                .texture("particle", this.modLoc("block/grave_headstone"))
                .texture("headstone", this.modLoc("block/grave_headstone"))
                .texture("cover", this.modLoc("block/grave_cover"));
        model.element().from(1, 0, 0).to(15, 15, 2)
                .face(Direction.DOWN).texture("#headstone").uvs(16, 14, 14, 2).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#headstone").uvs(0, 2, 2, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
                .face(Direction.NORTH).texture("#headstone").uvs(2, 1, 14, 16).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).texture("#headstone").uvs(2, 1, 14, 16).end()
                .face(Direction.WEST).texture("#headstone").uvs(0, 1, 2, 16).end()
                .face(Direction.EAST).texture("#headstone").uvs(14, 1, 16, 16).end()
                .end();
        model.element().from(2, 0, 2).to(14, 1, 15)
                .face(Direction.DOWN).texture("#cover").uvs(14, 15, 2, 2).cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#cover").uvs(2, 2, 14, 15).end()
                .face(Direction.SOUTH).texture("#cover").uvs(2, 15, 14, 16).end()
                .face(Direction.WEST).texture("#cover").uvs(2, 15, 15, 16).end()
                .face(Direction.EAST).texture("#cover").uvs(1, 15, 14, 16).end()
                .end();

        // South 0, west 90, north 180, east 270: the inscription faces FACING.
        this.getVariantBuilder(grave).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(model)
                .rotationY((int) state.getValue(GraveBlock.FACING).toYRot())
                .build(), GraveBlock.WATERLOGGED);
    }
}
