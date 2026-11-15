"""Textos de la quinta ronda de arreglos (2026-11-14): dardo de muérdago sin recarga, Medusa a la mitad de alcance, lira de
Orfeo (Epitafio de Seikilos, frena poco), yelmo de Hades (oculta todo y agachado vas más rápido), Gram pasiva, talismán
fu visible, Gleipnir (atar y debilitar), espada de Freyr en la espalda y +30 % al arma de cada set (las entradas de
las reliquias chinas y japonesas que se quitaron el 2026-11-15 ya no están).
También ajusta los números de moddata (tooltip_args) que cambiaron. Uso: python feedback5_lang.py"""
import io
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    "item.mythicrelics.mistletoe_dart.tooltip.1": "Mantén clic derecho y suelta para lanzarlo; queda clavado donde cae hasta que lo recojas (sin recarga)",
    "item.mythicrelics.gleipnir.tooltip.1": "Clic derecho a un mob: queda atado %1$s s, sin moverse ni atacar",
    "item.mythicrelics.gleipnir.tooltip.2": "Mientras está atado recibe +%3$s%% de daño",
    "item.mythicrelics.gleipnir.tooltip.3": "A los jefes no los ata: los frena mucho durante %4$s s",
    "item.mythicrelics.gleipnir.tooltip.4": "Recarga: %2$s s",
    "item.mythicrelics.gram.tooltip.1": "Pasiva: cada %1$s golpes, el siguiente es una estocada con +%2$s%% de daño",
    "item.mythicrelics.gram.tooltip.2": "La estocada hace el doble contra mobs de %3$s de vida o más (dragón, Wither, devastador, gólem, Warden)",
    "item.mythicrelics.orpheus_lyre.tooltip.1": "Mantén clic derecho para tocar el Epitafio de Seikilos, la canción griega completa más antigua:",
    "item.mythicrelics.orpheus_lyre.tooltip.4": "Mientras tocas caminas un poco más lento",
    "item.mythicrelics.hades_helm.tooltip.1": "Agachado y quieto %1$s s: te vuelves invisible del todo (armadura, reliquias y manos incluidas)",
    "item.mythicrelics.hades_helm.tooltip.4": "Con el yelmo puesto, agachado caminas un %2$s%% más rápido",
    "item.mythicrelics.fu_talismans.tooltip.1": "Clic derecho a un no-muerto: le pegas un talismán en la frente y queda inmóvil %1$s s",
    "item.mythicrelics.freyr_sword.tooltip.1": "Va envainada en la espalda. En combate (o con un hostil persiguiéndote a %1$s bloques) sale y pelea sola",
    "set.mythicrelics.anubis_set.bonus.2": "+30 % de daño con el cetro de Anubis",
    "set.mythicrelics.osiris_set.bonus.3": "+30 % de daño con el mayal",
    "set.mythicrelics.perseus_set.bonus.2": "+30 % de daño con la harpe",
    "item.mythicrelics.anubis_scepter.tooltip.1": "Juicio: el golpe que deja al no-muerto bajo el %1$s%% de vida, o que lo mata,",
    "item.mythicrelics.anubis_scepter.tooltip.2": "lo destruye y suelta %2$s veces la experiencia",
    "set.mythicrelics.thor_set.bonus.2": "+30 % de daño de Mjölnir lanzado y de sus rayos",
}
EN = {
    "item.mythicrelics.mistletoe_dart.tooltip.1": "Hold right-click and release to throw it; it stays stuck where it lands until you pick it up (no cooldown)",
    "item.mythicrelics.gleipnir.tooltip.1": "Right-click a mob: it is bound for %1$s s, unable to move or attack",
    "item.mythicrelics.gleipnir.tooltip.2": "While bound it takes +%3$s%% damage",
    "item.mythicrelics.gleipnir.tooltip.3": "Bosses can't be bound: they are heavily slowed for %4$s s",
    "item.mythicrelics.gleipnir.tooltip.4": "Cooldown: %2$s s",
    "item.mythicrelics.gram.tooltip.1": "Passive: every %1$s hits, the next one is a thrust with +%2$s%% damage",
    "item.mythicrelics.gram.tooltip.2": "The thrust deals double against mobs with %3$s health or more (dragon, Wither, ravager, golem, Warden)",
    "item.mythicrelics.orpheus_lyre.tooltip.1": "Hold right-click to play the Seikilos epitaph, the oldest complete Greek song:",
    "item.mythicrelics.orpheus_lyre.tooltip.4": "While playing you walk a little slower",
    "item.mythicrelics.hades_helm.tooltip.1": "Sneak still for %1$s s: you become fully invisible (armor, relics and hands included)",
    "item.mythicrelics.hades_helm.tooltip.4": "While wearing the helm you sneak %2$s%% faster",
    "item.mythicrelics.fu_talismans.tooltip.1": "Right-click an undead: you stick a talisman on its forehead and it is frozen for %1$s s",
    "item.mythicrelics.freyr_sword.tooltip.1": "Sheathed on your back. In combat (or with a hostile chasing you within %1$s blocks) it draws itself and fights",
    "set.mythicrelics.anubis_set.bonus.2": "+30% damage with the Scepter of Anubis",
    "set.mythicrelics.osiris_set.bonus.3": "+30% damage with the flail",
    "set.mythicrelics.perseus_set.bonus.2": "+30% damage with the harpe",
    "item.mythicrelics.anubis_scepter.tooltip.1": "Judgment: a hit that leaves undead below %1$s%% health, or kills them,",
    "item.mythicrelics.anubis_scepter.tooltip.2": "destroys them and drops %2$sx experience",
    "set.mythicrelics.thor_set.bonus.2": "+30% damage from thrown Mjölnir and its lightning",
}

REMOVE = ["item.mythicrelics.mistletoe_dart.tooltip.3", "item.mythicrelics.gram.tooltip.3", "item.mythicrelics.gram.tooltip.4",
          "message.mythicrelics.gram_ready"]

MODDATA = [
    ('"hades_helm": [int(c["HADES_STILL_TICKS"] // 20)],',
     '"hades_helm": [int(c["HADES_STILL_TICKS"] // 20), round(c["HADES_SNEAK_SPEED"] * 100 / 0.3)],'),
    ('"gleipnir": [int(c["GLEIPNIR_TICKS"] // 20), int(c["GLEIPNIR_COOLDOWN"] // 20)],',
     '"gleipnir": [int(c["GLEIPNIR_TICKS"] // 20), int(c["GLEIPNIR_COOLDOWN"] // 20), round(c["GLEIPNIR_DAMAGE_TAKEN"] * 100),\n'
     '                     int(c["GLEIPNIR_BOSS_TICKS"] // 20)],'),
    ('"gram": [int(c["GRAM_CHARGE_TICKS"] // 20), int(c["GRAM_THRUST_DAMAGE"]), int(c["GRAM_BIG_HEALTH"]), int(c["GRAM_COOLDOWN"] // 20)],',
     '"gram": [int(c["GRAM_HITS_PER_THRUST"]), round(c["GRAM_THRUST_BONUS"] * 100), int(c["GRAM_BIG_HEALTH"])],'),
    ('"mistletoe_dart": [int(c["DART_DAMAGE"]), int(c["DART_COOLDOWN"] // 20)],',
     '"mistletoe_dart": [int(c["DART_DAMAGE"])],'),
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
