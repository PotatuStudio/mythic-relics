"""Traducciones de la cuarta ronda de arreglos (2026-11-14): saco de xirang solo con la pasiva (5 min), harpe pasiva, Mímir como guía, aviso del cetro de Anubis, halcón con tecla y forma real.
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
    "item.mythicrelics.harpe.tooltip.1": "Pasiva: golpear a un mob con menos del %3$s%% de vida le corta la cabeza al instante (no jefes)",
    "item.mythicrelics.harpe.tooltip.2": "La cabeza cae el %1$s%% de las veces (esqueleto del Wither: %2$s%%)",
    "item.mythicrelics.harpe.tooltip.3": "Con la hoz lista, los mobs que ya se pueden decapitar brillan",
    "item.mythicrelics.mimir_head.tooltip.3": "Te guía con una estela de luz al tesoro más valioso a %3$s bloques (debris, diamante, cofre sin abrir…)",
    "message.mythicrelics.mimir_guide": "Mímir: %s a %s bloques, %s",
    "message.mythicrelics.mimir_nothing": "Mímir: no siento ningún tesoro cerca",
    "message.mythicrelics.mimir_silent": "Mímir calla",
    "message.mythicrelics.mimir_chest_name": "un cofre sin abrir",
    "message.mythicrelics.anubis_ready": "Anubis puede juzgar a ese no-muerto",
    "item.mythicrelics.falcon_cloak.tooltip.1": "Tecla del manto (V por defecto, en Controles): te vuelves halcón %1$s s, con su tamaño y su forma",
    "item.mythicrelics.falcon_cloak.tooltip.2": "Solo vuelas: no atacas, no usas, no rompes, no pones ni recoges nada. La misma tecla te vuelve humano; si te golpean, también (y caes lento)",
    "message.mythicrelics.falcon_start": "Eres un halcón (%1$s s). La tecla del manto te vuelve humano",
    "key.mythicrelics.falcon_form": "Forma de halcón (manto de Freyja)",
}
EN = {
    "item.mythicrelics.xirang_sack.tooltip.1": "If you fall into lava or the void, the earth grows under your feet and saves you (cooldown %1$s min)",
    "item.mythicrelics.xirang_sack.tooltip.2": "Just keep it in your inventory",
    "item.mythicrelics.harpe.tooltip.1": "Passive: hitting a mob below %3$s%% health cuts its head off instantly (not bosses)",
    "item.mythicrelics.harpe.tooltip.2": "The head drops %1$s%% of the time (Wither skeleton: %2$s%%)",
    "item.mythicrelics.harpe.tooltip.3": "With the sickle ready, mobs that can be beheaded glow",
    "item.mythicrelics.mimir_head.tooltip.3": "Guides you with a trail of light to the most valuable treasure within %3$s blocks (debris, diamond, unopened chest…)",
    "message.mythicrelics.mimir_guide": "Mímir: %s, %s blocks, %s",
    "message.mythicrelics.mimir_nothing": "Mímir: I sense no treasure nearby",
    "message.mythicrelics.mimir_silent": "Mímir falls silent",
    "message.mythicrelics.mimir_chest_name": "an unopened chest",
    "message.mythicrelics.anubis_ready": "Anubis can judge that undead",
    "item.mythicrelics.falcon_cloak.tooltip.1": "Cloak key (V by default, in Controls): you become a falcon for %1$s s, with its size and shape",
    "item.mythicrelics.falcon_cloak.tooltip.2": "You only fly: no attacking, using, breaking, placing or picking up anything. The same key turns you back; being hit does too (and you fall slowly)",
    "message.mythicrelics.falcon_start": "You are a falcon (%1$s s). The cloak key turns you back",
    "key.mythicrelics.falcon_form": "Falcon form (Freyja's cloak)",
}

REMOVE = ["item.mythicrelics.xirang_sack.tooltip.3", "tooltip.mythicrelics.xirang_charges",
          "message.mythicrelics.mimir_chest", "message.mythicrelics.mimir_danger", "message.mythicrelics.mimir_ore",
          "message.mythicrelics.mimir_spawner", "message.mythicrelics.mimir_whisper"]

MODDATA = [
    ('"xirang_sack": [int(c["XIRANG_WIDTH"]), int(c["XIRANG_HEIGHT"]), int(c["XIRANG_CHARGES"]), int(c["XIRANG_RECHARGE_TICKS"] // 20),\n'
     '                        int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],',
     '"xirang_sack": [int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],'),
    ('"mimir_head": [int(c["MIMIR_RADIUS"]), int(c["MIMIR_INTERVAL"] // 20)],',
     '"mimir_head": [int(c["MIMIR_RADIUS"]), int(c["MIMIR_INTERVAL"] // 20), int(c["GUIDE_RADIUS"])],'),
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
