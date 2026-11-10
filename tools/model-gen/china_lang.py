"""Traducciones de la tanda 1 china (es_es y en_us): nombres, tooltips, mito, mensajes, entidades, lugares de botín y
fases de la luna. Se agregan a los lang del mod (reemplazan las mismas claves si ya estaban) y quedan ordenados.
Las claves book.mythicrelics.relics.entry.* las escribe make_codex.py.
Uso: python china_lang.py
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    # Libro
    "book.mythicrelics.relics.china.name": "China",
    "book.mythicrelics.relics.china.intro.name": "Reliquias chinas",
    "lore.mythicrelics.china.intro": "Los tesoros chinos vienen de los mitos antiguos (Nüwa, Hou Yi y Chang'e) y de dos novelas del siglo XVI que fijaron la imagen de dioses y demonios: Viaje al Oeste, con el Rey Mono, y La investidura de los dioses, con Nezha.\\\nLos dragones chinos (long) no tienen alas ni escupen fuego: vuelan entre las nubes y traen la lluvia.",
    # Ruyi Jingu Bang
    "item.mythicrelics.ruyi_jingu_bang": "Ruyi Jingu Bang",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.1": "Arma pesada con %1$s bloques más de alcance",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.2": "Clic derecho: se estira %2$s bloques en línea recta y golpea a todo con %3$s de daño",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.3": "Recarga: %4$s s",
    "lore.mythicrelics.ruyi_jingu_bang": "El bastón del Rey Mono. Era la columna de hierro con que el Rey Dragón del Mar del Este medía la profundidad del océano; pesaba 13.500 jin y obedecía a Sun Wukong.\\\nCrecía hasta el cielo o se encogía hasta caber detrás de su oreja. Es de hierro negro con un aro de oro en cada punta.",
    # Pelos del Rey Mono
    "item.mythicrelics.monkey_king_hairs": "Pelos del Rey Mono",
    "item.mythicrelics.monkey_king_hairs.tooltip.1": "Clic derecho: soplas los pelos y salen %1$s monos guerreros durante %2$s s",
    "item.mythicrelics.monkey_king_hairs.tooltip.2": "Pelean contra lo que golpeas o lo que te golpea: %3$s de vida y %4$s de daño cada uno",
    "item.mythicrelics.monkey_king_hairs.tooltip.3": "Recarga: %5$s min",
    "lore.mythicrelics.monkey_king_hairs": "Sun Wukong se arrancaba un puñado de pelos, los masticaba, los soplaba y gritaba «¡Cambia!»: cada pelo se volvía un mono igual a él que peleaba a su lado.\\\nAsí enfrentó solo a los ejércitos del Cielo.",
    "entity.mythicrelics.monkey_warrior": "Mono guerrero",
    "message.mythicrelics.monkey_king_hairs": "¡Cambia!",
    # Calabaza
    "item.mythicrelics.purple_gold_gourd": "Calabaza roja de oro púrpura",
    "item.mythicrelics.purple_gold_gourd.tooltip.1": "Clic derecho a un mob: lo atrapas dentro (%1$s de vida o menos; no jefes, jugadores ni invocaciones)",
    "item.mythicrelics.purple_gold_gourd.tooltip.2": "Clic derecho otra vez: lo sueltas donde miras",
    "item.mythicrelics.purple_gold_gourd.tooltip.3": "Un hostil que pasa %2$s min dentro se disuelve: te da su botín y su experiencia",
    "item.mythicrelics.purple_gold_gourd.tooltip.4": "Recarga: %3$s s",
    "lore.mythicrelics.purple_gold_gourd": "El tesoro de los demonios Cuerno de Oro y Cuerno de Plata en Viaje al Oeste. Si la calabaza decía tu nombre y respondías, quedabas atrapado dentro, y a las pocas horas te disolvías. Wukong cayó en ella y escapó con un engaño.\\\nSu nombre, zijin hong hulu, es «calabaza roja de oro púrpura»: el oro púrpura es un oro rojizo, no el color morado.",
    "message.mythicrelics.gourd_too_strong": "La calabaza no puede con él",
    "message.mythicrelics.gourd_captured": "%s responde… y queda atrapado en la calabaza",
    "message.mythicrelics.gourd_dissolved": "%s se disolvió dentro de la calabaza",
    "tooltip.mythicrelics.gourd_inside": "Dentro: %s",
    # Abanico
    "item.mythicrelics.banana_leaf_fan": "Abanico de hoja de plátano",
    "item.mythicrelics.banana_leaf_fan.tooltip.1": "Cada golpe lanza una cuchilla de viento: %1$s bloques, %2$s de daño a lo que cruza",
    "item.mythicrelics.banana_leaf_fan.tooltip.2": "Clic derecho: un aleteo, un dash de viento de unos %3$s bloques que empuja a los mobs",
    "item.mythicrelics.banana_leaf_fan.tooltip.3": "%4$s aleteos; cada uno vuelve en %5$s s",
    "item.mythicrelics.banana_leaf_fan.tooltip.4": "Tres aleteos en %6$s s: nubes y lluvia durante %7$s s que apagan el fuego cercano",
    "lore.mythicrelics.banana_leaf_fan": "El abanico de la Princesa Abanico de Hierro. Un aleteo levantaba el viento, el segundo traía las nubes y el tercero la lluvia: así se apagaban las Montañas Llameantes.\\\nCon un solo aleteo mandó a Wukong volando a miles de leguas.",
    "tooltip.mythicrelics.fan_charges": "Aleteos: %s de %s",
    # Perla del dragón
    "item.mythicrelics.dragon_pearl": "Perla del dragón",
    "item.mythicrelics.dragon_pearl.tooltip.1": "Clic derecho: llamas a tu long, el dragón chino, y lo montas",
    "item.mythicrelics.dragon_pearl.tooltip.2": "Vuela hacia donde miras (salto para subir) y nada bajo el agua; montado respiras bajo el agua",
    "item.mythicrelics.dragon_pearl.tooltip.3": "Montado, usar la perla llama a la lluvia o la detiene",
    "item.mythicrelics.dragon_pearl.tooltip.4": "Al bajarte vuelve a las nubes a los %1$s s; si muere, vuelve en %2$s min",
    "lore.mythicrelics.dragon_pearl": "Los dragones chinos (long) no escupen fuego ni tienen alas: vuelan entre las nubes, traen la lluvia y gobiernan los mares y los ríos.\\\nTienen cuernos de ciervo, cuello de serpiente, escamas de carpa y garras de águila, y en el arte persiguen una perla llameante, fuente de su poder.",
    "entity.mythicrelics.long_dragon": "Long",
    "message.mythicrelics.dragon_rain": "El long llama a la lluvia",
    "message.mythicrelics.dragon_rain_stop": "El long despeja el cielo",
    # Ruedas
    "item.mythicrelics.wind_fire_wheels": "Ruedas de viento y fuego",
    "item.mythicrelics.wind_fire_wheels.tooltip.1": "+%1$s%% de velocidad y subes bloques de un paso",
    "item.mythicrelics.wind_fire_wheels.tooltip.2": "Corriendo: estela de llamas que quema a los hostiles y el fuego no te daña",
    "item.mythicrelics.wind_fire_wheels.tooltip.3": "Corriendo, cruzas la lava como si fuera suelo",
    "lore.mythicrelics.wind_fire_wheels": "Las ruedas de Nezha, el niño guerrero de La investidura de los dioses: dos ruedas que giraban entre viento y llamas bajo sus pies y lo llevaban volando a toda velocidad.",
    # Arco
    "item.mythicrelics.hou_yi_bow": "Arco de Hou Yi",
    "item.mythicrelics.hou_yi_bow.tooltip.1": "Flechas con +%1$s%% de daño que vuelan más rápido y más recto",
    "item.mythicrelics.hou_yi_bow.tooltip.2": "Contra blazes, ghasts, cubos de magma, phantoms y vex: daño x%2$s",
    "item.mythicrelics.hou_yi_bow.tooltip.3": "Lo que derribas así cae como un cuervo dorado y suelta el doble de botín",
    "lore.mythicrelics.hou_yi_bow": "Cuando diez soles salieron juntos y quemaban la Tierra, el arquero Hou Yi derribó nueve con su arco rojo y sus flechas blancas, regalo del emperador del cielo.\\\nCada sol era un cuervo dorado de tres patas, que cayó a tierra.",
    # Elixir
    "item.mythicrelics.change_elixir": "Elixir de Chang'e",
    "item.mythicrelics.change_elixir.tooltip.1": "Beber: ascenso lunar, saltas muy alto y caes despacio sin daño",
    "item.mythicrelics.change_elixir.tooltip.2": "Tus golpes en el aire hacen +%1$s%% de daño",
    "item.mythicrelics.change_elixir.tooltip.3": "Dura %2$s s con luna llena, %3$s s en cuarto y %4$s s en luna nueva",
    "item.mythicrelics.change_elixir.tooltip.4": "No se gasta. Recarga: %5$s min",
    "lore.mythicrelics.change_elixir": "La Reina Madre del Oeste le dio a Hou Yi el elixir de la inmortalidad. Su esposa Chang'e se lo bebió, se volvió ligera y flotó hasta la Luna, donde vive todavía con el Conejo de Jade.",
    "message.mythicrelics.change_elixir": "Ascenso lunar (%s): %s s",
    "moon_phase.mythicrelics.full_moon": "luna llena",
    "moon_phase.mythicrelics.waning_gibbous": "luna gibosa menguante",
    "moon_phase.mythicrelics.third_quarter": "cuarto menguante",
    "moon_phase.mythicrelics.waning_crescent": "luna menguante",
    "moon_phase.mythicrelics.new_moon": "luna nueva",
    "moon_phase.mythicrelics.waxing_crescent": "luna creciente",
    "moon_phase.mythicrelics.first_quarter": "cuarto creciente",
    "moon_phase.mythicrelics.waxing_gibbous": "luna gibosa creciente",
    # Piedra
    "item.mythicrelics.five_color_stone": "Piedra de cinco colores",
    "item.mythicrelics.five_color_stone.tooltip.1": "Cada %1$s s repara 1 punto de durabilidad de la armadura y de lo que tienes en las manos",
    "item.mythicrelics.five_color_stone.tooltip.2": "Los rayos no te hacen daño",
    "lore.mythicrelics.five_color_stone": "Cuando el pilar del cielo se rompió y el firmamento se agrietó, la diosa Nüwa fundió piedras de cinco colores y con ellas lo remendó.\\\nUna de las que sobraron es la piedra de «Sueño en el pabellón rojo».",
    # Lugares
    "loot.mythicrelics.chests.underwater_ruin_big": "ruinas submarinas grandes",
    "loot.mythicrelics.chests.underwater_ruin_small": "ruinas submarinas chicas",
    "loot.mythicrelics.chests.simple_dungeon": "mazmorras",
    "loot.mythicrelics.archaeology.trail_ruins_common": "arena y grava sospechosas comunes de ruinas de los senderos",
}

EN = {
    "book.mythicrelics.relics.china.name": "China",
    "book.mythicrelics.relics.china.intro.name": "Chinese Relics",
    "lore.mythicrelics.china.intro": "The Chinese treasures come from the ancient myths (Nüwa, Hou Yi and Chang'e) and from two 16th-century novels that set the image of gods and demons: Journey to the West, with the Monkey King, and Investiture of the Gods, with Nezha.\\\nChinese dragons (long) have no wings and breathe no fire: they fly among the clouds and bring the rain.",
    "item.mythicrelics.ruyi_jingu_bang": "Ruyi Jingu Bang",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.1": "Heavy weapon with %1$s blocks of extra reach",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.2": "Right-click: it stretches %2$s blocks in a straight line and hits everything for %3$s damage",
    "item.mythicrelics.ruyi_jingu_bang.tooltip.3": "Cooldown: %4$s s",
    "lore.mythicrelics.ruyi_jingu_bang": "The Monkey King's staff. It was the iron pillar the Dragon King of the Eastern Sea used to measure the depth of the ocean; it weighed 13,500 jin and obeyed Sun Wukong.\\\nIt grew up to the sky or shrank small enough to hide behind his ear. It is black iron with a gold band at each end.",
    "item.mythicrelics.monkey_king_hairs": "Hairs of the Monkey King",
    "item.mythicrelics.monkey_king_hairs.tooltip.1": "Right-click: blow the hairs and %1$s monkey warriors appear for %2$s s",
    "item.mythicrelics.monkey_king_hairs.tooltip.2": "They fight what you hit or what hits you: %3$s health and %4$s damage each",
    "item.mythicrelics.monkey_king_hairs.tooltip.3": "Cooldown: %5$s min",
    "lore.mythicrelics.monkey_king_hairs": "Sun Wukong would pluck a handful of hairs, chew them, blow them out and shout \"Change!\": each hair became a monkey just like him that fought at his side.\\\nThat is how he faced the armies of Heaven alone.",
    "entity.mythicrelics.monkey_warrior": "Monkey Warrior",
    "message.mythicrelics.monkey_king_hairs": "Change!",
    "item.mythicrelics.purple_gold_gourd": "Red Gourd of Purple Gold",
    "item.mythicrelics.purple_gold_gourd.tooltip.1": "Right-click a mob: trap it inside (%1$s health or less; no bosses, players or summons)",
    "item.mythicrelics.purple_gold_gourd.tooltip.2": "Right-click again: release it where you look",
    "item.mythicrelics.purple_gold_gourd.tooltip.3": "A hostile left inside for %2$s min dissolves: you get its loot and experience",
    "item.mythicrelics.purple_gold_gourd.tooltip.4": "Cooldown: %3$s s",
    "lore.mythicrelics.purple_gold_gourd": "The treasure of the demons Golden Horn and Silver Horn in Journey to the West. If the gourd called your name and you answered, you were trapped inside and dissolved within hours. Wukong fell into it and escaped by a trick.\\\nIts name, zijin hong hulu, means \"red gourd of purple gold\": purple gold is a reddish gold, not the colour purple.",
    "message.mythicrelics.gourd_too_strong": "The gourd can't hold it",
    "message.mythicrelics.gourd_captured": "%s answers… and is trapped in the gourd",
    "message.mythicrelics.gourd_dissolved": "%s dissolved inside the gourd",
    "tooltip.mythicrelics.gourd_inside": "Inside: %s",
    "item.mythicrelics.banana_leaf_fan": "Banana Leaf Fan",
    "item.mythicrelics.banana_leaf_fan.tooltip.1": "Every hit throws a wind blade: %1$s blocks, %2$s damage to whatever it crosses",
    "item.mythicrelics.banana_leaf_fan.tooltip.2": "Right-click: a flap, a wind dash of about %3$s blocks that pushes mobs away",
    "item.mythicrelics.banana_leaf_fan.tooltip.3": "%4$s flaps; each comes back in %5$s s",
    "item.mythicrelics.banana_leaf_fan.tooltip.4": "Three flaps within %6$s s: clouds and rain for %7$s s that put out nearby fire",
    "lore.mythicrelics.banana_leaf_fan": "The fan of Princess Iron Fan. One wave raised the wind, the second brought the clouds and the third the rain: that is how the Flaming Mountains were put out.\\\nWith a single wave she sent Wukong flying thousands of leagues away.",
    "tooltip.mythicrelics.fan_charges": "Flaps: %s of %s",
    "item.mythicrelics.dragon_pearl": "Dragon Pearl",
    "item.mythicrelics.dragon_pearl.tooltip.1": "Right-click: call your long, the Chinese dragon, and ride it",
    "item.mythicrelics.dragon_pearl.tooltip.2": "It flies where you look (jump to rise) and swims underwater; while riding you breathe underwater",
    "item.mythicrelics.dragon_pearl.tooltip.3": "While riding, using the pearl calls the rain or stops it",
    "item.mythicrelics.dragon_pearl.tooltip.4": "When you dismount it returns to the clouds after %1$s s; if it dies, it comes back in %2$s min",
    "lore.mythicrelics.dragon_pearl": "Chinese dragons (long) breathe no fire and have no wings: they fly among the clouds, bring the rain and rule the seas and rivers.\\\nThey have a stag's antlers, a snake's neck, a carp's scales and an eagle's claws, and in art they chase a flaming pearl, the source of their power.",
    "entity.mythicrelics.long_dragon": "Long",
    "message.mythicrelics.dragon_rain": "The long calls the rain",
    "message.mythicrelics.dragon_rain_stop": "The long clears the sky",
    "item.mythicrelics.wind_fire_wheels": "Wind Fire Wheels",
    "item.mythicrelics.wind_fire_wheels.tooltip.1": "+%1$s%% speed and you step up full blocks",
    "item.mythicrelics.wind_fire_wheels.tooltip.2": "While sprinting: a trail of flames burns hostiles and fire doesn't hurt you",
    "item.mythicrelics.wind_fire_wheels.tooltip.3": "While sprinting, you cross lava as if it were ground",
    "lore.mythicrelics.wind_fire_wheels": "The wheels of Nezha, the child warrior of Investiture of the Gods: two wheels spinning between wind and flames under his feet that carried him flying at full speed.",
    "item.mythicrelics.hou_yi_bow": "Bow of Hou Yi",
    "item.mythicrelics.hou_yi_bow.tooltip.1": "Arrows deal +%1$s%% damage and fly faster and straighter",
    "item.mythicrelics.hou_yi_bow.tooltip.2": "Against blazes, ghasts, magma cubes, phantoms and vexes: x%2$s damage",
    "item.mythicrelics.hou_yi_bow.tooltip.3": "What you shoot down this way falls like a golden crow and drops double loot",
    "lore.mythicrelics.hou_yi_bow": "When ten suns rose together and scorched the Earth, the archer Hou Yi shot down nine with his red bow and white arrows, a gift from the emperor of heaven.\\\nEach sun was a three-legged golden crow, which fell to the ground.",
    "item.mythicrelics.change_elixir": "Elixir of Chang'e",
    "item.mythicrelics.change_elixir.tooltip.1": "Drink: lunar ascent, you jump very high and fall slowly without damage",
    "item.mythicrelics.change_elixir.tooltip.2": "Your hits in the air deal +%1$s%% damage",
    "item.mythicrelics.change_elixir.tooltip.3": "Lasts %2$s s at full moon, %3$s s at a quarter and %4$s s at new moon",
    "item.mythicrelics.change_elixir.tooltip.4": "Never used up. Cooldown: %5$s min",
    "lore.mythicrelics.change_elixir": "The Queen Mother of the West gave Hou Yi the elixir of immortality. His wife Chang'e drank it, became weightless and floated up to the Moon, where she still lives with the Jade Rabbit.",
    "message.mythicrelics.change_elixir": "Lunar ascent (%s): %s s",
    "moon_phase.mythicrelics.full_moon": "full moon",
    "moon_phase.mythicrelics.waning_gibbous": "waning gibbous",
    "moon_phase.mythicrelics.third_quarter": "third quarter",
    "moon_phase.mythicrelics.waning_crescent": "waning crescent",
    "moon_phase.mythicrelics.new_moon": "new moon",
    "moon_phase.mythicrelics.waxing_crescent": "waxing crescent",
    "moon_phase.mythicrelics.first_quarter": "first quarter",
    "moon_phase.mythicrelics.waxing_gibbous": "waxing gibbous",
    "item.mythicrelics.five_color_stone": "Five-Colored Stone",
    "item.mythicrelics.five_color_stone.tooltip.1": "Every %1$s s it repairs 1 durability on your armor and on what you hold",
    "item.mythicrelics.five_color_stone.tooltip.2": "Lightning doesn't hurt you",
    "lore.mythicrelics.five_color_stone": "When the pillar of heaven broke and the sky cracked, the goddess Nüwa melted stones of five colours and patched the firmament with them.\\\nOne of the stones left over is the stone of \"Dream of the Red Chamber\".",
    "loot.mythicrelics.chests.underwater_ruin_big": "large underwater ruins",
    "loot.mythicrelics.chests.underwater_ruin_small": "small underwater ruins",
    "loot.mythicrelics.chests.simple_dungeon": "dungeons",
    "loot.mythicrelics.archaeology.trail_ruins_common": "common suspicious sand and gravel of trail ruins",
}

if __name__ == "__main__":
    assert set(ES) == set(EN), set(ES) ^ set(EN)
    for code, entries in (("es_es", ES), ("en_us", EN)):
        path = os.path.join(ASSETS, "lang", code + ".json")
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        data.update(entries)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(json.dumps(data, indent=2, ensure_ascii=False, sort_keys=True) + "\n")
        print(code, len(data))
