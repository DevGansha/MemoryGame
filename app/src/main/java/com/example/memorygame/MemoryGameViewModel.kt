package com.example.memorygame

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MemoryGameViewModel: ViewModel() {
    var selectedLevel:  MutableLiveData<String> = MutableLiveData("")
    var total:  MutableLiveData<Int> = MutableLiveData(0)
    var score:  MutableLiveData<Int> = MutableLiveData(0)
    var randomDigit: MutableLiveData<Int> = MutableLiveData(0)

    fun setSelectedLevel(int: Int){
        selectedLevel.postValue("Your current level is $int")
    }

    fun setTotal(value: Int){
        total.postValue(value)
    }

    fun setScore(value: Int){
        score.postValue(value)
    }
    fun setRandomDigit(value: Int){
        randomDigit.postValue(value)
    }

}