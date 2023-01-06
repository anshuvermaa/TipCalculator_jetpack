package com.example.tipcalculator

import android.icu.util.CurrencyAmount
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.components.InputField
import com.example.tipcalculator.ui.theme.TIPCALCULATORTheme
import com.example.tipcalculator.util.calculateTip
import com.example.tipcalculator.util.calculateTotalPerPerson

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
             MyApp {
//                 TopHeader()
                 MainContent()
             }


        }
    }
}


@Composable
fun MyApp(content: @Composable
    () -> Unit
){
    TIPCALCULATORTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background
        ) {
            content()



        }


    }

}



@Composable
fun TopHeader(totalPerPerson:Double){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(start = 15.dp, top = 0.dp, end = 15.dp, bottom = 20.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color= Color(0xFFE9D7F7)
    ) {

        Column(modifier=Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total="%.2f".format(totalPerPerson)
            Text(text = "Total per person",
                style=MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(text = "$${total}",
            style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
                )

        }

    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent(){
    val splitByState= remember {
        mutableStateOf(1)
    }
    val tipAmountState= remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState= remember {
        mutableStateOf(0.0)
    }

    val range=IntRange(start=1 , endInclusive = 100)

    Column(modifier = Modifier.padding(all=12.dp)) {
    BillForm(
        splitByState=splitByState,
    tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState,
        range = range
        ){
//            billAmt->
//        Log.d("bill","mainContent:$billAmt")

    }

    }

}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier=Modifier,
             range:IntRange=1..100,
             splitByState: MutableState<Int>,
             tipAmountState: MutableState<Double>,
             totalPerPersonState: MutableState<Double>,
onValChange:(String)->Unit={}){

    val totalBillState= remember {
        mutableStateOf("")
    }
    val validState= remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController=LocalSoftwareKeyboardController.current
    val sliderPositionState= remember {
        mutableStateOf(0f)

    }
    var percent=(sliderPositionState.value*100).toInt()


    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner=CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp,color=Color.LightGray)
    ) {
//        var percent="%.0f".format(sliderPositionState.value*100)

        Column(
            modifier=Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            InputField(valueState =totalBillState ,
                labelId = "Enter Bills",
                enabled =true ,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if(!validState) {

                        return@KeyboardActions
                    }
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                })
            if(validState){
                totalPerPersonState.value=     calculateTotalPerPerson(totalbill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = percent.toInt())
                Row(
                    modifier=modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",modifier=modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(120.dp))

                    Row(modifier=modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End){
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            if(splitByState.value>1){
                                splitByState.value -= 1
                                totalPerPersonState.value=calculateTotalPerPerson(totalbill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = percent.toInt())
                            }else 1
                            Log.d("Icon","BillForm: Add") })

                        
                        Text(text = "${splitByState.value}", modifier= Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(start = 9.dp, end = 9.dp))
                        
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            if(splitByState.value<range.last) {
                                splitByState.value=splitByState.value+1
                                totalPerPersonState.value=calculateTotalPerPerson(totalbill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = percent.toInt())
                            }
                            Log.d("Icon","BillForm: Add")  })

                    }
                }
                    Row{
                        Text(text = "Tip",modifier=Modifier.align(alignment = Alignment.CenterVertically))

                       Spacer(modifier = Modifier.width(200.dp))
                        Text(text = "${tipAmountState.value}",modifier=Modifier.align(alignment = Alignment.CenterVertically))

                    }

            
            
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "${percent}%")
                Spacer(modifier = Modifier.height(14.dp))

                // Slider
                Slider(value = sliderPositionState.value, onValueChange = {
                    newVal->
                    sliderPositionState.value=newVal
                    tipAmountState.value=
                        calculateTip(totalbill = totalBillState.value.toDouble(),percent=percent.toInt())


                    //here we calling calculateTotalperperson on evrytime slider values changes it calcultes
                    // the totalperperson cost


               totalPerPersonState.value=     calculateTotalPerPerson(totalbill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = percent.toInt())
                    Log.d("slider","billForm: $newVal")
                },
                modifier=Modifier.padding(start = 16.dp, end = 16.dp),
                    onValueChangeFinished = {

                    })


            }
            }else{
                Box{

                }
            }


        }

    }

}




val IconbuttonSizeModifier=Modifier.size(40.dp)

@Composable
fun RoundIconButton(modifier: Modifier=Modifier,
                    imageVector: ImageVector,
                    onClick:()-> Unit,
                    tint: Color=Color.Black.copy(alpha = 0.8f),
                    backgroundColor: Color=MaterialTheme.colors.background,
                    elevation:Dp=4.dp
){
    Card(modifier = modifier
        .padding(all = 4.dp)
        .clickable { onClick.invoke() }
        .then(IconbuttonSizeModifier),
        shape = CircleShape,
        backgroundColor=backgroundColor,
        elevation = elevation
        ) {
        Icon(imageVector = imageVector, contentDescription = "Plus or Minus Icone",tint=tint)

    }

}





@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TIPCALCULATORTheme {
        MyApp{
            Text(text = "hello Again")
//            Column(modifier = Modifier.padding(20.dp)){
//                TopHeader(totalPerPe)
//                Spacer(modifier = Modifier.height(5.dp))
//                MainContent()
//            }

        }
    }
}