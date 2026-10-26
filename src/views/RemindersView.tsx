import React, { useState } from 'react';
import { audioService } from '../services/audioService';
import { storageService } from '../services/storageService';
import { ScreenId, ReminderEvent } from '../types';

interface RemindersViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export const RemindersView: React.FC<RemindersViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const [activeTab, setActiveTab] = useState<'day' | 'week' | 'month'>('day');
  const [reminders, setReminders] = useState<ReminderEvent[]>(storageService.getReminders());
  const [heroDone, setHeroDone] = useState(false);
  const [activeDay, setActiveDay] = useState(16);

  const days = [
    { label: 'MON', num: 13, dots: 1 },
    { label: 'TUE', num: 14, dots: 2 },
    { label: 'WED', num: 15, dots: 1 },
    { label: 'THU', num: 16, dots: 3 },
    { label: 'FRI', num: 17, dots: 1 },
    { label: 'SAT', num: 18, dots: 1 },
    { label: 'SUN', num: 19, dots: 1 },
  ];

  const handleToggleEvent = (id: string) => {
    audioService.playChime('click');
    storageService.toggleReminder(id);
    setReminders(storageService.getReminders());
  };

  return (
    <div className="flex flex-col w-full px-4 pb-12 gap-y-4">
      {/* 1. Header & Context */}
      <section className="flex flex-col gap-1 pt-2">
        <div className="flex items-center justify-between">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#F9DF72] text-[#736200] font-bold text-[10px] shadow-xs rotate-[-1deg]">
            <span className="material-symbols-outlined text-[14px]">auto_awesome</span>
            <span>SYNCHRONIZED PLANNER • MAY 2024</span>
          </div>

          <div className="flex items-center gap-1 text-xs font-semibold text-neutral-500">
            <span className="w-2 h-2 rounded-full bg-[#6F5D00] dark:bg-[#F9DF72] animate-pulse"></span>
            <span>Live Sync</span>
          </div>
        </div>

        <div className="mt-1">
          <h1 className={`font-headline text-3xl font-bold tracking-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Moments & Reminders.
          </h1>
          <p className={`text-xs ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
            Gentle nudges, upcoming shared milestones, and timely reflections.
          </p>
        </div>
      </section>

      {/* 2. View Switcher & Date Picker */}
      <section className="flex items-center justify-between gap-2">
        <div className={`p-1 rounded-full flex gap-1 ${oledMode ? 'bg-neutral-900' : 'bg-[#EAE7E7]'}`}>
          {(['day', 'week', 'month'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => {
                audioService.playChime('click');
                setActiveTab(tab);
              }}
              className={`px-4 py-1.5 rounded-full text-xs font-bold capitalize transition-all ${
                activeTab === tab
                  ? 'bg-[#1C1B1B] text-white shadow-xs'
                  : 'text-neutral-500 hover:text-neutral-900 dark:hover:text-white'
              }`}
            >
              {tab}
            </button>
          ))}
        </div>

        <button className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-xs font-bold shadow-xs border ${
          oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-white border-black/[0.04] text-[#1C1B1B]'
        }`}>
          <span className="material-symbols-outlined text-[16px] text-[#864B70] dark:text-[#F5ACD7]">
            calendar_month
          </span>
          <span>May 13–19</span>
          <span className="material-symbols-outlined text-[16px] text-neutral-400">expand_more</span>
        </button>
      </section>

      {/* 3. Horizontal Weekly Interactive Strip */}
      <section className={`p-2 rounded-3xl flex items-center justify-between gap-1 border ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F0EDED] border-black/[0.04]'
      }`}>
        {days.map((d) => {
          const isActive = d.num === activeDay;
          return (
            <button
              key={d.num}
              onClick={() => {
                audioService.playChime('click');
                setActiveDay(d.num);
              }}
              className={`flex-1 flex flex-col items-center py-2 rounded-2xl transition-all ${
                isActive
                  ? 'bg-[#F9DF72] text-[#736200] shadow-sm font-bold scale-105'
                  : 'text-neutral-500 hover:text-neutral-800 dark:hover:text-white'
              }`}
            >
              <span className="text-[10px] font-bold">{d.label}</span>
              <span className="font-headline text-sm font-bold mt-0.5">{d.num}</span>
              <div className="flex items-center gap-0.5 mt-1 h-1">
                {Array.from({ length: d.dots }).map((_, i) => (
                  <span
                    key={i}
                    className={`w-1 h-1 rounded-full ${isActive ? 'bg-[#736200]' : 'bg-[#864B70]'}`}
                  />
                ))}
              </div>
            </button>
          );
        })}
      </section>

      {/* 4. Hero Next-Up Bento Card (Candy Pink) */}
      <section>
        <div className="relative overflow-hidden bg-[#F5ACD7] text-[#1C1B1B] p-5 rounded-3xl shadow-sm border border-black/10">
          <div className="flex items-center justify-between mb-3 relative z-10">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/90 text-[#1C1B1B] text-[10px] font-bold shadow-xs">
              <span className="w-1.5 h-1.5 rounded-full bg-[#864B70] animate-ping" />
              NEXT UP IN 45 MIN
            </div>

            <div className="flex items-center gap-1 text-[10px] font-bold bg-white/70 px-2.5 py-1 rounded-full">
              <span className="material-symbols-outlined text-[14px]">sync_saved_locally</span>
              <span>Google Cal Sync</span>
            </div>
          </div>

          <div className="relative z-10">
            <div className="flex items-baseline gap-2">
              <span className="font-headline text-3xl font-bold tracking-tight">11:30 AM</span>
              <span className="text-xs uppercase font-bold tracking-wider text-[#753C61]">TODAY</span>
            </div>

            <h2 className="font-headline text-lg font-bold mt-1 leading-snug">
              Dentist & Oral Wellness Checkup
            </h2>
            <p className="text-xs text-[#50434A] mt-1">
              Dr. Turner's Clinic • Bring updated allergy & nightguard notes.
            </p>

            <div className="mt-3 flex items-center gap-2 p-2 bg-white/80 rounded-2xl">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                alt="Maya"
                className="w-7 h-7 rounded-full object-cover ring-2 ring-white"
              />
              <p className="text-xs font-semibold truncate flex-1">
                Maya will send a voice ping 15m prior to departure
              </p>
              <button
                onClick={() => audioService.playChime('tibetan')}
                className="w-7 h-7 rounded-full bg-white flex items-center justify-center text-[#864B70] shadow-xs"
              >
                <span className="material-symbols-outlined text-[16px]">volume_up</span>
              </button>
            </div>

            <div className="mt-4 flex items-center gap-2">
              <button
                onClick={() => {
                  audioService.playChime('click');
                  setHeroDone(!heroDone);
                }}
                className={`flex-1 h-12 rounded-full font-headline text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs active:scale-95 transition-all ${
                  heroDone ? 'bg-neutral-800 text-white line-through' : 'bg-[#1C1B1B] text-white'
                }`}
              >
                <span className="material-symbols-outlined text-[18px]">
                  {heroDone ? 'task_alt' : 'check_circle'}
                </span>
                <span>{heroDone ? 'Completed' : 'Mark Done'}</span>
              </button>

              <button
                onClick={() => audioService.playChime('click')}
                className="h-12 px-5 rounded-full bg-white text-[#1C1B1B] text-xs font-bold shadow-xs hover:bg-neutral-50 active:scale-95 transition-all flex items-center gap-1"
              >
                <span className="material-symbols-outlined text-[16px]">snooze</span>
                <span>+10m</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* 5. Timeline for Today */}
      <section className="flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h3 className={`font-headline text-base font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Timeline For Today
            </h3>
            <span className="text-[10px] font-bold px-2.5 py-0.5 rounded-full bg-neutral-200 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300">
              4 Left
            </span>
          </div>
          <button className="text-xs font-bold text-[#864B70] dark:text-[#F5ACD7] flex items-center gap-0.5 hover:underline">
            Full Agenda <span className="material-symbols-outlined text-[15px]">arrow_forward</span>
          </button>
        </div>

        {/* Timeline item 1: Sourdough errand (Butter Yellow) */}
        <article className="bg-[#F9DF72] text-[#1C1B1B] p-4 rounded-3xl shadow-xs border border-black/10 flex flex-col gap-2.5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-0.5 rounded-full bg-white/90 text-xs font-bold shadow-xs">
                01:00 PM
              </span>
              <span className="text-[10px] font-bold uppercase tracking-wide opacity-80">
                Shared Errand
              </span>
            </div>
            <button
              onClick={() => handleToggleEvent('rem-2')}
              className="w-7 h-7 rounded-full bg-white flex items-center justify-center text-[#1C1B1B] shadow-xs"
            >
              <span className="material-symbols-outlined text-[16px]">check</span>
            </button>
          </div>

          <div>
            <h4 className="font-headline text-base font-bold leading-tight">
              Pick up Artisan Sourdough & Rolled Oats
            </h4>
            <p className="text-xs text-neutral-700 mt-0.5 font-medium">
              Golden Wheat Bakery • Maya added 2 items to the bag.
            </p>
          </div>

          <div 
            onClick={() => audioService.playChime('tibetan')}
            className="p-2.5 bg-white rounded-2xl flex items-center justify-between gap-2 shadow-xs cursor-pointer hover:bg-neutral-50 transition-colors"
          >
            <div className="flex items-center gap-2 min-w-0">
              <span className="w-7 h-7 rounded-full bg-[#1C1B1B] text-white flex items-center justify-center shrink-0">
                <span className="material-symbols-outlined text-[16px]">play_arrow</span>
              </span>
              <div className="min-w-0">
                <p className="text-xs font-bold truncate">"Remember seeded sourdough!"</p>
                <p className="text-[10px] text-neutral-500">Voice memo from Maya • 0:14</p>
              </div>
            </div>
            <span className="text-xs text-[#736200] font-bold">0:14 🎙️</span>
          </div>
        </article>

        {/* Timeline item 2: Studio Architecture Review (Lavender Blue) */}
        <article className="bg-[#DAE2FF] text-[#091A3E] p-4 rounded-3xl shadow-xs border border-[#4F5D85]/20 flex flex-col gap-2.5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-0.5 rounded-full bg-white/90 text-xs font-bold shadow-xs">
                03:15 PM
              </span>
              <span className="text-[10px] font-bold uppercase tracking-wide text-[#38466C]">
                Studio Review
              </span>
            </div>
            <button
              onClick={() => handleToggleEvent('rem-3')}
              className="w-7 h-7 rounded-full bg-white flex items-center justify-center shadow-xs"
            >
              <span className="material-symbols-outlined text-[16px] text-neutral-400">
                radio_button_unchecked
              </span>
            </button>
          </div>

          <div>
            <h4 className="font-headline text-base font-bold leading-tight">
              Studio Architecture Review & Sprint Retro
            </h4>
            <p className="text-xs text-[#38466C] mt-0.5">
              Google Meet room linked • Presenting the bento layout components.
            </p>
          </div>

          <div className="flex items-center justify-between pt-1">
            <div className="flex items-center gap-1.5">
              <span className="text-xs font-bold">Leo, Sam & Team</span>
            </div>
            <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white text-xs font-bold shadow-xs">
              <span className="material-symbols-outlined text-[14px]">videocam</span>
              Video Link
            </span>
          </div>
        </article>

        {/* Timeline item 3: Sunset Walk (Matcha Green) */}
        <article className={`p-4 rounded-3xl shadow-xs border flex flex-col gap-2.5 transition-colors ${
          oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-[#EAE7E7] border-black/[0.04] text-[#1C1B1B]'
        }`}>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-0.5 rounded-full bg-white dark:bg-neutral-800 text-xs font-bold shadow-xs">
                05:45 PM
              </span>
              <span className="text-[10px] font-bold uppercase tracking-wide text-neutral-500">
                Daily Habit
              </span>
            </div>
            <button
              onClick={() => handleToggleEvent('rem-4')}
              className="w-7 h-7 rounded-full bg-white dark:bg-neutral-800 flex items-center justify-center shadow-xs"
            >
              <span className="material-symbols-outlined text-[16px] text-neutral-400">
                radio_button_unchecked
              </span>
            </button>
          </div>

          <div>
            <h4 className="font-headline text-base font-bold leading-tight">
              Sunset Walk & Balcony Plant Watering
            </h4>
            <p className="text-xs text-neutral-500 mt-0.5">
              20 minutes offline reflection • Hydrate Monstera and ferns.
            </p>
          </div>

          <div className="flex items-center justify-between pt-1">
            <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white dark:bg-neutral-800 text-xs font-bold shadow-xs">
              <span className="material-symbols-outlined text-[14px] text-[#6F5D00] dark:text-[#F9DF72]">
                notifications_active
              </span>
              Gentle Tibetan Chime
            </span>
            <span className="text-xs font-bold text-[#864B70] dark:text-[#F5ACD7]">
              14-day streak 🔥
            </span>
          </div>
        </article>
      </section>

      {/* 6. Upcoming Highlights / Countdowns */}
      <section className={`p-5 rounded-3xl shadow-xs border transition-colors ${
        oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-[#F6F3F2] border-black/[0.04] text-[#1C1B1B]'
      }`}>
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-1.5">
            <span className="material-symbols-outlined text-[20px] text-[#864B70] dark:text-[#F5ACD7]">
              celebration
            </span>
            <h3 className="font-headline text-base font-bold">Upcoming Highlights</h3>
          </div>
          <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-white dark:bg-neutral-800 text-neutral-500">
            May / Jun
          </span>
        </div>

        <div className="flex flex-col gap-2.5">
          {/* Milestone 1: Birthday */}
          <div className="p-3.5 bg-white dark:bg-neutral-950 rounded-2xl flex items-center justify-between gap-3 shadow-xs">
            <div className="flex items-center gap-3 min-w-0">
              <div className="w-12 h-12 bg-[#F9DF72] text-[#736200] rounded-2xl flex flex-col items-center justify-center font-bold rotate-2 shrink-0">
                <span className="text-[9px] uppercase leading-none">In</span>
                <span className="font-headline text-base leading-tight">6</span>
                <span className="text-[9px] leading-none">Days</span>
              </div>
              <div className="min-w-0">
                <p className="font-headline text-sm font-bold truncate">Maya's Birthday 🎉</p>
                <p className="text-[11px] text-neutral-500 truncate">
                  May 22 • Surprise voice alarm scheduled
                </p>
              </div>
            </div>
            <span className="material-symbols-outlined text-[20px] text-[#864B70] dark:text-[#F5ACD7]">
              event_upcoming
            </span>
          </div>

          {/* Milestone 2: Product Sprint Launch */}
          <div className="p-3.5 bg-white dark:bg-neutral-950 rounded-2xl flex items-center justify-between gap-3 shadow-xs">
            <div className="flex items-center gap-3 min-w-0">
              <div className="w-12 h-12 bg-[#DAE2FF] text-[#091A3E] rounded-2xl flex flex-col items-center justify-center font-bold -rotate-1 shrink-0">
                <span className="text-[9px] uppercase leading-none">In</span>
                <span className="font-headline text-base leading-tight">12</span>
                <span className="text-[9px] leading-none">Days</span>
              </div>
              <div className="min-w-0">
                <p className="font-headline text-sm font-bold truncate">Product Sprint 14 Launch 🚀</p>
                <p className="text-[11px] text-neutral-500 truncate">
                  May 28 • Shared design team milestone
                </p>
              </div>
            </div>
            <span className="material-symbols-outlined text-[20px] text-[#4F5D85]">flag</span>
          </div>
        </div>
      </section>

      {/* 7. Bottom Action Bar */}
      <section className="flex items-center gap-2 pt-1">
        <button
          onClick={onOpenVoiceStudio}
          className="flex-1 h-13 rounded-full bg-[#1C1B1B] text-white font-headline text-xs font-bold flex items-center justify-center gap-2 shadow-md active:scale-98 transition-all"
        >
          <span className="material-symbols-outlined text-[20px] text-[#FCE275]">add_circle</span>
          <span>New Reminder or Event</span>
        </button>

        <button
          onClick={onOpenVoiceStudio}
          aria-label="Quick voice scheduling"
          className="w-13 h-13 rounded-full bg-[#F5ACD7] text-[#1C1B1B] flex items-center justify-center shadow-md hover:scale-105 active:scale-95 transition-all shrink-0"
        >
          <span className="material-symbols-outlined text-[22px]">mic</span>
        </button>
      </section>
    </div>
  );
};
