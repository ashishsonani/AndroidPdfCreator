package com.androidtutorialpoint.androidpdfcreator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableHeader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class PdfCreatorActivity extends AppCompatActivity {
    private static final String TAG = "PdfCreatorActivity";
    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private PdfPCell cell;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreator);

        Button mCreateButton = (Button) findViewById(R.id.button_create);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdfWrapper();
                    Toast.makeText(PdfCreatorActivity.this, "Pdf Created", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void createPdfWrapper() throws FileNotFoundException, DocumentException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                        return;
                                    }
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            });
                    return;
                }


                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            createPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(), "Agrify Invoice.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);

        // Open to write
        document.open();

        // Document Settings
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("AgrifyApp");
        document.addCreator("Ashish Sonani");

        /***
         * Variables for further use....
         */
        BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;
        /**
         * How to USE FONT....
         */
        BaseFont urName = null;
        try {
            urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Paragraph paragraph = new Paragraph();
        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        // Title Order Details...
        // Adding Title....
        Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
        Chunk mOrderDetailsTitleChunk = new Chunk("Agrify Order Details", mOrderDetailsTitleFont);
        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
        mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(mOrderDetailsTitleParagraph);
        // Adding Line Breakable Space....
        document.add(new Paragraph(""));
        // Adding Horizontal Line...
        document.add(new Chunk(lineSeparator));
        // Adding Line Breakable Space....
        document.add(new Paragraph(""));

        paragraph = new Paragraph("Customer Support: 9943123999");
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        paragraph = new Paragraph("Email :  agrifyapp@gmail.com");
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Chunk(lineSeparator));

        // a table with three columns
        PdfPTable table = new PdfPTable(3);

        String DemoProductName = "Mango",
                DemoProductQty = "10Kg",
                DemoProductAmount = "1000 Rs.",
                DemoProductSubTotal = "1000 Rs.";
        Font mUserFont = new Font(urName, 12.0f, Font.BOLD, BaseColor.BLACK);

        //Buyer Details
        PdfPCell BuyerDetailsCell = new PdfPCell();
        Paragraph BuyerDetails = new Paragraph("Buyer Details",mUserFont);
        BuyerDetails.setAlignment(Element.ALIGN_CENTER);
        BuyerDetailsCell.setColspan(3);
        BuyerDetailsCell.addElement(BuyerDetails);
        table.addCell(BuyerDetailsCell).setBorder(Rectangle.NO_BORDER);
        document.add(new Chunk(lineSeparator));
        //Buyer Name
        PdfPCell BuyerNameCell = new PdfPCell();
        Paragraph BuyerName = new Paragraph("Ashish Patel",mUserFont);
        BuyerNameCell.addElement(BuyerName);
        BuyerNameCell.setColspan(3);
        table.addCell(BuyerNameCell).setBorder(Rectangle.NO_BORDER);

        //Buyer Number
        PdfPCell BuyerNumberCell = new PdfPCell();
        Paragraph BuyerNumber = new Paragraph("+91 8758549166",mUserFont);
        BuyerNumberCell.addElement(BuyerNumber);
        BuyerNumberCell.setColspan(3);
        table.addCell(BuyerNumberCell).setBorder(Rectangle.NO_BORDER);

        //Buyer Address
        PdfPCell BuyerAddressCell = new PdfPCell();
        Paragraph BuyerAddress = new Paragraph(" E-202 Shanidhya Greens, Vejlpur,Ahmendabad");
        BuyerAddressCell.addElement(BuyerAddress);
        BuyerAddressCell.setColspan(3);
        table.addCell(BuyerAddressCell).setBorder(Rectangle.NO_BORDER);


        //Seller Details
        PdfPCell SellerDetailsCell = new PdfPCell();
        Paragraph SellerDetails = new Paragraph("Seller Details",mUserFont);
        SellerDetails.setAlignment(Element.ALIGN_CENTER);
        SellerDetailsCell.setColspan(3);
        SellerDetailsCell.addElement(SellerDetails);
        table.addCell(SellerDetailsCell).setBorder(Rectangle.NO_BORDER);
        document.add(new Chunk(lineSeparator));

        //Seller Name
        PdfPCell SellerNameCell = new PdfPCell();
        Paragraph SellerName = new Paragraph("Ashish Patel",mUserFont);
        SellerNameCell.addElement(SellerName);
        SellerNameCell.setColspan(3);
        table.addCell(SellerNameCell).setBorder(Rectangle.NO_BORDER);

        //Seller Number
        PdfPCell SellerNumberCell = new PdfPCell();
        Paragraph SellerNumber = new Paragraph("+91 8758549166",mUserFont);
        SellerNumberCell.addElement(SellerNumber);
        SellerNumberCell.setColspan(3);
        table.addCell(SellerNumberCell).setBorder(Rectangle.NO_BORDER);

        //SellerAddress
        PdfPCell SellerAddressCell = new PdfPCell();
        Paragraph SellerAddress = new Paragraph(" E-202 Shanidhya Greens, Vejlpur,Ahmendabad\n");
        SellerAddressCell.addElement(SellerAddress);
        SellerAddressCell.setColspan(3);
        table.addCell(SellerAddressCell).setBorder(Rectangle.NO_BORDER);

        //OrderId
        PdfPCell EmptyCell = new PdfPCell();
        Paragraph emptyrow = new Paragraph("  ");
        EmptyCell.setColspan(3);
        EmptyCell.addElement(emptyrow);
        table.addCell(EmptyCell).setBorder(Rectangle.NO_BORDER);

        //OrderId
        PdfPCell OrderIdCell = new PdfPCell();
        Paragraph orderID = new Paragraph("Order ID: 12345");
        OrderIdCell.setColspan(3);
        OrderIdCell.addElement(orderID);
        table.addCell(OrderIdCell);

        // OrderDate
        PdfPCell OrderDateCell = new PdfPCell();
        Paragraph orderDate = new Paragraph("Order Date: 01/04/2015 10:30:55 PM");
        OrderDateCell.setColspan(3);
        OrderDateCell.addElement(orderDate);
        table.addCell(OrderDateCell);

        //Cell Product
        PdfPCell ProductName = new PdfPCell();
        Paragraph productName = new Paragraph("Product");
        ProductName.addElement(productName);
        table.addCell(ProductName).setBorder(Rectangle.NO_BORDER);

        //Cell Qty
        PdfPCell ProductQty = new PdfPCell();
        Paragraph productQty = new Paragraph("Qty");
        ProductQty.addElement(productQty);
        table.addCell(ProductQty).setBorder(Rectangle.NO_BORDER);

        //Cell amount
        PdfPCell ProductAmount = new PdfPCell();
        Paragraph productAmount = new Paragraph("Amount");
        ProductAmount.addElement(productAmount);
        table.addCell(ProductAmount).setBorder(Rectangle.NO_BORDER);

        for (int i = 0 ;i <= 5; i++){
            table.addCell(DemoProductName);
            table.addCell(DemoProductQty);
            table.addCell(DemoProductAmount);
        }

        Chunk mTotalAmount = new Chunk(DemoProductAmount);
        Paragraph TotalAmount = new Paragraph(mTotalAmount);
        TotalAmount.setAlignment(Element.ALIGN_RIGHT);
        PdfPCell TotalAmountCell = new PdfPCell();
        TotalAmountCell.setColspan(3);
        TotalAmountCell.addElement(TotalAmount);
        table.addCell(TotalAmountCell);

        Chunk mSubTotal = new Chunk("SubTotal :"+DemoProductSubTotal);
        Paragraph SubTotal = new Paragraph(mSubTotal);
        SubTotal.setAlignment(Element.ALIGN_RIGHT);
        PdfPCell subTotalCell = new PdfPCell();
        subTotalCell.setColspan(3);
        subTotalCell.addElement(SubTotal);
        table.addCell(subTotalCell);

        document.add(table);
        document.close();
    }
}
