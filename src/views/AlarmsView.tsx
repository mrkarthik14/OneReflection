import React, { useState } from 'react';
import { WaveformVisualizer } from '../components/WaveformVisualizer';
import { audioService } from '../services/audioService';
import { storageService } from '../services/storageService';
import { ScreenId } from '../types';

interface AlarmsViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export const AlarmsView: React.FC<AlarmsViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const [heroAlarmEnabled, setHeroAlarmEnabled] = useState(true);
  const [standupAlarmEnabled, setStandupAlarmEnabled] = useState(true);
  const [sprintAlarmEnabled, setSprintAlarmEnabled] = useState(true);
  const [windDownAlarmEnabled, setWindDownAlarmEnabled] = useState(false);
  const [isPlayingMaya, setIsPlayingMaya] = useState(false);
  const [isPlayingLeo, setIsPlayingLeo] = useState(false);
  const [activeDayIndex, setActiveDayIndex] = useState(2); // Wednesday

  const weekDays = [
    { name: 'MON', num: '14' },
    { name: 'TUE', num: '15' },
    { name: 'WED', num: '16' },
    { name: 'THU', num: '17' },
    { name: 'FRI', num: '18' },
    { name: 'SAT', num: '19' },
  ];

  const handlePlayMaya = () => {
    if (!isPlayingMaya) {
      audioService.playChime('tibetan');
      setIsPlayingMaya(true);
      setTimeout(() => setIsPlayingMaya(false), 3500);
    } else {
      audioService.stopCurrentPlayback();
      setIsPlayingMaya(false);
    }
  };

  const handlePlayLeo = () => {
    if (!isPlayingLeo) {
      audioService.playChime('nordic');
      setIsPlayingLeo(true);
      setTimeout(() => setIsPlayingLeo(false), 3000);
    } else {
      audioService.stopCurrentPlayback();
      setIsPlayingLeo(false);
    }
  };

  return (
    <div className="flex flex-col w-full px-4 pb-12 gap-y-4">
      {/* 1. Top Daily Greeting & Highlight Badge */}
      <div className="flex flex-col gap-y-2 mt-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5 bg-[#F9DF72] text-[#736200] px-3 py-1.5 rounded-full shadow-xs">
            <span className="material-symbols-outlined text-[18px]">alarm_on</span>
            <span className="text-xs font-bold tracking-tight">Next alarm in 6h 45m</span>
          </div>

          <div 
            onClick={() => audioService.playChime('solfeggio')}
            className="relative flex items-center justify-center rotate-6 hover:rotate-12 transition-transform cursor-pointer"
            title="Play Solfeggio Chime"
          >
            <svg className="w-8 h-8 text-[#F9DF72] fill-current" viewBox="0 0 100 100">
              <path d="M50 0 C53 20 60 27 80 30 C60 33 53 40 50 60 C47 40 40 33 20 30 C40 27 47 20 50 0 Z" />
              <path d="M50 40 C52 53 57 58 70 60 C57 62 52 67 50 80 C48 67 43 62 30 60 C43 58 48 53 50 40 Z" opacity="0.8" />
            </svg>
            <span className="absolute text-xs text-[#736200] font-bold">✦</span>
          </div>
        </div>

        <div className="flex flex-col">
          <h1 className={`font-headline text-2xl font-bold tracking-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Wake-up & Shared Alarms
          </h1>
          <p className={`text-xs ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
            Start the day with voices you trust
          </p>
        </div>
      </div>

      {/* 2. Weekly Horizontal Calendar Strip */}
      <div className={`flex items-center justify-between gap-1 p-2 rounded-2xl border ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F0EDED] border-black/[0.04]'
      }`}>
        {weekDays.map((d, idx) => {
          const isActive = idx === activeDayIndex;
          return (
            <button
              key={d.num}
              onClick={() => {
                audioService.playChime('click');
                setActiveDayIndex(idx);
              }}
              className={`flex-1 flex flex-col items-center py-2 rounded-xl transition-all ${
                isActive
                  ? 'bg-[#F9DF72] text-[#736200] shadow-sm font-bold scale-105'
                  : idx === 0
                    ? 'bg-white dark:bg-neutral-800 text-[#1C1B1B] dark:text-white shadow-xs'
                    : 'text-neutral-500 hover:text-neutral-800 dark:hover:text-white'
              }`}
            >
              <span className="text-[10px] font-bold tracking-tight">{d.name}</span>
              <span className="font-headline text-sm font-bold mt-0.5">{d.num}</span>
              <span className={`w-1.5 h-1.5 rounded-full mt-1 ${isActive ? 'bg-[#736200]' : idx === 0 || idx === 3 || idx === 4 ? 'bg-[#864B70]' : 'bg-transparent'}`} />
            </button>
          );
        })}
      </div>

      {/* 3. Featured Active Alarm Hero Card (Butter Yellow Theme) */}
      <div className="relative bg-[#F9DF72] text-[#1C1B1B] rounded-3xl p-5 flex flex-col gap-y-4 shadow-sm overflow-hidden border border-black/10">
        <div className="flex items-start justify-between">
          <div className="flex flex-col">
            <div className="flex items-center gap-1.5">
              <span className="text-[10px] uppercase font-bold tracking-wider bg-white/90 px-2 py-0.5 rounded-full border border-black/10">
                Shared Voice
              </span>
              <span className="text-[10px] font-bold text-[#736200]">Tomorrow</span>
            </div>
            <span className="font-headline text-xl mt-1 font-bold">Morning Sunshine</span>
          </div>

          {/* Toggle Switch */}
          <button
            onClick={() => {
              audioService.playChime('click');
              setHeroAlarmEnabled(!heroAlarmEnabled);
            }}
            aria-label="Toggle Morning Sunshine Alarm"
            className={`w-13 h-7 rounded-full p-0.5 flex items-center transition-all cursor-pointer ${
              heroAlarmEnabled ? 'bg-[#1C1B1B]' : 'bg-neutral-400'
            }`}
          >
            <span
              className={`w-6 h-6 rounded-full bg-[#F9DF72] flex items-center justify-center transition-transform shadow-xs ${
                heroAlarmEnabled ? 'translate-x-6' : 'translate-x-0'
              }`}
            >
              <span className="material-symbols-outlined text-[14px] text-[#1C1B1B] font-bold">
                {heroAlarmEnabled ? 'check' : 'close'}
              </span>
            </span>
          </button>
        </div>

        {/* Giant Time Display */}
        <div className="flex items-baseline justify-between my-0.5">
          <div className="font-headline text-4xl tracking-tight font-bold">
            07:30 <span className="text-xl font-normal opacity-80">AM</span>
          </div>
          <div className="flex gap-1">
            {['M', 'T', 'W', 'T', 'F'].map((l, i) => (
              <span
                key={i}
                className="w-6 h-6 rounded-full bg-white flex items-center justify-center text-[10px] font-bold text-[#1C1B1B] shadow-xs"
              >
                {l}
              </span>
            ))}
          </div>
        </div>

        {/* Voice Clip Sub-Card */}
        <div className="bg-white/95 rounded-2xl p-3 flex flex-col gap-y-2 border border-black/10 shadow-xs">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="relative">
                <img
                  src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                  alt="Maya"
                  className="w-9 h-9 rounded-full object-cover shadow-xs"
                />
                <span className="absolute -bottom-1 -right-1 w-4 h-4 rounded-full bg-[#864B70] flex items-center justify-center text-white">
                  <span className="material-symbols-outlined text-[10px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                    favorite
                  </span>
                </span>
              </div>
              <div className="flex flex-col">
                <span className="font-headline text-xs font-bold text-[#1C1B1B]">
                  Maya (Partner)
                </span>
                <span className="text-[10px] text-neutral-500 font-medium">
                  Sent yesterday at 9:15 PM
                </span>
              </div>
            </div>

            <button
              onClick={handlePlayMaya}
              className={`w-8 h-8 rounded-full flex items-center justify-center hover:scale-105 active:scale-95 transition-all shadow-xs ${
                isPlayingMaya ? 'bg-[#864B70] text-white' : 'bg-[#1C1B1B] text-white'
              }`}
            >
              <span className="material-symbols-outlined text-[18px]">
                {isPlayingMaya ? 'pause' : 'play_arrow'}
              </span>
            </button>
          </div>

          <p className="text-xs italic text-[#1C1B1B] font-medium px-1">
            "Wake up lazybones, coffee is on me! Let’s crush today."
          </p>

          {/* Waveform visualizer */}
          <div className="pt-1">
            <WaveformVisualizer isPlaying={isPlayingMaya} barCount={18} heightClass="h-6" />
          </div>
        </div>
      </div>

      {/* 4. Bento Section: Upcoming Cycles */}
      <div className="flex items-center justify-between pt-1">
        <div className="flex items-center gap-1.5">
          <span className={`font-headline text-lg font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Upcoming Cycles
          </span>
          <span className="bg-neutral-200 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 px-2 py-0.5 rounded-full text-[10px] font-bold">
            3 total
          </span>
        </div>
        <button
          onClick={() => onNavigate('bedtime-sync')}
          className="text-xs font-bold text-[#864B70] dark:text-[#F5ACD7] hover:underline"
        >
          Customize
        </button>
      </div>

      {/* Stack of Alarm Cards */}
      <div className="flex flex-col gap-y-3">
        {/* Alarm 1: Lavender Blue Card - Studio Standup */}
        <div className="bg-[#DAE2FF] text-[#091A3E] rounded-3xl p-4 flex flex-col gap-y-3 shadow-xs border border-[#4F5D85]/20">
          <div className="flex items-start justify-between">
            <div className="flex flex-col">
              <div className="flex items-center gap-1">
                <span className="material-symbols-outlined text-[15px] text-[#4F5D85]">groups</span>
                <span className="text-[10px] uppercase font-bold tracking-wider text-[#38466C]">
                  Sync with Studio
                </span>
              </div>
              <span className="font-headline text-base font-bold text-[#091A3E]">
                Team Standup Ping
              </span>
            </div>

            <button
              onClick={() => {
                audioService.playChime('click');
                setStandupAlarmEnabled(!standupAlarmEnabled);
              }}
              className={`w-11 h-6 rounded-full p-0.5 flex items-center transition-colors ${
                standupAlarmEnabled ? 'bg-[#1C1B1B]' : 'bg-neutral-400'
              }`}
            >
              <span
                className={`w-5 h-5 rounded-full bg-[#DAE2FF] flex items-center justify-center transition-transform ${
                  standupAlarmEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              >
                <span className="material-symbols-outlined text-[12px] text-[#1C1B1B] font-bold">
                  {standupAlarmEnabled ? 'check' : 'close'}
                </span>
              </span>
            </button>
          </div>

          <div className="flex items-baseline justify-between">
            <div className="font-headline text-3xl font-bold">
              08:45 <span className="text-xs font-normal">AM</span>
            </div>
            <div className="flex items-center gap-1 bg-white/80 text-[#091A3E] px-2.5 py-1 rounded-full text-xs font-semibold shadow-xs">
              <span className="material-symbols-outlined text-[13px] text-[#4F5D85]">repeat</span>
              <span>Mon - Fri</span>
            </div>
          </div>

          <div className="bg-white/80 rounded-2xl px-3 py-2 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-[#4F5D85] animate-pulse"></span>
              <span className="text-xs font-medium text-[#091A3E]">
                Chime: Nordic Glass Bell (Remote Sync)
              </span>
            </div>
            <button 
              onClick={() => audioService.playChime('nordic')}
              title="Preview Nordic Chime"
              className="text-[#4F5D85] hover:text-[#091A3E]"
            >
              <span className="material-symbols-outlined text-[18px]">volume_up</span>
            </button>
          </div>
        </div>

        {/* Alarm 2: Candy Pink Card - Shared Focus Sprint */}
        <div className="bg-[#F5ACD7] text-[#1C1B1B] rounded-3xl p-4 flex flex-col gap-y-3 shadow-xs border border-black/10">
          <div className="flex items-start justify-between">
            <div className="flex flex-col">
              <div className="flex items-center gap-1">
                <span className="material-symbols-outlined text-[15px] text-[#864B70]">psychology</span>
                <span className="text-[10px] uppercase font-bold tracking-wider text-[#753C61]">
                  Buddy Sprint
                </span>
              </div>
              <span className="font-headline text-base font-bold">
                Shared Focus Sprint
              </span>
            </div>

            <button
              onClick={() => {
                audioService.playChime('click');
                setSprintAlarmEnabled(!sprintAlarmEnabled);
              }}
              className={`w-11 h-6 rounded-full p-0.5 flex items-center transition-colors ${
                sprintAlarmEnabled ? 'bg-[#1C1B1B]' : 'bg-neutral-400'
              }`}
            >
              <span
                className={`w-5 h-5 rounded-full bg-[#F5ACD7] flex items-center justify-center transition-transform ${
                  sprintAlarmEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              >
                <span className="material-symbols-outlined text-[12px] text-[#1C1B1B] font-bold">
                  {sprintAlarmEnabled ? 'check' : 'close'}
                </span>
              </span>
            </button>
          </div>

          <div className="flex items-baseline justify-between">
            <div className="font-headline text-3xl font-bold">
              02:30 <span className="text-xs font-normal">PM</span>
            </div>
            <div className="flex items-center -space-x-1.5">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU"
                alt="Leo"
                className="w-6 h-6 rounded-full object-cover ring-2 ring-white"
              />
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704"
                alt="Sofia"
                className="w-6 h-6 rounded-full object-cover ring-2 ring-white"
              />
              <span className="w-6 h-6 rounded-full bg-white text-[#1C1B1B] flex items-center justify-center text-[10px] font-bold shadow-xs">
                +2
              </span>
            </div>
          </div>

          <div className="bg-white/80 rounded-2xl px-3 py-2 flex items-center justify-between">
            <div className="flex items-center gap-2 min-w-0">
              <span className="material-symbols-outlined text-[16px] text-[#864B70]">mic</span>
              <span className="text-xs font-semibold truncate">
                Leo: “Time to lock in 45m deep code”
              </span>
            </div>
            <button
              onClick={handlePlayLeo}
              className="text-xs font-bold text-[#864B70] hover:underline shrink-0"
            >
              {isPlayingLeo ? 'Playing...' : '0:08 ▶'}
            </button>
          </div>
        </div>

        {/* Alarm 3: Calming Card - Wind Down */}
        <div className={`rounded-3xl p-4 flex flex-col gap-y-3 shadow-xs border transition-colors ${
          oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
        }`}>
          <div className="flex items-start justify-between">
            <div className="flex flex-col">
              <div className="flex items-center gap-1">
                <span className="material-symbols-outlined text-[15px] text-[#6F5D00] dark:text-[#F9DF72]">
                  nightlight
                </span>
                <span className="text-[10px] uppercase font-bold tracking-wider text-neutral-500">
                  Self Care
                </span>
              </div>
              <span className={`font-headline text-base font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
                Wind Down & Reflection
              </span>
            </div>

            <button
              onClick={() => {
                audioService.playChime('click');
                setWindDownAlarmEnabled(!windDownAlarmEnabled);
              }}
              className={`w-11 h-6 rounded-full p-0.5 flex items-center transition-colors ${
                windDownAlarmEnabled ? 'bg-[#1C1B1B] dark:bg-white' : 'bg-neutral-300 dark:bg-neutral-700'
              }`}
            >
              <span
                className={`w-5 h-5 rounded-full bg-white dark:bg-neutral-900 flex items-center justify-center transition-transform ${
                  windDownAlarmEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="flex items-baseline justify-between">
            <div className={`font-headline text-3xl font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              10:30 <span className="text-xs font-normal text-neutral-500">PM</span>
            </div>
            <span className="text-xs bg-neutral-200 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 px-2.5 py-1 rounded-full font-semibold">
              Everyday
            </span>
          </div>

          <div 
            onClick={() => audioService.playChime('solfeggio')}
            className="bg-white dark:bg-neutral-950 rounded-2xl px-3 py-2 flex items-center justify-between cursor-pointer hover:bg-neutral-50 dark:hover:bg-neutral-900 transition-colors shadow-xs"
          >
            <div className="flex items-center gap-2">
              <span className="material-symbols-outlined text-[16px] text-[#6F5D00] dark:text-[#F9DF72]">
                graphic_eq
              </span>
              <span className={`text-xs ${oledMode ? 'text-neutral-200' : 'text-[#1C1B1B]'}`}>
                Gentle Solfeggio Chime + 30s Audio Note
              </span>
            </div>
            <span className="material-symbols-outlined text-[16px] text-neutral-400">
              chevron_right
            </span>
          </div>
        </div>
      </div>

      {/* 5. Bottom Floating Action Dock */}
      <div className={`rounded-3xl p-3 flex flex-col gap-y-2 mt-2 border ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#EAE7E7] border-black/[0.04]'
      }`}>
        <div className="flex items-center justify-between px-1">
          <span className={`text-xs font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Need a gentle push?
          </span>
          <span className="text-[10px] text-neutral-500">Collaborative audio</span>
        </div>

        <div className="grid grid-cols-2 gap-2">
          <button
            onClick={onOpenVoiceStudio}
            className="h-12 bg-[#1C1B1B] text-white rounded-full px-3 flex items-center justify-center gap-2 shadow-xs hover:bg-neutral-800 active:scale-95 transition-all text-xs font-bold"
          >
            <span className="material-symbols-outlined text-[18px]">mic</span>
            <span>Record Voice</span>
          </button>

          <button
            onClick={() => onNavigate('people')}
            className="h-12 bg-white dark:bg-neutral-800 text-[#1C1B1B] dark:text-white rounded-full px-3 flex items-center justify-center gap-2 shadow-xs hover:bg-neutral-50 dark:hover:bg-neutral-700 active:scale-95 transition-all text-xs font-bold"
          >
            <span className="material-symbols-outlined text-[18px] text-[#864B70] dark:text-[#F5ACD7]">
              send
            </span>
            <span>Ask a Friend</span>
          </button>
        </div>
      </div>
    </div>
  );
};
