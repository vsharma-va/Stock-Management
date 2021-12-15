import Data
import java.lang.NumberFormatException

class UserControl(private val data: Data){
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
//            else if(userInput == 4){
//
//            }
        }
    }

    private fun addRecords(){
        do{
            println("Enter -1 to exit")
            print("Enter Name Of The Stock: ")
            var userInputName: String = readLine().toString()
            if (userInputName == "-1") {
                break
            }
            print("Enter Amount Of The Stock: ")
            var userInputAmount: Int = Integer.valueOf(readLine())
            if (userInputAmount == -1) {
                break
            }
            data.addRecords(userInputName, userInputAmount)
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
}