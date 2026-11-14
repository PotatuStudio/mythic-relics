"""Traducciones de la cuarta ronda de arreglos (2026-11-14): saco de xirang solo con la pasiva (5 min), harpe pasiva, Mímir como guía, aviso del cetro de Anubis, halcón con tecla y forma real, Mímir se pregunta (clic derecho), mirada de Medusa sostenida, tooltip corto con Shift, dardo que no vuelve.
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
    "item.mythicrelics.mimir_head.tooltip.1": "Clic derecho: le preguntas a Mímir por el tesoro más valioso a %3$s bloques (debris, diamante, cofre sin abrir…)",
    "item.mythicrelics.mimir_head.tooltip.2": "Te responde una vez y te guía con una estela de luz %2$s s; brillan los minerales valiosos a %1$s bloques y los cofres sin abrir",
    "item.mythicrelics.mimir_head.tooltip.3": "Recarga: %4$s s",
    "message.mythicrelics.mimir_arrived": "Mímir: aquí está",
    "tooltip.mythicrelics.more": "Shift: ver detalles",
    "item.mythicrelics.mistletoe_dart.tooltip.1": "Mantén clic derecho y suelta para lanzarlo; queda clavado donde cae hasta que lo recojas",
    "item.mythicrelics.medusa_head.tooltip.1": "Mantén clic derecho para alzarla como un escudo: sus ojos abren un cono de mirada de %2$s bloques",
    "item.mythicrelics.medusa_head.tooltip.2": "Todo mob que quede %3$s ms en la mirada se petrifica %1$s s (hostiles, neutros y pasivos; no jefes)",
    "item.mythicrelics.medusa_head.tooltip.3": "La piedra se agrieta hasta romperse. Si muere así se derrumba: doble botín y a veces su estatua",
    "set.mythicrelics.perseus_set.bonus.1": "La mirada de Medusa petrifica el doble de rápido y lo petrificado recibe +50 % de daño",
    "item.mythicrelics.mimir_head.tooltip.4": "Mientras lo sostienes, te avisa cuando un creeper empieza a explotar",
    "message.mythicrelics.mimir_guide": "Mímir: %s a %s bloques, %s",
    "message.mythicrelics.mimir_nothing": "Mímir: no siento ningún tesoro cerca",
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
    "item.mythicrelics.mimir_head.tooltip.1": "Right-click: ask Mímir for the most valuable treasure within %3$s blocks (debris, diamond, unopened chest…)",
    "item.mythicrelics.mimir_head.tooltip.2": "He answers once and guides you with a trail of light for %2$s s; valuable ores within %1$s blocks and unopened chests glow",
    "item.mythicrelics.mimir_head.tooltip.3": "Cooldown: %4$s s",
    "message.mythicrelics.mimir_arrived": "Mímir: here it is",
    "tooltip.mythicrelics.more": "Shift: details",
    "item.mythicrelics.mistletoe_dart.tooltip.1": "Hold right click and release to throw it; it stays where it lands until you pick it up",
    "item.mythicrelics.medusa_head.tooltip.1": "Hold right-click to raise it like a shield: its eyes open a gaze cone of %2$s blocks",
    "item.mythicrelics.medusa_head.tooltip.2": "Any mob that stays %3$s ms in the gaze turns to stone for %1$s s (hostile, neutral and passive; not bosses)",
    "item.mythicrelics.medusa_head.tooltip.3": "The stone cracks until it breaks. If it dies like that it crumbles: double loot and sometimes its statue",
    "set.mythicrelics.perseus_set.bonus.1": "Medusa's gaze petrifies twice as fast and petrified mobs take +50% damage",
    "item.mythicrelics.mimir_head.tooltip.4": "While you hold him, he warns you when a creeper starts to explode",
    "message.mythicrelics.mimir_guide": "Mímir: %s, %s blocks, %s",
    "message.mythicrelics.mimir_nothing": "Mímir: I sense no treasure nearby",
    "message.mythicrelics.mimir_chest_name": "an unopened chest",
    "message.mythicrelics.anubis_ready": "Anubis can judge that undead",
    "item.mythicrelics.falcon_cloak.tooltip.1": "Cloak key (V by default, in Controls): you become a falcon for %1$s s, with its size and shape",
    "item.mythicrelics.falcon_cloak.tooltip.2": "You only fly: no attacking, using, breaking, placing or picking up anything. The same key turns you back; being hit does too (and you fall slowly)",
    "message.mythicrelics.falcon_start": "You are a falcon (%1$s s). The cloak key turns you back",
    "key.mythicrelics.falcon_form": "Falcon form (Freyja's cloak)",
}

REMOVE = ["item.mythicrelics.xirang_sack.tooltip.3", "tooltip.mythicrelics.xirang_charges",
          "message.mythicrelics.mimir_chest", "message.mythicrelics.mimir_danger", "message.mythicrelics.mimir_ore",
          "message.mythicrelics.mimir_spawner", "message.mythicrelics.mimir_whisper",
          "message.mythicrelics.mimir_silent"]

MODDATA = [
    ('"xirang_sack": [int(c["XIRANG_WIDTH"]), int(c["XIRANG_HEIGHT"]), int(c["XIRANG_CHARGES"]), int(c["XIRANG_RECHARGE_TICKS"] // 20),\n'
     '                        int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],',
     '"xirang_sack": [int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],'),
    ('"mimir_head": [int(c["MIMIR_RADIUS"]), int(c["MIMIR_INTERVAL"] // 20), int(c["GUIDE_RADIUS"])],',
     '"mimir_head": [int(c["MIMIR_RADIUS"]), int(c["GUIDE_TICKS"] // 20), int(c["GUIDE_RADIUS"]), int(c["ASK_COOLDOWN"] // 20)],'),
    ('"medusa_head": [int(c["MEDUSA_TICKS"] // 20), int(c["MEDUSA_RADIUS"]), int(c["MEDUSA_COOLDOWN"] // 20)],',
     '"medusa_head": [int(c["MEDUSA_TICKS"] // 20), int(c["MEDUSA_RADIUS"]), int(c["MEDUSA_GAZE_TICKS"] * 50)],'),
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
