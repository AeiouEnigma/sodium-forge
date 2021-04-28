![Project icon](https://git-assets.jellysquid.me/hotlink-ok/sodium/icon-rounded-128px.png)

# Sodium/Lithium/Phosphor (Forge fork)
![GitHub license](https://img.shields.io/github/license/jellysquid3/sodium-fabric.svg)
![GitHub tag](https://img.shields.io/github/tag/jellysquid3/sodium-fabric.svg)

### This is Forge edition of Sodium/Lithium/Phosphor

Sodium is a free and open-source optimization mod for the Minecraft client that improves frame rates, reduces
micro-stutter, and fixes graphical issues in Minecraft.

:warning: This fork is experimental. Though this fork is functional, stuff will be broken.

### Downloads

Currently, only on [release page](https://github.com/AeiouEnigma/sodium-forge/releases/latest/).

### Community
Please do not join Caffeine discord if you intend to get support about this fork. All forks or unofficial version 
of sodium/phosphor/lithium are not support by original authors, if you encounter any issues, check current issues on this repo or 
make a new issue. 

### Building from source

If you're hacking on the code or would like to compile a custom build of Sodium from the latest sources, you'll want
to start here.

#### Prerequisites

You will need to install JDK 8 (or newer, see below) in order to build Sodium. You can either install this through
a package manager such as [Chocolatey](https://chocolatey.org/) on Windows or [SDKMAN!](https://sdkman.io/) on other
platforms. If you'd prefer to not use a package manager, you can always grab the installers or packages directly from
[AdoptOpenJDK](https://adoptopenjdk.net/).

On Windows, the Oracle JDK/JRE builds should be avoided where possible due to their poor quality. Always prefer using
the open-source builds from AdoptOpenJDK when possible.

#### Compiling

Navigate to the directory you've cloned this repository and launch a build with Gradle using `gradlew build` (Windows)
or `./gradlew build` (macOS/Linux). If you are not using the Gradle wrapper, simply replace `gradlew` with `gradle`
or the path to it.

The initial setup may take a few minutes. After Gradle has finished building everything, you can find the resulting
artifacts in `build/libs`.

### Tuning for optimal performance

_This section is entirely optional and is only aimed at users who are interested in squeezing out every drop from their
game. Sodium will work without issue in the default configuration of almost all launchers._

Generally speaking, newer versions of Java will provide better performance not only when playing Minecraft, but when
using Sodium as well. The default configuration your game launcher provides will usually be some old version of Java 8
that has been selected to maximize hardware compatibility instead of performance.

For most users, these compatibility issues are not relevant, and it should be relatively easy to upgrade the game's Java
runtime and apply the required patches. For more information on upgrading and tuning the Java runtime, see the
guide [here](https://gist.github.com/jellysquid3/8a7b21e57f47f5711eb5697e282e502e).

### License

Sodium is licensed under GNU LGPLv3, a free and open-source license. For more information, please see the
[license file](https://github.com/jellysquid3/sodium-fabric/blob/1.16.x/dev/LICENSE.txt).
