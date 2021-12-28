import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.*


//TODO -
// Working Customer Id and Invoice Id
// Recover a deleted bill
// Take values of some repeated fields from previous bills such as company name

//TODO (Recover a deleted bill)-
// Make an invoice table which will contain all the data for the bill so that the bill if deleted can be recovered (Done)
// LOGIC FOR CUSTOMER ID (Done)-
//      If its the first entry just give it the customer id of 1 and add another column of custExists and give it the value false
//      Check for the full name in the invoice table. If it exists use the same customer id. In custExists give it the value of true
//      If the name doesn't exist find all records with custExists = false and select the latest one. Then add one to its custID
// create a function which will search the table by buyers name.
// retrieve and send the data to Document class

//FIXME -
// If you select two of the same product bu in different turns they will be duplicated on the bill instead of being added (DONE)


open class Data {
    object StockTable: Table(){
        val id: Column<Int> = integer("id").autoIncrement()
        val stockName: Column<String> = varchar("stockName", 244)
        val stockAmount: Column<Int> = integer("stockAmount")
        val stockPrice: Column<Double> = double("stockPrice")

        override val primaryKey = PrimaryKey(id, name="PK_User_ID")
    }

    object InvoiceTable: Table(){
        val id: Column<Int> = integer("id").autoIncrement()
        val custFullName: Column<String> = text("custFullName")
        val invoiceName: Column<String> = text("invoiceName")
        val tableContent: Column<String> = text("tableContent")
        val yourCompanyName: Column<String> = text("yourCompanyName")
        val addLine1: Column<String> = text("addLine1")
        val date: Column<String> = text("date")
        val invoiceNumber: Column<Int> = integer("invoiceNumber")
        val phnNumber: Column<String> = text("phnNumber")
        val custId: Column<Int> = integer("custId")
        val faxNum: Column<String> = text("faxNum")
        val dueDate: Column<String> = varchar("dueDate", 20)
        val billToName: Column<String> = text("billToName")
        val companyName: Column<String> = text("companyName")
        val compAdd: Column<String> = text("compAdd")
        val compPhnNum: Column<String> = text("compPhnNum")
        val custExists: Column<String> = text("custExists")

        override val primaryKey = PrimaryKey(id, name="PK_Invoice_ID")
    }

    init{
        Database.connect("jdbc:sqlite:F:\\IDEs\\Kotlin\\Stock-Management\\src\\data\\data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.create(StockTable)
            SchemaUtils.create(InvoiceTable)
        }
    }

    open fun addRecords(name: String, amount: Int, price: Double){
        transaction {
            val map: MutableMap<String, Int> = retrieveStockAmount(name)
            if (map.isEmpty()) {
                StockTable.insert {
                    it[stockName] = name
                    it[stockAmount] = amount
                    it[stockPrice] = price
                }
            }
            else{
                StockTable.update({StockTable.stockName eq name}){
                    val antAmount: Int? = map[name]
                    var intAntAmount: Int = 0
                    if(antAmount != null){
                        intAntAmount = antAmount
                    }
                    val newAmount: Int = intAntAmount + amount
                    it[stockAmount] = newAmount
                    it[stockPrice] = price
                }
            }
        }
    }

    fun retrieveStockAmount(name: String="", index: Int=0): MutableMap<String, Int>{
        var returnedStockAmountMap: MutableMap<String, Int> = mutableMapOf()
        var amountStock: Int
        transaction {
            if(name.isNotEmpty()) {
                StockTable.select {
                    StockTable.stockName eq name
                }.forEach{
                    amountStock = it[StockTable.stockAmount]
                    returnedStockAmountMap[name] = amountStock
                }
            }

            else if(index != 0){
                StockTable.select{
                    StockTable.id eq index
                }.forEach{
                    amountStock = it[StockTable.stockAmount]
                    val antName: String = it[StockTable.stockName]
                    returnedStockAmountMap[antName] = amountStock
                }
            }
        }
        return returnedStockAmountMap
    }

    fun retrieveStockName(index: Int): String{
        var returnedStockName: String = ""
        transaction{
            StockTable.select{
                StockTable.id eq index
            }.forEach{
                returnedStockName = it[StockTable.stockName]
            }
        }
        return returnedStockName
    }

    private fun retrieveStockPrice(name: String="", index: Int=0): Double{
        var price: Double = 0.0
        transaction {
            if(name.isNotEmpty()) {
                StockTable.select {
                    StockTable.stockName eq name
                }.forEach{
                    price = it[StockTable.stockPrice]
                }
            }

            else if(index != 0){
                StockTable.select{
                    StockTable.id eq index
                }.forEach{
                    price = it[StockTable.stockPrice]
                }
            }
        }
        return price
    }

    open fun getStatus(): Boolean{
        var empty: Boolean = false
        transaction {
            val rows = StockTable.select{StockTable.id greater 0}.count()
            if (rows == 0L){
                println("The database is empty")
                empty = true
            }
        }
        return empty
    }

    open fun selectItemsForBill(amount: Int, name: String): MutableList<String>{
        var found: Boolean = true
        val map: MutableMap<String, Int> = retrieveStockAmount(name)
        var groupItem: MutableList<String> = mutableListOf()

        if (map.isEmpty()){
            println("Stock doesn't exist")
            found = false
        }

        if (found){
            transaction {
                StockTable.update({StockTable.stockName eq name}){
                    val antAmount: Int? = map[name]
                    var intAntAmount: Int = 0
                    if(antAmount != null){
                        intAntAmount = antAmount
                    }
                    if(amount > intAntAmount){
                        println("Not enough stock")
                    }
                    else{
                        val newAmount: Int = intAntAmount - amount
                        it[stockAmount] = newAmount
                    }
                }
            }

            val price: Double = retrieveStockPrice(name)
            val total: Double = price * amount
            groupItem.add(name)
            groupItem.add(amount.toString())
            groupItem.add(price.toString())
            groupItem.add(total.toString())
        }
        return groupItem
    }

    open fun addInvoiceToTable(fullName:String, content: MutableList<MutableList<String>>, userCompanyName: String,
                               userAddLine1: String, userPhnNumber: String,
                               numFax: String, dateDue: String, buyerName: String, buyerCompanyName: String, buyerCompanyAdd: String,
                               buyerCompPhnNum: String): String{
        val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
        val currentDate = sdf.format(Date()).toString()
        val name: String = "$currentDate $buyerName"
        val replacedName: String = name.replace("""/""", ".").replace(":", ",")
        var counter = 0

        transaction{
            val recordsCount: Long = InvoiceTable.select(InvoiceTable.id greater 0).count()
            var existingCustId: Int = 0
            InvoiceTable.insert{ it ->
                it[custFullName] = fullName
                it[invoiceName] = replacedName
                it[tableContent] = content.toString()
                it[yourCompanyName] = userCompanyName
                it[addLine1] = userAddLine1
                it[date] = currentDate
                it[invoiceNumber] = recordsCount.toInt()
                it[phnNumber] = userPhnNumber
                it[faxNum] = numFax
                it[dueDate] = dateDue
                it[billToName] = buyerName
                it[companyName] = buyerCompanyName
                it[compAdd] = buyerCompanyAdd
                it[compPhnNum] = buyerCompPhnNum
                if (recordsCount == 0L){
                    it[custExists] = "false"
                    it[custId] = 1
                }
                else{
                    InvoiceTable.select{
                        custFullName eq fullName
                    }.forEach{
                        counter++
                    }

                    if (counter == 0){
                        val x: MutableList<Int> = mutableListOf()
                        InvoiceTable.select{
                            InvoiceTable.custExists eq "false"
                        }.forEach{
                            x.add(it[custId])
                        }

                        val lastCustomerId: Int = x.last()
                        it[custId] = lastCustomerId + 1
                        it[custExists] = "false"
                    }
                    else{
                        InvoiceTable.select{
                            InvoiceTable.custFullName eq fullName
                        }.forEach{
                            existingCustId = it[custId]
                        }
                        it[custId] = existingCustId
                        it[custExists] = "true"
                    }
                }
            }
        }
        return replacedName
    }

    fun retrieveAllRecords(): Query {
        lateinit var allItems: Query
        transaction {
            allItems = StockTable.selectAll()
        }
        return allItems
    }
}