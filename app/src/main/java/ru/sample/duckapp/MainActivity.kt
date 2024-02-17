package ru.sample.duckapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api

class MainActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var duckImageView: ImageView
    private lateinit var codeEditText: EditText
    private val ducksApi = Api.ducksApi

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        duckImageView = findViewById(R.id.duckImageView)
        sendButton = findViewById(R.id.nextButton)
        codeEditText = findViewById(R.id.codeEditText)

        sendButton.setOnClickListener {

            val codeText = codeEditText.text.toString()
            val code = codeText.toIntOrNull()

            if (codeText.isBlank()) {
                fetchRandomDuckImage()
            } else if (code == null) {
                showToast(INVALID_CODE)
            } else if (isCodeInWhiteList(code)) {
                fetchDuckByCodeImage(code)
            } else {
                showToast(CODE_NOT_FOUND)
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

    private fun fetchDuckByCodeImage(code: Int) {
        try {
            getDuckByCodeAsync(code)
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
                    showToast(FAILED_FETCH)
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                showToast(FAILED_FETCH)
            }
        })
    }

    private fun getDuckByCodeAsync(code: Int) {
        val imageUrl = BASE_URL + CODE_ENDPOINT + code
        loadImage(imageUrl)
    }

    private fun loadImage(imageUrl: String) {
        Picasso.get().load(imageUrl).into(duckImageView)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val BASE_URL = "https://random-d.uk/api/v2/"

        private const val CODE_ENDPOINT = "http/"

        private const val INVALID_CODE = "Invalid code. Please enter a valid integer."

        private const val CODE_NOT_FOUND = "Code not found. There is no duck with such code."

        private const val FAILED_FETCH = "Failed to fetch duck image."

        private val codeWhiteList = listOf(
            100, 200, 301, 302, 400, 403, 404, 409, 413, 418, 420, 426, 429, 451, 500
        )

        fun getCodeWhiteList(): List<Int> = codeWhiteList

        fun isCodeInWhiteList(code: Int): Boolean = code in codeWhiteList
    }
}
