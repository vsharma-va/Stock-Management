import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.WebColors
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs
import com.itextpdf.layout.*
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.Border.*
import com.itextpdf.layout.borders.DottedBorder
import com.itextpdf.layout.element.*
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.*
import org.w3c.dom.css.Rect
import kotlin.collections.*

fun main(args: Array<String>){
    val pdf = PdfDocument(PdfWriter("F:\\IDEs\\Kotlin\\Stock-Management\\src\\data\\document.pdf"))
    val document = Document(pdf)

//    val text = Paragraph("Hello World")
//    text.setMargins(10f, 10f, 10f, 10f)
//    val text2 = Paragraph("What is")
//    document.add(text)
//    document.add(text2)
//    document.close()

//    val table = Table(UnitValue.createPercentArray(floatArrayOf(75f, 25f, 55f))).useAllAvailableWidth()
//    table.addHeaderCell(Cell().add(Paragraph("Product Name").setTextAlignment(TextAlignment.CENTER)))
//
//    val table2 = Table(UnitValue.createPercentArray(floatArrayOf(75f, 25f, 55f))).useAllAvailableWidth()
//    table2.addHeaderCell(Cell().add(Paragraph("Product Name").setTextAlignment(TextAlignment.CENTER)))
//
//    document.add(table)
//    document.add(table2)

    val companyName = Paragraph("Company Name").setFontSize(20f).setTextAlignment(TextAlignment.LEFT)
    companyName.setMargins(0f, 0f, 0f, 0f)
    companyName.add(Tab())
    companyName.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
    companyName.add("INVOICE").setFontSize(30f).setTextAlignment(TextAlignment.RIGHT)
    val add1 = Paragraph("[Street Address]").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
    add1.setMargins(0f, 0f, 0f, 4f)
    add1.add(Tab())
    add1.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
    add1.add("DATE").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)
    val add2 = Paragraph("[City, ST ZIP]").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
    add2.setMargins(0f, 0f, 0f, 4f)
    add2.add(Tab())
    add2.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
    add2.add("INVOICE #").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)
    val phoneNumber = Paragraph("Phone: [000-000-000]").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
    phoneNumber.setMargins(0f, 0f, 0f, 4f)
    phoneNumber.add(Tab())
    phoneNumber.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
    phoneNumber.add("CUSTOMER ID").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)
    val fax = Paragraph("Phone: [000-000-000]").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
    fax.setMargins(0f, 0f, 0f, 4f)
    fax.add(Tab())
    fax.addTabStops(TabStop(1000f, TabAlignment.RIGHT))
    fax.add("DUE DATE").setFontSize(10f).setTextAlignment(TextAlignment.RIGHT)
    val website = Paragraph("Website: somedomain.com").setFontSize(7f).setTextAlignment(TextAlignment.LEFT)
    website.setMargins(0f, 0f, 15f, 4f)

    val billTo = Paragraph("BILL TO").setFontSize(20f).setTextAlignment(TextAlignment.LEFT)
    billTo.setMargins(0f, 0f, 0f, 4f)
    val name = Paragraph("[Name]").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
    name.setMargins(0f, 0f, 0f, 4f)
    val compName = Paragraph("[Company Name]").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
    compName.setMargins(0f, 0f, 0f, 4f)
    val streetAdd = Paragraph("[City, ST ZIP]").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
    streetAdd.setMargins(0f, 0f, 0f, 4f)
    val phnNum = Paragraph("[Phone]").setFontSize(10f).setTextAlignment(TextAlignment.LEFT)
    phnNum.setMargins(0f, 0f, 15f, 4f)

    val table = Table(UnitValue.createPercentArray(floatArrayOf(100f, 30f, 35f))).useAllAvailableWidth()
    table.addHeaderCell(Cell().add(Paragraph("DESCRIPTION").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)))
    table.addHeaderCell(Cell().add(Paragraph("PRICE").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)))
    table.addHeaderCell(Cell().add(Paragraph("AMOUNT").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)))

    table.addCell(Cell().add(Paragraph("Oil")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("200")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("400")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))

    table.addCell(Cell().add(Paragraph("Wood")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("150")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("450")).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))

    table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("Subtotal")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("850")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))

    table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("Taxable")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("whatever")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))

    table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("Tax Rate")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("6.250%")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))

    table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("Tax due")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("21.56")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))

    table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("Other")).setTextAlignment(TextAlignment.CENTER).setBorderTop(
        NO_BORDER).setBorderLeft(NO_BORDER).setBorderRight(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("-")).setTextAlignment(TextAlignment.CENTER).setBorderTop(
        NO_BORDER).setBorderLeft(NO_BORDER).setBorderRight(NO_BORDER).setFontSize(7f))

    table.addCell(Cell().add(Paragraph(" ")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("TOTAL")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f))
    table.addCell(Cell().add(Paragraph("971.56")).setTextAlignment(TextAlignment.CENTER).setBorder(NO_BORDER).setFontSize(7f)
        .setBackgroundColor(ColorConstants.LIGHT_GRAY))

    document.add(companyName)
    document.add(add1)
    document.add(add2)
    document.add(phoneNumber)
    document.add(fax)
    document.add(website)
    document.add(billTo)
    document.add(name)
    document.add(compName)
    document.add(streetAdd)
    document.add(phnNum)
    document.add(table)
    document.close()
}