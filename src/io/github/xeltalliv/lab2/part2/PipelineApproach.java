package io.github.xeltalliv.lab2.part2;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import io.github.xeltalliv.lab2.SpeedTest;

public class PipelineApproach {
	private static final String sourceDir = "src/io/github/xeltalliv/lab2/part2/input.nobackup";;
	private static final String targetDir = "src/io/github/xeltalliv/lab2/part2/output.nobackup";;
	private static final String watermarkText = "[Modified]";

	private static ProducerConsumerQueue<File> fileQueue;
	private static ProducerConsumerQueue<FileImagePair> imageDataInQueue;
	private static ProducerConsumerQueue<FileImagePair> imageDataOutQueue;

	private record FileImagePair(File file, BufferedImage image) {}

	public static void main(String[] args) {
		SpeedTest.repeatCount = 1;
		SpeedTest.test(() -> {

			int imageReadingThreadCount = 5;
			int imageProcessingThreadCount = 1;
			int imageWritingThreadCount = 5;

			new File(targetDir).mkdirs();

			fileQueue = new ProducerConsumerQueue<>(10, 1);
			imageDataInQueue = new ProducerConsumerQueue<>(10, imageReadingThreadCount);
			imageDataOutQueue = new ProducerConsumerQueue<>(10, imageProcessingThreadCount);

			ArrayList<Thread> threads = new ArrayList<>(); 

			threads.add(new Thread(PipelineApproach::producer));
			for(int i=0; i<imageReadingThreadCount; i++) {
				threads.add(new Thread(PipelineApproach::readImages));
			}
			for(int i=0; i<imageProcessingThreadCount; i++) {
				threads.add(new Thread(PipelineApproach::processImages));
			}
			for(int i=0; i<imageWritingThreadCount; i++) {
				threads.add(new Thread(PipelineApproach::writeImages));
			}

			try {
				for(Thread thread : threads) thread.start();
				for(Thread thread : threads) thread.join();
			} catch (InterruptedException e) {
			}
			System.out.println("Done!");
		});
	}

	private static void producer() {
		File folder = new File(sourceDir);
		File[] listOfFiles = folder.listFiles();

		try {
			if (listOfFiles != null) {
				for (File file : listOfFiles) {
					fileQueue.put(file);
				}
			}
			System.out.println("Stopping producer");
			fileQueue.endProducer();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void readImages() {
		try {
			while (true) {
				File sourceFile = fileQueue.take();
				if (sourceFile == null) break;
				imageDataInQueue.put(readOneImage(sourceFile));
			}
			System.out.println("Stopping readImages");
			imageDataInQueue.endProducer();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static FileImagePair readOneImage(File sourceFile) {
		try {
			System.out.println("Reading: " + sourceFile.getName());
			BufferedImage originalImage = ImageIO.read(sourceFile);
			if (originalImage == null) {
				throw new IOException("Could not decode image");
			}
			return new FileImagePair(sourceFile, originalImage);
		} catch (IOException e) {
			System.err.println("Failed to process " + sourceFile.getName() + ": " + e.getMessage());
			return null;
		}
	}


	private static void processImages() {
		try {
			while (true) {
				FileImagePair fileImageIn = imageDataInQueue.take();
				if (fileImageIn == null) break;
				imageDataOutQueue.put(processOneImage(fileImageIn));
			}
			System.out.println("Stoppong processImages");
			imageDataOutQueue.endProducer();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static FileImagePair processOneImage(FileImagePair fileImageIn) {
		File sourceFile = fileImageIn.file();
		BufferedImage originalImage = fileImageIn.image();

		System.out.println("Processing: " + sourceFile.getName());

		BufferedImage grayscaleImage = new BufferedImage(
				originalImage.getWidth(), 
				originalImage.getHeight(), 
				BufferedImage.TYPE_BYTE_GRAY
				);
		Graphics2D g2d = grayscaleImage.createGraphics();
		g2d.drawImage(originalImage, 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(new Font("Arial", Font.BOLD, 30));
		g2d.setColor(new Color(255, 255, 255, 128));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int x = grayscaleImage.getWidth() - fontMetrics.stringWidth(watermarkText) - 20;
		int y = grayscaleImage.getHeight() - 20;
		g2d.drawString(watermarkText, x, y);
		g2d.dispose();

		return new FileImagePair(sourceFile, grayscaleImage);
	}


	private static void writeImages() {
		try {
			while (true) {
				FileImagePair fileImageOut = imageDataOutQueue.take();
				if (fileImageOut == null) break;
				writeOneImage(fileImageOut);
			}
			System.out.println("Stopping writeImage");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void writeOneImage(FileImagePair fileImageOut) {
		File sourceFile = fileImageOut.file();
		BufferedImage grayscaleImage = fileImageOut.image();
		File targetFile = new File(targetDir, sourceFile.getName());

		try {
			System.out.println("Writing: " + sourceFile.getName());
			ImageIO.write(grayscaleImage, "png", targetFile);
		} catch (IOException e) {
			System.err.println("Failed to process " + sourceFile.getName() + ": " + e.getMessage());
		}
	}
}
