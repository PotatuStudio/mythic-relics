"""Traducciones de la segunda ronda de arreglos (2026-11-10): bula del lobo, lámpara de Vesta, libros sibilinos, cabeza
de Mímir y candados de Jano. Uso: python feedback2_lang.py"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    "item.mythicrelics.wolf_bulla.tooltip.1": "Tus lobos a %1$s bloques tienen Regeneración I y Fuerza I",
    "item.mythicrelics.wolf_bulla.tooltip.2": "Los lobos salvajes no te atacan",
    "item.mythicrelics.wolf_bulla.tooltip.3": "Con %2$s corazones o menos aúllas: acuden %3$s lobos aliados por %4$s s",
    "item.mythicrelics.wolf_bulla.tooltip.4": "Recarga: %5$s min",
    "entity.mythicrelics.howl_wolf": "Lobo de la loba",
    "message.mythicrelics.wolf_howl": "¡Aúllas y acuden los hijos de la loba!",
    "item.mythicrelics.vesta_lamp.tooltip.1": "Clic derecho en el suelo: enciendes el fuego del hogar (%3$s min)",
    "item.mythicrelics.vesta_lamp.tooltip.2": "En la mano secundaria, a %1$s bloques de una fogata: Regeneración II",
    "item.mythicrelics.vesta_lamp.tooltip.3": "En la mano secundaria, siempre: el hambre baja al %2$s%% y la nieve polvo no te congela",
    "item.mythicrelics.vesta_lamp.tooltip.4": "Recarga del fuego: %4$s min",
    "item.mythicrelics.sibylline_books.tooltip.3": "y te da un mapa con la estructura más cercana; letras mágicas te guían a ella",
    "item.mythicrelics.sibylline_books.tooltip.5": "Cada consulta profetiza una estructura distinta",
    "item.mythicrelics.sibyl_map": "Profecía: %s",
    "item.mythicrelics.mimir_head.tooltip.1": "En la mano secundaria: cada %2$s s, los minerales valiosos",
    "item.mythicrelics.mimir_head.tooltip.2": "a %1$s bloques brillan a través de la piedra",
    "item.mythicrelics.mimir_head.tooltip.3": "Mímir te susurra pistas: minerales, cofres, generadores y peligros",
    "item.mythicrelics.mimir_head.tooltip.4": "Te avisa cuando un creeper empieza a explotar",
    "message.mythicrelics.mimir_whisper": "Mímir susurra: %s",
    "message.mythicrelics.mimir_ore": "hay %s a %s bloques, %s",
    "message.mythicrelics.mimir_chest": "un cofre que nadie ha abierto espera a %s bloques, %s",
    "message.mythicrelics.mimir_spawner": "algo engendra monstruos a %s bloques, %s",
    "message.mythicrelics.mimir_danger": "%s enemigos te rodean; ten cuidado",
    "message.mythicrelics.mimir_where.up": "arriba, al %s",
    "message.mythicrelics.mimir_where.down": "abajo, al %s",
    "message.mythicrelics.mimir_where.level": "al %s",
    "message.mythicrelics.janus_locked_by": "Cerrado con llave por %s",
    "message.mythicrelics.janus_someone": "otro jugador",
    "item.mythicrelics.janus_key.tooltip.2": "valla o cofre: lo cierras con llave y cuelga un candado (solo tú lo abres)",
    "item.mythicrelics.padlock": "Candado de Jano",
}

EN = {
    "item.mythicrelics.wolf_bulla.tooltip.1": "Your wolves within %1$s blocks get Regeneration I and Strength I",
    "item.mythicrelics.wolf_bulla.tooltip.2": "Wild wolves don't attack you",
    "item.mythicrelics.wolf_bulla.tooltip.3": "At %2$s hearts or less you howl: %3$s allied wolves come for %4$s s",
    "item.mythicrelics.wolf_bulla.tooltip.4": "Cooldown: %5$s min",
    "entity.mythicrelics.howl_wolf": "Wolf of the She-wolf",
    "message.mythicrelics.wolf_howl": "You howl and the she-wolf's children come!",
    "item.mythicrelics.vesta_lamp.tooltip.1": "Right-click the ground: light the hearth fire (%3$s min)",
    "item.mythicrelics.vesta_lamp.tooltip.2": "In the off hand, within %1$s blocks of a campfire: Regeneration II",
    "item.mythicrelics.vesta_lamp.tooltip.3": "In the off hand, always: hunger drops to %2$s%% and powder snow doesn't freeze you",
    "item.mythicrelics.vesta_lamp.tooltip.4": "Fire cooldown: %4$s min",
    "item.mythicrelics.sibylline_books.tooltip.3": "and gives you a map to the nearest structure; magic letters guide you there",
    "item.mythicrelics.sibylline_books.tooltip.5": "Each reading foretells a different structure",
    "item.mythicrelics.sibyl_map": "Prophecy: %s",
    "item.mythicrelics.mimir_head.tooltip.1": "In the off hand: every %2$s s, valuable ores",
    "item.mythicrelics.mimir_head.tooltip.2": "within %1$s blocks glow through the stone",
    "item.mythicrelics.mimir_head.tooltip.3": "Mímir whispers hints: ores, chests, spawners and dangers",
    "item.mythicrelics.mimir_head.tooltip.4": "Warns you when a creeper starts to explode",
    "message.mythicrelics.mimir_whisper": "Mímir whispers: %s",
    "message.mythicrelics.mimir_ore": "there is %s %s blocks away, %s",
    "message.mythicrelics.mimir_chest": "a chest nobody has opened waits %s blocks away, %s",
    "message.mythicrelics.mimir_spawner": "something spawns monsters %s blocks away, %s",
    "message.mythicrelics.mimir_danger": "%s enemies surround you; be careful",
    "message.mythicrelics.mimir_where.up": "above, to the %s",
    "message.mythicrelics.mimir_where.down": "below, to the %s",
    "message.mythicrelics.mimir_where.level": "to the %s",
    "message.mythicrelics.janus_locked_by": "Locked by %s",
    "message.mythicrelics.janus_someone": "another player",
    "item.mythicrelics.janus_key.tooltip.2": "fence gate or chest: you lock it and a padlock hangs on it (only you open it)",
    "item.mythicrelics.padlock": "Janus Padlock",
}

if __name__ == "__main__":
    assert set(ES) == set(EN), set(ES) ^ set(EN)
    for code, entries in (("es_es", ES), ("en_us", EN)):
        path = os.path.join(ASSETS, "lang", code + ".json")
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        data.update(entries)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(json.dumps(data, indent=2, ensure_ascii=False, sort_keys=True) + "\n")
        print(code, len(data))
