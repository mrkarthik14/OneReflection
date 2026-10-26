import React, { useState } from 'react';
import { ScreenId } from '../types';

interface HeaderProps {
  currentScreen: ScreenId;
  onNavigate: (screen: ScreenId) => void;
  oledMode: boolean;
  onToggleOLED: () => void;
  onOpenVoiceStudio: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  currentScreen,
  onNavigate,
  oledMode,
  onToggleOLED,
  onOpenVoiceStudio,
}) => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);

  const getSubtitle = () => {
    switch (currentScreen) {
      case 'home':
        return 'Home';
      case 'alarms':
        return 'Alarms';
      case 'tasks':
        return 'Tasks & Shared Flows';
      case 'chats':
        return 'Chats & Circles';
      case 'people':
        return 'Inner Circle & Key';
      case 'habits':
        return 'Daily Rituals & Habits';
      case 'reminders':
        return 'Reminders & Calendar Planner';
      case 'quiet-breakthrough':
        return 'Bedtime Quiet Hours';
      case 'bedtime-sync':
        return 'Bedtime Sync & Exceptions';
      case 'profile':
        return 'Profile & Harmony';
      default:
        return 'Home';
    }
  };

  const screens: { id: ScreenId; label: string; icon: string; badge?: string }[] = [
    { id: 'home', label: 'Home Dashboard', icon: 'home' },
    { id: 'alarms', label: 'Wake-up & Shared Alarms', icon: 'alarm', badge: '07:30' },
    { id: 'tasks', label: 'Tasks & Shared Flows', icon: 'check_circle', badge: '7' },
    { id: 'chats', label: 'Chats & Circles', icon: 'chat_bubble', badge: 'Live' },
    { id: 'reminders', label: 'Moments & Reminders', icon: 'calendar_month' },
    { id: 'habits', label: 'Daily Rituals & Habits', icon: 'eco' },
    { id: 'quiet-breakthrough', label: 'Bedtime Quiet Hours (Breakthrough)', icon: 'bedtime', badge: '01:24 AM' },
    { id: 'people', label: 'People & Sync Key (QR Code)', icon: 'group' },
    { id: 'profile', label: 'Profile & Harmony', icon: 'person' },
  ];

  return (
    <header className={`fixed top-0 inset-x-0 z-40 backdrop-blur-xl transition-colors duration-300 pt-safe ${
      oledMode ? 'bg-black/90 border-b border-neutral-900 text-white' : 'bg-[#FCF9F8]/85 border-b border-black/[0.04] text-[#1C1B1B]'
    }`}>
      <div className="max-w-2xl mx-auto h-16 px-4 flex items-center justify-between">
        {/* Logo & Brand Wordmark */}
        <div 
          className="flex items-center gap-2.5 cursor-pointer active:scale-98 transition-transform"
          onClick={() => setMenuOpen(!menuOpen)}
          role="button"
          tabIndex={0}
          aria-label="Toggle Screen Navigator"
        >
          {/* OneReflection Brand Icon */}
          <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-[#F5ACD7] to-[#F9DF72] flex items-center justify-center p-1 shadow-sm">
            <span className="material-symbols-outlined text-[18px] text-[#313030]" style={{ fontVariationSettings: "'FILL' 1" }}>
              auto_awesome
            </span>
          </div>

          <div className="flex flex-col">
            <div className="flex items-center gap-1.5">
              <span className="font-headline font-bold text-[18px] tracking-tight leading-none">
                OneReflection
              </span>
              <span className="material-symbols-outlined text-[16px] opacity-60">
                {menuOpen ? 'expand_less' : 'expand_more'}
              </span>
            </div>
            <span className="text-[11px] font-medium opacity-70 leading-tight">
              {getSubtitle()}
            </span>
          </div>
        </div>

        {/* Right Actions: OLED Toggle, Notifications, Profile Avatar */}
        <div className="flex items-center gap-1.5">
          {/* OLED 0-Nit Quick Switch Pill */}
          <button
            onClick={onToggleOLED}
            aria-label="Toggle OLED Pure Black Mode"
            className={`flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold shadow-xs transition-all active:scale-95 cursor-pointer ${
              oledMode
                ? 'bg-[#313030] text-[#F9DF72] border border-[#F9DF72]/40'
                : 'bg-neutral-900 text-white hover:bg-neutral-800'
            }`}
          >
            <span className="material-symbols-outlined text-[14px]">
              {oledMode ? 'wb_sunny' : 'dark_mode'}
            </span>
            <span className="hidden sm:inline">OLED</span>
            <span className="w-1.5 h-1.5 rounded-full bg-[#F9DF72] animate-pulse"></span>
          </button>

          {/* Notifications Button */}
          <div className="relative">
            <button
              onClick={() => setNotificationOpen(!notificationOpen)}
              aria-label="Notifications"
              className={`w-10 h-10 rounded-full flex items-center justify-center transition-colors ${
                oledMode ? 'hover:bg-neutral-800 text-neutral-300' : 'hover:bg-neutral-200/60 text-[#50434A]'
              }`}
            >
              <span className="material-symbols-outlined text-[20px]">notifications</span>
              <span className="absolute top-2 right-2 w-2 h-2 rounded-full bg-[#864B70]"></span>
            </button>

            {notificationOpen && (
              <div className="absolute right-0 mt-2 w-72 rounded-2xl bg-white dark:bg-neutral-900 shadow-xl border border-neutral-200 dark:border-neutral-800 p-3 z-50 animate-in fade-in slide-in-from-top-2 text-[#1C1B1B] dark:text-white">
                <div className="flex items-center justify-between pb-2 border-b border-neutral-100 dark:border-neutral-800">
                  <span className="font-headline font-bold text-xs uppercase tracking-wider">Mindful Notifications</span>
                  <span className="text-[10px] text-neutral-500">Live</span>
                </div>
                <div className="py-2 flex flex-col gap-2">
                  <div className="p-2 rounded-xl bg-[#F5ACD7]/20 flex items-start gap-2">
                    <span className="material-symbols-outlined text-[16px] text-[#864B70] mt-0.5">mic</span>
                    <div className="flex flex-col text-xs">
                      <span className="font-semibold">Maya sent a wake-up memo</span>
                      <span className="text-[10px] text-neutral-500">"Remember to drink lemon water..."</span>
                    </div>
                  </div>
                  <div className="p-2 rounded-xl bg-[#F9DF72]/20 flex items-start gap-2">
                    <span className="material-symbols-outlined text-[16px] text-[#736200] mt-0.5">water_drop</span>
                    <div className="flex flex-col text-xs">
                      <span className="font-semibold">Sofia completed Hydration</span>
                      <span className="text-[10px] text-neutral-500">7-Day Streak achieved!</span>
                    </div>
                  </div>
                </div>
                <button
                  onClick={() => setNotificationOpen(false)}
                  className="w-full py-1 text-center text-xs font-semibold text-neutral-500 hover:text-neutral-900 dark:hover:text-neutral-200"
                >
                  Close
                </button>
              </div>
            )}
          </div>

          {/* User Profile Avatar */}
          <button
            onClick={() => onNavigate('profile')}
            aria-label="User Profile"
            className="w-9 h-9 rounded-full overflow-hidden ring-2 ring-transparent hover:ring-[#F5ACD7] transition-all active:scale-95"
          >
            <img
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuBE7jpIocCISc-CQYfgAnahVZmLt7VB9UJKlTFKJX9JEBM0KeqWHrnY0EOlX7Xr016w-StCO-DfHViu0fGfhqLc5xXymxW-Idb1KIskGYbnQtfZt9R24CzmTiiG97t1oqT6BRybyaDZrINztAvlimWlhHUhwdR_IyfwE_j6uA1V6l9K1-IiXMA6jRa5-JRXxNhlmHpahWf3zorVKk7seurAn3RnMPBzb_UQpUlVYYBvZMwd1oKmMCq0"
              alt="Elena Vance"
              className="w-full h-full object-cover"
            />
          </button>
        </div>
      </div>

      {/* Quick Screen Switcher Drawer */}
      {menuOpen && (
        <div className="fixed inset-x-0 top-16 bg-white/95 dark:bg-neutral-950/95 backdrop-blur-2xl border-b border-neutral-200 dark:border-neutral-800 shadow-2xl p-4 z-50 animate-in fade-in slide-in-from-top-2 text-[#1C1B1B] dark:text-white">
          <div className="max-w-2xl mx-auto flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold uppercase tracking-wider text-neutral-500">
                Explore OneReflection Screens
              </span>
              <button
                onClick={() => setMenuOpen(false)}
                className="w-7 h-7 rounded-full bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-xs"
              >
                ✕
              </button>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
              {screens.map((s) => (
                <button
                  key={s.id}
                  onClick={() => {
                    onNavigate(s.id);
                    setMenuOpen(false);
                  }}
                  className={`p-2.5 rounded-2xl flex items-center gap-2 text-left transition-all active:scale-95 ${
                    currentScreen === s.id
                      ? 'bg-[#1C1B1B] text-white shadow-sm'
                      : 'bg-neutral-100/80 dark:bg-neutral-900 hover:bg-neutral-200 dark:hover:bg-neutral-800 text-neutral-800 dark:text-neutral-200'
                  }`}
                >
                  <span className="material-symbols-outlined text-[18px]">
                    {s.icon}
                  </span>
                  <div className="flex-1 min-w-0">
                    <span className="font-headline font-semibold text-xs block truncate leading-tight">
                      {s.label}
                    </span>
                  </div>
                  {s.badge && (
                    <span className="text-[9px] px-1.5 py-0.5 rounded-full bg-[#F5ACD7] text-[#1C1B1B] font-bold">
                      {s.badge}
                    </span>
                  )}
                </button>
              ))}
            </div>

            {/* Additional Modals / Sheets triggers */}
            <div className="pt-2 border-t border-neutral-100 dark:border-neutral-800 flex items-center justify-between gap-2">
              <button
                onClick={() => {
                  onOpenVoiceStudio();
                  setMenuOpen(false);
                }}
                className="flex-1 py-2 px-3 rounded-full bg-[#F5ACD7] text-[#1C1B1B] font-semibold text-xs flex items-center justify-center gap-1.5 shadow-sm active:scale-95"
              >
                <span className="material-symbols-outlined text-[16px]">mic</span>
                <span>Open Voice Studio</span>
              </button>
              <button
                onClick={() => {
                  onNavigate('bedtime-sync');
                  setMenuOpen(false);
                }}
                className="flex-1 py-2 px-3 rounded-full bg-[#F9DF72] text-[#736200] font-semibold text-xs flex items-center justify-center gap-1.5 shadow-sm active:scale-95"
              >
                <span className="material-symbols-outlined text-[16px]">tune</span>
                <span>Bedtime Rules Modal</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};
