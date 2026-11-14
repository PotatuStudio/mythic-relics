"""Modelos 3D de las armas que vuelan (en el aire ya no se dibuja el ícono del inventario, como el tridente vanilla):

- Espada de Freyr: hoja ancha de acero claro con el canal central dorado que brilla (la espada que pelea sola "brilla
  como el sol"), guarda de oro con las puntas levantadas y una gema ámbar, empuñadura de cuero con hilo de oro y pomo
  de oro.
- Mjölnir: cabeza de hierro oscuro con bisel claro y la franja de oro a lo largo (como en el ícono), mango corto (el
  defecto que le dejó Loki al picar a Brokkr) de madera con cuero en espiral y el anillo de oro del pomo.
- Gungnir: asta larga de fresno, collar de oro, punta de hoja de acero con las runas talladas en la punta (Sigrdrífumál)
  que brillan en celeste, y regatón de oro.
- Dardo de muérdago: ramita verde con pares de hojas alargadas y bayas blancas en las horquillas, la punta afilada y la
  base de madera atada.

Convención (ModelPart): la y crece hacia abajo, así que la punta del arma mira a -y; el origen queda cerca del centro
de la pieza para poder girarla en el aire. Usa las piezas de china_entities.py.
Uso: python weapon_entities.py <salida.json>
"""
import json
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import china_entities as CE  # noqa: E402
from china_entities import C, Model, cube, pick  # noqa: E402

GOLD = [C("#f5c842"), C("#ffd85a")]
GOLD_L = C("#fff3a8")
GOLD_D = C("#c98f1c")
GOLD_DD = C("#7d520c")
WOOD = [C("#a2662e"), C("#94592a")]
WOOD_D = C("#6e3f17")
LEATHER = C("#3f220b")
STEEL = [C("#dcdcdc"), C("#e8e8ec")]
STEEL_D = C("#a8a8b0")
STEEL_DD = C("#62626c")


def gold(face, x, y, w, h):
    if face == "top":
        return GOLD_L, None
    if face == "bottom":
        return GOLD_DD, None
    return (GOLD_D if random.random() < 0.15 else pick(GOLD)), None


# ---------------------------------------------------------------- Espada de Freyr
def freyr_blade(face, x, y, w, h):
    """Hoja: filos de acero blanco, canal central dorado que brilla y una línea gris que separa el filo del canal."""
    if face in ("front", "back"):
        if x == w // 2:
            return GOLD_L, GOLD_L
        return (C("#ffffff") if x in (0, w - 1) else C("#fff2c2")), None
    if face in ("west", "east"):
        return STEEL_D, None
    return C("#ffffff"), None


def freyr_tip(face, x, y, w, h):
    return (GOLD_L, GOLD_L) if face in ("front", "back") and x == w // 2 else (C("#ffffff"), None)


def freyr_guard(face, x, y, w, h):
    """Guarda de oro con una gema ámbar al centro, por delante y por detrás."""
    if face in ("front", "back") and x in (w // 2 - 1, w // 2):
        return C("#ff9f2a"), C("#ff9f2a")
    return gold(face, x, y, w, h)


def grip(face, x, y, w, h):
    """Cuero oscuro con vueltas de hilo de oro."""
    if face in ("top", "bottom"):
        return LEATHER, None
    return (GOLD_D if y % 2 == 0 else LEATHER), None


def freyr_sword():
    m = Model()
    oy = 6      # baja todo para que el origen quede a media hoja
    s = m.part("sword", None, [0, 0, 0], [0, 0, 0], [
        cube(-1.5, -19 + oy, -0.5, 3, 17, 1, freyr_blade),
        cube(-1, -21 + oy, -0.5, 2, 2, 1, freyr_tip),
        cube(-0.5, -22 + oy, -0.5, 1, 1, 1, freyr_tip),
        cube(-4, -2 + oy, -1, 8, 2, 2, freyr_guard),
        cube(-5, -4 + oy, -1, 1, 3, 2, gold),
        cube(4, -4 + oy, -1, 1, 3, 2, gold),
        cube(-1, 0 + oy, -1, 2, 5, 2, grip),
        cube(-1.5, 5 + oy, -1.5, 3, 2, 3, freyr_guard),
    ])
    return m


# ---------------------------------------------------------------- Mjölnir
IRON = [C("#34313c"), C("#3a3742"), C("#2e2b35")]


def mjolnir_head(face, x, y, w, h):
    """Hierro oscuro con bisel claro en los bordes y la franja de oro a lo largo de la cabeza (con un nudo grabado)."""
    if face in ("west", "east"):                      # las caras de golpe
        edge = x in (0, w - 1) or y in (0, h - 1)
        return (C("#55525f") if edge else C("#8a8796")), None
    if y == h // 2:
        return (GOLD_D if x % 3 == 0 else pick(GOLD)), None
    if y in (0, h - 1) or x in (0, w - 1):
        return C("#55525f"), None
    return pick(IRON), None


def handle(face, x, y, w, h):
    """Madera con una tira de cuero en espiral."""
    if face in ("top", "bottom"):
        return WOOD_D, None
    return (LEATHER if (y + x) % 3 == 0 else WOOD[0]), None


def mjolnir():
    m = Model()
    m.part("hammer", None, [0, 0, 0], [0, 0, 0], [
        cube(-4.5, -9, -2.5, 9, 5, 5, mjolnir_head),
        cube(-1, -4, -1, 2, 9, 2, handle),
        cube(-1.5, 5, -1.5, 3, 2, 3, gold),
    ])
    return m


# ---------------------------------------------------------------- Gungnir
RUNE = C("#7ff7ff")


def shaft(face, x, y, w, h):
    """Asta de fresno: vetas a lo largo."""
    if face in ("top", "bottom"):
        return WOOD_D, None
    return (C("#d39a5c") if y % 5 == 0 else pick(WOOD)), None


def spear_head(face, x, y, w, h):
    """Hoja de acero con las runas al centro (brillan) y los filos más claros."""
    if face in ("front", "back"):
        if x == w // 2 and 1 <= y <= h - 2 and y % 2 == 1:
            return RUNE, RUNE
        if x == w // 2:
            return STEEL_D, None
        return (C("#ffffff") if x in (0, w - 1) else pick(STEEL)), None
    return STEEL_D, None


def spear_tip(face, x, y, w, h):
    return (C("#ffffff") if face in ("front", "back") else STEEL_D), None


def gungnir():
    m = Model()
    m.part("spear", None, [0, 0, 0], [0, 0, 0], [
        cube(-0.5, -8, -0.5, 1, 24, 1, shaft),
        cube(-1, -10, -1, 2, 2, 2, gold),
        cube(-1.5, -16, -0.5, 3, 6, 1, spear_head),
        cube(-2.5, -14, -0.5, 1, 3, 1, spear_tip),
        cube(1.5, -14, -0.5, 1, 3, 1, spear_tip),
        cube(-1, -20, -0.5, 2, 4, 1, spear_head),
        cube(-0.5, -22, -0.5, 1, 2, 1, spear_tip),
        cube(-1, 16, -1, 2, 2, 2, gold),
    ])
    return m


# ---------------------------------------------------------------- Dardo de muérdago
GREEN = [C("#5fa13a"), C("#4f8f30")]
LEAF = [C("#b6e27a"), C("#9fd06a")]
LEAF_D = C("#2f6420")


def stem(face, x, y, w, h):
    return (LEAF_D if y % 4 == 3 else pick(GREEN)), None


def leaf(face, x, y, w, h):
    """Hoja alargada de muérdago: clara arriba, con el nervio oscuro."""
    if face == "bottom":
        return C("#5fa13a"), None
    return (LEAF_D if face == "top" and y == h // 2 else pick(LEAF)), None


def berry(face, x, y, w, h):
    return (C("#ffffff") if face != "bottom" else C("#6f93bd")), None


def dart_point(face, x, y, w, h):
    return C("#d8f0b0"), None


def binding(face, x, y, w, h):
    return (C("#a2662e") if y % 2 else LEATHER), None


def mistletoe_dart():
    m = Model()
    d = m.part("dart", None, [0, 0, 0], [0, 0, 0], [
        cube(-0.5, -8, -0.5, 1, 15, 1, stem),
        cube(-0.5, -11, -0.5, 1, 3, 1, dart_point),
        cube(-1, 5, -1, 2, 3, 2, binding),
    ])
    # Pares de hojas en las horquillas (como la planta: hojas opuestas, de a dos) y bayas blancas entre ellas.
    for i, y in enumerate((-1, 3)):
        m.part(f"leaf_r{i}", d, [-0.5, y, 0], [0, 0, -0.9], [cube(-3, -0.5, -1, 3, 1, 2, leaf)])
        m.part(f"leaf_l{i}", d, [0.5, y, 0], [0, 0, 0.9], [cube(0, -0.5, -1, 3, 1, 2, leaf)])
    m.part("berries", d, [0, 0, 0], [0, 0, 0], [cube(-1, -2, -1, 1, 1, 1, berry), cube(0, 2, 0, 1, 1, 1, berry)])
    return m


MODELS = {"freyr_sword": (freyr_sword, 32, 41), "mjolnir": (mjolnir, 64, 42), "gungnir": (gungnir, 32, 43),
          "mistletoe_dart": (mistletoe_dart, 32, 44)}
# Tamaño en el aire (multiplica la geometría; 16 px = 1 bloque).
SCALE = {"freyr_sword": 1.0, "mjolnir": 1.0, "gungnir": 1.0, "mistletoe_dart": 1.0}


def bake_one(key):
    make, width, seed = MODELS[key]
    random.seed(seed)
    return CE.bake(make(), width)


if __name__ == "__main__":
    out = {}
    for key in MODELS:
        parts, img, glow, w, h = bake_one(key)
        out[key] = {"parts": parts, "texW": w, "texH": h, "tex": CE.b64(img), "glow": CE.b64(glow), "scale": SCALE[key]}
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8"))
    print("ok", {k: (v["texW"], v["texH"]) for k, v in out.items()})
