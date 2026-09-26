# Assorted Util

A group of small mods that make everyday Minecraft a little easier. Each one can be installed on its own.

- [Assorted Graves](mods/graves) keeps your items and experience in a grave when you die
- [Assorted Double Doors](mods/doubledoors) opens both doors of a double door at once
- [Assorted Item Replacer](mods/itemreplacer) refills your hand when a tool breaks or a stack runs out
- [Assorted Time](mods/time) shows the in-game time and your real time
- [Assorted Light Overlay](mods/lightoverlay) shows light levels where mobs can spawn
- [Assorted Damage Numbers](mods/damagenumbers) shows damage numbers on creatures

Requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per Minecraft version and `26.2` is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* NeoForge version, or Fabric Loader and Fabric API versions
* Which of these mods you have and their versions
* Assorted Lib version
* The full `latest.log`, and the crash report if the game crashed

## Building

You need JDK 25. Each mod is its own folder under `mods`. The build setup comes from
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild) and `assortedbuild_version` in `gradle.properties`
picks the version.

To build against a local copy of Assorted Lib, publish it first.

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Some useful commands

```bash
./gradlew build                                  # build every mod
./gradlew :graves:neoforge:runClient                  # run one mod
./gradlew :all:neoforge:runClient                # run all of them together
./gradlew runGameTestServer runGameTest          # gametests on NeoForge and Fabric
./gradlew runClientData runServerData            # datagen
```

## License

[LGPL-3.0-only](LICENSE).
