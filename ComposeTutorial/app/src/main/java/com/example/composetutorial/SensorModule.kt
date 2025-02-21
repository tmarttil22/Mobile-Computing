package com.example.composetutorial

import android.app.Application
import com.example.composetutorial.Sensor.GyroScope
import com.example.composetutorial.Sensor.GyroSensor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @Provides
    @Singleton
    fun provideGyroSensor(app: Application): GyroSensor {
        return GyroScope(app)
    }
}