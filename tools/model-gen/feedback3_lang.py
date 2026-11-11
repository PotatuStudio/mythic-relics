"""Traducciones de la tercera ronda de arreglos (2026-11-10): cuervos de Odín como mascotas, Skíðblaðnir, Ruyi estirado,
abanico de viento, joyas de la marea en burbuja, mazo de la suerte, rescate del xirang, pestañas por mitología y el
bloque de set igual en todas las piezas (las líneas de set de cada ítem pasan a set.mythicrelics.<set>.bonus.N).
También ajusta los números de moddata (tooltip_args) que cambiaron. Uso: python feedback3_lang.py"""
import io
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    # Cuervos de Odín
    "item.mythicrelics.odin_ravens.tooltip.1": "Clic derecho: salen Huginn y Muninn; otra vez, vuelven con Odín",
    "item.mythicrelics.odin_ravens.tooltip.2": "Se posan cerca de ti y te miran; mientras están afuera, los hostiles a %1$s bloques brillan",
    "item.mythicrelics.odin_ravens.tooltip.3": "Agachado: Muninn te recuerda dónde moriste y dónde está tu cama",
    "entity.mythicrelics.odin_raven": "Cuervo de Odín",
    "entity.mythicrelics.huginn": "Huginn",
    "entity.mythicrelics.muninn": "Muninn",
    "message.mythicrelics.huginn": "Huginn y Muninn vuelan a tu lado",
    "message.mythicrelics.ravens_away": "Los cuervos vuelven con Odín",
    "entity.mythicrelics.skidbladnir_boat": "Skíðblaðnir",
    # Ruyi
    "item.mythicrelics.ruyi_jingu_bang.tooltip.1": "Arma pesada con %1$s bloques más de alcance",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.2": "Clic derecho: se estira %2$s bloques golpeando todo en línea recta (%3$s de daño)",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.3": "y queda largo %5$s s: +%6$s bloques de alcance y golpes al doble que barren alrededor",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.4": "Recarga: %4$s s",
    # Abanico
    "item.mythicrelics.banana_leaf_fan.tooltip.1": "Cada golpe lanza una cuchilla de viento: %1$s bloques, %2$s de daño a lo que cruza",
    "item.mythicrelics.banana_leaf_fan.tooltip.2": "Clic derecho: dash de viento de unos %3$s bloques; hiere (%6$s) y lanza lejos a los mobs que atraviesas",
    "item.mythicrelics.banana_leaf_fan.tooltip.3": "%4$s aleteos; cada uno vuelve en %5$s s",
    # Joyas de la marea
    "item.mythicrelics.tide_jewels.tooltip.1": "Kanju (clic derecho): una burbuja sin agua de %1$s bloques te rodea y te sigue durante %2$s s",
    # Mazo
    "item.mythicrelics.lucky_mallet.tooltip.1": "Clic derecho: creces %1$s veces durante %2$s s, con más alcance y %3$s corazones más",
    "item.mythicrelics.lucky_mallet.tooltip.2": "Grande: más fuerza, saltas más alto, subes bloques sin saltar y te empujan menos",
    "item.mythicrelics.lucky_mallet.tooltip.3": "Recarga: %4$s min",
    "item.mythicrelics.lucky_mallet.tooltip.4": "Agachado: los cultivos y crías a %5$s bloques crecen al instante",
    # Xirang
    "item.mythicrelics.xirang_sack.tooltip.3": "Si caes a la lava o al vacío, la tierra crece bajo tus pies y te salva (recarga %5$s min)",
    "message.mythicrelics.xirang_rescue": "¡La tierra que crece sola te salva!",
    # Pestañas
    "itemGroup.mythicrelics.egypt": "Mythic Relics · Egipto",
    "itemGroup.mythicrelics.greece": "Mythic Relics · Grecia",
    "itemGroup.mythicrelics.rome": "Mythic Relics · Roma",
    "itemGroup.mythicrelics.norse": "Mythic Relics · Nórdicos",
    "itemGroup.mythicrelics.china": "Mythic Relics · China",
    "itemGroup.mythicrelics.japan": "Mythic Relics · Japón",
    # Sets: un mismo bloque en todas las piezas
    "set.mythicrelics.anubis_set.bonus.1": "Los no-muertos no te atacan salvo que los ataques primero",
    "set.mythicrelics.osiris_set.bonus.1": "Al recibir daño mortal renaces con 4 ❤ y Regeneración II",
    "set.mythicrelics.osiris_set.bonus.2": "Recarga: %s min (un día de Minecraft)",
    "set.mythicrelics.perseus_set.bonus.1": "La cabeza de Medusa recarga a la mitad y lo petrificado recibe +50 % de daño",
    "set.mythicrelics.thor_set.bonus.1": "Cada martillo lanzado llama un rayo donde golpea, con o sin tormenta",
    "set.mythicrelics.erlang_set.bonus.1": "Cuando un mob te hiere, aparece el Perro Celestial durante 45 s (recarga 90 s)",
    "set.mythicrelics.regalia_set.bonus.1": "Kusanagi en una mano, el espejo en la otra y la magatama puesta",
    "set.mythicrelics.regalia_set.bonus.2": "De día: +20 % de daño y Resistencia I",
    "set.mythicrelics.fujin_raijin_set.bonus.1": "La bolsa en el inventario y el tambor puesto",
    "set.mythicrelics.fujin_raijin_set.bonus.2": "El rayo cae cada 3 golpes y salta a 2 hostiles cercanos",
    "item.mythicrelics.kusanagi.tooltip.3": "Recarga: %3$s s",
    "item.mythicrelics.yata_mirror.tooltip.3": "Recarga: %3$s s",
}

EN = {
    "item.mythicrelics.odin_ravens.tooltip.1": "Right-click: Huginn and Muninn come out; again, they return to Odin",
    "item.mythicrelics.odin_ravens.tooltip.2": "They perch near you and watch you; while they're out, hostiles within %1$s blocks glow",
    "item.mythicrelics.odin_ravens.tooltip.3": "Sneaking: Muninn reminds you where you died and where your bed is",
    "entity.mythicrelics.odin_raven": "Raven of Odin",
    "entity.mythicrelics.huginn": "Huginn",
    "entity.mythicrelics.muninn": "Muninn",
    "message.mythicrelics.huginn": "Huginn and Muninn fly to your side",
    "message.mythicrelics.ravens_away": "The ravens return to Odin",
    "entity.mythicrelics.skidbladnir_boat": "Skíðblaðnir",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.1": "Heavy weapon with %1$s more blocks of reach",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.2": "Right-click: stretches %2$s blocks hitting everything in a line (%3$s damage)",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.3": "and stays long for %5$s s: +%6$s blocks of reach and double hits that sweep around",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.4": "Cooldown: %4$s s",
    "item.mythicrelics.banana_leaf_fan.tooltip.1": "Each hit throws a wind blade: %1$s blocks, %2$s damage to whatever it crosses",
    "item.mythicrelics.banana_leaf_fan.tooltip.2": "Right-click: a wind dash of about %3$s blocks; hurts (%6$s) and flings the mobs you pass through",
    "item.mythicrelics.banana_leaf_fan.tooltip.3": "%4$s flaps; each comes back in %5$s s",
    "item.mythicrelics.tide_jewels.tooltip.1": "Kanju (right-click): a %1$s-block bubble without water surrounds and follows you for %2$s s",
    "item.mythicrelics.lucky_mallet.tooltip.1": "Right-click: grow %1$s times for %2$s s, with more reach and %3$s extra hearts",
    "item.mythicrelics.lucky_mallet.tooltip.2": "While big: more strength, higher jumps, step up blocks without jumping, less knockback",
    "item.mythicrelics.lucky_mallet.tooltip.3": "Cooldown: %4$s min",
    "item.mythicrelics.lucky_mallet.tooltip.4": "Sneaking: crops and young animals within %5$s blocks grow instantly",
    "item.mythicrelics.xirang_sack.tooltip.3": "If you fall into lava or the void, the earth grows under your feet and saves you (cooldown %5$s min)",
    "message.mythicrelics.xirang_rescue": "The self-growing earth saves you!",
    "itemGroup.mythicrelics.egypt": "Mythic Relics · Egypt",
    "itemGroup.mythicrelics.greece": "Mythic Relics · Greece",
    "itemGroup.mythicrelics.rome": "Mythic Relics · Rome",
    "itemGroup.mythicrelics.norse": "Mythic Relics · Norse",
    "itemGroup.mythicrelics.china": "Mythic Relics · China",
    "itemGroup.mythicrelics.japan": "Mythic Relics · Japan",
    "set.mythicrelics.anubis_set.bonus.1": "The undead don't attack you unless you attack them first",
    "set.mythicrelics.osiris_set.bonus.1": "On lethal damage you are reborn with 4 ❤ and Regeneration II",
    "set.mythicrelics.osiris_set.bonus.2": "Cooldown: %s min (one Minecraft day)",
    "set.mythicrelics.perseus_set.bonus.1": "Medusa's head recharges twice as fast and petrified mobs take +50% damage",
    "set.mythicrelics.thor_set.bonus.1": "Every thrown hammer calls down lightning where it hits, storm or not",
    "set.mythicrelics.erlang_set.bonus.1": "When a mob hurts you, the Celestial Hound appears for 45 s (90 s cooldown)",
    "set.mythicrelics.regalia_set.bonus.1": "Kusanagi in one hand, the mirror in the other and the magatama worn",
    "set.mythicrelics.regalia_set.bonus.2": "By day: +20% damage and Resistance I",
    "set.mythicrelics.fujin_raijin_set.bonus.1": "The bag in your inventory and the drum worn",
    "set.mythicrelics.fujin_raijin_set.bonus.2": "Lightning strikes every 3 hits and jumps to 2 nearby hostiles",
    "item.mythicrelics.kusanagi.tooltip.3": "Cooldown: %3$s s",
    "item.mythicrelics.yata_mirror.tooltip.3": "Cooldown: %3$s s",
}

# Líneas de set que estaban dentro de los tooltips de cada ítem (ahora van en el bloque del set).
REMOVE = ["item.mythicrelics.erlang_halberd.tooltip.2", "item.mythicrelics.erlang_halberd.tooltip.3",
          "item.mythicrelics.fujin_wind_bag.tooltip.4", "item.mythicrelics.yasakani_magatama.tooltip.2",
          "item.mythicrelics.banana_leaf_fan.tooltip.4"]

MODDATA = [
    ('"odin_ravens": [int(c["RAVENS_RADIUS"]), int(c["RAVENS_GLOW_TICKS"] // 20), int(c["RAVENS_COOLDOWN"] // 20)],',
     '"odin_ravens": [int(c["RAVENS_RADIUS"])],'),
    ('"ruyi_jingu_bang": [fmt(c["RUYI_REACH"]), int(c["RUYI_LENGTH"]), int(c["RUYI_STRETCH_DAMAGE"]), int(c["RUYI_COOLDOWN"] // 20)],',
     '"ruyi_jingu_bang": [fmt(c["RUYI_REACH"]), int(c["RUYI_LENGTH"]), int(c["RUYI_STRETCH_DAMAGE"]), int(c["RUYI_COOLDOWN"] // 20),\n'
     '                            int(c["RUYI_EXTENDED_TICKS"] // 20), int(c["RUYI_EXTENDED_REACH"])],'),
    ('"xirang_sack": [int(c["XIRANG_WIDTH"]), int(c["XIRANG_HEIGHT"]), int(c["XIRANG_CHARGES"]), int(c["XIRANG_RECHARGE_TICKS"] // 20)],',
     '"xirang_sack": [int(c["XIRANG_WIDTH"]), int(c["XIRANG_HEIGHT"]), int(c["XIRANG_CHARGES"]), int(c["XIRANG_RECHARGE_TICKS"] // 20),\n'
     '                        int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],'),
    ('"tide_jewels": [int(c["TIDE_LENGTH"]), int(c["TIDE_TICKS"] // 20),', '"tide_jewels": [int(c["TIDE_BUBBLE_RADIUS"]), int(c["TIDE_TICKS"] // 20),'),
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
            s = s.replace(a, b, 1)
    # Abanico: ya no hay lluvia; el sexto número es el daño del dash.
    i = s.index('"banana_leaf_fan": [')
    j = s.index("],", i) + 2
    s = s[:i] + ('"banana_leaf_fan": [int(c["FAN_BLADE_RANGE"]), int(c["FAN_BLADE_DAMAGE"]), int(c["FAN_DASH_BLOCKS"]), int(c["FAN_CHARGES"]),\n'
                 '                            int(c["FAN_RECHARGE_TICKS"] // 20), int(c["FAN_DASH_DAMAGE"])],') + s[j:]
    io.open(p, "w", encoding="utf-8", newline="\n").write(s)
    print("moddata ok")
