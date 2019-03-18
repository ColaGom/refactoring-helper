## Motivation

- Migrating from `java` to `kotlin`
- Statistics of java (excluded test files)
    - file count : 730
    - loc(java) : 101445 [ java+kt : 131216 ]
    - 77.3%

## Migration rules

- If possible, don't touch ***original code.*** (just convert `java` to `kotlin`)
- convert `butterknife` to `kotlin-android extension`
- convert `dagger` to `kodein`
    - exclude `presenter` inject, It will migrate during `MVVM` refactoring.
- don't convert `java interface` file.
    - for avoiding issue it related SAM(Single Abstract Method) conversions.
- primitive types, string to `var`
    - set default values
        - 0/0F/0L/""/...
- inner interface
    - convert `kotlin convention`
        - object : {OBJECT_NAME} {implement of interface}
- method parameter
    - default `nullable`
