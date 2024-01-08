package com.hfad.klientutvecklingsprojekt.quiz

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.currentRecomposeScope
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import org.json.JSONArray
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.hfad.klientutvecklingsprojekt.R
import com.hfad.klientutvecklingsprojekt.databinding.FragmentQuizBinding
import com.hfad.klientutvecklingsprojekt.gamestart.GameModel
import com.hfad.klientutvecklingsprojekt.player.MeData
import com.hfad.klientutvecklingsprojekt.player.MeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.Random


class QuizFragment : Fragment() {

    private lateinit var questions: JSONArray
    private var currentQuestionIndex = 0
    private var score = 0 // Lägg till poängräknare
    private val handler = Handler()
    private var isAnswerChecked = false
    private var meModel: MeModel? = null
    private var currentGameID = ""
    private var currentPlayerID = ""
    private lateinit var scoreTextView: TextView // Lägg till referens till textvy för poäng
    private var countDownTimer: CountDownTimer? = null
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val database =
        Firebase.database("https://klientutvecklingsprojekt-default-rtdb.europe-west1.firebasedatabase.app/")
    private val myRef = database.getReference("Quiz")
    var totalPlayersCount: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val view = binding.root

        MeData.meModel.observe(context as LifecycleOwner) { meModel ->
            meModel?.let {
                this@QuizFragment.meModel = it
                setText()
            } ?: run {
                Log.e("QuizFragment", "meModel is null")
            }
        }

        // Berätta för databasen att du inte är klar med quizet
        myRef.child(currentGameID).child("Players").child(currentPlayerID).child("doneWithQuiz").setValue(false)

        // Anropa metoden för att hämta seed och ladda frågor
        fetchQuizSeed()


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

        println("Körs detta flera gånger??")
        return view
    }
    private fun fetchQuizSeed() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val seedRef = myRef.child(currentGameID).child("seed")
            val dataSnapshot = seedRef.get().await()
            if (dataSnapshot.exists()) {
                val seed = dataSnapshot.getValue(Int::class.java)
                seed?.let {
                    withContext(Dispatchers.Main) {
                        loadQuestions(it)
                    }
                }
            } else {
                println("Seed finns inte i databasen")
            }
        } catch (e: Exception) {
            Log.e("QuizFragment", "Error fetching seed", e)
        }
    }
    private fun loadQuestions(seed: Int) {
        try {
            val jsonQuestions = loadJsonFromRawResource(R.raw.questions)
            val allQuestions = JSONArray(jsonQuestions)
            questions = JSONArray(selectRandomQuestions(allQuestions, 10, seed.toLong()).toString())
            displayQuestion()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun setText() {
        currentGameID = meModel?.gameID ?: ""
        currentPlayerID = meModel?.playerID ?: ""
        Log.d("QuizFragment", "playerID: ${currentPlayerID} GameID: ${currentGameID}")
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
            //alla frågor är besvarade
            stopTimer()
            binding.timerTextView.visibility = View.GONE
            finishQuiz()


        }


    }

    private fun resetButtonColors() {
        binding.option1Button.apply {
            setBackgroundColor(Color.WHITE)
            isEnabled = true
            setClickable(true)

        }
        binding.option2Button.apply {
            setBackgroundColor(Color.WHITE)
            isEnabled = true
            setClickable(true)
        }
        binding.option3Button.apply {
            setBackgroundColor(Color.WHITE)
            isEnabled = true
            setClickable(true)
        }
        binding.option4Button.apply {
            setBackgroundColor(Color.WHITE)
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
                button.setBackgroundColor(
                    resources.getColor(
                        android.R.color.holo_green_light,
                        null
                    )
                )
                if (button.text == selectedOption && !isAnswerChecked) {
                    // Om detta är det valda alternativet och svaret inte har kontrollerats tidigare
                    isAnswerChecked = true // Markera svaret som kontrollerat
                    score++ // Öka poängen vid rätt svar
                    updateScore() // Uppdatera textvyen för att visa den nya poängen
                } else if (button.text == selectedOption) {
                    // Om detta inte är det valda alternativet, sätt färgen till rött
                    selectedButton.setBackgroundColor(
                        resources.getColor(
                            android.R.color.holo_red_light,
                            null
                        )
                    )
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
                if (time < 4) {
                    binding.timerTextView.setTextColor(Color.RED)
                } else
                    binding.timerTextView.setTextColor(Color.BLACK)

            }

            override fun onFinish() {
                currentQuestionIndex++
                displayQuestion()
            }
        }
        countDownTimer?.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
    }

    private fun selectRandomQuestions(allQuestions: JSONArray, numberOfQuestions: Int, seed: Long): List<JSONObject> {
        val random = Random(seed)
        val totalQuestions = allQuestions.length()
        val allQuestionsIndices = (0 until totalQuestions).toMutableList()
        val randomIndices = allQuestionsIndices.shuffled(random).take(numberOfQuestions)

        return randomIndices.map { allQuestions.getJSONObject(it) }
    }

    private fun finishQuiz() {
        // Dölj alternativknapparna och visa poängen
        binding.option1Button.visibility = View.GONE
        binding.option2Button.visibility = View.GONE
        binding.option3Button.visibility = View.GONE
        binding.option4Button.visibility = View.GONE

        val scoreText = "End result: $score points"
        binding.questionTextView.text = scoreText

        // Berätta för databasen att du är klar med quiz
        myRef.child(currentGameID).child("Players").child(currentPlayerID).child("doneWithQuiz")
            .setValue(true)

        // Ladda upp poäng på databasen
        myRef.child(currentGameID).child("Scores").child(currentPlayerID).setValue(score)
        binding.scoreTextView.text = "WAITING FOR ALL PLAYERS TO FINISH THE QUIZ"
        // Initiera processen för att visa leaderboard
        initiateLeaderboardDisplay()
    }

    private fun initiateLeaderboardDisplay() {
        val playersCountRef = myRef.child(currentGameID).child("Players")
        playersCountRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totalPlayersCount = dataSnapshot.childrenCount.toInt()

                val playersRef = myRef.child(currentGameID).child("Players")
                playersRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var donePlayersCount = 0
                        for (playerSnapshot in dataSnapshot.children) {
                            val done =
                                playerSnapshot.child("doneWithQuiz").getValue(Boolean::class.java)
                            if (done == true) {
                                donePlayersCount++
                            }
                        }

                        if (donePlayersCount == totalPlayersCount) {
                            // Alla spelare är klara, visa leaderboard
                            showLeaderboard()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Hantera eventuella fel här
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Hantera eventuella fel här
            }
        })
    }

    data class PlayerScore(val nickname: String?, val score: Int)

    private fun showLeaderboard() = CoroutineScope(Dispatchers.Main).launch {
        try {
            val scoresRef = myRef.child(currentGameID).child("Scores")
            val playerRef =
                database.getReference("Player Data").child(currentGameID).child("players")
            val dataSnapshot = withContext(Dispatchers.IO) { scoresRef.get().await() }
            if (dataSnapshot.exists()) {
                val scoresList = mutableListOf<PlayerScore>()

                for (userSnapshot in dataSnapshot.children) {
                    val userNickname = withContext(Dispatchers.IO) {
                        playerRef.child(userSnapshot.key ?: "").child("nickname").get()
                            .await().value as String?
                    }
                    val userScore = userSnapshot.value.toString().toIntOrNull() ?: 0
                    scoresList.add(PlayerScore(userNickname, userScore))
                }

                // Sortera listan så att högsta poängen kommer först
                val sortedList = scoresList.sortedByDescending { it.score }

                val allScores = StringBuilder()
                sortedList.forEach {
                    allScores.append("User: ${it.nickname}, Score: ${it.score}\n")
                }

                binding.scoreTextView.text = allScores.toString()
                //PLUSSA PÅ SPELARENS POÄNG I DATABASEN
                println("Kallar på increasePlayerScore")
                increasePlayerScore(currentPlayerID, score)
                delay(10000)
                if (isAdded && view != null) {
                    database.getReference().child("Board Data").child(currentGameID)
                        .child("randomVal").setValue(-1)
                    view?.findNavController()
                        ?.navigate(R.id.action_quizFragment_to_testBoardFragment)
                }
            } else {
                binding.scoreTextView.text = "Ingen poäng att visa."
            }
        } catch (e: Exception) {
            binding.scoreTextView.text = "Failed to load scores."
            e.printStackTrace()
        }
    }

    private fun increasePlayerScore(playerId: String, increment: Int) =
        CoroutineScope(Dispatchers.IO).launch {
            val scoreRef = database.getReference("Player Data")
                .child(currentGameID)
                .child("players")
                .child(playerId)
                .child("score")

            try {
                val currentScoreSnapshot = scoreRef.get().await()
                val currentScore = currentScoreSnapshot.getValue(Int::class.java) ?: 0
                val newScore = currentScore + increment

                withContext(Dispatchers.Main) {
                    scoreRef.setValue(newScore).addOnSuccessListener {
                        // Uppdatera UI här, visa bekräftelsemeddelande, etc.
                        Log.d("UpdateScore", "Poängen har framgångsrikt uppdaterats till $newScore")
                        // Exempel: Toast.makeText(context, "Poäng uppdaterad till $newScore", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        // Hantera misslyckad uppdatering
                        Log.e("UpdateScore", "Misslyckades med att uppdatera poängen", it)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hantera fel vid läsning av poängen
                    Log.e("UpdateScore", "Fel vid läsning av aktuell poäng", e)
                }
            }
        }
}