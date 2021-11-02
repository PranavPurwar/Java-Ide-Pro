# Java-Ide-Pro
The sources of java n ide pro in java

## For contributing

You can compile these sources in sketchware pro or code assist or android studio
### Sketchware Pro

Steps for compilation ->

1. Make a new project.
2. Copy the main/java folder from this repo to `/data/.../files/java` folder of your project.
3. Copy the `classpath.jar` file from `app/libs` folder of this repo to `/data/.../files/classpath` folder of your project.
4. Compile
5. Use mt manager or apktool to decompile the built classes.dex and java n ide's `classes5.dex` file to smali source.
6. Replace the java n ide's `classes5.dex`'s classes with the one you just decompiled from built skpro project.
7. Recompile the dex files of java n ide.
8. Test.

#### or

Build the apk file as you do while compiling Sketchware Pro

> I will be uploading a video about it's compilation soon.
