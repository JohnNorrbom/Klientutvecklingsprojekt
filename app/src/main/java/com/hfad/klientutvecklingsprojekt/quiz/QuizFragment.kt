package com.hfad.klientutvecklingsprojekt.quiz

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
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentQuizBinding
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class QuizFragment : Fragment() {

    private lateinit var questions: JSONArray
    private var currentQuestionIndex = 0
    private var score = 0 // Lägg till poängräknare
    private val handler = Handler()
    private var isAnswerChecked = false

    private lateinit var scoreTextView: TextView // Lägg till referens till textvy för poäng
    private var countDownTimer: CountDownTimer? = null
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val database = Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Quiz")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        try {
            //  Läs in frågorna från JSON-filen
            val jsonQuestions = loadJsonFromRawResource(R.raw.questions)
            val allQuestions = JSONArray(jsonQuestions)
            // Slumpmässigt välj 10 frågor
            questions = JSONArray(selectRandomQuestions(allQuestions, 10).toString())
            displayQuestion()
            // TOP LEFT BUTTON
            binding.option1Button.setOnClickListener {
                checkAnswer(binding.option1Button.text.toString(), binding.option1Button)
            }
            //  TOP RIGHT BUTTON
            binding.option2Button.setOnClickListener {
                checkAnswer(binding.option2Button.text.toString(), binding.option2Button)
            }
            //  BOTTOM LEFT BUTTON
            binding.option3Button.setOnClickListener {
                checkAnswer(binding.option3Button.text.toString(), binding.option3Button)
            }
            // BOTTOM RIGHT BUTTON
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
            //binding.scoreTextView.visibility = View.GONE

            // Visa poängen
            val scoreText = "End result: $score points"
            binding.questionTextView.text = scoreText
            //LADDA UPP POÄNG PÅ DATABASEN!
            myRef.child("GameID").child("userID").child("Score").setValue(score)

            val scoreRef = myRef.child("GameID").child("userID").child("Score")

// Hämta poängen från databasen
            scoreRef.get().addOnSuccessListener { dataSnapshot ->
                // Hämta poängvärdet
                val userScore = dataSnapshot.value

                // Skriv ut användarpoängen
                binding.scoreTextView.text = "$userScore"
            }.addOnFailureListener { e ->
                // Hantera fel här om det uppstår något problem med att hämta data
                binding.scoreTextView.text = "Failed to load scoreboard."
            }

        }
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
//
//
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
//    // Uppdatera textvyen för att visa poängen
    private fun updateScore() {
        scoreTextView.text = "Points: $score"
    }
    private fun startTimer() {
        // Avbryt den befintliga timern om det finns en
        countDownTimer?.cancel()

        // Skapa och starta en ny timer
        var time = 10
        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerTextView.text = "Time left: $time s"
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
    private fun selectRandomQuestions(allQuestions: JSONArray, numberOfQuestions: Int): List<JSONObject> {
        val selectedQuestions = mutableListOf<JSONObject>()
        val totalQuestions = allQuestions.length()

        // Skapa en lista med alla frågornas index
        val allQuestionsIndices = (0 until totalQuestions).toMutableList()

        // Slumpmässigt välj 10 index från listan
        val randomIndices = allQuestionsIndices.shuffled().take(numberOfQuestions)

        // Lägg till de slumpmässigt valda frågorna i den nya listan
        for (index in randomIndices) {
            selectedQuestions.add(allQuestions.getJSONObject(index))
        }

        return selectedQuestions
    }
}