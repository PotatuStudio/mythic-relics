"""Arma la página de la animación de la espada de Freyr con el ícono del borrador de nórdicos 3.
Uso: python norse3_draft.py norse3_draft.json && python freyr_build.py"""
import base64
import io
import json
import os

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
draft = json.load(open(os.path.join(HERE, "norse3_draft.json"), encoding="utf-8"))
# Primer cuadro de la tira animada del ícono.
img = Image.open(io.BytesIO(base64.b64decode(draft["icons"]["freyr_sword"]["png"]))).crop((0, 0, 16, 16))
buf = io.BytesIO(); img.save(buf, "PNG")
tpl = open(os.path.join(HERE, "freyr_template.html"), encoding="utf-8").read()
out = tpl.replace("__SWORD__", base64.b64encode(buf.getvalue()).decode())
open(os.path.join(HERE, "nordicos-espada-freyr.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
