# Arsitektur UI/UX Web Modern: Anti-AI Slop, Sistem Desain Matematis, dan Agentic Development

Panduan ini dirancang sebagai standar rekayasa desain antarmuka web modern kelas produksi. Fokus utamanya adalah membasmi pola generik (*AI slop*), membangun hierarki spasial dan tipografis yang terukur, meminimalkan friksi pengguna (*zero pain points*), serta mengintegrasikan alur kerja otomatisasi cerdas berbasis **Agentic Software Development**.

---

## 1. Anatomi dan Eliminasi Desain "AI Slop"

### 1.1 Apa Itu "AI Slop" dalam Desain UI?
*AI Slop* merujuk pada produk visual generik yang dihasilkan oleh model generatif tanpa pertimbangan mendalam terhadap kegunaan fungsional, hierarki informasi, dan ergonomi antarmuka. 

Ciri-ciri umum *AI Slop*:
- **Pola Kartu Tiga Kolom Generik**: Penggunaan kartu persegi panjang dengan ikon di dalam lingkaran berwarna ungu/indigo pudar tanpa kontras logis.
- **Overdosis Glassmorphism**: `backdrop-blur` berlebihan dengan border semi-transparan `rgba(255,255,255,0.1)` yang merusak kontras rasio (gagal WCAG).
- **Dekorasi Tanpa Fungsi**: Lingkaran gradien melayang (*floating mesh gradient blobs*) yang tidak memiliki makna semantik dan membebani performa GPU.
- **Copywriting Klise**: Teks generik seperti *"Empower your workflows with seamless cutting-edge synergy"*.
- **Border-Radius Arbitrer**: Komponen bercampur antara sudut tajam, `rounded-2xl` (16px), dan `rounded-full` (9999px) tanpa aturan hierarki wadah.

### 1.2 Prinsip "Anti-AI Slop" (Utilitarian & Editorial Rigor)
1. **Tipografi sebagai Struktur Utama**: Gunakan kontras bobot teks, ukuran, dan pelacakan (*tracking*), bukan mengandalkan kotak/kartu sebagai pemisah.
2. **Materialitas & Batasan Tegas**: Gunakan garis batas 1px berdefinisi tinggi (`1px solid var(--border)`) dengan palet monokromatik berimbang dan satu warna aksen yang fungsional.
3. **Data-Dense & Efisien**: Utamakan densitas informasi yang relevan daripada ruang kosong tak bermakna (*meaningless whitespace*).
4. **Keaslian Interaksi**: Menghadirkan *tactile feedback*, transisi mikro yang deterministik (100–150ms), dan interaksi instan tanpa animasi dekoratif yang memperlambat alur kerja.

---

## 2. Sistem Spasi & Geometri Layout (Padding, Margin, Grid)

Fondasi keteraturan visual dibangun menggunakan **Sistem Skala 4pt/8pt Base Grid**. Semua nilai dimensi, *padding*, *margin*, dan *gap* harus merupakan kelipatan dari $4\text{px}$.

### 2.1 Skala Token Spasi

| Token | Nilai (px) | Nilai (rem) | Penggunaan Fungsional |
| :--- | :--- | :--- | :--- |
| `space-0.5` | 2px | 0.125rem | Border offset, micro-badge padding |
| `space-1` | 4px | 0.25rem | Gap internal antar ikon dan label kecil |
| `space-2` | 8px | 0.5rem | Padding tombol ringkas, gap list item |
| `space-3` | 12px | 0.75rem | Padding vertikal input field, gap toolbar |
| `space-4` | 16px | 1.0rem | Padding standar kartu (*compact*), gap form |
| `space-6` | 24px | 1.5rem | Padding kontainer kartu, jarak antar seksi kecil |
| `space-8` | 32px | 2.0rem | Margin antar komponen struktural |
| `space-12`| 48px | 3.0rem | Jarak vertikal antar seksi halaman |
| `space-16`| 64px | 4.0rem | Jarak vertikal utama (Hero, Footer) |

### 2.2 Aturan Struktural Margin vs. Padding

```
+--------------------------------------------------------+
| Margin (Eksternal): Jarak antar elemen independen      |
|  +--------------------------------------------------+  |
|  | Border (1px)                                     |  |
|  |  +--------------------------------------------+  |  |
|  |  | Padding (Internal): Wadah ke konten elemen |  |  |
|  |  |  +--------------------------------------+  |  |  |
|  |  |  | Konten / Child Elements              |  |  |  |
|  |  |  +--------------------------------------+  |  |  |
|  |  +--------------------------------------------+  |  |
|  +--------------------------------------------------+  |
+--------------------------------------------------------+
```

1. **Prinsip "Zero Collapsing Margin"**: Hindari penggunaan `margin-top` dan `margin-bottom` yang berantakan antar elemen turunan. Gunakan Flexbox/CSS Grid dengan atribut `gap` untuk tata letak yang deterministik.
2. **Proporsi Padding Tombol & Input**:
   - Aspek Rasio Optik Spasi: Padding horizontal harus berkisar antara **$1.5\times$ hingga $2\times$** dari padding vertikal.
   - Contoh: Input tinggi $40\text{px} \rightarrow \text{Padding Y} = 8\text{px}$, $\text{Padding X} = 14\text{px}-16\text{px}$.
3. **Penyelarasan Sudut Relatif (Nested Border-Radius)**:
   Agar sudut lingkaran dalam terlihat konsisten dengan sudut luar, gunakan rumus geometris:
   $$R_{\text{dalam}} = R_{\text{luar}} - \text{Padding}$$
   *Jika $R_{\text{luar}} = 16\text{px}$ dan $\text{Padding} = 8\text{px}$, maka $R_{\text{dalam}} = 8\text{px}$.*

---

## 3. Sistem Tipografi & Skala Modular

Tipografi anti-AI slop mengutamakan keterbacaan tinggi (*high legibility*), densitas data, dan hierarki optik yang tidak membingungkan.

### 3.1 Skala Tipe Modular (Rasio 1.200 - *Minor Third*)
Untuk antarmuka aplikasi berbasis web, rasio 1.200 atau 1.125 (*Major Second*) menghasilkan densitas yang jauh lebih teratur dibanding rasio besar seperti 1.414 (*Augmented Fourth*) yang sering memicu *AI slop*.

| Tingkat Hierarki | Ukuran Font ($px / rem$) | Line Height | Letter Spacing (*Tracking*) | Font Weight |
| :--- | :--- | :--- | :--- | :--- |
| **Display / H1** | 36px / 2.25rem | 1.15 (42px) | -0.025em (-0.9px) | 600 (SemiBold) |
| **Headline / H2**| 28px / 1.75rem | 1.20 (34px) | -0.020em (-0.56px) | 600 (SemiBold) |
| **Subhead / H3** | 22px / 1.375rem | 1.30 (28px) | -0.015em (-0.33px) | 500 (Medium) |
| **Title / H4**   | 18px / 1.125rem | 1.35 (24px) | -0.010em (-0.18px) | 600 (SemiBold) |
| **Body (Default)**| 14px / 0.875rem | 1.50 (21px) | 0.000em | 400 (Regular) |
| **Label / Action**| 13px / 0.8125rem| 1.30 (17px) | +0.005em | 500 (Medium) |
| **Caption / Mono**| 12px / 0.75rem  | 1.40 (16px) | +0.020em (+0.24px) | 400 / 500 |

### 3.2 Pasangan Font Berbasis Fungsi
- **Font Primer (Antarmuka & Teks Utama)**: Neo-grotesque modern dengan bentuk netral (*Geist Sans*, *Inter*, atau *Instrument Sans*).
- **Font Sekunder (Data, Nilai Moneter, & Kode)**: Monospace proporsional presisi (*Geist Mono*, *JetBrains Mono*).
- **Hindari**: Huruf Display melengkung (*quirky display fonts*) di dalam antarmuka transaksional/dashboard.

---

## 4. Pola Mengurangi Friksi Pengguna (*Frictionless UX*)

Untuk meminimalkan titik masalah (*pain points*) pengguna, arsitektur antarmuka harus menghilangkan hambatan kognitif dan teknis.

```
+----------------------------------------------------------------------------+
|                       ELIMINASI PAIN POINTS UTAMA                          |
+------------------------------------+---------------------------------------+
| Masalah Tradisional                | Solusi Best Practice                  |
+------------------------------------+---------------------------------------+
| Form reset saat terjadi error      | Pertahankan state lokal (Auto-save)   |
| Modal berlapis-lapis               | Slide-over drawer / Inline expansion  |
| Loading spinner layar penuh        | Skeleton screen + Optimistic UI       |
| Tombol aksi utama ambigu           | 1 Single Primary Action per tampilan  |
| Dropdown select kaku               | Combobox instan dengan input keyboard |
+------------------------------------+---------------------------------------+
```

### 4.1 Prinsip Desain Formulir Presisi
1. **Label Posisi Atas (*Top-aligned*)**: Selalu letakkan label di atas input field, bukan floating label yang membingungkan atau label samping yang menurunkan kecepatan pindai mata.
2. **Validasi Inline Pasca-Interaksi (*On-Blur*)**:
   - Jangan tampilkan pesan error sebelum pengguna selesai mengetik.
   - Sajikan pesan kesalahan yang deskriptif dan instruktif (misal: *"Gunakan minimal 8 karakter dengan 1 angka"*, bukan sekadar *"Format salah"*).
3. **Input Affordance**: Input numerik, tanggal, dan mata uang harus memformat data secara otomatis (*auto-masking*) dan menyediakan nilai default yang rasional.

### 4.2 Navigasi Berorientasi Keyboard
Aplikasi kelas profesional harus dapat dinavigasi sepenuhnya tanpa mouse:
- **Focus Rings Nyata**: `outline: 2px solid var(--accent); outline-offset: 2px;`. Jangan pernah menghapus outline tanpa menyediakan `focus-visible` pengganti.
- **Command Palette (`Cmd + K` / `Ctrl + K`)**: Menjadi jalur cepat (*accelerator*) untuk aksi berulang.

---

## 5. Token Desain & Spesifikasi Komponen

Gunakan pendekatan tiga tingkat (*three-tier token architecture*) untuk menjamin pemisahan fungsi dan kemudahan automasi.

### 5.1 Struktur Tiga Tingkat Token (JSON Standard)

```json
{
  "global": {
    "color": {
      "zinc-900": "#18181b",
      "zinc-800": "#27272a",
      "zinc-100": "#f4f4f5",
      "emerald-500": "#10b981",
      "rose-500": "#f43f5e"
    }
  },
  "semantic": {
    "background": {
      "canvas": "{global.color.zinc-900}",
      "surface": "{global.color.zinc-800}"
    },
    "text": {
      "primary": "{global.color.zinc-100}",
      "muted": "rgba(244, 244, 245, 0.6)"
    },
    "status": {
      "success": "{global.color.emerald-500}",
      "danger": "{global.color.rose-500}"
    }
  },
  "component": {
    "button": {
      "primary": {
        "bg": "{semantic.text.primary}",
        "text": "{semantic.background.canvas}",
        "hover": "#ffffff"
      }
    }
  }
}
```

### 5.2 Spesifikasi Komponen Utama: Button & Card

#### Blueprint Komponen Tombol Fungsional (React + Tailwind)
```tsx
import React, { ButtonHTMLAttributes, forwardRef } from 'react';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(({
  variant = 'primary',
  size = 'md',
  isLoading = false,
  children,
  className = '',
  disabled,
  ...props
}, ref) => {
  // Base: Ketat, deterministik, transisi cepat, focus-visible terdefinisi
  const baseStyles = "inline-flex items-center justify-center font-medium rounded-md transition-colors duration-150 ease-out focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-zinc-950 disabled:opacity-50 disabled:cursor-not-allowed select-none active:scale-[0.98]";

  // Varian Visi Anti-AI Slop (Warna pekat, border tegas, minim gradien buatan)
  const variants = {
    primary: "bg-zinc-900 text-zinc-50 hover:bg-zinc-800 active:bg-zinc-900 border border-zinc-900 shadow-sm",
    secondary: "bg-white text-zinc-900 border border-zinc-200 hover:bg-zinc-50 hover:border-zinc-300 shadow-sm",
    danger: "bg-rose-600 text-white hover:bg-rose-700 border border-rose-600"
  };

  // Metrik Spasi: Rasio X/Y konsisten
  const sizes = {
    sm: "h-8 px-3 text-xs gap-1.5",
    md: "h-10 px-4 text-sm gap-2",
    lg: "h-11 px-5 text-base gap-2.5"
  };

  return (
    <button
      ref={ref}
      disabled={disabled || isLoading}
      className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {isLoading ? (
        <span className="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin" />
      ) : children}
    </button>
  );
});

Button.displayName = 'Button';
```

---

## 6. Integrasi dalam Metodologi AGENTIC Development

Metodologi **Agentic Development** melibatkan agen AI otonom (seperti Coding Agent, Cursor, Claude Code, model MCP) yang menulis kode bersama manusia. Dalam sistem ini, tantangan terbesarnya adalah menjaga stabilitas desain agar agen tidak memproduksi styling liar atau variasi arbitrer.

```
+-------------------------------------------------------------------------------+
|                       PIPELINE AGENTIC DESIGN SYSTEM                          |
+-------------------------------------------------------------------------------+
|                                                                               |
|   1. Desain Single Source of Truth: Token JSON (DTCG Spec)                    |
|                          │                                                    |
|                          ▼                                                    |
|   2. Engine Sinkronisasi (AST Parser & Type Generator)                        |
|      (Menghasilkan Tailwind Config, CSS Vars, dan Strict TypeScript Props)    |
|                          │                                                    |
|                          ▼                                                    |
|   3. System Prompt & Context Injection untuk AI Agent                         |
|      (Agen diikat oleh aturan: "DILARANG inline styles / arbitrary hex code") |
|                          │                                                    |
|                          ▼                                                    |
|   4. Agen Mengompilasi Kode UI Berbasis Kontrak Komponen                      |
|                          │                                                    |
|                          ▼                                                    |
|   5. Pengujian Kualitas Otomatis (Linter, Visual Regression, WCAG Test)       |
|                                                                               |
+-------------------------------------------------------------------------------+
```

### 6.1 Pola Desain Antarmuka untuk Interaksi Berbasis Agen (*Agentic UX*)
Saat membangun antarmuka web yang memiliki agen AI otonom di dalamnya, terapkan pola UX berikut:

1. **State Transparan (*Agent State Introspection*)**:
   Antarmuka harus membedakan secara visual antara 4 fase kerja agen:
   - *Planning* (Agen sedang membedah instruksi).
   - *Tool Calling* (Agen sedang mengakses API / database eksternal).
   - *Awaiting Confirmation* (Agen meminta persetujuan manusia pada aksi destruktif).
   - *Finished / Idle*.
2. **Kapasitas Batal & Pembalikan (*Speculative Execution & Rollback*)**:
   Setiap perubahan antarmuka yang dibuat oleh agen harus memiliki log perubahan (*diff preview*) dan fungsi `Undo` instan dengan satu kali klik.
3. **Interruptibility**:
   Pengguna harus dapat menghentikan eksekusi agen kapan saja via tombol `Esc` atau tombol `Stop` tanpa merusak *state* lokal aplikasi.

### 6.2 Aturan Pembatas Agen (*Guardrails*) dalam Kode
Untuk memastikan agen pemrograman tidak merusak konsistensi desain sistem, tetapkan file `.cursorrules` atau `.rules/design-system.md` dalam repository:

```markdown
# Aturan Ketat Desain Sistem untuk AI Agent

1. LARANGAN ARBITRER:
   - Dilarang membuat inline CSS `style={{ ... }}`.
   - Dilarang menggunakan nilai Tailwind arbitrary (misal: `p-[17px]`, `text-[#4a3f21]`).
   - Gunakan HANYA token yang ada di file `tailwind.config.ts`.

2. PRINSIP STRUKTUR:
   - Selalu gunakan `gap-*` pada kontainer Flex/Grid, jangan gunakan margin individual pada anak elemen.
   - Nested radius harus mengikuti formula: R_child = R_parent - padding.

3. KONTRAK KOMPONEN:
   - Gunakan komponen dari `@/components/ui/*`. Jangan membungkus elemen dasar dengan tag HTML primitif secara berulang.
   - Setiap form control wajib memiliki `id`, `aria-describedby`, dan label eksplisit.
```

### 6.3 Siklus Pengembangan Agentic Sprints

```
          [Tentukan Spesifikasi Fitur & User Flow]
                             │
                             ▼
              [Agent: Scaffold UI & Logika]
                             │
                             ▼
         [Linter Validasi Token & Aksesibilitas]
                 ┌───────────┴───────────┐
               (Lolos)                (Gagal)
                 │                       │
                 ▼                       ▼
    [Human Review: Review UX]   [Agent: Auto-fix Styling]
                 │
                 ▼
      [Merge ke Produksi]
```

1. **Definisi Kontrak**: Desainer dan engineer menetapkan skema JSON input/output serta komponen yang diizinkan sebelum agen mulai bekerja.
2. **Eksekusi Agen Terisolasi**: Agen membangun UI di lingkungan terisolasi (*branch/container*) dengan akses membaca katalog Storybook atau token desain.
3. **Validasi Statis**: Pipeline CI/CD menjalankan pengecekan linter otomatis untuk mendeteksi deviasi token desain sebelum masuk ke tahap *review* manual.

---

## 7. Checklist Implementasi Siap Produksi

- [ ] **Sistem Spasi**: Semua layout memakai basis grid 4px/8px tanpa nilai piksel ganjil.
- [ ] **Audit Anti-Slop**: Tidak ada mesh gradient dekoratif, glassmorphism tak berdasar, atau bayangan (*box-shadow*) buram tak terkontrol.
- [ ] **Rasio Kontras**: Semua teks memenuhi standar minimal kontras rasio WCAG 2.2 AA ($4.5:1$ untuk teks biasa, $3:1$ untuk teks besar).
- [ ] **Status Lengkap**: Komponen mendukung status `Default`, `Hover`, `Active`, `Focus-Visible`, `Loading`, dan `Disabled`.
- [ ] **Ergonomi Input**: Label berada di atas bidang isian, feedback error bersifat solutif, dan autofokus diatur secara higienis.
- [ ] **Kesiapan Agen**: Token desain terdokumentasi dalam format JSON/TypeScript ketat sehingga agen AI dapat memproduksi kode yang sesuai standar arsitektur secara konsisten.