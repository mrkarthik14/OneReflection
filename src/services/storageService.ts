import {
  AlarmItem,
  TaskItem,
  HabitItem,
  CircleMember,
  ChatChannel,
  ReminderEvent,
  UserProfile,
  TaskCategory,
  CategoryColor,
  TaskPriority,
} from '../types';

export interface PriorityConfig {
  label: TaskPriority;
  level: number;
  icon: string;
  badgeBg: string;
  badgeText: string;
  badgeBorder: string;
  dotColor: string;
}

export const PRIORITY_CONFIG: Record<TaskPriority, PriorityConfig> = {
  High: {
    label: 'High',
    level: 3,
    icon: 'priority_high',
    badgeBg: 'bg-[#FFDAD6] dark:bg-[#93000A]/40',
    badgeText: 'text-[#BA1A1A] dark:text-[#FFB4AB]',
    badgeBorder: 'border-[#BA1A1A]/30',
    dotColor: '#BA1A1A',
  },
  Medium: {
    label: 'Medium',
    level: 2,
    icon: 'flag',
    badgeBg: 'bg-[#F9DF72]/70 dark:bg-[#736200]/40',
    badgeText: 'text-[#736200] dark:text-[#F9DF72]',
    badgeBorder: 'border-[#736200]/25',
    dotColor: '#D4A017',
  },
  Low: {
    label: 'Low',
    level: 1,
    icon: 'low_priority',
    badgeBg: 'bg-[#B8E98E]/50 dark:bg-[#2A4413]/40',
    badgeText: 'text-[#2A4413] dark:text-[#B8E98E]',
    badgeBorder: 'border-[#2A4413]/20',
    dotColor: '#5C9E31',
  },
};

export const CATEGORY_COLORS: Record<CategoryColor, {
  label: string;
  preview: string;
  cardBg: string;
  cardBorder: string;
  badgeBg: string;
  badgeText: string;
  badgeBorder: string;
  accentText: string;
}> = {
  pink: {
    label: 'Candy Pink',
    preview: '#F5ACD7',
    cardBg: 'bg-[#F5ACD7]',
    cardBorder: 'border-[#864B70]/20',
    badgeBg: 'bg-white/90',
    badgeText: 'text-[#1C1B1B]',
    badgeBorder: 'border-black/10',
    accentText: 'text-[#753C61]',
  },
  yellow: {
    label: 'Butter Yellow',
    preview: '#F9DF72',
    cardBg: 'bg-[#F9DF72]',
    cardBorder: 'border-[#6F5D00]/20',
    badgeBg: 'bg-white/90',
    badgeText: 'text-[#1C1B1B]',
    badgeBorder: 'border-black/10',
    accentText: 'text-[#736200]',
  },
  lavender: {
    label: 'Lavender Blue',
    preview: '#DAE2FF',
    cardBg: 'bg-[#DAE2FF]',
    cardBorder: 'border-[#4F5D85]/20',
    badgeBg: 'bg-white/90',
    badgeText: 'text-[#091A3E]',
    badgeBorder: 'border-[#4F5D85]/20',
    accentText: 'text-[#38466C]',
  },
  matcha: {
    label: 'Soft Matcha',
    preview: '#B8E98E',
    cardBg: 'bg-[#EAE7E7] dark:bg-neutral-900',
    cardBorder: 'border-[#37531D]/20',
    badgeBg: 'bg-white dark:bg-neutral-800',
    badgeText: 'text-[#1C1B1B] dark:text-white',
    badgeBorder: 'border-black/5',
    accentText: 'text-[#2A4413] dark:text-[#B8E98E]',
  },
  lilac: {
    label: 'Warm Lilac',
    preview: '#E7A2EE',
    cardBg: 'bg-[#E7A2EE]',
    cardBorder: 'border-[#864B70]/20',
    badgeBg: 'bg-white/90',
    badgeText: 'text-[#1C1B1B]',
    badgeBorder: 'border-black/10',
    accentText: 'text-[#531858]',
  },
  cyan: {
    label: 'Soft Cyan',
    preview: '#A5E8EF',
    cardBg: 'bg-[#A5E8EF]',
    cardBorder: 'border-[#085C68]/20',
    badgeBg: 'bg-white/90',
    badgeText: 'text-[#0A4952]',
    badgeBorder: 'border-[#085C68]/20',
    accentText: 'text-[#0A4952]',
  },
  slate: {
    label: 'Surface Slate',
    preview: '#EAE7E7',
    cardBg: 'bg-[#F6F3F2] dark:bg-neutral-900',
    cardBorder: 'border-black/10 dark:border-neutral-800',
    badgeBg: 'bg-white dark:bg-neutral-800',
    badgeText: 'text-[#1C1B1B] dark:text-white',
    badgeBorder: 'border-black/5',
    accentText: 'text-[#313030] dark:text-neutral-300',
  },
};

export const INITIAL_CATEGORIES: TaskCategory[] = [
  { id: 'cat-work', name: 'Work', color: 'slate', icon: 'business_center', isDefault: true },
  { id: 'cat-personal', name: 'Personal', color: 'matcha', icon: 'person', isDefault: true },
  { id: 'cat-health', name: 'Health', color: 'pink', icon: 'favorite', isDefault: true },
  { id: 'cat-studio', name: 'Studio Dev', color: 'lavender', icon: 'code', isDefault: true },
  { id: 'cat-errand', name: 'Shared Errands', color: 'yellow', icon: 'shopping_bag', isDefault: true },
  { id: 'cat-ritual', name: 'Daily Ritual', color: 'cyan', icon: 'spa', isDefault: true },
];

const INITIAL_PROFILE: UserProfile = {
  name: 'Elena Vance',
  handle: '@elenavance',
  avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBE7jpIocCISc-CQYfgAnahVZmLt7VB9UJKlTFKJX9JEBM0KeqWHrnY0EOlX7Xr016w-StCO-DfHViu0fGfhqLc5xXymxW-Idb1KIskGYbnQtfZt9R24CzmTiiG97t1oqT6BRybyaDZrINztAvlimWlhHUhwdR_IyfwE_j6uA1V6l9K1-IiXMA6jRa5-JRXxNhlmHpahWf3zorVKk7seurAn3RnMPBzb_UQpUlVYYBvZMwd1oKmMCq0',
  joinedDate: 'Nov 2023',
  syncScore: 8.9,
  pairedPartner: {
    name: 'Maya R.',
    handle: '@mayar',
    status: 'Awake & Flowing ☀️',
    hasMutualConsent: true,
  },
  audioPreset: 'studio',
  alarmGradualFade: true,
  quietHours: {
    bedtime: '22:30',
    wake: '07:00',
    syncWithPartner: true,
  },
  oledPureBlackMode: false,
};

const INITIAL_ALARMS: AlarmItem[] = [
  {
    id: 'alarm-1',
    title: 'Morning Sunshine',
    time: '07:30',
    period: 'AM',
    days: ['M', 'T', 'W', 'T', 'F'],
    enabled: true,
    category: 'shared',
    creatorName: 'Maya R.',
    creatorRole: 'Partner',
    creatorAvatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr',
    voiceSnippetQuote: 'Rise and shine sunny! Remember to drink your lemon water first thing.',
    voiceSnippetDuration: '0:24',
    chimeSound: 'tibetan',
    isBreakthrough: true,
  },
  {
    id: 'alarm-2',
    title: 'Team Standup Ping',
    time: '08:45',
    period: 'AM',
    days: ['M', 'T', 'W', 'T', 'F'],
    enabled: true,
    category: 'studio',
    chimeSound: 'nordic',
  },
  {
    id: 'alarm-3',
    title: 'Shared Focus Sprint',
    time: '02:30',
    period: 'PM',
    days: ['W'],
    enabled: true,
    category: 'sprint',
    voiceSnippetQuote: 'Time to lock in 45m deep code',
    voiceSnippetDuration: '0:08',
    chimeSound: 'nordic',
  },
  {
    id: 'alarm-4',
    title: 'Wind Down & Reflection',
    time: '10:30',
    period: 'PM',
    days: ['M', 'T', 'W', 'T', 'F', 'S', 'S'],
    enabled: false,
    category: 'selfcare',
    voiceSnippetQuote: 'Gentle Solfeggio Chime + 30s Audio Note',
    chimeSound: 'solfeggio',
  },
];

const INITIAL_TASKS: TaskItem[] = [
  {
    id: 'task-1',
    title: 'Design Critique & Prototype',
    description: 'Screen 3 Bento review & gesture refinement with Maya R.',
    category: 'Work',
    categoryId: 'cat-work',
    dueTime: '11:30 AM',
    completed: false,
    priority: 'High',
    subtasksDone: 3,
    subtasksTotal: 4,
    voiceBriefQuote: 'Screen 3 Bento review & gesture refinement',
    voiceBriefDuration: '0:14s',
    assignees: [
      { name: 'Elena', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBE7jpIocCISc-CQYfgAnahVZmLt7VB9UJKlTFKJX9JEBM0KeqWHrnY0EOlX7Xr016w-StCO-DfHViu0fGfhqLc5xXymxW-Idb1KIskGYbnQtfZt9R24CzmTiiG97t1oqT6BRybyaDZrINztAvlimWlhHUhwdR_IyfwE_j6uA1V6l9K1-IiXMA6jRa5-JRXxNhlmHpahWf3zorVKk7seurAn3RnMPBzb_UQpUlVYYBvZMwd1oKmMCq0' },
      { name: 'Maya', avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr' },
    ],
  },
  {
    id: 'task-2',
    title: 'Daily Botanical Care & Terrace Water',
    description: 'Mindful hydration cycle for balcony greenery & herbs.',
    category: 'Daily Ritual',
    categoryId: 'cat-ritual',
    dueTime: '01:00 PM',
    completed: false,
    priority: 'Low',
    streak: 14,
  },
  {
    id: 'task-3',
    title: 'OneReflection Kotlin Compose Sync',
    description: 'Architecture review of the reactive shared store state.',
    category: 'Studio Dev',
    categoryId: 'cat-studio',
    dueTime: '04:15 PM',
    completed: false,
    priority: 'High',
    codeBranch: 'feat/sync-v3',
    assignees: [
      { name: 'Leo', initials: 'LS' },
      { name: 'Sam', initials: 'SM' },
    ],
  },
  {
    id: 'task-4',
    title: 'Pick up artisan sourdough & oats',
    description: 'Boulangerie Saint-Germain on 4th Ave.',
    category: 'Shared Errands',
    categoryId: 'cat-errand',
    dueTime: '06:30 PM',
    completed: false,
    priority: 'Medium',
    voiceBriefQuote: 'Get the seeded one if warm!',
  },
];

const INITIAL_COMPLETED_TASKS: TaskItem[] = [
  {
    id: 'done-1',
    title: 'Morning Espresso Meditation',
    description: 'Quiet 10 min breathwork with dark roast',
    category: 'ritual',
    dueTime: '08:00 AM',
    completed: true,
    priority: 'Low',
  },
  {
    id: 'done-2',
    title: 'Send Invoice to Studio Nord',
    description: 'Sprint 13 sign-off deliverables',
    category: 'studio',
    dueTime: '09:15 AM',
    completed: true,
    priority: 'High',
  },
  {
    id: 'done-3',
    title: 'Hydration: 4 of 4 glasses',
    description: '1.2L logged before noon',
    category: 'ritual',
    dueTime: '10:30 AM',
    completed: true,
    priority: 'Low',
  },
  {
    id: 'done-4',
    title: 'Sync Dog Walk with Maya',
    description: 'Sunny loop along the waterfront park',
    category: 'errand',
    dueTime: '11:00 AM',
    completed: true,
    priority: 'Medium',
  },
];

const INITIAL_HABITS: HabitItem[] = [
  {
    id: 'habit-1',
    title: 'Mindful Hydration',
    category: 'hydration',
    currentValue: 1.8,
    targetValue: 2.5,
    unit: 'L',
    completedToday: false,
    streakDays: 7,
    partnerSynced: 'Sofia',
    colorTheme: 'yellow',
  },
  {
    id: 'habit-2',
    title: 'Gratitude & Voice Journal',
    category: 'gratitude',
    currentValue: 1,
    targetValue: 1,
    unit: 'memo',
    completedToday: true,
    streakDays: 14,
    colorTheme: 'pink',
    lastLogTime: '08:15 AM',
  },
  {
    id: 'habit-3',
    title: '45m Deep Work Sprint',
    category: 'focus',
    currentValue: 2,
    targetValue: 3,
    unit: 'sprints',
    completedToday: false,
    streakDays: 12,
    partnerSynced: 'Leo & Maya',
    colorTheme: 'cyan',
  },
  {
    id: 'habit-4',
    title: 'Evening Sunset Walk',
    category: 'walk',
    currentValue: 4820,
    targetValue: 6000,
    unit: 'steps',
    completedToday: false,
    streakDays: 5,
    colorTheme: 'green',
  },
];

const INITIAL_MEMBERS: CircleMember[] = [
  {
    id: 'member-maya',
    name: 'Maya R.',
    handle: '@mayar',
    role: 'Primary Partner',
    status: 'Awake in Brooklyn • Oura Synced',
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr',
    isOnline: true,
    isAwake: true,
    permissions: {
      voiceAlarms: true,
      sharedTasks: true,
      rhythmPings: true,
      emergencyBreakthrough: true,
      gentleVoiceWhispers: true,
      wakeUpAudioQueue: true,
    },
    sensitivity: 'gentle',
  },
  {
    id: 'member-leo',
    name: 'Leo Chen',
    handle: '@leoc',
    role: 'Studio Co-founder',
    status: 'Focus Session • 45m left',
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU',
    isOnline: true,
    isAwake: true,
    permissions: {
      voiceAlarms: false,
      sharedTasks: true,
      rhythmPings: false,
      emergencyBreakthrough: true,
      gentleVoiceWhispers: false,
      wakeUpAudioQueue: false,
    },
    sensitivity: 'critical',
  },
  {
    id: 'member-sofia',
    name: 'Sofia Miller',
    handle: '@sofiam',
    role: 'Mindful Rhythm Buddy',
    status: 'Completed Hydration Streak (7d)',
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704',
    isOnline: true,
    isAwake: true,
    permissions: {
      voiceAlarms: false,
      sharedTasks: true,
      rhythmPings: true,
      emergencyBreakthrough: false,
      gentleVoiceWhispers: false,
      wakeUpAudioQueue: false,
    },
    sensitivity: 'gentle',
  },
];

const INITIAL_CHANNELS: ChatChannel[] = [
  {
    id: 'chat-maya',
    title: 'Maya R.',
    subtitle: 'Partner • In Deep Flow',
    lastActive: '2m ago',
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr',
    theme: 'pink',
    badge: '1 new memo',
    hasVoiceMemo: true,
    voiceDuration: '0:38 / 1:12',
    lastMessage: '“Sending gentle morning energy! I also attached our dinner sourdough grocery to-do.”',
    attachedTask: {
      title: 'Pick up artisan sourdough',
      itemsLeft: 'Shared To-Do • 2 items remaining',
    },
  },
  {
    id: 'chat-studio',
    title: 'Studio Dev Sync',
    subtitle: 'Sprint 14 Sync Hub',
    lastActive: '11:20 AM',
    icon: 'groups',
    theme: 'lavender',
    membersCount: 4,
    hasVoiceMemo: true,
    voiceDuration: '0:45s',
    lastMessage: 'Leo: “Compose migration looks solid on staging...”',
  },
  {
    id: 'chat-sofia',
    title: 'Sofia Miller',
    subtitle: 'Mindful Rhythm Buddy',
    lastActive: 'Yesterday',
    avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704',
    theme: 'yellow',
    hasVoiceMemo: false,
    lastMessage: 'Shared a completed ritual: Hydration Goal reached (+500ml)',
  },
  {
    id: 'chat-family',
    title: 'Mom & Family Hub',
    subtitle: 'Scheduled Morning Routine',
    lastActive: 'May 14',
    icon: 'favorite',
    theme: 'clean',
    membersCount: 3,
    hasVoiceMemo: true,
    lastMessage: 'Scheduled Voice Wake-up set for Sunday 08:30 AM',
  },
];

const INITIAL_REMINDERS: ReminderEvent[] = [
  {
    id: 'rem-1',
    title: 'Dentist & Oral Wellness Checkup',
    location: "Dr. Turner's Clinic • Bring updated allergy & nightguard notes.",
    time: '11:30 AM',
    category: 'Health',
    completed: false,
    source: 'google-cal',
    partnerNote: 'Maya will send a voice ping 15m prior to departure',
  },
  {
    id: 'rem-2',
    title: 'Pick up Artisan Sourdough & Rolled Oats',
    location: 'Golden Wheat Bakery • Maya added 2 items to the bag.',
    time: '01:00 PM',
    category: 'Shared Errand',
    completed: false,
    source: 'shared',
    partnerNote: '"Remember seeded sourdough!" Voice memo from Maya • 0:14',
  },
  {
    id: 'rem-3',
    title: 'Studio Architecture Review & Sprint Retro',
    location: 'Google Meet room linked • Presenting the bento layout components.',
    time: '03:15 PM',
    category: 'Studio Review',
    completed: false,
    source: 'onereflection',
  },
  {
    id: 'rem-4',
    title: 'Sunset Walk & Balcony Plant Watering',
    location: '20 minutes offline reflection • Hydrate Monstera and ferns.',
    time: '05:45 PM',
    category: 'Daily Habit',
    completed: false,
    source: 'onereflection',
  },
  {
    id: 'rem-5',
    title: 'Daily Reflection & Wind-Down Voice Note',
    location: '"What brought the quietest joy today?" • Automatic audio backup.',
    time: '08:30 PM',
    category: 'Evening Voice Prompt',
    completed: false,
    source: 'onereflection',
  },
];

class StorageService {
  private profile: UserProfile = INITIAL_PROFILE;
  private alarms: AlarmItem[] = INITIAL_ALARMS;
  private tasks: TaskItem[] = INITIAL_TASKS;
  private completedTasks: TaskItem[] = INITIAL_COMPLETED_TASKS;
  private habits: HabitItem[] = INITIAL_HABITS;
  private members: CircleMember[] = INITIAL_MEMBERS;
  private channels: ChatChannel[] = INITIAL_CHANNELS;
  private reminders: ReminderEvent[] = INITIAL_REMINDERS;
  private categories: TaskCategory[] = INITIAL_CATEGORIES;
  private listeners: Set<() => void> = new Set();
  private outboxQueue: { id: string; action: string; timestamp: number }[] = [];

  constructor() {
    this.loadFromStorage();
  }

  private loadFromStorage() {
    try {
      const savedProfile = localStorage.getItem('oner_profile');
      if (savedProfile) this.profile = JSON.parse(savedProfile);

      const savedAlarms = localStorage.getItem('oner_alarms');
      if (savedAlarms) this.alarms = JSON.parse(savedAlarms);

      const savedTasks = localStorage.getItem('oner_tasks');
      if (savedTasks) {
        this.tasks = JSON.parse(savedTasks).map((t: TaskItem) => ({
          ...t,
          priority: t.priority === 'High' ? 'High' : (t.priority === 'Low' ? 'Low' : 'Medium'),
        }));
      }

      const savedCompleted = localStorage.getItem('oner_completed_tasks');
      if (savedCompleted) {
        this.completedTasks = JSON.parse(savedCompleted).map((t: TaskItem) => ({
          ...t,
          priority: t.priority === 'High' ? 'High' : (t.priority === 'Low' ? 'Low' : 'Medium'),
        }));
      }

      const savedHabits = localStorage.getItem('oner_habits');
      if (savedHabits) this.habits = JSON.parse(savedHabits);

      const savedMembers = localStorage.getItem('oner_members');
      if (savedMembers) this.members = JSON.parse(savedMembers);

      const savedCategories = localStorage.getItem('oner_categories');
      if (savedCategories) {
        this.categories = JSON.parse(savedCategories);
      } else {
        this.categories = INITIAL_CATEGORIES;
      }
    } catch {
      // Use in-memory defaults if storage fails
    }
  }

  private persist() {
    try {
      localStorage.setItem('oner_profile', JSON.stringify(this.profile));
      localStorage.setItem('oner_alarms', JSON.stringify(this.alarms));
      localStorage.setItem('oner_tasks', JSON.stringify(this.tasks));
      localStorage.setItem('oner_completed_tasks', JSON.stringify(this.completedTasks));
      localStorage.setItem('oner_habits', JSON.stringify(this.habits));
      localStorage.setItem('oner_members', JSON.stringify(this.members));
      localStorage.setItem('oner_categories', JSON.stringify(this.categories));
    } catch {
      // Ignored
    }
    this.notify();
  }

  subscribe(listener: () => void): () => void {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  private notify() {
    this.listeners.forEach((listener) => listener());
  }

  // Categories API
  getCategories(): TaskCategory[] {
    return [...this.categories];
  }

  getCategory(id: string): TaskCategory | undefined {
    return this.categories.find((c) => c.id === id);
  }

  addCategory(name: string, color: CategoryColor, icon?: string): TaskCategory {
    const newCategory: TaskCategory = {
      id: `cat-${Date.now()}`,
      name: name.trim(),
      color,
      icon: icon || 'label',
      isDefault: false,
    };
    this.categories.push(newCategory);
    this.queueSync(`add_category_${newCategory.id}`);
    this.persist();
    return newCategory;
  }

  updateCategory(id: string, updates: Partial<TaskCategory>): TaskCategory | null {
    const cat = this.categories.find((c) => c.id === id);
    if (cat) {
      Object.assign(cat, updates);
      if (updates.name) {
        this.tasks.forEach((t) => {
          if (t.categoryId === id) t.category = updates.name!;
        });
        this.completedTasks.forEach((t) => {
          if (t.categoryId === id) t.category = updates.name!;
        });
      }
      this.queueSync(`update_category_${id}`);
      this.persist();
      return cat;
    }
    return null;
  }

  deleteCategory(id: string): boolean {
    const index = this.categories.findIndex((c) => c.id === id);
    if (index >= 0) {
      this.categories.splice(index, 1);
      const fallbackCat = this.categories[0] || INITIAL_CATEGORIES[0];
      this.tasks.forEach((t) => {
        if (t.categoryId === id) {
          t.categoryId = fallbackCat.id;
          t.category = fallbackCat.name;
        }
      });
      this.completedTasks.forEach((t) => {
        if (t.categoryId === id) {
          t.categoryId = fallbackCat.id;
          t.category = fallbackCat.name;
        }
      });
      this.queueSync(`delete_category_${id}`);
      this.persist();
      return true;
    }
    return false;
  }

  // Profile API
  getProfile(): UserProfile {
    return { ...this.profile };
  }

  updateProfile(updates: Partial<UserProfile>) {
    this.profile = { ...this.profile, ...updates };
    this.queueSync('update_profile');
    this.persist();
  }

  toggleOLEDMode(): boolean {
    this.profile.oledPureBlackMode = !this.profile.oledPureBlackMode;
    this.persist();
    return this.profile.oledPureBlackMode;
  }

  // Alarms API
  getAlarms(): AlarmItem[] {
    return [...this.alarms];
  }

  toggleAlarm(id: string): boolean {
    const alarm = this.alarms.find((a) => a.id === id);
    if (alarm) {
      alarm.enabled = !alarm.enabled;
      this.queueSync(`toggle_alarm_${id}`);
      this.persist();
      return alarm.enabled;
    }
    return false;
  }

  addAlarm(alarm: Omit<AlarmItem, 'id'>) {
    const newAlarm: AlarmItem = {
      ...alarm,
      id: `alarm-${Date.now()}`,
    };
    this.alarms.unshift(newAlarm);
    this.queueSync('add_alarm');
    this.persist();
  }

  // Tasks API
  getTasks(): TaskItem[] {
    return [...this.tasks];
  }

  getCompletedTasks(): TaskItem[] {
    return [...this.completedTasks];
  }

  toggleTask(id: string) {
    const activeIdx = this.tasks.findIndex((t) => t.id === id);
    if (activeIdx >= 0) {
      const [task] = this.tasks.splice(activeIdx, 1);
      task.completed = true;
      this.completedTasks.unshift(task);
      this.queueSync(`complete_task_${id}`);
      this.persist();
      return;
    }

    const doneIdx = this.completedTasks.findIndex((t) => t.id === id);
    if (doneIdx >= 0) {
      const [task] = this.completedTasks.splice(doneIdx, 1);
      task.completed = false;
      this.tasks.push(task);
      this.queueSync(`uncomplete_task_${id}`);
      this.persist();
    }
  }

  addTask(
    title: string,
    categoryInput?: string,
    dueTime: string = '05:00 PM',
    description: string = 'Added via quick action',
    priority: TaskPriority = 'Medium'
  ) {
    let targetCat = this.categories.find((c) => c.id === categoryInput || c.name.toLowerCase() === categoryInput?.toLowerCase());
    if (!targetCat) {
      targetCat = this.categories[0] || INITIAL_CATEGORIES[0];
    }

    const newTask: TaskItem = {
      id: `task-${Date.now()}`,
      title: title.trim(),
      description,
      category: targetCat.name,
      categoryId: targetCat.id,
      dueTime,
      completed: false,
      priority,
    };
    this.tasks.unshift(newTask);
    this.queueSync('add_task');
    this.persist();
    return newTask;
  }

  updateTaskPriority(id: string, priority: TaskPriority): boolean {
    const task = this.tasks.find((t) => t.id === id) || this.completedTasks.find((t) => t.id === id);
    if (task) {
      task.priority = priority;
      this.queueSync(`update_priority_${id}`);
      this.persist();
      return true;
    }
    return false;
  }

  // Habits API
  getHabits(): HabitItem[] {
    return [...this.habits];
  }

  incrementHydration(amountMl: number) {
    const hydration = this.habits.find((h) => h.category === 'hydration');
    if (hydration) {
      hydration.currentValue = Number((hydration.currentValue + amountMl / 1000).toFixed(2));
      if (hydration.currentValue >= hydration.targetValue) {
        hydration.completedToday = true;
      }
      this.queueSync('log_hydration');
      this.persist();
    }
  }

  toggleHabitDone(id: string) {
    const habit = this.habits.find((h) => h.id === id);
    if (habit) {
      habit.completedToday = !habit.completedToday;
      if (habit.completedToday) {
        habit.streakDays += 1;
      } else {
        habit.streakDays = Math.max(0, habit.streakDays - 1);
      }
      this.queueSync(`toggle_habit_${id}`);
      this.persist();
    }
  }

  // Members API
  getMembers(): CircleMember[] {
    return [...this.members];
  }

  togglePermission(memberId: string, permissionKey: keyof CircleMember['permissions']) {
    const member = this.members.find((m) => m.id === memberId);
    if (member) {
      member.permissions[permissionKey] = !member.permissions[permissionKey];
      this.queueSync(`toggle_perm_${memberId}_${permissionKey}`);
      this.persist();
    }
  }

  // Channels API
  getChannels(): ChatChannel[] {
    return [...this.channels];
  }

  // Reminders API
  getReminders(): ReminderEvent[] {
    return [...this.reminders];
  }

  toggleReminder(id: string) {
    const r = this.reminders.find((item) => item.id === id);
    if (r) {
      r.completed = !r.completed;
      this.queueSync(`toggle_reminder_${id}`);
      this.persist();
    }
  }

  // Offline outbox queue & E2E encryption simulation
  private queueSync(action: string) {
    this.outboxQueue.push({
      id: `mut-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`,
      action,
      timestamp: Date.now(),
    });
  }

  getOutboxCount(): number {
    return this.outboxQueue.length;
  }

  clearOutbox() {
    this.outboxQueue = [];
  }
}

export const storageService = new StorageService();
