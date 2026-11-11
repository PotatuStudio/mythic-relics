"""Genera el libro de reliquias (Modonomicon) y la wiki del repo desde los mismos datos que usan el mod, el tooltip y el
visor (moddata.py), para que nunca se contradigan.

Escribe:
  - common/src/main/resources/data/mythicrelics/modonomicon/books/relics/  (book.json, una categoría y sus entradas
    por cultura)
  - las claves book.mythicrelics.relics.entry.* con los números ya calculados en los lang (reemplaza las anteriores)
  - docs/wiki/README.md, una página por cultura (egipto.md, grecia.md…) y docs/wiki/img/*.png (íconos, primer cuadro,
    x4; requiere Pillow)

Los textos del mito (lore.mythicrelics.*) y los títulos fijos del libro se escriben a mano en los lang.
Correr de nuevo cada vez que cambien los números, el botín, los tooltips o el mito:
    python make_codex.py
"""
import json
import os
import shutil
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import (ARCHAEOLOGY, ASSETS, CULTURES, LOOT_TABLE_KEYS, ORDER, ROOT, SETS, SLOTS, constants, lang, loot,  # noqa: E402
                     pct, rarity, set_args, substitute, tooltip_args, tooltip_lines)

BOOK = "relics"
NS = "mythicrelics"
BOOK_DIR = os.path.join(ROOT, "common", "src", "main", "resources", "data", NS, "modonomicon", "books", BOOK)
WIKI = os.path.join(ROOT, "docs", "wiki")
GENERATED_PREFIX = f"book.{NS}.{BOOK}.entry."
CHESTS = {"desert_pyramid": 4}   # cofres por estructura, para la probabilidad por estructura

# Cada cultura: página de la wiki, ícono de la categoría y de su introducción, y posición de cada entrada en el mapa
# de nodos de su categoría (en celdas).
CULTURE_INFO = {
    "egypt": {
        "wiki": "egipto.md", "title": "Egipto", "icon": "ankh", "intro_icon": "minecraft:chiseled_sandstone",
        "positions": {
            "intro": (0, 0),
            "golden_scarab": (-4, 2), "eye_of_horus": (-2, 2), "ankh": (2, 2), "mummy_wraps": (4, 2),
            "jackal_mask": (-2, 5), "anubis_scepter": (0, 5), "jackal_ring": (2, 5), "anubis_set": (0, 7),
            "ra_disk": (-4, 10), "bastet_amulet": (-2, 10), "thoth_palette": (2, 10), "sekhmet_claws": (4, 10),
            "osiris_crook": (-1, 13), "osiris_flail": (1, 13), "osiris_set": (0, 15),
        },
    },
    "greece": {
        "wiki": "grecia.md", "title": "Grecia", "icon": "hades_helm", "intro_icon": "minecraft:chiseled_quartz_block",
        "positions": {
            "intro": (0, 0),
            "hermes_sandals": (-2, 2), "hades_helm": (0, 2), "athena_aegis": (2, 2), "perseus_set": (0, 4),
            "medusa_head": (0, 6),
            "ariadne_thread": (-3, 8), "charon_obol": (0, 8), "orpheus_lyre": (3, 8),
            "icarus_wings": (-2, 11), "golden_fleece": (0, 11), "cerberus_collar": (2, 11),
            "pandora_jar": (-2, 13), "styx_water": (0, 13), "prometheus_fennel": (2, 13),
        },
    },
    "rome": {
        "wiki": "roma.md", "title": "Roma", "icon": "ancile", "intro_icon": "minecraft:chiseled_stone_bricks",
        "positions": {
            "intro": (0, 0),
            "ancile": (-2, 2), "palladium": (0, 2), "vesta_lamp": (2, 2),
            "wolf_bulla": (-3, 5), "janus_key": (-1, 5), "sibylline_books": (1, 5), "augur_lituus": (3, 5),
        },
    },
    "norse": {
        "wiki": "nordicos.md", "title": "Nórdicos", "icon": "mjolnir", "intro_icon": "minecraft:packed_ice",
        "positions": {
            "intro": (0, 0),
            "jarngreipr": (-2, 2), "mjolnir": (0, 2), "megingjord": (2, 2), "thor_set": (0, 4),
            "gungnir": (-3, 7), "draupnir": (-1, 7), "gjallarhorn": (1, 7), "gleipnir": (3, 7),
            "idunn_apples": (0, 9),
            "sleipnir_horseshoe": (-3, 12), "odin_ravens": (-1, 12), "mimir_head": (1, 12), "brisingamen": (3, 12),
            "skidbladnir": (-2, 14), "andvaranaut": (0, 14), "odrerir": (2, 14),
        },
    },
    "china": {
        "wiki": "china.md", "title": "China", "icon": "dragon_pearl", "intro_icon": "minecraft:red_glazed_terracotta",
        "positions": {
            "intro": (0, 0),
            "ruyi_jingu_bang": (-2, 2), "monkey_king_hairs": (0, 2), "purple_gold_gourd": (2, 2),
            "banana_leaf_fan": (-3, 5), "dragon_pearl": (-1, 5), "wind_fire_wheels": (1, 5), "hou_yi_bow": (3, 5),
            "change_elixir": (-1, 8), "five_color_stone": (1, 8),
            "fire_tipped_spear": (-3, 11), "universe_ring": (-1, 11), "erlang_eye": (1, 11), "erlang_halberd": (3, 11),
            "qilin_horn": (-3, 14), "pixiu_bracelet": (-1, 14), "fu_talismans": (1, 14), "xirang_sack": (3, 14),
            "immortal_peach_branch": (0, 16),
        },
    },
    "japan": {
        "wiki": "japon.md", "title": "Japón", "icon": "kusanagi", "intro_icon": "minecraft:cherry_log",
        "positions": {
            "intro": (0, 0),
            "kusanagi": (-2, 2), "yata_mirror": (0, 2), "yasakani_magatama": (2, 2),
            "raijin_drum": (-3, 5), "oni_kanabo": (-1, 5), "kitsune_star_ball": (1, 5), "tide_jewels": (3, 5),
            "lucky_mallet": (-2, 8), "kibi_dango": (0, 8), "urashima_rod": (2, 8),
            "fujin_wind_bag": (-3, 11), "tengu_mask": (-1, 11), "yashiori_sake": (1, 11), "kappa_dish": (3, 11),
            "tanuki_leaf": (-3, 14), "kintaro_axe": (-1, 14), "zashiki_warashi": (1, 14), "ame_no_nuboko": (3, 14),
            "orochi_scale": (0, 16),
        },
    },
}
SET_ICONS = {"anubis_set": "jackal_mask", "osiris_set": "osiris_flail", "perseus_set": "hades_helm", "thor_set": "mjolnir"}
SLOT_NAMES = {
    "es_es": {"legs/belt": "cinturón", "head/face": "cara", "chest/necklace": "collar", "hand/glove": "guantes",
              "hand/ring": "anillo", "mainhand": "mano (arma)", "head/hat": "sombrero", "offhand": "mano secundaria",
              "feet/shoes": "zapatos", "chest/back": "espalda", "chest/cape": "capa", "block": "bloque (se coloca)", "hand": "mano",
              "inventory": "inventario"},
    "en_us": {"legs/belt": "belt", "head/face": "face", "chest/necklace": "necklace", "hand/glove": "gloves",
              "hand/ring": "ring", "mainhand": "main hand (weapon)", "head/hat": "hat", "offhand": "off hand",
              "feet/shoes": "shoes", "chest/back": "back", "chest/cape": "cape", "block": "block (placed)", "hand": "hand",
              "inventory": "inventory"},
}
WORDS = {
    "es_es": {"per_chest": "{p} % por cofre", "per_brush": "{p} % por bloque cepillado",
              "per_structure": "≈ {p} % por pirámide", "slot": "Espacio"},
    "en_us": {"per_chest": "{p}% per chest", "per_brush": "{p}% per brushed block",
              "per_structure": "≈ {p}% per pyramid", "slot": "Slot"},
}
RARITIES = {"common": "común", "uncommon": "poco común", "rare": "rara", "epic": "épica"}
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
        per = w["per_brush"] if s["table"] in ARCHAEOLOGY else w["per_chest"]
        text = f"**{place[0].upper() + place[1:]}**: {per.format(p=pct(s['chance']))}"
        chests = CHESTS.get(s["table"])
        if chests:
            text += f" ({w['per_structure'].format(p=pct(1 - (1 - s['chance']) ** chests))})"
        out.append(text)
    return out


def set_of(item):
    return next((name for name, pieces in SETS.items() if item in pieces), None)


def culture_of(item_or_set):
    for culture, items in CULTURES.items():
        if item_or_set in items or item_or_set in SETS and SETS[item_or_set][0] in items:
            return culture
    raise KeyError(item_or_set)


def set_lines(m, set_id, sargs):
    """Título del bono del set y su efecto en una frase (las líneas 2 y 3 del tooltip juntas)."""
    title = substitute(m[f"tooltip.mythicrelics.{set_id}.1"], sargs)
    first, second = (substitute(m[f"tooltip.mythicrelics.{set_id}.{i}"], sargs).strip() for i in (2, 3))
    # Si la segunda línea empieza con mayúscula es otra frase (Osiris); si no, sigue la misma (Anubis, Perseo).
    joiner = ". " if second[:1].isupper() and first[-1:] not in ".:!?" else " "
    return title, first + joiner + second


def generated_keys(langs, args, sources, sargs):
    """Claves del libro con los números ya puestos: efecto, descripción corta y dónde se encuentra."""
    keys = {code: {} for code in langs}
    for code, m in langs.items():
        for item in ORDER:
            lines = sentences(tooltip_lines(m, item, args[item]))
            effect = lines[:]
            set_id = set_of(item)
            if set_id:
                title, bonus = set_lines(m, set_id, sargs[set_id])
                effect += ["", f"*{title}*", bonus]
            effect.append("")
            effect.append(f"{WORDS[code]['slot']}: {SLOT_NAMES[code][SLOTS[item]]}")
            keys[code][f"{GENERATED_PREFIX}{item}.effect"] = BREAK.join(effect)
            keys[code][f"{GENERATED_PREFIX}{item}.description"] = lines[0]
            keys[code][f"{GENERATED_PREFIX}{item}.where"] = BREAK.join(where_lines(code, m, sources.get(item, [])))
        for set_id in SETS:
            keys[code][f"{GENERATED_PREFIX}{set_id}.effect"] = BREAK.join(set_lines(m, set_id, sargs[set_id]))
    return keys


def text_page(page_id, title, text):
    return {"type": "modonomicon:text", "id": page_id, "title": title, "text": text, "show_title_separator": True}


def entry(culture, entry_id, name, description, icon, pages, parents=()):
    pos = CULTURE_INFO[culture]["positions"][entry_id]
    e = {"type": "modonomicon:content", "id": f"{NS}:{culture}/{entry_id}", "category": f"{NS}:{culture}",
         "name": name, "icon": icon, "x": pos[0], "y": pos[1], "pages": pages}
    if description:
        e["description"] = description
    if parents:
        e["parents"] = [{"entry": f"{NS}:{culture}/{p}"} for p in parents]
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
    count = 0
    for sort, (culture, items) in enumerate(CULTURES.items()):
        info = CULTURE_INFO[culture]
        write_json(os.path.join(BOOK_DIR, "categories", culture + ".json"), {
            "name": f"{prefix}.{culture}.name", "icon": f"{NS}:{info['icon']}", "sort_number": sort})
        entries = {"intro": entry(culture, "intro", f"{prefix}.{culture}.intro.name", None, info["intro_icon"],
                                  [text_page("intro", f"{prefix}.{culture}.intro.name", f"lore.{NS}.{culture}.intro")])}
        for item in items:
            g = f"{GENERATED_PREFIX}{item}"
            pages = [
                {"type": "modonomicon:spotlight", "id": "relic", "title": "", "text": f"{g}.effect", "item": f"{NS}:{item}"},
                text_page("myth", f"{prefix}.myth", f"lore.{NS}.{item}"),
                text_page("where", f"{prefix}.where", f"{g}.where"),
            ]
            entries[item] = entry(culture, item, f"item.{NS}.{item}", f"{g}.description", f"{NS}:{item}", pages,
                                  parents=("intro",))
        for set_id, pieces in SETS.items():
            if culture_of(set_id) != culture:
                continue
            entries[set_id] = entry(culture, set_id, f"{prefix}.{set_id}.name", None, f"{NS}:{SET_ICONS[set_id]}", [
                text_page("set", f"{prefix}.{set_id}.name", f"{GENERATED_PREFIX}{set_id}.effect"),
                text_page("myth", f"{prefix}.myth", f"lore.{NS}.{set_id}"),
            ], parents=tuple(sorted(pieces)))
        for name, e in entries.items():
            write_json(os.path.join(BOOK_DIR, "entries", culture, name + ".json"), e)
        count += len(entries)
    return count


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


def wiki_page(culture, items, es, args, sources, rar, sargs):
    info = CULTURE_INFO[culture]
    out = [f"# {info['title']}", "",
           "<!-- Generado por tools/model-gen/make_codex.py desde los datos del mod. No editar a mano. -->", "",
           plain(es[f"lore.{NS}.{culture}.intro"]), "",
           "| | Reliquia | Espacio | Rareza | Dónde |", "|---|---|---|---|---|"]
    for item in items:
        places = "; ".join(plain(x).replace("**", "") for x in where_lines("es_es", es, sources.get(item, [])))
        out.append(f"| ![](img/{item}.png) | [{es[f'item.{NS}.{item}']}](#{item.replace('_', '-')}) | "
                   f"{SLOT_NAMES['es_es'][SLOTS[item]]} | {RARITIES[rar.get(item, 'common')]} | {places} |")
    for item in items:
        out += ["", f"<a id=\"{item.replace('_', '-')}\"></a>", f"## {es[f'item.{NS}.{item}']}", "",
                f"<img src=\"img/{item}.png\" width=\"64\" alt=\"\">", "",
                f"**Espacio:** {SLOT_NAMES['es_es'][SLOTS[item]]} · **Rareza:** {RARITIES[rar.get(item, 'common')]}", ""]
        out += [f"- {line}" for line in sentences(tooltip_lines(es, item, args[item]))]
        set_id = set_of(item)
        if set_id:
            title, bonus = set_lines(es, set_id, sargs[set_id])
            out += ["", f"*{title}* {bonus}"]
        out += ["", "**Mito.** " + plain(es[f"lore.{NS}.{item}"]), "",
                "**Dónde encontrarla.** " + "; ".join(plain(x) for x in where_lines("es_es", es, sources.get(item, [])))]
    for set_id in SETS:
        if culture_of(set_id) != culture:
            continue
        title, bonus = set_lines(es, set_id, sargs[set_id])
        out += ["", f"## {es[f'book.{NS}.{BOOK}.{set_id}.name']}", "", f"{title} {bonus}", "",
                "**Mito.** " + plain(es[f"lore.{NS}.{set_id}"])]
    out.append("")
    with open(os.path.join(WIKI, info["wiki"]), "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(out))


def write_wiki(langs, args, sources, rar, sargs):
    from PIL import Image
    img_dir = os.path.join(WIKI, "img")
    os.makedirs(img_dir, exist_ok=True)
    for item in ORDER:
        icon = Image.open(os.path.join(ASSETS, "textures", "item", item + ".png")).crop((0, 0, 16, 16))
        icon.resize((64, 64), Image.NEAREST).save(os.path.join(img_dir, item + ".png"))
    es = langs["es_es"]
    lines = []
    for culture, items in CULTURES.items():
        wiki_page(culture, items, es, args, sources, rar, sargs)
        sets = [es[f"book.{NS}.{BOOK}.{s}.name"] for s in SETS if culture_of(s) == culture]
        info = CULTURE_INFO[culture]
        lines.append(f"- [{info['title']}]({info['wiki']}): {len(items)} reliquias"
                     + (f", con {' y '.join(sets)}." if sets else "."))
    index = ["# Wiki de Mythic Relics", "",
             "<!-- Generado por tools/model-gen/make_codex.py desde los datos del mod. No editar a mano. -->", "",
             "Reliquias de la mitología convertidas en equipo RPG. No se fabrican: se encuentran en estructuras que "
             "calzan con su cultura, y mientras más poderosas, más raras.", "",
             "En el juego, todo esto está en el **Códice de reliquias** (requiere Modonomicon). Con JEI instalado, cada "
             "reliquia muestra además dónde encontrarla.", "",
             "## Culturas", ""] + lines + [""]
    with open(os.path.join(WIKI, "README.md"), "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(index))


if __name__ == "__main__":
    langs = lang()
    consts = constants()
    args = tooltip_args(consts)
    sargs = set_args(consts)
    sources = loot()
    update_lang(generated_keys(langs, args, sources, sargs))
    langs = lang()
    n = write_book()
    write_wiki(langs, args, sources, rarity(), sargs)
    print(f"libro: {n} entradas en {os.path.relpath(BOOK_DIR, ROOT)}; wiki en docs/wiki")
