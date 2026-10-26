import React, { useState, useEffect } from 'react';
import { WaveformVisualizer } from '../components/WaveformVisualizer';
import { audioService } from '../services/audioService';

interface VoiceStudioSheetProps {
  isOpen: boolean;
  onClose: () => void;
  onSaveVoiceNote: (quote: string, duration: string) => void;
  oledMode: boolean;
}

export const VoiceStudioSheet: React.FC<VoiceStudioSheetProps> = ({
  isOpen,
  onClose,
  onSaveVoiceNote,
  oledMode,
}) => {
  const [selectedMode, setSelectedMode] = useState<'alarm' | 'reminder' | 'habit' | 'chat'>('alarm');
  const [isRecording, setIsRecording] = useState(true);
  const [seconds, setSeconds] = useState(31);
  const [vibeFilter, setVibeFilter] = useState<'natural' | 'radio' | 'echo'>('natural');
  const [encryptedBubble, setEncryptedBubble] = useState(true);

  // Timer counter simulation during recording
  useEffect(() => {
    if (!isOpen || !isRecording) return;
    const interval = setInterval(() => {
      setSeconds((prev) => (prev >= 90 ? 31 : prev + 1));
    }, 1000);
    return () => clearInterval(interval);
  }, [isOpen, isRecording]);

  if (!isOpen) return null;

  const formatTime = (totalSeconds: number) => {
    const mins = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
    const secs = String(totalSeconds % 60).padStart(2, '0');
    return `${mins}:${secs}`;
  };

  const handleToggleRecord = async () => {
    audioService.playChime('click');
    if (isRecording) {
      await audioService.stopRecording();
      setIsRecording(false);
    } else {
      await audioService.startRecording();
      setIsRecording(true);
    }
  };

  const handleRestart = () => {
    audioService.playChime('click');
    setSeconds(0);
    setIsRecording(true);
  };

  const handleAttach = () => {
    audioService.playChime('tibetan');
    onSaveVoiceNote('Rise and shine sunny! Remember to drink your lemon water first thing.', formatTime(seconds));
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex flex-col justify-end">
      {/* Dimmed backdrop */}
      <div 
        onClick={onClose}
        className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
      />

      {/* Slide-Up Sheet Container */}
      <div className={`relative z-10 w-full max-w-lg mx-auto rounded-t-[32px] shadow-2xl flex flex-col max-h-[92vh] overflow-y-auto no-scrollbar border-t pb-safe transition-colors ${
        oledMode ? 'bg-[#0A0A0A] border-neutral-800 text-white' : 'bg-white border-black/10 text-[#1C1B1B]'
      }`}>
        {/* Drag Handle Indicator */}
        <div className="w-full flex justify-center py-2.5">
          <div className="w-12 h-1.5 rounded-full bg-neutral-300 dark:bg-neutral-700" />
        </div>

        {/* Header Section */}
        <div className="px-5 pb-2 flex flex-col gap-1.5">
          <div className="flex items-center justify-between">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#DAE2FF] text-[#091A3E] font-bold text-[10px] shadow-xs rotate-[-1.5deg]">
              <span className="material-symbols-outlined text-[14px]">mic</span>
              <span>VOICE STUDIO • DISPATCH</span>
            </div>

            <button
              onClick={onClose}
              aria-label="Dismiss Voice Studio"
              className="w-8 h-8 rounded-full bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200 transition-colors"
            >
              <span className="material-symbols-outlined text-[18px]">close</span>
            </button>
          </div>

          <div className="mt-1">
            <h2 className="font-headline text-xl font-bold tracking-tight">
              Record a gentle voice note.
            </h2>
            <p className="text-xs text-neutral-500 mt-0.5">
              Attach your warmth to an alarm, daily habit, or shared to-do.
            </p>
          </div>
        </div>

        {/* Mode Selector Pills */}
        <div className="px-5 py-2 flex items-center gap-2 overflow-x-auto no-scrollbar">
          {[
            { id: 'alarm', label: 'Morning Alarm', icon: 'alarm_on', activeColor: 'bg-[#F9DF72] text-[#736200]' },
            { id: 'reminder', label: 'Reminder Ping', icon: 'notifications', activeColor: 'bg-[#FFD8EC] text-[#864B70]' },
            { id: 'habit', label: 'Daily Habit', icon: 'task_alt', activeColor: 'bg-[#DAE2FF] text-[#091A3E]' },
            { id: 'chat', label: 'Circle Chat', icon: 'forum', activeColor: 'bg-neutral-200 text-neutral-900' },
          ].map((mode) => {
            const isActive = selectedMode === mode.id;
            return (
              <button
                key={mode.id}
                onClick={() => {
                  audioService.playChime('click');
                  setSelectedMode(mode.id as typeof selectedMode);
                }}
                className={`flex items-center gap-1.5 px-3.5 py-2 rounded-full text-xs font-bold shrink-0 transition-transform active:scale-95 shadow-xs ${
                  isActive
                    ? mode.activeColor
                    : 'bg-neutral-100 dark:bg-neutral-900 text-neutral-600 dark:text-neutral-300'
                }`}
              >
                <span className="material-symbols-outlined text-[16px]">{mode.icon}</span>
                <span>{mode.label}</span>
              </button>
            );
          })}
        </div>

        {/* Main Content Container with Bento Layout */}
        <div className="px-5 flex flex-col gap-4 mt-2 pb-6">
          {/* Destination Assignment Bento Card */}
          <div className="bg-[#F5ACD7] text-[#1C1B1B] p-4 rounded-3xl flex flex-col gap-2.5 border border-black/10 shadow-xs">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-1.5">
                <span className="material-symbols-outlined text-[16px] text-[#753C61]">outgoing_mail</span>
                <span className="text-[10px] font-bold text-[#753C61] uppercase tracking-wide">
                  Destination & Schedule
                </span>
              </div>
              <span className="px-2.5 py-0.5 rounded-full bg-white/90 text-xs font-bold shadow-xs">
                Tomorrow • 07:30 AM
              </span>
            </div>

            <div className="bg-white/95 p-3 rounded-2xl flex items-center justify-between border border-black/5 shadow-xs">
              <div className="flex items-center gap-2.5">
                <div className="relative">
                  <img
                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                    alt="Maya"
                    className="w-11 h-11 rounded-full object-cover ring-2 ring-white"
                  />
                  <div className="absolute -bottom-0.5 -right-0.5 w-4 h-4 rounded-full bg-[#F9DF72] flex items-center justify-center">
                    <span className="material-symbols-outlined text-[10px] text-[#736200] font-bold">
                      favorite
                    </span>
                  </div>
                </div>

                <div className="flex flex-col min-w-0">
                  <span className="font-headline font-bold text-xs truncate">Maya (Partner)</span>
                  <span className="text-[10px] text-neutral-500">Alarm: "Rise gently with tea"</span>
                </div>
              </div>

              <div className="flex items-center gap-1 px-2.5 py-1 rounded-full bg-neutral-100 text-[#1C1B1B]">
                <span className="material-symbols-outlined text-[15px] text-[#864B70]">sensors</span>
                <span className="text-[10px] font-bold">Wake-up Sync</span>
              </div>
            </div>
          </div>

          {/* Expressive Waveform & Timer Bento Card */}
          <div className={`p-4 rounded-3xl flex flex-col items-center gap-3 border shadow-xs ${
            oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
          }`}>
            <div className="w-full flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span className="relative flex h-2.5 w-2.5">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-500 opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-red-500"></span>
                </span>
                <span className="text-xs font-bold">Recording Live</span>
                <span className="text-[10px] text-neutral-500">• 48kHz HD</span>
              </div>

              <div className="flex items-center gap-1 px-2 py-0.5 rounded-full bg-white dark:bg-neutral-800 text-[10px] font-bold shadow-xs">
                <span className="material-symbols-outlined text-[13px]">graphic_eq</span>
                <span>Crisp Voice EQ</span>
              </div>
            </div>

            {/* Big Space Grotesk Time Counter */}
            <div className="flex items-baseline gap-1 my-0.5">
              <span className="font-headline text-4xl font-bold tracking-tight">
                {formatTime(seconds)}
              </span>
              <span className="text-base text-neutral-400 font-mono tracking-tight">
                / 01:30
              </span>
            </div>

            {/* Dynamic Waveform Visualizer */}
            <div className="w-full h-16 bg-white dark:bg-neutral-950 rounded-2xl px-3 flex items-center justify-center shadow-xs border border-black/5 dark:border-neutral-800">
              <WaveformVisualizer isPlaying={isRecording} barCount={24} heightClass="h-12" color="mixed" />
            </div>

            {/* Audio Vibe Presets Strip */}
            <div className="w-full flex items-center justify-between pt-1">
              <span className="text-[10px] font-bold uppercase tracking-wider text-neutral-500">
                Voice Vibe Filter
              </span>
              <div className="flex items-center gap-1.5">
                <button
                  onClick={() => setVibeFilter('natural')}
                  className={`px-3 py-1 rounded-full text-xs font-bold transition-all ${
                    vibeFilter === 'natural'
                      ? 'bg-[#1C1B1B] text-white shadow-xs'
                      : 'bg-white dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300'
                  }`}
                >
                  ✨ Natural
                </button>
                <button
                  onClick={() => setVibeFilter('radio')}
                  className={`px-3 py-1 rounded-full text-xs font-bold transition-all ${
                    vibeFilter === 'radio'
                      ? 'bg-[#1C1B1B] text-white shadow-xs'
                      : 'bg-white dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300'
                  }`}
                >
                  📻 Warm Radio
                </button>
                <button
                  onClick={() => setVibeFilter('echo')}
                  className={`px-3 py-1 rounded-full text-xs font-bold transition-all ${
                    vibeFilter === 'echo'
                      ? 'bg-[#1C1B1B] text-white shadow-xs'
                      : 'bg-white dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300'
                  }`}
                >
                  🎐 Gentle Echo
                </button>
              </div>
            </div>
          </div>

          {/* Tactile Recording Controls Row */}
          <div className="flex items-center justify-center gap-6 py-1">
            {/* Reset / Restart */}
            <div className="flex flex-col items-center gap-1">
              <button
                onClick={handleRestart}
                aria-label="Discard and restart recording"
                className="w-12 h-12 rounded-full bg-neutral-200 dark:bg-neutral-800 hover:bg-neutral-300 dark:hover:bg-neutral-700 flex items-center justify-center text-neutral-700 dark:text-neutral-200 shadow-xs active:scale-95 transition-transform"
              >
                <span className="material-symbols-outlined text-[20px]">delete</span>
              </button>
              <span className="text-[10px] text-neutral-500 font-medium">Restart</span>
            </div>

            {/* Central Microphone Button */}
            <div className="relative flex items-center justify-center">
              <div className="absolute w-20 h-20 rounded-full bg-[#F5ACD7]/40 animate-ping pointer-events-none"></div>
              <div className="absolute w-18 h-18 rounded-full bg-[#F5ACD7]/60 pointer-events-none"></div>

              <button
                onClick={handleToggleRecord}
                aria-label="Toggle Recording"
                className={`relative z-10 w-16 h-16 rounded-full flex items-center justify-center shadow-lg active:scale-90 transition-transform cursor-pointer ${
                  isRecording ? 'bg-[#1C1B1B] text-[#F5ACD7]' : 'bg-[#864B70] text-white'
                }`}
              >
                <span className="material-symbols-outlined text-[30px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                  {isRecording ? 'pause' : 'mic'}
                </span>
              </button>
            </div>

            {/* Preview / Pause Button */}
            <div className="flex flex-col items-center gap-1">
              <button
                onClick={() => setIsRecording(!isRecording)}
                aria-label="Pause recording"
                className="w-12 h-12 rounded-full bg-[#F9DF72] text-[#736200] hover:bg-[#FCE275] flex items-center justify-center shadow-xs active:scale-95 transition-transform"
              >
                <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                  {isRecording ? 'pause' : 'play_arrow'}
                </span>
              </button>
              <span className="text-[10px] text-neutral-500 font-medium">
                {isRecording ? 'Pause' : 'Resume'}
              </span>
            </div>
          </div>

          {/* Consent & Encrypted Bubble Toggle */}
          <div className="p-3.5 rounded-2xl bg-neutral-100 dark:bg-neutral-900 flex items-center justify-between gap-3 border border-black/5 dark:border-neutral-800">
            <div className="flex items-center gap-2.5 min-w-0">
              <div className="w-8 h-8 rounded-full bg-[#DAE2FF] flex items-center justify-center text-[#091A3E] shrink-0">
                <span className="material-symbols-outlined text-[16px]">lock</span>
              </div>
              <div className="flex flex-col min-w-0">
                <span className="text-xs font-bold truncate">End-to-End Encrypted Bubble</span>
                <span className="text-[10px] text-neutral-500 leading-tight">
                  Maya will receive this gently at wake-up time.
                </span>
              </div>
            </div>

            <button
              onClick={() => setEncryptedBubble(!encryptedBubble)}
              className={`w-11 h-6 rounded-full p-0.5 flex items-center transition-colors shrink-0 ${
                encryptedBubble ? 'bg-[#1C1B1B]' : 'bg-neutral-300 dark:bg-neutral-700'
              }`}
            >
              <div
                className={`w-5 h-5 rounded-full bg-[#F9DF72] shadow-xs transition-transform ${
                  encryptedBubble ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          {/* Action Buttons Footer */}
          <div className="flex flex-col gap-2 pt-1">
            <button
              onClick={handleAttach}
              className="w-full h-14 rounded-full bg-[#1C1B1B] text-white font-headline text-sm font-bold flex items-center justify-center gap-2 shadow-lg active:scale-98 transition-all hover:bg-neutral-800"
            >
              <span className="material-symbols-outlined text-[20px] text-[#F5ACD7]">
                check_circle
              </span>
              <span>Attach to Morning Alarm</span>
            </button>

            <button
              onClick={onClose}
              className="w-full h-11 rounded-full bg-neutral-200 dark:bg-neutral-800 hover:bg-neutral-300 dark:hover:bg-neutral-700 text-xs font-bold transition-colors"
            >
              Save to Voice Drafts
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
