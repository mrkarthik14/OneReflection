package com.onereflection.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.onereflection.app.model.ChimeType
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class ChimePlayer {
    @Volatile
    private var activeTrack: AudioTrack? = null

    fun play(type: ChimeType) {
        stop()
        thread(start = true, isDaemon = true) {
            try {
                val sampleRate = 22050
                val durationSec = when (type) {
                    ChimeType.CLICK -> 0.08
                    ChimeType.WATER -> 0.35
                    else -> 1.15
                }
                val numSamples = (sampleRate * durationSec).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val sample = when (type) {
                        ChimeType.TIBETAN -> {
                            val env = exp(-2.4 * t) * (1.0 - exp(-22.0 * t))
                            env * (0.6 * sin(2.0 * PI * 432.0 * t) + 0.3 * sin(2.0 * PI * 864.0 * t) + 0.1 * sin(2.0 * PI * 648.0 * t))
                        }
                        ChimeType.NORDIC -> {
                            val env = exp(-3.0 * t) * (1.0 - exp(-30.0 * t))
                            env * (0.55 * sin(2.0 * PI * 528.0 * t) + 0.35 * sin(2.0 * PI * 792.0 * t))
                        }
                        ChimeType.SOLFEGGIO -> {
                            val env = exp(-2.8 * t) * (1.0 - exp(-28.0 * t))
                            env * (0.65 * sin(2.0 * PI * 528.0 * t) + 0.25 * sin(2.0 * PI * 1056.0 * t))
                        }
                        ChimeType.WATER -> {
                            val freq = 620.0 + 980.0 * (t / durationSec)
                            val env = exp(-9.0 * t) * (1.0 - exp(-60.0 * t))
                            env * sin(2.0 * PI * freq * t)
                        }
                        ChimeType.CLICK -> {
                            val env = exp(-48.0 * t)
                            env * sin(2.0 * PI * 880.0 * t)
                        }
                        ChimeType.PIANO -> {
                            val env = exp(-3.2 * t) * (1.0 - exp(-45.0 * t))
                            env * (0.55 * sin(2.0 * PI * 440.0 * t) + 0.3 * sin(2.0 * PI * 554.37 * t) + 0.15 * sin(2.0 * PI * 659.25 * t))
                        }
                        ChimeType.RIVER -> {
                            val env = exp(-2.0 * t) * (1.0 - exp(-15.0 * t))
                            env * (0.5 * sin(2.0 * PI * 396.0 * t) + 0.35 * sin(2.0 * PI * 594.0 * t))
                        }
                    }
                    buffer[i] = (sample * 0.42 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                activeTrack = track
                track.write(buffer, 0, buffer.size)
                track.play()
            } catch (_: Throwable) {
                // Ignore audio hardware limitations in headless environments
            }
        }
    }

    fun stop() {
        val current = activeTrack
        activeTrack = null
        try {
            current?.stop()
            current?.release()
        } catch (_: Throwable) {
            // Safe cleanup
        }
    }
}
