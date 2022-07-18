package com.example.memorygame

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.memorygame.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MemoryGame : AppCompatActivity() {

    lateinit var myJob: Job
//    lateinit var numbers: List<Int>
    var num = ""
    var score=0
    var total = 0
    var startDigit: Double = 10.0
    var endDigit: Double = 99.0

    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var memoryGameViewModel: MemoryGameViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding =  DataBindingUtil.setContentView(this, R.layout.activity_main)
        memoryGameViewModel = ViewModelProvider(this).get(MemoryGameViewModel ::class.java)

        observeUI()

        memoryGameViewModel.setSelectedLevel(rangeSlider.value.toInt())

        rangeSlider.addOnChangeListener { slider, value, fromUser ->
            startDigit = Math.pow(10.toDouble(), value.toDouble()-1)
            endDigit = Math.pow(10.toDouble(), value.toDouble()) - 1


            memoryGameViewModel.setSelectedLevel(rangeSlider.value.toInt())
        }

        startGameBtn.setOnClickListener {
            beforeGame.visibility = View.GONE
            afterGame.visibility = View.VISIBLE

            gameStart()
        }

        submitAns.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                hideKeyboard(v!!)
                if (!output.text.isBlank()) {
                    if(output.text.toString().equals(num.toSortedSet().joinToString().replace(", ", "").trim()))  {
                        score++
                        memoryGameViewModel.setScore(score)
                    }

                    output.text.clear()
//                    scoreBoard.text = "Correct $score | Incorrect ${total - score}"
                    myJob.cancel()
                }
            }})

        endGame.setOnClickListener {
            if(myJob.isActive) myJob.cancel()

            beforeGame.visibility = View.VISIBLE
            afterGame.visibility = View.GONE
            total = 0
            score = 0
            memoryGameViewModel.setTotal(total)
            memoryGameViewModel.setScore(score)
  //          scoreBoard.text = "Correct $score | Incorrect ${total - score}"
        }
        playAgain.setOnClickListener {
            gameStart()
        }
    }

    fun gameStart(){
        var randomNum = (startDigit.toInt()..endDigit.toInt()).shuffled().random().toString()
        num = randomNum
        var numbers = randomNum.map { it.toString().toInt() }

        Log.d("NUMBER", randomNum)

        total++

        memoryGameViewModel.setTotal(total)
        myJob = startRepeatingJob(1000L, numbers)
    }

    fun observeUI(){
        memoryGameViewModel.selectedLevel.observe(this){
            activityMainBinding.selectedLevel.text = it
        }
        memoryGameViewModel.score.observe(this){
            activityMainBinding.scoreBoard.text = "Correct $it | Incorrect ${memoryGameViewModel.total.value!! - it}"
        }
        memoryGameViewModel.randomDigit.observe(this){
            activityMainBinding.digitTxt.text = if(it==0) "" else it.toString()
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun startRepeatingJob(timeInterval: Long, numbers: List<Int>): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (NonCancellable.isActive) {
                numbers.forEach {
                    memoryGameViewModel.setRandomDigit(it)
                    //digitTxt.text = it.toString()
                    delay(timeInterval)
                }
//                digitTxt.text = ""
                memoryGameViewModel.setRandomDigit(0)
                break
            }
        }
    }
    private fun hideKeyboard(view: View) {
        view.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}