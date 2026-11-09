# Páginas de diseño

Cada tanda de reliquias se diseña con el usuario antes de programarla, en una página publicada como Artifact: fichas
(mito, fidelidad fiel / licencia / pop, efectos, espacio, dónde se encuentra) y un muñeco 3D con las reliquias puestas
y en la mano. Las decisiones se preguntan aparte, en el chat.

- `template.html`: la plantilla (three.js r128). Lleva el ítem en la mano igual que el juego: misma cadena de
  transformaciones que `LivingEntityRenderer` + `ItemInHandLayer` + el `display` de `item/handheld` o `item/generated`,
  con el ítem extruido 1 px. Las armas y bastones van con `heldIcon(id, brazo, "handheld")`.
- `draft_example.py`: borrador de íconos 16x16 y modelos puestos (escribe `draft.json`).
- `relics_example.py`: las fichas.
- `build_page_example.py`: reemplaza los textos de la plantilla y arma la página (`nordicos-tanda-2.html`).

Para una tanda nueva: copiar los tres ejemplos, cambiar textos, fichas, íconos y la lista `ORDER` de lo que se ve en
el muñeco. Antes de publicar, mirar el 3D desde todos los lados (frente, costados, atrás) en un navegador local
(`python -m http.server` + Playwright).
