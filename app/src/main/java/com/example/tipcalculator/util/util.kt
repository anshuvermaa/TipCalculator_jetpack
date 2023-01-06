package com.example.tipcalculator.util



fun calculateTip(totalbill: Double, percent: Int): Double {
    return  if(totalbill>1 && totalbill.toString().isNotEmpty()) {
        (totalbill*percent)/100
    }else 0.0
}

fun calculateTotalPerPerson(
    totalbill: Double,
    splitBy:Int,
    tipPercentage:Int):Double {
    val Bill= calculateTip(totalbill,tipPercentage)+ totalbill
  return (Bill/splitBy)
}
