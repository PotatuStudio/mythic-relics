"""Busca caras coplanares que se solapan entre reliquias del cuerpo que se pueden llevar a la vez (parpadeo)."""
import itertools
import json
import os

ASSETS = r"C:\dev\mods\mythic-relics\common\src\main\resources\assets\mythicrelics\models\item"
GROUPS = {"collar": ["ankh", "bastet_amulet", "cerberus_collar", "wolf_bulla", "brisingamen"], "cinturón": ["golden_scarab", "megingjord"],
          "espalda": ["athena_aegis", "icarus_wings"], "capa": ["golden_fleece"]}


def els(name):
    return json.load(open(os.path.join(ASSETS, name + "_worn.json")))["elements"]


def faces(e):
    f, t = e["from"], e["to"]
    for axis in range(3):
        others = [a for a in range(3) if a != axis]
        rect = (f[others[0]], t[others[0]], f[others[1]], t[others[1]])
        yield axis, f[axis], rect
        yield axis, t[axis], rect


def overlap(r1, r2):
    return min(r1[1], r2[1]) - max(r1[0], r2[0]) > 1e-6 and min(r1[3], r2[3]) - max(r1[2], r2[2]) > 1e-6


bad = 0
for g1, g2 in itertools.combinations(GROUPS, 2):
    for a in GROUPS[g1]:
        for b in GROUPS[g2]:
            for ea in els(a):
                for eb in els(b):
                    for ax1, c1, r1 in faces(ea):
                        for ax2, c2, r2 in faces(eb):
                            if ax1 == ax2 and abs(c1 - c2) < 1e-6 and overlap(r1, r2):
                                bad += 1
                                print(f"{a}:{ea['name']} / {b}:{eb['name']} eje {'xyz'[ax1]} = {c1}")
print("coplanares:", bad)
