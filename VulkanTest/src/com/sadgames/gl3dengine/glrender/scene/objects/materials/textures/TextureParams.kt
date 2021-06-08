package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.TextureParams.TextureFilter.*
import org.lwjgl.opengl.GL20

class TextureParams(val filterMode: TextureFilter, val wrapMode: TextureWrap) {
    enum class TextureFilter(val gLEnum: Int) {
        /** Fetch the nearest texel that best maps to the pixel on screen.  */
        Nearest(GL20.GL_NEAREST),

        /** Fetch four nearest texels that best maps to the pixel on screen.  */
        Linear(GL20.GL_LINEAR),

        /** @see TextureFilter.MipMapLinearLinear
         */
        MipMap(GL20.GL_LINEAR_MIPMAP_LINEAR),

        /** Fetch the best fitting image from the mip map chain based on the pixel/texel ratio and then sample the texels with a
         * nearest filter.  */
        MipMapNearestNearest(GL20.GL_NEAREST_MIPMAP_NEAREST),

        /** Fetch the best fitting image from the mip map chain based on the pixel/texel ratio and then sample the texels with a
         * linear filter.  */
        MipMapLinearNearest(GL20.GL_LINEAR_MIPMAP_NEAREST),

        /** Fetch the two best fitting images from the mip map chain and then sample the nearest texel from each of the two images,
         * combining them to the final output pixel.  */
        MipMapNearestLinear(GL20.GL_NEAREST_MIPMAP_LINEAR),

        /** Fetch the two best fitting images from the mip map chain and then sample the four nearest texels from each of the two
         * images, combining them to the final output pixel.  */
        MipMapLinearLinear(GL20.GL_LINEAR_MIPMAP_LINEAR);

        val isMipMap: Boolean
            get() = gLEnum != GL20.GL_NEAREST && gLEnum != GL20.GL_LINEAR

    }

    enum class TextureWrap(val gLEnum: Int) {
        MirroredRepeat(GL20.GL_MIRRORED_REPEAT), ClampToEdge(GL20.GL_CLAMP_TO_EDGE), Repeat(GL20.GL_REPEAT);

    }

    constructor(useMipMap: Boolean): this(if (useMipMap) MipMapLinearLinear else Linear, TextureWrap.Repeat)
}