import React, { useState } from 'react';
import { WaveformVisualizer } from '../components/WaveformVisualizer';
import { audioService } from '../services/audioService';
import { ScreenId } from '../types';

interface ChatsViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export const ChatsView: React.FC<ChatsViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const [activeCategory, setActiveCategory] = useState<'all' | 'memos' | 'tasks' | 'family'>('all');
  const [isPlayingDispatch, setIsPlayingDispatch] = useState(false);
  const [speedMultiplier, setSpeedMultiplier] = useState<'1.0x' | '1.5x' | '2.0x'>('1.5x');
  const [sourdoughChecked, setSourdoughChecked] = useState(false);
  const [isWalkieTalkieActive, setIsWalkieTalkieActive] = useState(false);

  const handleToggleDispatch = () => {
    if (!isPlayingDispatch) {
      audioService.playChime('tibetan');
      setIsPlayingDispatch(true);
      setTimeout(() => setIsPlayingDispatch(false), 5000);
    } else {
      audioService.stopCurrentPlayback();
      setIsPlayingDispatch(false);
    }
  };

  const handleWalkieTalkiePress = () => {
    audioService.playChime('click');
    setIsWalkieTalkieActive(true);
  };

  const handleWalkieTalkieRelease = () => {
    audioService.playChime('solfeggio');
    setIsWalkieTalkieActive(false);
  };

  return (
    <div className="flex flex-col w-full px-4 pb-12 gap-y-5">
      {/* 1. Header & Context Block */}
      <section className="flex flex-col gap-1.5 pt-2">
        <div className="flex items-center justify-between">
          <div className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-[#F9DF72] text-[#736200] shadow-xs rotate-[-1deg]">
            <span className="text-xs">✨</span>
            <span className="text-[10px] font-bold uppercase tracking-wider">
              In Sync • Shared Voices
            </span>
          </div>

          <div 
            onClick={() => audioService.playChime('nordic')}
            className="w-8 h-8 rounded-full bg-[#FFD8EC] flex items-center justify-center text-[#864B70] rotate-6 shadow-xs cursor-pointer"
            title="Acoustic ping"
          >
            <span className="material-symbols-outlined text-[16px]">graphic_eq</span>
          </div>
        </div>

        <div className="pt-0.5">
          <h1 className={`font-headline text-3xl font-bold tracking-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Chats & Circles
          </h1>
          <p className={`text-xs ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
            Gentle whispers, quick pings, and shared daily rhythms.
          </p>
        </div>

        {/* Search Bar */}
        <div className="pt-1 flex items-center gap-2">
          <div className="relative flex-1">
            <span className="material-symbols-outlined absolute left-3.5 top-1/2 -translate-y-1/2 text-neutral-400 text-[18px]">
              search
            </span>
            <input
              type="text"
              placeholder="Search voice notes, circles, rituals..."
              className={`w-full h-11 pl-10 pr-4 rounded-full text-xs outline-none shadow-xs transition-colors ${
                oledMode 
                  ? 'bg-neutral-900 text-white placeholder:text-neutral-500 border border-neutral-800' 
                  : 'bg-white text-[#1C1B1B] placeholder:text-neutral-400 border border-black/[0.04]'
              }`}
            />
          </div>
          <button
            onClick={() => audioService.playChime('click')}
            aria-label="Filter chats"
            className={`w-11 h-11 rounded-full flex items-center justify-center shadow-xs active:scale-95 transition-transform ${
              oledMode ? 'bg-neutral-900 text-white' : 'bg-white text-[#1C1B1B]'
            }`}
          >
            <span className="material-symbols-outlined text-[18px]">tune</span>
          </button>
        </div>

        {/* Category Filter Chips */}
        <div className="flex items-center gap-1.5 pt-1 overflow-x-auto no-scrollbar">
          <button
            onClick={() => setActiveCategory('all')}
            className={`px-3.5 py-1.5 rounded-full text-xs font-bold transition-colors whitespace-nowrap ${
              activeCategory === 'all'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'bg-neutral-200 dark:bg-neutral-900 text-neutral-600 dark:text-neutral-300'
            }`}
          >
            All Circles
          </button>
          <button
            onClick={() => setActiveCategory('memos')}
            className={`px-3.5 py-1.5 rounded-full text-xs font-bold transition-colors whitespace-nowrap flex items-center gap-1 ${
              activeCategory === 'memos'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'bg-neutral-200 dark:bg-neutral-900 text-neutral-600 dark:text-neutral-300'
            }`}
          >
            <span className="material-symbols-outlined text-[13px]">mic</span>
            Voice Memos
          </button>
          <button
            onClick={() => setActiveCategory('tasks')}
            className={`px-3.5 py-1.5 rounded-full text-xs font-bold transition-colors whitespace-nowrap flex items-center gap-1 ${
              activeCategory === 'tasks'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'bg-neutral-200 dark:bg-neutral-900 text-neutral-600 dark:text-neutral-300'
            }`}
          >
            <span className="material-symbols-outlined text-[13px]">task_alt</span>
            Shared Tasks
          </button>
          <button
            onClick={() => setActiveCategory('family')}
            className={`px-3.5 py-1.5 rounded-full text-xs font-bold transition-colors whitespace-nowrap flex items-center gap-1 ${
              activeCategory === 'family'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'bg-neutral-200 dark:bg-neutral-900 text-neutral-600 dark:text-neutral-300'
            }`}
          >
            <span className="material-symbols-outlined text-[13px]">family_restroom</span>
            Family
          </button>
        </div>
      </section>

      {/* 2. Active Circle Presence (Horizontal Scroll Row) */}
      <section className="flex flex-col gap-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className={`font-headline text-base font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Your Circle
            </span>
            <span className="px-2 py-0.5 rounded-full bg-[#F9DF72] text-[#736200] font-bold text-[10px]">
              5 online
            </span>
          </div>
          <button
            onClick={() => onNavigate('people')}
            className="text-xs font-bold text-[#864B70] dark:text-[#F5ACD7] flex items-center gap-0.5 hover:underline"
          >
            Manage <span className="material-symbols-outlined text-[15px]">chevron_right</span>
          </button>
        </div>

        <div className="flex items-center gap-3.5 overflow-x-auto no-scrollbar py-1 -mx-4 px-4">
          {/* Invite Pill */}
          <div 
            onClick={() => onNavigate('people')}
            className="flex flex-col items-center gap-1 shrink-0 cursor-pointer active:scale-95 transition-transform"
          >
            <div className={`w-14 h-14 rounded-full flex items-center justify-center shadow-xs border ${
              oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-white border-black/[0.04] text-[#1C1B1B]'
            }`}>
              <span className="material-symbols-outlined text-[22px]">person_add</span>
            </div>
            <span className="text-[10px] font-bold text-neutral-500">Invite</span>
          </div>

          {/* Maya (Partner) */}
          <div className="flex flex-col items-center gap-1 shrink-0 cursor-pointer">
            <div className="relative w-14 h-14 rounded-full bg-[#F5ACD7] p-0.5 shadow-xs">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                alt="Maya"
                className="w-full h-full object-cover rounded-full"
              />
              <span className="absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-[#F9DF72] text-[#736200] flex items-center justify-center text-[10px] shadow-xs">
                ✨
              </span>
            </div>
            <span className={`text-xs font-bold leading-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Maya
            </span>
            <span className="text-[10px] text-neutral-500 -mt-0.5">Awake</span>
          </div>

          {/* Leo */}
          <div className="flex flex-col items-center gap-1 shrink-0 cursor-pointer">
            <div className="relative w-14 h-14 rounded-full bg-[#B3C1EF] p-0.5 shadow-xs">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU"
                alt="Leo"
                className="w-full h-full object-cover rounded-full"
              />
              <span className="absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-[#1C1B1B] text-white flex items-center justify-center text-[10px] shadow-xs">
                ⚡
              </span>
            </div>
            <span className={`text-xs font-bold leading-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Leo
            </span>
            <span className="text-[10px] text-neutral-500 -mt-0.5">Sprint</span>
          </div>

          {/* Sofia */}
          <div className="flex flex-col items-center gap-1 shrink-0 cursor-pointer">
            <div className="relative w-14 h-14 rounded-full bg-[#F9DF72] p-0.5 shadow-xs">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704"
                alt="Sofia"
                className="w-full h-full object-cover rounded-full"
              />
              <span className="absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-[#DAE2FF] text-[#091A3E] flex items-center justify-center text-[10px] shadow-xs">
                🎧
              </span>
            </div>
            <span className={`text-xs font-bold leading-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Sofia
            </span>
            <span className="text-[10px] text-neutral-500 -mt-0.5">Listening</span>
          </div>

          {/* Alex */}
          <div className="flex flex-col items-center gap-1 shrink-0 cursor-pointer">
            <div className="relative w-14 h-14 rounded-full bg-[#FFD8EC] p-0.5 shadow-xs">
              <div className="w-full h-full rounded-full bg-neutral-200 dark:bg-neutral-800 flex items-center justify-center text-xs font-bold">
                AC
              </div>
              <span className="absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-[#864B70] text-white flex items-center justify-center text-[10px] shadow-xs animate-pulse">
                🎙️
              </span>
            </div>
            <span className={`text-xs font-bold leading-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Alex
            </span>
            <span className="text-[10px] text-neutral-500 -mt-0.5">Talking</span>
          </div>
        </div>
      </section>

      {/* 3. Featured Bento: Highlight Collaborative Conversation (Candy Pink Bento) */}
      <section className="flex flex-col">
        <div className="relative w-full rounded-3xl bg-[#F5ACD7] text-[#1C1B1B] p-5 shadow-sm overflow-hidden flex flex-col gap-3.5 border border-black/10">
          <div className="flex items-center justify-between relative z-10">
            <div className="flex items-center gap-2.5">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                alt="Maya"
                className="w-11 h-11 rounded-full object-cover ring-2 ring-white/60 shadow-xs"
              />
              <div className="flex flex-col">
                <div className="flex items-center gap-1.5">
                  <span className="font-headline font-bold text-base">Maya R.</span>
                  <span className="w-2 h-2 rounded-full bg-[#FCE275]"></span>
                </div>
                <span className="text-xs text-[#753C61] font-semibold">Partner • In Deep Flow</span>
              </div>
            </div>

            <span className="px-2.5 py-1 rounded-full bg-[#1C1B1B] text-white font-bold text-[10px] shadow-xs flex items-center gap-1">
              <span className="material-symbols-outlined text-[12px]">graphic_eq</span>
              1 new memo
            </span>
          </div>

          {/* Interactive Voice Note Waveform Player */}
          <div className="relative z-10 w-full rounded-2xl bg-white/90 p-3.5 shadow-xs flex flex-col gap-2.5 border border-black/10">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-1 text-[#1C1B1B]">
                <span className="material-symbols-outlined text-[16px] text-[#864B70]">record_voice_over</span>
                <span className="text-xs font-bold">Morning Voice Dispatch</span>
              </div>
              <span className="text-[11px] font-mono text-neutral-500">0:38 / 1:12</span>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={handleToggleDispatch}
                aria-label="Play voice note"
                className={`w-10 h-10 shrink-0 rounded-full flex items-center justify-center active:scale-95 transition-transform shadow-xs ${
                  isPlayingDispatch ? 'bg-[#864B70] text-white' : 'bg-[#1C1B1B] text-white'
                }`}
              >
                <span className="material-symbols-outlined text-[20px]">
                  {isPlayingDispatch ? 'pause' : 'play_arrow'}
                </span>
              </button>

              <div className="flex-1 overflow-hidden">
                <WaveformVisualizer isPlaying={isPlayingDispatch} barCount={20} heightClass="h-8" />
              </div>

              <button
                onClick={() => {
                  const next = speedMultiplier === '1.0x' ? '1.5x' : speedMultiplier === '1.5x' ? '2.0x' : '1.0x';
                  setSpeedMultiplier(next);
                  audioService.playChime('click');
                }}
                className="px-2 py-1 rounded-full bg-neutral-200 text-[#1C1B1B] font-bold text-[10px] active:scale-95"
              >
                {speedMultiplier}
              </button>
            </div>

            <p className="text-xs italic text-[#1C1B1B] leading-snug pt-0.5">
              “Sending gentle morning energy! I also attached our dinner sourdough grocery to-do.”
            </p>
          </div>

          {/* Embedded Contextual Shared Task Mini-Chip */}
          <div className="relative z-10 w-full rounded-2xl bg-white/95 p-3 shadow-xs flex items-center justify-between border border-black/10">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-xl bg-[#F9DF72] text-[#736200] flex items-center justify-center text-sm shadow-xs font-bold">
                🥖
              </div>
              <div className="flex flex-col">
                <span className={`text-xs font-bold ${sourdoughChecked ? 'line-through text-neutral-400' : 'text-[#1C1B1B]'}`}>
                  Pick up artisan sourdough
                </span>
                <span className="text-[10px] text-neutral-500">Shared To-Do • 2 items remaining</span>
              </div>
            </div>

            <button
              onClick={() => {
                audioService.playChime('click');
                setSourdoughChecked(!sourdoughChecked);
              }}
              className={`w-7 h-7 rounded-full flex items-center justify-center transition-all ${
                sourdoughChecked
                  ? 'bg-[#1C1B1B] text-white'
                  : 'bg-neutral-200 text-neutral-700 hover:bg-neutral-300'
              }`}
            >
              <span className="material-symbols-outlined text-[16px]">check</span>
            </button>
          </div>

          {/* Bottom Card Meta */}
          <div className="flex items-center justify-between pt-0.5 relative z-10">
            <span className="text-[11px] font-semibold text-[#753C61]">Delivered 2m ago</span>
            <button
              onClick={onOpenVoiceStudio}
              className="px-3.5 py-1.5 rounded-full bg-[#1C1B1B] text-white font-bold text-xs flex items-center gap-1 shadow-xs active:scale-95 transition-all"
            >
              <span className="material-symbols-outlined text-[14px]">reply</span>
              Reply
            </button>
          </div>
        </div>
      </section>

      {/* 4. Recent Conversations Bento Grid */}
      <section className="flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <h2 className={`font-headline text-base font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Recent Channels
          </h2>
          <span className="text-[10px] font-bold text-neutral-500 uppercase tracking-wider">
            Updated Now
          </span>
        </div>

        {/* Channel 1: Studio Dev Sync (Lavender Blue) */}
        <article className="w-full rounded-3xl bg-[#DAE2FF] text-[#091A3E] p-4 shadow-xs flex flex-col gap-2.5 border border-[#4F5D85]/20">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-9 h-9 rounded-2xl bg-[#1C1B1B] text-white flex items-center justify-center">
                <span className="material-symbols-outlined text-[18px]">groups</span>
              </div>
              <div className="flex flex-col">
                <div className="flex items-center gap-1.5">
                  <span className="font-headline font-bold text-sm">Studio Dev Sync</span>
                  <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-white text-[#091A3E]">
                    Leo & Sam
                  </span>
                </div>
                <span className="text-[11px] text-[#38466C] font-medium">Sprint 14 Sync Hub</span>
              </div>
            </div>
            <span className="text-[10px] font-mono text-neutral-500">11:20 AM</span>
          </div>

          <div className="rounded-2xl bg-white/80 p-2.5 flex items-center justify-between gap-2 shadow-xs">
            <div className="flex items-center gap-2 min-w-0">
              <span className="w-6 h-6 rounded-full bg-[#B3C1EF] text-[#091A3E] flex items-center justify-center shrink-0">
                <span className="material-symbols-outlined text-[13px]">graphic_eq</span>
              </span>
              <p className="text-xs truncate">
                Leo: “Compose migration looks solid on staging...”
              </p>
            </div>
            <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-[#F9DF72] text-[#736200] shrink-0">
              0:45s
            </span>
          </div>

          <div className="flex items-center justify-between pt-0.5">
            <div className="flex items-center gap-1.5">
              <span className="px-2.5 py-0.5 rounded-full bg-white text-[10px] font-bold">
                ⚡ Sprint Block
              </span>
              <span className="px-2 py-0.5 rounded-full bg-white text-[10px] font-semibold text-neutral-500">
                4 members
              </span>
            </div>
            <span className="material-symbols-outlined text-[16px] text-[#4F5D85]">done_all</span>
          </div>
        </article>

        {/* Channel 2: Sofia Miller (Butter Yellow) */}
        <article className="w-full rounded-3xl bg-[#F9DF72] text-[#736200] p-4 shadow-xs flex flex-col gap-2.5 border border-black/10">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704"
                alt="Sofia"
                className="w-9 h-9 rounded-full object-cover ring-2 ring-white"
              />
              <div className="flex flex-col">
                <span className="font-headline font-bold text-sm text-[#1C1B1B]">Sofia Miller</span>
                <span className="text-[11px] font-medium opacity-85">Mindful Rhythm Buddy</span>
              </div>
            </div>
            <span className="text-[10px] font-mono opacity-80">Yesterday</span>
          </div>

          <div className="rounded-2xl bg-white/80 p-2.5 flex items-center justify-between gap-2 shadow-xs text-[#1C1B1B]">
            <div className="flex items-center gap-2 min-w-0">
              <span className="w-6 h-6 rounded-full bg-[#FCE275] flex items-center justify-center text-xs shrink-0">
                💧
              </span>
              <p className="text-xs truncate">
                Shared a completed ritual: <strong>Hydration Goal (+500ml)</strong>
              </p>
            </div>
            <span className="material-symbols-outlined text-[18px] text-[#736200]">verified</span>
          </div>

          <div className="flex items-center justify-between pt-0.5">
            <span className="px-2.5 py-0.5 rounded-full bg-white text-[10px] font-bold text-[#1C1B1B]">
              ✨ 7-Day Streak
            </span>
            <span className="text-[10px] font-bold">Sent cheer 👋</span>
          </div>
        </article>

        {/* Channel 3: Mom & Family Hub */}
        <article className={`w-full rounded-3xl p-4 shadow-xs flex flex-col gap-2.5 border transition-colors ${
          oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-white border-black/[0.04] text-[#1C1B1B]'
        }`}>
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-9 h-9 rounded-2xl bg-[#FFD8EC] text-[#864B70] flex items-center justify-center">
                <span className="material-symbols-outlined text-[18px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                  favorite
                </span>
              </div>
              <div className="flex flex-col">
                <span className="font-headline font-bold text-sm">Mom & Family Hub</span>
                <span className="text-[11px] text-neutral-500">Scheduled Morning Routine</span>
              </div>
            </div>
            <span className="text-[10px] font-mono text-neutral-400">May 14</span>
          </div>

          <div className={`rounded-2xl p-2.5 flex items-center justify-between gap-2 ${
            oledMode ? 'bg-neutral-950' : 'bg-[#F6F3F2]'
          }`}>
            <div className="flex items-center gap-2 min-w-0">
              <span className="material-symbols-outlined text-[#864B70] text-[16px]">alarm</span>
              <p className="text-xs truncate">
                Scheduled Voice Wake-up set for <strong>Sunday 08:30 AM</strong>
              </p>
            </div>
            <span className="px-2 py-0.5 rounded-full bg-[#FFD8EC] text-[#864B70] text-[10px] font-bold shrink-0">
              Ready
            </span>
          </div>

          <div className="flex items-center justify-between pt-0.5">
            <span className="text-[10px] text-neutral-500 font-semibold">3 family members</span>
            <span className="text-[10px] font-bold text-[#864B70] dark:text-[#F5ACD7]">
              Tap to listen memo
            </span>
          </div>
        </article>
      </section>

      {/* 5. Floating Walkie-Talkie & Live Audio Action Triggers */}
      <section className="pt-1 flex flex-col gap-2">
        <div className={`w-full rounded-3xl p-2.5 shadow-sm flex items-center gap-2 border ${
          oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#EAE7E7] border-black/[0.04]'
        }`}>
          {/* Walkie Talkie Hold to Talk */}
          <button
            onMouseDown={handleWalkieTalkiePress}
            onMouseUp={handleWalkieTalkieRelease}
            onTouchStart={handleWalkieTalkiePress}
            onTouchEnd={handleWalkieTalkieRelease}
            aria-label="Hold to talk walkie-talkie"
            className={`flex-1 h-13 py-3 px-4 rounded-full font-headline text-xs font-bold flex items-center justify-center gap-2 shadow-sm transition-all select-none cursor-pointer ${
              isWalkieTalkieActive
                ? 'bg-[#864B70] text-white scale-98 ring-4 ring-[#F5ACD7]'
                : 'bg-[#1C1B1B] text-white active:scale-95'
            }`}
          >
            <div className="w-6 h-6 rounded-full bg-[#F5ACD7] text-[#1C1B1B] flex items-center justify-center">
              <span className="material-symbols-outlined text-[15px]">mic</span>
            </div>
            <span>{isWalkieTalkieActive ? 'Transmitting Live...' : 'Hold to Talk'}</span>
            <span className="text-[9px] px-1.5 py-0.5 rounded-full bg-white/20 uppercase font-mono">
              Live
            </span>
          </button>

          {/* New Circle Trigger */}
          <button
            onClick={() => onNavigate('people')}
            aria-label="Create new circle"
            className="h-13 py-3 px-4 rounded-full bg-white dark:bg-neutral-800 text-[#1C1B1B] dark:text-white font-headline text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs active:scale-95 transition-transform"
          >
            <span className="material-symbols-outlined text-[16px]">add_circle</span>
            <span>New Circle</span>
          </button>
        </div>

        <div className="text-center pt-1">
          <p className="text-[10px] text-neutral-400 font-medium">
            🔒 End-to-end encrypted audio bubbles • Keep audio notes mindful & sweet
          </p>
        </div>
      </section>
    </div>
  );
};
