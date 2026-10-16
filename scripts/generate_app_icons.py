#!/usr/bin/env python3
"""
Generate Android launcher icon assets for Coffee Creator.
Outputs adaptive foregrounds, legacy square/round density buckets, and XML descriptors.
"""

import os
import sys
import numpy as np
from PIL import Image, ImageDraw

def generate_icons(master_path: str, res_dir: str):
    print(f"Loading master icon from: {master_path}")
    master_img = Image.open(master_path).convert("RGB")
    master_arr = np.array(master_img, dtype=np.float32)

    width, height = master_img.size
    cx, cy = (width - 1) / 2.0, (height - 1) / 2.0

    # 1. Background color sampled from corners
    c1 = master_arr[:20, :20, :3]
    c2 = master_arr[:20, -20:, :3]
    c3 = master_arr[-20:, :20, :3]
    c4 = master_arr[-20:, -20:, :3]
    bg_color = np.mean([c1.mean(axis=(0, 1)), c2.mean(axis=(0, 1)), c3.mean(axis=(0, 1)), c4.mean(axis=(0, 1))], axis=0)
    bg_tuple = (int(round(bg_color[0])), int(round(bg_color[1])), int(round(bg_color[2])))
    bg_hex = f"#{bg_tuple[0]:02X}{bg_tuple[1]:02X}{bg_tuple[2]:02X}"
    print(f"Detected background color: {bg_tuple} ({bg_hex})")

    # 2. Extract transparent foreground (emblem with alpha)
    y, x = np.ogrid[:height, :width]
    r = np.sqrt((x - cx) ** 2 + (y - cy) ** 2)

    dist = np.linalg.norm(master_arr - bg_color, axis=2)
    alpha = np.clip((dist - 6.0) / 18.0, 0.0, 1.0)
    # Radial boundary clamp: smooth falloff at radius 282..286
    rad_factor = np.clip(1.0 - (r - 282.0) / 4.0, 0.0, 1.0)
    alpha = alpha * rad_factor

    rgba = np.zeros((height, width, 4), dtype=np.uint8)
    rgba[:, :, :3] = np.clip(master_arr, 0, 255).astype(np.uint8)
    rgba[:, :, 3] = (alpha * 255.0).astype(np.uint8)
    foreground_master = Image.fromarray(rgba, mode="RGBA")

    # 3. Create full master composite (square)
    square_master = Image.new("RGB", (width, height), bg_tuple)
    square_master.paste(foreground_master, (0, 0), foreground_master)

    # 4. Create round master (masked to circle)
    round_master = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    circle_mask = Image.new("L", (width, height), 0)
    mask_draw = ImageDraw.Draw(circle_mask)
    # Circle with 1px inset to avoid edge bleed
    mask_draw.ellipse([4, 4, width - 4, height - 4], fill=255)
    round_master.paste(square_master, (0, 0), circle_mask)

    # 5. Density configurations
    # Adaptive foreground: 108dp base
    foreground_densities = {
        "mipmap-mdpi": 108,
        "mipmap-hdpi": 162,
        "mipmap-xhdpi": 216,
        "mipmap-xxhdpi": 324,
        "mipmap-xxxhdpi": 432,
    }

    # Legacy launcher icons: 48dp base
    legacy_densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }

    # 6. Save assets
    raw_dir = os.path.join(res_dir, "raw")
    os.makedirs(raw_dir, exist_ok=True)
    master_png_path = os.path.join(raw_dir, "app_icon_master.png")
    square_master.save(master_png_path, "PNG")
    print(f"Saved master icon to: {master_png_path}")

    for density, size in foreground_densities.items():
        folder = os.path.join(res_dir, density)
        os.makedirs(folder, exist_ok=True)
        resized_fg = foreground_master.resize((size, size), Image.Resampling.LANCZOS)
        out_path = os.path.join(folder, "ic_launcher_foreground.png")
        resized_fg.save(out_path, "PNG")
        print(f"Generated {out_path} ({size}x{size})")

    for density, size in legacy_densities.items():
        folder = os.path.join(res_dir, density)
        os.makedirs(folder, exist_ok=True)

        # Legacy square
        resized_sq = square_master.resize((size, size), Image.Resampling.LANCZOS)
        out_sq = os.path.join(folder, "ic_launcher.png")
        resized_sq.save(out_sq, "PNG")

        # Legacy round
        resized_rd = round_master.resize((size, size), Image.Resampling.LANCZOS)
        out_rd = os.path.join(folder, "ic_launcher_round.png")
        resized_rd.save(out_rd, "PNG")
        print(f"Generated legacy icons in {folder} ({size}x{size})")

    # 7. Write XML files
    values_dir = os.path.join(res_dir, "values")
    os.makedirs(values_dir, exist_ok=True)
    bg_xml_path = os.path.join(values_dir, "ic_launcher_background.xml")
    with open(bg_xml_path, "w", encoding="utf-8") as f:
        f.write(f"""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">{bg_hex}</color>
</resources>
""")
    print(f"Wrote {bg_xml_path}")

    anydpi_dir = os.path.join(res_dir, "mipmap-anydpi-v26")
    os.makedirs(anydpi_dir, exist_ok=True)

    adaptive_xml = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
"""
    launcher_xml_path = os.path.join(anydpi_dir, "ic_launcher.xml")
    round_xml_path = os.path.join(anydpi_dir, "ic_launcher_round.xml")
    with open(launcher_xml_path, "w", encoding="utf-8") as f:
        f.write(adaptive_xml)
    with open(round_xml_path, "w", encoding="utf-8") as f:
        f.write(adaptive_xml)
    print(f"Wrote {launcher_xml_path} and {round_xml_path}")
    print("App icon generation complete!")

if __name__ == "__main__":
    master_path = r"C:\Users\HP OMEN\.gemini\antigravity\brain\3f4485d8-461f-492c-b8da-3ed04f571adf\coffee_creator_master_icon_1792180014887.jpg"
    res_dir = r"D:\SEMESTER13\KodeProject\CoffeCreator\app\src\main\res"
    if len(sys.argv) > 1:
        master_path = sys.argv[1]
    if len(sys.argv) > 2:
        res_dir = sys.argv[2]
    generate_icons(master_path, res_dir)
