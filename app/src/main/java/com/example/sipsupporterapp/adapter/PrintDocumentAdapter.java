package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.IOException;

public class PrintDocumentAdapter extends android.print.PrintDocumentAdapter {
    private Context context;
    public PdfDocument pdfDocument;
    private int pageHeight;
    private int pageWidth;
    public int totalPages = 4;
    private RecyclerView recyclerView;

    public PrintDocumentAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page, int pageNumber) {
        Canvas canvas = page.getCanvas();
        recyclerView.draw(canvas);

 /*       pageNumber++;

        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);

        int xPos = (canvas.getWidth() / 2) + 16;
        int yPos = 100;
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        canvas.drawText("احتراما بدینوسیله شرح خدمات و کالاهای ارائه شده بحضور شما ارائه می شود", xPos, yPos, paint);

       *//* paint.setTextSize(14);
        canvas.drawText("This is some test content to verify that custom document printing works",
                leftMargin, titleBaseLine + 35, paint);*//*

        if (pageNumber % 2 == 0)
            paint.setColor(Color.RED);
        else
            paint.setColor(Color.GREEN);

        PdfDocument.PageInfo pageInfo = page.getInfo();


        canvas.drawCircle(pageInfo.getPageWidth() / 2,
                pageInfo.getPageHeight() / 2,
                150,
                paint);*/
    }


    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        pdfDocument = new PrintedPdfDocument(context, newAttributes);

        pageHeight = recyclerView.getHeight();
        pageWidth = recyclerView.getWidth();

       /* pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 10 * 2;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 10 * 2;*/

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalPages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        for (int i = 0; i < totalPages; i++) {
            if (pageInRange(pages, i)) {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                PdfDocument.Page page =
                        pdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    pdfDocument.close();
                    pdfDocument = null;
                    return;
                }
                drawPage(page, i);
                pdfDocument.finishPage(page);
            }
        }
        try {
            pdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            pdfDocument.close();
            pdfDocument = null;
        }

        callback.onWriteFinished(pages);
    }
}
