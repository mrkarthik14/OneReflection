# Custom Task Category Management System

A category and tag management system for tasks in OneReflection, allowing users to define, edit, and delete custom categories with curated pastel bento color badges that persist across sessions in local storage and drive dynamic filter pills.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following choices were confirmed during Phase 1:
> - **Access Point**: Dedicated category management modal accessible directly from the Tasks view and the filter pill bar via a `+ Manage Tags` button.
> - **Badge Styling & Palette**: Curated pastel bento colors matching the OneReflection design system (Candy Pink, Butter Yellow, Lavender Blue, Soft Matcha, Warm Lilac, Soft Cyan, and Surface Slate) ensuring high contrast and harmonious aesthetic.
> - **Task Organization**: Dynamic horizontal filter pill bar with real-time task counters, badge color indicators, and active state transitions.

---

### 1. Overview & Core Concept

- **What It Does**: Enables users to personalize their workflow by creating, editing, and deleting task categories (e.g., 'Work', 'Personal', 'Health', 'Studio Dev', 'Fitness', 'Home'). Each category is assigned a name and a curated pastel color badge.
- **Task Association**: When creating or viewing tasks, users can select from their custom categories. The task card dynamically inherits the category's pastel background tint, contrasting text, and tag badge.
- **Persistence & Synchronization**: Categories are stored in `localStorage` under `oner_categories` with default seeds ('Work', 'Personal', 'Health', 'Studio Dev', 'Shared Errands', 'Ritual') and reactively update all filter pills and task cards without page refresh.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Managing Categories from Filter Bar**:
   - In `TasksView`, the horizontal filter bar displays active filter pills alongside a tactile `Manage Tags` / `+ Category` chip.
   - Tapping it opens the `CategoryManagerModal`.
2. **Creating a Custom Category**:
   - Inside the modal, users enter a category name (e.g., "Health", "Deep Work", "Client Studio").
   - A row of 7 curated pastel circular color swatches allows instant selection with active ring feedback:
     - 🌸 **Candy Pink**: `#F5ACD7` (Text `#753C61`, Border `#F5ACD7`)
     - 🧈 **Butter Yellow**: `#F9DF72` (Text `#736200`, Border `#F9DF72`)
     - 🫐 **Lavender Blue**: `#DAE2FF` (Text `#091A3E`, Border `#DAE2FF`)
     - 🍵 **Soft Matcha**: `#B8E98E` (Text `#2A4413`, Border `#B8E98E`)
     - 🪻 **Warm Lilac**: `#E7A2EE` (Text `#531858`, Border `#E7A2EE`)
     - 🌊 **Soft Cyan**: `#A5E8EF` (Text `#0A4952`, Border `#A5E8EF`)
     - 🪨 **Surface Slate**: `#EAE7E7` (Text `#313030`, Border `#EAE7E7`)
   - Tapping "Add Category" triggers a gentle haptic sound and immediately saves to storage.
3. **Editing & Deleting Categories**:
   - Each category in the modal list displays an edit button (to rename or change badge color) and a delete button with safe reassignment warning.
4. **Filtering Tasks by Custom Category**:
   - Selecting any filter pill in `TasksView` filters the visible active tasks in real time.
   - The pill reflects the category's pastel accent color with an active dot indicator and exact count badge.
5. **Assigning Category During Task Creation**:
   - The "Add New Task" modal features a horizontal category selector displaying all custom categories as selectable pastel chips.

---

### 3. Key Product Decisions & Trade-Offs

- **Curated Palette vs. Unrestricted Hex Picker**:
  - *Chosen Approach*: 7 curated pastel tokens matching the Playful Editorial Bento design system.
  - *Why*: Unrestricted hex codes often lead to clashing contrasts and illegible text; curated bento colors guarantee high WCAG legibility, pastel warmth, and cohesive aesthetics.
- **Dynamic Task Card Styling**:
  - *Chosen Approach*: Task cards dynamically apply their category's pastel background tint and badge color rather than a static fallback.
  - *Why*: Gives immediate visual scanning cues on mobile devices without relying on tiny text labels.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Category Management Flow                        │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  ┌──────────────────────────────┐    ┌──────────────────────────────┐  │
│  │   TasksView Filter Pill Bar  │    │     "Add Task" Dialog        │  │
│  │   [All] [Work] [Personal] +  │    │  Selectable Category Chips   │  │
│  └──────────────┬───────────────┘    └──────────────┬───────────────┘  │
│                 │                                   │                  │
│                 ▼                                   ▼                  │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                     CategoryManagerModal                         │  │
│  │  • Add Category Form (Name + Color Palette Swatches)             │  │
│  │  • Category List (Badge Preview, Rename, Color Edit, Delete)     │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
│                                     │                                  │
│                                     ▼                                  │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │               StorageService (Reactive Local Storage)            │  │
│  │  • getCategories(), addCategory(), updateCategory(),             │  │
│  │    deleteCategory()                                              │  │
│  │  • LocalStorage key: 'oner_categories'                           │  │
│  │  • Reactive listener subscriptions across components             │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

#### Updated Data Entities
```typescript
export interface TaskCategory {
  id: string;
  name: string;
  color: 'pink' | 'yellow' | 'lavender' | 'matcha' | 'lilac' | 'cyan' | 'slate';
  bgClass: string;
  textClass: string;
  borderClass: string;
  icon?: string;
  isDefault?: boolean;
}

export interface TaskItem {
  id: string;
  title: string;
  description: string;
  categoryId: string; // References TaskCategory.id
  dueTime: string;
  completed: boolean;
  subtasksDone?: number;
  subtasksTotal?: number;
  voiceBriefQuote?: string;
  voiceBriefDuration?: string;
  assignees?: { name: string; avatar?: string; initials?: string }[];
  codeBranch?: string;
  priority?: 'High' | 'Normal';
  streak?: number;
}
```

---

### 5. Implementation Sequence

1. **Type Definitions & Theme Tokens**:
   - Update `src/types.ts` to add `TaskCategory` and link `TaskItem.categoryId`.
2. **Storage Service Enhancements**:
   - Update `src/services/storageService.ts` to store `oner_categories`, seed default categories (Work, Personal, Health, Studio Dev, Shared Errands, Ritual), and expose full CRUD methods (`addCategory`, `updateCategory`, `deleteCategory`).
3. **Category Manager Modal Component**:
   - Create `src/components/CategoryManagerModal.tsx` with color palette selector, preview badge, and edit/delete actions.
4. **Tasks View & Creation Modal Integration**:
   - Update `src/views/TasksView.tsx`:
     - Render dynamic filter pills with custom badges and live task counters.
     - Add "+ Manage" button in filter bar.
     - Update task creation dialog with category selector chips.
     - Apply category color styling to task bento cards.
5. **Verification**:
   - Verify compilation with `compile_applet` and linting with `lint_applet`.
