package com.onereflection.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.onereflection.app.model.ChimeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object ChimePlayer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var activeJob: Job? = null

    fun play(type: ChimeType) {
        activeJob?.cancel()
        activeJob = scope.launch {
            runCatching {
                val sampleRate = 22050
                val durationSec = when (type) {
                    ChimeType.CLICK -> 0.055
                    ChimeType.WATER -> 0.16
                    ChimeType.NORDIC -> 1.1
                    ChimeType.SOLFEGGIO -> 1.4
                    ChimeType.PIANO -> 1.2
                    ChimeType.RIVER -> 1.5
                    ChimeType.TIBETAN -> 1.6
                }
                val numSamples = (sampleRate * durationSec).toInt()
                val pcm = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val sample = when (type) {
                        ChimeType.CLICK -> {
                            val freq = 800.0 * exp(-t * 14.0)
                            val env = exp(-t * 45.0)
                            0.25 * env * sin(2.0 * PI * freq * t)
                        }
                        ChimeType.WATER -> {
                            val freq = 450.0 + 500.0 * progress
                            val env = sin(PI * progress) * exp(-t * 6.0)
                            0.32 * env * sin(2.0 * PI * freq * t)
                        }
                        ChimeType.TIBETAN -> {
                            val env = (1.0 - exp(-t * 10.0)) * exp(-t * 1.9)
                            val s = sin(2.0 * PI * 180.0 * t) * 0.50 +
                                sin(2.0 * PI * 362.0 * t) * 0.25 +
                                sin(2.0 * PI * 544.0 * t) * 0.15 +
                                sin(2.0 * PI * 728.0 * t) * 0.10
                            0.30 * env * s
                        }
                        ChimeType.NORDIC -> {
                            val env = exp(-t * 3.2)
                            val s = sin(2.0 * PI * 880.0 * t) * 0.55 +
                                sin(2.0 * PI * 1320.0 * t) * 0.30 +
                                sin(2.0 * PI * 1760.0 * t) * 0.15
                            0.22 * env * s
                        }
                        ChimeType.SOLFEGGIO -> {
                            val env = (1.0 - exp(-t * 12.0)) * exp(-t * 2.1)
                            0.26 * env * sin(2.0 * PI * 528.0 * t)
                        }
                        ChimeType.PIANO -> {
                            val env = exp(-t * 3.0)
                            val s = sin(2.0 * PI * 261.6 * t) +
                                sin(2.0 * PI * 329.6 * t) * 0.8 +
                                sin(2.0 * PI * 392.0 * t) * 0.7
                            0.18 * env * s
                        }
                        ChimeType.RIVER -> {
                            val env = sin(PI * progress)
                            val s = sin(2.0 * PI * 320.0 * t) * 0.5 +
                                sin(2.0 * PI * 480.0 * t) * 0.3 +
                                sin(2.0 * PI * 640.0 * t) * 0.2
                            0.18 * env * s
                        }
                    }
                    pcm[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                }

                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(pcm.size * 2)

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
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()
                kotlinx.coroutines.delay((durationSec * 1000).toLong() + 80L)
                track.stop()
                track.release()
            }
        }
    }

    fun stop() {
        activeJob?.cancel()
        activeJob = null
    }
}
