# Weather Check

![Android support](https://shields.io/badge/Android-SDK_34-green) ![Android support](https://shields.io/badge/v.1.0-purple)

<p align="center">
    <img src="images/logo.png" width="200" alt="logo"/> 
</p>

App presenting weather and forecast in selected location.

## Release

`
v1.0
`

## Technologies

- Android SDK 34
- JVM 1.8
- Gradle 8.2.2
- Kotlin 1.9.22
- Jackson 2.15.2
- OkHttp3 4.12.0
- Android Material 1.11.0

# TODO
1. Layout menu jest relative przez co nie ma prawidłowych proporcji
widgetów i najeżdżają na siebie. Należy coś z tym zrobić. [DONE]
2. Istnieją tylko 3 pliki zapisów. Należy sprawdzić, czy każda lokacja
z osobną nazwą potrzebuje osobnego pliku. [DONE]
3. Spinner nie zachowuje prawidłowego kształu. Należy to poprawić.
4. Przetestować czy prawidłowo kasuje rejestry z historii.
5. Refactoring nazw. [DONE]
6. Obrót powoduje zawieszenie programu (nie można znaleźć kontruktora Menu).
7. Obsługa zmiany jednostek.