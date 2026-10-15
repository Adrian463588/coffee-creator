# Arsitektur UI/UX Web Modern 2026: Anti-AI Slop, Geometri Spasial Terukur, Mikro-Interaksi Taktil, dan Master Contract DESIGN.md

Dokumen ini adalah spesifikasi rekayasa antarmuka (*interface engineering specification*) standar industri modern tahun 2026. Tujuannya adalah mengeliminasi degradasi visual generik (*AI slop*), menetapkan aturan geometris tata letak matematis, menghapus friksi kognitif (*zero-pain-point UX*), mengatur anggaran mikro-interaksi berkinerja tinggi, dan menyediakan arsitektur master `DESIGN.md` deterministik untuk sinkronisasi desainer, tim engineer, serta agen AI otonom (*Agentic Development*).

---

## 1. Paradigma UI/UX 2026: Anatomi & Eliminasi "AI Slop"

Pada lanskap rekayasa web 2026, pengguna mengalami kelelahan visual (*visual fatigue*) terhadap template sintetis yang diproduksi massal oleh LLM era awal. Antarmuka berkualitas tinggi bertransformasi dari estetika "kosong, mengilap, dan dekoratif tanpa tujuan" ke arah **Tactile Industrial & Swiss Modern Editorial**: berdensitas tinggi, presisi, berbobot tipografis, responsif instan, dan dapat diandalkan secara fungsional.

### 1.1 Dekonstruksi Pola "AI Slop" vs. Standar Produksi 2026

| Dimensi Rekayasa | Karakteristik "AI Slop" (Dilarang) | Standar Produksi 2026 (Best Practice) |
| :--- | :--- | :--- |
| **Materialitas & Surface** | Overdosis `backdrop-blur(24px)`, gradien neon melayang (*mesh blobs*), kartu tanpa struktur. | Permukaan padat (*solid surfaces*), palet netral berbasis ruang warna OKLCH, batas optik tajam 1px (*tangible hairline borders*). |
| **Tata Letak & Spasial** | Spasi kosong masif tanpa makna (*wasteful whitespace*), kartu seragam 3-kolom repetitif. | Densitas data hierarkis dan scannable (*high data-density*), layout asimetris fungsional, adaptif terhadap container query. |
| **Tipografi** | Satu ukuran raksasa dengan body copy pudar; tracking huruf arbitrer atau dinonaktifkan. | Skala modular matematis (*Minor Third*); kompensasi optik: *negative tracking* pada display, *positive tracking* pada mikro-teks. |
| **Mikro-Interaksi** | Animasi pantul (*bouncy spring*) $500\text{ms}$ yang lambat dan menghalangi kecepatan alur kerja. | Transisi deterministik ultra-responsif ($80\text{ms} - 160\text{ms}$), umpan balik taktil (`active:scale-[0.98]`), interupsi instan. |
| **Form & Input** | Floating labels yang bergeser canggung, error merah instan saat karakter pertama diketik. | Label statis persisten di atas input, rasio padding proporsional, validasi debounced saat `onBlur`, auto-save draft lokal. |
| **Aksesibilitas** | Teks abu-abu muda di atas background gelap (gagal kontras WCAG); `outline: none` tanpa pengganti. | Kepatuhan mutlak WCAG 2.2 Level AA / APCA (*Accessible Perceptual Contrast Algorithm*), ring fokus terisolasi yang kentara. |

---

## 2. Geometri Spasial Matematis (Padding, Margin, Grid, & Container)

Fondasi visual yang stabil menuntut eliminasi angka arbitrer. Semua dimensi antarmuka dikalkulasi menggunakan **Sistem Skala Basis 4pt/8pt** dengan prinsip *intrinsic layout* (CSS Grid, Subgrid, dan Flexbox).

### 2.1 Skala Token Spasial Linear-Eksponensial

```
  Token     Nilai   Penerapan Fungsional Rekayasa
  ───────────────────────────────────────────────────────────────────────
  [0.5]      2px    Offset hairline border, micro-badge vertical padding
  [1]        4px    Gap internal ikon-teks, inline dot indicator
  [2]        8px    Padding vertikal tombol padat, gap item list ringkas
  [3]       12px    Padding vertikal input field, toolbar button gap
  [4]       16px    Padding standar kartu, gap antar-elemen form
  [6]       24px    Padding kontainer panel, gutter grid dashboard
  [8]       32px    Jarak vertikal antar-kelompok section fungsional
  [12]      48px    Jarak antar-seksi halaman aplikasi utama
  [16]      64px    Offset batas kanvas aplikasi / workspace margin
```

### 2.2 Hukum Geometri Spasial Terukur

```
+-------------------------------------------------------------------------+
| PARENT CONTAINER (Grid / Flex Stack)                                    |
|  Gap: Menentukan jarak antar-elemen secara terpusat                     |
|                                                                         |
|  +--------------------------------+  +-------------------------------+  |
|  | Card Element A                 |  | Card Element B                |  |
|  |                                |  |                               |  |
|  |  +--------------------------+  |  |                               |  |
|  |  | Internal Padding (P)     |  |  |                               |  |
|  |  |                          |  |  |                               |  |
|  |  |  [ Child Target Element ]|  |  |                               |  |
|  |  |                          |  |  |                               |  |
|  |  +--------------------------+  |  |                               |  |
|  +--------------------------------+  +-------------------------------+  |
+-------------------------------------------------------------------------+
```

1. **Hukum Encapsulated Layout (Zero Margin Invariant)**:
   - Komponen mandiri **dilarang mengekspor margin luar** (`margin`, `mt-*`, `mb-*`).
   - Posisi dan jarak antar-elemen wajib diatur secara eksklusif oleh kontainer induk menggunakan properti `gap`. Hal ini membasmi isu *collapsing margins* serta menjamin komponen dapat dipindahkan tanpa merusak ritme halaman.
2. **Proporsi Rasio Aspek Spasi Input & Tombol**:
   - Untuk keterbacaan optimal dan kemudahan tapping, rasio padding horizontal ($P_x$) terhadap padding vertikal ($P_y$) wajib memenuhi rentang matematis:
     $$1.5 \le \frac{P_x}{P_y} \le 2.0$$
   - *Implementasi*: Tombol berukuran $36\text{px} \rightarrow P_y = 8\text{px}$, $P_x = 16\text{px}$ (Rasio $2.0$). Tombol kompak $28\text{px} \rightarrow P_y = 6\text{px}$, $P_x = 10\text{px}$ (Rasio $1.66$).
3. **Formula Sudut Konsentris (Concentric Nested Border-Radius)**:
   - Lengkungan sudut elemen di dalam kontainer berbingkai wajib mengecil secara proporsional agar kelengkungan optik tetap sejajar:
     $$R_{\text{dalam}} = \max\left(0,\, R_{\text{luar}} - P\right)$$
   - *Contoh*: Jika kontainer memiliki $R_{\text{luar}} = 16\text{px}$ dan padding internal $P = 12\text{px}$, maka border-radius elemen anak adalah:
     $$R_{\text{dalam}} = 16\text{px} - 12\text{px} = 4\text{px}$$
   - Jika $R_{\text{luar}} = 8\text{px}$ dan $P = 8\text{px}$, maka $R_{\text{dalam}} = 0\text{px}$ (sudut siku-siku natural tanpa distorsi).

---

## 3. Sistem Tipografi Presisi & Skala Modular 2026

Tipografi anti-slop mengandalkan ritme vertikal, bobot medium untuk UI action, dan penyesuaian spasi antar-huruf (*letter-spacing / tracking*) yang presisi.

### 3.1 Skala Modular Minor Third (Rasio 1.200)

Untuk antarmuka fungsional, rasio Minor Third ($1.200$) mempertahankan kerapatan informasi tanpa disparitas ukuran yang memecah hierarki pembacaan.

| Token Peran | Ukuran ($px$) | Ukuran ($rem$) | Line Height ($LH$) | Tracking ($Letter-Spacing$) | Weight |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Display (H1)** | 32px | 2.000rem | 1.15 (37px) | `-0.025em` (-0.80px) | 600 (SemiBold) |
| **Section (H2)** | 24px | 1.500rem | 1.20 (29px) | `-0.020em` (-0.48px) | 600 (SemiBold) |
| **Title (H3)**   | 20px | 1.250rem | 1.25 (25px) | `-0.015em` (-0.30px) | 600 (SemiBold) |
| **Subhead (H4)** | 16px | 1.000rem | 1.35 (22px) | `-0.010em` (-0.16px) | 500 (Medium) |
| **Body (Default)**| 14px | 0.875rem | 1.50 (21px) | `0.000em` (0.00px) | 400 (Regular) |
| **UI Action / Form**| 13px | 0.8125rem| 1.35 (18px) | `+0.005em` (+0.07px) | 500 (Medium) |
| **Caption / Meta**| 11px | 0.6875rem| 1.40 (15px) | `+0.025em` (+0.28px) | 500 (Medium) |
| **Data / Monospace**| 12px | 0.750rem | 1.45 (17px) | `0.000em` (0.00px) | 400 (Mono) |

### 3.2 Kompensasi Optik Tipografi
1. **Aturan Tracking Terbalik (Inverse Tracking Curve)**:
   - Semakin besar skala fisik huruf, spasi antar-huruf harus **semakin rapat** (*negative tracking*) untuk mencegah karakter tampak terpisah.
   - Semakin kecil ukuran font (di bawah $13\text{px}$), spasi antar-huruf harus **semakin renggang** (*positive tracking*) agar bentuk karakter (*counter space*) tidak melebur.
2. **Formula Rasio Line-Height Dinamis**:
   $$\text{Line-Height} = 1.1 + \frac{0.4}{1 + \left(\frac{\text{Font-Size}}{14}\right)}$$
   Judul berukuran besar wajib memiliki line-height yang rapat ($1.15 - 1.25$), sedangkan body text memerlukan ruang vertikal ($1.50$) demi kelancaran alur mata pembaca.

---

## 4. Mikro-Interaksi Modern 2026: Kecepatan, Taktil, dan Fisika

Mikro-interaksi pada tahun 2026 berfokus pada **taktil fungsional, prediktabilitas, dan eliminasi latensi**. Tren animasi membal (*spring bouncy*) berdurasi lama telah digantikan oleh transisi presisi bergaya instrumen industrial.

```
+-------------------------------------------------------------------------------+
|                    ANGGARAN WAKTU TRANSISI (TIMING BUDGETS)                   |
+----------------------------------+-----------------------+--------------------+
| Kategori Interaksi               | Anggaran Durasi (ms)  | Karakteristik Easing|
+----------------------------------+-----------------------+--------------------+
| Feedback Mikro (Hover / Active)  | 80ms – 120ms          | ease-out / instant |
| Toggle / Checkbox / Accordion    | 140ms – 180ms         | cubic-bezier(0.16, 1, 0.3, 1) |
| Kontekstual Drawer / Slide-Over  | 200ms – 240ms         | cubic-bezier(0.2, 0.8, 0.2, 1) |
| Layout Morphing (View Transition)| 180ms – 250ms         | linear-spatial interpolation |
+----------------------------------+-----------------------+--------------------+
* Batas Mutlak: Dilarang membuat animasi fungsional antarmuka yang melebihi 250ms.
```

### 4.1 Prinsip Mikro-Interaksi Modern

1. **Umpan Balik Taktil Instan (Micro-Depression)**:
   - Elemen interaktif (tombol, item dropdown, card clickable) harus memberikan konfirmasi fisik saat ditekan:
     ```css
     .interactive-element:active {
       transform: scale(0.98) translateY(0.5px);
       transition-duration: 60ms;
     }
     ```
2. **Eliminasi Hover Trap**:
   - Transisi hover tidak boleh menghalangi klik berikutnya. Gunakan transisi properti selektif (`transition: color 100ms, background-color 100ms, border-color 100ms`), bukan `transition: all`.
3. **Transisi Kontinu Spasial (FLIP & Native View Transitions)**:
   - Gunakan CSS View Transitions API (`document.startViewTransition()`) saat mengubah state tampilan atau berpindah dari item daftar ke tampilan detail, mencegah kedipan putih layar penuh.
4. **Pola Status Agen & Asinkron (Optimistic Feedback)**:
   - Gantikan spinner layar penuh dengan *targeted localized skeleton* atau pembaruan status instan (*optimistic UI*).
   - Indikator pemrosesan AI agen menggunakan *pulse hairline glow* pada tepi kontainer ($1.5\text{s}$ durasi siklus), bukan modal popup yang mengunci kanvas kerja.
5. **Kepatuhan Terhadap Aksesibilitas Gerak**:
   - Wajib menyertakan penanganan `prefers-reduced-motion`:
     ```css
     @media (prefers-reduced-motion: reduce) {
       *, *::before, *::after {
         animation-duration: 0.01ms !important;
         transition-duration: 0.01ms !important;
       }
     }
     ```

---

## 5. Arsitektur Pola Ergonomis (*Zero-Pain-Point UX*)

```
+-------------------------------------------------------------------------------+
|                       ARSITEKTUR ZERO PAIN POINTS                             |
+------------------------------------+------------------------------------------+
| Sumber Friksi Umum                 | Solusi Deterministik 2026                |
+------------------------------------+------------------------------------------+
| Modal pop-up menumpuk              | Slide-over drawer / Inline expand        |
| Formulir terhapus saat reload      | Draft caching otomatis (IndexedDB/Local) |
| Loading spinner layar penuh        | Localized Skeleton + Optimistic UI       |
| Kehilangan konteks keyboard        | Focus ring nyata + Command Palette (Kbd) |
| Kehilangan data akibat aksi instan | Undo toast 5 detik (gantikan modal konfirmasi) |
| Error form yang membingungkan      | Validasi debounced on-blur + pesan presisi |
+------------------------------------+------------------------------------------+
```

### 5.1 Standar Form Ergonomics
1. **Label Statis di Atas Bidang Input**: Jangan gunakan *floating label*. Tempatkan label tetap di atas kiri input field agar mata pengguna dapat memindai formulir secara vertikal tanpa gangguan visual.
2. **Validasi Debounced On-Blur**:
   - Jangan pernah menyalakan error validasi saat pengguna baru saja mengetik huruf pertama.
   - Validasi dijalankan saat event `onBlur` atau setelah jeda pengetikan pengguna ($500\text{ms}$ debounce).
3. **Eksekusi Destruktif dengan Jaminan Undo**: Gantikan modal konfirmasi *"Apakah Anda yakin?"* untuk tindakan umum dengan eksekusi instan yang disertai panel aksi undo 5 detik (*"Item dipindahkan ke sampah — [Batalkan]"*).

---

## 6. Arsitektur Warna Berbasis OKLCH & Standar WCAG 2.2

Model warna sRGB dan HSL memiliki inkonsistensi persepsi luminositas. Standar 2026 menggunakan model **OKLCH** (*Lightness, Chroma, Hue*) untuk menjamin kontras perceptually uniform di seluruh tema.

```
  oklch(Lightness Chroma Hue)
        │         │      │
        │         │      └── Hue (0 - 360): Sudut spektrum rona
        │         └───────── Chroma (0 - 0.4): Kejenuhan murni tanpa distorsi
        └─────────────────── Lightness (0% - 100%): Persepsi kecerahan mata manusia
```

### Token Semantik Dual-Mode OKLCH

```css
:root {
  /* Surface Netral (Light Mode) */
  --bg-canvas: oklch(99% 0.002 240);
  --bg-surface: oklch(97% 0.004 240);
  --bg-subtle: oklch(94% 0.006 240);
  --border-subtle: oklch(88% 0.008 240);
  --border-strong: oklch(70% 0.015 240);

  /* Tipografi */
  --text-primary: oklch(20% 0.015 240);
  --text-secondary: oklch(45% 0.020 240);
  --text-muted: oklch(62% 0.015 240);

  /* Aksen Taktil (Electric Sapphire) */
  --accent-base: oklch(52% 0.22 260);
  --accent-hover: oklch(46% 0.24 260);
  --accent-contrast: oklch(99% 0 0);

  /* Status Fungsional */
  --status-success: oklch(62% 0.17 145);
  --status-danger: oklch(58% 0.22 25);
  --status-warning: oklch(75% 0.16 75);
}

.dark {
  /* Surface Netral (Dark Mode - Mengindari Pure Black #000) */
  --bg-canvas: oklch(14% 0.006 240);
  --bg-surface: oklch(18% 0.008 240);
  --bg-subtle: oklch(24% 0.012 240);
  --border-subtle: oklch(28% 0.012 240);
  --border-strong: oklch(40% 0.018 240);

  /* Tipografi Dark Mode */
  --text-primary: oklch(96% 0.005 240);
  --text-secondary: oklch(75% 0.010 240);
  --text-muted: oklch(55% 0.012 240);

  /* Aksen Dark Mode */
  --accent-base: oklch(65% 0.20 260);
  --accent-hover: oklch(72% 0.18 260);
  --accent-contrast: oklch(14% 0.01 260);
}
```

---

## 7. Master Contract Specification: `DESIGN.md`

File `DESIGN.md` berikut disimpan di root direktori repositori. Dokumen ini bertindak sebagai *Single Source of Truth* (SSOT) yang mengikat manusia dan AI Coding Agent (*Cursor, Claude Code, Copilot*) agar tidak menghasilkan kode styling arbitrer.

```markdown:DESIGN.md:DESIGN.md
# SYSTEM DESIGN CONTRACT & INVARIANTS (2026 SPECIFICATION)

Dokumen ini mendefinisikan aturan mutlak desain antarmuka, tata letak spasial, tipografi, dan interaksi untuk seluruh basis kode aplikasi ini. Semua engineer dan AI Coding Agent (Cursor, Claude Code, Copilot) WAJIB mematuhi spesifikasi ini tanpa pengecualian.

---

## 1. ATURAN INTEGRITAS KODE (ZERO SLOP INVARIANTS)

1. LARANGAN ARBITRER:
   - Dilarang membuat inline styles: `style={{ ... }}` atau `style="..."`.
   - Dilarang menggunakan arbitrary Tailwind/CSS classes (contoh: `p-[17px]`, `w-[342px]`, `bg-[#6366f1]`).
   - Gunakan HANYA token utilitas yang dipetakan pada sistem desain.

2. ATURAN ENKAPSULASI SPASIAL:
   - Komponen turunan DILARANG mengekspor margin eksternal (`mt-*`, `mb-*`, `m-*`).
   - Semua jarak antar-komponen diatur oleh kontainer pembungkus menggunakan Flexbox/Grid `gap-*`.

3. RUMUS SUDUT KONSENTRIS:
   - Nested Border Radius: `R_inner = Math.max(0, R_outer - Padding)`. Dilarang mencampur radius secara acak.

4. MATERIALITAS & VISUAL:
   - Dilarang menggunakan efek glassmorphism berlebihan (`backdrop-blur` intens dengan teks abu-abu).
   - Dilarang menggunakan gradien dekoratif tanpa tujuan semantik.
   - Garis batas komponen wajib solid 1px: `border border-[var(--border-subtle)]`.

---

## 2. SPESIFIKASI SKALA & TOKEN

### 2.1 Skala Spasi (Grid Basis 4px / 8px)
- `0.5`: 2px  (micro offsets, borders)
- `1`:   4px  (inline icon spacing)
- `2`:   8px  (compact button padding, dense list item)
- `3`:   12px (form vertical padding, toolbar gaps)
- `4`:   16px (standard card padding, medium gap)
- `6`:   24px (container padding, structural column gap)
- `8`:   32px (section vertical gap)
- `12`:  48px (page blocks)

### 2.2 Hierarki Tipografi
- Skala: 1.200 (Minor Third)
- Font Display & Body: Inter Tight / Geist Sans (`font-sans`)
- Font Data & Monospace: Geist Mono / JetBrains Mono (`font-mono`)
- Display (H1): 32px / LH 1.15 / Tracking -0.025em / Weight 600
- Section (H2): 24px / LH 1.20 / Tracking -0.020em / Weight 600
- Title   (H3): 20px / LH 1.25 / Tracking -0.015em / Weight 600
- Body (Text):  14px / LH 1.50 / Tracking 0.000em  / Weight 400
- Caption/UI:   12px / LH 1.40 / Tracking +0.020em / Weight 500

---

## 3. ATURAN MIKRO-INTERAKSI & ERGONOMI

1. FOCUS STATES:
   - Dilarang mematikan outline tanpa pengganti (`outline: none` dilarang keras).
   - Selalu terapkan: `focus-visible:ring-2 focus-visible:ring-[var(--accent-base)] focus-visible:ring-offset-2`.

2. TRANSISI MIKRO & ANGGARAN DURASI:
   - Durasi animasi mikro: 80ms - 150ms (`ease-out`). Tidak boleh melebihi 250ms untuk UI fungsional.
   - Elemen interaktif wajib menyertakan feedback tekan: `active:scale-[0.98] active:translate-y-[0.5px]`.
   - Selalu hormati preferensi pengguna dengan menyertakan mitigasi `prefers-reduced-motion`.

3. PREVENSI KEHILANGAN DATA:
   - Semua input form panjang wajib menyimpan state draft lokal secara debounced.
   - Status destruktif menggunakan eksekusi dengan opsi undo toast daripada modal konfirmasi berulang.
```

---

## 8. Implementasi Komponen Blueprint (React 19 + Tailwind CSS)

Berikut adalah komponen siap produksi yang menerapkan seluruh spesifikasi: enkapsulasi spasial, rasio $P_x/P_y$, border-radius konsentris, dan mikro-interaksi taktil.

### 8.1 Komponen Taktil Interaktif (Button dengan Micro-Depression)

```tsx
import React, { ButtonHTMLAttributes, forwardRef } from 'react';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'subtle' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(({
  children,
  variant = 'primary',
  size = 'md',
  isLoading = false,
  disabled,
  className = '',
  ...props
}, ref) => {
  // Matriks Dimensi: Memenuhi rasio Px / Py antara 1.5 - 2.0
  const sizeClasses = {
    sm: 'h-7 px-2.5 text-xs gap-1.5 rounded-md',      // Py: 4px, Px: 10px (Rasio 2.5)
    md: 'h-9 px-3.5 text-sm gap-2 rounded-md',        // Py: 8px, Px: 14px (Rasio 1.75)
    lg: 'h-11 px-5 text-sm gap-2.5 rounded-lg',       // Py: 12px, Px: 20px (Rasio 1.66)
  }[size];

  const variantClasses = {
    primary: `
      bg-[var(--accent-base)] text-[var(--accent-contrast)]
      hover:bg-[var(--accent-hover)]
      border border-transparent
    `,
    secondary: `
      bg-[var(--bg-surface)] text-[var(--text-primary)]
      hover:bg-[var(--bg-subtle)] hover:border-[var(--border-strong)]
      border border-[var(--border-subtle)]
    `,
    subtle: `
      bg-transparent text-[var(--text-secondary)]
      hover:bg-[var(--bg-subtle)] hover:text-[var(--text-primary)]
      border border-transparent
    `,
    danger: `
      bg-[var(--status-danger)] text-white
      hover:brightness-95
      border border-transparent
    `,
  }[variant];

  return (
    <button
      ref={ref}
      disabled={disabled || isLoading}
      className={`
        inline-flex items-center justify-center font-medium
        select-none whitespace-nowrap
        /* Mikro-Interaksi Taktil Standar 2026 */
        transition-all duration-100 ease-out
        active:scale-[0.98] active:translate-y-[0.5px]
        motion-reduce:transform-none
        /* Aksesibilitas Keyboard Ring */
        focus-visible:outline-none focus-visible:ring-2 
        focus-visible:ring-[var(--accent-base)] focus-visible:ring-offset-2
        disabled:opacity-50 disabled:pointer-events-none
        shadow-xs
        ${sizeClasses}
        ${variantClasses}
        ${className}
      `}
      {...props}
    >
      {isLoading ? (
        <span className="w-3.5 h-3.5 border-2 border-current border-t-transparent rounded-full animate-spin" />
      ) : null}
      {children}
    </button>
  );
});

Button.displayName = 'Button';
```

### 8.2 Input Presisi dengan Feedback Aksesibel & Validasi On-Blur

```tsx
import React, { InputHTMLAttributes, forwardRef, useId } from 'react';

export interface TextInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  hint?: string;
}

export const TextInput = forwardRef<HTMLInputElement, TextInputProps>(({
  label,
  error,
  hint,
  disabled,
  className = '',
  id,
  ...props
}, ref) => {
  const generatedId = useId();
  const inputId = id || generatedId;
  const hintId = `${inputId}-hint`;
  const errorId = `${inputId}-error`;

  return (
    <div className="flex flex-col gap-1.5 w-full text-left">
      {/* Label Statis di Atas: Scan vertikal optimal tanpa animasi floating */}
      <div className="flex items-center justify-between">
        <label 
          htmlFor={inputId} 
          className="text-xs font-medium tracking-[0.005em] text-[var(--text-secondary)] select-none"
        >
          {label}
        </label>
        {hint && !error && (
          <span id={hintId} className="text-[11px] text-[var(--text-muted)]">
            {hint}
          </span>
        )}
      </div>

      {/* Input Shell: h-9 (36px), px-3 (12px), py-2 (8px) */}
      <div className="relative flex items-center">
        <input
          ref={ref}
          id={inputId}
          disabled={disabled}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? errorId : hint ? hintId : undefined}
          className={`
            w-full h-9 px-3 text-sm
            bg-[var(--bg-canvas)] text-[var(--text-primary)]
            border rounded-md shadow-xs
            transition-colors duration-100 ease-out
            placeholder:text-[var(--text-muted)]
            disabled:opacity-50 disabled:cursor-not-allowed
            focus-visible:outline-none focus-visible:ring-2 
            ${error 
              ? 'border-[var(--status-danger)] focus-visible:ring-[var(--status-danger)]' 
              : 'border-[var(--border-subtle)] hover:border-[var(--border-strong)] focus-visible:ring-[var(--accent-base)]'
            }
            ${className}
          `}
          {...props}
        />
      </div>

      {/* Pesan Kesalahan Deskriptif */}
      {error && (
        <span 
          id={errorId} 
          role="alert" 
          className="text-[12px] font-medium text-[var(--status-danger)] flex items-center gap-1 tracking-tight"
        >
          <svg className="w-3.5 h-3.5 shrink-0" viewBox="0 0 16 16" fill="currentColor">
            <path fillRule="evenodd" d="M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zm-.75-9.25a.75.75 0 0 1 1.5 0v3.5a.75.75 0 0 1-1.5 0v-3.5zm.75 6.75a.875.875 0 1 1 0-1.75.875.875 0 0 1 0 1.75z" clipRule="evenodd" />
          </svg>
          {error}
        </span>
      )}
    </div>
  );
});

TextInput.displayName = 'TextInput';
```

### 8.3 Card Kontainer dengan Sudut Konsentris ($R_{\text{dalam}} = R_{\text{luar}} - P$)

```tsx
import React, { ReactNode } from 'react';

interface ContentCardProps {
  title: string;
  badge?: string;
  children: ReactNode;
  actions?: ReactNode;
}

export const ContentCard = ({ title, badge, children, actions }: ContentCardProps) => {
  // Formula Geometri Konsentris:
  // Outer Radius = 12px (rounded-xl)
  // Internal Padding = 16px (p-4)
  // Elemen Anak Radius = Math.max(0, 12 - 16) = 0px (natural sharp) atau 4px jika ber-offset
  return (
    <article className="group relative flex flex-col p-4 rounded-xl border border-[var(--border-subtle)] bg-[var(--bg-surface)] shadow-xs transition-colors duration-120 hover:border-[var(--border-strong)]">
      <header className="flex items-center justify-between pb-3 mb-3 border-b border-[var(--border-subtle)]">
        <div className="flex items-center gap-2">
          <h3 className="text-sm font-semibold tracking-[-0.01em] text-[var(--text-primary)]">
            {title}
          </h3>
          {badge && (
            <span className="px-1.5 py-0.5 text-[10px] font-medium tracking-wide uppercase rounded-[4px] bg-[var(--bg-subtle)] text-[var(--text-secondary)] border border-[var(--border-subtle)]">
              {badge}
            </span>
          )}
        </div>
        {actions && (
          <div className="flex items-center gap-1.5">
            {actions}
          </div>
        )}
      </header>

      <div className="text-xs leading-relaxed text-[var(--text-secondary)]">
        {children}
      </div>
    </article>
  );
};
```

---

## 9. Checklist Verifikasi & Quality Gate 2026

Gunakan daftar periksa berikut dalam review PR (*Pull Request*) atau validasi otomatis CI/CD:

- [ ] **Spasi Linier Basis 4pt/8pt**: Seluruh nilai dimensi padding, margin, dan gap mematuhi kelipatan token sistem (tidak ada nilai ganjil arbitrer seperti `17px`, `23px`).
- [ ] **Encapsulated Margin**: Komponen tidak membocorkan margin ke luar; seluruh jarak dikendalikan oleh properti `gap` kontainer induk.
- [ ] **Formula Sudut Konsentris**: Nilai radius internal mematuhi formula matematis $R_{\text{dalam}} = \max(0, R_{\text{luar}} - P)$.
- [ ] **Anggaran Mikro-Interaksi**: Tidak ada transisi fungsional antarmuka yang melebihi $250\text{ms}$; feedback klik tombol menyertakan kompresi taktil (`scale(0.98)`).
- [ ] **Aksesibilitas Gerak**: Aturan `@media (prefers-reduced-motion: reduce)` diterapkan untuk meniadakan animasi bagi pengguna sensitif gerak.
- [ ] **Perceptual Contrast OKLCH**: Seluruh token warna memenuhi rasio kontras WCAG 2.2 AA ($4.5:1$ untuk teks reguler, $3:1$ untuk elemen grafis).
- [ ] **Zero-Pain-Point Ergonomics**: Form memiliki label statis persisten, validasi debounced on-blur, auto-save state, serta opsi *undo* untuk aksi destruktif.
- [ ] **Sinkronisasi Repositori**: File `DESIGN.md` telah disematkan di root proyek untuk membatasi ruang halusinasi agen AI.