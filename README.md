O dataset-u:
Izvor: https://zenodo.org/records/13808085

Dataset sadrzi sirok skup podataka (1.6 gb) o razlicitim parametrima sistema za snabdevanje vodom na univerzitetu. 
Za projekat iskoriscen je samo deo dataseta - 1 rezervoar vode sa svojim pumpama i podaci o nivou vode u rezervoaru i
snaga u svakom od tri kanala pumpi rezervoara.

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
topic EMQX brokera i prikazuje korisniku neocekivane dogadjaje. Kao zahtev 3. projekta, prosiren je
da prikazuje predikcije i ocitane vrednosti sa senzora za nivo vode u rezervoaru. On se koriscenjem
web socketa povezuje na NATS message broker odakle sa ml/predictions topic-a dobija odgovarajuce
informacije.

DataManager i Gateway takodje loguju sve podatke o zahtevima koji stizu do njih, dok EventManager
loguje sve poruke koje procita sa dev/cdc topica.

MLAAS je servis koji na osnovu treniranog modela vrsi predikciju nivoa vode u rezervoaru na osnovu
snage kanala u pumpama. Ovaj servis izlaze rest endpoint /predict kome se pristupa radi predikcije.

Analytics je python servis koji cita podatke sa dev/cdc topic-a emqx brokera i na osnovu procitanih
podataka salje zahtev mlaas servisu od koga dobija predikciju nivoa vode u rezervoaru. Ovu informaciju
zajedno sa stvarnim nivoom vode koji je izmerio senzor publikuje na NATS ml/predictions topic.
