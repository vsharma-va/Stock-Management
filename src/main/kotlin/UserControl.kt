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
            println("Press 5 to recover a deleted bill")
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
                val billItem: MutableList<MutableList<String>> = collectBillData()
                var templateData: MutableList<String> = mutableListOf()
                val buyersFullName: String
                val invoiceReturnData: MutableList<String>
                var bool: Boolean = false
                if (billItem.isNotEmpty()){
                    val lengthInvoice: Long = data.lengthOfInvoiceTable()
                    if (lengthInvoice > 0){
                        println("Would you like to take the value of your company name, phone number, fax number, address from" +
                                " the previous invoice" +
                                " \nEnter 1 to confirm" +
                                " \nEnter 2 to reject ")
                        val userInputChoice: String = readLine().toString()
                        if (userInputChoice == "1"){
                            val dataMap: MutableMap<String, String> = data.retrieveInvoiceData(lengthInvoice.toInt())
                            templateData = getDataForDocumentTemplate(true)
                            println("Enter the full name of the buyer")
                            buyersFullName = readLine().toString()
                            invoiceReturnData = data.addInvoiceToTable(buyersFullName, billItem, dataMap["yourCompanyName"].toString()
                                , dataMap["addLine1"].toString(), dataMap["phnNumber"].toString(),
                                dataMap["faxNum"].toString(), templateData[7], templateData[8], templateData[9], templateData[10], templateData[11],
                                templateData[12], templateData[13])
                            bool = document.generateTemplate(dataMap["yourCompanyName"].toString(), dataMap["addLine1"].toString()
                                , templateData[2], invoiceReturnData[2].toInt(),
                                dataMap["phnNumber"].toString(), invoiceReturnData[1], dataMap["faxNum"].toString(), templateData[7], templateData[8], templateData[9],
                                templateData[10], templateData[11], invoiceReturnData[0])
                        }
                        else if(userInputChoice == "2"){
                            templateData = getDataForDocumentTemplate(false)
                            println("Enter the full name of the buyer")
                            buyersFullName = readLine().toString()
                            invoiceReturnData = data.addInvoiceToTable(buyersFullName, billItem, templateData[0], templateData[1], templateData[4],
                                templateData[6], templateData[7], templateData[8], templateData[9], templateData[10], templateData[11],
                                templateData[12], templateData[13])
                            bool = document.generateTemplate(templateData[0], templateData[1], templateData[2], invoiceReturnData[2].toInt(),
                                templateData[4], invoiceReturnData[1], templateData[6], templateData[7], templateData[8], templateData[9],
                                templateData[10], templateData[11], invoiceReturnData[0])
                        }
                    }
                    else{
                        templateData = getDataForDocumentTemplate(false)
                        println("Enter the full name of the buyer")
                        buyersFullName = readLine().toString()
                        invoiceReturnData = data.addInvoiceToTable(buyersFullName, billItem, templateData[0], templateData[1], templateData[4],
                            templateData[6], templateData[7], templateData[8], templateData[9], templateData[10], templateData[11],
                            templateData[12], templateData[13])
                        bool = document.generateTemplate(templateData[0], templateData[1], templateData[2], invoiceReturnData[2].toInt(),
                            templateData[4], invoiceReturnData[1], templateData[6], templateData[7], templateData[8], templateData[9],
                            templateData[10], templateData[11], invoiceReturnData[0])
                    }
                    if (bool){
                        document.generateTable(billItem, templateData[12].toFloat(), templateData[13].toFloat())
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
            else if(userChoice == 5){
                recoverBill()
            }
        }
    }

    private fun addRecords(){
        var correct: Boolean = true
        var userInputPrice:Double?
        do{
            println("Enter -1 to exit")
            print("Enter Name Of The Stock: ")
            val userInputName: String = readLine().toString()
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
        val billItems: MutableList<MutableList<String>> = mutableListOf()
        var groupedItems: MutableList<String>
        var dataForBillItems: MutableList<String>
        val packedForBillItems: MutableList<MutableList<String>> = mutableListOf()
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

    private fun getDataForDocumentTemplate(auto: Boolean): MutableList<String>{
        val returnArray: MutableList<String> = mutableListOf()
        val currentDate: String
        val dueDate: String
        var compName: String = ""
        var addLine1: String = ""
        var invoiceNumber: Int = 0
        var phnNumber: String = ""
        var custId: String = "0"
        var faxNum: String = ""
        val billToName: String
        val companyName: String
        val companyAdd: String
        val compPhnNum: String
        val cgstPercentage: String
        val igstPercentage: String


        if (auto){
            val sdf = SimpleDateFormat("dd/M/yyyy")
            currentDate = sdf.format(Date()).toString()

            print("Enter Due Date: (dd/mm/yyyy) ")
            dueDate = readLine().toString()

            print("Enter the buyers name: ")
            billToName = readLine().toString()

            print("Enter the buyers company name: (Press enter to leave it empty) ")
            companyName = readLine().toString()

            print("Enter the buyers/ company's address: ")
            companyAdd = readLine().toString()

            print("Enter buyers/ company's phone number: ")
            compPhnNum = readLine().toString()

            print("Enter CGST %: ")
            cgstPercentage = readLine().toString()

            print("Enter IGST %: ")
            igstPercentage = readLine().toString()
        }
        else{
            print("Enter Your Company's name: ")
            compName = readLine().toString()

            print("Enter Your Company's Address Line 1: ")
            addLine1 = readLine().toString()

            val sdf = SimpleDateFormat("dd/M/yyyy")
            currentDate = sdf.format(Date()).toString()

            // create database to store all the invoices
            // take its index as the invoice number
            invoiceNumber = 1

            print("Enter Your Company's Phone Number: ")
            phnNumber = readLine().toString()

            custId = "0"

            print("Enter Fax Number: (press enter to leave it empty)")
            faxNum = readLine().toString()

            print("Enter Due Date: (dd/mm/yyyy) ")
            dueDate = readLine().toString()

            print("Enter the buyers name: ")
            billToName = readLine().toString()

            print("Enter the buyers company name: (Press enter to leave it empty) ")
            companyName = readLine().toString()

            print("Enter the buyers/ company's address: ")
            companyAdd = readLine().toString()

            print("Enter buyers/ company's phone number: ")
            compPhnNum = readLine().toString()

            print("Enter CGST %: ")
            cgstPercentage = readLine().toString()

            print("Enter IGST %: ")
            igstPercentage = readLine().toString()
        }


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
        returnArray.add(cgstPercentage)
        returnArray.add(igstPercentage)

        return returnArray
    }

    private fun recoverBill(){
        var matchedResults: MutableList<MutableList<String>> = mutableListOf()
        var nextStep: Boolean = false
        do{
            println("Enter the full name of the customer: ")
            val userInputName: String = readLine().toString()
            if (userInputName == "-1"){
                nextStep = false
                break
            }
            else{
                matchedResults = data.searchInvoiceTable(userInputName)
            }

            if (matchedResults.isNotEmpty()){
                var printString: String = ""
                for ((indexCounter, i) in matchedResults.withIndex()){
                    for(z in i){
                        printString += "$z "
                    }
                    println(printString)
                    printString = ""
                }
                nextStep = true
                break
            }
            else {
                nextStep = false
                println("No Invoices Found Try Again or Enter -1 to exit")
            }
        }while(true)

        if (nextStep){
            val dataMap: MutableMap<String, String>
            println("Enter the index number of the invoice you want to recover: ")
            val userInputIndex: String = readLine().toString()
            try{
                dataMap = data.retrieveInvoiceData(userInputIndex.toInt())
                document.generateTemplate(dataMap["yourCompanyName"].toString(), dataMap["addLine1"].toString(),
                dataMap["date"].toString(), dataMap["invoiceNumber"].toString().toInt(), dataMap["phnNumber"].toString(),
                dataMap["custId"].toString(), dataMap["faxNum"].toString(), dataMap["dueDate"].toString(),
                dataMap["billToName"].toString(), dataMap["companyName"].toString(), dataMap["compAdd"].toString(),
                dataMap["compPhnNum"].toString(), dataMap["invoiceName"].toString())

                val temp: String = dataMap["tableContent"].toString()
                var start: Int = 0
                val answer: MutableList<MutableList<String>> = mutableListOf()
                val trying: MutableList<String> = temp.replace("[", "")
                .replace("]", "").replace(" ", "").split(",") as MutableList<String>
                for ((index, i) in trying.withIndex()){
                    if ((index == 0) or (index % 4 == 0)){
                        start = index
                    }
                    if ((index + 1) % 4 == 0){
                        answer.add(trying.subList(start, index+1))
                    }
                }
                document.generateTable(answer, dataMap["cgstPercentage"]!!.toFloat(), dataMap["igstPercentage"]!!.toFloat())
                document.writeToPdf()
            }catch(e: NumberFormatException){
                println("Please enter an integer only")
                println("Exiting..")
            }
        }
    }
}