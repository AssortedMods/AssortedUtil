# Assorted Util

Small things that make everyday Minecraft smoother, each its own mod so you can take only the ones
you want. Graves, double doors, the item replacer and time come from the Grim Util part of the old
[Grim Pack](https://github.com/grim3212/grim-pack), brought forward from 1.12. All of it is rebuilt on
the modern APIs.

| Mod | Directory | What it does |
| --- | --- | --- |
| [Assorted Graves](mods/graves) | `mods/graves/` | Where you die, your items and experience go into a grave. |
| [Assorted Double Doors](mods/doubledoors) | `mods/doubledoors/` | Pairs of doors, trapdoor hatches and fence gates open together. |
| [Assorted Item Replacer](mods/itemreplacer) | `mods/itemreplacer/` | Refills your hand when a stack runs out or a tool breaks. |
| [Assorted Time](mods/time) | `mods/time/` | A key for a clock with the world's time and yours. |
| [Assorted Light Overlay](mods/lightoverlay) | `mods/lightoverlay/` | A key that shows where monsters can spawn. |
| [Assorted Damage Numbers](mods/damagenumbers) | `mods/damagenumbers/` | Damage and healing as numbers off creatures. |

The last four only change what one player sees or does, so they work on any server.

Every one requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per
Minecraft version; `26.2` is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* Loader and its version — NeoForge, or Fabric Loader together with Fabric API
* Which of these mods, and their versions
* Assorted Lib version
* The full `latest.log`, plus the crash report if the game crashed

## Building

JDK 25 and the bundled Gradle wrapper. Each mod is a directory under `mods/`, laid out like a one-mod repository:
its own `gradle.properties`, `README.md` and `CHANGELOG.md`, and `common/`, `fabric/`, `neoforge/`.
`assorted_mods` in the root `gradle.properties` lists them; the root file holds only what they share.
Each `common/` holds the loader-agnostic code; both loader modules compile those sources inline.

How the build works - the Minecraft and loader versions, the runs, the tests, publishing - lives in
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild), pinned by `assortedbuild_version` in
`gradle.properties`. This repository only says what the mods are.

Assorted Lib is consumed as a Maven artifact. To build against an unreleased one, publish it first:

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Then from this repository - a task named alone runs in every mod, `:<dir>:` picks one:

```bash
./gradlew build                                 # every mod; jars land in mods/<dir>/<module>/build/libs
./gradlew :graves:neoforge:runClient
./gradlew :graves:fabric:runClient
./gradlew :all:neoforge:runClient               # all six mods in one game (:all:fabric too)
./gradlew runGameTestServer                     # headless gametests on NeoForge, non-zero exit on failure
./gradlew runGameTest                           # and on Fabric
./gradlew :time:fabric:runClientGameTest        # screenshots what a server cannot see; opens a game window
./gradlew runClientData runServerData           # datagen
./gradlew :graves:publishMods -PdryRun=true     # rehearse one mod's release
```

Generated resources are committed. The NeoForge datagen writes them for both loaders; they are
regenerated, never hand-edited.

## License

[LGPL-3.0-only](LICENSE).
