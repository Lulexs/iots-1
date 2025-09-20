Gateway je servis koji sluzi za komunikaciju izmedju klijenata/senzora i ostatka sistema.
Nudi 2 kontrolera - PowerController i WaterTank controller, koji izvrsavaju crud operacije
nad odgovarajucim podacima koji stizu sa razlicitih senzora i to pet standardnih operacija -
dodavanje, azuriranje, brisanje i pribavljanje jednog podatka ili liste podataka u opsegu,
kao i agregacija podataka. Ove operacije se vrse na osnovu imena rezervoara, imena pumpe ako
je to neophodno i vremena kada je podatak zabelezen. Gateway servis komunicira sa DataManager
servisom koriscenjem grpc-a.

DataManager je servis koji sluzi za upisivanje i citanje senzorskih podataka iz bazu. Gateway
mu putem grpc-a salje zahtev za izvrsenjem neke operacije. DataManager prima taj zahtev,
izvrsava operaciju nad bazom i vraca odgovor Gateway-u. DataManager takodje implementira
Repository dizajn patern za rad sa bazom podataka.

DataManager i Gateway takodje loguju sve podatke o zahtevima koji stizu do njih.
