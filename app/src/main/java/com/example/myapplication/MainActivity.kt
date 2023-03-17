package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    //views<
    lateinit var textViewCityName: TextView
    lateinit var textViewDescription: TextView
    lateinit var imageViewIcon: ImageView
    lateinit var textViewSunrise: TextView
    lateinit var textViewSunset: TextView
    lateinit var textViewTemp: TextView
    lateinit var imageViewCity: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var cardView: CardView
    //views>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        //views<
        asignViews()
        //views>
        requestAndGetData()
    }
    fun showContent(cityName:String, weatherDescription:String, urlIcon:String, sunrise:Int, sunset:Int, temp:Double) {
        cardView.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        textViewCityName.text = cityName
        textViewDescription.text = weatherDescription
        Glide.with(this).load(urlIcon).into(imageViewIcon)
        textViewSunrise.text = convertUnixTimeToClock(sunrise)
        textViewSunset.text = convertUnixTimeToClock(sunset)
        textViewTemp.text = temp.toInt().toString()
    }

    fun convertUnixTimeToClock(unixTime:Int):String{
        val date = Date(unixTime*1000.toLong())
        val simpleDateformatter = SimpleDateFormat("hh:mm:ss a")
        val timeInClock = simpleDateformatter.format(date)
        return timeInClock
    }
    fun asignViews(){
        textViewCityName = findViewById(R.id.textViewCityName)
        textViewDescription = findViewById(R.id.textViewDescription)
        imageViewIcon = findViewById(R.id.imageViewIcon)
        textViewSunrise = findViewById(R.id.textViewSunrise)
        textViewSunset = findViewById(R.id.textViewSunset)
        textViewTemp = findViewById(R.id.textViewTemp)
        imageViewCity = findViewById(R.id.imageViewCity)
        progressBar = findViewById(R.id.progressBar)
        cardView = findViewById(R.id.cardView)
    }
    fun getDataAndShowThem(rawContent:String){
        var jsonObject = JSONObject(rawContent)
        var jsonArray = jsonObject.getJSONArray("weather")
        var jsonObjectWeather = jsonArray.getJSONObject(0)
        var icon = jsonObjectWeather.getString("icon")
        var urlImage = "https://openweathermap.org/img/wn/${icon}@2x.png"
        var sysObject = jsonObject.getJSONObject("sys")
        var sunrise = sysObject.getInt("sunrise")
        var sunset = sysObject.getInt("sunset")
        var mainObject= jsonObject.getJSONObject("main")
        var temp = mainObject.getDouble("temp")
        runOnUiThread {
            showContent(
                jsonObject.getString("name"),
                jsonObjectWeather.getString("description"),
                urlImage,
                sunrise,
                sunset,
                temp
            )
        }
    }
    fun requestAndGetData(){
        var client = OkHttpClient()
        var request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=Damghan&appid=555b8a6c848d14bdad48dec79c24b766&lang=fa&units=metric")
            .build()
        var response = client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("tagx","onFailre: faild")
            }

            override fun onResponse(call: Call, response: Response) {
                var rawContent = "${response.body!!.string()}"
                getDataAndShowThem(rawContent)
            }
        })
    }
    fun reload(view:View){
        cardView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        textViewCityName.text = "نام شهر"
        textViewDescription.text = "وضعیت"
        textViewSunrise.text = "00:00:00 AM"
        textViewSunset.text = "00:00:00 PM"
        textViewTemp.text = "00"
        Glide.with(this).load(R.drawable.ic_refresh).into(imageViewIcon)
        requestAndGetData()

    }

}