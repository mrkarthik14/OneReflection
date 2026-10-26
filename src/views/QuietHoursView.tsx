import React, { useState } from 'react';
import { WaveformVisualizer } from '../components/WaveformVisualizer';
import { audioService } from '../services/audioService';
import { ScreenId } from '../types';

interface QuietHoursViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
  onToggleOLED: () => void;
}

export const QuietHoursView: React.FC<QuietHoursViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
  onToggleOLED,
}) => {
  const [isPlayingWhisper, setIsPlayingWhisper] = useState(true);
  const [speakerMode, setSpeakerMode] = useState<'bedside' | 'earpiece'>('bedside');
  const [radiatorOff, setRadiatorOff] = useState(false);
  const [silenceRestOfNight, setSilenceRestOfNight] = useState(false);
  const [isWhisperingBack, setIsWhisperingBack] = useState(false);
  const [sleepingAckSent, setSleepingAckSent] = useState(false);

  const handleTogglePlayback = () => {
    if (!isPlayingWhisper) {
      audioService.playChime('tibetan');
      setIsPlayingWhisper(true);
    } else {
      audioService.stopCurrentPlayback();
      setIsPlayingWhisper(false);
    }
  };

  const handleSpeakerToggle = () => {
    audioService.playChime('click');
    setSpeakerMode((prev) => (prev === 'bedside' ? 'earpiece' : 'bedside'));
  };

  const handleRadiatorToggle = () => {
    audioService.playChime('click');
    setRadiatorOff(!radiatorOff);
  };

  const handleSilenceToggle = () => {
    audioService.playChime('click');
    setSilenceRestOfNight(!silenceRestOfNight);
  };

  const handleOkSleeping = () => {
    audioService.playChime('solfeggio');
    setSleepingAckSent(true);
    setTimeout(() => setSleepingAckSent(false), 3000);
  };

  return (
    <div className={`flex flex-col w-full px-4 pb-12 gap-y-4 pt-1 transition-colors ${
      oledMode ? 'text-white' : 'text-[#1C1B1B]'
    }`}>
      {/* 1. Ambient Night Status Pill Bar */}
      <div className={`flex items-center justify-between backdrop-blur-md rounded-full px-4 py-2 border shadow-xs ${
        oledMode ? 'bg-neutral-900/90 border-neutral-800' : 'bg-[#EAE7E7]/80 border-black/[0.04]'
      }`}>
        <div className="flex items-center gap-2">
          <span className="material-symbols-outlined text-[#F9DF72] text-[18px]" style={{ fontVariationSettings: "'FILL' 1" }}>
            bedtime
          </span>
          <span className="font-headline text-xs font-bold tracking-tight">01:24 AM</span>
          <span className="w-1.5 h-1.5 rounded-full bg-[#6F5D00] dark:bg-[#F9DF72]"></span>
          <span className="text-[11px] text-neutral-500">Quiet Hours Active</span>
        </div>

        <div className="flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-[#F9DF72] text-[#736200] font-bold text-[10px]">
          <span className="relative flex h-2 w-2">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#6F5D00] opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-[#6F5D00]"></span>
          </span>
          <span>AUDIO PASS</span>
        </div>
      </div>

      {/* 2. OLED 0-Nit Night Mode Pill Banner */}
      <div className={`rounded-3xl p-3 border flex items-center justify-between gap-3 shadow-xs transition-colors ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
      }`}>
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-9 h-9 rounded-full bg-[#1C1B1B] text-white flex items-center justify-center shrink-0">
            <span className="material-symbols-outlined text-[18px] text-[#F9DF72]" style={{ fontVariationSettings: "'FILL' 1" }}>
              dark_mode
            </span>
          </div>
          <div className="flex flex-col min-w-0">
            <div className="flex items-center gap-1.5">
              <span className="text-xs font-bold truncate">OLED Pure Black (#000000)</span>
              <span className="text-[9px] px-1.5 py-0.2 rounded-full bg-neutral-200 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 font-bold">
                {oledMode ? 'Active' : 'Ready'}
              </span>
            </div>
            <span className="text-[10px] text-neutral-500 truncate">
              Zero-nit panel sleep • Melatonin & battery safe
            </span>
          </div>
        </div>

        <button
          onClick={onToggleOLED}
          aria-label="Toggle OLED Mode"
          className={`w-12 h-7 rounded-full p-0.5 flex items-center transition-all cursor-pointer ${
            oledMode ? 'bg-[#F9DF72]' : 'bg-neutral-300 dark:bg-neutral-700'
          }`}
        >
          <div
            className={`w-6 h-6 rounded-full flex items-center justify-center text-[9px] font-bold shadow-xs transition-transform ${
              oledMode ? 'translate-x-5 bg-[#1C1B1B] text-[#F9DF72]' : 'translate-x-0 bg-white text-neutral-600'
            }`}
          >
            {oledMode ? 'on' : 'off'}
          </div>
        </button>
      </div>

      {/* 3. Breakthrough Rule Sticky Banner */}
      <div className={`rounded-3xl p-4 flex flex-col gap-1.5 border shadow-xs ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
      }`}>
        <div className="flex items-center justify-between">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#FFD8EC] text-[#37062A] text-[10px] font-bold shadow-xs rotate-[-1deg]">
            <span className="material-symbols-outlined text-[14px]" style={{ fontVariationSettings: "'FILL' 1" }}>
              verified
            </span>
            <span>VERIFIED PARTNER PERMISSION</span>
          </div>

          <span className="text-[11px] font-semibold text-neutral-500 flex items-center gap-1">
            <span className="material-symbols-outlined text-[15px] text-[#4F5D85]">volume_down</span>
            Max 20%
          </span>
        </div>

        <p className="text-xs text-neutral-500 dark:text-neutral-400 leading-relaxed pt-0.5">
          <strong className={`font-semibold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>Maya R.</strong> bypassed quiet mode via your granular{' '}
          <button
            onClick={() => onNavigate('bedtime-sync')}
            className="text-[#864B70] dark:text-[#F5ACD7] font-bold underline decoration-[#F5ACD7] underline-offset-2"
          >
            Gentle Voice Whispers
          </button>{' '}
          bedtime rule.
        </p>
      </div>

      {/* 4. Partner Presence Bento Card */}
      <div className={`rounded-3xl p-4 flex items-center justify-between border shadow-xs ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-white border-black/[0.04]'
      }`}>
        <div className="flex items-center gap-3 min-w-0">
          <div className="relative flex items-center justify-center shrink-0">
            <div className="absolute -inset-1 rounded-full bg-[#F9DF72]/60 animate-pulse blur-[2px]"></div>
            <img
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
              alt="Maya"
              className="w-13 h-13 rounded-full object-cover relative z-10 shadow-xs ring-2 ring-[#F9DF72]"
            />
            <div className="absolute bottom-0 right-0 z-20 w-4 h-4 rounded-full bg-[#F9DF72] flex items-center justify-center text-[10px] text-[#736200] font-bold shadow-xs">
              🎙️
            </div>
          </div>

          <div className="flex flex-col min-w-0">
            <div className="flex items-center gap-1.5">
              <h2 className="font-headline font-bold text-base truncate">Maya R.</h2>
              <span className="px-2 py-0.5 rounded-full bg-[#FFD8EC] text-[#37062A] text-[9px] font-bold">
                Primary
              </span>
            </div>
            <p className="text-xs text-neutral-500 truncate flex items-center gap-1 mt-0.5">
              <span className="material-symbols-outlined text-[13px] text-[#4F5D85]">watch</span>
              Awake in Brooklyn • Oura Synced
            </p>
            <span className="text-[10px] text-[#4F5D85] dark:text-[#DAE2FF] font-semibold mt-0.5">
              Mutual Rest Exception Granted
            </span>
          </div>
        </div>

        <button 
          onClick={handleTogglePlayback}
          className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 transition-all ${
            isPlayingWhisper ? 'bg-[#864B70] text-white' : 'bg-neutral-200 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300'
          }`}
        >
          <span className="material-symbols-outlined text-[20px]">
            {isPlayingWhisper ? 'pause' : 'graphic_eq'}
          </span>
        </button>
      </div>

      {/* 5. Live Incoming Voice Breakthrough Player (Hero Bento) */}
      <div className="rounded-3xl bg-[#DAE2FF]/30 p-5 flex flex-col gap-4 border border-[#4F5D85]/20 shadow-xs relative overflow-hidden">
        <div className="flex flex-col gap-1 relative z-10">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-1.5">
              <span className="material-symbols-outlined text-[#864B70] dark:text-[#F5ACD7] text-[20px] animate-bounce">
                sound_detection_dog_barking
              </span>
              <span className="font-headline font-bold text-base">Maya is whispering...</span>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-white dark:bg-neutral-900 text-xs font-bold shadow-xs flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-[#864B70] animate-ping"></span>
              00:08 / 00:27
            </span>
          </div>
          <p className="text-xs text-neutral-500">
            Tibetan Bowl Swell gently sounded twice prior to playback
          </p>
        </div>

        {/* Dynamic Waveform Track */}
        <div className="w-full bg-white/80 dark:bg-neutral-950/80 backdrop-blur-md rounded-2xl p-3 flex items-center justify-between gap-1 shadow-xs border border-black/5 dark:border-neutral-800">
          <WaveformVisualizer isPlaying={isPlayingWhisper} barCount={20} heightClass="h-8" />
        </div>

        {/* Instant Bedside Transcription Card */}
        <div className="w-full bg-white dark:bg-neutral-900 rounded-2xl p-4 flex flex-col gap-1.5 shadow-xs border border-black/5 dark:border-neutral-800">
          <div className="flex items-center justify-between text-[11px] font-bold text-[#4F5D85] dark:text-[#DAE2FF]">
            <span className="flex items-center gap-1">
              <span className="material-symbols-outlined text-[14px]">auto_awesome</span>
              Instant Bedside Transcription
            </span>
            <span className="text-neutral-500 font-normal">Soft Whisper EQ</span>
          </div>

          <p className="text-xs italic leading-relaxed pt-1">
            “Hey love, sorry to wake you gently — just wanted to make sure you turned the radiator off before deep sleep, it was rattling earlier...”
          </p>
        </div>

        {/* Playback Mode Switch Toggle */}
        <div className="flex items-center justify-between pt-0.5 relative z-10">
          <div className="flex items-center gap-1.5 text-xs text-neutral-500">
            <span className="material-symbols-outlined text-[16px]">tune</span>
            <span>Audio Profile: <strong>Calm & Low Gain</strong></span>
          </div>

          <button
            onClick={handleSpeakerToggle}
            className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-bold shadow-xs active:scale-95 transition-all ${
              speakerMode === 'bedside'
                ? 'bg-white dark:bg-neutral-900 text-[#1C1B1B] dark:text-white'
                : 'bg-[#F9DF72] text-[#736200]'
            }`}
          >
            <span className="material-symbols-outlined text-[15px]">
              {speakerMode === 'bedside' ? 'speaker' : 'headphones'}
            </span>
            <span>{speakerMode === 'bedside' ? 'Bedside Speaker' : 'Earphone Mode'}</span>
          </button>
        </div>
      </div>

      {/* 6. Tactile Response Controls Bento Grid */}
      <div className="flex flex-col gap-2">
        <div className="flex items-center justify-between px-1">
          <span className="text-[10px] font-bold uppercase tracking-wider text-neutral-500">
            Gentle Response Actions
          </span>
          <span className="text-[10px] font-bold text-[#864B70] dark:text-[#F5ACD7]">
            Micro-haptics enabled
          </span>
        </div>

        <div className="grid grid-cols-2 gap-2.5">
          {/* Action 1: Whisper Back */}
          <button
            onMouseDown={() => {
              audioService.playChime('click');
              setIsWhisperingBack(true);
            }}
            onMouseUp={() => {
              audioService.playChime('tibetan');
              setIsWhisperingBack(false);
            }}
            onTouchStart={() => {
              audioService.playChime('click');
              setIsWhisperingBack(true);
            }}
            onTouchEnd={() => {
              audioService.playChime('tibetan');
              setIsWhisperingBack(false);
            }}
            className={`rounded-3xl p-4 flex flex-col items-center justify-center gap-1 text-center shadow-xs transition-all cursor-pointer ${
              isWhisperingBack
                ? 'bg-[#864B70] text-white scale-95 ring-4 ring-[#F5ACD7]'
                : 'bg-[#F5ACD7] text-[#1C1B1B] active:scale-95'
            }`}
          >
            <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center text-[#864B70] mb-0.5 shadow-xs">
              <span className="material-symbols-outlined text-[22px]">mic</span>
            </div>
            <span className="font-headline font-bold text-sm">
              {isWhisperingBack ? 'Recording...' : 'Whisper Back'}
            </span>
            <span className="text-[10px] opacity-80">Hold for 15s note</span>
          </button>

          {/* Action 2: I'm OK Sleeping */}
          <button
            onClick={handleOkSleeping}
            className="rounded-3xl bg-[#F9DF72] text-[#736200] p-4 flex flex-col items-center justify-center gap-1 text-center shadow-xs active:scale-95 transition-all cursor-pointer"
          >
            <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center text-[#736200] mb-0.5 shadow-xs">
              <span className="material-symbols-outlined text-[22px]">bedtime</span>
            </div>
            <span className="font-headline font-bold text-sm">
              {sleepingAckSent ? 'Sent & Muted ✓' : 'I\'m OK • Sleeping'}
            </span>
            <span className="text-[10px] opacity-80">Auto-replies & mutes</span>
          </button>
        </div>

        {/* Secondary Single-Tap Pills */}
        <div className="flex items-center gap-2 pt-1">
          <button
            onClick={handleRadiatorToggle}
            className={`flex-1 rounded-full py-3 px-4 flex items-center justify-center gap-1.5 shadow-xs active:scale-98 transition-all text-xs font-bold border ${
              radiatorOff
                ? 'bg-[#864B70] text-white border-transparent'
                : oledMode
                  ? 'bg-neutral-900 border-neutral-800 text-white'
                  : 'bg-[#EAE7E7] border-black/[0.04] text-[#1C1B1B]'
            }`}
          >
            <span className="material-symbols-outlined text-[16px]">
              {radiatorOff ? 'check_circle' : 'hvac'}
            </span>
            <span>{radiatorOff ? 'Radiator Turned Off ✓' : 'Turn Radiator Off'}</span>
          </button>

          <button
            onClick={handleSilenceToggle}
            className={`flex-1 rounded-full py-3 px-4 flex items-center justify-center gap-1.5 shadow-xs active:scale-98 transition-all text-xs font-bold border ${
              silenceRestOfNight
                ? 'bg-neutral-800 text-neutral-300 border-transparent'
                : oledMode
                  ? 'bg-neutral-900 border-neutral-800 text-neutral-300'
                  : 'bg-[#F6F3F2] border-black/[0.04] text-[#50434A]'
            }`}
          >
            <span className="material-symbols-outlined text-[16px]">do_not_disturb_on</span>
            <span>{silenceRestOfNight ? 'All Audio Silenced' : 'Silence Rest of Night'}</span>
          </button>
        </div>
      </div>

      {/* 7. Sleep Horizon Footer Indicator */}
      <div className={`rounded-full px-4 py-2.5 flex items-center justify-between border shadow-inner ${
        oledMode ? 'bg-neutral-950 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
      }`}>
        <div className="flex items-center gap-2 text-xs">
          <span className="material-symbols-outlined text-[#F9DF72] text-[18px]">wb_twilight</span>
          <span>Wake Horizon in <strong>5h 36m</strong> (07:00 AM)</span>
        </div>
        <div className="flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-[#DAE2FF] text-[#091A3E] text-[10px] font-bold">
          <span className="material-symbols-outlined text-[13px]">dark_mode</span>
          <span>Deep Rest</span>
        </div>
      </div>
    </div>
  );
};
