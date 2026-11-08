"""Genera el libro de reliquias (Modonomicon) y la wiki del repo desde los mismos datos que usan el mod, el tooltip y el
visor (moddata.py), para que nunca se contradigan.

Escribe:
  - common/src/main/resources/data/mythicrelics/modonomicon/books/relics/  (book.json, categoría y entradas)
  - las claves book.mythicrelics.relics.<reliquia>.* con los números ya calculados en los lang (reemplaza las anteriores)
  - docs/wiki/README.md, docs/wiki/egipto.md y docs/wiki/img/*.png (íconos, primer cuadro, x4; requiere Pillow)

Los textos del mito (lore.mythicrelics.*) y los títulos fijos del libro se escriben a mano en los lang.
Correr de nuevo cada vez que cambien los números, el botín, los tooltips o el mito:
    python make_codex.py
"""
import json
import os
import shutil
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import (ASSETS, LOOT_TABLE_KEYS, ORDER, ROOT, SET_PIECES, SLOTS, constants, lang, loot, pct,  # noqa: E402
                     rarity, tooltip_args, tooltip_lines)

BOOK = "relics"
NS = "mythicrelics"
BOOK_DIR = os.path.join(ROOT, "common", "src", "main", "resources", "data", NS, "modonomicon", "books", BOOK)
WIKI = os.path.join(ROOT, "docs", "wiki")
GENERATED_PREFIX = f"book.{NS}.{BOOK}.entry."
CHESTS = {"desert_pyramid": 4}   # cofres por estructura, para la probabilidad por estructura

# Posiciones en el mapa de nodos de la categoría (en celdas).
POSITIONS = {
    "intro": (0, 0),
    "golden_scarab": (-4, 2), "eye_of_horus": (-2, 2), "ankh": (2, 2), "mummy_wraps": (4, 2),
    "jackal_mask": (-2, 5), "anubis_scepter": (0, 5), "jackal_ring": (2, 5),
    "anubis_set": (0, 7),
}
SLOT_NAMES = {
    "es_es": {"legs/belt": "cinturón", "head/face": "cara", "chest/necklace": "collar", "hand/glove": "guantes",
              "hand/ring": "anillo", "mainhand": "mano (arma)"},
    "en_us": {"legs/belt": "belt", "head/face": "face", "chest/necklace": "necklace", "hand/glove": "gloves",
              "hand/ring": "ring", "mainhand": "main hand (weapon)"},
}
WORDS = {
    "es_es": {"per_chest": "{p} % por cofre", "per_structure": "≈ {p} % por pirámide", "slot": "Espacio",
              "set": "Set de Anubis"},
    "en_us": {"per_chest": "{p}% per chest", "per_structure": "≈ {p}% per pyramid", "slot": "Slot",
              "set": "Anubis set"},
}
BREAK = "\\\n"   # salto de línea duro en el markdown de Modonomicon


def sentences(lines):
    """Une las líneas que el tooltip corta por ancho (la siguiente empieza en minúscula) en una sola frase."""
    out = []
    for line in lines:
        if out and line[:1].islower():
            out[-1] = out[-1] + " " + line
        else:
            out.append(line)
    return out


def where_lines(code, lang_map, sources):
    w = WORDS[code]
    out = []
    for s in sources:
        place = lang_map[LOOT_TABLE_KEYS[s["table"]]]
        text = f"**{place[0].upper() + place[1:]}**: {w['per_chest'].format(p=pct(s['chance']))}"
        chests = CHESTS.get(s["table"])
        if chests:
            text += f" ({w['per_structure'].format(p=pct(1 - (1 - s['chance']) ** chests))})"
        out.append(text)
    return out


def generated_keys(langs, args, sources):
    """Claves del libro con los números ya puestos: efecto, descripción corta y dónde se encuentra."""
    keys = {code: {} for code in langs}
    for code, m in langs.items():
        for item in ORDER:
            lines = sentences(tooltip_lines(m, item, args[item]))
            effect = lines[:]
            if item in SET_PIECES:
                effect.append("")
                effect.append(f"*{m['tooltip.mythicrelics.anubis_set.1']}*")
                effect.append(m["tooltip.mythicrelics.anubis_set.2"].strip() + " " + m["tooltip.mythicrelics.anubis_set.3"].strip())
            effect.append("")
            effect.append(f"{WORDS[code]['slot']}: {SLOT_NAMES[code][SLOTS[item]]}")
            keys[code][f"{GENERATED_PREFIX}{item}.effect"] = BREAK.join(effect)
            keys[code][f"{GENERATED_PREFIX}{item}.description"] = lines[0]
            keys[code][f"{GENERATED_PREFIX}{item}.where"] = BREAK.join(where_lines(code, m, sources.get(item, [])))
        set_lines = [m["tooltip.mythicrelics.anubis_set.1"], m["tooltip.mythicrelics.anubis_set.2"].strip() + " "
                     + m["tooltip.mythicrelics.anubis_set.3"].strip()]
        keys[code][f"{GENERATED_PREFIX}anubis_set.effect"] = BREAK.join(set_lines)
    return keys


def text_page(page_id, title, text):
    return {"type": "modonomicon:text", "id": page_id, "title": title, "text": text, "show_title_separator": True}


def entry(entry_id, name, description, icon, pos, pages, parents=()):
    e = {"type": "modonomicon:content", "id": f"{NS}:egypt/{entry_id}", "category": f"{NS}:egypt",
         "name": name, "icon": icon, "x": pos[0], "y": pos[1], "pages": pages}
    if description:
        e["description"] = description
    if parents:
        e["parents"] = [{"entry": f"{NS}:egypt/{p}"} for p in parents]
    return e


def write_json(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write("\n")


def write_book():
    if os.path.isdir(BOOK_DIR):
        shutil.rmtree(BOOK_DIR)   # solo la carpeta generada del libro
    prefix = f"book.{NS}.{BOOK}"
    write_json(os.path.join(BOOK_DIR, "book.json"), {
        "name": f"{prefix}.name",
        "tooltip": f"{prefix}.tooltip",
        "display_mode": "node",
        "generate_book_item": True,
        "model": "modonomicon:modonomicon_blue",
        "creative_tab": f"{NS}:main",
    })
    write_json(os.path.join(BOOK_DIR, "categories", "egypt.json"), {
        "name": f"{prefix}.egypt.name",
        "icon": f"{NS}:ankh",
        "sort_number": 0,
    })
    entries = {"intro": entry("intro", f"{prefix}.egypt.intro.name", None, "minecraft:chiseled_sandstone",
                              POSITIONS["intro"], [text_page("intro", f"{prefix}.egypt.intro.name",
                                                             f"lore.{NS}.egypt.intro")])}
    for item in ORDER:
        g = f"{GENERATED_PREFIX}{item}"
        pages = [
            {"type": "modonomicon:spotlight", "id": "relic", "title": "", "text": f"{g}.effect", "item": f"{NS}:{item}"},
            text_page("myth", f"{prefix}.myth", f"lore.{NS}.{item}"),
            text_page("where", f"{prefix}.where", f"{g}.where"),
        ]
        entries[item] = entry(item, f"item.{NS}.{item}", f"{g}.description", f"{NS}:{item}", POSITIONS[item], pages,
                              parents=("intro",))
    entries["anubis_set"] = entry("anubis_set", f"{prefix}.anubis_set.name", None, f"{NS}:jackal_mask",
                                  POSITIONS["anubis_set"], [
                                      text_page("set", f"{prefix}.anubis_set.name", f"{GENERATED_PREFIX}anubis_set.effect"),
                                      text_page("myth", f"{prefix}.myth", f"lore.{NS}.anubis_set"),
                                  ], parents=tuple(sorted(SET_PIECES)))
    for name, e in entries.items():
        write_json(os.path.join(BOOK_DIR, "entries", "egypt", name + ".json"), e)
    return len(entries)


def update_lang(keys):
    for code, add in keys.items():
        path = os.path.join(ASSETS, "lang", code + ".json")
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        data = {k: v for k, v in data.items() if not k.startswith(GENERATED_PREFIX)}
        data.update(add)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            json.dump(dict(sorted(data.items())), f, indent=2, ensure_ascii=False)
            f.write("\n")


def plain(text):
    return text.replace(BREAK, "  \n")   # salto duro en Markdown de GitHub


def write_wiki(langs, args, sources, rar):
    from PIL import Image
    img_dir = os.path.join(WIKI, "img")
    os.makedirs(img_dir, exist_ok=True)
    for item in ORDER:
        icon = Image.open(os.path.join(ASSETS, "textures", "item", item + ".png")).crop((0, 0, 16, 16))
        icon.resize((64, 64), Image.NEAREST).save(os.path.join(img_dir, item + ".png"))
    es = langs["es_es"]
    rarities = {"common": "común", "uncommon": "poco común", "rare": "rara", "epic": "épica"}
    out = ["# Egipto", "",
           "<!-- Generado por tools/model-gen/make_codex.py desde los datos del mod. No editar a mano. -->", "",
           plain(es[f"lore.{NS}.egypt.intro"]), "",
           "| | Reliquia | Espacio | Rareza | Dónde |", "|---|---|---|---|---|"]
    for item in ORDER:
        places = "; ".join(plain(x).replace("**", "") for x in where_lines("es_es", es, sources.get(item, [])))
        out.append(f"| ![](img/{item}.png) | [{es[f'item.{NS}.{item}']}](#{item.replace('_', '-')}) | "
                   f"{SLOT_NAMES['es_es'][SLOTS[item]]} | {rarities[rar.get(item, 'common')]} | {places} |")
    for item in ORDER:
        out += ["", f"<a id=\"{item.replace('_', '-')}\"></a>", f"## {es[f'item.{NS}.{item}']}", "",
                f"<img src=\"img/{item}.png\" width=\"64\" alt=\"\">", "",
                f"**Espacio:** {SLOT_NAMES['es_es'][SLOTS[item]]} · **Rareza:** {rarities[rar.get(item, 'common')]}", ""]
        out += [f"- {line}" for line in sentences(tooltip_lines(es, item, args[item]))]
        if item in SET_PIECES:
            out += ["", f"*{es['tooltip.mythicrelics.anubis_set.1']}* {es['tooltip.mythicrelics.anubis_set.2'].strip()} "
                        f"{es['tooltip.mythicrelics.anubis_set.3'].strip()}"]
        out += ["", "**Mito.** " + plain(es[f"lore.{NS}.{item}"]), "",
                "**Dónde encontrarla.** " + "; ".join(plain(x) for x in where_lines("es_es", es, sources.get(item, [])))]
    out += ["", "## Set de Anubis", "",
            f"{es['tooltip.mythicrelics.anubis_set.1']} {es['tooltip.mythicrelics.anubis_set.2'].strip()} "
            f"{es['tooltip.mythicrelics.anubis_set.3'].strip()}", "", "**Mito.** " + plain(es[f"lore.{NS}.anubis_set"]), ""]
    with open(os.path.join(WIKI, "egipto.md"), "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(out))
    index = ["# Wiki de Mythic Relics", "",
             "<!-- Generado por tools/model-gen/make_codex.py desde los datos del mod. No editar a mano. -->", "",
             "Reliquias de la mitología convertidas en equipo RPG. No se fabrican: se encuentran en estructuras que "
             "calzan con su cultura, y mientras más poderosas, más raras.", "",
             "En el juego, todo esto está en el **Códice de reliquias** (requiere Modonomicon). Con JEI instalado, cada "
             "reliquia muestra además dónde encontrarla.", "",
             "## Culturas", "", f"- [Egipto](egipto.md): {len(ORDER)} reliquias, incluido el set de Anubis.", ""]
    with open(os.path.join(WIKI, "README.md"), "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(index))


if __name__ == "__main__":
    langs = lang()
    args = tooltip_args(constants())
    sources = loot()
    update_lang(generated_keys(langs, args, sources))
    langs = lang()
    n = write_book()
    write_wiki(langs, args, sources, rarity())
    print(f"libro: {n} entradas en {os.path.relpath(BOOK_DIR, ROOT)}; wiki en docs/wiki")
