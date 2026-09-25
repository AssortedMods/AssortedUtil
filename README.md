# Assorted Util

Small things that make everyday Minecraft smoother. Four come from the Grim Util part of the old
[Grim Pack](https://github.com/grim3212/grim-pack), brought forward from 1.12. All of it is rebuilt on the modern APIs.

- **Graves**: where you die, everything you carried and all your experience go into a grave. Use it to
  get everything back into the slots it came from.
- **Double doors**: one click opens both doors of a pair, a whole trapdoor hatch, or fence gates
  stacked on or beside each other. Modded doors, trapdoors and gates work too. Sneak to open just one.
- **Item replacer**: when the stack in your hand runs out or your tool breaks, a match from your
  inventory takes its place.
- **Time**: press H for Grim Util's clock panel. Press again to step through game time, real time,
  both, and hidden.
- **Light overlay**: press F7 to see the block light wherever a monster could spawn.
- **Damage numbers**: damage and healing pop off creatures as numbers, with what caused them.

Each is a part that can be switched off. Graves and double doors are in `assortedutil-common.toml`
because they change the world. The other four are in `assortedutil-client.toml`, so each player
chooses for themselves, and they work on any server.

Requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per Minecraft version; `26.2`
is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* Loader and its version — NeoForge, or Fabric Loader together with Fabric API
* Assorted Util version
* Assorted Lib version
* The full `latest.log`, plus the crash report if the game crashed

## Building

JDK 25 and the bundled Gradle wrapper. `common/` holds the loader-agnostic code; both loader
modules compile those sources inline rather than depending on a common jar, so there is nothing to
install between them.

How the build works - the Minecraft and loader versions, the runs, the tests, publishing - lives in
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild), pinned by `assortedbuild_version` in
`gradle.properties`. This repository only says what the mod is.

Assorted Lib is consumed as a Maven artifact. To build against an unreleased one, publish it first:

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Then from this repository:

```bash
./gradlew build                        # every module; jars land in <module>/build/libs
./gradlew :neoforge:runClient
./gradlew :fabric:runClient
./gradlew :neoforge:runGameTestServer  # headless gametests, non-zero exit on failure
./gradlew :fabric:runGameTest
./gradlew :fabric:runClientGameTest    # screenshots the grave, clock, overlay and a damage number; opens a game window
./gradlew :neoforge:runClientData      # datagen
./gradlew :neoforge:runServerData
```

Generated resources are committed. The NeoForge datagen writes them for both loaders; they are
regenerated, never hand-edited.

## License

[LGPL-3.0-only](LICENSE).
