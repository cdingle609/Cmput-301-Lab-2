package com.example.listcity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listcity.ui.theme.ListCityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val cityRepository = CityRepository()
        setContent {
            ListCityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CityListScreen(
                        cities = cityRepository.cities,
                        onAddCity = {cityRepository.addCity(it)}, //the city typed by user will be added in CityRepo
                        onDelCity = {cityRepository.delCity(it)}, //city selected is deleted
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable //this means that this function describes part of the apps UI
fun CityListScreen(
    //cities: List<String> is the list of city names that the screen gets from MainActivity
    cities: List<String>,

    //modifier: Modifier allows layout modification such as padding to be passed on the screen
    modifier: Modifier,

    //onAddCity: (String)-> Unit is a function parameter that takes on a string
    //that is typed by the user. A return value of Unit means this function
    //performs an action but does not return a value
    onAddCity: (String)->Unit,

    //takes the city name as a string and delete it
    onDelCity: (String)->Unit
) {
    //newCityName stores the current text from input field and
    //remember keeps the value available while the composable
    //is on the screen. MutableStateOf("") tells compose to
    //update UI when value changes
    var newCityName by remember {mutableStateOf("")}

    //stores the city display on the screen that is double-clicked
    //which means it's selected for deletion
    var cityToDelete by remember {mutableStateOf("")}

    //used to show a city can be deleted if a city is double-clicked
    var deleteButtonIndicator by remember {mutableStateOf(false)}

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(16.dp)) {

            Button(
                onClick = {
                    if(newCityName.isNotBlank()) {  //delete button turns blue indicating double-clicked can be deleted
                        onAddCity(newCityName)
                        newCityName = ""
                    }
                }

            ) {
                (Text("Add City"))
            }

            Button(
                onClick = {
                    if(cityToDelete.isNotBlank() && deleteButtonIndicator) {
                        onDelCity(cityToDelete)
                        deleteButtonIndicator = !deleteButtonIndicator //toggle button to gray
                        cityToDelete = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (deleteButtonIndicator) Color.Red else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                (Text("Delete City"))
            }
        }

        Row(modifier = Modifier.padding(16.dp)) {

            OutlinedTextField(
                value = newCityName,
                onValueChange = { newCityName = it },
                label = { Text("City name") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

        }
        //LazyColumn is the compose for basic scrolling ListView
        LazyColumn(modifier = modifier.fillMaxSize()) {
            //items(cities) loops through the cityList
            //and creates one UI row per city
            items(cities) { city ->
                //cityToDelete = it means that it is the name of the city as a string
                //that was double-clicked which is passed to cityToDelete
                //to be deleted if the delete button is pressed
                CityRow(city = city,
                        onDoubleClick = {
                            cityToDelete = it
                            deleteButtonIndicator = !deleteButtonIndicator //toggle button to red which shows the city double-clicked is selected
                            }
                        )
            }
        }
    }
}

@Composable
//onDoubleClick: (String)->Unit lets me use the city that is double-clicked as a string
fun CityRow(city: String, onDoubleClick: (String)->Unit) {
    Text(
        text = city,
        fontSize= 28.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .combinedClickable(interactionSource = remember {  //remember is used to that MutableInteractionSource is reused
                MutableInteractionSource()}, indication = ripple(), onClick = {},
                onDoubleClick = {onDoubleClick(city)})



    )

}
class CityRepository { //class where cities are stored
    private val _cities = mutableStateListOf(  //private so that other apps cannot change the list
        "Edmonton", "Vancouver", "Moscow",
        "Sydney", "Berlin", "Vienna", "Osaka",
        "Tokyo", "Beijing", "New Delhi")

    val cities: List<String>  //read only list for the UI to display
        get() = _cities

    fun addCity(city: String) {
        _cities.add(city)
    }

    fun delCity(city: String) {
        _cities.remove(city)
    }
}

