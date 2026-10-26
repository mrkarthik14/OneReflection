import React, { useState } from 'react';
import { audioService } from '../services/audioService';

interface BedtimeSyncModalProps {
  isOpen: boolean;
  onClose: () => void;
  oledMode: boolean;
}

export const BedtimeSyncModal: React.FC<BedtimeSyncModalProps> = ({
  isOpen,
  onClose,
  oledMode,
}) => {
  const [bedtimeMinutes, setBedtimeMinutes] = useState(22 * 60 + 30); // 22:30
  const [wakeMinutes, setWakeMinutes] = useState(7 * 60); // 07:00
  const [syncWithPartner, setSyncWithPartner] = useState(true);
  const [mayaEmergency, setMayaEmergency] = useState(true);
  const [mayaWhispers, setMayaWhispers] = useState(true);
  const [mayaWakeQueue, setMayaWakeQueue] = useState(true);
  const [sensitivity, setSensitivity] = useState<'critical' | 'gentle' | 'all'>('gentle');
  const [leoEmergency, setLeoEmergency] = useState(true);
  const [leoMilestones, setLeoMilestones] = useState(false);
  const [familyCalls, setFamilyCalls] = useState(true);
  const [selectedVibe, setSelectedVibe] = useState<'tibetan' | 'piano' | 'river'>('tibetan');
  const [haptics, setHaptics] = useState(true);

  if (!isOpen) return null;

  const formatMinutes = (totalMins: number) => {
    let normalized = totalMins % (24 * 60);
    if (normalized < 0) normalized += 24 * 60;
    const h = Math.floor(normalized / 60);
    const m = normalized % 60;
    return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
  };

  const handleAdjustBedtime = (delta: number) => {
    audioService.playChime('click');
    setBedtimeMinutes((prev) => prev + delta);
  };

  const handleAdjustWake = (delta: number) => {
    audioService.playChime('click');
    setWakeMinutes((prev) => prev + delta);
  };

  const handleSave = () => {
    audioService.playChime('tibetan');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex flex-col justify-end">
      {/* Dimmed backdrop */}
      <div 
        onClick={onClose}
        className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
      />

      <div className={`relative z-10 w-full max-w-lg mx-auto rounded-t-[32px] shadow-2xl flex flex-col max-h-[92vh] overflow-y-auto no-scrollbar border-t pb-safe transition-colors ${
        oledMode ? 'bg-[#0A0A0A] border-neutral-800 text-white' : 'bg-white border-black/10 text-[#1C1B1B]'
      }`}>
        {/* Grab bar */}
        <div className="w-full flex justify-center py-2.5">
          <div className="w-12 h-1.5 rounded-full bg-neutral-300 dark:bg-neutral-700" />
        </div>

        {/* Modal Header */}
        <div className="px-5 pb-3 flex flex-col gap-1.5">
          <div className="flex items-center justify-between">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#DAE2FF] text-[#091A3E] font-bold text-[10px] shadow-xs rotate-[-1deg]">
              <span className="material-symbols-outlined text-[14px]">bedtime</span>
              <span>BEDTIME HARMONY • MUTUAL REST</span>
            </div>

            <button
              onClick={onClose}
              aria-label="Close modal"
              className="w-8 h-8 rounded-full bg-neutral-100 dark:bg-neutral-800 flex items-center justify-center text-neutral-600 dark:text-neutral-300 hover:bg-neutral-200 transition-colors"
            >
              <span className="material-symbols-outlined text-[18px]">close</span>
            </button>
          </div>

          <div className="mt-1">
            <h1 className="font-headline text-2xl font-bold tracking-tight">
              Quiet Hours & Bedtime Sync.
            </h1>
            <p className="text-xs text-neutral-500 leading-relaxed mt-0.5">
              Protect your restorative sleep while keeping delicate audio bridges open for those who matter most.
            </p>
          </div>
        </div>

        {/* Modal Body */}
        <div className="px-5 flex flex-col gap-4 pb-8">
          {/* Bento 1: Global Sleep Schedule */}
          <section className="bg-[#FFD8EC]/35 rounded-3xl p-4 flex flex-col gap-3 border border-black/5 relative overflow-hidden">
            <div className="flex items-center justify-between">
              <span className="text-[10px] uppercase font-bold tracking-wider text-[#753C61] flex items-center gap-1">
                <span className="material-symbols-outlined text-[15px]">schedule</span>
                Global Rest Horizon
              </span>
              <div className="px-2.5 py-0.5 rounded-full bg-white/90 text-xs font-bold text-[#1C1B1B] shadow-xs">
                Auto-Adaptive
              </div>
            </div>

            {/* Steppers */}
            <div className="grid grid-cols-2 gap-2.5">
              {/* Bedtime Silence */}
              <div className="bg-white dark:bg-neutral-900 rounded-2xl p-3 flex flex-col gap-1 shadow-xs border border-black/5">
                <div className="flex items-center gap-1.5 text-neutral-500 text-[10px] font-bold uppercase">
                  <span className="material-symbols-outlined text-[16px] text-[#864B70]">nights_stay</span>
                  <span>Bedtime Silence</span>
                </div>
                <div className="flex items-center justify-between pt-1">
                  <span className="font-headline text-2xl font-bold">
                    {formatMinutes(bedtimeMinutes)}
                  </span>
                  <div className="flex items-center gap-1 bg-neutral-100 dark:bg-neutral-800 rounded-full p-0.5">
                    <button
                      onClick={() => handleAdjustBedtime(-15)}
                      className="w-6 h-6 rounded-full bg-white dark:bg-neutral-700 flex items-center justify-center text-xs font-bold shadow-xs active:scale-90"
                    >
                      -
                    </button>
                    <button
                      onClick={() => handleAdjustBedtime(15)}
                      className="w-6 h-6 rounded-full bg-white dark:bg-neutral-700 flex items-center justify-center text-xs font-bold shadow-xs active:scale-90"
                    >
                      +
                    </button>
                  </div>
                </div>
              </div>

              {/* Wake Horizon */}
              <div className="bg-white dark:bg-neutral-900 rounded-2xl p-3 flex flex-col gap-1 shadow-xs border border-black/5">
                <div className="flex items-center gap-1.5 text-neutral-500 text-[10px] font-bold uppercase">
                  <span className="material-symbols-outlined text-[16px] text-[#736200]">wb_twilight</span>
                  <span>Wake Horizon</span>
                </div>
                <div className="flex items-center justify-between pt-1">
                  <span className="font-headline text-2xl font-bold">
                    {formatMinutes(wakeMinutes)}
                  </span>
                  <div className="flex items-center gap-1 bg-neutral-100 dark:bg-neutral-800 rounded-full p-0.5">
                    <button
                      onClick={() => handleAdjustWake(-15)}
                      className="w-6 h-6 rounded-full bg-white dark:bg-neutral-700 flex items-center justify-center text-xs font-bold shadow-xs active:scale-90"
                    >
                      -
                    </button>
                    <button
                      onClick={() => handleAdjustWake(15)}
                      className="w-6 h-6 rounded-full bg-white dark:bg-neutral-700 flex items-center justify-center text-xs font-bold shadow-xs active:scale-90"
                    >
                      +
                    </button>
                  </div>
                </div>
              </div>
            </div>

            {/* Duration Badge */}
            <div className="flex items-center gap-2 px-3 py-2 bg-[#F9DF72]/40 rounded-2xl text-[#736200] text-xs font-semibold">
              <span className="material-symbols-outlined text-[18px]">spa</span>
              <span>8h 30m uninterrupted peace • Gentle fade swells active</span>
            </div>

            {/* Mutual Sync Toggle */}
            <div className="flex items-center justify-between p-3 bg-white dark:bg-neutral-900 rounded-2xl shadow-xs border border-black/5">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-full bg-[#F9DF72] flex items-center justify-center text-[#736200]">
                  <span className="material-symbols-outlined text-[18px]">sync</span>
                </div>
                <div className="flex flex-col">
                  <span className="text-xs font-bold">Sync Sleep State with Maya</span>
                  <span className="text-[10px] text-neutral-500">
                    Calibrates quiet hours when either partner doses off
                  </span>
                </div>
              </div>

              <button
                onClick={() => setSyncWithPartner(!syncWithPartner)}
                className={`w-11 h-6 rounded-full p-0.5 flex items-center transition-colors ${
                  syncWithPartner ? 'bg-[#864B70]' : 'bg-neutral-300 dark:bg-neutral-700'
                }`}
              >
                <div
                  className={`w-5 h-5 rounded-full bg-white shadow-xs transition-transform ${
                    syncWithPartner ? 'translate-x-5' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>
          </section>

          {/* Bento 2: Member Breakthroughs */}
          <section className="flex flex-col gap-2">
            <div className="flex items-center justify-between">
              <h2 className="font-headline text-base font-bold">Circle Member Breakthroughs</h2>
              <span className="px-2 py-0.5 rounded-full bg-[#F9DF72] text-[#736200] font-bold text-[9px] uppercase tracking-wider">
                Granular Rules
              </span>
            </div>

            <p className="text-xs text-neutral-500">
              Specify exactly who can break through quiet hours, what audio format is allowed, and under what conditions.
            </p>

            {/* Member 1: Maya R. */}
            <div className="bg-[#F9DF72]/20 rounded-3xl p-4 flex flex-col gap-2.5 border border-[#736200]/20">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <img
                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
                    alt="Maya"
                    className="w-10 h-10 rounded-full object-cover ring-2 ring-[#F9DF72]"
                  />
                  <div className="flex flex-col">
                    <div className="flex items-center gap-1.5">
                      <span className="font-headline font-bold text-sm">Maya R.</span>
                      <span className="px-1.5 py-0.2 rounded-full bg-[#FFD8EC] text-[#37062A] text-[9px] font-bold">
                        Primary Partner
                      </span>
                    </div>
                    <span className="text-[10px] text-neutral-500">
                      Awake & Synced via Oura Loop
                    </span>
                  </div>
                </div>
                <span className="material-symbols-outlined text-[#864B70] text-[20px]">favorite</span>
              </div>

              {/* Controls */}
              <div className="flex flex-col gap-2 pt-1">
                <div className="bg-white dark:bg-neutral-900 p-2.5 rounded-2xl flex items-center justify-between shadow-xs">
                  <div className="flex flex-col pr-2">
                    <span className="text-xs font-bold">Emergency Breakthrough</span>
                    <span className="text-[10px] text-neutral-500">Bypass silence with high-priority chime</span>
                  </div>
                  <button
                    onClick={() => setMayaEmergency(!mayaEmergency)}
                    className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                      mayaEmergency ? 'bg-[#F9DF72]' : 'bg-neutral-300 dark:bg-neutral-700'
                    }`}
                  >
                    <div
                      className={`w-4 h-4 rounded-full bg-[#1C1B1B] shadow-xs transition-transform ${
                        mayaEmergency ? 'translate-x-5' : 'translate-x-0'
                      }`}
                    />
                  </button>
                </div>

                <div className="bg-white dark:bg-neutral-900 p-2.5 rounded-2xl flex items-center justify-between shadow-xs">
                  <div className="flex flex-col pr-2">
                    <div className="flex items-center gap-1">
                      <span className="text-xs font-bold">Gentle Voice Whispers</span>
                      <span className="px-1.5 py-0.2 rounded bg-neutral-100 dark:bg-neutral-800 text-[9px] font-bold">
                        Until 23:30
                      </span>
                    </div>
                    <span className="text-[10px] text-neutral-500">Soft audio capped at 20% max volume</span>
                  </div>
                  <button
                    onClick={() => setMayaWhispers(!mayaWhispers)}
                    className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                      mayaWhispers ? 'bg-[#F9DF72]' : 'bg-neutral-300 dark:bg-neutral-700'
                    }`}
                  >
                    <div
                      className={`w-4 h-4 rounded-full bg-[#1C1B1B] shadow-xs transition-transform ${
                        mayaWhispers ? 'translate-x-5' : 'translate-x-0'
                      }`}
                    />
                  </button>
                </div>

                <div className="bg-white dark:bg-neutral-900 p-2.5 rounded-2xl flex items-center justify-between shadow-xs">
                  <div className="flex flex-col pr-2">
                    <span className="text-xs font-bold">Wake-Up Audio Queue</span>
                    <span className="text-[10px] text-neutral-500">Can plant warm morning greetings for 07:00</span>
                  </div>
                  <button
                    onClick={() => setMayaWakeQueue(!mayaWakeQueue)}
                    className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                      mayaWakeQueue ? 'bg-[#F9DF72]' : 'bg-neutral-300 dark:bg-neutral-700'
                    }`}
                  >
                    <div
                      className={`w-4 h-4 rounded-full bg-[#1C1B1B] shadow-xs transition-transform ${
                        mayaWakeQueue ? 'translate-x-5' : 'translate-x-0'
                      }`}
                    />
                  </button>
                </div>

                {/* Sensitivity threshold */}
                <div className="bg-white dark:bg-neutral-900 p-2.5 rounded-2xl flex flex-col gap-1.5 shadow-xs">
                  <span className="text-[10px] font-bold uppercase tracking-wider text-neutral-500">
                    Breakthrough Sensitivity Threshold
                  </span>
                  <div className="grid grid-cols-3 gap-1 bg-neutral-100 dark:bg-neutral-800 p-1 rounded-full text-[10px] font-bold text-center">
                    <button
                      onClick={() => setSensitivity('critical')}
                      className={`py-1 rounded-full transition-all ${
                        sensitivity === 'critical' ? 'bg-[#1C1B1B] text-white shadow-xs' : 'text-neutral-500'
                      }`}
                    >
                      Critical Only
                    </button>
                    <button
                      onClick={() => setSensitivity('gentle')}
                      className={`py-1 rounded-full transition-all ${
                        sensitivity === 'gentle' ? 'bg-[#1C1B1B] text-white shadow-xs' : 'text-neutral-500'
                      }`}
                    >
                      Gentle Check-ins
                    </button>
                    <button
                      onClick={() => setSensitivity('all')}
                      className={`py-1 rounded-full transition-all ${
                        sensitivity === 'all' ? 'bg-[#1C1B1B] text-white shadow-xs' : 'text-neutral-500'
                      }`}
                    >
                      All Alarms
                    </button>
                  </div>
                </div>
              </div>
            </div>

            {/* Member 2: Leo Chen */}
            <div className="bg-[#DAE2FF]/30 rounded-3xl p-4 flex flex-col gap-2.5 border border-[#4F5D85]/20">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <img
                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU"
                    alt="Leo"
                    className="w-10 h-10 rounded-full object-cover ring-2 ring-[#B3C1EF]"
                  />
                  <div className="flex flex-col">
                    <div className="flex items-center gap-1.5">
                      <span className="font-headline font-bold text-sm">Leo Chen</span>
                      <span className="px-1.5 py-0.2 rounded-full bg-neutral-200 dark:bg-neutral-800 text-[9px] font-bold">
                        Studio Co-founder
                      </span>
                    </div>
                    <span className="text-[10px] text-neutral-500">Design Sprint & Releases</span>
                  </div>
                </div>
                <span className="material-symbols-outlined text-neutral-500 text-[20px]">work</span>
              </div>

              <div className="flex flex-col gap-2 pt-1">
                <div className="bg-white dark:bg-neutral-900 p-2.5 rounded-2xl flex items-center justify-between shadow-xs">
                  <div className="flex flex-col pr-2">
                    <span className="text-xs font-bold">Emergency Breakthrough</span>
                    <span className="text-[10px] text-neutral-500">Requires double-ping verification</span>
                  </div>
                  <button
                    onClick={() => setLeoEmergency(!leoEmergency)}
                    className={`w-10 h-5 rounded-full p-0.5 flex items-center transition-colors ${
                      leoEmergency ? 'bg-[#DAE2FF]' : 'bg-neutral-300 dark:bg-neutral-700'
                    }`}
                  >
                    <div
                      className={`w-4 h-4 rounded-full bg-[#091A3E] shadow-xs transition-transform ${
                        leoEmergency ? 'translate-x-5' : 'translate-x-0'
                      }`}
                    />
                  </button>
                </div>

                <div className="bg-white dark:bg-neutral-900 p-2.5 rounded-2xl flex items-center justify-between shadow-xs opacity-85">
                  <div className="flex flex-col pr-2">
                    <span className="text-xs font-bold flex items-center gap-1">
                      Voice Notes & Chat Audio <span className="material-symbols-outlined text-[13px]">lock</span>
                    </span>
                    <span className="text-[10px] text-neutral-500">Held silently in holding bay until 07:00</span>
                  </div>
                  <span className="px-2.5 py-0.5 rounded-full bg-neutral-200 dark:bg-neutral-800 text-[10px] font-bold">
                    LOCKED
                  </span>
                </div>
              </div>
            </div>
          </section>

          {/* Bento 3: Audio Vibe Tone & Haptics */}
          <section className={`p-4 rounded-3xl flex flex-col gap-2.5 border ${
            oledMode ? 'bg-neutral-900 border-neutral-800' : 'bg-[#F6F3F2] border-black/[0.04]'
          }`}>
            <div className="flex items-center justify-between">
              <span className="font-headline text-sm font-bold">Audio Vibe & Haptic Texture</span>
              <span className="material-symbols-outlined text-[#864B70] dark:text-[#F5ACD7] text-[18px]">
                graphic_eq
              </span>
            </div>
            <p className="text-xs text-neutral-500">
              Choose the tone that sounds whenever an approved breakthrough occurs during sleep.
            </p>

            <div className="flex flex-wrap gap-2 pt-1">
              {[
                { id: 'tibetan', label: 'Tibetan Bowl Swell (Soft)' },
                { id: 'piano', label: 'Warm Piano Chime' },
                { id: 'river', label: 'Ambient River Glow' },
              ].map((tone) => (
                <button
                  key={tone.id}
                  onClick={() => {
                    audioService.playChime(tone.id as typeof selectedVibe);
                    setSelectedVibe(tone.id as typeof selectedVibe);
                  }}
                  className={`px-3 py-1.5 rounded-full text-xs font-bold transition-all ${
                    selectedVibe === tone.id
                      ? 'bg-[#1C1B1B] text-white shadow-xs'
                      : 'bg-white dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300'
                  }`}
                >
                  {tone.label}
                </button>
              ))}
            </div>

            <div className="bg-white dark:bg-neutral-950 p-3 rounded-2xl flex items-center justify-between shadow-xs mt-1">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-neutral-500 text-[18px]">vibration</span>
                <div className="flex flex-col">
                  <span className="text-xs font-bold">Muted Vibration Pulses</span>
                  <span className="text-[10px] text-neutral-500">Micro-taps only; avoids harsh buzzing</span>
                </div>
              </div>

              <button
                onClick={() => setHaptics(!haptics)}
                className={`w-11 h-6 rounded-full p-0.5 flex items-center transition-colors ${
                  haptics ? 'bg-[#864B70]' : 'bg-neutral-300 dark:bg-neutral-700'
                }`}
              >
                <div
                  className={`w-5 h-5 rounded-full bg-white shadow-xs transition-transform ${
                    haptics ? 'translate-x-5' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>
          </section>

          {/* Action Footer */}
          <div className="flex flex-col gap-2 pt-1">
            <button
              onClick={handleSave}
              className="w-full h-13 rounded-full bg-[#1C1B1B] text-white font-headline text-xs font-bold flex items-center justify-center gap-2 shadow-lg active:scale-98 transition-all hover:bg-neutral-800"
            >
              <span className="material-symbols-outlined text-[18px] text-[#F9DF72]">bedtime</span>
              <span>Save Bedtime Sync & Exceptions</span>
            </button>

            <button
              onClick={onClose}
              className="w-full py-2 text-center text-xs font-semibold text-neutral-500 hover:text-neutral-800 dark:hover:text-neutral-200"
            >
              Reset to Circle Defaults
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
