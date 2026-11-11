"""Traducciones de los arreglos tras la primera prueba en NeoForge (2026-11-10): etiqueta de mitología, aviso y estado
de los sets, recarga en el tooltip, escarabajo que salva del golpe mortal, nueva cabeza de Medusa y su estatua, hinojo
que funde el stack entero. Uso: python feedback1_lang.py"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    "culture.mythicrelics.egypt": "Mitología egipcia",
    "culture.mythicrelics.greece": "Mitología griega",
    "culture.mythicrelics.rome": "Mitología romana",
    "culture.mythicrelics.norse": "Mitología nórdica",
    "culture.mythicrelics.china": "Mitología china",
    "culture.mythicrelics.japan": "Mitología japonesa",
    "set.mythicrelics.anubis_set": "Set de Anubis",
    "set.mythicrelics.osiris_set": "Insignias de Osiris",
    "set.mythicrelics.perseus_set": "Set de Perseo",
    "set.mythicrelics.thor_set": "Set de Thor",
    "set.mythicrelics.erlang_set": "Set de Erlang Shen",
    "set.mythicrelics.regalia_set": "Los Tres Tesoros",
    "set.mythicrelics.fujin_raijin_set": "Set de Fūjin y Raijin",
    "message.mythicrelics.set_on": "%s: activo",
    "message.mythicrelics.set_off": "%s: inactivo",
    "tooltip.mythicrelics.set_active": "✔ %s activo",
    "tooltip.mythicrelics.set_inactive": "✘ %s incompleto",
    "tooltip.mythicrelics.cooldown": "⌛ Recargando: %s %%",
    "item.mythicrelics.golden_scarab.tooltip.1": "Al bajar de %1$s corazones o ante un golpe mortal, Jepri te hace renacer",
    "item.mythicrelics.medusa_head.tooltip.1": "Clic derecho: petrifica %1$s s a los hostiles que tienes delante",
    "item.mythicrelics.medusa_head.tooltip.2": "hasta %2$s bloques (no a los jefes). Si mueren así se derrumban: doble botín y a veces su estatua",
    "item.mythicrelics.prometheus_fennel.tooltip.2": "Clic derecho: cocina o funde al instante el stack entero de la otra mano",
    "item.mythicrelics.prometheus_fennel.tooltip.3": "Recarga: %2$s s por objeto (mínimo %3$s s)",
    "item.mythicrelics.stone_statue": "Estatua de piedra",
    "item.mythicrelics.stone_statue.of": "Estatua de %s",
    "item.mythicrelics.stone_statue.tooltip": "Lo que dejó la mirada de Medusa. Colócala como adorno; un golpe la recoge",
    "entity.mythicrelics.stone_statue": "Estatua de piedra",
}

EN = {
    "culture.mythicrelics.egypt": "Egyptian mythology",
    "culture.mythicrelics.greece": "Greek mythology",
    "culture.mythicrelics.rome": "Roman mythology",
    "culture.mythicrelics.norse": "Norse mythology",
    "culture.mythicrelics.china": "Chinese mythology",
    "culture.mythicrelics.japan": "Japanese mythology",
    "set.mythicrelics.anubis_set": "Anubis set",
    "set.mythicrelics.osiris_set": "Regalia of Osiris",
    "set.mythicrelics.perseus_set": "Perseus set",
    "set.mythicrelics.thor_set": "Thor set",
    "set.mythicrelics.erlang_set": "Erlang Shen set",
    "set.mythicrelics.regalia_set": "The Three Treasures",
    "set.mythicrelics.fujin_raijin_set": "Fūjin and Raijin set",
    "message.mythicrelics.set_on": "%s: active",
    "message.mythicrelics.set_off": "%s: inactive",
    "tooltip.mythicrelics.set_active": "✔ %s active",
    "tooltip.mythicrelics.set_inactive": "✘ %s incomplete",
    "tooltip.mythicrelics.cooldown": "⌛ Recharging: %s %%",
    "item.mythicrelics.golden_scarab.tooltip.1": "Below %1$s hearts or on a lethal hit, Khepri brings you back",
    "item.mythicrelics.medusa_head.tooltip.1": "Right-click: petrify for %1$s s the hostiles in front of you",
    "item.mythicrelics.medusa_head.tooltip.2": "up to %2$s blocks (not bosses). If they die that way they crumble: double loot and sometimes their statue",
    "item.mythicrelics.prometheus_fennel.tooltip.2": "Right-click: instantly cook or smelt the whole stack in your other hand",
    "item.mythicrelics.prometheus_fennel.tooltip.3": "Cooldown: %2$s s per item (at least %3$s s)",
    "item.mythicrelics.stone_statue": "Stone Statue",
    "item.mythicrelics.stone_statue.of": "Statue of %s",
    "item.mythicrelics.stone_statue.tooltip": "What Medusa's gaze left behind. Place it as decoration; one hit picks it up",
    "entity.mythicrelics.stone_statue": "Stone Statue",
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
