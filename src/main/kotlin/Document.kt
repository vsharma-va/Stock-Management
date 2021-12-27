import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.TabAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import javax.print.Doc

class Document(private val data: Data) {
    private lateinit var companyNameAndInvoice: Paragraph
    private lateinit var add1AndDate: Paragraph
    private lateinit var add2AndInvoiceNum: Paragraph
    private lateinit var phnNumAndCustId: Paragraph
    private lateinit var fax: Paragraph
    private lateinit var billTo: Paragraph
    private lateinit var name: Paragraph
    private lateinit var compName: Paragraph
    private lateinit var streetAdd: Paragraph
    private lateinit var phnNum: Paragraph

    private lateinit var table: Table

    private lateinit var pdf: PdfDocument
    private lateinit var document: Document


    fun generateTemplate(yourCompanyName: String, addLine1: String, date: String, invoiceNumber: Int, phnNumber: String, custId: String,
                        faxNum: String, dueDate: String, billToName: String, companyName: String, compAdd: String,
                        compPhnNum: String): Boolean{
        val fileName: MutableList<String> = data.addInvoiceToTable(billToName)
        if (fileName[1].toBoolean()){
            pdf = PdfDocument(PdfWriter("""F:\\IDEs\\Kotlin\\Stock-Management\\src\\data\\pdf\\(${fileName[0]}).pdf"""))
            document = Document(pdf)
            companyNameAndInvoice = Paragraph(yourCompanyName).setFontSize(20f).setTextAlignment(TextAlignment.LEFT).setBackgroundColor(
                ColorConstants.LIGHT_GRAY)
            companyNameAndInvoice.setMargins(0f, 0f, 0f, 0f)
            companyNameAndInvoice.add(Tab())
            companyNameAndInvoice.addTabStops(TabStop(520f, TabAlignment.RIGHT))
            companyNameAndInvoice.add("INVOICE").setFontSize(30f).setTextAlignment(TextAlignment.RIGHT)

            add1AndDate = Paragraph("$addLine1").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
            add1AndDate.setMargins(0f, 0f, 0f, 4f)
            add1AndDate.add(Tab())
            add1AndDate.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
            add1AndDate.add("DATE: $date").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)

            add2AndInvoiceNum = Paragraph("[City, ST ZIP]").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
            add2AndInvoiceNum.setMargins(0f, 0f, 0f, 4f)
            add2AndInvoiceNum.add(Tab())
            add2AndInvoiceNum.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
            add2AndInvoiceNum.add("INVOICE #$invoiceNumber").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)

            phnNumAndCustId = Paragraph("Phone: $phnNumber").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
            phnNumAndCustId.setMargins(0f, 0f, 0f, 4f)
            phnNumAndCustId.add(Tab())
            phnNumAndCustId.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
            phnNumAndCustId.add("CUSTOMER ID: $custId").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)

            fax = Paragraph("fax: $faxNum").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
            fax.setMargins(0f, 0f, 0f, 4f)
            fax.add(Tab())
            fax.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
            fax.add("DUE DATE: $dueDate").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)

            billTo = Paragraph("BILL TO").setFontSize(20f).setTextAlignment(TextAlignment.LEFT).setBackgroundColor(ColorConstants.LIGHT_GRAY)
            billTo.setMargins(0f, 0f, 0f, 4f)
            name = Paragraph("$billToName").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
            name.setMargins(0f, 0f, 0f, 4f)
            compName = Paragraph("$companyName").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
            compName.setMargins(0f, 0f, 0f, 4f)
            streetAdd = Paragraph("$compAdd").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
            streetAdd.setMargins(0f, 0f, 0f, 4f)
            phnNum = Paragraph("$compPhnNum").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
            phnNum.setMargins(0f, 0f, 15f, 4f)
            return true
        }
        else{
            println("Aborted")
            return false
        }
    }

    fun generateTable(billList: MutableList<MutableList<String>>, cgst: Float, igst: Float){
        var subTotal: Double = 0.0
        table = Table(UnitValue.createPercentArray(floatArrayOf(100f, 30f, 30f, 35f))).useAllAvailableWidth()
        table.addHeaderCell(Cell().add(Paragraph("DESCRIPTION").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)))
        table.addHeaderCell(Cell().add(Paragraph("QTY").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)))
        table.addHeaderCell(Cell().add(Paragraph("AMOUNT").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)))
        table.addHeaderCell(Cell().add(Paragraph("TOTAL AMOUNT").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)))

        for (item in billList){
            table.addCell(Cell().add(Paragraph(item[0])).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
            table.addCell(Cell().add(Paragraph(item[1])).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
            table.addCell(Cell().add(Paragraph(item[2])).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
            table.addCell(Cell().add(Paragraph(item[3])).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))

            subTotal += item[3].toDouble()
        }

        table.addCell(Cell().add(Paragraph(" ")).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("Subtotal")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("$subTotal")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))

        table.addCell(Cell().add(Paragraph(" ")).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("CGST")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("$cgst %")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))

        table.addCell(Cell().add(Paragraph(" ")).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("IGST")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("$igst %")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))

        val cgstAmount: Double = subTotal * (cgst/100)
        val cgstAmountRounded: Double = String.format("%.3f", cgstAmount).toDouble()
        val igstAmount: Double = subTotal * (igst/100)
        val igstAmountRounded: Double = String.format("%.3f", igstAmount).toDouble()

        table.addCell(Cell().add(Paragraph(" ")).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("Tax Due")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("${cgstAmountRounded + igstAmountRounded}")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))

        table.addCell(Cell().add(Paragraph(" ")).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("Other")).setTextAlignment(TextAlignment.CENTER).setBorderTop(
            Border.NO_BORDER
        ).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("-")).setTextAlignment(TextAlignment.CENTER).setBorderTop(
            Border.NO_BORDER
        ).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setFontSize(7f))

        table.addCell(Cell().add(Paragraph(" ")).setBorder(Border.NO_BORDER))
        table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("TOTAL")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f))
        table.addCell(Cell().add(Paragraph("${cgstAmountRounded + igstAmountRounded + subTotal}")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setFontSize(7f)
            .setBackgroundColor(ColorConstants.LIGHT_GRAY))


    }

    fun writeToPdf(){
        document.add(companyNameAndInvoice)
        document.add(add1AndDate)
        document.add(add2AndInvoiceNum)
        document.add(phnNumAndCustId)
        document.add(fax)
        document.add(billTo)
        document.add(name)
        document.add(compName)
        document.add(streetAdd)
        document.add(phnNum)
        document.add(table)

        document.close()
    }
}