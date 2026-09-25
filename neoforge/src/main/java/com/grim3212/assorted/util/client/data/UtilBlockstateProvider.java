package com.grim3212.assorted.util.client.data;

import com.grim3212.assorted.util.Constants;
import com.grim3212.assorted.util.common.block.UtilBlocks;
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

/** The grave's model is 1.12's, a headstone over a slab of cover, with its UVs kept. */
public class UtilBlockstateProvider extends ModelProvider {

    private static final TextureSlot HEADSTONE = TextureSlot.create("headstone");
    private static final TextureSlot COVER = TextureSlot.create("cover");

    private static final ModelTemplate GRAVE = ExtendedModelTemplateBuilder.builder()
            .parent(Identifier.withDefaultNamespace("block/block"))
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(HEADSTONE)
            .requiredTextureSlot(COVER)
            .element(e -> e.from(1, 0, 0).to(15, 15, 2)
                    .face(Direction.DOWN, f -> f.texture(HEADSTONE).uvs(16, 14, 14, 2).rotation(Quadrant.R270).cullface(Direction.DOWN))
                    .face(Direction.UP, f -> f.texture(HEADSTONE).uvs(0, 2, 2, 14).rotation(Quadrant.R90))
                    .face(Direction.NORTH, f -> f.texture(HEADSTONE).uvs(2, 1, 14, 16).cullface(Direction.NORTH))
                    .face(Direction.SOUTH, f -> f.texture(HEADSTONE).uvs(2, 1, 14, 16))
                    .face(Direction.WEST, f -> f.texture(HEADSTONE).uvs(0, 1, 2, 16))
                    .face(Direction.EAST, f -> f.texture(HEADSTONE).uvs(14, 1, 16, 16)))
            .element(e -> e.from(2, 0, 2).to(14, 1, 15)
                    .face(Direction.DOWN, f -> f.texture(COVER).uvs(14, 15, 2, 2).cullface(Direction.DOWN))
                    .face(Direction.UP, f -> f.texture(COVER).uvs(2, 2, 14, 15))
                    .face(Direction.SOUTH, f -> f.texture(COVER).uvs(2, 15, 14, 16))
                    .face(Direction.WEST, f -> f.texture(COVER).uvs(2, 15, 15, 16))
                    .face(Direction.EAST, f -> f.texture(COVER).uvs(1, 15, 14, 16)))
            .build();

    public UtilBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Util block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        Block grave = UtilBlocks.GRAVE.get();
        Material headstone = new Material(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/grave_headstone"));
        Identifier model = GRAVE.create(grave, new TextureMapping()
                .put(TextureSlot.PARTICLE, headstone)
                .put(HEADSTONE, headstone)
                .put(COVER, new Material(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/grave_cover"))), blockModels.modelOutput);

        // South 0, west 90, north 180, east 270: the inscription faces FACING.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(grave, BlockModelGenerators.plainVariant(model))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));
    }
}
