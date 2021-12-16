import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

open class Data {
    object StockTable: Table(){
        val id: Column<Int> = integer("id").autoIncrement()
        val stockName: Column<String> = varchar("stockName", 244)
        val stockAmount: Column<Int> = integer("stockAmount")
        val stockPrice: Column<Double> = double("stockPrice")

        override val primaryKey = PrimaryKey(id, name="PK_User_ID")
    }
    init{
        Database.connect("jdbc:sqlite:F:\\IDEs\\Kotlin\\Stock-Management\\src\\data\\data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.create(StockTable)
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
}