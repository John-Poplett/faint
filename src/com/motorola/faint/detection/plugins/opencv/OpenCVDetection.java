/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
 *
 * |  This program is free software: you can redistribute it and/or modify   |
 *    it under the terms of the GNU General Public License as published by
 * |  the Free Software Foundation, either version 3 of the License, or      |
 *    (at your option) any later version.
 * |                                                                         |
 *    This program is distributed in the hope that it will be useful,
 * |  but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * |  GNU General Public License for more details.                           |
 *
 * |  You should have received a copy of the GNU General Public License      |
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * |                                                                         |
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 *******************************************************************************/

package com.motorola.faint.detection.plugins.opencv;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;

import javax.swing.JPanel;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Utilities;
import de.offis.faint.interfaces.IDetectionPlugin;
import de.offis.faint.interfaces.ISwingCustomizable;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class OpenCVDetection implements IDetectionPlugin, ISwingCustomizable, Serializable {

	private static final long serialVersionUID = -9011612824955017486L;

	protected static final String SUBFOLDER = "opencv";

	private static final String[] CASCADES = {
			"haarcascade_frontalface_default.xml",
			"haarcascade_frontalface_alt.xml",
			"haarcascade_frontalface_alt_tree.xml",
			"haarcascade_frontalface_alt2.xml",
			"haarcascade_profileface.xml" };

	// @serial
	private String currentCascadeFile = CASCADES[0];

	// @serial
	private float scaleFactor = 1.1f;

	private transient OpenCVSettingsPanel settingsPanel = null;

	// @serial
	private int scanWindowSize;

	transient CvMemStorage storage = null;

	transient CvHaarClassifierCascade classifier = null;

	private boolean isValidState() {
		return classifier != null && classifier instanceof CvHaarClassifierCascade;
	}

	private void validateState() {
		if(!isValidState()) {
			throw new IllegalArgumentException("classifier field not initialized properly");
		}
	}

	/**
	 * Constructor.
	 *
	 */
	public OpenCVDetection(){

		File dataFolder = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + SUBFOLDER);
		dataFolder.mkdirs();

		// copy cascade files to disk, if necessary
		for (String cascade : CASCADES){

			String file = dataFolder.getAbsolutePath() + File.separator + cascade;
			File destinationFile = new File(file);

			if (!destinationFile.exists())
				try {
					URL cascadeURL = OpenCVDetection.class.getResource(cascade);
					Utilities.saveFileFromURL(cascadeURL, destinationFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		// Load native libraries
		loadLibraries(dataFolder);
        validateState();

	}

	private void loadLibraries(File dataFolder) {
		storage = CvMemStorage.create();

		final String classifierName = getCascade();

		 // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
        	// FIXME - throw an exception here?
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

	}


	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.detection.IDetectionPlugin#detectFaces(java.lang.String)
	 */
	public Region[] detectFaces(String file, int scanWindowSize) {

		/*
		 * FIXME - workaround when object readObject is not called on start-up and
		 * the class object is not fully deserialized. Patch is here until the real
		 * issue is finally debugged.
		 */
		if(!isValidState()) {
			File dataFolder = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + SUBFOLDER);
			loadLibraries(dataFolder);
		}
		validateState();
		this.scanWindowSize = scanWindowSize;
		IplImage image = cvLoadImage(file);
		final int width = image.width();
		final int height = image.height();
		IplImage grayImage = IplImage.create(width, height, IPL_DEPTH_8U, 1);
		cvCvtColor(image, grayImage, CV_BGR2GRAY);
		CvSeq faces = cvHaarDetectObjects(grayImage, this.classifier, storage, 1.1, 3,
				CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
		final int total = faces.total();
		Region[] regions = new Region[total];
		for (int i = 0; i < total; i++) {
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			final int w = r.width(), h = r.height();
			final int x = r.x() + w/2, y = r.y() + h/2;
			regions[i] = new Region(x, y, w, h, 0.0, file);
			r.close();
		}
		cvClearMemStorage(storage);
		return regions;
	}

	public String getCurrentCascadeFile(){
		return currentCascadeFile;
	}

	public String getCascade() {
		return MainController.getInstance().getDataDir().getAbsolutePath() + File.separator + SUBFOLDER + File.separator + this.currentCascadeFile;
	}

	public void setCascade(String cascade) {
		this.currentCascadeFile = cascade;
	}

	public int getMinSize(){
		return this.scanWindowSize;
	}

	public void setScale(float scaleFactor){
		this.scaleFactor = scaleFactor;
	}

	public float getScale(){
		return scaleFactor;
	}

	public String toString(){
		return getName();
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getName()
	 */
	public String getName() {
		return "OpenCV Haarclassifier Detection";
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getRequirementNotes()
	 */
	public String getDescription() {
		return "<p>The OpenCV Haarclassifier Detection uses javacv.</p>";
	}

	public String getCopyrightNotes() {
		InputStream is = OpenCVDetection.class.getResourceAsStream("license.txt");
		String notes = null;
		try {
			notes = Utilities.inputStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notes;
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getSettingsPanel()
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null)
			settingsPanel = new OpenCVSettingsPanel(this);
		return settingsPanel;
	}

	/**
	 * Method that reinitializes the Haar clsssifier on deserialization.
	 *
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		File dataFolder = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + SUBFOLDER);
		loadLibraries(dataFolder);
        validateState();
	}

//	/*
//	 * For testing purpose
//	 */
public static void main(String[] args){
		OpenCVDetection detection = new OpenCVDetection();
		//Region[] regions = detection.detectFaces("C:\\test.jpg", 30);
		Region[] regions = detection.detectFaces("/Users/vcdt34/Downloads/lfw/George_W_Bush/George_w_Bush_0001.jpg", 30);
		for (Region region : regions){
			System.out.println(region);
		}
	}
}
