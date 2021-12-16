import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

object StockTable: Table(){
    val id: Column<Int> = integer("id").autoIncrement()
    val stockName: Column<String> = varchar("stockName", 244)
    val stockAmount: Column<Int> = integer("stockAmount")

    override val primaryKey = PrimaryKey(id, name="PK_User_ID")
}

fun main(args: Array<String>){
    Database.connect("jdbc:sqlite:F:\\IDEs\\Kotlin\\Stock-Management\\src\\data\\test.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel =
        Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(StockTable)
    }

//    transaction {
//        StockTable.insert{
//            it[stockName] = "Oil"
//            it[stockAmount] = 25
//        }
//    }
    transaction {
        val query: Query = StockTable.select { StockTable.stockName eq "Oil" }
        println(query)
        val rows = StockTable.select{StockTable.id greater 0}.count()
        print(rows)
    }

//    var returnedStockName: String = ""
//    var map: MutableMap<String, Int> = mutableMapOf()
//    transaction{
//        val query: Query = StockTable.select{ StockTable.id eq 1 }
//        for ((counter, i) in query.withIndex()){
//            map[i] = counter
//        }
//    }
//    println(returnedStockName)

    val h: Array<Array<String>> = arrayOf(arrayOf("what", "is"), arrayOf("name", "dude"))
    for (i in h){
        println(i[0])
    }

//    val k = Document()
//    k.generateTemplate("MHello Corp", "What", "23/12/2021", 1, "123-456-789", "123", "456",
//        "24/12/2021", "Vaibhav", "Sharmas", "where", "123-432-543")
//    k.generateTable(arrayOf(arrayOf("oil", "20", "200", "4000"), arrayOf("Wood", "30", "100", "3000")), 8f, 7f)
//    k.writeToPdf()
}