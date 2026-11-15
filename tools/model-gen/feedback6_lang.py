"""Textos de la sexta ronda de arreglos (2026-11-15): Medusa a 2 s con lentitud creciente, manzana de la Discordia
también contra jefes, hilo de Ariadna sin límite de distancia y 1 min, escudo de Aquiles con una sola carga de +100 %,
égida de Atenea como escudo, Tyrfing que cuesta medio corazón por golpe que no mata, cayado y mayal de Osiris más
fuertes (no-muertos de tu lado, Regeneración con las dos insignias, renacer cada 10 min), aterrizaje seguro tras el
doble salto de Hermes y las alas de Ícaro, y paleta de Thot sin límite.
Uso: python feedback6_lang.py (después, python make_codex.py)"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    "item.mythicrelics.medusa_head.tooltip.2": "Todo mob que quede %3$s s en la mirada se va frenando hasta quedar de piedra %1$s s (hostiles, neutros y pasivos; no jefes)",
    "set.mythicrelics.perseus_set.bonus.1": "La mirada de Medusa petrifica en la mitad del tiempo y lo petrificado recibe +50 % de daño",
    "item.mythicrelics.apple_of_discord.tooltip.2": "Brilla %2$s s y los hostiles a %3$s bloques lo atacan a él (también si es un jefe)",
    "item.mythicrelics.ariadne_thread.summary": "Clic derecho ata el hilo; agachado, vuelves al inicio desde donde sea · recarga %1$s min",
    "item.mythicrelics.ariadne_thread.tooltip.2": "Agachado y clic derecho: vuelves al inicio, desde cualquier distancia",
    "item.mythicrelics.ariadne_thread.tooltip.3": "Solo en la misma dimensión. Recarga: %1$s min",
    "item.mythicrelics.achilles_shield.summary": "Escudo irrompible; bloquear carga tu siguiente golpe (+%2$s%%)",
    "item.mythicrelics.achilles_shield.tooltip.2": "Cólera: al bloquear un golpe, tu siguiente golpe hace +%2$s%% de daño",
    "item.mythicrelics.achilles_shield.tooltip.3": "y manda lejos al golpeado. Una sola carga; dura %1$s s",
    "message.mythicrelics.achilles_wrath": "Cólera de Aquiles: tu siguiente golpe hace el doble",
    "item.mythicrelics.athena_aegis.summary": "Escudo irrompible: al bloquear un golpe cuerpo a cuerpo, el atacante queda aturdido %1$s s",
    "item.mythicrelics.athena_aegis.tooltip.1": "Escudo que no se rompe. Al bloquear con ella un golpe cuerpo a cuerpo,",
    "item.mythicrelics.athena_aegis.tooltip.2": "el atacante queda aturdido %1$s s y retrocede (no a los jefes)",
    "item.mythicrelics.tyrfing.summary": "Todos tus golpes son críticos (+%1$s%%), pero cada golpe que no mata te quita medio corazón",
    "item.mythicrelics.tyrfing.tooltip.3": "Maldición: cada golpe que no mata te quita medio corazón",
    "item.mythicrelics.osiris_crook.summary": "Los animales te siguen; clic derecho vuelve adulta una cría o pone de tu lado a un no-muerto · recarga %1$s s",
    "item.mythicrelics.osiris_crook.tooltip.2": "Clic derecho en una cría: la vuelve adulta",
    "item.mythicrelics.osiris_crook.tooltip.3": "Clic derecho en un no-muerto hostil: pelea de tu lado %2$s s (no jefes)",
    "item.mythicrelics.osiris_crook.tooltip.4": "Recarga: %1$s s",
    "message.mythicrelics.osiris_rules": "Osiris, rey de los muertos, lo pone de tu lado",
    "set.mythicrelics.osiris_set.bonus.1": "Con las dos insignias en las manos: Regeneración I",
    "set.mythicrelics.osiris_set.bonus.2": "Al recibir daño mortal renaces con 4 ❤ y Regeneración II. Recarga: %s min",
    "set.mythicrelics.osiris_set.bonus.3": "+30 % de daño con el mayal",
    "item.mythicrelics.hermes_sandals.tooltip.2": "Después del doble salto no recibes daño de caída hasta tocar el suelo",
    "item.mythicrelics.hermes_sandals.tooltip.3": "La arena y la tierra de almas no te frenan",
    "item.mythicrelics.icarus_wings.tooltip.2": "Hasta %1$s aleteos por vuelo (se recargan en el suelo); tras planear o aletear no recibes daño de caída",
    "item.mythicrelics.thoth_palette.tooltip.3": "Sin límite de puntos, y no se pierden al morir",
    "item.mythicrelics.thoth_palette.tooltip.4": "En la mano secundaria: +%1$s%% de experiencia",
    "tooltip.mythicrelics.thoth_palette.written": "Escrito: %s puntos (como subir al nivel %s)",
}
EN = {
    "item.mythicrelics.medusa_head.tooltip.2": "Any mob that stays %3$s s in the gaze slows down until it turns to stone for %1$s s (hostile, neutral and passive; not bosses)",
    "set.mythicrelics.perseus_set.bonus.1": "Medusa's gaze petrifies in half the time and petrified mobs take +50% damage",
    "item.mythicrelics.apple_of_discord.tooltip.2": "It glows for %2$s s and hostiles within %3$s blocks attack it (bosses too)",
    "item.mythicrelics.ariadne_thread.summary": "Right-click ties the thread; sneaking, you return to it from anywhere · cooldown %1$s min",
    "item.mythicrelics.ariadne_thread.tooltip.2": "Sneak + right-click: return to the start, from any distance",
    "item.mythicrelics.ariadne_thread.tooltip.3": "Same dimension only. Cooldown: %1$s min",
    "item.mythicrelics.achilles_shield.summary": "Unbreakable shield; blocking powers up your next hit (+%2$s%%)",
    "item.mythicrelics.achilles_shield.tooltip.2": "Wrath: when you block a blow, your next hit deals +%2$s%% damage",
    "item.mythicrelics.achilles_shield.tooltip.3": "and sends the target flying. A single charge; lasts %1$s s",
    "message.mythicrelics.achilles_wrath": "Wrath of Achilles: your next hit deals double",
    "item.mythicrelics.athena_aegis.summary": "Unbreakable shield: blocking a melee hit stuns the attacker for %1$s s",
    "item.mythicrelics.athena_aegis.tooltip.1": "A shield that never breaks. When it blocks a melee hit,",
    "item.mythicrelics.athena_aegis.tooltip.2": "the attacker is stunned for %1$s s and pushed back (not bosses)",
    "item.mythicrelics.tyrfing.summary": "Every hit is critical (+%1$s%%), but every hit that doesn't kill costs you half a heart",
    "item.mythicrelics.tyrfing.tooltip.3": "Curse: every hit that doesn't kill costs you half a heart",
    "item.mythicrelics.osiris_crook.summary": "Animals follow you; right-click grows up a baby or turns an undead to your side · cooldown %1$s s",
    "item.mythicrelics.osiris_crook.tooltip.2": "Right-click a baby animal: it grows up",
    "item.mythicrelics.osiris_crook.tooltip.3": "Right-click a hostile undead: it fights on your side for %2$s s (not bosses)",
    "item.mythicrelics.osiris_crook.tooltip.4": "Cooldown: %1$s s",
    "message.mythicrelics.osiris_rules": "Osiris, king of the dead, turns it to your side",
    "set.mythicrelics.osiris_set.bonus.1": "With both regalia in your hands: Regeneration I",
    "set.mythicrelics.osiris_set.bonus.2": "On lethal damage you are reborn with 4 ❤ and Regeneration II. Cooldown: %s min",
    "set.mythicrelics.osiris_set.bonus.3": "+30% damage with the flail",
    "item.mythicrelics.hermes_sandals.tooltip.2": "After a double jump you take no fall damage until you land",
    "item.mythicrelics.hermes_sandals.tooltip.3": "Sand and soul soil don't slow you down",
    "item.mythicrelics.icarus_wings.tooltip.2": "Up to %1$s flaps per flight (recharged on the ground); after gliding or flapping you take no fall damage",
    "item.mythicrelics.thoth_palette.tooltip.3": "No point limit, and they are not lost on death",
    "item.mythicrelics.thoth_palette.tooltip.4": "In your off hand: +%1$s%% experience",
    "tooltip.mythicrelics.thoth_palette.written": "Written: %s points (like reaching level %s)",
}

REMOVE = ["message.mythicrelics.ariadne_far", "message.mythicrelics.tyrfing_curse", "message.mythicrelics.tyrfing_hungers"]

if __name__ == "__main__":
    assert set(ES) == set(EN), set(ES) ^ set(EN)
    for code, entries in (("es_es", ES), ("en_us", EN)):
        path = os.path.join(ASSETS, "lang", code + ".json")
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        data.update(entries)
        for k in REMOVE:
            data.pop(k, None)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(json.dumps(data, indent=2, ensure_ascii=False, sort_keys=True) + "\n")
        print(code, len(data))
