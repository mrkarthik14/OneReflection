import React, { useState } from 'react';
import { WaveformVisualizer } from '../components/WaveformVisualizer';
import { audioService } from '../services/audioService';
import { storageService } from '../services/storageService';
import { ScreenId } from '../types';

interface HabitsViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export const HabitsView: React.FC<HabitsViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const [hydration, setHydration] = useState(1.8);
  const [isPlayingJournal, setIsPlayingJournal] = useState(false);
  const [sprintActive, setSprintActive] = useState(false);
  const [sprintMinutes, setSprintMinutes] = useState(45);
  const [walkChecked, setWalkChecked] = useState(false);

  const handleAddHydration = (amount: number) => {
    audioService.playChime('water');
    const newVal = Number((hydration + amount).toFixed(2));
    setHydration(newVal);
    storageService.incrementHydration(amount * 1000);
  };

  const handleToggleJournal = () => {
    if (!isPlayingJournal) {
      audioService.playChime('solfeggio');
      setIsPlayingJournal(true);
      setTimeout(() => setIsPlayingJournal(false), 4000);
    } else {
      audioService.stopCurrentPlayback();
      setIsPlayingJournal(false);
    }
  };

  const handleToggleSprint = () => {
    audioService.playChime('click');
    setSprintActive(!sprintActive);
  };

  const handleToggleWalk = () => {
    audioService.playChime('click');
    setWalkChecked(!walkChecked);
  };

  return (
    <div className="flex flex-col w-full px-4 pb-12 gap-y-4">
      {/* 1. Header Intro */}
      <section className="flex flex-col gap-1 pt-2">
        <div className="flex items-center justify-between">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#F9DF72] text-[#736200] font-bold text-[10px]">
            <span className="material-symbols-outlined text-[15px]">eco</span>
            <span>MINDFUL FLOW</span>
          </div>

          <button
            onClick={() => onNavigate('reminders')}
            aria-label="Habit calendar overview"
            className={`w-9 h-9 rounded-full flex items-center justify-center transition-colors ${
              oledMode ? 'bg-neutral-900 text-white' : 'bg-[#EAE7E7] text-[#1C1B1B]'
            }`}
          >
            <span className="material-symbols-outlined text-[18px]">calendar_today</span>
          </button>
        </div>

        <div className="mt-1">
          <h1 className={`font-headline text-2xl font-bold tracking-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Daily Rituals & Habits
          </h1>
          <p className={`text-xs ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
            Tiny moments of focus, gentle rhythm.
          </p>
        </div>
      </section>

      {/* 2. Hero Bento Card: Today's Rhythm & 7-Day Bubble Strip */}
      <section className="relative overflow-hidden bg-[#FFD8EC] text-[#37062A] rounded-3xl p-5 flex flex-col gap-3.5 border border-black/10 shadow-xs">
        {/* Playful Rotated Sticker */}
        <div className="absolute -top-1 -right-1 rotate-6 bg-[#F9DF72] text-[#736200] px-3 py-1 rounded-full shadow-xs flex items-center gap-1">
          <span className="material-symbols-outlined text-[14px]">auto_awesome</span>
          <span className="text-[10px] font-bold">In Flow ✨</span>
        </div>

        <div className="flex items-center justify-between mt-1">
          <div className="flex flex-col">
            <span className="text-[10px] uppercase font-bold tracking-wider text-[#6B3358]">
              Today's Balance
            </span>
            <div className="flex items-baseline gap-1 mt-0.5">
              <span className="font-headline text-4xl font-bold text-[#37062A]">6</span>
              <span className="font-headline text-xl text-[#6B3358]">/ 8</span>
            </div>
            <span className="text-xs text-[#6B3358] font-medium">Habits Completed Today</span>
          </div>

          {/* Radial Completion Snapshot */}
          <div className="relative w-20 h-20 flex items-center justify-center">
            <svg className="w-full h-full -rotate-90" viewBox="0 0 72 72">
              <circle
                className="stroke-[#F5ACD7]/40"
                cx="36"
                cy="36"
                fill="transparent"
                r="30"
                strokeWidth="6"
              />
              <circle
                className="stroke-[#864B70]"
                cx="36"
                cy="36"
                fill="transparent"
                r="30"
                strokeDasharray="188.4"
                strokeDashoffset="47.1"
                strokeLinecap="round"
                strokeWidth="6"
              />
            </svg>
            <div className="absolute flex flex-col items-center justify-center text-center">
              <span className="font-headline text-base font-bold leading-none">75%</span>
              <span className="text-[9px] uppercase tracking-wider font-semibold opacity-80">score</span>
            </div>
          </div>
        </div>

        {/* 7-Day Mini Bubble Habit Strip */}
        <div className="bg-white/90 rounded-2xl p-2.5 flex items-center justify-between text-[#1C1B1B]">
          {[
            { day: 'M', done: true, color: 'bg-[#F9DF72] text-[#736200]' },
            { day: 'T', done: true, color: 'bg-[#F5ACD7] text-[#1C1B1B]' },
            { day: 'W', done: true, color: 'bg-[#DAE2FF] text-[#091A3E]' },
            { day: 'T', done: true, color: 'bg-[#F9DF72] text-[#736200]' },
            { day: 'F', done: true, color: 'bg-[#1C1B1B] text-white', today: true },
            { day: 'S', done: false, color: 'bg-neutral-200 text-neutral-500' },
            { day: 'S', done: false, color: 'bg-neutral-200 text-neutral-500' },
          ].map((item, idx) => (
            <div key={idx} className="flex flex-col items-center gap-1 flex-1">
              <span className={`text-[10px] font-bold ${item.today ? 'text-[#864B70]' : 'text-neutral-500'}`}>
                {item.day}
              </span>
              <div className={`w-7 h-7 rounded-full flex items-center justify-center shadow-xs text-xs font-bold ${item.color}`}>
                {item.done ? (item.today ? '★' : '✓') : '·'}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 3. Section Title */}
      <div className="flex items-center justify-between pt-1">
        <div className="flex items-center gap-1.5">
          <span className={`font-headline text-base font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Active Rituals
          </span>
          <span className="px-2 py-0.5 rounded-full bg-neutral-200 dark:bg-neutral-800 text-xs font-bold text-neutral-700 dark:text-neutral-300">
            4
          </span>
        </div>
        <button className="text-xs font-bold text-[#864B70] dark:text-[#F5ACD7] hover:underline">
          Reorder
        </button>
      </div>

      {/* 4. Bento Grid Habit Deck */}
      <div className="flex flex-col gap-3.5">
        {/* Card 1: Butter Yellow - Mindful Hydration */}
        <div className="bg-[#F9DF72] text-[#1C1B1B] rounded-3xl p-5 flex flex-col gap-3.5 shadow-xs border border-black/10">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-10 h-10 rounded-2xl bg-white/90 flex items-center justify-center text-[#736200] shadow-xs">
                <span className="material-symbols-outlined text-[22px]">water_drop</span>
              </div>
              <div>
                <h2 className="font-headline text-base font-bold leading-tight">Mindful Hydration</h2>
                <p className="text-xs text-[#736200] font-semibold">6 of 8 glasses logged</p>
              </div>
            </div>

            <span className="px-2.5 py-1 rounded-full bg-white/90 text-xs font-bold shadow-xs">
              💧 Regular
            </span>
          </div>

          <div className="flex items-baseline justify-between bg-white/85 p-3.5 rounded-2xl">
            <div className="flex flex-col">
              <span className="text-[10px] uppercase font-bold text-neutral-500">Current Volume</span>
              <span className="font-headline text-3xl font-bold leading-none mt-1">
                {hydration} <span className="text-sm font-normal text-neutral-500">/ 2.5 L</span>
              </span>
            </div>

            <div className="flex items-center gap-1.5">
              <button
                onClick={() => handleAddHydration(0.25)}
                className="h-9 px-3 rounded-full bg-[#1C1B1B] text-white text-xs font-bold active:scale-95 transition-transform"
              >
                +250ml
              </button>
              <button
                onClick={() => handleAddHydration(0.5)}
                className="h-9 px-3 rounded-full bg-[#1C1B1B] text-white text-xs font-bold active:scale-95 transition-transform"
              >
                +500ml
              </button>
            </div>
          </div>

          <div className="flex items-center justify-between text-xs text-[#736200] font-semibold">
            <div className="flex items-center gap-1">
              <span className="material-symbols-outlined text-[15px]">sync_alt</span>
              <span>Shared ping with Sofia</span>
            </div>
            <span>Goal 72%</span>
          </div>
        </div>

        {/* Card 2: Candy Pink - Gratitude & Voice Journal */}
        <div className="bg-[#FAB0DC] text-[#37062A] rounded-3xl p-5 flex flex-col gap-3.5 shadow-xs border border-black/10">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-10 h-10 rounded-2xl bg-white/90 flex items-center justify-center text-[#864B70] shadow-xs">
                <span className="material-symbols-outlined text-[22px]">mic</span>
              </div>
              <div>
                <h2 className="font-headline text-base font-bold leading-tight">
                  Gratitude & Voice Journal
                </h2>
                <span className="text-xs text-[#6B3358] font-semibold">Completed at 08:15 AM</span>
              </div>
            </div>

            <div className="w-8 h-8 rounded-full bg-[#1C1B1B] text-white flex items-center justify-center shadow-xs">
              <span className="material-symbols-outlined text-[18px]">done</span>
            </div>
          </div>

          <div className="bg-white/90 rounded-2xl p-3 flex items-center justify-between gap-3 text-[#1C1B1B] shadow-xs">
            <button
              onClick={handleToggleJournal}
              aria-label="Play recording"
              className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 active:scale-95 transition-all ${
                isPlayingJournal ? 'bg-[#864B70] text-white' : 'bg-[#1C1B1B] text-white'
              }`}
            >
              <span className="material-symbols-outlined text-[18px]">
                {isPlayingJournal ? 'pause' : 'play_arrow'}
              </span>
            </button>

            <div className="flex flex-col flex-1 min-w-0">
              <span className="text-xs font-bold truncate">Morning Reflection #42</span>
              <WaveformVisualizer isPlaying={isPlayingJournal} barCount={16} heightClass="h-4" />
            </div>

            <span className="text-[11px] font-mono text-neutral-500 shrink-0">0:35s</span>
          </div>

          <div className="flex items-center gap-1.5 text-xs text-[#6B3358] font-semibold">
            <span className="material-symbols-outlined text-[15px]">favorite</span>
            <span>Noted: "Grateful for crisp morning sunlight & warm mint tea."</span>
          </div>
        </div>

        {/* Card 3: Lavender Blue - 45m Deep Work Sprint */}
        <div className="bg-[#DAE2FF] text-[#091A3E] rounded-3xl p-5 flex flex-col gap-3.5 shadow-xs border border-[#4F5D85]/20">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-10 h-10 rounded-2xl bg-white/90 flex items-center justify-center text-[#4F5D85] shadow-xs">
                <span className="material-symbols-outlined text-[22px]">timer</span>
              </div>
              <div>
                <h2 className="font-headline text-base font-bold leading-tight">
                  45m Deep Work Sprint
                </h2>
                <span className="text-xs text-[#38466C] font-semibold">Session 2 of 3 today</span>
              </div>
            </div>

            <span className="px-2.5 py-1 rounded-full bg-white/90 text-xs font-bold text-[#091A3E] shadow-xs">
              Quiet mode
            </span>
          </div>

          <div className="flex items-center justify-between bg-white/80 p-3.5 rounded-2xl shadow-xs">
            <div className="flex items-center gap-2">
              <div className="flex -space-x-2 overflow-hidden">
                <span className="inline-block h-7 w-7 rounded-full bg-[#B3C1EF] text-[#091A3E] text-center leading-7 font-bold text-xs">
                  L
                </span>
                <span className="inline-block h-7 w-7 rounded-full bg-[#F5ACD7] text-[#1C1B1B] text-center leading-7 font-bold text-xs">
                  M
                </span>
              </div>
              <div className="flex flex-col ml-1">
                <span className="text-xs font-bold text-[#091A3E]">Leo & Maya</span>
                <span className="text-[10px] text-neutral-500">Also sprinting now</span>
              </div>
            </div>

            <button
              onClick={handleToggleSprint}
              className={`h-10 px-4 rounded-full font-headline text-xs font-bold flex items-center gap-1.5 active:scale-95 transition-all ${
                sprintActive ? 'bg-[#864B70] text-white' : 'bg-[#1C1B1B] text-white'
              }`}
            >
              <span className="material-symbols-outlined text-[16px]">
                {sprintActive ? 'pause' : 'play_circle'}
              </span>
              <span>{sprintActive ? `Active (${sprintMinutes}m)` : 'Start 45m'}</span>
            </button>
          </div>

          <div className="flex items-center justify-between text-xs text-[#38466C] font-semibold">
            <span>Target: Screenplay editing & scene sync</span>
            <span className="font-bold">Streak: 12d</span>
          </div>
        </div>

        {/* Card 4: Matcha Green - Evening Sunset Walk */}
        <div className={`rounded-3xl p-5 flex flex-col gap-3.5 shadow-xs border transition-colors ${
          oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-[#EAE7E7] border-black/[0.04] text-[#1C1B1B]'
        }`}>
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-10 h-10 rounded-2xl bg-white dark:bg-neutral-800 flex items-center justify-center text-[#6F5D00] dark:text-[#F9DF72] shadow-xs">
                <span className="material-symbols-outlined text-[22px]">directions_walk</span>
              </div>
              <div>
                <h2 className="font-headline text-base font-bold leading-tight">Evening Sunset Walk</h2>
                <span className="text-xs text-neutral-500">Scheduled for 06:45 PM</span>
              </div>
            </div>

            <button
              onClick={handleToggleWalk}
              className={`w-8 h-8 rounded-full flex items-center justify-center active:scale-90 transition-transform ${
                walkChecked ? 'bg-[#1C1B1B] text-white' : 'bg-white dark:bg-neutral-800 text-neutral-400'
              }`}
            >
              <span className="material-symbols-outlined text-[20px]">
                {walkChecked ? 'check_circle' : 'circle'}
              </span>
            </button>
          </div>

          <div className={`p-3.5 rounded-2xl flex flex-col gap-2 ${
            oledMode ? 'bg-neutral-950' : 'bg-white/90'
          }`}>
            <div className="flex items-baseline justify-between">
              <div className="flex flex-col">
                <span className="text-[10px] uppercase font-bold text-neutral-500">Daily Activity</span>
                <span className="font-headline text-2xl font-bold leading-none mt-1">
                  4,820 <span className="text-xs font-normal text-neutral-500">/ 6,000 steps</span>
                </span>
              </div>
              <span className="text-xs font-bold text-[#6F5D00] dark:text-[#F9DF72]">80% reached</span>
            </div>

            <div className="w-full h-2 rounded-full bg-neutral-200 dark:bg-neutral-800 overflow-hidden">
              <div className="h-full bg-[#864B70] rounded-full transition-all duration-500" style={{ width: '80%' }}></div>
            </div>
          </div>

          <div className="flex items-center justify-between text-xs text-neutral-500 font-semibold">
            <span className="flex items-center gap-1">
              <span className="material-symbols-outlined text-[15px]">wb_twilight</span>
              Sunset at 7:12 PM today
            </span>
            <span>Gentle stroll</span>
          </div>
        </div>
      </div>

      {/* 5. Weekly Momentum Chart */}
      <section className={`rounded-3xl p-5 flex flex-col gap-3 border transition-colors ${
        oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-[#F6F3F2] border-black/[0.04] text-[#1C1B1B]'
      }`}>
        <div className="flex items-center justify-between">
          <div>
            <h2 className="font-headline text-base font-bold">Weekly Momentum</h2>
            <p className="text-xs text-neutral-500">Playful rhythms, zero guilt.</p>
          </div>
          <span className="text-[10px] font-bold px-2.5 py-1 rounded-full bg-white dark:bg-neutral-800 text-neutral-500">
            Past 7 Days
          </span>
        </div>

        <div className="bg-white dark:bg-neutral-950 rounded-2xl p-4 flex flex-col gap-2 shadow-xs">
          <div className="flex items-end justify-between h-24 pt-2 px-1">
            {[
              { day: 'M', pct: '70%', color: 'bg-[#F9DF72]' },
              { day: 'T', pct: '85%', color: 'bg-[#F5ACD7]' },
              { day: 'W', pct: '60%', color: 'bg-[#DAE2FF]' },
              { day: 'T', pct: '90%', color: 'bg-[#FCE275]' },
              { day: 'F', pct: '75%', color: 'bg-[#864B70]', active: true },
              { day: 'S', pct: '40%', color: 'bg-neutral-300 dark:bg-neutral-800' },
              { day: 'S', pct: '30%', color: 'bg-neutral-300 dark:bg-neutral-800' },
            ].map((bar, i) => (
              <div key={i} className="flex flex-col items-center gap-2 h-full justify-end flex-1">
                <div
                  className={`w-6 rounded-t-xl transition-all ${bar.color}`}
                  style={{ height: bar.pct }}
                />
                <span className={`text-[10px] font-bold ${bar.active ? 'text-[#864B70] font-black' : 'text-neutral-500'}`}>
                  {bar.day}
                </span>
              </div>
            ))}
          </div>

          <div className="flex items-center gap-2 pt-2 text-xs text-neutral-500">
            <span className="material-symbols-outlined text-[16px] text-[#864B70]">volunteer_activism</span>
            <span className="text-[11px]">No pressure streaks — rest days and recovery time are celebrated.</span>
          </div>
        </div>
      </section>

      {/* 6. Primary Bottom Button */}
      <button
        onClick={onOpenVoiceStudio}
        className="w-full h-13 rounded-full bg-[#1C1B1B] text-white font-headline text-sm font-bold flex items-center justify-center gap-2 shadow-md active:scale-98 transition-all"
      >
        <span className="material-symbols-outlined text-[20px]">add_circle</span>
        <span>New Habit Ritual</span>
      </button>
    </div>
  );
};
