# OnlyCoords

Le mod Minecraft qui affiche juste tes coordonnées. Rien d'autre.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8%20%7C%2026.1.2-62B47A)
![Mod Loader](https://img.shields.io/badge/Mod%20Loader-Fabric-DBD0B4)
![License](https://img.shields.io/badge/License-MIT-blue)

## Pourquoi OnlyCoords ?

L'écran de debug F3 affiche bien tes coordonnées… mais noyées au milieu de dizaines de lignes techniques dont tu n'as pas besoin. Les alternatives existantes sont souvent soit trop lourdes, soit pas assez compactes ou configurables. OnlyCoords fait une seule chose et la fait bien : un petit HUD de coordonnées propre, léger et entièrement réglable.

## Versions supportées

| Minecraft | Loader |
|-----------|--------|
| 1.21.8    | Fabric |
| 26.1.2    | Fabric |

Une seule base de code construit les deux versions, gérée en multi-version avec [Stonecutter](https://stonecutter.kikugie.dev/).

## Fonctionnalités

- **HUD de coordonnées** X / Y / Z en temps réel
- **Entièrement configurable** :
  - Ancrage sur 9 positions à l'écran (coins, bords, centre)
  - Échelle (taille) ajustable
  - Décimales activables (0 à 3 chiffres), point décimal forcé (`.`)
  - Couleur du texte et ombre portée
  - Fond semi-transparent optionnel (couleur réglable)
  - Direction cardinale optionnelle (`Facing: N / NE / E / ...`)
  - Masquage automatique quand l'écran de debug F3 est ouvert
- **Persistance** de la configuration en JSON
- **Raccourci clavier** pour afficher/masquer le HUD (assignable, non lié par défaut)
- **Écran de configuration en jeu** via ModMenu + YACL

Mod **100 % côté client** : fonctionne en solo comme sur n'importe quel serveur.

## Installation

1. Installe [Fabric Loader](https://fabricmc.net/use/) pour ta version de Minecraft.
2. Place dans ton dossier `mods/` :
   - **Fabric API** (requis)
   - **OnlyCoords** (le `.jar` correspondant à ta version de Minecraft)
   - *(recommandé)* **ModMenu** + **YetAnotherConfigLib (YACL)** pour l'écran de configuration en jeu
3. Lance le jeu : les coordonnées s'affichent. Tout se règle ensuite via ModMenu → OnlyCoords.

## Compilation depuis les sources

Le projet utilise [Stonecutter](https://stonecutter.kikugie.dev/) pour gérer les deux versions de Minecraft à partir d'une source unique.

Prérequis : **JDK 21** et **JDK 25** (Gradle sélectionne automatiquement le bon toolchain selon la version).

```bash
# Construire toutes les versions d'un coup :
./gradlew build

# (optionnel) Basculer la vue active du code source sur une version :
./gradlew "Set active project to 1.21.8"
./gradlew "Set active project to 26.1.2"
```

Les `.jar` produits se trouvent dans :

```
versions/1.21.8/build/libs/onlycoords-<version>+1.21.8.jar
versions/26.1.2/build/libs/onlycoords-<version>+26.1.2.jar
```

## Licence

[MIT](LICENSE)

---

Développé par **Kuzalo**.
