# DFS
Danis File Sync

Programm zum Synchronisieren von Verzeichnissen mit Versionierung.
Ich habe es hauptsächlich geschrieben, um mit ihm den Java Workspace (vor großen Änderungen am Code) wie einen snapshot einfrieren zu können.
Einfach kurz snippen die Dateien im ganzen Workspace und falls man was verschlimmbessert hat,
kann man immer wieder schauen, wie es mal funktioniert hat.
Man kann aber damit alles synchronisieren was man so Lust hat.

Ich würde gerne später eine server-fähige Version davon erstellen (für sync auf eine NAS zB), kriege es aber grad zeitlich noch nicht hin...


![grafik](https://user-images.githubusercontent.com/56628625/146671836-968f694f-3e23-407b-b619-29ba93932946.png)

Es hat m.E. folgende Vorteile vor anderen Lösungen (obwohl ich mir nicht viel Zeit genommen habe, um in Ergebnissen anderer zu suchen..)
- Es arbeitet auch nach Fehlern weiter: Nichts ist nerviger, als nach langem Joggen zurück zu kommen und zu sehen, dass der Kopier-Job nach 7% angehalten wurde und ein schönes Überspringen, Ignorieren... Fenster auf dem Bildschirm blinkt. Der logger zeigt einem dann die Fehler im Nachhinein an, die man korrigieren muss. Ja, man kann auch normal mit dem Tool kopieren.
- Man kann den Sync-Job Testen lassen VOR dem eigentlich Run. Das wird durch das markierte Häkchen beim Start sogar immmer intuitiv vorgegeben. Dabei sieht man, ob Schalter und Pfade gut gesetzt sind. Es wird dabei normal gearbeitet mit lgging im Ausgabefenster ohne die Kopier-Jobs, sonst ist alles gleich wie beim normalen Run. Einfach ein 2. Mal auf den Sync-Button drücken, wenn man genug gesehen hat...
-  Man kann zu jeder Zeit unterbrechen (durch Klick auf Start) und später irgendwann wieder forfahren (von vorne anschmeißen) ohne dass das Gesamt-Ergebnis ein anderes ist, als wenn der Job in einem i.O. durchlaufen konnte.
