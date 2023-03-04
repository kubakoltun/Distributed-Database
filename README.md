# Distributed-Database

Implementacja:

Kod zawiera implementację klasy DatabaseNode, która dziedziczy po klasie Thread. Klasa ta odpowiada za utworzenie serwera, który nasłuchuje na wybranym porcie i przyjmuje połączenia od klientów. Dla każdego połączenia tworzony jest nowy wątek (klasa DatabaseServerThread), który obsługuje komunikację z klientem.
Klasa DatabaseNode posiada również pole database, które jest mapą przechowującą dane w formie klucz-wartość.
Protokół polega na wysłaniu przez klienta zapytania do serwera, na które otrzyma odpowiedź z odpowiednią wartością lub komunikatem o błędzie.


Kompilacja i instalacja:

Aby skompilować i uruchomić program należy wykonać następujące kroki:
-Pobrać plik z kodem źródłowym.
-Otworzyć terminal i przejść do folderu z plikiem.
-Wpisać polecenie "javac DatabaseNode.java" aby skompilować kod.
-Wpisać polecenie "java DatabaseNode -tcpport [numer_portu] -record [klucz]:[wartość]" aby uruchomić program.


Zaimplementowane:

Klasa DatabaseNode, która tworzy serwer i obsługuje połączenia z klientami
Klasa DatabaseServerThread, która obsługuje komunikację z klientem
Obsługa mapy przechowującej dane w formie klucz-wartość


Niedziałające:

Brak funkcjonalności połączenia z innymi węzłami
Brak możliwości zakończenia pracy programu (poza zamknięciem terminala)
Brak obsługi błędów w przypadku nieprawidłowych argumentów podczas uruchamiania programu.
