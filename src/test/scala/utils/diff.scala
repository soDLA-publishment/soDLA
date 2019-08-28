package utils

object Tools {
  def diffString(a: String, b: String, name:String="") {
    if (a != b) {
      println(name + " : " + a + " ------- " + b)
    }else{
      println(name + " PASS-" + a)
    }

  }
}