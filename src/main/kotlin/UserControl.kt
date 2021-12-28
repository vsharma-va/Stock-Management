import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NumberFormatException

class UserControl(private val data: Data, private val document: Document){
    fun whatToDo()
    {
        if(data.getStatus()){
            addRecords()
        }
        else{
            println("Press 1 to Add Records to database")
            println("Press 2 to retrieve stock name w.r.t stock name or index number")
            println("Press 3 to retrieve stock amount w.r.t to index number")
            println("Press 4 to create a bill")
            val userChoice: Int = Integer.valueOf(readLine())
            if (userChoice == 1){
                addRecords()
            }
            else if(userChoice == 2){
                val nameAmountMap: MutableMap<String, Int> = retrieveStockAmount()
                println("Found: ")
                println(nameAmountMap)
            }
            else if(userChoice == 3){
                val userInputIndex: Int = Integer.valueOf(readLine())
                val foundValue: String = data.retrieveStockName(userInputIndex)
                println("Found: $foundValue")
            }
            else if(userChoice == 4){
                var billItem: MutableList<MutableList<String>> = collectBillData()
                if (billItem.isNotEmpty()){
                    val templateData: MutableList<String> = getDataForDocumentTemplate()
                    println("Enter the full name of the buyer")
                    val buyersFullName: String = readLine().toString()
                    val fileName: String = data.addInvoiceToTable(buyersFullName, billItem, templateData[0], templateData[1], templateData[4],
                     templateData[6], templateData[7], templateData[8], templateData[9], templateData[10], templateData[11])
                    val bool: Boolean = document.generateTemplate(templateData[0], templateData[1], templateData[2], templateData[3].toInt(),
                        templateData[4], templateData[5], templateData[6], templateData[7], templateData[8], templateData[9],
                        templateData[10], templateData[11], fileName)
                    if (bool){
                        print("Enter CGST %: ")
                        val cgstPercentage: Float = readLine()!!.toFloat()

                        print("Enter IGST %: ")
                        val igstPercentage: Float = readLine()!!.toFloat()

                        document.generateTable(billItem, cgstPercentage, igstPercentage)
                        document.writeToPdf()
                    }
                    else{
                        println("Exiting.. (Error in generating template for the pdf)")
                    }
                }
                else{
                    println("Exiting.. (The bill item list[list] is empty)")
                }
            }
        }
    }

    private fun addRecords(){
        var correct: Boolean = true
        var userInputPrice:Double?
        do{
            println("Enter -1 to exit")
            print("Enter Name Of The Stock: ")
            var userInputName: String = readLine().toString()
            if (userInputName == "-1") {
                break
            }
            if (userInputName.isEmpty()){
                println("Name cannot be left empty")
                correct = false
                break
            }
            print("Enter Amount Of The Stock: ")
            var userInputAmount: Int = 0
            try {
                userInputAmount = Integer.valueOf(readLine())
                if (userInputAmount == -1) {
                    break
                }
            }catch(e: NumberFormatException){
                println("Please enter an integer")
                correct = false
            }

            do {
                print("Enter the price : ")
                userInputPrice = readLine()!!.toDoubleOrNull()
                if (userInputPrice == null) println("Not a valid number, try again")
            }
            while (userInputPrice == null)

            if(correct){
                val userInputPriceDouble: Double = userInputPrice.toDouble()
                data.addRecords(userInputName, userInputAmount, userInputPriceDouble)
            }
            else{
                println("Exiting.. (Incorrect input given in either the name or the amount)")
                break
            }

        }while(true)
    }

    private fun retrieveStockAmount(): MutableMap<String, Int>{
        var returnedMap: MutableMap<String, Int> = mutableMapOf()
        val userInput = readLine()
        var index: Int = 0
        var name: Boolean = false
        try{
            index = Integer.valueOf(userInput)
        } catch(e: NumberFormatException){
            userInput.toString()
            name = true
        }
        if (name){
            if (userInput != null) {
                returnedMap = data.retrieveStockAmount(name=userInput)
            }
        }
        else{
            returnedMap = data.retrieveStockAmount(index=index)
        }
        return returnedMap
    }

    private fun collectBillData(): MutableList<MutableList<String>>{
        var billItems: MutableList<MutableList<String>> = mutableListOf()
        var groupedItems: MutableList<String>
        var dataForBillItems: MutableList<String>
        var packedForBillItems: MutableList<MutableList<String>> = mutableListOf()
        var userInput: String
        var cont: Boolean = true
        val allItems: Query = data.retrieveAllRecords()
        transaction{
            println()
            allItems.forEach {
                var printLine: String = ""
                val x: String = it.toString()
                val splitList: List<String> = x.split(",")
                for (i in splitList){
                    val segment: String = i.split("=")[1]
                    printLine += " $segment"
                }
                println(printLine)
            }
        }
        println()
        do {
            do{
                var map: MutableMap<String, Int>

                println("Enter -1 to exit anytime")
                print("Enter the name or the index of the stock: ")
                userInput = readLine().toString()

                if (userInput == "-1"){
                    cont = false
                    break
                }

                map = data.retrieveStockAmount(name=userInput)
                println("Found: $map")

                if (map.isNotEmpty()){
                    break
                }
                else{
                    println("No record found try again")
                }
            } while(true)

            if (!cont){
                break
            }

            print("Enter the Quantity: ")
            val userInputAmount: Int = Integer.valueOf(readLine())

            if (userInputAmount == -1){
                break
            }

            groupedItems = mutableListOf(userInputAmount.toString(), userInput)
            if (groupedItems in billItems){
                val inx: Int = billItems.indexOf(groupedItems)
                billItems[inx][0] = (billItems[inx][0].toInt() + userInputAmount).toString()
            }
            else{
                billItems.add(groupedItems)
            }
        } while(true)

        println("\n")
        println("\n")
        println("Selected Items: ")
        for (i in billItems){
            println("Name- ${i[1]}, amount- ${i[0]}")
        }

        println("\n")
        println("Are you sure you want to proceed ? Changes will be made to the database and will have to be reverted manually")
        print("1 for continue\n2 to discard: ")
        val userDecision: String = readLine().toString()

        if (userDecision == "1"){
            for (i in billItems){
                dataForBillItems = data.selectItemsForBill(i[0].toInt(), i[1])
                packedForBillItems.add(dataForBillItems)
            }
        }
        return packedForBillItems
    }

    private fun getDataForDocumentTemplate(): MutableList<String>{
        var returnArray: MutableList<String> = mutableListOf()

        print("Enter Your Company's name: ")
        val compName: String = readLine().toString()

        print("Enter Your Company's Address Line 1: ")
        val addLine1: String = readLine().toString()

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date()).toString()

        // create database to store all the invoices
        // take its index as the invoice number
        val invoiceNumber: Int = 1

        print("Enter Your Company's Phone Number: ")
        val phnNumber: String = readLine().toString()

        print("Enter Customer ID: ")
        val custId: String = readLine().toString()

        print("Enter Fax Number: (press enter to leave it empty)")
        val faxNum: String = readLine().toString()

        print("Enter Due Date: (dd/mm/yyyy) ")
        val dueDate: String = readLine().toString()

        print("Enter the buyers name: ")
        val billToName: String = readLine().toString()

        print("Enter the buyers company name: (Press enter to leave it empty) ")
        val companyName: String = readLine().toString()

        print("Enter the buyers/ company's address: ")
        val companyAdd: String = readLine().toString()

        print("Enter buyers/ company's phone number: ")
        val compPhnNum: String = readLine().toString()

        returnArray.add(compName)
        returnArray.add(addLine1)
        returnArray.add(currentDate)
        returnArray.add(invoiceNumber.toString())
        returnArray.add(phnNumber)
        returnArray.add(custId)
        returnArray.add(faxNum)
        returnArray.add(dueDate)
        returnArray.add(billToName)
        returnArray.add(companyName)
        returnArray.add(companyAdd)
        returnArray.add(compPhnNum)

        return returnArray
    }
}