package com.mike_caron.megacorp.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.item.Bottle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

// TODO: Auto-generated Javadoc
@SideOnly(Side.CLIENT)
public final class ModelBottle implements IModel
{
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(MegaCorpMod.modId, "bottle"), "inventory");

    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public static final ModelBottle MODEL = new ModelBottle();

    @Nullable
    private final ResourceLocation emptyLocation = new ResourceLocation(MegaCorpMod.modId, "items/bottle_empty");
    @Nullable
    private final ResourceLocation filledLocation = new ResourceLocation(MegaCorpMod.modId, "items/bottle_filled");
    @Nullable
    private final Fluid fluid;

    /**
     * Instantiates a new model bottle.
     */
    public ModelBottle()
    {
        this(null);
    }

    /**
     * Instantiates a new model bottle.
     *
     * @param parFluid
     *            the par fluid
     */
    public ModelBottle(Fluid parFluid)
    {
        fluid = parFluid;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.client.model.IModel#getTextures()
     */
    @Override
    public Collection<ResourceLocation> getTextures()
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        if (emptyLocation != null)
            builder.add(emptyLocation);
        if (filledLocation != null)
            builder.add(filledLocation);

        return builder.build();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.client.model.IModel#getDependencies()
     */
    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.client.model.IModel#bake(net.minecraftforge.common.model.IModelState, net.minecraft.client.renderer.vertex.VertexFormat, java.util.function.Function)
     */
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        // DEBUG
        //System.out.println("Baking custom model");

        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite fluidSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if (fluid != null)
        {
            // DEBUG
            //System.out.println("Getting fluid sprite");

            fluidSprite = bakedTextureGetter.apply(fluid.getStill());
        }

        if (emptyLocation != null)
        {
            //            // DEBUG
            //            System.out.println("Buiding empty model");

            IBakedModel model = (new ItemLayerModel(ImmutableList.of(emptyLocation))).bake(state, format, bakedTextureGetter);
            builder.addAll(model.getQuads(null, null, 0));
        }
        if (filledLocation != null && fluidSprite != null)
        {
            // DEBUG
            //System.out.println("Building filled model");

            TextureAtlasSprite filledTexture = bakedTextureGetter.apply(filledLocation);
            // build liquid layer (inside)
            builder.addAll(
                ItemTextureQuadConverter.convertTexture(format, transform, filledTexture, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
            builder.addAll(
                ItemTextureQuadConverter.convertTexture(format, transform, filledTexture, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
        }

        return new Baked(this, builder.build(), fluidSprite, format, Maps.immutableEnumMap(transformMap), Maps.newHashMap());
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.client.model.IModel#getDefaultState()
     */
    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }

    /**
     * Sets the liquid in the model. fluid - Name of the fluid in the FluidRegistry If the fluid can't be found, water is used
     *
     * @param customData
     *            the custom data
     * @return the model bottle
     */
    @Override
    public ModelBottle process(ImmutableMap<String, String> customData)
    {
        // DEBUG
        //System.out.println("process method with custom data = ");

        String fluidName = customData.get("fluid");
        Fluid fluid = FluidRegistry.getFluid(fluidName);

        if (fluid == null)
            fluid = this.fluid;

        // create new model with correct liquid
        return new ModelBottle(fluid);
    }

    /**
     * Allows to use different textures for the model.
     *
     * @param textures
     *            the textures
     * @return the model bottle
     */
    @Override
    public ModelBottle retexture(ImmutableMap<String, String> textures)
    {
        // don't allow retexturing
        // DEBUG
        //System.out.println("Retexturing");

        return new ModelBottle(fluid);
    }

    public enum CustomModelLoader implements ICustomModelLoader
    {
        INSTANCE;

        /*
         * (non-Javadoc)
         *
         * @see net.minecraftforge.client.model.ICustomModelLoader#accepts(net.minecraft.util.ResourceLocation)
         */
        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals(MegaCorpMod.modId) && modelLocation.getResourcePath().contains("bottle");
        }

        /*
         * (non-Javadoc)
         *
         * @see net.minecraftforge.client.model.ICustomModelLoader#loadModel(net.minecraft.util.ResourceLocation)
         */
        @Override
        public IModel loadModel(ResourceLocation modelLocation)
        {
            //            // DEBUG
            //            System.out.println("Loading model");

            return MODEL;
        }

        /*
         * (non-Javadoc)
         *
         * @see net.minecraft.client.resources.IResourceManagerReloadListener#onResourceManagerReload(net.minecraft.client.resources.IResourceManager)
         */
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // no need to clear cache since we create a new model instance
        }
    }

    private static final class BakedOverrideHandler extends ItemOverrideList
    {
        public static final BakedOverrideHandler INSTANCE = new BakedOverrideHandler();

        private BakedOverrideHandler()
        {
            super(ImmutableList.<ItemOverride> of());

            //            // DEBUG
            //            System.out.println("Constructing BakedOverrideHandler");
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
        {
            FluidStack fluidStack = Bottle.getFluid(stack);

            if (fluidStack == null || fluidStack.amount <= 0)
            {
                // // DEBUG
                 //System.out.println("fluid stack is null, returning original model");

                // empty bucket
                return originalModel;
            }

            // // DEBUG
            // System.out.println("Fluid stack was not null and fluid amount = "+fluidStack.amount);

            Baked model = (Baked) originalModel;

            Fluid fluid = fluidStack.getFluid();
            String name = fluid.getName();

            if (!model.cache.containsKey(name))
            {
                // DEBUG
                //System.out.println("The model cache does not have key for fluid name");
                IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                    @Override
                    public TextureAtlasSprite apply(ResourceLocation location)
                    {
                        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                    }
                };
                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format,
                    textureGetter);
                model.cache.put(name, bakedModel);
                return bakedModel;
            }

            // // DEBUG
            // System.out.println("The model cache already has key so returning the model");

            return model.cache.get(name);
        }
    }

    // the filled bucket is based on the empty bucket
    private static final class Baked implements IBakedModel
    {

        private final ModelBottle parent;
        // FIXME: guava cache?
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
        private final ImmutableList<BakedQuad> quads;
        private final TextureAtlasSprite particle;
        private final VertexFormat format;

        public Baked(ModelBottle parent,
                     ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format,
                     ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
                     Map<String, IBakedModel> cache)
        {
            this.quads = quads;
            this.particle = particle;
            this.format = format;
            this.parent = parent;
            this.transforms = transforms;
            this.cache = cache;

            //            // DEBUG
            //            System.out.println("Constructing Baked");
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return BakedOverrideHandler.INSTANCE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
        {
            return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            if (side == null)
                return quads;
            return ImmutableList.of();
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return true;
        }

        @Override
        public boolean isGui3d()
        {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return particle;
        }
    }
}