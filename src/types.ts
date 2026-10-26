export type ScreenId = 
  | 'home' 
  | 'alarms' 
  | 'tasks' 
  | 'chats' 
  | 'people' 
  | 'habits' 
  | 'reminders' 
  | 'bedtime-sync' 
  | 'quiet-breakthrough' 
  | 'profile';

export interface AlarmItem {
  id: string;
  title: string;
  time: string;
  period: 'AM' | 'PM';
  days: string[];
  enabled: boolean;
  category: 'shared' | 'studio' | 'selfcare' | 'sprint';
  creatorName?: string;
  creatorAvatar?: string;
  creatorRole?: string;
  voiceSnippetQuote?: string;
  voiceSnippetDuration?: string;
  chimeSound: string;
  isBreakthrough?: boolean;
}

export type CategoryColor = 'pink' | 'yellow' | 'lavender' | 'matcha' | 'lilac' | 'cyan' | 'slate';

export interface TaskCategory {
  id: string;
  name: string;
  color: CategoryColor;
  icon?: string;
  isDefault?: boolean;
}

export type TaskPriority = 'Low' | 'Medium' | 'High';

export interface TaskItem {
  id: string;
  title: string;
  description: string;
  category: string;
  categoryId?: string;
  dueTime: string;
  completed: boolean;
  subtasksDone?: number;
  subtasksTotal?: number;
  voiceBriefQuote?: string;
  voiceBriefDuration?: string;
  assignees?: { name: string; avatar?: string; initials?: string }[];
  codeBranch?: string;
  priority?: TaskPriority;
  streak?: number;
}

export interface HabitItem {
  id: string;
  title: string;
  category: 'hydration' | 'gratitude' | 'focus' | 'walk';
  currentValue: number;
  targetValue: number;
  unit: string;
  completedToday: boolean;
  streakDays: number;
  partnerSynced?: string;
  colorTheme: 'yellow' | 'pink' | 'cyan' | 'green';
  lastLogTime?: string;
}

export interface CircleMember {
  id: string;
  name: string;
  handle: string;
  role: string;
  status: string;
  avatar: string;
  isOnline: boolean;
  isAwake: boolean;
  permissions: {
    voiceAlarms: boolean;
    sharedTasks: boolean;
    rhythmPings: boolean;
    emergencyBreakthrough: boolean;
    gentleVoiceWhispers: boolean;
    wakeUpAudioQueue: boolean;
  };
  sensitivity: 'critical' | 'gentle' | 'all';
}

export interface ChatChannel {
  id: string;
  title: string;
  subtitle: string;
  lastActive: string;
  avatar?: string;
  icon?: string;
  theme: 'pink' | 'lavender' | 'yellow' | 'clean';
  badge?: string;
  membersCount?: number;
  hasVoiceMemo: boolean;
  voiceDuration?: string;
  lastMessage: string;
  attachedTask?: {
    title: string;
    itemsLeft: string;
  };
}

export interface ReminderEvent {
  id: string;
  title: string;
  location: string;
  time: string;
  category: string;
  completed: boolean;
  source: 'google-cal' | 'onereflection' | 'shared';
  partnerNote?: string;
  voicePingTime?: string;
  daysRemaining?: number;
}

export interface UserProfile {
  name: string;
  handle: string;
  avatar: string;
  joinedDate: string;
  syncScore: number;
  pairedPartner: {
    name: string;
    handle: string;
    status: string;
    hasMutualConsent: boolean;
  };
  audioPreset: 'studio' | 'vintage' | 'whisper';
  alarmGradualFade: boolean;
  quietHours: {
    bedtime: string;
    wake: string;
    syncWithPartner: boolean;
  };
  oledPureBlackMode: boolean;
}
