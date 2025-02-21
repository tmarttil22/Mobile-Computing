package com.example.composetutorial.Sensor

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor

class GyroScope(
    context: Context
): AndroidSensor(
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_GYROSCOPE,
    sensorType = Sensor.TYPE_GYROSCOPE
)