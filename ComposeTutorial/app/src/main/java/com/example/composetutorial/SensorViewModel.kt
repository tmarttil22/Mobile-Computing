package com.example.composetutorial

import androidx.lifecycle.ViewModel
import com.example.composetutorial.Sensor.GyroSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    gyroSensor: GyroSensor
): ViewModel() {

    var _gyroData = MutableStateFlow(listOf(0f, 0f, 0f))
    val gyroData: StateFlow<List<Float>> = _gyroData.asStateFlow()

    init {
        gyroSensor.startListening()
        gyroSensor.updateOnSensorValuesChanged { values ->
            val gyroX = values[0]
            val gyroY = values[1]
            val gyroZ = values[2]
            _gyroData.value = listOf(gyroX, gyroY, gyroZ)
        }
    }
}