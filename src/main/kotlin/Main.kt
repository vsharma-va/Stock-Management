fun main(args: Array<String>){
    val dataClass: Data = Data()
    val document: Document = Document()
    val userClass: UserControl = UserControl(dataClass, document)

    userClass.whatToDo()
}

