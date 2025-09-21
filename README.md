Gateway je dotnet servis koji sluzi za komunikaciju izmedju klijenata/senzora i ostatka sistema.
Nudi 2 kontrolera - PowerController i WaterTank controller, koji izvrsavaju crud operacije
nad odgovarajucim podacima koji stizu sa razlicitih senzora i to pet standardnih operacija -
dodavanje, azuriranje, brisanje i pribavljanje jednog podatka ili liste podataka u opsegu,
kao i agregacija podataka. Ove operacije se vrse na osnovu imena rezervoara, imena pumpe ako
je to neophodno i vremena kada je podatak zabelezen. Gateway servis komunicira sa DataManager
servisom koriscenjem grpc-a.

DataManager je spring boot servis koji sluzi za upisivanje i citanje senzorskih podataka iz bazu.
Gateway mu putem grpc-a salje zahtev za izvrsenjem neke operacije. DataManager prima taj zahtev,
izvrsava operaciju nad bazom i vraca odgovor Gateway-u. DataManager takodje implementira
Repository dizajn patern za rad sa bazom podataka.
Prosirenje za drugi projekat: DataManager sada, pord upisivanja podataka u bazu salje i na
dev/cdc topic EMQX brokera.

EventManager je python servis koji cita podatke sa dev/cdc topica EMQX brokera, filtrira ih
i dogadjaje sa neocekivanim vrednostima salje na dev/unexpected topic istom EMQX brokera.
Konekcija sa EMQX brokerom ostvaruje se preko tcp-a.

MqttClient je react web aplikacija koja se koriscenjem web soketa povezuje na dev/unexpected
topic EMQX brokera i prikazuje korisniku neocekivane dogadjaje.

DataManager i Gateway takodje loguju sve podatke o zahtevima koji stizu do njih, dok EventManager
loguje sve poruke koje procita sa dev/cdc topica.
