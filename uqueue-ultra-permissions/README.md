# uQueue Ultra Permissions Integration

This submodule contains the Ultra Permissions integration for uQueue. A seperate submodule is used because Ultra Permissions is a paid plugin and not everyone will be able to compile against it. A precompiled version of this module is stored in [the repo](https://repo.noah.pm/#browse/browse:maven-releases:me%2Fnoahvdaa%2Fuqueue%2Futil%2Fpermissions%2FUltraPermissionsPermissionUtil) and is normally used for compilation.

## Compiling

To compile this module, the Ultra Permissions jar is required as a dependency. Place it in this folder with the name "Ultrapermissions.jar". Then, you can simply run `mvn package`. The jar will only contain the `UltraPermissionsPermissionUtil` class.
