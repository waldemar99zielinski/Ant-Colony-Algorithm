# Ant Colony Algorithm 

Projekt zrealizowany w ramach przedmiotu Podstawy Sztucznej Inteligencji. Dla sieci janos-us-ca pochodzącej z sndlib ustala optymalną ścieżkę 
według ustalonej metryki, a także podejmuje się wizualizacji sieci w czasie rzeczywistym.

### Uruchomienie
Zalecamy korzystanie z IntelliJ IDE. W celu kompilacji i uruchomienia projektu należy:
 - Pobrać i wypakować bibliotekę JavaFX np. z: https://gluonhq.com/products/javafx/ (JavaFX nie jest częścią JDK od wersji 8+)
 - Pobrać i wypakować SNDlib ze strony http://sndlib.zib.de/home.action 
 - Do struktur projektu w zakładce biblioteki dołączyć  '%ścieżka_do_folderu_JavaFX/lib'
 - W tej samej zakładce załączyć również 'SNDlib.jar', a także zawartość folderu 'SNDlib/lib'
 - W konfiguracji Debugowania/Uruchomienia w polu VM Options dołączyć argumenty:
 >> --module-path ścieżka_do_folderu_javaFX\lib --add-modules=javafx.controls,javafx.fxml,javafx.media
