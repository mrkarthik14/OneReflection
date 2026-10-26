import React, { useEffect, useState } from 'react';

interface WaveformVisualizerProps {
  isPlaying?: boolean;
  barCount?: number;
  color?: 'primary' | 'secondary' | 'tertiary' | 'white' | 'mixed';
  heightClass?: string;
}

export const WaveformVisualizer: React.FC<WaveformVisualizerProps> = ({
  isPlaying = false,
  barCount = 14,
  color = 'primary',
  heightClass = 'h-7',
}) => {
  const [pulsePhase, setPulsePhase] = useState(0);

  useEffect(() => {
    if (!isPlaying) return;
    const interval = setInterval(() => {
      setPulsePhase((prev) => (prev + 1) % 100);
    }, 120);
    return () => clearInterval(interval);
  }, [isPlaying]);

  // Pre-calculated organic heights for waveform rhythm
  const baseHeights = [28, 55, 85, 42, 95, 68, 75, 48, 62, 88, 52, 35, 70, 45, 80, 60];

  const getColorClass = (index: number) => {
    if (color === 'white') return 'bg-white';
    if (color === 'secondary') return 'bg-[#736200]';
    if (color === 'tertiary') return 'bg-[#414e76]';
    if (color === 'mixed') {
      if (index % 3 === 0) return 'bg-[#864b70]';
      if (index % 3 === 1) return 'bg-[#f9df72]';
      return 'bg-[#313030]';
    }
    // Default primary plum/pink
    return index < 6 ? 'bg-[#864b70]' : 'bg-[#d4c2c9]';
  };

  return (
    <div className={`flex items-center gap-1 ${heightClass} px-1 select-none overflow-hidden`}>
      {Array.from({ length: barCount }).map((_, i) => {
        const baseH = baseHeights[i % baseHeights.length];
        const dynamicH = isPlaying
          ? Math.max(20, Math.min(100, baseH + Math.sin(pulsePhase * 0.4 + i) * 35))
          : baseH;

        return (
          <span
            key={i}
            className={`w-1.5 rounded-full transition-all duration-150 ${getColorClass(i)}`}
            style={{ height: `${dynamicH}%` }}
          />
        );
      })}
    </div>
  );
};
