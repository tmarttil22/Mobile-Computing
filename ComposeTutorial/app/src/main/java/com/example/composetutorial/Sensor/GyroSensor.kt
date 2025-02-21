package com.example.composetutorial.Sensor

// https://www.youtube.com/watch?v=IU-EAtITRRM followed as a guide
abstract class GyroSensor(protected val sensorType: Int) {

    protected var onSensorValuesChanged: ((List<Float>) -> Unit)? = null

    abstract val doesSensorExist: Boolean

    abstract fun startListening()
    abstract fun stopListening()

    fun updateOnSensorValuesChanged(listener: (List<Float>) -> Unit) {
        onSensorValuesChanged = listener
    }
}