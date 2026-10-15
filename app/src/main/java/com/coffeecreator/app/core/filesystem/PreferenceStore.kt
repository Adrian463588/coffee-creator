package com.coffeecreator.app.core.filesystem

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.coffeecreator.app.preferences.AppPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

private object PreferenceSerializer : Serializer<AppPreferences> {
    override val defaultValue: AppPreferences = AppPreferences.newBuilder()
        .setTheme("system").setMaxTokens(128).setTemperature(0.4f).build()
    override suspend fun readFrom(input: InputStream): AppPreferences = try {
        AppPreferences.parseFrom(input)
    } catch (failure: InvalidProtocolBufferException) {
        throw CorruptionException("Preferences cannot be read; no records were changed.", failure)
    }
    override suspend fun writeTo(t: AppPreferences, output: OutputStream) = t.writeTo(output)
}

private val Context.appPreferences by dataStore(fileName = "coffee-preferences.pb", serializer = PreferenceSerializer)

class PreferenceStore(private val context: Context) {
    val data = context.appPreferences.data
    suspend fun setTheme(theme: String) {
        require(theme in setOf("system", "light", "dark")) { "Invalid theme." }
        context.appPreferences.updateData { it.toBuilder().setTheme(theme).build() }
    }
    suspend fun setGeneration(maxTokens: Int, temperature: Float) {
        require(maxTokens in 16..256 && temperature.isFinite() && temperature in 0.1f..1.2f) { "Invalid generation parameters." }
        context.appPreferences.updateData { it.toBuilder().setMaxTokens(maxTokens).setTemperature(temperature).build() }
    }
    suspend fun finishOnboarding() {
        context.appPreferences.updateData { it.toBuilder().setOnboarded(true).build() }
    }
}
