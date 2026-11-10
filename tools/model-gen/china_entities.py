"""Borrador de las dos entidades de la tanda 1 china, con la misma geometría que tendrán en el juego (partes de
ModelPart con cubos y UV de caja de Minecraft, y en Minecraft la y crece hacia abajo y el frente es -z):

- Mono guerrero (Pelos del Rey Mono): Sun Wukong según Viaje al Oeste: cara peluda con hocico de «dios del trueno»,
  ojos de fuego dorados (brillan), gorro de alas de fénix púrpura y dorado con dos plumas largas de faisán, diadema de
  oro, cota de malla dorada, faldellín de piel de tigre, botas de nube y su bastón de hierro con aros de oro.
- Long (Perla del dragón): dragón chino sin alas, largo y serpentino en segmentos encadenados; cabeza de camello con
  cuernos de ciervo, bigotes largos, melena y barba de fuego, escamas de carpa azul verdosas, vientre de placas, garras
  de águila, cresta dorsal y la perla llameante delante del hocico (brilla).

Escribe un JSON con las partes y las texturas (normal y la que brilla) en base64.
Uso: python china_entities.py <salida.json> [<carpeta_png>]"""
import base64
import io
import json
import math
import os
import random
import sys

from PIL import Image

random.seed(7)


def C(h):
    return tuple(int(h[i:i + 2], 16) for i in (1, 3, 5))


def pick(cols):
    return random.choice(cols)


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def front_x(face, x, w):
    """Distancia (en px) desde el borde del frente (-z) en las caras laterales."""
    return w - 1 - x if face == "west" else x


# ---------------------------------------------------------------- pintores: (cara, x, y, w, h) -> (color|None, brillo|None)
def solid(*cols, glow=False):
    cols = [C(c) for c in cols]

    def f(face, x, y, w, h):
        c = pick(cols)
        return c, (c if glow else None)
    return f


FUR = [C("#b8782f"), C("#a96a26"), C("#c4863a"), C("#9a5e20")]
FUR_D = C("#6e4214")
SKIN = [C("#e8c09a"), C("#e2b68e"), C("#edc7a3")]
SKIN_D = C("#b98a62")
GOLD = [C("#f5c842"), C("#f0bd35"), C("#f8d055")]
GOLD_L, GOLD_D = C("#fff3a8"), C("#b0791a")
EYE = C("#ffd23a")


def fur(face, x, y, w, h):
    if face == "bottom":
        return FUR_D, None
    return (FUR_D if random.random() < 0.12 else pick(FUR)), None


def gold(face, x, y, w, h):
    if face == "top":
        return GOLD_L, None
    if face == "bottom" or y == h - 1:
        return GOLD_D, None
    return (GOLD_L if y == 0 else pick(GOLD)), None


def chainmail(face, x, y, w, h):
    """Cota de malla dorada (suozi huangjin jia): anillos en ajedrez, con la faja roja y la hebilla en la cintura."""
    if face in ("front", "back", "west", "east") and h >= 10 and y in (h - 3, h - 2):
        if face == "front" and w // 2 - 1 <= x <= w // 2:
            return GOLD_L, None
        return (C("#c42a20") if y == h - 3 else C("#8c1d16")), None
    if face == "bottom":
        return FUR_D, None
    if (x + y) % 2 == 0:
        return pick(GOLD), None
    return (C("#c9952a") if y % 2 else C("#e0aa30")), None


def tiger(face, x, y, w, h):
    """Faldellín de piel de tigre (hupi qun): naranja con rayas negras y el borde de abajo claro."""
    if face in ("top", "bottom"):
        return None, None
    if y == h - 1:
        return C("#f3e2c8"), None
    if math.sin(x * 1.3 + y * 0.6) > 0.72:
        return C("#1f1410"), None
    return pick([C("#e88a2a"), C("#f09a36"), C("#dc7c22")]), None


def boots(face, x, y, w, h):
    """Botas para andar sobre las nubes (ousi buyun lü): seda oscura con nubes blancas abajo; arriba, la pierna peluda."""
    if face == "bottom":
        return C("#1a1f33"), None
    if face == "top":
        return FUR_D, None
    if y < h - 5:
        return fur(face, x, y, w, h)
    if y == h - 5:
        return GOLD_D, None
    cloud = (y == h - 2 and x % 3 != 0) or (y == h - 3 and x % 3 == 1)
    return (C("#f2f4fa") if cloud else pick([C("#2a3150"), C("#252b47")])), None


def arm(face, x, y, w, h):
    """Brazo: hombro de oro, pelo y brazal de oro; la mano de piel."""
    if face == "top":
        return GOLD_L, None
    if face == "bottom":
        return SKIN_D, None
    if y < 3:
        return gold(face, x, y, w, 3)
    if h - 5 <= y < h - 2:
        return (GOLD_L if y == h - 5 else pick(GOLD)), None
    if y >= h - 2:
        return pick(SKIN), None
    return fur(face, x, y, w, h)


def monkey_head(face, x, y, w, h):
    """Cabeza: pelo dorado oscuro; la cara de piel en forma de corazón, cejas oscuras y los ojos de fuego dorados."""
    if face == "front":
        if y == 3 and x in (1, 2, 5, 6):
            g = EYE if x in (2, 5) else C("#ff8a1e")
            return g, g
        if y == 2 and 1 <= x <= 6:
            return FUR_D, None
        # cara en forma de corazón: frente y mejillas peludas, piel solo alrededor de los ojos y la boca
        face_mask = (y >= 3 and abs(x - 3.5) < 2.7 - max(0, y - 5) * 0.6) or (y == 2 and x in (3, 4))
        if face_mask:
            return pick(SKIN), None
        return fur(face, x, y, w, h)
    return fur(face, x, y, w, h)


def muzzle(face, x, y, w, h):
    if face == "front":
        if y == 0 and x in (1, 2):
            return C("#5a3a22"), None
        if y == h - 1:
            return C("#7a3a2a"), None
    return pick(SKIN), None


def crown(face, x, y, w, h):
    """Gorro de púrpura y oro con alas de fénix (fengchi zijin guan)."""
    if face == "top" or y == 0 or y == h - 1:
        return (GOLD_L if face == "top" and (x + y) % 3 == 0 else pick(GOLD)), None
    return pick([C("#7a3fa0"), C("#6a338e"), C("#8a4cb0")]), None


def feather(face, x, y, w, h):
    """Pluma de faisán (lingzi): franjas pardas y crema, punta oscura."""
    if y < 2:
        return C("#3a2414"), None
    return (C("#f0e2c0") if (y // 2) % 2 else C("#a2662e")), None


def staff(face, x, y, w, h):
    """Ruyi Jingu Bang: hierro negro con un aro de oro en cada punta."""
    if y < 3 or y >= h - 3:
        return gold(face, x, 1, w, 3)
    return pick([C("#34313c"), C("#3c3946"), C("#2b2932")]), None


# ---- dragón
SCALE = [C("#2f9ba0"), C("#2a8f96"), C("#35a8a8")]
SCALE_D = C("#175a6e")
SCALE_RIM = C("#8fe6dc")
BELLY = [C("#f2d98a"), C("#ecd07a")]
BELLY_D = C("#c9a24a")
FLAME = [C("#ff7a2a"), C("#ff5a1e"), C("#ff9a30")]


def scales(face, x, y, w, h):
    """Escamas de carpa: arcos con el borde claro; arriba más oscuro con la línea dorada del lomo; abajo, el vientre de
    placas (como de almeja) amarillo crema."""
    if face == "bottom":
        return (BELLY_D if y % 3 == 0 else pick(BELLY)), None
    if face == "top":
        if abs(x - (w - 1) / 2) < 0.6 or (w % 2 == 0 and x in (w // 2 - 1, w // 2)):
            return GOLD_D, None
        cell = (x + (y // 3) * 2) % 4
        return (SCALE_D if cell == 0 else mix(pick(SCALE), SCALE_D, 0.35)), None
    if face in ("front", "back"):
        return mix(pick(SCALE), SCALE_D, 0.2), None
    if y >= h - 2:
        return (BELLY_D if y == h - 1 else pick(BELLY)), None
    row = y // 3
    cx = (x + (row % 2) * 2) % 4
    if y % 3 == 2 and cx in (1, 2):
        return SCALE_RIM, None
    if y % 3 == 2 or cx == 0:
        return SCALE_D, None
    t = y / max(1, h)
    return mix(pick(SCALE), C("#5fd0c0"), t * 0.4), None


def dragon_head(face, x, y, w, h):
    """Cráneo: escamas; en los costados, cerca del frente, los ojos rojo dorado que brillan bajo la ceja."""
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y in (2, 3) and fx in (1, 2, 3):
            g = C("#ffd23a") if (y == 2 and fx == 2) else C("#ff4a1e")
            return g, g
        if y == 1 and fx in (0, 1, 2, 3, 4):
            return GOLD_D, None
    return scales(face, x, y, w, h)


def snout(face, x, y, w, h):
    """Hocico: escamas; la nariz con los orificios adelante y los dientes blancos en el borde de abajo."""
    if face == "front":
        if y == 1 and x in (1, 2, w - 3, w - 2):
            return C("#0e2a30"), None
        if y == h - 1:
            return (C("#fbf7ea") if x % 2 == 0 else C("#7a1a14")), None
        return mix(pick(SCALE), SCALE_RIM, 0.25), None
    if face in ("west", "east") and y == h - 1:
        return (C("#fbf7ea") if x % 2 == 0 else C("#7a1a14")), None
    return scales(face, x, y, w, h)


def jaw(face, x, y, w, h):
    if face == "top":
        return C("#a8303a"), None   # boca y lengua
    if face in ("west", "east", "front") and y == 0:
        return (C("#fbf7ea") if x % 2 == 0 else C("#7a1a14")), None
    if face == "bottom":
        return pick(BELLY), None
    return scales(face, x, y, w, h)


def antler(face, x, y, w, h):
    """Cuernos de ciervo: color hueso, con las puntas más claras."""
    if y == 0 and h > 2:
        return C("#fff6dc"), None
    return pick([C("#d8c49a"), C("#ccb588"), C("#e2cfa6")]), None


def whisker(face, x, y, w, h):
    return pick([C("#f5c842"), C("#f0bd35"), C("#ffe08a")]), None


def flame_plane(face, x, y, w, h, tooth=4, glow=False, axis="y"):
    """Plano dentado como de llamas (cresta, melena, barba, mechón de la cola): rojo abajo, naranja, punta amarilla."""
    if face in ("front", "back", "top", "bottom") and axis == "y":
        return None, None
    span, along = (h, x) if axis == "y" else (w, y)
    pos = y if axis == "y" else x
    k = along % tooth
    th = span * (1 - k / tooth)
    depth = span - pos  # alto desde la base
    if depth > th:
        return None, None
    t = depth / max(1, span)
    c = C("#ffe14a") if t > 0.75 else C("#ff9a30") if t > 0.4 else C("#e8452a")
    return c, (c if glow else None)


def crest(face, x, y, w, h):
    return flame_plane(face, x, y, w, h, tooth=4)


def mane(face, x, y, w, h):
    """Melena: mechones de llamas que salen hacia atrás, de largo variable y con huecos entre ellos."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    reach = w * (0.35 + 0.65 * ((y * 0.618) % 1))
    if fx > reach or (y % 3 == 2 and fx > w * 0.25):
        return None, None
    t = fx / max(1, reach)
    c = C("#ffe14a") if t > 0.8 else C("#ff9a30") if t > 0.45 else C("#e8452a")
    return c, None


def beard(face, x, y, w, h):
    return flame_plane(face, x, h - 1 - y, w, h, tooth=3)


def tail_tuft(face, x, y, w, h):
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        reach_y = abs(y - (h - 1) / 2) / (h / 2)
        if fx > w * (1 - reach_y * 0.8) or (fx + y) % 3 == 0 and fx > w * 0.6:
            return None, None
        t = fx / max(1, w)
        c = C("#ffe14a") if t > 0.7 else C("#ff9a30") if t > 0.35 else C("#e8452a")
        return c, None
    return None, None


def claws(face, x, y, w, h):
    return pick([C("#f2ead2"), C("#e2d6b6")]), None


def pearl(face, x, y, w, h):
    light = (x + y) / max(1, w + h - 2)
    c = mix(C("#ffffff"), C("#9fd8f0"), light)
    return c, c


def pearl_flame(face, x, y, w, h):
    """Llamas alrededor de la perla, en un plano cruzado (brilla)."""
    if face in ("front", "back"):
        cx, cy = (w - 1) / 2, (h - 1) / 2
    else:
        cx, cy = (w - 1) / 2, (h - 1) / 2
    d = math.hypot(x - cx, y - cy)
    ang = math.atan2(y - cy, x - cx)
    lim = 2.6 + 1.6 * max(0.0, math.cos(ang * 4 + 0.6)) + (1.0 if y < cy else 0)
    if d > lim or d < 1.6:
        return None, None
    c = C("#ffe14a") if d < 2.6 else C("#ff9a30") if d < 3.6 else C("#ff5a1e")
    return c, c


# ---------------------------------------------------------------- modelo
class Model:
    def __init__(self):
        self.parts = []

    def part(self, name, parent, offset, rot, cubes):
        self.parts.append({"name": name, "parent": parent, "offset": offset, "rot": rot, "cubes": cubes})
        return name


def cube(x, y, z, w, h, d, paint):
    return {"pos": [x, y, z], "size": [w, h, d], "paint": paint}


def faces(u, v, w, h, d):
    return {"top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d), "west": (u, v + d, d, h),
            "front": (u + d, v + d, w, h), "east": (u + d + w, v + d, d, h), "back": (u + 2 * d + w, v + d, w, h)}


def bake(model, tex_w):
    """Acomoda los cubos en la textura (estantes) y los pinta. Devuelve (partes con UV, PNG normal, PNG que brilla)."""
    items = [(p, c) for p in model.parts for c in p["cubes"]]
    order = sorted(items, key=lambda pc: -(pc[1]["size"][2] + pc[1]["size"][1]))
    x = y = shelf = 0
    for p, c in order:
        w, h, d = c["size"]
        fw, fh = 2 * (w + d), d + h
        if x + fw > tex_w:
            x, y, shelf = 0, y + shelf, 0
        c["uv"] = [x, y]
        x += fw
        shelf = max(shelf, fh)
    tex_h = 1
    while tex_h < y + shelf:
        tex_h *= 2
    img = Image.new("RGBA", (tex_w, tex_h), (0, 0, 0, 0))
    glow = Image.new("RGBA", (tex_w, tex_h), (0, 0, 0, 0))
    px, gx = img.load(), glow.load()
    for p, c in items:
        w, h, d = c["size"]
        for face, (fx, fy, fw, fh) in faces(c["uv"][0], c["uv"][1], w, h, d).items():
            for yy in range(fh):
                for xx in range(fw):
                    col, gl = c["paint"](face, xx, yy, fw, fh)
                    if col is not None:
                        px[fx + xx, fy + yy] = (*col, 255)
                    if gl is not None:
                        gx[fx + xx, fy + yy] = (*gl, 255)
    out = []
    for p in model.parts:
        out.append({"name": p["name"], "parent": p["parent"], "offset": p["offset"], "rot": p["rot"],
                    "cubes": [[*c["uv"], *c["pos"], *c["size"]] for c in p["cubes"]]})
    return out, img, glow, tex_w, tex_h


def monkey():
    m = Model()
    m.part("body", None, [0, 3, 0], [0.12, 0, 0], [cube(-4, 0, -2, 8, 11, 4, chainmail)])
    m.part("kilt", "body", [0, 9, 0], [0, 0, 0], [cube(-4.5, 0, -2.5, 9, 5, 5, tiger)])
    m.part("head", None, [0, 3, 0], [0, 0, 0], [
        cube(-4, -8, -4, 8, 8, 8, monkey_head),
        cube(-2, -3, -6, 4, 3, 2, muzzle),
        cube(-5, -5, -1, 1, 2, 2, solid("#e2b68e", "#d8a87e")),
        cube(4, -5, -1, 1, 2, 2, solid("#e2b68e", "#d8a87e")),
        # diadema de oro (jingu), apenas por fuera de la cabeza
        cube(-4.5, -6.5, -4.5, 9, 1, 1, gold), cube(-4.5, -6.5, 3.5, 9, 1, 1, gold),
        cube(-4.5, -6.5, -3.5, 1, 1, 7, gold), cube(3.5, -6.5, -3.5, 1, 1, 7, gold),
        # gorro de alas de fénix
        cube(-3, -10, -3, 6, 2, 6, crown), cube(-1, -12, -3.5, 2, 3, 1, gold),
    ])
    m.part("wing_r", "head", [-3, -9, 0], [0, 0, 0.5], [cube(-1, -3, -2, 1, 4, 4, crown)])
    m.part("wing_l", "head", [3, -9, 0], [0, 0, -0.5], [cube(0, -3, -2, 1, 4, 4, crown)])
    m.part("feather_r", "head", [-1.5, -10, -1], [-0.35, 0, -0.18], [cube(-0.5, -12, -0.5, 1, 12, 1, feather)])
    m.part("feather_r2", "feather_r", [0, -12, 0], [-0.9, 0, 0], [cube(-0.5, -10, -0.5, 1, 10, 1, feather)])
    m.part("feather_l", "head", [1.5, -10, -1], [-0.35, 0, 0.18], [cube(-0.5, -12, -0.5, 1, 12, 1, feather)])
    m.part("feather_l2", "feather_l", [0, -12, 0], [-0.9, 0, 0], [cube(-0.5, -10, -0.5, 1, 10, 1, feather)])
    m.part("arm_r", None, [-5.5, 4, 0], [0, 0, 0], [cube(-1.5, -1, -1.5, 3, 13, 3, arm), cube(-2.5, -2, -2, 4, 2, 4, gold)])
    m.part("arm_l", None, [5.5, 4, 0], [0, 0, 0], [cube(-1.5, -1, -1.5, 3, 13, 3, arm), cube(-1.5, -2, -2, 4, 2, 4, gold)])
    m.part("staff", "arm_r", [0, 11, -0.5], [0.25, 0, 0], [cube(-0.5, -22, -0.5, 1, 30, 1, staff)])
    m.part("leg_r", None, [-2, 14, 0], [0, 0, 0], [cube(-2, 0, -2, 4, 10, 4, boots)])
    m.part("leg_l", None, [2, 14, 0], [0, 0, 0], [cube(-2, 0, -2, 4, 10, 4, boots)])
    m.part("tail", "body", [0, 10, 2], [-0.6, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 7, fur)])
    m.part("tail2", "tail", [0, 0, 7], [-1.0, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 6, fur)])
    return m


SEGMENTS = [(8, 10), (10, 12), (11, 12), (10, 12), (9, 12), (8, 12), (7, 12), (6, 11), (4, 10), (3, 9)]


def dragon():
    m = Model()
    parent = None
    for i, (s, length) in enumerate(SEGMENTS):
        name = f"seg{i}"
        off = [0, 0, 0] if parent is None else [0, (SEGMENTS[i - 1][0] - s) / 2, SEGMENTS[i - 1][1]]
        cubes = [cube(-s / 2, -s / 2, 0, s, s, length, scales)]
        m.part(name, parent, off, [0, 0, 0], cubes)
        crest_h = max(2, s // 2)
        m.part(f"crest{i}", name, [0, -s / 2, 0], [0, 0, 0], [cube(0, -crest_h, 1, 0, crest_h, length - 2, crest)])
        parent = name
    # cola: mechón de llamas
    m.part("tuft", parent, [0, 0, SEGMENTS[-1][1]], [0, 0, 0],
           [cube(0, -5, 0, 0, 10, 12, tail_tuft)])
    # patas delanteras (seg1) y traseras (seg4)
    for seg, z in (("seg1", 5), ("seg4", 5)):
        s = SEGMENTS[int(seg[3:])][0]
        for side, sx in (("r", -1), ("l", 1)):
            up = m.part(f"{seg}_leg_{side}", seg, [sx * (s / 2), s / 2 - 2, z], [0.7, 0, sx * -0.35],
                        [cube(-1.5, 0, -1.5, 3, 6, 3, scales)])
            lo = m.part(f"{seg}_shin_{side}", up, [0, 6, 0], [-1.1, 0, 0],
                        [cube(-1, 0, -1, 2, 5, 2, scales),
                         cube(-0.01 if sx > 0 else 0.01, 0, 1, 0, 4, 3, crest)])
            m.part(f"{seg}_foot_{side}", lo, [0, 5, 0], [0.4, 0, 0],
                   [cube(-2, 0, -2.5, 4, 1, 3, scales),
                    cube(-2, 0, -4.5, 1, 1, 2, claws), cube(-0.5, 0, -5, 1, 1, 3, claws),
                    cube(1, 0, -4.5, 1, 1, 2, claws), cube(-2.5, -0.5, -0.5, 1, 1, 2, claws)])
    # cabeza
    m.part("head", "seg0", [0, -1, 0], [0.1, 0, 0], [
        cube(-5, -6, -10, 10, 8, 10, dragon_head),
        cube(-4, -3, -18, 8, 5, 8, snout),
        cube(-3, -4, -18, 6, 1, 4, scales),
        cube(-5.5, -7, -9, 3, 2, 5, scales), cube(2.5, -7, -9, 3, 2, 5, scales),  # cejas
    ])
    m.part("jaw", "head", [0, 2, -9], [0.3, 0, 0], [cube(-3.5, 0, -8.5, 7, 2, 8, jaw)])
    m.part("beard", "jaw", [0, 2, -2], [0, 0, 0], [cube(0, 0, -6, 0, 6, 8, beard)])
    for side, sx in (("r", -1), ("l", 1)):
        m.part(f"antler_{side}", "head", [sx * 3, -6, -4], [-0.55, 0, sx * -0.35], [
            cube(-0.5, -11, -0.5, 1, 11, 1, antler),
            cube(-0.5, -5, -3.5, 1, 1, 3, antler), cube(-0.5, -9, 0.5, 1, 1, 3, antler),
            cube(-0.5, -12, -2.5, 1, 1, 2, antler),
        ])
        w1 = m.part(f"whisker_{side}", "head", [sx * 4, -1, -16], [-0.25, sx * 0.9, 0],
                    [cube(-0.5, -0.5, 0, 1, 1, 9, whisker)])
        m.part(f"whisker2_{side}", w1, [0, 0, 9], [-0.7, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 9, whisker)])
        m.part(f"mane_{side}", "head", [sx * 5, -6, -5], [0.15, sx * 0.6, 0], [cube(0, 0, 0, 0, 10, 16, mane)])
    m.part("pearl", "head", [0, 6, -24], [0, 0, 0], [cube(-2, -2, -2, 4, 4, 4, pearl),
                                                     cube(0, -5, -5, 0, 10, 10, pearl_flame),
                                                     cube(-5, -5, 0, 10, 10, 0, pearl_flame)])
    return m


def b64(img):
    buf = io.BytesIO()
    img.save(buf, "PNG")
    return base64.b64encode(buf.getvalue()).decode()


if __name__ == "__main__":
    out = {}
    folder = sys.argv[2] if len(sys.argv) > 2 else None
    for name, make, tw in (("monkey", monkey, 64), ("dragon", dragon, 256)):
        parts, img, glow, w, h = bake(make(), tw)
        out[name] = {"parts": parts, "texW": w, "texH": h, "tex": b64(img), "glow": b64(glow)}
        if folder:
            img.resize((w * 4, h * 4), Image.NEAREST).save(os.path.join(folder, f"{name}_tex.png"))
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8"))
    print("ok", {k: (v["texW"], v["texH"]) for k, v in out.items()})
