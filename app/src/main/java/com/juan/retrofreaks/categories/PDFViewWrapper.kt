package com.juan.retrofreaks.categories

import com.github.barteksc.pdfviewer.PDFView
import android.view.View

/*
This class instantianes and creates instances of PDFView
 */
class PDFViewWrapper(pdfView:PDFView?,root:View) {
    private var pdfViewL = pdfView as PDFView
    private var rootL = root

    fun getWPDFView() = pdfViewL
    fun getRoot() = rootL
}