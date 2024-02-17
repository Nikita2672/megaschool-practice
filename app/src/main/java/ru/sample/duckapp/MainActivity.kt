package ru.sample.duckapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api

class MainActivity : AppCompatActivity() {

    private lateinit var nextButton: Button
    private lateinit var duckImageView: ImageView
    private lateinit var codeEditText: EditText // Add an EditText to input the code

    private val ducksApi = Api.ducksApi

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        duckImageView = findViewById(R.id.duckImageView)
        nextButton = findViewById(R.id.nextButton)
        codeEditText = findViewById(R.id.codeEditText) // Initialize the EditText

        nextButton.setOnClickListener {
            val code = codeEditText.text.toString().toIntOrNull()
            if (code != null) {
                loadImage(BASE_URL + "http/" + code)
            } else {
                showToast("Invalid code. Please enter a valid integer.")
            }
        }
    }

    private fun fetchRandomDuckImage() {
        try {
            getRandomDuckAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getRandomDuckAsync() {
        ducksApi.getRandomDuck().enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    val duck = response.body()
                    duck?.let {
                        loadImage(it.url)
                    }
                } else {
                    showToast("Failed to fetch duck image")
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                showToast("Failed to fetch duck image")
            }
        })
    }

    private fun loadImage(imageUrl: String) {
        Picasso.get().load(imageUrl).into(duckImageView)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val BASE_URL = "https://random-d.uk/api/v2/"
    }
}
