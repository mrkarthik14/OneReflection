import React, { useState } from 'react';
import { audioService } from '../services/audioService';
import { storageService } from '../services/storageService';
import { ScreenId } from '../types';

interface ProfileViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export const ProfileView: React.FC<ProfileViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const profile = storageService.getProfile();
  const [eqPreset, setEqPreset] = useState<'studio' | 'vintage' | 'whisper'>('studio');
  const [fadeEnabled, setFadeEnabled] = useState(profile.alarmGradualFade);
  const [morningNotesEnabled, setMorningNotesEnabled] = useState(true);
  const [habitsVisibility, setHabitsVisibility] = useState(true);
  const [livePresence, setLivePresence] = useState(true);
  const [noPressureNudges, setNoPressureNudges] = useState(true);
  const [softHaptics, setSoftHaptics] = useState(true);
  const [cacheCleared, setCacheCleared] = useState(false);

  const handleClearCache = () => {
    audioService.playChime('click');
    setCacheCleared(true);
    setTimeout(() => setCacheCleared(false), 2500);
  };

  return (
    <div className={`flex flex-col w-full px-4 pb-12 gap-y-5 transition-colors ${
      oledMode ? 'text-white' : 'text-[#1C1B1B]'
    }`}>
      {/* 1. Top Sticker & Header */}
      <section className="flex flex-col gap-1.5 pt-2">
        <div className="inline-flex self-start items-center gap-1.5 px-3 py-1 bg-neutral-200 dark:bg-neutral-800 rounded-full rotate-[-1deg] shadow-xs">
          <span className="text-[10px] font-bold uppercase tracking-wider text-neutral-600 dark:text-neutral-300">
            ✨ YOUR SPACE • PREFERENCES & IDENTITY
          </span>
        </div>
        <h1 className="font-headline text-3xl font-bold tracking-tight mt-0.5">
          Profile & Harmony.
        </h1>
        <p className="text-xs text-neutral-500 leading-relaxed max-w-sm">
          Manage your voice persona, partner sync mesh, bedtime routines, and privacy boundaries.
        </p>
      </section>

      {/* 2. Hero Profile Bento Card (Candy Pink) */}
      <section className="bg-[#F5ACD7] text-[#1C1B1B] p-5 rounded-3xl relative overflow-hidden flex flex-col gap-3.5 border border-black/10 shadow-xs">
        <div className="flex items-start justify-between">
          <div className="relative">
            <div className="w-20 h-20 rounded-full overflow-hidden ring-4 ring-white shadow-xs">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuBE7jpIocCISc-CQYfgAnahVZmLt7VB9UJKlTFKJX9JEBM0KeqWHrnY0EOlX7Xr016w-StCO-DfHViu0fGfhqLc5xXymxW-Idb1KIskGYbnQtfZt9R24CzmTiiG97t1oqT6BRybyaDZrINztAvlimWlhHUhwdR_IyfwE_j6uA1V6l9K1-IiXMA6jRa5-JRXxNhlmHpahWf3zorVKk7seurAn3RnMPBzb_UQpUlVYYBvZMwd1oKmMCq0"
                alt="Elena Vance"
                className="w-full h-full object-cover"
              />
            </div>
            <button
              onClick={() => audioService.playChime('click')}
              aria-label="Edit avatar"
              className="absolute -bottom-1 -right-1 w-7 h-7 bg-[#1C1B1B] text-white rounded-full flex items-center justify-center shadow-xs active:scale-90 transition-transform"
            >
              <span className="material-symbols-outlined text-[15px]">edit</span>
            </button>
          </div>

          <div className="inline-flex items-center gap-1 px-3 py-1.5 bg-white text-[#1C1B1B] rounded-full rotate-2 shadow-xs font-bold text-[10px]">
            <span>Awake & Flowing ☀️</span>
          </div>
        </div>

        <div className="flex flex-col">
          <div className="flex items-center gap-1.5">
            <span className="font-headline text-2xl font-bold tracking-tight">Elena Vance</span>
            <span className="material-symbols-outlined text-[18px] text-[#864B70]" style={{ fontVariationSettings: "'FILL' 1" }}>
              verified
            </span>
          </div>
          <span className="text-xs font-semibold text-[#753C61]">@elenavance</span>
          <span className="text-xs mt-1 text-[#753C61] font-medium opacity-90">
            OneReflection Member since Nov 2023 • Deep Sync Active
          </span>
        </div>

        {/* Paired Partner Mesh Tile */}
        <div className="bg-white/95 rounded-2xl p-3 flex flex-col gap-1.5 text-[#1C1B1B] shadow-xs border border-black/5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#6F5D00] opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-[#6F5D00]"></span>
              </span>
              <div className="flex flex-col min-w-0">
                <span className="text-xs font-bold truncate">Paired with Maya R. (@mayar)</span>
                <span className="text-[10px] text-neutral-500">Live Heartbeat & Morning Mesh Active</span>
              </div>
            </div>
            <span className="px-2 py-0.5 bg-[#DAE2FF] text-[#091A3E] text-[10px] font-bold rounded-full">
              Mutual Consent 🛡️
            </span>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="grid grid-cols-2 gap-2 pt-1">
          <button
            onClick={() => audioService.playChime('click')}
            className="h-11 rounded-full bg-[#1C1B1B] text-white text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs active:scale-95 transition-transform"
          >
            <span className="material-symbols-outlined text-[16px]">person</span>
            <span>Edit Profile</span>
          </button>
          <button
            onClick={() => onNavigate('people')}
            className="h-11 rounded-full bg-white text-[#1C1B1B] text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs active:scale-95 transition-transform"
          >
            <span className="material-symbols-outlined text-[16px]">qr_code_2</span>
            <span>Identity Key</span>
          </button>
        </div>
      </section>

      {/* 3. Voice Persona & Mic Quality Bento (Butter Yellow) */}
      <section className="bg-[#F9DF72] text-[#1C1B1B] p-5 rounded-3xl flex flex-col gap-3.5 border border-black/10 shadow-xs">
        <div className="flex items-start justify-between">
          <div className="flex flex-col">
            <span className="text-[10px] uppercase font-bold tracking-wider text-[#736200]">
              Acoustic Identity
            </span>
            <h2 className="font-headline text-lg font-bold tracking-tight">
              Voice Persona & Mic Quality
            </h2>
          </div>
          <div className="w-9 h-9 rounded-full bg-white/90 text-[#736200] flex items-center justify-center shadow-xs">
            <span className="material-symbols-outlined text-[18px]">graphic_eq</span>
          </div>
        </div>

        {/* EQ Presets */}
        <div className="flex flex-col gap-1.5">
          <span className="text-xs font-bold text-[#736200]">EQ Profile Presets</span>
          <div className="flex flex-wrap gap-2">
            {[
              { id: 'studio', label: 'Crisp Studio (Active)' },
              { id: 'vintage', label: 'Warm Vintage Mic' },
              { id: 'whisper', label: 'Soft Whisper EQ' },
            ].map((p) => (
              <button
                key={p.id}
                onClick={() => {
                  audioService.playChime('click');
                  setEqPreset(p.id as typeof eqPreset);
                }}
                className={`px-3 py-1.5 rounded-full text-xs font-bold transition-all ${
                  eqPreset === p.id
                    ? 'bg-[#1C1B1B] text-white shadow-xs'
                    : 'bg-white text-[#1C1B1B] hover:bg-neutral-50'
                }`}
              >
                {p.label}
              </button>
            ))}
          </div>
        </div>

        {/* Audio Delivery Toggle */}
        <div className="bg-white/95 rounded-2xl p-3 flex items-center justify-between text-[#1C1B1B] shadow-xs">
          <div className="flex flex-col pr-2">
            <span className="text-xs font-bold">Gentle Fade-in on Alarms</span>
            <span className="text-[10px] text-neutral-500">Gradual volume swells over 4 minutes</span>
          </div>

          <button
            onClick={() => setFadeEnabled(!fadeEnabled)}
            className={`w-11 h-6 rounded-full p-0.5 flex items-center transition-colors shrink-0 ${
              fadeEnabled ? 'bg-[#1C1B1B]' : 'bg-neutral-300'
            }`}
          >
            <div
              className={`w-5 h-5 rounded-full bg-[#F9DF72] shadow-xs transition-transform ${
                fadeEnabled ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* Fidelity & Test Ping */}
        <div className="flex items-center justify-between gap-2 pt-1">
          <div className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-white/80 rounded-full text-xs font-bold text-[#736200]">
            <span className="material-symbols-outlined text-[15px]">mic</span>
            <span>48kHz Lossless FLAC</span>
          </div>

          <button
            onClick={() => audioService.playChime('tibetan')}
            className="h-9 px-4 rounded-full bg-[#1C1B1B] text-white text-xs font-bold flex items-center gap-1.5 active:scale-95 transition-transform"
          >
            <span className="material-symbols-outlined text-[16px]">volume_up</span>
            <span>Test Voice Ping</span>
          </button>
        </div>
      </section>

      {/* 4. Partner & Circle Synchronization (Lavender Blue) */}
      <section className="bg-[#DAE2FF] text-[#091A3E] p-5 rounded-3xl flex flex-col gap-3.5 border border-[#4F5D85]/20 shadow-xs">
        <div className="flex items-start justify-between">
          <div className="flex flex-col">
            <span className="text-[10px] uppercase font-bold tracking-wider text-[#38466C]">
              Resonance Network
            </span>
            <h2 className="font-headline text-lg font-bold tracking-tight">
              Sync & Sharing Mesh
            </h2>
          </div>
          <div className="w-9 h-9 rounded-full bg-white/90 text-[#4F5D85] flex items-center justify-center shadow-xs">
            <span className="material-symbols-outlined text-[18px]">hub</span>
          </div>
        </div>

        <div className="flex flex-col gap-2">
          {/* Maya */}
          <div className="bg-white/95 rounded-2xl p-3 flex items-center justify-between text-[#1C1B1B] shadow-xs">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-full bg-[#FFD8EC] text-[#864B70] flex items-center justify-center font-bold text-xs">
                M
              </div>
              <div className="flex flex-col">
                <span className="text-xs font-bold">Maya R.</span>
                <span className="text-[10px] text-neutral-500">Full Voice, Routines & Wake Sync</span>
              </div>
            </div>
            <span className="px-2 py-0.5 bg-[#F9DF72] text-[#736200] rounded-full text-[10px] font-bold">
              Primary
            </span>
          </div>

          {/* Leo */}
          <div className="bg-white/95 rounded-2xl p-3 flex items-center justify-between text-[#1C1B1B] shadow-xs">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-full bg-[#DAE2FF] text-[#091A3E] flex items-center justify-center font-bold text-xs">
                L
              </div>
              <div className="flex flex-col">
                <span className="text-xs font-bold">Leo Chen</span>
                <span className="text-[10px] text-neutral-500">Sprint & Tasks mesh only</span>
              </div>
            </div>
            <span className="px-2 py-0.5 bg-neutral-200 text-neutral-700 rounded-full text-[10px] font-bold">
              Partner
            </span>
          </div>
        </div>

        {/* Sync Rule Toggles */}
        <div className="flex flex-col gap-2 pt-1 text-[#1C1B1B]">
          <div className="bg-white/80 rounded-2xl p-3 flex items-center justify-between">
            <span className="text-xs font-bold">Morning Wake-up Voice Notes</span>
            <button
              onClick={() => setMorningNotesEnabled(!morningNotesEnabled)}
              className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                morningNotesEnabled ? 'bg-[#4F5D85]' : 'bg-neutral-300'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-white shadow-xs transition-transform ${
                  morningNotesEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="bg-white/80 rounded-2xl p-3 flex items-center justify-between">
            <span className="text-xs font-bold">Shared Habit Streak Visibility</span>
            <button
              onClick={() => setHabitsVisibility(!habitsVisibility)}
              className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                habitsVisibility ? 'bg-[#4F5D85]' : 'bg-neutral-300'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-white shadow-xs transition-transform ${
                  habitsVisibility ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="bg-white/80 rounded-2xl p-3 flex items-center justify-between">
            <div className="flex flex-col">
              <span className="text-xs font-bold">Quiet Hours Sync</span>
              <span className="text-[10px] text-neutral-500">Bedtime silence locked</span>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-[#DAE2FF] text-[#091A3E] text-xs font-bold">
              22:30 - 07:00
            </span>
          </div>
        </div>
      </section>

      {/* 5. Mindful Notifications */}
      <section className={`p-5 rounded-3xl flex flex-col gap-3 border transition-colors ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
      }`}>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="material-symbols-outlined text-[18px]">spa</span>
            <h2 className="font-headline text-base font-bold">Mindful Notifications</h2>
          </div>
          <span className="text-[10px] font-bold px-2.5 py-0.5 rounded-full bg-neutral-200 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300">
            Calm Mode
          </span>
        </div>

        <div className="flex flex-col gap-2">
          <div className="bg-white dark:bg-neutral-950 rounded-2xl p-3 flex items-center justify-between shadow-xs">
            <div className="flex flex-col pr-2">
              <span className="text-xs font-bold">No-Pressure Daily Nudges</span>
              <span className="text-[10px] text-neutral-500">Polite check-ins that never stack</span>
            </div>
            <button
              onClick={() => setNoPressureNudges(!noPressureNudges)}
              className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                noPressureNudges ? 'bg-[#864B70]' : 'bg-neutral-300 dark:bg-neutral-700'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-white shadow-xs transition-transform ${
                  noPressureNudges ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="bg-white dark:bg-neutral-950 rounded-2xl p-3 flex items-center justify-between shadow-xs">
            <div className="flex flex-col pr-2">
              <span className="text-xs font-bold">Soft Haptic Vibration Rhythms</span>
              <span className="text-[10px] text-neutral-500">Gentle organic motor waveforms</span>
            </div>
            <button
              onClick={() => setSoftHaptics(!softHaptics)}
              className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                softHaptics ? 'bg-[#864B70]' : 'bg-neutral-300 dark:bg-neutral-700'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-white shadow-xs transition-transform ${
                  softHaptics ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>
      </section>

      {/* 6. Privacy & Data Vault */}
      <section className={`p-5 rounded-3xl flex flex-col gap-3 border transition-colors ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
      }`}>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="material-symbols-outlined text-[18px]">lock</span>
            <h2 className="font-headline text-base font-bold">Privacy & Data Vault</h2>
          </div>
          <span className="material-symbols-outlined text-[#864B70] dark:text-[#F5ACD7] text-[18px]" style={{ fontVariationSettings: "'FILL' 1" }}>
            shield
          </span>
        </div>

        <div className="flex flex-col gap-2">
          <div className="bg-white dark:bg-neutral-950 rounded-2xl p-3 flex items-center justify-between shadow-xs">
            <div className="flex items-center gap-2">
              <span className="material-symbols-outlined text-[#6F5D00] dark:text-[#F9DF72] text-[18px]">
                check_circle
              </span>
              <div className="flex flex-col">
                <span className="text-xs font-bold">End-to-End Bubble Encryption</span>
                <span className="text-[10px] text-neutral-500">Only sync keys stored on-device</span>
              </div>
            </div>
            <span className="text-[10px] text-neutral-400 font-bold">Always On</span>
          </div>

          <div className="bg-white dark:bg-neutral-950 rounded-2xl p-3 flex items-center justify-between shadow-xs">
            <div className="flex flex-col">
              <span className="text-xs font-bold">Offline Audio Cache</span>
              <span className="text-[10px] text-neutral-500">
                {cacheCleared ? '0 MB (Cleaned)' : '248 MB stored locally'}
              </span>
            </div>
            <button
              onClick={handleClearCache}
              className="px-3.5 py-1.5 rounded-full bg-neutral-200 dark:bg-neutral-800 text-xs font-bold hover:bg-[#FFDAD6] hover:text-[#93000A] transition-colors"
            >
              {cacheCleared ? 'Cleared ✓' : 'Clear Cache'}
            </button>
          </div>
        </div>
      </section>

      {/* 7. Account Actions & Footnote */}
      <section className="flex flex-col items-center gap-3 pt-1">
        <div className="grid grid-cols-2 gap-2 w-full">
          <button
            onClick={() => audioService.playChime('click')}
            className={`h-12 rounded-full text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs border ${
              oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-[#EAE7E7] border-black/[0.04] text-[#1C1B1B]'
            }`}
          >
            <span className="material-symbols-outlined text-[16px]">switch_account</span>
            <span>Switch Account</span>
          </button>

          <button
            onClick={() => audioService.playChime('click')}
            className="h-12 rounded-full bg-[#FFDAD6] text-[#93000A] text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs"
          >
            <span className="material-symbols-outlined text-[16px]">logout</span>
            <span>Sign Out</span>
          </button>
        </div>

        <div className="flex flex-col items-center text-center gap-1 pt-2">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 bg-neutral-200 dark:bg-neutral-900 rounded-full text-neutral-500 text-[10px]">
            <span className="material-symbols-outlined text-[13px]">favorite</span>
            <span>Built for mindful human connection</span>
          </div>
          <span className="text-[10px] text-neutral-400">OneReflection v2.4.1 (Build 490)</span>
        </div>
      </section>
    </div>
  );
};
