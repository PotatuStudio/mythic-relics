"""Resúmenes del tooltip corto (item.mythicrelics.<id>.summary): una línea con lo importante y la recarga. Sin Shift,
client/RelicTooltips muestra esta línea en vez de las del efecto; con Shift, el detalle completo. Usan los mismos %n$s
que las líneas del efecto (relic/TooltipArgs). Uso: python summaries_lang.py"""
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

# id: (español, inglés)
SUMMARY = {
    "achilles_shield": ("Escudo irrompible; cada bloqueo carga tu siguiente golpe (+%3$s%% por nivel)",
                        "Unbreakable shield; each block powers up your next hit (+%3$s%% per level)"),
    "ancile": ("Escudo irrompible que devuelve flechas y proyectiles; protege a los aldeanos en invasiones",
               "Unbreakable shield that sends arrows and projectiles back; shields villagers in raids"),
    "andvaranaut": ("Doble oro de minerales y trueques, pero %1$s corazones menos y los hostiles te prefieren",
                    "Double gold from ore and bartering, but %1$s fewer hearts and hostiles target you"),
    "ankh": ("Cura medio corazón cada %1$s s; los no-muertos te hacen %2$s%% menos daño",
             "Heals half a heart every %1$s s; undead deal %2$s%% less damage to you"),
    "anubis_scepter": ("Destruye de un golpe al no-muerto bajo el %1$s%% de vida: %2$s veces la experiencia",
                       "One-hit kills undead below %1$s%% health: %2$s times the experience"),
    "apple_of_discord": ("Clic derecho a un mob: los hostiles cercanos lo atacan %2$s s · recarga %4$s s",
                         "Right-click a mob: nearby hostiles attack it for %2$s s · cooldown %4$s s"),
    "ariadne_thread": ("Clic derecho ata el hilo; agachado, vuelves al inicio · recarga %2$s min",
                       "Right-click ties the thread; sneaking, you return to it · cooldown %2$s min"),
    "athena_aegis": ("Al bloquear un golpe cuerpo a cuerpo, el atacante queda aturdido %1$s s",
                     "Blocking a melee hit stuns the attacker for %1$s s"),
    "augur_lituus": ("Clic derecho: auspicios, casi siempre Suerte II y Prisa I · recarga %4$s min",
                     "Right-click: take the auspices, usually Luck II and Haste I · cooldown %4$s min"),
    "bastet_amulet": ("Los creepers y los phantoms te evitan y las caídas no te hacen daño",
                      "Creepers and phantoms avoid you and falls deal no damage"),
    "brisingamen": ("Cultivos y crías cercanas crecen más rápido; matar hostiles te cura %2$s corazón",
                    "Nearby crops and babies grow faster; killing hostiles heals %2$s heart"),
    "cerberus_collar": ("Si un mob te hiere, Cerbero aparece y lo ataca %1$s s · recarga %4$s s",
                        "When a mob hurts you, Cerberus appears and attacks it for %1$s s · cooldown %4$s s"),
    "change_elixir": ("Bébelo: saltas muy alto, caes sin daño y pegas más fuerte en el aire · recarga %5$s min",
                      "Drink: jump very high, fall safely and hit harder in the air · cooldown %5$s min"),
    "charon_obol": ("Si mueres con él, conservas el inventario y la experiencia (se gasta)",
                    "If you die carrying it, you keep your inventory and experience (consumed)"),
    "dainsleif": ("El golpeado no se cura por %1$s s y sangra medio corazón por segundo",
                  "Its victims can't heal for %1$s s and bleed half a heart per second"),
    "dragon_pearl": ("Clic derecho: llamas a tu dragón long y lo montas; vuela y nada",
                     "Right-click: summon and ride your long dragon; it flies and swims"),
    "draupnir": ("Cada %1$sª noche que lo llevas puesto gotean %2$s lingotes de oro",
                 "Every %1$sth night you wear it, it drips %2$s gold ingots"),
    "eye_of_horus": ("Inmune a Ceguera y Oscuridad; tras un golpe fuerte recuperas el %2$s%% · recarga %4$s s",
                     "Immune to Blindness and Darkness; after a big hit you recover %2$s%% · cooldown %4$s s"),
    "falcon_cloak": ("Tecla del manto (V): te vuelves halcón y vuelas %1$s s · recarga %2$s s",
                     "Cloak key (V): turn into a falcon and fly for %1$s s · cooldown %2$s s"),
    "five_color_stone": ("Repara poco a poco tu armadura y lo que tienes en las manos; inmune a los rayos",
                         "Slowly repairs your armor and held items; immune to lightning"),
    "freyr_sword": ("Va en la espalda; sale de la vaina y pelea sola a tu lado: %2$s de daño por tajo",
                    "Worn on your back; it draws itself and fights beside you: %2$s damage per slash"),
    "fu_talismans": ("Clic derecho a un no-muerto: queda inmóvil %1$s s · recarga %2$s s",
                     "Right-click an undead: it is frozen for %1$s s · cooldown %2$s s"),
    "gjallarhorn": ("Tócalo: Fuerza I y Velocidad I para ti y tus aliados · recarga %6$s min",
                    "Blow it: Strength I and Speed I for you and your allies · cooldown %6$s min"),
    "gleipnir": ("Clic derecho a un mob: queda atado %1$s s y recibe +%3$s%% de daño · recarga %2$s s",
                 "Right-click a mob: it is bound for %1$s s and takes +%3$s%% damage · cooldown %2$s s"),
    "golden_bridle": ("Clic derecho: llamas a Pegaso y lo montas; vuela, planea y embiste en picada",
                      "Right-click: summon and ride Pegasus; it flies, glides and dive-rams"),
    "golden_fleece": ("Héroe de la aldea mientras lo llevas; esquilar puede dar oro",
                      "Hero of the Village while worn; shearing may drop gold"),
    "golden_scarab": ("Al bajar de %1$s corazones o morir, renaces con Absorción %2$s · recarga %4$s s",
                      "Below %1$s hearts or on a fatal hit, you are reborn with Absorption %2$s · cooldown %4$s s"),
    "gram": ("Pasiva: cada %1$s golpes, una estocada con +%2$s%% de daño (el doble contra mobs grandes)",
             "Passive: every %1$s hits, a thrust with +%2$s%% damage (double against big mobs)"),
    "gungnir": ("Lánzala: busca al mob más cercano a donde apuntas y vuelve a ti",
                "Throw it: it seeks the mob nearest your aim and returns to you"),
    "hades_helm": ("Agachado y quieto %1$s s: invisible del todo; agachado caminas más rápido",
                   "Sneak still for %1$s s: fully invisible; you also sneak faster"),
    "harpe": ("Decapita al instante a los mobs bajo el %3$s%% de vida · recarga %4$s s",
              "Instantly beheads mobs below %3$s%% health · cooldown %4$s s"),
    "hermes_sandals": ("+%1$s%% de velocidad y doble salto",
                       "+%1$s%% speed and a double jump"),
    "hou_yi_bow": ("Flechas más fuertes y rectas; daño x%2$s contra blazes, ghasts y phantoms",
                   "Stronger, straighter arrows; x%2$s damage against blazes, ghasts and phantoms"),
    "icarus_wings": ("Mantén salto en el aire para planear y aletear; no subas demasiado al sol",
                     "Hold jump in the air to glide and flap; don't fly too close to the sun"),
    "idunn_apples": ("Come una: Regeneración II, Absorción y te quita los males; crece una por día",
                     "Eat one: Regeneration II, Absorption and cures ailments; one grows back per day"),
    "jackal_mask": ("Los no-muertos que matas sueltan almas que te curan %1$s ❤",
                    "Undead you kill drop souls that heal %1$s ❤"),
    "jackal_ring": ("De noche o bajo tierra, al recibir un golpe acuden %1$s chacales · recarga %3$s s",
                    "At night or underground, getting hit calls %1$s jackals · cooldown %3$s s"),
    "janus_key": ("Agachado: cierras con llave puertas, trampillas, vallas y cofres (solo tú los abres)",
                  "Sneak: lock doors, trapdoors, gates and chests (only you can open them)"),
    "jarngreipr": ("+%1$s de daño sin arma; inmune a los rayos",
                   "+%1$s unarmed damage; immune to lightning"),
    "medusa_head": ("Álzala como escudo: los mobs que la miran se petrifican",
                    "Raise it like a shield: mobs caught in its gaze turn to stone"),
    "megingjord": ("+%1$s%% de daño cuerpo a cuerpo y %2$s%% menos retroceso",
                   "+%1$s%% melee damage and %2$s%% less knockback"),
    "mimir_head": ("Clic derecho: Mímir te guía al tesoro más valioso cercano · recarga %4$s s",
                   "Right-click: Mímir guides you to the most valuable treasure nearby · cooldown %4$s s"),
    "mistletoe_dart": ("Lánzalo cuando quieras: %1$s de daño que ignora la armadura, el doble contra jefes",
                       "Throw it whenever you like: %1$s damage that ignores armor, double against bosses"),
    "mjolnir": ("Lánzalo: golpea hasta %1$s bloques y vuelve a ti; en tormenta, llama un rayo",
                "Throw it: strikes up to %1$s blocks away and returns; in storms, calls lightning"),
    "monkey_king_hairs": ("Clic derecho: %1$s monos guerreros pelean por ti %2$s s · recarga %5$s min",
                          "Right-click: %1$s monkey warriors fight for you for %2$s s · cooldown %5$s min"),
    "mummy_wraps": ("Tus golpes dan Hambre II y Lentitud; los husks no te atacan",
                    "Your hits inflict Hunger II and Slowness; husks don't attack you"),
    "nemean_lion_skin": ("%1$s%% menos daño de armas y proyectiles; sin arma pegas +%2$s",
                         "%1$s%% less damage from weapons and projectiles; +%2$s unarmed damage"),
    "odin_ravens": ("Clic derecho: Huginn y Muninn vigilan y hacen brillar a los hostiles cercanos",
                    "Right-click: Huginn and Muninn keep watch and make nearby hostiles glow"),
    "odrerir": ("Bébelo: +%1$s niveles y Prisa I; una vez por día",
                "Drink: +%1$s levels and Haste I; once per day"),
    "orpheus_lyre": ("Tócala (Epitafio de Seikilos): los animales te siguen y los hostiles se duermen",
                     "Play it (Seikilos epitaph): animals follow you and hostiles fall asleep"),
    "osiris_crook": ("Los animales de granja te siguen; clic derecho vuelve adulta una cría · recarga %1$s s",
                     "Farm animals follow you; right-click grows up a baby · cooldown %1$s s"),
    "osiris_flail": ("Ataque cargado giratorio a %1$s bloques; trilla y replanta los cultivos",
                     "Charged spinning attack within %1$s blocks; threshes and replants crops"),
    "palladium": ("Colócalo: no aparecen monstruos a %1$s bloques",
                  "Place it: no monsters spawn within %1$s blocks"),
    "pandora_jar": ("Clic derecho: abres la jarra; puede salir un mal, vexes, un tesoro o la Esperanza",
                    "Right-click: open the jar; out may come a curse, vexes, a treasure or Hope"),
    "prometheus_fennel": ("Lo que golpeas arde; clic derecho cocina o funde el stack de la otra mano",
                          "Sets what you hit on fire; right-click cooks or smelts the off-hand stack"),
    "purple_gold_gourd": ("Clic derecho: atrapas un mob y lo sueltas después · recarga %3$s s",
                          "Right-click: trap a mob inside and release it later · cooldown %3$s s"),
    "ra_disk": ("Bajo el sol, +%1$s%% de daño a no-muertos y los quemas; el ureo quema al que te pega",
                "Under the sun, +%1$s%% damage to undead and you burn them; the uraeus burns attackers"),
    "ruyi_jingu_bang": ("+%1$s bloques de alcance; clic derecho se estira %2$s bloques · recarga %4$s s",
                        "+%1$s blocks of reach; right-click extends it %2$s blocks · cooldown %4$s s"),
    "sekhmet_claws": ("Frenesí: cada muerte suma +%3$s%% de daño y velocidad (máx. %2$s)",
                      "Frenzy: each kill adds +%3$s%% damage and speed (max %2$s)"),
    "sibylline_books": ("Clic derecho (%1$s niveles): un mapa a la estructura más cercana · recarga %4$s min",
                        "Right-click (%1$s levels): a map to the nearest structure · cooldown %4$s min"),
    "skidbladnir": ("Clic derecho al agua: despliegas un bote que se pliega solo",
                    "Right-click water: unfold a boat that folds itself back up"),
    "sleipnir_horseshoe": ("Clic derecho: llamas a Sleipnir y lo montas; galopa sobre el agua",
                           "Right-click: summon and ride Sleipnir; it gallops over water"),
    "styx_water": ("Bébela: %1$s min recibiendo el %2$s%% del daño, salvo por la espalda · recarga %3$s min",
                   "Drink: for %1$s min take %2$s%% damage, except from behind · cooldown %3$s min"),
    "talos_nail": ("Clic derecho: Talos, el gigante de bronce, pelea a tu lado %1$s s · recarga %7$s min",
                   "Right-click: Talos, the bronze giant, fights beside you for %1$s s · cooldown %7$s min"),
    "thoth_palette": ("Guarda tu experiencia (clic derecho) y recupérala (agachado); no se pierde al morir",
                      "Store your experience (right-click) and read it back (sneak); kept on death"),
    "tyrfing": ("Todos tus golpes son críticos (+%1$s%%), pero te hiere si no mata",
                "Every hit is critical (+%1$s%%), but it hurts you if it doesn't kill"),
    "vesta_lamp": ("En la otra mano: menos hambre y Regeneración II junto a una fogata",
                   "In the off hand: less hunger and Regeneration II near a campfire"),
    "wolf_bulla": ("Tus lobos se fortalecen; con poca vida aúllas y acuden %3$s lobos · recarga %5$s min",
                   "Your wolves grow stronger; at low health you howl and %3$s wolves come · cooldown %5$s min"),
    "xirang_sack": ("Si caes a la lava o al vacío, la tierra crece y te salva · recarga %1$s min",
                    "If you fall into lava or the void, the earth grows and saves you · cooldown %1$s min"),
    "zeus_thunderbolt": ("Carga y suelta: un rayo que salta entre hostiles · recarga %6$s s",
                         "Charge and release: lightning that chains between hostiles · cooldown %6$s s"),
}

if __name__ == "__main__":
    for code, col in (("es_es", 0), ("en_us", 1)):
        path = os.path.join(ASSETS, "lang", code + ".json")
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        relics = {m.group(1) for k in data for m in [re.match(r"item\.mythicrelics\.(\w+)\.tooltip\.1$", k)] if m}
        missing = relics - set(SUMMARY)
        if missing:
            raise SystemExit("faltan resúmenes: " + ", ".join(sorted(missing)))
        for rid, texts in SUMMARY.items():
            text = texts[col]
            # Solo %n$s que existan en las líneas del efecto de esa reliquia.
            used = {int(n) for k, v in data.items() if k.startswith(f"item.mythicrelics.{rid}.tooltip.")
                    for n in re.findall(r"%(\d+)\$s", v)}
            for n in re.findall(r"%(\d+)\$s", text):
                if int(n) not in used:
                    raise SystemExit(f"{rid} ({code}): %{n}$s no está en sus líneas del efecto")
            data[f"item.mythicrelics.{rid}.summary"] = text
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(json.dumps(data, indent=2, ensure_ascii=False, sort_keys=True) + "\n")
        print(code, len(SUMMARY), "resúmenes")
