package games.league;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Scanner;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ec.simple.SimpleFitness;
import ec.vector.DoubleVectorIndividual;

public class WPCSequenceVisualizer {

	private static final int WPC_LENGTH = 25;
	private static final int WPC_SIZE = 5;

	public WPCSequenceVisualizer() {

	}

	private PdfPTable createTable(double[] wpc) {
		System.err.println(Arrays.toString(wpc));

		PdfPTable table = new PdfPTable(WPC_SIZE);

		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;

		for (int i = 0; i < WPC_LENGTH; i++) {
			// max = Math.max(Math.abs(wpc[i]), max);
			max = Math.max((float) wpc[i], max);
			min = Math.min((float) wpc[i], min);
		}

		for (int i = 0; i < WPC_LENGTH; i++) {
			PdfPCell cell = new PdfPCell(new Paragraph(" "));
			cell.setBorder(Rectangle.BOX);
			cell.setBorderColor(new GrayColor(0.7f));
			cell.setPadding(0.8f);

			// cell.setBackgroundColor(new GrayColor((max - (float)wpc[i]) /
			// (max - min)));
			cell.setBackgroundColor(new GrayColor(
					0.5f - ((float) wpc[i] / 2.0f)));
			// cell.setBackgroundColor(new GrayColor(0.5f - (float)wpc[i]));

			// if (wpc[i] >= 0) {
			// cell.setBackgroundColor(new Color(0, Math.min(1, (float)wpc[i] /
			// (float)max), 0));
			// } else {
			// cell.setBackgroundColor(new Color(Math.min(1,
			// Math.abs((float)wpc[i] / (float)max)), 0, 0));
			// }
			table.addCell(cell);
		}

		return table;
	}

	public void visualize(String fileIn, String fileOut, int limit,
			int frequency) {
		Document document = new Document(PageSize.A4, 0, 0, 0, 0);
		document.setMarginMirroring(true);

		try {
			PdfWriter.getInstance(document, new FileOutputStream(fileOut));
			document.open();

			PdfPTable table = new PdfPTable(4);
			Scanner sc = new Scanner(new FileReader(fileIn));
			for (int gen = 0; gen <= limit; gen++) {
				for (int i = 0; i < 8; i++) {
					sc.next();
				}

				double[] wpc = new double[WPC_LENGTH];
				for (int i = 0; i < WPC_LENGTH; i++) {
					wpc[i] = sc.nextDouble();
				}

				if ((gen % frequency) == 0) {
					PdfPCell cell = new PdfPCell(createTable(wpc));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPadding(8.0f);
					table.addCell(cell);
				}
			}

			table.setComplete(true);
			table.setWidthPercentage(80);
			document.add(table);

		} catch (Exception e) {
			e.printStackTrace();
		}

		document.close();
	}

	public void visualize2(String fileIn, String fileOut, int limit,
			int frequency) {
		DoubleVectorIndividual ind = new DoubleVectorIndividual();
		ind.fitness = new SimpleFitness();
		ind.genome = new double[WPC_LENGTH];

		Document document = new Document(PageSize.A4, 0, 0, 0, 0);
		document.setMarginMirroring(true);

		try {
			PdfWriter.getInstance(document, new FileOutputStream(fileOut));
			document.open();

			PdfPTable table = new PdfPTable(5);
			LineNumberReader reader = new LineNumberReader(new FileReader(
					fileIn));

			// reader.readLine();
			// ind.readIndividual(null, reader);

			for (int gen = 0; gen < limit; gen++) {
				reader.readLine();
				ind.readIndividual(null, reader);

				if ((gen % frequency) == 0) {
					PdfPCell cell = new PdfPCell(createTable(ind.genome));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPadding(8.0f);
					table.addCell(cell);
				}
			}

			table.setComplete(true);
			table.setWidthPercentage(75);
			document.add(table);

		} catch (Exception e) {
			e.printStackTrace();
		}

		document.close();
	}

	public static void main(String[] args) {
		WPCSequenceVisualizer vis = new WPCSequenceVisualizer();

		vis.visualize2("ctdl-mc-go-2/ind_787.stat", "out.pdf", 50, 1);

		// File inputDir = new File("ctdl-go");
		// for (String file : inputDir.list()) {
		// System.out.println(file);
		// vis.visualize2("ctdl-go/" + file, file + ".pdf", 50, 1);
		// }
	}
}