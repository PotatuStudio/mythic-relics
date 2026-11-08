"""Utilidades compartidas para generar texturas (copiadas de Misfit Relics: color hex y escritor de PNG sin dependencias)."""
import struct
import zlib


def H(s):
    return tuple(int(s[i:i + 2], 16) for i in (1, 3, 5)) + (255,)


T = (0, 0, 0, 0)

def png(path, img):
    raw = b"".join(b"\x00" + b"".join(bytes(c) for c in row) for row in img)

    def chunk(tag, data):
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xffffffff)

    with open(path, "wb") as f:
        f.write(b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", len(img[0]), len(img), 8, 6, 0, 0, 0))
                + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b""))


def blank():
    return [[T] * 16 for _ in range(16)]
