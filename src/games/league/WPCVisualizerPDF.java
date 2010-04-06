package games.league;

import java.awt.Color;
import java.io.FileOutputStream;
import java.util.Scanner;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class WPCVisualizerPDF {

	public static void main(String[] args) {
		Document document = new Document(PageSize.A4);

		try {
			PdfWriter.getInstance(document, new FileOutputStream("WPC.pdf"));
			document.open();

			PdfPTable table = new PdfPTable(5);
			Scanner sc = new Scanner(System.in);
			double sum = 0;
			for (int i = 0; i < 25; i++) {
				float w = (float)sc.nextDouble();
				sum += w;
				System.out.println(w);
				PdfPCell cell = new PdfPCell(new Paragraph(" "));
				cell.setPadding(36.0f);
				cell.setBorder(Rectangle.BOX);
				cell.setBorderColor(new GrayColor(0.5f));
				//cell.setBackgroundColor(new GrayColor(0.5f - w));
				if (w >= 0) {
					cell.setBackgroundColor(new GrayColor(w));					
					cell.setBackgroundColor(new Color(0, Math.min(1, w * 3.0f), 0));					
				} else {
					cell.setBackgroundColor(new Color(Math.min(1, Math.abs(w * 3.0f)), 0, 0));
				}

				table.addCell(cell);
			}
			
			System.err.println(sum / 25);
			document.add(table);
		} catch (Exception de) {
			de.printStackTrace();
		}
		document.close();
	}
}