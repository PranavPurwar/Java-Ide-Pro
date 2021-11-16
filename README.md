# Java-Ide-Pro
The sources of java n ide pro in java
# Note
This repository is currently not being updated, because im working on other project.
Btw pull requests will still be accepted
## For contributing

You can compile these sources in sketchware pro or code assist or android studio
### Sketchware Pro

Steps for compilation ->

1. Make a new project.
2. Copy the app/src/main/java folder from this repo to `/data/.../files/java` folder of your project.
3. Copy the `classpath.jar` file from `app/libs` folder of this repo to `/data/.../files/classpath` folder of your project.
4. Compile the project.
5. Use mt manager or apktool to merge the built dex file with the file `base.dex` found in the app/build folder of this repository.
6. Delete the already present classes5.dex (if present)
7. Copy the merged file inside the apk
8. Test It !

#### or

Build the apk file as you do while compiling Sketchware Pro

> I will be uploading a video about it's compilation in near future.
