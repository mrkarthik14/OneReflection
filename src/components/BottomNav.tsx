import React from 'react';
import { ScreenId } from '../types';
import { audioService } from '../services/audioService';

interface BottomNavProps {
  currentScreen: ScreenId;
  onNavigate: (screen: ScreenId) => void;
  onOpenQuickCreate: () => void;
  oledMode: boolean;
}

export const BottomNav: React.FC<BottomNavProps> = ({
  currentScreen,
  onNavigate,
  onOpenQuickCreate,
  oledMode,
}) => {
  const handleTabClick = (screen: ScreenId) => {
    audioService.playChime('click');
    onNavigate(screen);
  };

  const handlePlusClick = () => {
    audioService.playChime('tibetan');
    onOpenQuickCreate();
  };

  return (
    <div className="fixed bottom-0 inset-x-0 z-40 pb-safe px-4 pointer-events-none">
      <nav 
        className={`pointer-events-auto mx-auto mb-2 max-w-md h-16 rounded-full shadow-[0_10px_30px_rgba(23,23,23,0.25)] flex items-center justify-between px-3 relative border transition-colors ${
          oledMode ? 'bg-[#121214] border-neutral-800' : 'bg-[#1C1B1B] border-white/10'
        }`}
      >
        {/* Tab 1: Home */}
        <button
          onClick={() => handleTabClick('home')}
          aria-label="Home"
          className={`flex flex-col items-center justify-center min-w-[48px] h-12 transition-all group ${
            currentScreen === 'home'
              ? 'text-white font-semibold'
              : 'text-[#E5E2E1]/60 hover:text-white'
          }`}
        >
          <span 
            className="material-symbols-outlined text-[22px]" 
            style={{ fontVariationSettings: currentScreen === 'home' ? "'FILL' 1" : "'FILL' 0" }}
          >
            home
          </span>
          <span className="text-[10px] tracking-tight mt-0.5">Home</span>
          {currentScreen === 'home' && (
            <span className="w-1.5 h-1.5 rounded-full bg-[#F5ACD7] mt-0.5 shadow-sm"></span>
          )}
        </button>

        {/* Tab 2: Alarms */}
        <button
          onClick={() => handleTabClick('alarms')}
          aria-label="Alarms"
          className={`flex flex-col items-center justify-center min-w-[48px] h-12 transition-all group ${
            currentScreen === 'alarms'
              ? 'text-white font-semibold'
              : 'text-[#E5E2E1]/60 hover:text-white'
          }`}
        >
          <span 
            className="material-symbols-outlined text-[22px]"
            style={{ fontVariationSettings: currentScreen === 'alarms' ? "'FILL' 1" : "'FILL' 0" }}
          >
            schedule
          </span>
          <span className="text-[10px] tracking-tight mt-0.5">Alarms</span>
          {currentScreen === 'alarms' && (
            <span className="w-1.5 h-1.5 rounded-full bg-[#F5ACD7] mt-0.5 shadow-sm"></span>
          )}
        </button>

        {/* Center Floating Plus Action */}
        <div className="relative flex items-center justify-center">
          <button
            onClick={handlePlusClick}
            aria-label="Create new voice entry or task"
            className="w-14 h-14 -mt-6 rounded-full bg-[#F5ACD7] text-[#1C1B1B] flex items-center justify-center shadow-[0_8px_20px_rgba(245,172,215,0.45)] hover:scale-105 active:scale-95 transition-transform cursor-pointer group"
          >
            <span className="material-symbols-outlined text-[28px] font-bold group-hover:rotate-90 transition-transform duration-200">
              add
            </span>
          </button>
        </div>

        {/* Tab 3: Tasks */}
        <button
          onClick={() => handleTabClick('tasks')}
          aria-label="Tasks"
          className={`flex flex-col items-center justify-center min-w-[48px] h-12 transition-all group ${
            currentScreen === 'tasks'
              ? 'text-white font-semibold'
              : 'text-[#E5E2E1]/60 hover:text-white'
          }`}
        >
          <span 
            className="material-symbols-outlined text-[22px]"
            style={{ fontVariationSettings: currentScreen === 'tasks' ? "'FILL' 1" : "'FILL' 0" }}
          >
            check_circle
          </span>
          <span className="text-[10px] tracking-tight mt-0.5">Tasks</span>
          {currentScreen === 'tasks' && (
            <span className="w-1.5 h-1.5 rounded-full bg-[#F5ACD7] mt-0.5 shadow-sm"></span>
          )}
        </button>

        {/* Tab 4: Chats */}
        <button
          onClick={() => handleTabClick('chats')}
          aria-label="Chats"
          className={`flex flex-col items-center justify-center min-w-[48px] h-12 transition-all group ${
            currentScreen === 'chats'
              ? 'text-white font-semibold'
              : 'text-[#E5E2E1]/60 hover:text-white'
          }`}
        >
          <span 
            className="material-symbols-outlined text-[22px]"
            style={{ fontVariationSettings: currentScreen === 'chats' ? "'FILL' 1" : "'FILL' 0" }}
          >
            chat_bubble
          </span>
          <span className="text-[10px] tracking-tight mt-0.5">Chats</span>
          {currentScreen === 'chats' && (
            <span className="w-1.5 h-1.5 rounded-full bg-[#F5ACD7] mt-0.5 shadow-sm"></span>
          )}
        </button>

        {/* Tab 5: Circle / People */}
        <button
          onClick={() => handleTabClick('people')}
          aria-label="Circle"
          className={`flex flex-col items-center justify-center min-w-[48px] h-12 transition-all group ${
            currentScreen === 'people'
              ? 'text-white font-semibold'
              : 'text-[#E5E2E1]/60 hover:text-white'
          }`}
        >
          <span 
            className="material-symbols-outlined text-[22px]"
            style={{ fontVariationSettings: currentScreen === 'people' ? "'FILL' 1" : "'FILL' 0" }}
          >
            group
          </span>
          <span className="text-[10px] tracking-tight mt-0.5">Circle</span>
          {currentScreen === 'people' && (
            <span className="w-1.5 h-1.5 rounded-full bg-[#F5ACD7] mt-0.5 shadow-sm"></span>
          )}
        </button>
      </nav>
    </div>
  );
};
