import React, { useState } from 'react';
import { TaskCategory, CategoryColor } from '../types';
import { storageService, CATEGORY_COLORS } from '../services/storageService';
import { audioService } from '../services/audioService';

interface CategoryManagerModalProps {
  isOpen: boolean;
  onClose: () => void;
  oledMode: boolean;
  onCategoriesUpdated: () => void;
}

export const CategoryManagerModal: React.FC<CategoryManagerModalProps> = ({
  isOpen,
  onClose,
  oledMode,
  onCategoriesUpdated,
}) => {
  const [categories, setCategories] = useState<TaskCategory[]>(storageService.getCategories());
  const [nameInput, setNameInput] = useState('');
  const [selectedColor, setSelectedColor] = useState<CategoryColor>('pink');
  const [editingId, setEditingId] = useState<string | null>(null);

  if (!isOpen) return null;

  const colorKeys: CategoryColor[] = ['pink', 'yellow', 'lavender', 'matcha', 'lilac', 'cyan', 'slate'];

  const refreshList = () => {
    setCategories(storageService.getCategories());
    onCategoriesUpdated();
  };

  const handleSaveCategory = (e: React.FormEvent) => {
    e.preventDefault();
    if (!nameInput.trim()) return;

    audioService.playChime('click');

    if (editingId) {
      storageService.updateCategory(editingId, {
        name: nameInput.trim(),
        color: selectedColor,
      });
      setEditingId(null);
    } else {
      storageService.addCategory(nameInput.trim(), selectedColor);
    }

    setNameInput('');
    setSelectedColor('pink');
    refreshList();
  };

  const handleStartEdit = (cat: TaskCategory) => {
    audioService.playChime('click');
    setEditingId(cat.id);
    setNameInput(cat.name);
    setSelectedColor(cat.color);
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setNameInput('');
    setSelectedColor('pink');
  };

  const handleDelete = (id: string) => {
    audioService.playChime('click');
    storageService.deleteCategory(id);
    if (editingId === id) {
      handleCancelEdit();
    }
    refreshList();
  };

  const currentColorConfig = CATEGORY_COLORS[selectedColor];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div 
        onClick={onClose}
        className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
      />

      {/* Modal Dialog */}
      <div className={`relative z-10 w-full max-w-md rounded-3xl p-5 shadow-2xl flex flex-col gap-4 max-h-[90vh] overflow-y-auto no-scrollbar border transition-colors ${
        oledMode
          ? 'bg-neutral-900 border-neutral-800 text-white'
          : 'bg-white border-black/10 text-[#1C1B1B]'
      }`}>
        {/* Header */}
        <div className="flex items-center justify-between pb-1 border-b border-black/5 dark:border-neutral-800">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-[#F5ACD7] text-[#1C1B1B] flex items-center justify-center">
              <span className="material-symbols-outlined text-[18px]">sell</span>
            </div>
            <div>
              <h2 className="font-headline font-bold text-base">Custom Task Tags & Badges</h2>
              <p className="text-[11px] text-neutral-500">
                Define categories with persistent pastel color badges
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-neutral-500 hover:text-neutral-900 dark:hover:text-white transition-colors"
          >
            ✕
          </button>
        </div>

        {/* Add / Edit Form */}
        <form onSubmit={handleSaveCategory} className="flex flex-col gap-3 p-3.5 rounded-2xl bg-neutral-100/80 dark:bg-neutral-950 border border-black/5 dark:border-neutral-800">
          <div className="flex items-center justify-between">
            <span className="text-[10px] uppercase font-bold tracking-wider text-neutral-500">
              {editingId ? 'Edit Tag' : 'Create New Tag'}
            </span>

            {/* Live Preview Badge */}
            <div className="flex items-center gap-1.5">
              <span className="text-[10px] text-neutral-400">Preview:</span>
              <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold shadow-xs border ${currentColorConfig.badgeBg} ${currentColorConfig.badgeText} ${currentColorConfig.badgeBorder}`}>
                {nameInput.trim() || 'Sample Tag'}
              </span>
            </div>
          </div>

          {/* Name Input */}
          <input
            type="text"
            value={nameInput}
            onChange={(e) => setNameInput(e.target.value)}
            placeholder="Tag name (e.g., Deep Work, Health, Client)..."
            className={`w-full h-11 px-3.5 rounded-xl text-xs font-semibold outline-none transition-all shadow-xs ${
              oledMode
                ? 'bg-neutral-900 text-white placeholder:text-neutral-500 focus:ring-2 focus:ring-[#F5ACD7]'
                : 'bg-white text-[#1C1B1B] placeholder:text-neutral-400 focus:ring-2 focus:ring-[#1C1B1B]'
            }`}
            autoFocus
          />

          {/* Curated Color Swatches */}
          <div className="flex flex-col gap-1.5">
            <span className="text-[10px] font-bold text-neutral-500">Select Badge Color</span>
            <div className="flex items-center justify-between gap-1 py-1">
              {colorKeys.map((colorKey) => {
                const conf = CATEGORY_COLORS[colorKey];
                const isSelected = selectedColor === colorKey;
                return (
                  <button
                    key={colorKey}
                    type="button"
                    onClick={() => {
                      audioService.playChime('click');
                      setSelectedColor(colorKey);
                    }}
                    title={conf.label}
                    className={`w-8 h-8 rounded-full transition-all flex items-center justify-center cursor-pointer shadow-xs ${
                      isSelected
                        ? 'ring-3 ring-[#1C1B1B] dark:ring-white scale-110'
                        : 'opacity-85 hover:opacity-100 hover:scale-105'
                    }`}
                    style={{ backgroundColor: conf.preview }}
                  >
                    {isSelected && (
                      <span className="material-symbols-outlined text-[14px] text-[#1C1B1B] font-bold">
                        check
                      </span>
                    )}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Form Actions */}
          <div className="flex items-center gap-2 pt-1">
            <button
              type="submit"
              disabled={!nameInput.trim()}
              className="flex-1 h-10 rounded-full bg-[#1C1B1B] text-white text-xs font-bold shadow-xs active:scale-95 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
            >
              {editingId ? 'Save Tag Changes' : '+ Add Custom Tag'}
            </button>

            {editingId && (
              <button
                type="button"
                onClick={handleCancelEdit}
                className="px-3.5 h-10 rounded-full bg-neutral-200 dark:bg-neutral-800 text-xs font-bold text-neutral-700 dark:text-neutral-300 active:scale-95 transition-all"
              >
                Cancel
              </button>
            )}
          </div>
        </form>

        {/* Categories List */}
        <div className="flex flex-col gap-2">
          <div className="flex items-center justify-between px-1">
            <span className="text-[10px] uppercase font-bold tracking-wider text-neutral-500">
              Active Task Tags ({categories.length})
            </span>
            <span className="text-[10px] text-neutral-400">Persisted locally</span>
          </div>

          <div className="flex flex-col gap-1.5 max-h-52 overflow-y-auto no-scrollbar pr-0.5">
            {categories.map((cat) => {
              const conf = CATEGORY_COLORS[cat.color] || CATEGORY_COLORS.slate;
              const isCurrentlyEditing = editingId === cat.id;

              return (
                <div
                  key={cat.id}
                  className={`flex items-center justify-between p-2.5 rounded-2xl border transition-all ${
                    isCurrentlyEditing
                      ? 'ring-2 ring-[#864B70] bg-[#FFD8EC]/30'
                      : oledMode
                        ? 'bg-neutral-950 border-neutral-800'
                        : 'bg-neutral-50 border-black/5 hover:bg-neutral-100'
                  }`}
                >
                  <div className="flex items-center gap-2.5 min-w-0">
                    <span
                      className="w-4 h-4 rounded-full shrink-0 shadow-xs ring-1 ring-black/10"
                      style={{ backgroundColor: conf.preview }}
                    />
                    <div className="flex items-center gap-1.5 min-w-0">
                      <span className="text-xs font-bold truncate">{cat.name}</span>
                      <span className={`px-2 py-0.2 rounded-full text-[10px] font-semibold border ${conf.badgeBg} ${conf.badgeText} ${conf.badgeBorder}`}>
                        {conf.label}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => handleStartEdit(cat)}
                      className="w-7 h-7 rounded-full bg-neutral-200 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 flex items-center justify-center text-xs hover:bg-neutral-300 transition-colors"
                      title="Edit tag"
                    >
                      <span className="material-symbols-outlined text-[14px]">edit</span>
                    </button>

                    <button
                      onClick={() => handleDelete(cat.id)}
                      className="w-7 h-7 rounded-full bg-[#FFDAD6] text-[#93000A] flex items-center justify-center text-xs hover:bg-red-200 transition-colors"
                      title="Delete tag"
                    >
                      <span className="material-symbols-outlined text-[14px]">delete</span>
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Footer */}
        <div className="pt-2 border-t border-black/5 dark:border-neutral-800 flex justify-end">
          <button
            onClick={onClose}
            className="w-full h-11 rounded-full bg-neutral-200 dark:bg-neutral-800 text-xs font-bold text-neutral-700 dark:text-neutral-200 hover:bg-neutral-300 active:scale-95 transition-all"
          >
            Done
          </button>
        </div>
      </div>
    </div>
  );
};
