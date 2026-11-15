"""Busca caras coplanares que se solapan entre reliquias del cuerpo que se pueden llevar a la vez (parpadeo)."""
import itertools
import json
import os

ASSETS = r"C:\dev\mods\mythic-relics\common\src\main\resources\assets\mythicrelics\models\item"
# Grupos de espacios que se pueden llevar a la vez, por parte del cuerpo (solo se comparan los de la misma parte:
# cada parte tiene sus propias coordenadas).
PARTS = {
    "cuerpo": {"collar": ["ankh", "bastet_amulet", "cerberus_collar", "wolf_bulla", "brisingamen", "yasakani_magatama"],
               "cinturón": ["golden_scarab", "megingjord", "five_color_stone"],
               "espalda": ["athena_aegis", "icarus_wings", "raijin_drum", "falcon_cloak", "freyr_sword"], "capa": ["golden_fleece", "nemean_lion_skin"]},
    "cabeza": {"cara": ["eye_of_horus", "jackal_mask", "erlang_eye", "tengu_mask"], "sombrero": ["ra_disk", "hades_helm", "kappa_dish"]},
    "brazo": {"anillo": ["jackal_ring", "draupnir", "andvaranaut", "pixiu_bracelet", "universe_ring"],
              "guantes": ["mummy_wraps", "sekhmet_claws", "jarngreipr"]},
}


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
for GROUPS in PARTS.values():
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
