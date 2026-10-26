class AudioService {
  private audioCtx: AudioContext | null = null;
  private mediaRecorder: MediaRecorder | null = null;
  private recordedChunks: Blob[] = [];
  private analyser: AnalyserNode | null = null;
  private sourceNode: MediaStreamAudioSourceNode | null = null;
  private micStream: MediaStream | null = null;
  private currentAudioElement: HTMLAudioElement | null = null;

  private getContext(): AudioContext {
    if (!this.audioCtx) {
      const AudioContextClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      this.audioCtx = new AudioContextClass();
    }
    if (this.audioCtx.state === 'suspended') {
      this.audioCtx.resume();
    }
    return this.audioCtx;
  }

  // Play a synthesized soundscape (Tibetan bowl, Nordic glass, solfeggio chime, piano, river)
  playChime(type: 'tibetan' | 'nordic' | 'solfeggio' | 'water' | 'click' | 'piano' | 'river' = 'tibetan') {
    const ctx = this.getContext();
    const now = ctx.currentTime;

    if (type === 'piano') {
      // Warm piano chord (C major: 261.6, 329.6, 392.0)
      [261.6, 329.6, 392.0].forEach((freq) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(freq, now);
        gain.gain.setValueAtTime(0.12, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 1.8);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(now);
        osc.stop(now + 2.0);
      });
      return;
    }

    if (type === 'river') {
      // Ambient river glow / water rush
      const freqs = [320, 480, 640];
      freqs.forEach((freq) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(freq, now);
        gain.gain.setValueAtTime(0.08, now);
        gain.gain.linearRampToValueAtTime(0.001, now + 2.5);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(now);
        osc.stop(now + 2.6);
      });
      return;
    }

    if (type === 'click') {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(800, now);
      osc.frequency.exponentialRampToValueAtTime(400, now + 0.05);
      gain.gain.setValueAtTime(0.15, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.05);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(now);
      osc.stop(now + 0.06);
      return;
    }

    if (type === 'tibetan') {
      // Warm resonant Tibetan bowl: Fundamental ~180Hz + overtones at 360, 540, 720
      const freqs = [180, 362, 544, 728];
      freqs.forEach((freq, idx) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(freq, now);

        const initialGain = 0.22 / (idx + 1);
        gain.gain.setValueAtTime(0, now);
        gain.gain.linearRampToValueAtTime(initialGain, now + 0.35);
        gain.gain.exponentialRampToValueAtTime(0.0001, now + 3.8);

        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(now);
        osc.stop(now + 4.0);
      });
      return;
    }

    if (type === 'nordic') {
      // Crystalline glass chime
      const freqs = [880, 1320, 1760];
      freqs.forEach((f, i) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(f, now);
        gain.gain.setValueAtTime(0.15 / (i + 1), now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 2.0);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(now);
        osc.stop(now + 2.2);
      });
      return;
    }

    if (type === 'solfeggio') {
      // 528Hz DNA repair / peaceful harmony
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(528, now);
      gain.gain.setValueAtTime(0, now);
      gain.gain.linearRampToValueAtTime(0.2, now + 0.2);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 2.8);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(now);
      osc.stop(now + 3.0);
      return;
    }

    if (type === 'water') {
      // Gentle water droplet bubble sound
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(450, now);
      osc.frequency.exponentialRampToValueAtTime(950, now + 0.12);
      gain.gain.setValueAtTime(0.2, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.14);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(now);
      osc.stop(now + 0.15);
      return;
    }
  }

  // Real microphone recording with MediaRecorder
  async startRecording(onFrequencyData?: (data: Uint8Array) => void): Promise<boolean> {
    try {
      this.micStream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.recordedChunks = [];
      
      const ctx = this.getContext();
      this.sourceNode = ctx.createMediaStreamSource(this.micStream);
      this.analyser = ctx.createAnalyser();
      this.analyser.fftSize = 64;
      this.sourceNode.connect(this.analyser);

      if (onFrequencyData) {
        const bufferLength = this.analyser.frequencyBinCount;
        const dataArray = new Uint8Array(bufferLength);
        const poll = () => {
          if (this.analyser && this.mediaRecorder && this.mediaRecorder.state === 'recording') {
            this.analyser.getByteFrequencyData(dataArray);
            onFrequencyData(dataArray);
            requestAnimationFrame(poll);
          }
        };
        requestAnimationFrame(poll);
      }

      this.mediaRecorder = new MediaRecorder(this.micStream);
      this.mediaRecorder.ondataavailable = (e) => {
        if (e.data.size > 0) {
          this.recordedChunks.push(e.data);
        }
      };

      this.mediaRecorder.start(100);
      return true;
    } catch {
      // Fallback: simulation if mic permission is denied
      return false;
    }
  }

  stopRecording(): Promise<Blob | null> {
    return new Promise((resolve) => {
      if (!this.mediaRecorder || this.mediaRecorder.state === 'inactive') {
        this.cleanupMic();
        resolve(null);
        return;
      }

      this.mediaRecorder.onstop = () => {
        const blob = new Blob(this.recordedChunks, { type: 'audio/webm' });
        this.cleanupMic();
        resolve(blob);
      };

      this.mediaRecorder.stop();
    });
  }

  private cleanupMic() {
    if (this.micStream) {
      this.micStream.getTracks().forEach((track) => track.stop());
      this.micStream = null;
    }
    if (this.sourceNode) {
      this.sourceNode.disconnect();
      this.sourceNode = null;
    }
    this.analyser = null;
  }

  playBlob(blob: Blob, onEnded?: () => void): HTMLAudioElement {
    if (this.currentAudioElement) {
      this.currentAudioElement.pause();
    }
    const url = URL.createObjectURL(blob);
    const audio = new Audio(url);
    this.currentAudioElement = audio;
    audio.onended = () => {
      URL.revokeObjectURL(url);
      if (onEnded) onEnded();
    };
    audio.play().catch(() => {});
    return audio;
  }

  stopCurrentPlayback() {
    if (this.currentAudioElement) {
      this.currentAudioElement.pause();
      this.currentAudioElement = null;
    }
  }
}

export const audioService = new AudioService();
