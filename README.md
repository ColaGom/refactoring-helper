## Migration rules

- If possible, don't touch ***original code.*** (just convert `java` to `kotlin`)
- convert `butterknife` to `kotlin-android extension`
- convert `dagger` to `kodein`
    - exclude `presenter` inject, It will migrate during `MVVM` refactoring.
- don't convert `java interface` file.
    - for avoid issue it related SAM(Single Abstract Method) conversions.
- primitive types, string to `var`
    - set default values
        - 0/0F/0L/""/...
- inner interface
    - convert `kotlin convention`
        - object : {OBJECT_NAME} {implement of interface}
- method parameter
    - default `nullable`
