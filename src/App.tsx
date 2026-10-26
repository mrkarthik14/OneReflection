import React, { useState, useEffect } from 'react';
import { ScreenId } from './types';
import { storageService } from './services/storageService';
import { Header } from './components/Header';
import { BottomNav } from './components/BottomNav';
import { HomeView } from './views/HomeView';
import { AlarmsView } from './views/AlarmsView';
import { TasksView } from './views/TasksView';
import { ChatsView } from './views/ChatsView';
import { HabitsView } from './views/HabitsView';
import { RemindersView } from './views/RemindersView';
import { QuietHoursView } from './views/QuietHoursView';
import { PeopleView } from './views/PeopleView';
import { ProfileView } from './views/ProfileView';
import { VoiceStudioSheet } from './views/VoiceStudioSheet';
import { BedtimeSyncModal } from './views/BedtimeSyncModal';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<ScreenId>('home');
  const [oledMode, setOledMode] = useState<boolean>(false);
  const [voiceStudioOpen, setVoiceStudioOpen] = useState<boolean>(false);
  const [bedtimeSyncOpen, setBedtimeSyncOpen] = useState<boolean>(false);

  // Sync OLED mode with document class and background
  useEffect(() => {
    if (oledMode) {
      document.documentElement.classList.add('dark');
      document.body.style.backgroundColor = '#000000';
    } else {
      document.documentElement.classList.remove('dark');
      document.body.style.backgroundColor = '#FCF9F8';
    }
  }, [oledMode]);

  const handleToggleOLED = () => {
    const next = !oledMode;
    setOledMode(next);
    storageService.updateProfile({ oledPureBlackMode: next });
  };

  const handleSaveVoiceNote = (quote: string, duration: string) => {
    storageService.addAlarm({
      title: 'Voice Wake-up Memo',
      time: '07:30',
      period: 'AM',
      days: ['M', 'T', 'W', 'T', 'F'],
      enabled: true,
      category: 'shared',
      creatorName: 'Elena (You)',
      voiceSnippetQuote: quote,
      voiceSnippetDuration: duration,
      chimeSound: 'tibetan',
      isBreakthrough: true,
    });
  };

  const renderActiveScreen = () => {
    switch (currentScreen) {
      case 'home':
        return (
          <HomeView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'alarms':
        return (
          <AlarmsView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'tasks':
        return (
          <TasksView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'chats':
        return (
          <ChatsView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'habits':
        return (
          <HabitsView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'reminders':
        return (
          <RemindersView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'quiet-breakthrough':
        return (
          <QuietHoursView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
            onToggleOLED={handleToggleOLED}
          />
        );
      case 'people':
        return (
          <PeopleView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'profile':
        return (
          <ProfileView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
      case 'bedtime-sync':
        return (
          <QuietHoursView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
            onToggleOLED={handleToggleOLED}
          />
        );
      default:
        return (
          <HomeView
            onNavigate={setCurrentScreen}
            onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
            oledMode={oledMode}
          />
        );
    }
  };

  return (
    <div className={`min-h-screen w-full flex flex-col items-center transition-colors duration-300 ${
      oledMode ? 'bg-[#000000] text-white' : 'bg-[#FCF9F8] text-[#1C1B1B]'
    }`}>
      {/* Header Bar */}
      <Header
        currentScreen={currentScreen}
        onNavigate={(screen) => {
          if (screen === 'bedtime-sync') {
            setBedtimeSyncOpen(true);
          } else {
            setCurrentScreen(screen);
          }
        }}
        oledMode={oledMode}
        onToggleOLED={handleToggleOLED}
        onOpenVoiceStudio={() => setVoiceStudioOpen(true)}
      />

      {/* Main Responsive Mobile Viewport Container */}
      <main className="w-full max-w-md min-h-screen pt-20 pb-28 flex flex-col relative">
        {renderActiveScreen()}
      </main>

      {/* Floating Pill Bottom Navigation */}
      <BottomNav
        currentScreen={currentScreen}
        onNavigate={setCurrentScreen}
        onOpenQuickCreate={() => setVoiceStudioOpen(true)}
        oledMode={oledMode}
      />

      {/* Voice Studio Bottom Sheet */}
      <VoiceStudioSheet
        isOpen={voiceStudioOpen}
        onClose={() => setVoiceStudioOpen(false)}
        onSaveVoiceNote={handleSaveVoiceNote}
        oledMode={oledMode}
      />

      {/* Bedtime Sync & Exceptions Modal */}
      <BedtimeSyncModal
        isOpen={bedtimeSyncOpen}
        onClose={() => setBedtimeSyncOpen(false)}
        oledMode={oledMode}
      />
    </div>
  );
}
