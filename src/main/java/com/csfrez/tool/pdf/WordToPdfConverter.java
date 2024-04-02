package com.csfrez.tool.pdf;

public class WordToPdfConverter {

    public static void main(String[] args) {
        String inputFile = "E:\\tmp\\docx\\1f720f72c6.docx";
        String outputFile = "E:\\tmp\\docx\\" + System.currentTimeMillis() + ".pdf";
        try {
//            InputStream templateInputStream = new FileInputStream(inputFile);
//            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
//            //MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
//
//            FileOutputStream os = new FileOutputStream(outputFile);
//            Docx4J.toPDF(wordMLPackage,os);
//            os.flush();
//            os.close();


//            Converter converter = new Converter(inputFile);
//            converter.convert(outputFile, new PdfConvertOptions());

//            // Load the Word document
//            FileInputStream fis = new FileInputStream(inputFile);
//            XWPFDocument document = new XWPFDocument(fis);
//
//            // Create a PDF document
//            PDDocument pdfDocument = new PDDocument();
//            PDPage page = new PDPage();
//            pdfDocument.addPage(page);
//
//            // Create a content stream for writing to the PDF
//            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
//
//            // Extract text from Word document and write to PDF
//            contentStream.beginText();
//            File file = new File("C:\\Windows\\Fonts\\simfang.ttf");
//            PDType0Font font = PDType0Font.load(pdfDocument, file);
//
//            //PDTrueTypeFont font = PDTrueTypeFont.loadTTF(pdfDocument, file);
//
//            contentStream.setFont(font, 12);
//            for (XWPFParagraph paragraph : document.getParagraphs()) {
//                //contentStream.newLineAtOffset(100, 700); // Adjust position as needed
//                contentStream.showText(paragraph.getText());
//            }
//            contentStream.endText();
//            contentStream.close();
//
//            // Save the PDF document
//            FileOutputStream fos = new FileOutputStream(outputFile);
//            pdfDocument.save(fos);
//            pdfDocument.close();
//
//            System.out.println("PDF created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}