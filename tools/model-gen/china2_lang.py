"""Traducciones de la tanda 2 china (es_es y en_us): nombres, tooltips, mito, mensajes y entidades. Se agregan a los
lang del mod (reemplazan las mismas claves) y quedan ordenados. Uso: python china2_lang.py"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402

ES = {
    "item.mythicrelics.fire_tipped_spear": "Lanza de punta de fuego",
    "item.mythicrelics.fire_tipped_spear.tooltip.1": "Prende fuego al golpeado %1$s s",
    "item.mythicrelics.fire_tipped_spear.tooltip.2": "Clic derecho: llamarada en cono de %2$s bloques, %3$s de daño y fuego",
    "item.mythicrelics.fire_tipped_spear.tooltip.3": "Recarga: %4$s s (%5$s s con las ruedas de viento y fuego puestas)",
    "lore.mythicrelics.fire_tipped_spear": "La lanza de Nezha en La investidura de los dioses (huojian qiang): su punta ardía y con ella derrotó a ejércitos enteros, junto con sus ruedas de viento y fuego.",
    "item.mythicrelics.universe_ring": "Aro del Universo",
    "item.mythicrelics.universe_ring.tooltip.1": "Clic derecho: lanzas el aro, golpea hasta %1$s enemigos a %2$s bloques y vuelve",
    "item.mythicrelics.universe_ring.tooltip.2": "%3$s de daño a cada uno y los deja aturdidos %4$s s",
    "item.mythicrelics.universe_ring.tooltip.3": "Recarga: %5$s s",
    "lore.mythicrelics.universe_ring": "El qiankun quan, el aro de oro con que nació Nezha. Lo lanzaba como un arma y volvía a su mano; con él mató al hijo del Rey Dragón del Mar del Este.",
    "item.mythicrelics.erlang_eye": "Ojo de Erlang Shen",
    "item.mythicrelics.erlang_eye.tooltip.1": "Los invisibles brillan para ti",
    "item.mythicrelics.erlang_eye.tooltip.2": "Los hostiles a %1$s bloques brillan, también a través de las paredes",
    "lore.mythicrelics.erlang_eye": "Erlang Shen, el único dios que venció al Rey Mono, tenía un tercer ojo en la frente que veía a través de cualquier disfraz.\\\nAsí descubrió a Wukong aunque se transformara en templo, pez o pájaro.",
    "item.mythicrelics.erlang_halberd": "Alabarda de tres puntas",
    "item.mythicrelics.erlang_halberd.tooltip.1": "%1$s bloque más de alcance; cada golpe barre a los mobs a %2$s bloques",
    "item.mythicrelics.erlang_halberd.tooltip.2": "Set de Erlang Shen (con el Ojo puesto): cuando un mob te hiere, aparece el Perro Celestial durante %3$s s",
    "item.mythicrelics.erlang_halberd.tooltip.3": "Recarga del perro: %4$s s",
    "lore.mythicrelics.erlang_halberd": "El arma de Erlang Shen, la sanjian liangren dao: una alabarda de tres puntas y doble filo con la que peleó trescientos asaltos contra el Rey Mono.\\\nSu perro, el Xiaotian Quan, le mordió la pierna a Wukong para que Erlang lo atrapara.",
    "entity.mythicrelics.celestial_dog": "Perro Celestial",
    "message.mythicrelics.celestial_dog": "¡El Perro Celestial acude!",
    "item.mythicrelics.qilin_horn": "Cuerno del qilin",
    "item.mythicrelics.qilin_horn.tooltip.1": "Clic derecho: llamas al qilin y lo montas",
    "item.mythicrelics.qilin_horn.tooltip.2": "Muy rápido, salta alto, sin daño de caída, inmune al fuego y no pisa los cultivos",
    "item.mythicrelics.qilin_horn.tooltip.3": "Montado, regeneras tú y los animales y aldeanos a %1$s bloques",
    "item.mythicrelics.qilin_horn.tooltip.4": "Al bajarte se va a los %2$s s; si muere, vuelve en %3$s min",
    "lore.mythicrelics.qilin_horn": "El qilin, bestia benévola con cabeza de dragón, cuerpo de ciervo cubierto de escamas, cola de buey y un solo cuerno. Aparece ante los gobernantes justos.\\\nCamina sin aplastar la hierba ni dañar a ningún ser vivo, y lo envuelven llamas que no queman.",
    "entity.mythicrelics.qilin": "Qilin",
    "item.mythicrelics.pixiu_bracelet": "Pulsera del pixiu",
    "item.mythicrelics.pixiu_bracelet.tooltip.1": "Al matar a un hostil, %1$s%% de que suelte hasta %2$s pepitas de oro",
    "item.mythicrelics.pixiu_bracelet.tooltip.2": "Al morir conservas el oro, las esmeraldas y los diamantes",
    "lore.mythicrelics.pixiu_bracelet": "El pixiu es un león alado que se alimenta de oro y plata y no tiene por dónde soltarlos: todo lo que entra se queda.\\\nPor eso en China se llevan pulseras de jade con un pixiu para atraer la riqueza y no perderla.",
    "message.mythicrelics.pixiu_kept": "El pixiu no dejó escapar tus tesoros",
    "item.mythicrelics.fu_talismans": "Talismanes de papel",
    "item.mythicrelics.fu_talismans.tooltip.1": "Clic derecho a un no-muerto: le pegas un talismán y queda inmóvil %1$s s",
    "item.mythicrelics.fu_talismans.tooltip.2": "Recarga: %2$s s",
    "lore.mythicrelics.fu_talismans": "Los fu, talismanes de papel amarillo escritos con tinta bermellón por los sacerdotes taoístas.\\\nEn el folclore, uno pegado en la frente de un jiangshi (el cadáver que salta) lo deja inmóvil.",
    "message.mythicrelics.talisman_not_undead": "Los talismanes solo sujetan a los no-muertos",
    "item.mythicrelics.xirang_sack": "Saco de Xirang",
    "item.mythicrelics.xirang_sack.tooltip.1": "Clic derecho: crece frente a ti un muro de tierra de %1$s de ancho y %2$s de alto que corta el agua",
    "item.mythicrelics.xirang_sack.tooltip.2": "%3$s puñados; vuelve uno cada %4$s s",
    "lore.mythicrelics.xirang_sack": "El xirang era la tierra que crecía sola. Gun la robó del Cielo para contener el Gran Diluvio; su hijo Yu el Grande la usó para levantar diques y abrir canales hasta domar las aguas.",
    "tooltip.mythicrelics.xirang_charges": "Puñados: %s de %s",
    "item.mythicrelics.immortal_peach_branch": "Rama del melocotonero celestial",
    "item.mythicrelics.immortal_peach_branch.tooltip.1": "Da un melocotón de la inmortalidad cada %1$s días",
    "item.mythicrelics.immortal_peach_branch.tooltip.2": "Comerlo: 8 corazones extra durante %2$s min, Regeneración II %3$s s y quita los efectos negativos",
    "lore.mythicrelics.immortal_peach_branch": "En el Huerto Celestial de la Reina Madre del Oeste crecían los melocotones de la inmortalidad, que maduraban cada tres mil años.\\\nEl Rey Mono, puesto a cuidarlos, se los comió casi todos.",
    "tooltip.mythicrelics.peach_ripe": "Hay un melocotón maduro",
    "tooltip.mythicrelics.peach_growing": "El próximo melocotón está creciendo",
    "message.mythicrelics.peach_ripe": "Maduró un melocotón de la inmortalidad",
    "loot.mythicrelics.chests.trial_chambers.reward": "recompensas de cámaras de desafío",
}

EN = {
    "item.mythicrelics.fire_tipped_spear": "Fire-Tipped Spear",
    "item.mythicrelics.fire_tipped_spear.tooltip.1": "Sets what it hits on fire for %1$s s",
    "item.mythicrelics.fire_tipped_spear.tooltip.2": "Right-click: a cone of flame %2$s blocks long, %3$s damage and fire",
    "item.mythicrelics.fire_tipped_spear.tooltip.3": "Cooldown: %4$s s (%5$s s while wearing the Wind Fire Wheels)",
    "lore.mythicrelics.fire_tipped_spear": "Nezha's spear in Investiture of the Gods (huojian qiang): its tip burned, and with it and his Wind Fire Wheels he defeated whole armies.",
    "item.mythicrelics.universe_ring": "Universe Ring",
    "item.mythicrelics.universe_ring.tooltip.1": "Right-click: throw the ring; it hits up to %1$s enemies within %2$s blocks and comes back",
    "item.mythicrelics.universe_ring.tooltip.2": "%3$s damage to each and stuns them for %4$s s",
    "item.mythicrelics.universe_ring.tooltip.3": "Cooldown: %5$s s",
    "lore.mythicrelics.universe_ring": "The qiankun quan, the golden ring Nezha was born with. He threw it as a weapon and it came back to his hand; with it he killed the son of the Dragon King of the Eastern Sea.",
    "item.mythicrelics.erlang_eye": "Eye of Erlang Shen",
    "item.mythicrelics.erlang_eye.tooltip.1": "Invisible creatures glow for you",
    "item.mythicrelics.erlang_eye.tooltip.2": "Hostiles within %1$s blocks glow, even through walls",
    "lore.mythicrelics.erlang_eye": "Erlang Shen, the only god who defeated the Monkey King, had a third eye on his forehead that saw through any disguise.\\\nThat is how he found Wukong even when he turned into a temple, a fish or a bird.",
    "item.mythicrelics.erlang_halberd": "Three-Pointed Halberd",
    "item.mythicrelics.erlang_halberd.tooltip.1": "%1$s block of extra reach; each hit sweeps mobs within %2$s blocks",
    "item.mythicrelics.erlang_halberd.tooltip.2": "Erlang Shen set (with the Eye worn): when a mob hurts you, the Celestial Hound appears for %3$s s",
    "item.mythicrelics.erlang_halberd.tooltip.3": "Hound cooldown: %4$s s",
    "lore.mythicrelics.erlang_halberd": "Erlang Shen's weapon, the sanjian liangren dao: a three-pointed, double-edged halberd with which he fought three hundred bouts against the Monkey King.\\\nHis hound, the Xiaotian Quan, bit Wukong's leg so Erlang could catch him.",
    "entity.mythicrelics.celestial_dog": "Celestial Hound",
    "message.mythicrelics.celestial_dog": "The Celestial Hound answers!",
    "item.mythicrelics.qilin_horn": "Qilin Horn",
    "item.mythicrelics.qilin_horn.tooltip.1": "Right-click: call the qilin and ride it",
    "item.mythicrelics.qilin_horn.tooltip.2": "Very fast, jumps high, no fall damage, fire immune and doesn't trample crops",
    "item.mythicrelics.qilin_horn.tooltip.3": "While riding, you and the animals and villagers within %1$s blocks regenerate",
    "item.mythicrelics.qilin_horn.tooltip.4": "When you dismount it leaves after %2$s s; if it dies, it comes back in %3$s min",
    "lore.mythicrelics.qilin_horn": "The qilin, a benevolent beast with a dragon's head, a deer's body covered in scales, an ox's tail and a single horn. It appears before just rulers.\\\nIt walks without crushing the grass or harming any living thing, wreathed in flames that do not burn.",
    "entity.mythicrelics.qilin": "Qilin",
    "item.mythicrelics.pixiu_bracelet": "Pixiu Bracelet",
    "item.mythicrelics.pixiu_bracelet.tooltip.1": "When you kill a hostile, %1$s%% chance it drops up to %2$s gold nuggets",
    "item.mythicrelics.pixiu_bracelet.tooltip.2": "When you die you keep your gold, emeralds and diamonds",
    "lore.mythicrelics.pixiu_bracelet": "The pixiu is a winged lion that feeds on gold and silver and has no way to let them out: whatever goes in stays.\\\nThat is why in China people wear jade bracelets with a pixiu to draw wealth and never lose it.",
    "message.mythicrelics.pixiu_kept": "The pixiu didn't let your treasures escape",
    "item.mythicrelics.fu_talismans": "Paper Talismans",
    "item.mythicrelics.fu_talismans.tooltip.1": "Right-click an undead: stick a talisman on it and it can't move for %1$s s",
    "item.mythicrelics.fu_talismans.tooltip.2": "Cooldown: %2$s s",
    "lore.mythicrelics.fu_talismans": "The fu, talismans of yellow paper written in vermilion ink by Taoist priests.\\\nIn folklore, one stuck on the forehead of a jiangshi (the hopping corpse) freezes it in place.",
    "message.mythicrelics.talisman_not_undead": "The talismans only hold the undead",
    "item.mythicrelics.xirang_sack": "Sack of Xirang",
    "item.mythicrelics.xirang_sack.tooltip.1": "Right-click: a wall of earth %1$s wide and %2$s high grows in front of you and cuts off water",
    "item.mythicrelics.xirang_sack.tooltip.2": "%3$s handfuls; one comes back every %4$s s",
    "lore.mythicrelics.xirang_sack": "Xirang was the self-growing earth. Gun stole it from Heaven to hold back the Great Flood; his son Yu the Great used it to raise dikes and dig channels until the waters were tamed.",
    "tooltip.mythicrelics.xirang_charges": "Handfuls: %s of %s",
    "item.mythicrelics.immortal_peach_branch": "Branch of the Celestial Peach",
    "item.mythicrelics.immortal_peach_branch.tooltip.1": "Grows a peach of immortality every %1$s days",
    "item.mythicrelics.immortal_peach_branch.tooltip.2": "Eating it: 8 extra hearts for %2$s min, Regeneration II for %3$s s and harmful effects removed",
    "lore.mythicrelics.immortal_peach_branch": "In the Heavenly Orchard of the Queen Mother of the West grew the peaches of immortality, which ripened once every three thousand years.\\\nThe Monkey King, set to guard them, ate almost all of them.",
    "tooltip.mythicrelics.peach_ripe": "A peach is ripe",
    "tooltip.mythicrelics.peach_growing": "The next peach is growing",
    "message.mythicrelics.peach_ripe": "A peach of immortality has ripened",
    "loot.mythicrelics.chests.trial_chambers.reward": "trial chamber rewards",
}

if __name__ == "__main__":
    assert set(ES) == set(EN), set(ES) ^ set(EN)
    for code, entries in (("es_es", ES), ("en_us", EN)):
        path = os.path.join(ASSETS, "lang", code + ".json")
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        for k in ("loot.mythicrelics.chests.trial_chambers.reward",):
            if k in data:
                entries = {kk: vv for kk, vv in entries.items() if kk != k}
        data.update(entries)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(json.dumps(data, indent=2, ensure_ascii=False, sort_keys=True) + "\n")
        print(code, len(data))
