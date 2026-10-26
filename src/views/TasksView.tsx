import React, { useState, useEffect } from 'react';
import { audioService } from '../services/audioService';
import { storageService, CATEGORY_COLORS, PRIORITY_CONFIG } from '../services/storageService';
import { TaskItem, TaskCategory, ScreenId, TaskPriority } from '../types';
import { CategoryManagerModal } from '../components/CategoryManagerModal';

interface TasksViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export type TaskSortMode = 'priority-desc' | 'priority-asc' | 'time' | 'default';

export const TasksView: React.FC<TasksViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const [categories, setCategories] = useState<TaskCategory[]>(storageService.getCategories());
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | 'all'>('all');
  const [tasks, setTasks] = useState<TaskItem[]>(storageService.getTasks());
  const [completedTasks, setCompletedTasks] = useState<TaskItem[]>(storageService.getCompletedTasks());
  const [completedAccordionOpen, setCompletedAccordionOpen] = useState(true);
  const [activeDay, setActiveDay] = useState(16);
  const [newTaskInput, setNewTaskInput] = useState('');
  const [newTaskCategoryId, setNewTaskCategoryId] = useState<string>(
    categories[0]?.id || 'cat-work'
  );
  const [newTaskPriority, setNewTaskPriority] = useState<TaskPriority>('Medium');
  const [sortMode, setSortMode] = useState<TaskSortMode>('priority-desc');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showCategoryModal, setShowCategoryModal] = useState(false);

  // Subscribe to storage changes
  useEffect(() => {
    const unsubscribe = storageService.subscribe(() => {
      setCategories(storageService.getCategories());
      setTasks(storageService.getTasks());
      setCompletedTasks(storageService.getCompletedTasks());
    });
    return unsubscribe;
  }, []);

  const days = [
    { name: 'Mon', num: 14 },
    { name: 'Tue', num: 15 },
    { name: 'Wed', num: 16 },
    { name: 'Thu', num: 17 },
    { name: 'Fri', num: 18 },
    { name: 'Sat', num: 19 },
  ];

  const handleToggleTask = (id: string) => {
    audioService.playChime('click');
    storageService.toggleTask(id);
    setTasks(storageService.getTasks());
    setCompletedTasks(storageService.getCompletedTasks());
  };

  const handleAddNewTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTaskInput.trim()) return;
    audioService.playChime('solfeggio');
    storageService.addTask(
      newTaskInput.trim(),
      newTaskCategoryId,
      '05:00 PM',
      'Added via quick action',
      newTaskPriority
    );
    setNewTaskInput('');
    setNewTaskPriority('Medium');
    setShowAddModal(false);
    setTasks(storageService.getTasks());
  };

  const handleCyclePriority = (e: React.MouseEvent, task: TaskItem) => {
    e.stopPropagation();
    audioService.playChime('click');
    const cycleMap: Record<TaskPriority, TaskPriority> = {
      High: 'Medium',
      Medium: 'Low',
      Low: 'High',
    };
    const currentPriority: TaskPriority = task.priority || 'Medium';
    const nextPriority = cycleMap[currentPriority];
    storageService.updateTaskPriority(task.id, nextPriority);
    setTasks(storageService.getTasks());
    setCompletedTasks(storageService.getCompletedTasks());
  };

  // Filter tasks by selected category
  const filteredTasks = tasks.filter((t) => {
    if (selectedCategoryId === 'all') return true;
    if (t.categoryId) return t.categoryId === selectedCategoryId;
    // Fallback match by category name
    const matchingCat = categories.find((c) => c.id === selectedCategoryId);
    return matchingCat && t.category?.toLowerCase() === matchingCat.name.toLowerCase();
  });

  // Numeric priority weight for sorting
  const getPriorityWeight = (p?: TaskPriority) => {
    if (p === 'High') return 3;
    if (p === 'Medium') return 2;
    if (p === 'Low') return 1;
    return 2;
  };

  // Sorted tasks based on active sortMode
  const sortedTasks = [...filteredTasks].sort((a, b) => {
    if (sortMode === 'priority-desc') {
      const diff = getPriorityWeight(b.priority) - getPriorityWeight(a.priority);
      if (diff !== 0) return diff;
      return a.dueTime.localeCompare(b.dueTime);
    }
    if (sortMode === 'priority-asc') {
      const diff = getPriorityWeight(a.priority) - getPriorityWeight(b.priority);
      if (diff !== 0) return diff;
      return a.dueTime.localeCompare(b.dueTime);
    }
    if (sortMode === 'time') {
      return a.dueTime.localeCompare(b.dueTime);
    }
    return 0;
  });

  // Calculate task count per category
  const getCategoryCount = (catId: string) => {
    const cat = categories.find((c) => c.id === catId);
    return tasks.filter((t) => {
      if (t.categoryId) return t.categoryId === catId;
      return cat && t.category?.toLowerCase() === cat.name.toLowerCase();
    }).length;
  };

  const getTaskCategoryConfig = (task: TaskItem) => {
    let cat = categories.find((c) => c.id === task.categoryId);
    if (!cat) {
      cat = categories.find((c) => c.name.toLowerCase() === task.category?.toLowerCase());
    }
    const colorKey = cat ? cat.color : 'slate';
    return {
      category: cat || { id: 'unknown', name: task.category || 'General', color: 'slate' as const },
      theme: CATEGORY_COLORS[colorKey] || CATEGORY_COLORS.slate,
    };
  };

  return (
    <div className="flex flex-col w-full px-4 pb-12 gap-y-4">
      {/* 1. Header & Sync Score */}
      <div className="flex flex-col gap-y-1.5 pt-2">
        <div className="flex items-center justify-between">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#F9DF72] text-[#736200] font-bold text-[10px]">
            <span className="material-symbols-outlined text-[15px]">celebration</span>
            <span>SYNCHRONIZED CIRCLE</span>
          </div>

          <div className={`flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold ${
            oledMode ? 'bg-neutral-900 text-neutral-200' : 'bg-neutral-200 text-[#1C1B1B]'
          }`}>
            <span className="material-symbols-outlined text-[15px] text-[#864B70] dark:text-[#F5ACD7]" style={{ fontVariationSettings: "'FILL' 1" }}>
              stars
            </span>
            <span>8.8 Sync Score</span>
          </div>
        </div>

        <div>
          <h1 className={`font-headline text-2xl font-bold tracking-tight ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
            Tasks & Shared Flows
          </h1>
          <p className={`text-xs ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
            Conquer your day alongside your circle.
          </p>
        </div>
      </div>

      {/* 2. Horizontal Weekly Calendar Strip */}
      <div className="flex items-center justify-between gap-1 overflow-x-auto no-scrollbar py-1">
        {days.map((d) => {
          const isActive = d.num === activeDay;
          return (
            <button
              key={d.num}
              onClick={() => {
                audioService.playChime('click');
                setActiveDay(d.num);
              }}
              className={`flex flex-col items-center justify-center min-w-[48px] h-16 rounded-2xl transition-all ${
                isActive
                  ? 'bg-[#F9DF72] text-[#736200] shadow-sm font-bold scale-105'
                  : oledMode
                    ? 'bg-neutral-900 text-neutral-400 hover:text-white'
                    : 'bg-[#F6F3F2] text-[#50434A] hover:bg-neutral-200'
              }`}
            >
              <span className="text-[10px] font-bold">{d.name}</span>
              <span className="font-headline text-base font-bold mt-0.5">{d.num}</span>
              {isActive && <span className="w-1.5 h-1.5 rounded-full bg-[#736200] mt-0.5" />}
            </button>
          );
        })}
      </div>

      {/* 3. Category Filter Pills Row (Dynamic + Manage Button) */}
      <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-1">
        {/* All Pill */}
        <button
          onClick={() => {
            audioService.playChime('click');
            setSelectedCategoryId('all');
          }}
          className={`shrink-0 h-9 px-4 rounded-full text-xs font-bold flex items-center gap-1.5 transition-all cursor-pointer ${
            selectedCategoryId === 'all'
              ? 'bg-[#1C1B1B] text-white shadow-xs'
              : 'bg-neutral-200 dark:bg-neutral-900 text-neutral-600 dark:text-neutral-300 hover:bg-neutral-300'
          }`}
        >
          <span>All</span>
          <span className="w-4 h-4 rounded-full bg-white/20 text-[10px] flex items-center justify-center font-bold">
            {tasks.length}
          </span>
        </button>

        {/* Dynamic Custom Categories */}
        {categories.map((cat) => {
          const isSelected = selectedCategoryId === cat.id;
          const conf = CATEGORY_COLORS[cat.color] || CATEGORY_COLORS.slate;
          const count = getCategoryCount(cat.id);

          return (
            <button
              key={cat.id}
              onClick={() => {
                audioService.playChime('click');
                setSelectedCategoryId(cat.id);
              }}
              className={`shrink-0 h-9 px-3.5 rounded-full text-xs font-bold flex items-center gap-2 transition-all cursor-pointer border ${
                isSelected
                  ? 'bg-[#1C1B1B] text-white border-transparent shadow-xs'
                  : 'bg-neutral-200 dark:bg-neutral-900 text-neutral-700 dark:text-neutral-300 border-transparent hover:bg-neutral-300 dark:hover:bg-neutral-800'
              }`}
            >
              {/* Category Color Dot */}
              <span
                className="w-2.5 h-2.5 rounded-full shrink-0 shadow-xs"
                style={{ backgroundColor: conf.preview }}
              />
              <span>{cat.name}</span>
              <span className={`text-[10px] font-bold px-1.5 py-0.2 rounded-full ${
                isSelected ? 'bg-white/20 text-white' : 'bg-black/5 dark:bg-white/10 text-neutral-500'
              }`}>
                {count}
              </span>
            </button>
          );
        })}

        {/* Manage Categories Trigger */}
        <button
          onClick={() => {
            audioService.playChime('click');
            setShowCategoryModal(true);
          }}
          aria-label="Manage task tags and categories"
          className="shrink-0 h-9 px-3 rounded-full bg-[#FFD8EC] hover:bg-[#F5ACD7] text-[#864B70] text-xs font-bold flex items-center gap-1 shadow-xs active:scale-95 transition-all cursor-pointer"
        >
          <span className="material-symbols-outlined text-[15px]">add_circle</span>
          <span>Tags</span>
        </button>
      </div>

      {/* 4. Sort Controller & List Overview Bar */}
      <div className="flex items-center justify-between pt-1 pb-0.5 px-0.5">
        <div className="flex items-center gap-1.5">
          <span className="text-[11px] font-bold text-neutral-500 uppercase tracking-wider">
            Flows ({sortedTasks.length})
          </span>
          {selectedCategoryId !== 'all' && (
            <span className="text-[10px] px-2 py-0.5 rounded-full bg-neutral-200 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300 font-semibold">
              Filtered
            </span>
          )}
        </div>

        {/* Priority & Time Sort Buttons */}
        <div className="flex items-center gap-1 bg-neutral-200/80 dark:bg-neutral-900 p-0.5 rounded-full border border-black/5 dark:border-neutral-800">
          <button
            type="button"
            onClick={() => {
              audioService.playChime('click');
              setSortMode('priority-desc');
            }}
            title="Sort by Priority: High to Low"
            className={`h-7 px-2.5 rounded-full text-[10px] font-bold flex items-center gap-1 transition-all cursor-pointer ${
              sortMode === 'priority-desc'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'text-neutral-600 dark:text-neutral-400 hover:text-black dark:hover:text-white'
            }`}
          >
            <span className="material-symbols-outlined text-[13px] text-[#FFB4AB]" style={{ fontVariationSettings: "'FILL' 1" }}>
              priority_high
            </span>
            <span>High ↓</span>
          </button>

          <button
            type="button"
            onClick={() => {
              audioService.playChime('click');
              setSortMode('priority-asc');
            }}
            title="Sort by Priority: Low to High"
            className={`h-7 px-2.5 rounded-full text-[10px] font-bold flex items-center gap-1 transition-all cursor-pointer ${
              sortMode === 'priority-asc'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'text-neutral-600 dark:text-neutral-400 hover:text-black dark:hover:text-white'
            }`}
          >
            <span className="material-symbols-outlined text-[13px] text-[#B8E98E]" style={{ fontVariationSettings: "'FILL' 1" }}>
              low_priority
            </span>
            <span>Low ↑</span>
          </button>

          <button
            type="button"
            onClick={() => {
              audioService.playChime('click');
              setSortMode('time');
            }}
            title="Sort by Due Time"
            className={`h-7 px-2.5 rounded-full text-[10px] font-bold flex items-center gap-1 transition-all cursor-pointer ${
              sortMode === 'time'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'text-neutral-600 dark:text-neutral-400 hover:text-black dark:hover:text-white'
            }`}
          >
            <span className="material-symbols-outlined text-[13px]">
              schedule
            </span>
            <span>Time</span>
          </button>

          <button
            type="button"
            onClick={() => {
              audioService.playChime('click');
              setSortMode('default');
            }}
            title="Default Order"
            className={`h-7 px-2 rounded-full text-[10px] font-bold flex items-center justify-center transition-all cursor-pointer ${
              sortMode === 'default'
                ? 'bg-[#1C1B1B] text-white shadow-xs'
                : 'text-neutral-600 dark:text-neutral-400 hover:text-black dark:hover:text-white'
            }`}
          >
            <span>☰</span>
          </button>
        </div>
      </div>

      {/* 5. Bento Tasks Deck */}
      <div className="flex flex-col gap-y-3.5">
        {sortedTasks.length === 0 ? (
          <div className="p-8 text-center rounded-3xl bg-neutral-100 dark:bg-neutral-900 border border-black/5 text-neutral-500">
            <span className="material-symbols-outlined text-3xl mb-1 text-neutral-400">task</span>
            <p className="text-xs font-bold">No tasks in this view</p>
            <p className="text-[11px] text-neutral-400 mt-0.5">Tap + Add New Task below to create one</p>
          </div>
        ) : (
          sortedTasks.map((t) => {
            const { category: cat, theme: conf } = getTaskCategoryConfig(t);
            const prio: TaskPriority = t.priority || 'Medium';
            const prioConf = PRIORITY_CONFIG[prio] || PRIORITY_CONFIG.Medium;

            return (
              <article
                key={t.id}
                className={`relative rounded-3xl p-5 flex flex-col gap-3 shadow-xs border transition-transform active:scale-[0.99] overflow-hidden ${
                  oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : `${conf.cardBg} ${conf.cardBorder} text-[#1C1B1B]`
                }`}
              >
                {/* Header row: Category Badge, Priority Badge & Due Time */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-1.5 flex-wrap">
                    <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold shadow-xs border ${conf.badgeBg} ${conf.badgeText} ${conf.badgeBorder}`}>
                      {cat.name}
                    </span>

                    {/* Color-Coded Priority Indicator Badge */}
                    <button
                      type="button"
                      onClick={(e) => handleCyclePriority(e, t)}
                      title={`Priority: ${prio}. Tap to change.`}
                      className={`px-2 py-0.5 rounded-full text-[10px] font-bold flex items-center gap-1 border shadow-2xs transition-all active:scale-95 cursor-pointer ${prioConf.badgeBg} ${prioConf.badgeText} ${prioConf.badgeBorder}`}
                    >
                      <span
                        className="material-symbols-outlined text-[13px] shrink-0"
                        style={{ fontVariationSettings: "'FILL' 1" }}
                      >
                        {prioConf.icon}
                      </span>
                      <span>{prioConf.label}</span>
                    </button>
                  </div>

                  <div className="flex items-center gap-1 bg-white/80 dark:bg-neutral-800/80 px-2 py-0.5 rounded-full text-xs font-semibold text-neutral-700 dark:text-neutral-300">
                    <span className="material-symbols-outlined text-[13px]">schedule</span>
                    <span>{t.dueTime}</span>
                  </div>
                </div>

                {/* Title & Checkbox */}
                <div className="flex items-start justify-between gap-3">
                  <div className="flex-1 min-w-0">
                    <h2 className="font-headline text-lg font-bold leading-snug">
                      {t.title}
                    </h2>
                    <p className={`text-xs mt-0.5 ${oledMode ? 'text-neutral-400' : 'text-[#50434A]'}`}>
                      {t.description}
                    </p>
                  </div>

                  <button
                    onClick={() => handleToggleTask(t.id)}
                    aria-label="Mark task done"
                    className="w-8 h-8 rounded-full bg-white dark:bg-neutral-800 flex items-center justify-center hover:scale-105 active:scale-95 transition-transform shadow-xs cursor-pointer shrink-0"
                  >
                    <span className="material-symbols-outlined text-[18px] text-neutral-300">
                      circle
                    </span>
                  </button>
                </div>

                {/* Subtasks, Voice Brief, Code Branch, or Assignees */}
                {(t.subtasksDone !== undefined || t.voiceBriefQuote || t.codeBranch || t.assignees) && (
                  <div className="flex flex-wrap items-center gap-2 pt-1">
                    {t.subtasksDone !== undefined && (
                      <div className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white/90 dark:bg-neutral-800 text-xs font-bold shadow-xs">
                        <span className="material-symbols-outlined text-[14px]">checklist</span>
                        <span>{t.subtasksDone} of {t.subtasksTotal} subtasks</span>
                      </div>
                    )}

                    {t.voiceBriefQuote && (
                      <button
                        onClick={() => audioService.playChime('tibetan')}
                        className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white/90 dark:bg-neutral-800 text-xs font-bold shadow-xs active:scale-95 transition-all"
                      >
                        <span className="material-symbols-outlined text-[14px] text-[#864B70] dark:text-[#F5ACD7]" style={{ fontVariationSettings: "'FILL' 1" }}>
                          play_circle
                        </span>
                        <span>{t.voiceBriefDuration ? `Voice: ${t.voiceBriefDuration}` : 'Voice Note'}</span>
                      </button>
                    )}

                    {t.codeBranch && (
                      <div className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white/90 dark:bg-neutral-800 text-xs font-bold shadow-xs">
                        <span className="material-symbols-outlined text-[14px]">terminal</span>
                        <span>{t.codeBranch}</span>
                      </div>
                    )}

                    {t.assignees && (
                      <div className="ml-auto flex items-center -space-x-2">
                        {t.assignees.map((a, idx) => (
                          a.avatar ? (
                            <img
                              key={idx}
                              src={a.avatar}
                              alt={a.name}
                              className="w-7 h-7 rounded-full object-cover ring-2 ring-white"
                            />
                          ) : (
                            <div
                              key={idx}
                              className="w-7 h-7 rounded-full bg-[#B3C1EF] text-[#091A3E] text-xs font-bold flex items-center justify-center ring-2 ring-white"
                            >
                              {a.initials}
                            </div>
                          )
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </article>
            );
          })
        )}
      </div>

      {/* 5. Completed Today Accordion */}
      <div className={`rounded-3xl p-4 flex flex-col gap-3 border transition-colors ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
      }`}>
        <button
          onClick={() => setCompletedAccordionOpen(!completedAccordionOpen)}
          className="flex items-center justify-between w-full text-left cursor-pointer"
        >
          <div className="flex items-center gap-2">
            <span className="w-6 h-6 rounded-full bg-neutral-300 dark:bg-neutral-800 text-neutral-800 dark:text-neutral-200 flex items-center justify-center text-xs font-bold">
              {completedTasks.length}
            </span>
            <span className={`font-headline text-base font-bold ${oledMode ? 'text-white' : 'text-[#1C1B1B]'}`}>
              Completed Today
            </span>
          </div>

          <span className={`material-symbols-outlined text-[20px] transition-transform duration-200 ${
            completedAccordionOpen ? 'rotate-180' : 'rotate-0'
          }`}>
            expand_more
          </span>
        </button>

        {completedAccordionOpen && (
          <div className="flex flex-col gap-2 pt-1">
            {completedTasks.map((t) => {
              const { category: cat, theme: conf } = getTaskCategoryConfig(t);
              const prio: TaskPriority = t.priority || 'Medium';
              const prioConf = PRIORITY_CONFIG[prio] || PRIORITY_CONFIG.Medium;

              return (
                <div
                  key={t.id}
                  onClick={() => handleToggleTask(t.id)}
                  className={`flex items-center justify-between py-2 px-3 rounded-2xl cursor-pointer hover:opacity-80 transition-all ${
                    oledMode ? 'bg-neutral-950 text-neutral-400' : 'bg-white/80 text-neutral-600'
                  }`}
                >
                  <div className="flex items-center gap-2 min-w-0">
                    <span className="material-symbols-outlined text-[18px] text-[#6F5D00] dark:text-[#F9DF72]" style={{ fontVariationSettings: "'FILL' 1" }}>
                      check_circle
                    </span>
                    <span className="text-xs line-through truncate font-medium">
                      {t.title}
                    </span>
                    <span className={`px-2 py-0.2 rounded-full text-[9px] font-bold border ${conf.badgeBg} ${conf.badgeText} ${conf.badgeBorder}`}>
                      {cat.name}
                    </span>
                    <span className={`px-1.5 py-0.2 rounded-full text-[9px] font-bold border flex items-center gap-0.5 opacity-75 ${prioConf.badgeBg} ${prioConf.badgeText} ${prioConf.badgeBorder}`}>
                      <span className="material-symbols-outlined text-[10px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                        {prioConf.icon}
                      </span>
                      <span>{prio}</span>
                    </span>
                  </div>
                  <span className="text-[10px] text-neutral-400 font-mono shrink-0 ml-2">
                    {t.dueTime}
                  </span>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* 6. Bottom Fast Actions Bar */}
      <div className="flex items-center gap-2.5 pt-1">
        <button
          onClick={() => setShowAddModal(true)}
          className="flex-1 h-13 rounded-full bg-[#1C1B1B] text-white font-headline text-sm font-bold flex items-center justify-center gap-2 shadow-sm hover:bg-neutral-800 active:scale-98 transition-all cursor-pointer"
        >
          <span className="material-symbols-outlined text-[20px]">add_circle</span>
          <span>Add New Task</span>
        </button>

        <button
          onClick={onOpenVoiceStudio}
          aria-label="Voice to Task"
          className="w-13 h-13 rounded-full bg-[#F9DF72] text-[#736200] flex items-center justify-center shadow-sm hover:scale-105 active:scale-95 transition-transform cursor-pointer shrink-0"
        >
          <span className="material-symbols-outlined text-[24px]">mic</span>
        </button>
      </div>

      {/* Inline Quick Add Task Modal with Category Selector */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <form
            onSubmit={handleAddNewTask}
            className="w-full max-w-sm bg-white dark:bg-neutral-900 rounded-3xl p-5 shadow-2xl border border-neutral-200 dark:border-neutral-800 flex flex-col gap-4 text-[#1C1B1B] dark:text-white"
          >
            <div className="flex items-center justify-between">
              <span className="font-headline text-lg font-bold">New Flow or Task</span>
              <button
                type="button"
                onClick={() => setShowAddModal(false)}
                className="w-7 h-7 rounded-full bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-xs"
              >
                ✕
              </button>
            </div>

            <input
              type="text"
              value={newTaskInput}
              onChange={(e) => setNewTaskInput(e.target.value)}
              placeholder="e.g. Prep dinner sourdough or review designs..."
              className="w-full h-12 px-4 rounded-2xl bg-neutral-100 dark:bg-neutral-800 border-none text-xs font-semibold focus:ring-2 focus:ring-[#F5ACD7] outline-none"
              autoFocus
            />

            {/* Category Selector Chips */}
            <div className="flex flex-col gap-1.5">
              <span className="text-[10px] font-bold text-neutral-500 uppercase tracking-wider">
                Select Tag / Category
              </span>
              <div className="flex flex-wrap gap-1.5 max-h-32 overflow-y-auto no-scrollbar">
                {categories.map((c) => {
                  const isSelected = newTaskCategoryId === c.id;
                  const conf = CATEGORY_COLORS[c.color] || CATEGORY_COLORS.slate;
                  return (
                    <button
                      key={c.id}
                      type="button"
                      onClick={() => {
                        audioService.playChime('click');
                        setNewTaskCategoryId(c.id);
                      }}
                      className={`px-3 py-1.5 rounded-full text-xs font-bold transition-all flex items-center gap-1.5 border ${
                        isSelected
                          ? 'bg-[#1C1B1B] text-white border-transparent shadow-xs scale-105'
                          : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 border-transparent hover:bg-neutral-200'
                      }`}
                    >
                      <span
                        className="w-2 h-2 rounded-full"
                        style={{ backgroundColor: conf.preview }}
                      />
                      <span>{c.name}</span>
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Priority Level Selector */}
            <div className="flex flex-col gap-1.5">
              <div className="flex items-center justify-between">
                <span className="text-[10px] font-bold text-neutral-500 uppercase tracking-wider">
                  Priority Level
                </span>
                <span className="text-[10px] text-neutral-400">
                  Selected: <strong className="text-neutral-800 dark:text-neutral-200">{newTaskPriority}</strong>
                </span>
              </div>
              <div className="grid grid-cols-3 gap-2">
                {(['Low', 'Medium', 'High'] as TaskPriority[]).map((prio) => {
                  const pConf = PRIORITY_CONFIG[prio];
                  const isSelected = newTaskPriority === prio;
                  return (
                    <button
                      key={prio}
                      type="button"
                      onClick={() => {
                        audioService.playChime('click');
                        setNewTaskPriority(prio);
                      }}
                      className={`py-2 px-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 transition-all border cursor-pointer ${
                        isSelected
                          ? 'bg-[#1C1B1B] text-white border-transparent shadow-xs ring-2 ring-offset-1 ring-black/20'
                          : `${pConf.badgeBg} ${pConf.badgeText} ${pConf.badgeBorder} hover:opacity-90`
                      }`}
                    >
                      <span
                        className="material-symbols-outlined text-[14px]"
                        style={{ fontVariationSettings: "'FILL' 1" }}
                      >
                        {pConf.icon}
                      </span>
                      <span>{prio}</span>
                    </button>
                  );
                })}
              </div>
            </div>

            <div className="flex items-center gap-2 pt-1">
              <button
                type="submit"
                disabled={!newTaskInput.trim()}
                className="flex-1 h-11 rounded-full bg-[#1C1B1B] text-white text-xs font-bold active:scale-95 disabled:opacity-40 transition-transform cursor-pointer"
              >
                Save Task
              </button>
              <button
                type="button"
                onClick={() => setShowAddModal(false)}
                className="px-4 h-11 rounded-full bg-neutral-200 dark:bg-neutral-800 text-xs font-bold text-neutral-600 dark:text-neutral-300"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Category Manager Modal */}
      <CategoryManagerModal
        isOpen={showCategoryModal}
        onClose={() => setShowCategoryModal(false)}
        oledMode={oledMode}
        onCategoriesUpdated={() => {
          setCategories(storageService.getCategories());
          setTasks(storageService.getTasks());
        }}
      />
    </div>
  );
};
