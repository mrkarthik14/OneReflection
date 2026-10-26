import React, { useState } from 'react';
import { WaveformVisualizer } from '../components/WaveformVisualizer';
import { audioService } from '../services/audioService';
import { storageService } from '../services/storageService';
import { ScreenId } from '../types';

interface HomeViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export const HomeView: React.FC<HomeViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const [alarmActive, setAlarmActive] = useState(true);
  const [isPlayingVoice, setIsPlayingVoice] = useState(false);
  const [activeDay, setActiveDay] = useState(15);

  const days = [
    { label: 'MO', num: 13 },
    { label: 'TU', num: 14 },
    { label: 'WE', num: 15 },
    { label: 'TH', num: 16 },
    { label: 'FR', num: 17 },
    { label: 'SA', num: 18 },
    { label: 'SU', num: 19 },
  ];

  const handleToggleVoicePlay = () => {
    if (!isPlayingVoice) {
      audioService.playChime('tibetan');
      setIsPlayingVoice(true);
      setTimeout(() => setIsPlayingVoice(false), 4000);
    } else {
      audioService.stopCurrentPlayback();
      setIsPlayingVoice(false);
    }
  };

  const handleToggleAlarm = () => {
    audioService.playChime('click');
    setAlarmActive(!alarmActive);
  };

  return (
    <div className="flex flex-col w-full px-4 gap-5 pb-10">
      {/* 1. Greeting & Micro-badge Header */}
      <div className="flex items-center justify-between pt-2">
        <div className="flex flex-col">
          <div className="flex items-center gap-1.5">
            <span className={`text-xs font-medium ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
              Wednesday, May 15
            </span>
            <span className="inline-flex items-center px-2 py-0.5 rounded-full bg-[#F9DF72] text-[#736200] font-bold text-[10px]">
              ✨ In Flow
            </span>
          </div>
          <h1 className={`font-headline text-2xl font-bold tracking-tight mt-0.5 ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Hey Taigo, ready today?
          </h1>
        </div>
        <div 
          onClick={onOpenVoiceStudio}
          role="button"
          tabIndex={0}
          aria-label="Open voice studio"
          className="w-10 h-10 rounded-full bg-[#FFD8EC] flex items-center justify-center rotate-6 hover:rotate-12 transition-transform shadow-xs cursor-pointer active:scale-95"
        >
          <span className="material-symbols-outlined text-[#864B70] text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
            local_fire_department
          </span>
        </div>
      </div>

      {/* 2. Calendar Day-of-Week Strip */}
      <div className="flex items-center justify-between gap-1 overflow-x-auto py-1 no-scrollbar">
        {days.map((d) => {
          const isActive = d.num === activeDay;
          return (
            <button
              key={d.num}
              onClick={() => {
                audioService.playChime('click');
                setActiveDay(d.num);
              }}
              className={`flex flex-col items-center justify-center py-2 px-2.5 rounded-2xl min-w-[44px] transition-all duration-200 ${
                isActive
                  ? 'bg-[#1C1B1B] text-white shadow-md scale-105'
                  : oledMode
                    ? 'bg-neutral-900 text-neutral-400 hover:text-white'
                    : 'bg-[#F6F3F2] text-[#50434A] hover:bg-[#EAE7E7]'
              }`}
            >
              <span className="text-[10px] font-bold opacity-80">{d.label}</span>
              <span className="font-headline text-base font-bold mt-0.5">{d.num}</span>
              {isActive && <span className="w-1.5 h-1.5 rounded-full bg-[#F9DF72] mt-1" />}
            </button>
          );
        })}
      </div>

      {/* 3. Hero Sync Score Bento Section */}
      <div className={`rounded-3xl p-4 flex flex-col relative overflow-hidden shadow-sm border transition-colors ${
        oledMode 
          ? 'bg-neutral-900/90 border-neutral-800 text-white' 
          : 'bg-white border-black/[0.04] text-[#1C1B1B]'
      }`}>
        <div className="flex items-start justify-between">
          <div className="flex flex-col">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#FFD8EC] text-[#37062A] font-semibold text-xs w-fit">
              <span className="material-symbols-outlined text-[15px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                auto_awesome
              </span>
              <span>daily sync score</span>
            </div>
            <div className="flex items-baseline gap-2 mt-3">
              <span className="font-headline text-4xl font-bold tracking-tighter">8.9</span>
              <span className={`text-sm ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>/ 10</span>
            </div>
            <p className={`text-xs mt-1 ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
              4 of 6 focus moments completed today
            </p>
          </div>

          {/* Decorative Starburst Sticker Badge */}
          <div className="w-16 h-16 rounded-full bg-[#F9DF72] text-[#736200] flex items-center justify-center -rotate-6 relative shadow-sm shrink-0">
            <svg className="w-12 h-12 text-[#736200]" fill="currentColor" viewBox="0 0 100 100">
              <path d="M50 0 L58 35 L93 22 L72 50 L99 69 L64 73 L62 100 L42 75 L15 93 L27 60 L0 50 L28 36 L11 9 L43 23 Z" />
            </svg>
            <span className="absolute font-headline text-lg font-bold text-[#1C1B1B]">★</span>
          </div>
        </div>

        {/* Segmented Bar Progress Indicator (4 of 6 completed) */}
        <div className="grid grid-cols-6 gap-1.5 mt-4 pt-2">
          <div className="h-2 rounded-full bg-[#864B70]"></div>
          <div className="h-2 rounded-full bg-[#864B70]"></div>
          <div className="h-2 rounded-full bg-[#864B70]"></div>
          <div className="h-2 rounded-full bg-[#864B70]"></div>
          <div className={`h-2 rounded-full ${oledMode ? 'bg-neutral-800' : 'bg-[#D4C2C9]'}`}></div>
          <div className={`h-2 rounded-full ${oledMode ? 'bg-neutral-800' : 'bg-[#D4C2C9]'}`}></div>
        </div>
      </div>

      {/* 4. Featured Voice Alarm Card */}
      <div className={`rounded-3xl p-4 flex flex-col gap-3 relative overflow-hidden shadow-sm border transition-colors ${
        oledMode
          ? 'bg-neutral-900 border-neutral-800'
          : 'bg-[#EAE7E7] border-black/[0.04]'
      }`}>
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-2.5">
            <img
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
              alt="Maya R."
              className="w-11 h-11 rounded-full object-cover shadow-xs ring-2 ring-white/60"
            />
            <div className="flex flex-col">
              <span className={`font-headline text-base font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
                Tomorrow, 07:30 AM
              </span>
              <span className={`text-xs flex items-center gap-1 ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
                Voice Alarm by <strong className="font-semibold text-[#864B70] dark:text-[#F5ACD7]">Maya R.</strong>
              </span>
            </div>
          </div>

          {/* Toggle Switch */}
          <button
            onClick={handleToggleAlarm}
            aria-label="Toggle alarm"
            className={`w-12 h-7 rounded-full p-0.5 flex items-center transition-all duration-300 cursor-pointer ${
              alarmActive ? 'bg-[#1C1B1B]' : 'bg-neutral-400 dark:bg-neutral-700'
            }`}
          >
            <div
              className={`w-6 h-6 rounded-full bg-white shadow-sm transform transition-transform duration-300 ${
                alarmActive ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* Voice Note Pill Container */}
        <div className="rounded-2xl bg-white dark:bg-neutral-950 p-3 flex flex-col gap-2 shadow-xs border border-black/[0.04] dark:border-neutral-800">
          <div className="flex items-center justify-between">
            <div className="inline-flex items-center gap-1.5 text-[#864B70] dark:text-[#F5ACD7] font-bold text-xs">
              <span className="material-symbols-outlined text-[15px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                mic
              </span>
              <span>Recorded snippet</span>
            </div>
            <span className="text-[11px] font-mono text-neutral-500">0:24</span>
          </div>

          <p className="text-xs italic text-[#1C1B1B] dark:text-neutral-200">
            "Rise and shine sunny! Remember to drink your lemon water first thing."
          </p>

          {/* Waveform and Play Button Row */}
          <div className="flex items-center gap-2.5 pt-1">
            <button
              onClick={handleToggleVoicePlay}
              className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 transition-all active:scale-95 shadow-xs ${
                isPlayingVoice
                  ? 'bg-[#864B70] text-white'
                  : 'bg-[#1C1B1B] text-white hover:bg-neutral-800'
              }`}
            >
              <span className="material-symbols-outlined text-[20px]">
                {isPlayingVoice ? 'pause' : 'play_arrow'}
              </span>
            </button>

            {/* Interactive Waveform */}
            <div className="flex-1 overflow-hidden">
              <WaveformVisualizer isPlaying={isPlayingVoice} barCount={18} heightClass="h-8" />
            </div>
          </div>
        </div>
      </div>

      {/* 5. Today's Highlights (2x2 Modular Bento Grid) */}
      <div className="flex flex-col gap-2">
        <div className="flex items-center justify-between">
          <h2 className={`font-headline text-lg font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Today's Highlights
          </h2>
          <button
            onClick={() => onNavigate('reminders')}
            className="text-xs font-semibold text-[#864B70] dark:text-[#F5ACD7] flex items-center gap-0.5 hover:underline"
          >
            <span>See all</span>
            <span className="material-symbols-outlined text-[15px]">arrow_forward</span>
          </button>
        </div>

        <div className="grid grid-cols-2 gap-2.5">
          {/* Tile 1: Butter Yellow Wake-Up Note */}
          <div 
            onClick={() => onNavigate('alarms')}
            role="button"
            tabIndex={0}
            className="rounded-3xl bg-[#F9DF72] text-[#1C1B1B] p-4 flex flex-col justify-between h-40 shadow-sm cursor-pointer hover:scale-[1.01] active:scale-98 transition-all"
          >
            <div className="flex items-start justify-between">
              <span className="px-2.5 py-1 rounded-full bg-white/90 text-xs font-bold shadow-xs">
                7:30 AM
              </span>
              <span className="w-7 h-7 rounded-full bg-[#1C1B1B] text-white flex items-center justify-center text-xs font-bold shadow-xs">
                ✓
              </span>
            </div>
            <div>
              <div className="font-headline text-lg font-bold leading-snug">
                Wake-up Note
              </div>
              <p className="text-xs font-semibold mt-0.5 opacity-85">
                Maya left voice memo
              </p>
            </div>
          </div>

          {/* Tile 2: Candy Pink Gratitude Card */}
          <div 
            onClick={() => onNavigate('habits')}
            role="button"
            tabIndex={0}
            className="rounded-3xl bg-[#F5ACD7] text-[#1C1B1B] p-4 flex flex-col justify-between h-40 shadow-sm cursor-pointer hover:scale-[1.01] active:scale-98 transition-all"
          >
            <div className="flex items-start justify-between">
              <span className="px-2.5 py-1 rounded-full bg-white/90 text-xs font-bold shadow-xs">
                15 min
              </span>
              <div className="w-7 h-7 rounded-full bg-[#1C1B1B] text-white flex items-center justify-center -rotate-12 shadow-xs">
                <span className="material-symbols-outlined text-[16px] text-white" style={{ fontVariationSettings: "'FILL' 1" }}>
                  favorite
                </span>
              </div>
            </div>
            <div>
              <div className="font-headline text-lg font-bold leading-snug">
                Gratitude
              </div>
              <p className="text-xs font-semibold mt-0.5 opacity-85">
                3 moments pinned
              </p>
            </div>
          </div>

          {/* Tile 3: Matcha / Surface Container Tasks */}
          <div 
            onClick={() => onNavigate('tasks')}
            role="button"
            tabIndex={0}
            className={`rounded-3xl p-4 flex flex-col justify-between h-40 shadow-sm cursor-pointer hover:scale-[1.01] active:scale-98 transition-all ${
              oledMode ? 'bg-neutral-900 text-white' : 'bg-[#EAE7E7] text-[#1C1B1B]'
            }`}
          >
            <div className="flex items-start justify-between">
              <span className="px-2.5 py-1 rounded-full bg-white dark:bg-neutral-800 text-xs font-bold shadow-xs">
                3 / 4 Done
              </span>
              <span className="material-symbols-outlined text-[#864B70] dark:text-[#F5ACD7] text-[20px]">
                checklist
              </span>
            </div>
            <div>
              <div className="font-headline text-lg font-bold leading-snug">
                Daily Focus
              </div>
              <p className="text-xs text-neutral-500 dark:text-neutral-400 mt-0.5">
                Next: Evening walk
              </p>
            </div>
          </div>

          {/* Tile 4: Soft Cyan Hydration Card */}
          <div 
            onClick={() => {
              audioService.playChime('water');
              storageService.incrementHydration(250);
            }}
            role="button"
            tabIndex={0}
            className="rounded-3xl bg-[#DAE2FF] text-[#091A3E] p-4 flex flex-col justify-between h-40 shadow-sm cursor-pointer hover:scale-[1.01] active:scale-98 transition-all"
          >
            <div className="flex items-start justify-between">
              <span className="px-2.5 py-1 rounded-full bg-white/90 text-xs font-bold shadow-xs">
                6 / 8 Cups
              </span>
              <span className="material-symbols-outlined text-[#4F5D85] text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                water_drop
              </span>
            </div>
            <div>
              <div className="font-headline text-lg font-bold leading-snug">
                Hydration
              </div>
              <p className="text-xs font-semibold text-[#38466C] mt-0.5">
                1.5L logged (+250ml)
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* 6. Your People Section */}
      <div className="flex flex-col gap-2 pt-1">
        <div className="flex items-center justify-between">
          <h2 className={`font-headline text-lg font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Your People
          </h2>
          <span className="text-xs px-2 py-0.5 rounded-full bg-neutral-200 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 font-semibold">
            3 online
          </span>
        </div>

        <div className="flex items-center gap-4 overflow-x-auto py-2 -mx-4 px-4 no-scrollbar">
          {/* Person 1: Maya */}
          <div 
            onClick={() => onNavigate('chats')}
            className="flex flex-col items-center min-w-[76px] cursor-pointer group active:scale-95 transition-transform"
          >
            <div className="relative">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                alt="Maya"
                className="w-14 h-14 rounded-full object-cover shadow-sm ring-2 ring-[#F5ACD7]"
              />
              <span className="absolute bottom-0 right-0 w-4 h-4 rounded-full bg-[#F9DF72] flex items-center justify-center text-[10px] text-[#736200] shadow-xs font-bold">
                🎙️
              </span>
            </div>
            <span className={`text-xs font-bold mt-1.5 ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Maya
            </span>
            <span className="text-[10px] text-neutral-500 truncate max-w-[76px]">
              Morning note
            </span>
          </div>

          {/* Person 2: Leo */}
          <div 
            onClick={() => onNavigate('chats')}
            className="flex flex-col items-center min-w-[76px] cursor-pointer group active:scale-95 transition-transform"
          >
            <div className="relative">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU"
                alt="Leo"
                className="w-14 h-14 rounded-full object-cover shadow-sm ring-2 ring-[#B3C1EF]"
              />
              <span className="absolute bottom-0 right-0 w-4 h-4 rounded-full bg-neutral-800 text-white flex items-center justify-center text-[9px] shadow-xs font-bold">
                ✓
              </span>
            </div>
            <span className={`text-xs font-bold mt-1.5 ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Leo
            </span>
            <span className="text-[10px] text-neutral-500 truncate max-w-[76px]">
              Tasks done
            </span>
          </div>

          {/* Person 3: Sofia */}
          <div 
            onClick={() => onNavigate('chats')}
            className="flex flex-col items-center min-w-[76px] cursor-pointer group active:scale-95 transition-transform"
          >
            <div className="relative">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704"
                alt="Sofia"
                className="w-14 h-14 rounded-full object-cover shadow-sm ring-2 ring-[#F9DF72]"
              />
              <span className="absolute bottom-0 right-0 w-4 h-4 rounded-full bg-[#FFD8EC] text-[#864B70] flex items-center justify-center text-[9px] shadow-xs font-bold">
                ☀️
              </span>
            </div>
            <span className={`text-xs font-bold mt-1.5 ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Sofia
            </span>
            <span className="text-[10px] text-neutral-500 truncate max-w-[76px]">
              Awake
            </span>
          </div>

          {/* Person 4: Add New Friend Pill */}
          <div 
            onClick={() => onNavigate('people')}
            className="flex flex-col items-center min-w-[76px] cursor-pointer group active:scale-95 transition-transform"
          >
            <div className={`w-14 h-14 rounded-full flex items-center justify-center transition-colors ${
              oledMode ? 'bg-neutral-900 text-neutral-300' : 'bg-[#EAE7E7] text-[#50434A]'
            }`}>
              <span className="material-symbols-outlined text-[24px]">person_add</span>
            </div>
            <span className={`text-xs font-bold mt-1.5 ${oledMode ? 'text-neutral-300' : 'text-neutral-700'}`}>
              Add
            </span>
            <span className="text-[10px] text-neutral-500">
              Connect
            </span>
          </div>
        </div>
      </div>

      {/* 7. Quick Check-in Prompt */}
      <div className={`rounded-3xl p-4 flex items-center justify-between border transition-colors ${
        oledMode
          ? 'bg-neutral-900 border-neutral-800 text-white'
          : 'bg-[#F6F3F2] border-black/[0.04] text-[#1C1B1B]'
      }`}>
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-[#F9DF72] flex items-center justify-center text-[#736200] shrink-0">
            <span className="material-symbols-outlined text-[20px]">edit_note</span>
          </div>
          <div className="flex flex-col">
            <span className="font-headline font-bold text-sm">Evening Reflection</span>
            <span className="text-xs text-neutral-500">Ready at 8:00 PM</span>
          </div>
        </div>

        <button
          onClick={() => {
            audioService.playChime('click');
            onNavigate('quiet-breakthrough');
          }}
          className="px-4 py-2 rounded-full bg-white dark:bg-neutral-800 text-xs font-bold shadow-xs active:scale-95 transition-transform"
        >
          Preview
        </button>
      </div>
    </div>
  );
};
