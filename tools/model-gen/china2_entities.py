"""Borrador de las dos entidades de la tanda 2 china, con la misma geometría que tendrán en el juego (partes de
ModelPart con UV de caja de Minecraft; la y crece hacia abajo, el frente es -z y el suelo está en y = 24):

- Perro Celestial (Xiaotian Quan), el perro de Erlang Shen: lebrel negro, esbelto y veloz, de pecho hondo y cintura
  fina, orejas en punta, collar de oro con una campanita y ojos ámbar que brillan.
- Qilin: cabeza de dragón con barba y un solo cuerno, cuerpo de ciervo cubierto de escamas verde jade con borde de
  oro, cascos hendidos y cola de buey con mechón, llamas que no queman en las patas, la melena y el lomo (brillan) y
  una silla para el jinete.

Usa las piezas de china_entities.py. Cada modelo pinta con su propia semilla, así su textura no depende de los demás.
Uso: python china2_entities.py <salida.json> [<carpeta_png>]"""
import json
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import china_entities as CE  # noqa: E402
from china_entities import C, Model, cube, mix, pick, front_x  # noqa: E402

# ---------------------------------------------------------------- Perro Celestial
BLACK = [C("#1e1c22"), C("#24222a"), C("#191720"), C("#2a2730")]
BLACK_HI = C("#4a4656")
AMBER = C("#ffb830")


def black_fur(face, x, y, w, h):
    if face == "top":
        return (BLACK_HI if random.random() < 0.15 else pick(BLACK)), None
    if face == "bottom":
        return C("#121016"), None
    return (BLACK_HI if random.random() < 0.06 else pick(BLACK)), None


def dog_head(face, x, y, w, h):
    """Cráneo: ojos ámbar que brillan a los costados del frente, con una ceja clara."""
    if face == "front":
        if y == 2 and x in (0, w - 1):
            return AMBER, AMBER
        if y == 1 and x in (0, w - 1):
            return BLACK_HI, None
    return black_fur(face, x, y, w, h)


def dog_snout(face, x, y, w, h):
    if face == "front" and y == 0 and 0 < x < w - 1:
        return C("#0a090c"), None   # trufa
    if face in ("west", "east") and y == h - 1:
        return C("#3a1418"), None   # línea de la boca
    return black_fur(face, x, y, w, h)


def collar(face, x, y, w, h):
    if face == "front" and x == w // 2 and y == h - 1:
        return C("#fff3a8"), None
    return CE.gold(face, x, y, w, h)


def bell(face, x, y, w, h):
    return CE.gold(face, x, y, w, h)


def dog_leg(face, x, y, w, h):
    if y >= h - 1 and face not in ("top",):
        return C("#5a5560"), None   # uñas
    return black_fur(face, x, y, w, h)


def dog():
    m = Model()
    m.part("chest", None, [0, 13, -3], [0, 0, 0], [cube(-3, -4, -3, 6, 7, 6, black_fur)])
    m.part("waist", "chest", [0, -0.5, 3], [-0.08, 0, 0], [cube(-2, -3, 0, 4, 5, 7, black_fur)])
    m.part("collar", "chest", [0, -3, -3], [0, 0, 0], [cube(-3.5, -1, -0.5, 7, 2, 1, collar),
                                                        cube(-0.5, 1, -1, 1, 1, 1, bell)])
    head = m.part("head", None, [0, 10.5, -6], [0.1, 0, 0], [
        cube(-2.5, -3, -4, 5, 5, 5, dog_head),
        cube(-1.5, -1, -8, 3, 3, 4, dog_snout),
    ])
    m.part("ear_r", head, [-1.7, -3, -1], [-0.2, 0, -0.12], [cube(-0.5, -2, -0.5, 1, 2, 1, black_fur)])
    m.part("ear_l", head, [1.7, -3, -1], [-0.2, 0, 0.12], [cube(-0.5, -2, -0.5, 1, 2, 1, black_fur)])
    for name, x, z in (("leg_fr", -1.6, -5), ("leg_fl", 1.6, -5), ("leg_hr", -1.4, 5), ("leg_hl", 1.4, 5)):
        m.part(name, None, [x, 15, z], [0, 0, 0], [cube(-1, 0, -1, 2, 9, 2, dog_leg)])
    tail = m.part("tail", "waist", [0, -2, 6.5], [-0.9, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 6, black_fur)])
    m.part("tail_tip", tail, [0, 0, 6], [0.7, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 5, black_fur)])
    return m


# ---------------------------------------------------------------- Qilin
JADE = [C("#2e9a74"), C("#2a8c6a"), C("#35a87f")]
JADE_D = C("#17583f")
SCALE_GOLD = C("#f0c040")
CREAM = [C("#f4e6c0"), C("#ecdcae")]


def jade_scales(face, x, y, w, h):
    """Escamas verde jade con el borde de oro; vientre crema."""
    if face == "bottom":
        return (C("#c9b27a") if y % 3 == 0 else pick(CREAM)), None
    if face == "top":
        if abs(x - (w - 1) / 2) < 0.6 or (w % 2 == 0 and x in (w // 2 - 1, w // 2)):
            return SCALE_GOLD, None
    row = y // 3
    cx = (x + (row % 2) * 2) % 4
    if y % 3 == 2 and cx in (1, 2):
        return SCALE_GOLD, None
    if y % 3 == 2 or cx == 0:
        return JADE_D, None
    if face in ("west", "east") and y >= h - 2:
        return pick(CREAM), None
    return pick(JADE), None


def qilin_head(face, x, y, w, h):
    """Cabeza de dragón: escamas, ojos rojo dorado que brillan a los costados, cerca del frente."""
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 1 and fx in (1, 2):
            g = C("#ffd23a") if fx == 1 else C("#ff4a1e")
            return g, g
        if y == 0 and fx in (0, 1, 2, 3):
            return SCALE_GOLD, None
    return jade_scales(face, x, y, w, h)


def qilin_snout(face, x, y, w, h):
    if face == "front":
        if y == 0 and x in (0, w - 1):
            return C("#0e2a20"), None
        if y == h - 1:
            return (C("#fbf7ea") if x % 2 == 0 else C("#7a1a14")), None
        return mix(pick(JADE), C("#8fe6c0"), 0.25), None
    return jade_scales(face, x, y, w, h)


def horn(face, x, y, w, h):
    if y == 0:
        return C("#fff6dc"), None
    return (C("#f0c040") if (y // 2) % 2 else C("#e2cfa6")), None


def hoof_leg(face, x, y, w, h):
    """Pata: escamas arriba y casco hendido de bronce abajo (dos dedos)."""
    if y >= h - 2:
        if face == "front" and x == w // 2:
            return C("#2a1a10"), None
        return C("#6e4a24") if y == h - 2 else C("#4a3018"), None
    if face == "bottom":
        return C("#4a3018"), None
    return jade_scales(face, x, y, w, h)


def flame_tuft(face, x, y, w, h):
    """Llamas que no queman (brillan): plano dentado de oro a rojo."""
    return CE.flame_plane(face, x, y, w, h, tooth=3, glow=True)


def qilin_mane(face, x, y, w, h):
    """Melena: mechones de llamas que salen hacia atrás a lo largo del cuello (brillan)."""
    col, _ = CE.mane(face, x, y, w, h)
    return col, col


def saddle(face, x, y, w, h):
    if face == "top" and (x in (0, w - 1) or y in (0, h - 1)):
        return SCALE_GOLD, None
    if face in ("front", "back", "west", "east") and y == h - 1:
        return SCALE_GOLD, None
    return pick([C("#b8281e"), C("#a82418"), C("#c42a20")]), None


def beard(face, x, y, w, h):
    return CE.flame_plane(face, x, h - 1 - y, w, h, tooth=2, glow=True)


def tail_tuft(face, x, y, w, h):
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    spread = abs(y - (h - 1) / 2) / (h / 2)
    if fx > w * (1 - spread * 0.7):
        return None, None
    t = fx / max(1, w)
    c = C("#ffe14a") if t > 0.7 else C("#ff9a30") if t > 0.35 else C("#e8452a")
    return c, c


def qilin():
    m = Model()
    body = m.part("body", None, [0, 11, 2], [0, 0, 0], [
        cube(-4, -4, -10, 8, 8, 18, jade_scales),
        cube(-4.5, -5, -5, 9, 1, 7, saddle),
        cube(-5, -4, -3, 1, 4, 3, saddle), cube(4, -4, -3, 1, 4, 3, saddle),
    ])
    m.part("spine_flames", body, [0, -4, 2], [0, 0, 0], [cube(0, -3, 0, 0, 3, 6, flame_tuft)])
    neck = m.part("neck", None, [0, 9, -7], [0.55, 0, 0], [cube(-2.5, -10, -3, 5, 11, 5, jade_scales)])
    m.part("mane", neck, [0, -10, 1.5], [0, 0, 0], [cube(0, 0, 0, 0, 11, 5, qilin_mane)])
    head = m.part("head", neck, [0, -9.5, -1], [-0.55, 0, 0], [
        cube(-3, -5, -4, 6, 5, 6, qilin_head),
        cube(-2, -4, -9, 4, 3, 5, qilin_snout),
        cube(-3.5, -6, -2, 2, 2, 2, jade_scales), cube(1.5, -6, -2, 2, 2, 2, jade_scales),  # cejas y orejas
    ])
    m.part("jaw", head, [0, -1, -4], [0.12, 0, 0], [cube(-1.5, 0, -4.5, 3, 1, 5, jade_scales)])
    m.part("beard", head, [0, 0, -6], [0, 0, 0], [cube(0, 0, -2, 0, 4, 5, beard)])
    horn_base = m.part("horn", head, [0, -5, -1.5], [-0.45, 0, 0], [cube(-0.5, -5, -0.5, 1, 5, 1, horn),
                                                                   cube(-1, -1, -1, 2, 1, 2, horn)])
    m.part("horn_tip", horn_base, [0, -5, 0], [-0.4, 0, 0], [cube(-0.5, -3, -0.5, 1, 3, 1, horn)])
    for name, x, z in (("leg_fr", -2.5, -6), ("leg_fl", 2.5, -6), ("leg_hr", -2.5, 8), ("leg_hl", 2.5, 8)):
        leg = m.part(name, None, [x, 14, z], [0, 0, 0], [cube(-1.5, 0, -1.5, 3, 10, 3, hoof_leg)])
        # llamas en el menudillo, hacia atrás
        m.part(name + "_flame", leg, [0, 6, 1.5], [0, 0, 0], [cube(0, -2, 0, 0, 4, 3, flame_tuft)])
    tail = m.part("tail", body, [0, -3, 8], [-0.9, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 7, jade_scales)])
    m.part("tail_tuft", tail, [0, 0, 7], [0.5, 0, 0], [cube(0, -3, 0, 0, 6, 6, tail_tuft)])
    return m


# Escala con que se dibuja cada uno (el qilin, del tamaño de un caballo).
SCALE = {"dog": 1.0, "qilin": 1.35}
MODELS = {"dog": (dog, 64, 11), "qilin": (qilin, 128, 12)}


def bake_one(key):
    make, width, seed = MODELS[key]
    random.seed(seed)
    return CE.bake(make(), width)


if __name__ == "__main__":
    out = {}
    folder = sys.argv[2] if len(sys.argv) > 2 else None
    for key in MODELS:
        parts, img, glow, w, h = bake_one(key)
        out[key] = {"parts": parts, "texW": w, "texH": h, "tex": CE.b64(img), "glow": CE.b64(glow), "scale": SCALE[key]}
        if folder:
            from PIL import Image
            img.resize((w * 4, h * 4), Image.NEAREST).save(os.path.join(folder, f"{key}_tex.png"))
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8"))
    print("ok", {k: (v["texW"], v["texH"]) for k, v in out.items()})
