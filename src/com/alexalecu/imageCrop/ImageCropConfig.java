/*
 * Copyright (C) 2010 Alex Cojocaru
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alexalecu.imageCrop;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;

import com.alexalecu.imageUtil.ImageSelectMethod;

/**
 * define the current status of an image in the selection panel
 */
public class ImageCropConfig implements Cloneable {
	
	private File imageFile; // the filename of the current image
	private double scaleFactor; // the scale factor
	private Color bgColor; // the background color
	private int bgTolerance; // the tolerance for the background color
	private Rectangle selectionRect; // the selection rectangle properties
	private ImageCropState state; // the image state
	private ImageSelectMethod selectMethod; // the select method to be used for auto-selecting


	/**
	 * create a new instance, setting the scale factor to 1, the background color to black, the
	 * selection rectangle to 0 and the image state to STATE_INIT
	 */
	public ImageCropConfig() {
		scaleFactor = 1d;
		bgColor = Color.BLACK;
		bgTolerance = 3;
		selectionRect = null;
		state = ImageCropState.StateInit;
		selectMethod = ImageSelectMethod.SelectMinimum;
	}

	/**
	 * @return the image file name
	 */
	public File getImageFile() {
		return imageFile;
	}

	/**
	 * set the image file name
	 * @param imageFile
	 */
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}

	/**
	 * @return the scale factor
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * set the scale factor
	 * @param scaleFactor
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * @return the background color
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * set the background color
	 * @param bgColor
	 */
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * @return the background color tolerance
	 */
	public int getBgTolerance() {
		return bgTolerance;
	}

	/**
	 * set the background color tolerance
	 * @param bgTolerance
	 */
	public void setBgTolerance(int bgTolerance) {
		this.bgTolerance = bgTolerance;
	}

	/**
	 * @return the selection rectangle
	 */
	public Rectangle getSelectionRect() {
		return selectionRect;
	}

	/**
	 * set the selection rectangle
	 * @param selectionRect
	 */
	public void setSelectionRect(Rectangle selectionRect) {
		this.selectionRect = selectionRect;
	}

	/**
	 * @return the current image state
	 */
	public ImageCropState getState() {
		return state;
	}

	/**
	 * set the current image state
	 * @param state
	 */
	public void setState(ImageCropState state) {
		this.state = state;
	}
	
	/**
	 * @return the select method to be used for auto-selection
	 */
	public ImageSelectMethod getSelectMethod() {
		return selectMethod;
	}

	/**
	 * set the select method to be used for auto-selection
	 * @param selectMethod
	 */
	public void setSelectMethod(ImageSelectMethod selectMethod) {
		this.selectMethod = selectMethod;
	}

	/**
	 * @return true if the selection rectangle is valid
	 */
	public boolean isSelection() {
		return selectionRect.x != 0 || selectionRect.y != 0 ||
			selectionRect.width != 0 || selectionRect.height != 0;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ImageCropConfig imageCropConfig = new ImageCropConfig();
		
		imageCropConfig.imageFile = imageFile;
		imageCropConfig.scaleFactor = scaleFactor;
		imageCropConfig.bgColor = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
		imageCropConfig.bgTolerance = bgTolerance;
		imageCropConfig.state = state;
		imageCropConfig.selectMethod = selectMethod;
		imageCropConfig.selectionRect = new Rectangle(selectionRect.x, selectionRect.y,
				selectionRect.width, selectionRect.height);
		
		return imageCropConfig;
	}

}
