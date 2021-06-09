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

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.sadgames.sysutils.common.ColorUtils;

import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;


/** @author mzechner */
public class Gdx2DPixmap {
	public static final int GDX2D_FORMAT_ALPHA = 1;
	public static final int GDX2D_FORMAT_LUMINANCE_ALPHA = 2;
	public static final int GDX2D_FORMAT_RGB888 = 3;
	public static final int GDX2D_FORMAT_RGBA8888 = 4;
	public static final int GDX2D_FORMAT_RGB565 = 5;
	public static final int GDX2D_FORMAT_RGBA4444 = 6;

	public static final int GDX2D_SCALE_NEAREST = 0;
	public static final int GDX2D_SCALE_LINEAR = 1;

	public static final int GDX2D_BLEND_NONE = 0;
	public static final int GDX2D_BLEND_SRC_OVER = 1;

	public static int toGlFormat (int format) {
		switch (format) {
		case GDX2D_FORMAT_ALPHA:
			return GL20.GL_ALPHA;
		case GDX2D_FORMAT_LUMINANCE_ALPHA:
			return GL20.GL_LUMINANCE_ALPHA;
		case GDX2D_FORMAT_RGB888:
		case GDX2D_FORMAT_RGB565:
			return GL20.GL_RGB;
		case GDX2D_FORMAT_RGBA8888:
		case GDX2D_FORMAT_RGBA4444:
			return GL20.GL_RGBA;
		default:
			throw new RuntimeException("unknown format: " + format);
		}
	}

	public static int toGlType (int format) {
		switch (format) {
		case GDX2D_FORMAT_ALPHA:
		case GDX2D_FORMAT_LUMINANCE_ALPHA:
		case GDX2D_FORMAT_RGB888:
		case GDX2D_FORMAT_RGBA8888:
			return GL20.GL_UNSIGNED_BYTE;
		case GDX2D_FORMAT_RGB565:
			return GL20.GL_UNSIGNED_SHORT_5_6_5;
		case GDX2D_FORMAT_RGBA4444:
			return GL20.GL_UNSIGNED_SHORT_4_4_4_4;
		default:
			throw new RuntimeException("unknown format: " + format);
		}
	}

	int width;
	int height;
	int format;
	ByteBuffer pixelPtr;
	long[] nativeData = new long[4];

	public Gdx2DPixmap (ByteBuffer encodedData, int offset, int len, int requestedFormat) throws IOException {
		pixelPtr = load(encodedData, offset, len);
		if (pixelPtr == null) throw new IOException("Error loading image: " + getFailureReason());
		if (encodedData != null)
			encodedData.clear();

		/*if (requestedFormat != 0 && requestedFormat != format) {
			convert(requestedFormat);
		}*/
	}

	public Gdx2DPixmap (ByteBuffer encodedData, int scaleFactor)  throws IOException {
		BufferedImage srcImage = ImageIO.read(new ByteBufferBackedInputStream(encodedData));
		encodedData.clear();

		pixelPtr =
			createPixmap(Scalr.resize(srcImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, srcImage.getWidth() / scaleFactor, srcImage.getHeight() / scaleFactor, Scalr.OP_ANTIALIAS));
	}

	/*public Gdx2DPixmap (InputStream in, int requestedFormat) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int readBytes = 0;

		while ((readBytes = in.read(buffer)) != -1) {
			bytes.write(buffer, 0, readBytes);
		}

		buffer = bytes.toByteArray();
		pixelPtr = load(nativeData, buffer, 0, buffer.length);
		if (pixelPtr == null) throw new IOException("Error loading pixmap: " + getFailureReason());

		basePtr = nativeData[0];
		width = (int)nativeData[1];
		height = (int)nativeData[2];
		format = (int)nativeData[3];

		if (requestedFormat != 0 && requestedFormat != format) {
			convert(requestedFormat);
		}
	}*/

	public Gdx2DPixmap(BufferedImage img) {
		pixelPtr = createPixmap(img);
	}

	public Gdx2DPixmap(int width, int height, int format, int fillColor) throws RuntimeException {
		pixelPtr = newImage(width, height, format, fillColor);
	}

	public Gdx2DPixmap(ByteBuffer pixelPtr, long[] nativeData) {
		this.pixelPtr = pixelPtr;
		width = (int)nativeData[0];
		height = (int)nativeData[1];
		format = (int)nativeData[2];
	}

	/*private void convert (int requestedFormat) {
		Gdx2DPixmap pixmap = new Gdx2DPixmap(width, height, requestedFormat);
		pixmap.drawPixmap(this, 0, 0, 0, 0, width, height);
		dispose();
		this.format = pixmap.format;
		this.height = pixmap.height;
		this.nativeData = pixmap.nativeData;
		this.pixelPtr = pixmap.pixelPtr;
		this.width = pixmap.width;
	}*/

	public void dispose () {
		try {
			if(nativeData[3] > 0)
				STBImage.stbi_image_free(pixelPtr);
			pixelPtr.clear();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public void clear (int color) {
		clear(basePtr, color);
	}*/

	/*public void setPixel (int x, int y, int color) {
		setPixel(basePtr, x, y, color);
	}*/

	public int getPixel (int x, int y) {
		//byte[] pixels = new byte[pixelPtr.capacity()];
		//pixelPtr.get(pixels);

		int pixel = pixelPtr.getInt(y * width * 4 + x * 4);
		pixel = ColorUtils.argb((pixel >> 24) & 0xFF,  pixel & 0xFF, (pixel >> 8) & 0xFF, (pixel >> 16) & 0xFF);

		return pixel;
	}

	/*public void drawLine (int x, int y, int x2, int y2, int color) {
		drawLine(basePtr, x, y, x2, y2, color);
	}

	public void drawRect (int x, int y, int width, int height, int color) {
		drawRect(basePtr, x, y, width, height, color);
	}

	public void drawCircle (int x, int y, int radius, int color) {
		drawCircle(basePtr, x, y, radius, color);
	}

	public void fillRect (int x, int y, int width, int height, int color) {
		fillRect(basePtr, x, y, width, height, color);
	}

	public void fillCircle (int x, int y, int radius, int color) {
		fillCircle(basePtr, x, y, radius, color);
	}

	public void fillTriangle (int x1, int y1, int x2, int y2, int x3, int y3, int color) {
		fillTriangle(basePtr, x1, y1, x2, y2, x3, y3, color);
	}

	public void drawPixmap (Gdx2DPixmap src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
		drawPixmap(src.basePtr, basePtr, srcX, srcY, width, height, dstX, dstY, width, height);
	}

	public void drawPixmap (Gdx2DPixmap src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth,
		int dstHeight) {
		drawPixmap(src.basePtr, basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
	}

	public void setBlend (int blend) {
		setBlend(basePtr, blend);
	}

	public void setScale (int scale) {
		setScale(basePtr, scale);
	}*/

	/*public static Gdx2DPixmap newPixmap (InputStream in, int requestedFormat) {
		try {
			return new Gdx2DPixmap(in, requestedFormat);
		} catch (IOException e) {
			return null;
		}
	}*/

	public static Gdx2DPixmap newPixmap (int width, int height, int format) {
		try {
			return new Gdx2DPixmap(width, height, format, 0);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public ByteBuffer getPixels () {
		return pixelPtr;
	}

	public void setPixels(ByteBuffer pixels) {
		if (pixelPtr != null) {
			pixelPtr.clear();
			pixelPtr = pixels;
		}
	}

	public int getHeight () {
		return height;
	}

	public int getWidth () {
		return width;
	}

	public int getFormat () {
		return format;
	}

	public int getGLInternalFormat () {
		return toGlFormat(format);
	}

	public int getGLFormat () {
		return getGLInternalFormat();
	}

	public int getGLType () {
		return toGlType(format);
	}

	public String getFormatString () {
		return getFormatString(format);
	}

	static private String getFormatString (int format) {
		switch (format) {
		case GDX2D_FORMAT_ALPHA:
			return "alpha";
		case GDX2D_FORMAT_LUMINANCE_ALPHA:
			return "luminance alpha";
		case GDX2D_FORMAT_RGB888:
			return "rgb888";
		case GDX2D_FORMAT_RGBA8888:
			return "rgba8888";
		case GDX2D_FORMAT_RGB565:
			return "rgb565";
		case GDX2D_FORMAT_RGBA4444:
			return "rgba4444";
		default:
			return "unknown";
		}
	}

	private ByteBuffer load(ByteBuffer input, int offset, int len) {
		ByteBuffer output = null;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			output = STBImage.stbi_load_from_memory(input, w, h, channels, 4);
			if(output == null) {
				throw new Exception("Can't load image " + " " + STBImage.stbi_failure_reason());
			}

			width = w.get();
			height = h.get();
			format = GDX2D_FORMAT_RGBA8888;
			nativeData[3] = 1;

		} catch(Exception e) {
			e.printStackTrace();
		}

		return output;
	}

	private ByteBuffer newImage (int width, int height, int format, int fillColor) {
		BufferedImage image = new BufferedImage(width, height, 2);
		Graphics paint = image.getGraphics();
		paint.setColor(new Color(fillColor));
		paint.fillRect(0,0, width, height);

		return createPixmap(image);
	}

	@NotNull
	private ByteBuffer createPixmap(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
				buffer.put((byte) (pixel & 0xFF));               // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
			}
		}

		buffer.flip();

		this.width = image.getWidth();
		this.height = image.getHeight();
		nativeData[3] = 0;

		return buffer;
	}

	public static String getFailureReason () {
		return STBImage.stbi_failure_reason();
	}
}
