package com.hfad.klientutvecklingsprojekt

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.json.JSONArray
import androidx.appcompat.app.AppCompatActivity
import com.hfad.klientutvecklingsprojekt.databinding.ActivityMainBinding
import com.hfad.klientutvecklingsprojekt.databinding.FragmentQuizBinding
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var questions: JSONArray
    private var currentQuestionIndex = 0
    private var score = 0 // Lägg till poängräknare
    private val handler = Handler()
    private var isAnswerChecked = false

    private lateinit var scoreTextView: TextView // Lägg till referens till textvy för poäng
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("I AM IN CONSTRUCUTR RIGHT NOW !!!! !!! !")
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        try {
            questions = JSONArray(jsonQuestions)
            displayQuestion()

            // Sätt upp lyssnare för knapparna
            binding.option1Button.setOnClickListener {
                checkAnswer(binding.option1Button.text.toString(), binding.option1Button)
            }

            binding.option2Button.setOnClickListener {
                checkAnswer(binding.option2Button.text.toString(), binding.option2Button)
            }

            binding.option3Button.setOnClickListener {
                checkAnswer(binding.option3Button.text.toString(), binding.option3Button)
            }

            binding.option4Button.setOnClickListener {
                checkAnswer(binding.option4Button.text.toString(), binding.option4Button)
            }

            // Hitta referensen till textvyen för poäng
            scoreTextView = binding.scoreTextView

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return binding.root
    }


    // Läs in frågorna från JSON-filen
    val jsonQuestions = loadJsonFromRawResource(R.raw.questions)


    private fun loadJsonFromRawResource(resourceId: Int): String {
        var json: String? = null
        try {
            // Öppna en InputStream för den råa resursen
            val inputStream: InputStream = resources.openRawResource(resourceId)

            // Läs innehållet i filen
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            // Konvertera bytestream till sträng
            json = String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json ?: ""
    }




    private fun resetButtonColors() {
        binding.option1Button.apply {
            setBackgroundColor(Color.BLACK)
            isEnabled = true
            setClickable(true)
        }
        binding.option2Button.apply {
            setBackgroundColor(Color.BLACK)
            isEnabled = true
            setClickable(true)
        }
        binding.option3Button.apply {
            setBackgroundColor(Color.BLACK)
            isEnabled = true
            setClickable(true)
        }
        binding.option4Button.apply {
            setBackgroundColor(Color.BLACK)
            isEnabled = true
            setClickable(true)
        }
    }

    private fun displayQuestion() {
        resetButtonColors()

        if (currentQuestionIndex < questions.length()) {
            val questionObject = questions.getJSONObject(currentQuestionIndex)

            // Sätt upp frågan och alternativen
            val questionText = questionObject.getString("question")
            val optionsArray = questionObject.getJSONArray("options")

            // Sätt frågetexten i TextView
            binding.questionTextView.text = questionText

            // Sätt alternativen på knapparna
            for (i in 0 until optionsArray.length()) {
                val option = optionsArray.getString(i)
                when (i) {
                    0 -> binding.option1Button.text = option
                    1 -> binding.option2Button.text = option
                    2 -> binding.option3Button.text = option
                    3 -> binding.option4Button.text = option
                }
            }
            startTimer()
        } else {
            // Alla frågor är besvarade
            binding.option1Button.visibility = View.GONE
            binding.option2Button.visibility = View.GONE
            binding.option3Button.visibility = View.GONE
            binding.option4Button.visibility = View.GONE

            // Visa poängen
            val scoreText = "Ditt slutresultat: $score poäng"
            binding.questionTextView.text = scoreText
        }
    }

    private fun checkAnswer(selectedOption: String, selectedButton: View) {
        val correctAnswer = questions.getJSONObject(currentQuestionIndex).getString("correctAnswer")

        val buttons = listOf(
            binding.option1Button,
            binding.option2Button,
            binding.option3Button,
            binding.option4Button
        )

        for (button in buttons) {
            if (button.text == correctAnswer) {
                // Rätt svar
                button.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
                if (button.text == selectedOption && !isAnswerChecked) {
                    // Om detta är det valda alternativet och svaret inte har kontrollerats tidigare
                    isAnswerChecked = true // Markera svaret som kontrollerat
                    score++ // Öka poängen vid rätt svar
                    updateScore() // Uppdatera textvyen för att visa den nya poängen
                } else if (button.text == selectedOption) {
                    // Om detta inte är det valda alternativet, sätt färgen till rött
                    selectedButton.setBackgroundColor(resources.getColor(android.R.color.holo_red_light, null))
                }
            } else if (button.text == selectedOption) {
                isAnswerChecked = true
                button.setBackgroundColor(resources.getColor(android.R.color.holo_red_light, null))
            } else {
                button.isEnabled = false
            }
            button.setClickable(false)
        }

        // Fördröjning innan nästa fråga
        handler.postDelayed({
            isAnswerChecked = false // Återställ isAnswerChecked för nästa fråga
            currentQuestionIndex++
            displayQuestion() // Visa nästa fråga
        }, 2000) // 2000 ms = 2 sekunder
    }




    // Uppdatera textvyen för att visa poängen
    private fun updateScore() {
        scoreTextView.text = "Poäng: $score"
    }
    private fun startTimer() {
        // Avbryt den befintliga timern om det finns en
        countDownTimer?.cancel()

        // Skapa och starta en ny timer
        var time = 10
        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerTextView.text = "Tid kvar: $time s"
                time--
                if (time < 4){
                    binding.timerTextView.setTextColor(Color.RED)
                }
                else
                    binding.timerTextView.setTextColor(Color.BLACK)

            }

            override fun onFinish() {
                currentQuestionIndex++
                displayQuestion()
            }
        }

        countDownTimer?.start()
    }


}