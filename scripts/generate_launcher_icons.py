"""Generate Android launcher resources from the licensed source icon."""
from pathlib import Path
import sys

from PIL import Image


source = Image.open(sys.argv[1]).convert("RGBA")
resources = Path(__file__).parents[1] / "app" / "src" / "main" / "res"

(resources / "drawable-nodpi").mkdir(parents=True, exist_ok=True)
source.save(resources / "drawable-nodpi" / "cogsworth_icon_source.png")

for folder, size in (
    ("mipmap-mdpi", 48),
    ("mipmap-hdpi", 72),
    ("mipmap-xhdpi", 96),
    ("mipmap-xxhdpi", 144),
    ("mipmap-xxxhdpi", 192),
):
    destination = resources / folder
    destination.mkdir(parents=True, exist_ok=True)
    canvas = Image.new("RGBA", (size, size), (247, 240, 229, 255))
    art = source.resize((round(size * 0.84), round(size * 0.84)), Image.Resampling.LANCZOS)
    canvas.alpha_composite(art, ((size - art.width) // 2, (size - art.height) // 2))
    canvas.save(destination / "ic_launcher.png")

foreground_dir = resources / "drawable"
foreground_dir.mkdir(parents=True, exist_ok=True)
foreground = Image.new("RGBA", (432, 432), (0, 0, 0, 0))
foreground.alpha_composite(source.resize((288, 288), Image.Resampling.LANCZOS), (72, 72))
foreground.save(foreground_dir / "ic_launcher_foreground.png")
