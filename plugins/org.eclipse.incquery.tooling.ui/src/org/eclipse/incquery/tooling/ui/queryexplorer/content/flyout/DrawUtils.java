/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *    Tamas Szabo - code extensions, modifications
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.content.flyout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class DrawUtils {
	
	public static void drawImageCHCV(GC gc, Image image, int x, int y,
			int width, int height) {
		if (image != null) {
			Rectangle imageBounds = image.getBounds();
			int centerX = (width - imageBounds.width) / 2;
			int centerY = y + (height - imageBounds.height) / 2;
			gc.drawImage(image, x + centerX, centerY);
		}
	}

	public static void drawHighlightRectangle(GC gc, int x, int y, int width,
			int height) {
		int right = x + width - 1;
		int bottom = y + height - 1;
		
		Color oldForeground = gc.getForeground();
		try {
			gc.setForeground(FlyoutConstants.buttonLightest);
			gc.drawLine(x, y, right, y);
			gc.drawLine(x, y, x, bottom);
			
			gc.setForeground(FlyoutConstants.buttonDarker);
			gc.drawLine(right, y, right, bottom);
			gc.drawLine(x, bottom, right, bottom);
		} finally {
			gc.setForeground(oldForeground);
		}
	}

	public static Image createRotatedImage(Image srcImage) {
		// prepare Display
		Display display = Display.getCurrent();
		if (display == null) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}
		// rotate ImageData
		ImageData destData;
		{
			ImageData srcData = srcImage.getImageData();
			if (srcData.depth < 8) {
				destData = rotatePixelByPixel(srcData);
			} else {
				destData = rotateOptimized(srcData);
			}
		}
		// create new image
		return new Image(display, destData);
	}

	private static ImageData rotatePixelByPixel(ImageData srcData) {
		ImageData destData = new ImageData(srcData.height, srcData.width,
				srcData.depth, srcData.palette);
		for (int y = 0; y < srcData.height; y++) {
			for (int x = 0; x < srcData.width; x++) {
				destData.setPixel(y, srcData.width - x - 1,
						srcData.getPixel(x, y));
			}
		}
		return destData;
	}

	private static ImageData rotateOptimized(ImageData srcData) {
		int bytesPerPixel = Math.max(1, srcData.depth / 8);
		int destBytesPerLine = ((srcData.height * bytesPerPixel - 1)
				/ srcData.scanlinePad + 1)
				* srcData.scanlinePad;
		byte[] newData = new byte[destBytesPerLine * srcData.width];
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = srcY;
				int destY = srcData.width - srcX - 1;
				int destIndex = destY * destBytesPerLine + destX
						* bytesPerPixel;
				int srcIndex = srcY * srcData.bytesPerLine + srcX
						* bytesPerPixel;
				System.arraycopy(srcData.data, srcIndex, newData, destIndex,
						bytesPerPixel);
			}
		}
		return new ImageData(srcData.height, srcData.width, srcData.depth,
				srcData.palette, srcData.scanlinePad, newData);
	}
}
