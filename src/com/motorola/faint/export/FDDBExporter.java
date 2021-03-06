package com.motorola.faint.export;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.SwingWorker;

import de.offis.faint.controller.MainController;
import de.offis.faint.model.FaceDatabase;
import de.offis.faint.model.Region;

public class FDDBExporter extends SwingWorker<String, String> {

	protected String[] files;
	protected int filesProcessed;
	protected String dbName;
	FaceDatabase db;

	public FDDBExporter(String dbName) {
		this.dbName = dbName;
		db = MainController.getInstance().getFaceDB();
		files = db.getKnownFiles();
		java.util.Arrays.sort(files);
	}

	// http://stackoverflow.com/questions/34459486/joining-paths-in-java
	private static String getRelativePath(String absPath, String baseDir) {
		String[] pieces = absPath.split("/");
		for (int index = 0; index < pieces.length - 1; index++) {
			if (pieces[index].equals(baseDir)) {
				pieces = Arrays.copyOfRange(pieces, index + 1, pieces.length);
				Path dest = Arrays.stream(pieces).reduce(/* identity */ Paths.get(""), /* accumulator */ Path::resolve,
						/* combiner */ Path::resolve);
				return dest.toString();
			}
		}
		return absPath;
	}

	/*
	 * Helper function to strip the extension from the end of a file path.
	 */
	static private String trimExtension(String filePath) {
		return filePath.split("\\.(?=[^\\.]+$)")[0];
	}

	public void export(OutputStream out) {
		PrintStream printStream = new PrintStream(out);
		printStream.println("Let's get started!");
		for (filesProcessed = 0; filesProcessed < files.length; filesProcessed++) {
			final String file = files[filesProcessed];
			Region[] regions = db.getRegionsForImage(file);
			if (regions != null && regions.length > 0) {
				printStream.println(trimExtension(getRelativePath(file, dbName)));
				printStream.format("%d\n", regions.length);
				for (Region region : regions) {
					printStream.println(region.toFDDBString());
				}
			}
		}
	}

	@Override
	protected String doInBackground() throws Exception {
		export(System.out);
		return "Finished";
	}

	public static void main(String[] args) {
		FDDBExporter exporter = new FDDBExporter("data");
		exporter.export(System.out);
		System.exit(0);
	}
}
