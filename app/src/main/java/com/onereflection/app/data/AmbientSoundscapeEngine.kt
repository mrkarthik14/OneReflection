package com.onereflection.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.onereflection.app.model.ElementalTheme
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.tanh

/**
 * Real-time procedural background ambient soundscape synthesizer backed by Android's streaming
 * [AudioTrack]. Generates distinct organic soundscapes for every [ElementalTheme]:
 * - EMBER: Evening crickets & warm hearth crackle
 * - SOLAR: Sunlit meadow warmth & gentle morning songbirds
 * - BREEZE: Sweeping alpine wind gusts & rustling air currents
 * - BLOOM: Lotus garden breeze & soft pentatonic dew chimes
 * - CHERRY_BLOSSOM: Sakura spring breeze & hollow bamboo chimes
 * - SNOW: Hushed arctic stillness & crystalline frost shimmer
 * - RAIN: Steady monsoon rainfall patter & organic water droplets
 *
 * Crossfades smoothly between themes (~1.35s) and ramps volume without clicks or pops.
 */
class AmbientSoundscapeEngine {

    companion object {
        private const val SAMPLE_RATE = 22050
        private const val CHUNK_SAMPLES = 1024
        private const val CROSSFADE_SAMPLES = (SAMPLE_RATE * 1.35).toInt()
    }

    private val stateLock = Any()

    @Volatile
    private var desiredEnabled: Boolean = false

    @Volatile
    private var isForeground: Boolean = true

    @Volatile
    private var targetVolume: Float = 0.65f

    @Volatile
    private var activeTheme: ElementalTheme = ElementalTheme.BLOOM

    @Volatile
    private var previousTheme: ElementalTheme = ElementalTheme.BLOOM

    @Volatile
    private var crossfadeRemaining: Int = 0

    @Volatile
    private var workerRunning: Boolean = false

    private var workerThread: Thread? = null

    fun update(
        enabled: Boolean,
        theme: ElementalTheme,
        volume: Float
    ) {
        synchronized(stateLock) {
            val clampedVol = volume.coerceIn(0.0f, 1.0f)
            targetVolume = clampedVol

            if (theme != activeTheme) {
                previousTheme = activeTheme
                activeTheme = theme
                crossfadeRemaining = CROSSFADE_SAMPLES
            }

            desiredEnabled = enabled
            if (shouldStreamLocked() && !workerRunning) {
                startWorkerLocked()
            }
        }
    }

    fun setForeground(foreground: Boolean) {
        synchronized(stateLock) {
            isForeground = foreground
            if (shouldStreamLocked() && !workerRunning) {
                startWorkerLocked()
            }
        }
    }

    fun release() {
        synchronized(stateLock) {
            desiredEnabled = false
            isForeground = false
            workerRunning = false
        }
        workerThread?.interrupt()
        workerThread = null
    }

    private fun shouldStreamLocked(): Boolean = desiredEnabled && isForeground

    private fun startWorkerLocked() {
        workerRunning = true
        workerThread = thread(
            start = true,
            isDaemon = true,
            name = "OneReflection-AmbientSoundscape",
            priority = Thread.NORM_PRIORITY - 1
        ) {
            runStreamingLoop()
        }
    }

    private fun runStreamingLoop() {
        var audioTrack: AudioTrack? = null
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(CHUNK_SAMPLES * 2)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack.play()

            val buffer = ShortArray(CHUNK_SAMPLES)
            val dsp = ElementalDspBank(SAMPLE_RATE)
            var currentGain = 0.0
            var sampleIndex = 0L

            while (!Thread.currentThread().isInterrupted) {
                val wantPlay = desiredEnabled && isForeground
                val goalGain = if (wantPlay) targetVolume.toDouble() else 0.0

                val themeNow = activeTheme
                val themePrev = previousTheme
                var fadeLeft = crossfadeRemaining

                for (i in 0 until CHUNK_SAMPLES) {
                    val t = sampleIndex.toDouble() / SAMPLE_RATE
                    sampleIndex++

                    // Smooth gain envelope (~250ms fade-in/out) to eliminate clicks
                    currentGain += (goalGain - currentGain) * 0.00055

                    val rawSample = if (fadeLeft > 0 && themePrev != themeNow) {
                        val mix = 1.0 - (fadeLeft.toDouble() / CROSSFADE_SAMPLES.toDouble())
                        // Equal-power crossfade curve
                        val fadeIn = sin(mix * PI * 0.5)
                        val fadeOut = sin((1.0 - mix) * PI * 0.5)
                        fadeLeft--
                        dsp.sample(themePrev, t) * fadeOut + dsp.sample(themeNow, t) * fadeIn
                    } else {
                        dsp.sample(themeNow, t)
                    }

                    val shaped = tanh(rawSample * 1.15) * currentGain * 0.58
                    val pcm = (shaped * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    buffer[i] = pcm.toShort()
                }

                crossfadeRemaining = fadeLeft
                audioTrack.write(buffer, 0, CHUNK_SAMPLES)

                if (!wantPlay && currentGain < 0.0008) {
                    break
                }
            }
        } catch (_: Throwable) {
            // Safe fallback on headless environments without an audio sink
        } finally {
            try {
                audioTrack?.pause()
                audioTrack?.flush()
                audioTrack?.stop()
                audioTrack?.release()
            } catch (_: Throwable) {
                // Safe release
            }
            synchronized(stateLock) {
                workerRunning = false
                if (shouldStreamLocked()) {
                    startWorkerLocked()
                }
            }
        }
    }
}

/**
 * Stateful, zero-allocation real-time DSP soundscape bank producing distinct organic ambience
 * for each of the 7 Elemental Themes.
 */
private class ElementalDspBank(private val sampleRate: Int) {
    private var rngState: Long = 0x5DEECE66DL

    // Pink noise filter state (Paul Kellet 3-stage approximation)
    private var p0 = 0.0
    private var p1 = 0.0
    private var p2 = 0.0

    // Separate low/band filter states per acoustic character
    private var hearthLp = 0.0
    private var hearthCrackleEnv = 0.0

    private var windLp1 = 0.0
    private var windBp1 = 0.0
    private var windLp2 = 0.0
    private var windBp2 = 0.0

    private var rainLp = 0.0
    private var rainHpPrev = 0.0
    private var rainRumbleLp = 0.0
    private var rainDropTimer = 1800
    private var rainDropAge = 99999
    private var rainDropFreq = 980.0

    private var solarBirdTimer = 14000
    private var solarBirdAge = 99999
    private var solarBirdBaseFreq = 2500.0
    private var solarAirLp = 0.0

    private var bloomChimeTimer = 11000
    private var bloomChimeAge = 99999
    private var bloomChimeFreq = 587.33
    private var bloomAirLp = 0.0

    private var sakuraChimeTimer = 16000
    private var sakuraChimeAge = 99999
    private var sakuraChimeFreq = 523.25
    private var sakuraAirLp = 0.0

    private var snowChimeTimer = 24000
    private var snowChimeAge = 99999
    private var snowChimeFreq = 1567.98
    private var snowWindLp = 0.0

    private val bloomNotes = doubleArrayOf(587.33, 659.25, 739.99, 880.0, 987.77, 1174.66)
    private val sakuraNotes = doubleArrayOf(440.0, 493.88, 523.25, 659.25, 783.99, 880.0)
    private val snowNotes = doubleArrayOf(1318.51, 1567.98, 1760.0, 2093.0, 2349.32)

    private fun nextWhite(): Double {
        rngState = (rngState * 6364136223846793005L + 1442695040888963407L)
        val bits = ((rngState ushr 33) and 0x7FFFFFFFL).toDouble() / 0x7FFFFFFFL.toDouble()
        return bits * 2.0 - 1.0
    }

    private fun nextPink(): Double {
        val white = nextWhite()
        p0 = 0.99765 * p0 + white * 0.0990460
        p1 = 0.96300 * p1 + white * 0.2965164
        p2 = 0.57000 * p2 + white * 1.0526913
        return (p0 + p1 + p2 + white * 0.1848) * 0.11
    }

    fun sample(theme: ElementalTheme, t: Double): Double = when (theme) {
        ElementalTheme.EMBER -> sampleEmberCrickets(t)
        ElementalTheme.SOLAR -> sampleSolarMeadow(t)
        ElementalTheme.BREEZE -> sampleBreezeWind(t)
        ElementalTheme.BLOOM -> sampleBloomGarden(t)
        ElementalTheme.CHERRY_BLOSSOM -> sampleSakuraGrove(t)
        ElementalTheme.SNOW -> sampleSnowStillness(t)
        ElementalTheme.RAIN -> sampleMonsoonRain(t)
    }

    /**
     * EMBER: Rhythmic twilight crickets (dual detuned stridulating chirps around 4.2kHz & 3.95kHz)
     * layered over a cozy low-frequency glowing hearth bed & occasional soft ember crackles.
     */
    private fun sampleEmberCrickets(t: Double): Double {
        // Warm hearth rumble
        val pink = nextPink()
        hearthLp += (pink - hearthLp) * 0.035
        val hearthBed = hearthLp * 0.72

        // Occasional subtle fireplace ember pop/crackle
        if (nextWhite() > 0.9986) {
            hearthCrackleEnv = 0.35 + (nextWhite() * 0.25)
        }
        hearthCrackleEnv *= 0.991
        val crackle = nextWhite() * hearthCrackleEnv * 0.22

        // Primary cricket: 1.15s cycle with 3 crisp rhythmic chirp pulses per burst
        val cycleA = (t % 1.18)
        val chirpEnvA = if (cycleA < 0.34) {
            val syllable = (cycleA / 0.113) % 1.0
            val pulseGate = if (syllable < 0.72) sin((syllable / 0.72) * PI) else 0.0
            val trill = 0.5 + 0.5 * sin(2.0 * PI * 36.0 * t)
            pulseGate * (0.45 + 0.55 * trill)
        } else {
            0.0
        }
        val cricketA = chirpEnvA * (
            0.68 * sin(2.0 * PI * 4240.0 * t) +
                0.24 * sin(2.0 * PI * 4390.0 * t) +
                0.08 * sin(2.0 * PI * 8480.0 * t)
            ) * 0.20

        // Distant secondary cricket offset in phase & slightly lower pitch
        val cycleB = ((t + 0.57) % 1.46)
        val chirpEnvB = if (cycleB < 0.28) {
            val syllable = (cycleB / 0.14) % 1.0
            val gate = if (syllable < 0.68) sin((syllable / 0.68) * PI) else 0.0
            val trill = 0.5 + 0.5 * sin(2.0 * PI * 32.0 * t)
            gate * (0.5 + 0.5 * trill)
        } else {
            0.0
        }
        val cricketB = chirpEnvB * sin(2.0 * PI * 3920.0 * t) * 0.11

        return hearthBed + crackle + cricketA + cricketB
    }

    /**
     * SOLAR: Warm sunlit harmonic pad + gentle sunlit meadow air + soft morning birdsong trills.
     */
    private fun sampleSolarMeadow(t: Double): Double {
        val pink = nextPink()
        solarAirLp += (pink - solarAirLp) * 0.055
        val meadowAir = solarAirLp * 0.42

        // Golden sunlit warm drone (breathing slowly)
        val breath = 0.72 + 0.28 * sin(2.0 * PI * 0.11 * t)
        val warmDrone = breath * (
            0.48 * sin(2.0 * PI * 196.0 * t) +
                0.32 * sin(2.0 * PI * 293.66 * t) +
                0.18 * sin(2.0 * PI * 392.0 * t)
            ) * 0.14

        // Periodic gentle birdsong glide
        solarBirdTimer--
        if (solarBirdTimer <= 0) {
            solarBirdTimer = (sampleRate * (2.6 + (nextWhite() + 1.0) * 1.9)).toInt()
            solarBirdAge = 0
            solarBirdBaseFreq = 2250.0 + (nextWhite() + 1.0) * 480.0
        }
        var bird = 0.0
        if (solarBirdAge < sampleRate * 0.36) {
            val bt = solarBirdAge.toDouble() / sampleRate
            val env = sin((bt / 0.36) * PI) * exp(-2.8 * bt)
            val pitchCurve = solarBirdBaseFreq + 420.0 * sin(2.0 * PI * 5.2 * bt)
            bird = env * sin(2.0 * PI * pitchCurve * bt) * 0.13
            solarBirdAge++
        }

        return meadowAir + warmDrone + bird
    }

    /**
     * BREEZE: Sweeping alpine wind gusts crafted from dual resonant state-variable bandpass filters
     * modulating smoothly across airy frequency bands.
     */
    private fun sampleBreezeWind(t: Double): Double {
        val pink = nextPink()

        // Primary sweeping valley gust (280 Hz .. 760 Hz)
        val gustMod1 = 0.5 + 0.5 * sin(2.0 * PI * 0.13 * t + 0.4 * sin(2.0 * PI * 0.04 * t))
        val freq1 = 260.0 + gustMod1 * 480.0
        val f1 = (2.0 * sin(PI * freq1 / sampleRate)).coerceIn(0.02, 0.42)
        val q1 = 0.28
        val hp1 = pink - windLp1 - q1 * windBp1
        windBp1 += f1 * hp1
        windLp1 += f1 * windBp1

        // Secondary upper whistling air current (680 Hz .. 1380 Hz)
        val gustMod2 = 0.5 + 0.5 * sin(2.0 * PI * 0.09 * t + 1.8)
        val freq2 = 660.0 + gustMod2 * 640.0
        val f2 = (2.0 * sin(PI * freq2 / sampleRate)).coerceIn(0.04, 0.58)
        val q2 = 0.34
        val hp2 = pink - windLp2 - q2 * windBp2
        windBp2 += f2 * hp2
        windLp2 += f2 * windBp2

        val swell = 0.55 + 0.45 * gustMod1
        return (windBp1 * 0.62 * swell) + (windBp2 * 0.26 * (0.4 + 0.6 * gustMod2)) + (windLp1 * 0.18)
    }

    /**
     * BLOOM: Serene botanical garden air + soft resonant pentatonic dewdrop chimes.
     */
    private fun sampleBloomGarden(t: Double): Double {
        val pink = nextPink()
        bloomAirLp += (pink - bloomAirLp) * 0.065
        val gardenSwell = 0.65 + 0.35 * sin(2.0 * PI * 0.14 * t)
        val airBed = bloomAirLp * 0.48 * gardenSwell

        bloomChimeTimer--
        if (bloomChimeTimer <= 0) {
            bloomChimeTimer = (sampleRate * (1.8 + (nextWhite() + 1.0) * 1.4)).toInt()
            bloomChimeAge = 0
            val idx = (((nextWhite() + 1.0) * 0.5) * bloomNotes.size).toInt().coerceIn(0, bloomNotes.lastIndex)
            bloomChimeFreq = bloomNotes[idx]
        }

        var chime = 0.0
        if (bloomChimeAge < sampleRate * 2.2) {
            val ct = bloomChimeAge.toDouble() / sampleRate
            val env = exp(-2.1 * ct) * (1.0 - exp(-45.0 * ct))
            chime = env * (
                0.68 * sin(2.0 * PI * bloomChimeFreq * ct) +
                    0.22 * sin(2.0 * PI * bloomChimeFreq * 2.0 * ct) +
                    0.10 * sin(2.0 * PI * bloomChimeFreq * 3.0 * ct)
                ) * 0.17
            bloomChimeAge++
        }

        val warmHum = sin(2.0 * PI * 174.61 * t) * 0.045 * (0.7 + 0.3 * sin(2.0 * PI * 0.08 * t))
        return airBed + chime + warmHum
    }

    /**
     * CHERRY_BLOSSOM: Soft spring petal air + tranquil hollow bamboo wind chimes.
     */
    private fun sampleSakuraGrove(t: Double): Double {
        val pink = nextPink()
        sakuraAirLp += (pink - sakuraAirLp) * 0.09
        val sway = 0.6 + 0.4 * sin(2.0 * PI * 0.16 * t)
        val petalBreeze = sakuraAirLp * 0.46 * sway

        sakuraChimeTimer--
        if (sakuraChimeTimer <= 0) {
            sakuraChimeTimer = (sampleRate * (2.1 + (nextWhite() + 1.0) * 1.8)).toInt()
            sakuraChimeAge = 0
            val idx = (((nextWhite() + 1.0) * 0.5) * sakuraNotes.size).toInt().coerceIn(0, sakuraNotes.lastIndex)
            sakuraChimeFreq = sakuraNotes[idx]
        }

        var bamboo = 0.0
        if (sakuraChimeAge < sampleRate * 1.9) {
            val bt = sakuraChimeAge.toDouble() / sampleRate
            // Hollow bamboo attack + resonant decay
            val env = exp(-2.8 * bt) * (1.0 - exp(-70.0 * bt))
            bamboo = env * (
                0.76 * sin(2.0 * PI * sakuraChimeFreq * bt) +
                    0.18 * sin(2.0 * PI * sakuraChimeFreq * 2.756 * bt) * exp(-4.5 * bt)
                ) * 0.18
            sakuraChimeAge++
        }

        return petalBreeze + bamboo
    }

    /**
     * SNOW: Deep hushed arctic snowfall silence bed + sparse ethereal frost crystal shimmers.
     */
    private fun sampleSnowStillness(t: Double): Double {
        val pink = nextPink()
        snowWindLp += (pink - snowWindLp) * 0.028
        val slowDrift = 0.55 + 0.45 * sin(2.0 * PI * 0.07 * t)
        val arcticBed = snowWindLp * 0.58 * slowDrift

        // Soft crystalline sub-pad
        val frostPad = (
            0.55 * sin(2.0 * PI * 261.63 * t) +
                0.45 * sin(2.0 * PI * 392.0 * t)
            ) * 0.055 * (0.65 + 0.35 * sin(2.0 * PI * 0.06 * t))

        snowChimeTimer--
        if (snowChimeTimer <= 0) {
            snowChimeTimer = (sampleRate * (2.8 + (nextWhite() + 1.0) * 2.1)).toInt()
            snowChimeAge = 0
            val idx = (((nextWhite() + 1.0) * 0.5) * snowNotes.size).toInt().coerceIn(0, snowNotes.lastIndex)
            snowChimeFreq = snowNotes[idx]
        }

        var crystal = 0.0
        if (snowChimeAge < sampleRate * 2.4) {
            val st = snowChimeAge.toDouble() / sampleRate
            val env = exp(-1.9 * st) * (1.0 - exp(-35.0 * st))
            crystal = env * (
                0.75 * sin(2.0 * PI * snowChimeFreq * st) +
                    0.25 * sin(2.0 * PI * (snowChimeFreq * 1.004) * st)
                ) * 0.095
            snowChimeAge++
        }

        return arcticBed + frostPad + crystal
    }

    /**
     * RAIN: Continuous monsoon rainfall patter + deep distant cloud rumble + organic water droplet plinks.
     */
    private fun sampleMonsoonRain(t: Double): Double {
        val white = nextWhite()
        val pink = nextPink()

        // Steady rain shower wash (bright-balanced filtered spectrum with soft natural swells)
        rainLp += (white - rainLp) * 0.34
        val rainTexture = (rainLp - rainHpPrev * 0.35)
        rainHpPrev = rainLp
        val rainSwell = 0.78 + 0.22 * sin(2.0 * PI * 0.11 * t + 0.5 * sin(2.0 * PI * 0.035 * t))
        val rainBed = (rainTexture * 0.36 + pink * 0.38) * rainSwell

        // Soft distant monsoon cloud rumble
        rainRumbleLp += (pink - rainRumbleLp) * 0.018
        val rumble = rainRumbleLp * 0.52

        // Organic randomized water droplet plinks on leaves/puddles
        rainDropTimer--
        if (rainDropTimer <= 0) {
            rainDropTimer = (sampleRate * (0.14 + (nextWhite() + 1.0) * 0.28)).toInt()
            rainDropAge = 0
            rainDropFreq = 780.0 + (nextWhite() + 1.0) * 460.0
        }
        var droplet = 0.0
        if (rainDropAge < sampleRate * 0.09) {
            val dt = rainDropAge.toDouble() / sampleRate
            val env = exp(-34.0 * dt) * (1.0 - exp(-240.0 * dt))
            // Upward bubble-pitch sweep authentic to falling water drops
            val pitch = rainDropFreq + 640.0 * (dt / 0.09)
            droplet = env * sin(2.0 * PI * pitch * dt) * 0.14
            rainDropAge++
        }

        return rainBed + rumble + droplet
    }
}
