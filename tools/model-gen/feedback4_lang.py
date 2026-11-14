"""Traducciones de la cuarta ronda de arreglos (2026-11-14): saco de xirang solo con la pasiva (5 min).
También ajusta los números de moddata (tooltip_args) que cambiaron. Uso: python feedback4_lang.py"""
import io
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    "item.mythicrelics.xirang_sack.tooltip.1": "Si caes a la lava o al vacío, la tierra crece bajo tus pies y te salva (recarga %1$s min)",
    "item.mythicrelics.xirang_sack.tooltip.2": "Basta con llevarlo en el inventario",
}
EN = {
    "item.mythicrelics.xirang_sack.tooltip.1": "If you fall into lava or the void, the earth grows under your feet and saves you (cooldown %1$s min)",
    "item.mythicrelics.xirang_sack.tooltip.2": "Just keep it in your inventory",
}

REMOVE = ["item.mythicrelics.xirang_sack.tooltip.3", "tooltip.mythicrelics.xirang_charges"]

MODDATA = [
    ('"xirang_sack": [int(c["XIRANG_WIDTH"]), int(c["XIRANG_HEIGHT"]), int(c["XIRANG_CHARGES"]), int(c["XIRANG_RECHARGE_TICKS"] // 20),\n'
     '                        int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],',
     '"xirang_sack": [int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],'),
]

if __name__ == "__main__":
    assert set(ES) == set(EN), set(ES) ^ set(EN)
    for code, entries in (("es_es", ES), ("en_us", EN)):
        path = os.path.join(ASSETS, "lang", code + ".json")
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        data.update(entries)
        for k in REMOVE:
            data.pop(k, None)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(json.dumps(data, indent=2, ensure_ascii=False, sort_keys=True) + "\n")
        print(code, len(data))
    p = os.path.join(os.path.dirname(os.path.abspath(__file__)), "moddata.py")
    s = io.open(p, encoding="utf-8").read()
    for a, b in MODDATA:
        if a in s:
            s = s.replace(a, b)
        elif b not in s:
            raise SystemExit("moddata: no encontré " + a[:60])
    io.open(p, "w", encoding="utf-8", newline="\n").write(s)
    print("moddata ok")
