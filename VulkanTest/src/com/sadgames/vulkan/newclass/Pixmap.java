/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.sadgames.vulkan.newclass;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Pixmap {
	/** Different pixel formats.
	 *
	 * @author mzechner */
	public enum Format {
		Alpha, Intensity, LuminanceAlpha, RGB565, RGBA4444, RGB888, RGBA8888;

		public static int toGdx2DPixmapFormat (Format format) {
			if (format == Alpha) return Gdx2DPixmap.GDX2D_FORMAT_ALPHA;
			if (format == Intensity) return Gdx2DPixmap.GDX2D_FORMAT_ALPHA;
			if (format == LuminanceAlpha) return Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA;
			if (format == RGB565) return Gdx2DPixmap.GDX2D_FORMAT_RGB565;
			if (format == RGBA4444) return Gdx2DPixmap.GDX2D_FORMAT_RGBA4444;
			if (format == RGB888) return Gdx2DPixmap.GDX2D_FORMAT_RGB888;
			if (format == RGBA8888) return Gdx2DPixmap.GDX2D_FORMAT_RGBA8888;
			throw new RuntimeException("Unknown Format: " + format);
		}

		public static Format fromGdx2DPixmapFormat (int format) {
			if (format == Gdx2DPixmap.GDX2D_FORMAT_ALPHA) return Alpha;
			if (format == Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA) return LuminanceAlpha;
			if (format == Gdx2DPixmap.GDX2D_FORMAT_RGB565) return RGB565;
			if (format == Gdx2DPixmap.GDX2D_FORMAT_RGBA4444) return RGBA4444;
			if (format == Gdx2DPixmap.GDX2D_FORMAT_RGB888) return RGB888;
			if (format == Gdx2DPixmap.GDX2D_FORMAT_RGBA8888) return RGBA8888;
			throw new RuntimeException("Unknown Gdx2DPixmap Format: " + format);
		}

		public static int toGlFormat (Format format) {
			return Gdx2DPixmap.toGlFormat(toGdx2DPixmapFormat(format));
		}

		public static int toGlType (Format format) {
			return Gdx2DPixmap.toGlType(toGdx2DPixmapFormat(format));
		}
	}

	public enum Blending {
		None, SourceOver
	}

	public enum Filter {
		NearestNeighbour, BiLinear
	}

	private Blending blending = Blending.SourceOver;
	private Filter filter = Filter.BiLinear;

	final Gdx2DPixmap pixmap;
	int color = 0;

	private boolean disposed;

	/** Sets the type of {@link Blending} to be used for all operations. Default is {@link Blending#SourceOver}.
	 * @param blending the blending type */
	/*public void setBlending (Blending blending) {
		this.blending = blending;
		pixmap.setBlend(blending == Blending.None ? 0 : 1);
	}*/

	/** Sets the type of interpolation {@link Filter} to be used in conjunction with
	 * {@link Pixmap#drawPixmap(Pixmap, int, int, int, int, int, int, int, int)}.
	 * @param filter the filter. */
	/*public void setFilter (Filter filter) {
		this.filter = filter;
		pixmap.setScale(filter == Filter.NearestNeighbour ? Gdx2DPixmap.GDX2D_SCALE_NEAREST : Gdx2DPixmap.GDX2D_SCALE_LINEAR);
	}*/

	/** Creates a new Pixmap instance with the given width, height and format.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param format the {@link Format} */
	public Pixmap (int width, int height, Format format, int fillColor) {
		pixmap = new Gdx2DPixmap(width, height, Format.toGdx2DPixmapFormat(format), fillColor);
	}

	/** Creates a new Pixmap instance from the given encoded image data. The image can be encoded as JPEG, PNG or BMP.
	 * @param encodedData the encoded image data
	 * @param offset the offset
	 * @param len the length */
	public Pixmap (ByteBuffer encodedData, int offset, int len) {
		try {
			pixmap = new Gdx2DPixmap(encodedData, offset, len, 0);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load pixmap from image data", e);
		}
	}

	public Pixmap (File file) {
		try {
			ByteBuffer encodedData = FileUtils.readBuffer(file);
			pixmap = new Gdx2DPixmap(encodedData, 0, encodedData.limit(), 0);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load file: " + file, e);
		}
	}

	/** Constructs a new Pixmap from a {@link Gdx2DPixmap}.
	 * @param pixmap */
	public Pixmap (Gdx2DPixmap pixmap) {
		this.pixmap = pixmap;
	}

	/** Sets the color for the following drawing operations
	 * @param color the color, encoded as RGBA8888 */
	public void setColor (int color) {
		this.color = color;
	}

	/** Sets the color for the following drawing operations.
	 *
	 * @param r The red component.
	 * @param g The green component.
	 * @param b The blue component.
	 * @param a The alpha component. */
	public void setColor (float r, float g, float b, float a) {
		color = new Color(r, g, b, a).getRGB();
	}

	/** Sets the color for the following drawing operations.
	 * @param color The color. */
	public void setColor (Color color) {
		this.color = color.getRGB();
	}

	/** Fills the complete bitmap with the currently set color. */
	/*public void fill () {
		pixmap.clear(color);
	}*/

// /**
// * Sets the width in pixels of strokes.
// *
// * @param width The stroke width in pixels.
// */
// public void setStrokeWidth (int width);

	/*public void drawLine (int x, int y, int x2, int y2) {
		pixmap.drawLine(x, y, x2, y2, color);
	}*/

	/*public void drawRectangle (int x, int y, int width, int height) {
		pixmap.drawRect(x, y, width, height, color);
	}*/

	/*public void drawPixmap (Pixmap pixmap, int x, int y) {
		drawPixmap(pixmap, x, y, 0, 0, pixmap.getWidth(), pixmap.getHeight());
	}*/

	/*public void drawPixmap (Pixmap pixmap, int x, int y, int srcx, int srcy, int srcWidth, int srcHeight) {
		this.pixmap.drawPixmap(pixmap.pixmap, srcx, srcy, x, y, srcWidth, srcHeight);
	}*/

	/*public void drawPixmap (Pixmap pixmap, int srcx, int srcy, int srcWidth, int srcHeight, int dstx, int dsty, int dstWidth,
		int dstHeight) {
		this.pixmap.drawPixmap(pixmap.pixmap, srcx, srcy, srcWidth, srcHeight, dstx, dsty, dstWidth, dstHeight);
	}*/

	/*public void fillRectangle (int x, int y, int width, int height) {
		pixmap.fillRect(x, y, width, height, color);
	}*/

	/*public void drawCircle (int x, int y, int radius) {
		pixmap.drawCircle(x, y, radius, color);
	}*/

	/*public void fillCircle (int x, int y, int radius) {
		pixmap.fillCircle(x, y, radius, color);
	}

	public void fillTriangle (int x1, int y1, int x2, int y2, int x3, int y3) {
		pixmap.fillTriangle(x1, y1, x2, y2, x3, y3, color);
	}*/

	public int getPixel (int x, int y) {
		return pixmap.getPixel(x, y);
	}

	/** @return The width of the Pixmap in pixels. */
	public int getWidth () {
		return pixmap.getWidth();
	}

	/** @return The height of the Pixmap in pixels. */
	public int getHeight () {
		return pixmap.getHeight();
	}

	/** Releases all resources associated with this Pixmap. */
	public void dispose () {
		if (disposed) throw new RuntimeException("Pixmap already disposed!");
		pixmap.dispose();
		disposed = true;
	}

	public boolean isDisposed () {
		return disposed;
	}

	/*public void drawPixel (int x, int y) {
		pixmap.setPixel(x, y, color);
	}*/

	/*public void drawPixel (int x, int y, int color) {
		pixmap.setPixel(x, y, color);
	}*/

	public int getGLFormat () {
		return pixmap.getGLFormat();
	}

	public int getGLInternalFormat () {
		return pixmap.getGLInternalFormat();
	}

	public int getGLType () {
		return pixmap.getGLType();
	}

	/** Returns the direct ByteBuffer holding the pixel data. For the format Alpha each value is encoded as a byte. For the format
	 * LuminanceAlpha the luminance is the first byte and the alpha is the second byte of the pixel. For the formats RGB888 and
	 * RGBA8888 the color components are stored in a single byte each in the order red, green, blue (alpha). For the formats RGB565
	 * and RGBA4444 the pixel colors are stored in shorts in machine dependent order.
	 * @return the direct {@link ByteBuffer} holding the pixel data. */
	public ByteBuffer getPixels () {
		if (disposed) throw new RuntimeException("Pixmap already disposed");
		return pixmap.getPixels();
	}

	public void setPixels(ByteBuffer pixels) {
		pixmap.setPixels(pixels);
	}

	/** @return the {@link Format} of this Pixmap. */
	public Format getFormat () {
		return Format.fromGdx2DPixmapFormat(pixmap.getFormat());
	}

	/** @return the currently set {@link Blending} */
	public Blending getBlending () {
		return blending;
	}

	/** @return the currently set {@link Filter} */
	public Filter getFilter (){
		return filter;
	}
}
