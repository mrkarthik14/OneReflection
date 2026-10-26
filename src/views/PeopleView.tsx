import React, { useState } from 'react';
import { audioService } from '../services/audioService';
import { storageService } from '../services/storageService';
import { ScreenId } from '../types';

interface PeopleViewProps {
  onNavigate: (screen: ScreenId) => void;
  onOpenVoiceStudio: () => void;
  oledMode: boolean;
}

export const PeopleView: React.FC<PeopleViewProps> = ({
  onNavigate,
  onOpenVoiceStudio,
  oledMode,
}) => {
  const [copied, setCopied] = useState(false);
  const [pendingDismissed, setPendingDismissed] = useState(false);
  const [mayaVoiceAlarms, setMayaVoiceAlarms] = useState(true);
  const [mayaSharedHabits, setMayaSharedHabits] = useState(true);
  const [mayaRhythmPings, setMayaRhythmPings] = useState(true);

  const handleCopyCode = () => {
    audioService.playChime('click');
    navigator.clipboard?.writeText('REFLECTION-MY-8924').catch(() => {});
    setCopied(true);
    setTimeout(() => setCopied(false), 2400);
  };

  const handleAcceptJulian = () => {
    audioService.playChime('solfeggio');
    setPendingDismissed(true);
  };

  return (
    <div className={`flex flex-col w-full px-4 pb-12 gap-y-5 transition-colors ${
      oledMode ? 'text-white' : 'text-[#1C1B1B]'
    }`}>
      {/* 1. Header & Trust Eyebrow */}
      <section className="flex flex-col gap-1 pt-2">
        <div className="inline-flex items-center gap-1.5 self-start px-3 py-1 rounded-full bg-[#FCE275] text-[#221B00] text-[10px] font-bold shadow-xs rotate-[-1deg]">
          <span className="material-symbols-outlined text-[13px]">verified_user</span>
          <span>TRUSTED CIRCLE • SAFE SYNC</span>
        </div>

        <h1 className="font-headline text-3xl font-bold tracking-tight mt-0.5">
          Better together.
        </h1>
        <p className="text-xs text-neutral-500 leading-relaxed max-w-sm">
          Invite your closest souls, share playful voice wake-ups, and sync daily habits in warm harmony.
        </p>
      </section>

      {/* 2. Hero Bento Card: Personal Invite & Stylized QR Code */}
      <section className="relative overflow-hidden rounded-3xl bg-[#F5ACD7] p-5 text-[#1C1B1B] shadow-sm border border-black/10">
        <div className="relative z-10 flex flex-col items-center text-center gap-3.5">
          {/* Top Stamp */}
          <div className="flex items-center justify-between w-full">
            <span className="px-3 py-1 rounded-full bg-white text-xs font-bold shadow-xs">
              Your Sync Key
            </span>
            <div className="w-8 h-8 rounded-full bg-[#F9DF72] flex items-center justify-center text-[#736200] shadow-xs rotate-6">
              <span className="material-symbols-outlined text-[18px]">favorite</span>
            </div>
          </div>

          {/* Stylized QR Code Component */}
          <div className="p-3 bg-white rounded-2xl shadow-xs flex flex-col items-center justify-center relative border border-black/5">
            <div className="relative w-40 h-40 flex items-center justify-center bg-[#F6F3F2] rounded-xl p-2">
              <svg className="w-full h-full text-[#1C1B1B]" fill="currentColor" viewBox="0 0 160 160">
                {/* QR Corner Targets */}
                <rect x="10" y="10" width="40" height="40" rx="8" fill="currentColor" fillOpacity="0.9" />
                <rect x="18" y="18" width="24" height="24" rx="4" fill="#ffffff" />
                <rect x="24" y="24" width="12" height="12" rx="2" fill="currentColor" />

                <rect x="110" y="10" width="40" height="40" rx="8" fill="currentColor" fillOpacity="0.9" />
                <rect x="118" y="18" width="24" height="24" rx="4" fill="#ffffff" />
                <rect x="124" y="24" width="12" height="12" rx="2" fill="currentColor" />

                <rect x="10" y="110" width="40" height="40" rx="8" fill="currentColor" fillOpacity="0.9" />
                <rect x="18" y="118" width="24" height="24" rx="4" fill="#ffffff" />
                <rect x="24" y="124" width="12" height="12" rx="2" fill="currentColor" />

                {/* Pattern Blocks */}
                <circle cx="68" cy="22" r="5" fill="currentColor" />
                <circle cx="88" cy="22" r="4" fill="currentColor" />
                <circle cx="78" cy="38" r="6" fill="currentColor" />
                <rect x="62" y="52" width="10" height="10" rx="3" fill="currentColor" />
                <rect x="86" y="54" width="12" height="8" rx="2" fill="currentColor" />
                <circle cx="28" cy="74" r="5" fill="currentColor" />
                <circle cx="46" cy="82" r="6" fill="currentColor" />
                <rect x="18" y="90" width="14" height="8" rx="2" fill="currentColor" />
                <circle cx="120" cy="68" r="5" fill="currentColor" />
                <circle cx="138" cy="80" r="6" fill="currentColor" />
                <rect x="112" y="92" width="12" height="10" rx="2" fill="currentColor" />
                <circle cx="66" cy="116" r="6" fill="currentColor" />
                <circle cx="86" cy="120" r="4" fill="currentColor" />
                <rect x="70" y="132" width="18" height="12" rx="3" fill="currentColor" />
                <circle cx="120" cy="124" r="5" fill="currentColor" />
                <circle cx="136" cy="138" r="6" fill="currentColor" />
              </svg>

              {/* Center Emblem */}
              <div className="absolute inset-0 m-auto w-10 h-10 rounded-full bg-[#F9DF72] flex items-center justify-center shadow-md">
                <span className="material-symbols-outlined text-[20px] text-[#736200]" style={{ fontVariationSettings: "'FILL' 1" }}>
                  sensors
                </span>
              </div>
            </div>
            <div className="mt-2 text-[10px] font-bold text-neutral-500 tracking-wider uppercase">
              Scan to Sync
            </div>
          </div>

          {/* Unique Code Pill */}
          <div className="w-full flex flex-col items-center gap-1">
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white shadow-xs">
              <span className="font-headline text-xs font-bold tracking-widest text-[#1C1B1B]">
                REFLECTION-MY-8924
              </span>
              <button
                onClick={handleCopyCode}
                className="w-6 h-6 rounded-full bg-[#1C1B1B] text-white flex items-center justify-center hover:scale-105 active:scale-95 transition-transform"
                title="Copy code"
              >
                <span className="material-symbols-outlined text-[13px]">
                  {copied ? 'check' : 'content_copy'}
                </span>
              </button>
            </div>
            <span className={`text-[11px] font-bold text-[#753C61] h-3 transition-opacity ${copied ? 'opacity-100' : 'opacity-0'}`}>
              Code copied to clipboard!
            </span>
          </div>

          {/* Buttons */}
          <div className="grid grid-cols-2 gap-2.5 w-full">
            <button
              onClick={handleCopyCode}
              className="h-12 px-4 rounded-full bg-[#1C1B1B] text-white text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs active:scale-95 transition-transform"
            >
              <span className="material-symbols-outlined text-[16px]">share</span>
              <span>Share Link</span>
            </button>
            <button
              onClick={() => audioService.playChime('click')}
              className="h-12 px-4 rounded-full bg-white text-[#1C1B1B] text-xs font-bold flex items-center justify-center gap-1.5 shadow-xs active:scale-95 transition-transform"
            >
              <span className="material-symbols-outlined text-[16px]">qr_code_scanner</span>
              <span>Scan Friend</span>
            </button>
          </div>

          {/* Footnote */}
          <div className="flex items-center gap-1 px-3 py-1 rounded-full bg-white/70 text-xs font-semibold text-neutral-600">
            <span className="material-symbols-outlined text-[14px] text-[#864B70]">security</span>
            <span>Alarms & voice notes require explicit mutual consent.</span>
          </div>
        </div>
      </section>

      {/* 3. Pending Requests */}
      {!pendingDismissed && (
        <section className="flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <h2 className="font-headline text-base font-bold">Pending Requests</h2>
              <span className="w-5 h-5 rounded-full bg-[#FFD8EC] text-[#864B70] text-[10px] font-bold flex items-center justify-center">
                1
              </span>
            </div>
            <button
              onClick={() => setPendingDismissed(true)}
              className="text-xs font-bold text-[#864B70] dark:text-[#F5ACD7] hover:underline"
            >
              Clear
            </button>
          </div>

          <div className={`p-4 rounded-3xl border shadow-xs flex flex-col gap-3 ${
            oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
          }`}>
            <div className="flex items-center gap-3">
              <div className="relative">
                <div className="w-11 h-11 rounded-full bg-[#DAE2FF] text-[#091A3E] flex items-center justify-center font-headline font-bold text-sm">
                  JV
                </div>
                <span className="absolute bottom-0 right-0 w-3.5 h-3.5 rounded-full bg-[#F9DF72] flex items-center justify-center text-[8px]">
                  ✨
                </span>
              </div>
              <div className="flex-1 min-w-0">
                <span className="font-headline font-bold text-sm block truncate">Julian Vance</span>
                <span className="text-xs text-neutral-500">Connected with Leo Chen</span>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={handleAcceptJulian}
                className="flex-1 h-10 rounded-full bg-[#1C1B1B] text-white text-xs font-bold flex items-center justify-center gap-1 active:scale-95 shadow-xs"
              >
                <span className="material-symbols-outlined text-[15px]">check</span>
                <span>Accept & Sync</span>
              </button>
              <button
                onClick={() => setPendingDismissed(true)}
                className="h-10 px-4 rounded-full bg-neutral-200 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300 text-xs font-bold active:scale-95"
              >
                Dismiss
              </button>
            </div>
          </div>
        </section>
      )}

      {/* 4. Your Inner Circle */}
      <section className="flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h2 className="font-headline text-base font-bold">Your Inner Circle</h2>
            <span className="px-2 py-0.5 rounded-full bg-[#FCE275] text-[#221B00] text-[10px] font-bold">
              3 active souls
            </span>
          </div>
        </div>

        {/* Member 1: Maya R. (Butter Yellow Bento) */}
        <div className="rounded-3xl bg-[#F9DF72] text-[#1C1B1B] p-4 shadow-xs flex flex-col gap-3 border border-black/10">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <div className="relative">
                <img
                  src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                  alt="Maya"
                  className="w-12 h-12 rounded-full object-cover ring-2 ring-white"
                />
                <span className="absolute bottom-0 right-0 w-3 h-3 rounded-full bg-emerald-500 ring-2 ring-white"></span>
              </div>
              <div className="flex flex-col">
                <div className="flex items-center gap-1.5">
                  <span className="font-headline font-bold text-sm">Maya R.</span>
                  <span className="px-2 py-0.2 rounded-full bg-white text-[#736200] text-[9px] font-bold">
                    Partner • Deep Sync
                  </span>
                </div>
                <span className="text-[11px] text-[#736200] font-semibold mt-0.5">
                  Awake & synced at 06:45 AM
                </span>
              </div>
            </div>

            <button
              onClick={() => onNavigate('bedtime-sync')}
              className="w-8 h-8 rounded-full bg-white/80 flex items-center justify-center hover:bg-white transition-colors"
            >
              <span className="material-symbols-outlined text-[16px]">tune</span>
            </button>
          </div>

          {/* Granular Permissions */}
          <div className="flex flex-col gap-1.5">
            <div className="flex items-center justify-between p-2.5 rounded-2xl bg-white shadow-xs">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-[#864B70]">mic</span>
                <span className="text-xs font-bold">Voice Alarms</span>
              </div>
              <button
                onClick={() => setMayaVoiceAlarms(!mayaVoiceAlarms)}
                className={`w-6 h-6 rounded-full flex items-center justify-center ${
                  mayaVoiceAlarms ? 'bg-[#F9DF72] text-[#736200]' : 'bg-neutral-200 text-neutral-400'
                }`}
              >
                <span className="material-symbols-outlined text-[14px] font-bold">check</span>
              </button>
            </div>

            <div className="flex items-center justify-between p-2.5 rounded-2xl bg-white shadow-xs">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-[#4F5D85]">checklist</span>
                <span className="text-xs font-bold">Shared Habits & Tasks</span>
              </div>
              <button
                onClick={() => setMayaSharedHabits(!mayaSharedHabits)}
                className={`w-6 h-6 rounded-full flex items-center justify-center ${
                  mayaSharedHabits ? 'bg-[#F9DF72] text-[#736200]' : 'bg-neutral-200 text-neutral-400'
                }`}
              >
                <span className="material-symbols-outlined text-[14px] font-bold">check</span>
              </button>
            </div>

            <div className="flex items-center justify-between p-2.5 rounded-2xl bg-white shadow-xs">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-[#6F5D00]">notifications_active</span>
                <span className="text-xs font-bold">Rhythm Pings</span>
              </div>
              <button
                onClick={() => setMayaRhythmPings(!mayaRhythmPings)}
                className={`w-6 h-6 rounded-full flex items-center justify-center ${
                  mayaRhythmPings ? 'bg-[#F9DF72] text-[#736200]' : 'bg-neutral-200 text-neutral-400'
                }`}
              >
                <span className="material-symbols-outlined text-[14px] font-bold">check</span>
              </button>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-2">
            <button
              onClick={() => onNavigate('chats')}
              className="h-10 rounded-full bg-[#1C1B1B] text-white text-xs font-bold flex items-center justify-center gap-1 shadow-xs active:scale-95"
            >
              <span className="material-symbols-outlined text-[15px]">record_voice_over</span>
              <span>Voice Ping</span>
            </button>
            <button
              onClick={() => onNavigate('bedtime-sync')}
              className="h-10 rounded-full bg-white text-[#1C1B1B] text-xs font-bold flex items-center justify-center gap-1 shadow-xs active:scale-95"
            >
              <span className="material-symbols-outlined text-[15px]">tune</span>
              <span>Manage Access</span>
            </button>
          </div>
        </div>

        {/* Member 2: Leo Chen (Lavender Blue) */}
        <div className="rounded-3xl bg-[#DAE2FF] text-[#091A3E] p-4 shadow-xs flex flex-col gap-2.5 border border-[#4F5D85]/20">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU"
                alt="Leo"
                className="w-12 h-12 rounded-full object-cover ring-2 ring-white"
              />
              <div className="flex flex-col">
                <span className="font-headline font-bold text-sm">Leo Chen</span>
                <span className="text-[11px] text-[#38466C] font-semibold">
                  Focus Session • 45m left
                </span>
              </div>
            </div>
            <span className="px-2 py-0.5 rounded-full bg-white text-[#091A3E] text-[10px] font-bold">
              Studio Sprint
            </span>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => onNavigate('tasks')}
              className="flex-1 h-9 rounded-full bg-[#1C1B1B] text-white text-xs font-bold flex items-center justify-center gap-1 active:scale-95 shadow-xs"
            >
              <span className="material-symbols-outlined text-[14px]">send</span>
              <span>Sync Habit</span>
            </button>
            <button
              onClick={() => onNavigate('bedtime-sync')}
              className="h-9 px-4 rounded-full bg-white text-[#091A3E] text-xs font-bold active:scale-95 shadow-xs"
            >
              Permissions
            </button>
          </div>
        </div>

        {/* Member 3: Sofia Miller (Soft Matcha) */}
        <div className={`rounded-3xl p-4 shadow-xs flex flex-col gap-2.5 border transition-colors ${
          oledMode ? 'bg-neutral-900 border-neutral-800 text-white' : 'bg-[#EAE7E7] border-black/[0.04] text-[#1C1B1B]'
        }`}>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <img
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704"
                alt="Sofia"
                className="w-12 h-12 rounded-full object-cover ring-2 ring-white"
              />
              <div className="flex flex-col">
                <span className="font-headline font-bold text-sm">Sofia Miller</span>
                <span className="text-[11px] text-neutral-500">
                  Completed Hydration Streak (7d)
                </span>
              </div>
            </div>
            <span className="px-2 py-0.5 rounded-full bg-white dark:bg-neutral-800 text-xs font-bold">
              Ritual Pal
            </span>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => {
                audioService.playChime('water');
                onNavigate('chats');
              }}
              className="flex-1 h-9 rounded-full bg-[#1C1B1B] text-white text-xs font-bold flex items-center justify-center gap-1 active:scale-95 shadow-xs"
            >
              <span className="material-symbols-outlined text-[14px]">celebration</span>
              <span>Send High Five</span>
            </button>
            <button
              onClick={() => onNavigate('bedtime-sync')}
              className="h-9 px-4 rounded-full bg-white dark:bg-neutral-800 text-xs font-bold active:scale-95 shadow-xs"
            >
              Adjust
            </button>
          </div>
        </div>
      </section>

      {/* 5. Security & Control Footnote */}
      <div className={`p-4 rounded-2xl text-center flex flex-col items-center gap-1 border ${
        oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
      }`}>
        <div className="flex items-center gap-1.5 text-xs font-bold text-[#864B70] dark:text-[#F5ACD7]">
          <span className="material-symbols-outlined text-[16px]">lock_reset</span>
          <span>End-to-end encrypted reflection mesh</span>
        </div>
        <p className="text-[11px] text-neutral-500 max-w-xs leading-relaxed">
          You are always in complete control. Revoke any voice alarm or habit access instantly from any card.
        </p>
      </div>
    </div>
  );
};
