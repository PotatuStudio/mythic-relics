"""Armas 3D que se sostienen exactamente como una espada vanilla.

La espada vanilla es un sprite plano en z 7.5..8.5 con la hoja en DIAGONAL: el mango en el píxel (2.5, 2.5) (desde abajo a
la izquierda) y la punta arriba a la derecha, sobre la recta x = y que pasa por el centro (8, 8). Sus posiciones de mano
salen de models/item/handheld.json. Si un modelo 3D pone su eje sobre esa misma diagonal y el agarre en ese mismo punto,
puede usar las mismas posiciones de mano y queda en la mano igual que una espada, en primera y en tercera persona.

Cómo se usa: se dibuja el arma VERTICAL, con el eje en x = z = 8 y el agarre en y = GRIP_Y; `diagonal()` la gira -45° en z
alrededor del centro (8, 8, 8), lo que lleva el eje a la diagonal y el agarre al punto del mango vanilla. Si el arma no
cabe en -16..32 (un báculo largo), `diagonal(..., half=True)` la dibuja a la mitad alrededor del centro y
`handheld_display(2)` dobla la escala: el resultado es idéntico y entra en los límites del formato."""

GRIP_Y = 8 - 5.5 * 2 ** 0.5   # agarre a 7.78 px del centro sobre la diagonal (el píxel 2.5, 2.5 del sprite)

_HANDHELD = {
    "thirdperson_righthand": {"rotation": [0, -90, 55], "translation": [0, 4.0, 0.5], "scale": 0.85},
    "thirdperson_lefthand": {"rotation": [0, 90, -55], "translation": [0, 4.0, 0.5], "scale": 0.85},
    "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [1.13, 3.2, 1.13], "scale": 0.68},
    "firstperson_lefthand": {"rotation": [0, 90, -25], "translation": [1.13, 3.2, 1.13], "scale": 0.68},
}


def handheld_display(scale=1.0):
    """Las posiciones de mano de models/item/handheld.json (vanilla), con la escala multiplicada por {scale}."""
    return {k: {"rotation": list(v["rotation"]), "translation": list(v["translation"]),
                "scale": [round(v["scale"] * scale, 4)] * 3} for k, v in _HANDHELD.items()}


def diagonal(elements, half=False):
    """Gira el arma vertical a la diagonal de la espada vanilla (y la achica a la mitad si {half})."""
    out = []
    for el in elements:
        assert "rotation" not in el, "las piezas no pueden venir giradas"
        el = dict(el)
        if half:
            el["from"] = [round(8 + (v - 8) / 2, 4) for v in el["from"]]
            el["to"] = [round(8 + (v - 8) / 2, 4) for v in el["to"]]
        el["rotation"] = {"origin": [8, 8, 8], "axis": "z", "angle": -45}
        out.append(el)
    return out
