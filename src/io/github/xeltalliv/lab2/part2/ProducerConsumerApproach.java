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

public class ProducerConsumerApproach {
	private static final String sourceDir = "src/io/github/xeltalliv/lab2/part2/input.nobackup";;
	private static final String targetDir = "src/io/github/xeltalliv/lab2/part2/output.nobackup";;
	private static final String watermarkText = "[Modified]";

	private static ProducerConsumerQueue<File> fileQueue;

	private record FileImagePair(File file, BufferedImage image) {}

	public static void main(String[] args) {
		SpeedTest.repeatCount = 1;
		SpeedTest.test(() -> {

			int consumerThreadCount = 11;

			new File(targetDir).mkdirs();

			fileQueue = new ProducerConsumerQueue<>(10, 1);

			ArrayList<Thread> threads = new ArrayList<>(); 

			threads.add(new Thread(ProducerConsumerApproach::producer));
			for(int i=0; i<consumerThreadCount; i++) {
				threads.add(new Thread(ProducerConsumerApproach::consumer));
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

	private static void consumer() {
		try {
			while (true) {
				File sourceFile = fileQueue.take();
				if (sourceFile == null) break;
				FileImagePair imageIn = readOneImage(sourceFile);
				FileImagePair imageOut = processOneImage(imageIn);
				writeOneImage(imageOut);
			}
			System.out.println("Stopping readImages");
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
