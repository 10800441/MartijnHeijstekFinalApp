Reviewed by Nils HulzeBosch:

MainActivity:
- Code ziet er goed uit! Duidelijke naamgeving van variabelen/methods en indents. Methods zijn niet te lang, maar ik zou bij getSavedSpots() en getSavedSessions() wel comments toevoegen, iets van 3 regels per method bovenaan of in de method af en toe een regel. Anders moet je nog nét teveel ‘raden’. Bij de rest zijn de comments wel goed, maar zelf doe ik meestal iets meer comments (ook omdat ik dat zelf chill vind, maar is je eigen keuze).
- Mogelijk kan je onCreate() nog opdelen in 3 of 4 methods, bijv.:
initializeParams(), SendToSignIn(), showWelcomeMessage() en setAdapters()
Maar is je eigen keuze, meer voor de leesbaarheid/bondigheid van onCreate(), meestal wel fijn als die heel overzichtelijk is.

SignInActivity:
- Beetje hetzelfde: nog boven elke method een comment toevoegen en mogelijk onCreate() opdelen in enkele methods. Verder goed!

SurfSpotActivity:
- Hoe je je variabelen hebt opgedeeld bovenaan is erg overzichtelijk :-) Miss kan je dat ook in de MainActivity doen. Anders doet John Cena het wel voor je.
- Ditmaal is onCreate() wel iets te lang, dus opdelen in een aantal methods en die onder elkaar aanroepen (dan zie je ook gelijk aan de naam van de method wat er in welke volgorde gebeurt).
- Paar commentjes erbij

SearchActivity:
- Deze comment staat bovenaan, maar die snap ik niet helemaal haha:
“ I this activity the API checks for matching whoeid's and redui “
- onCreate() opdelen in bijv. initializeParams(), checkConnection() en initializeAdapter()
- Paar commentjes erbij

Session.java:
Heel slim dat je toString() hebt gebruikt, scheelt een hoop werk als je dat los zou moeten aanmaken in bijv. een CustomAdapter haha ;-)

Algemeen (beetje herhaling):
- goede naamgeving variabelen / methods / activities
- comments mogen iets langer / vaker
- liever iets meer methods aanmaken dan heel veel in één method ( hint onCreate() hint )
- het aantal witregels dat je gebruikt tussen of in methods verschilt wel eens, ik zou gewoon één standaard nemen voor de leesbaarheid / netheid
- maar verder: je voldoet aan alle requirements dus dat is heel mooi. En heel goed dat je FireBase hebt gebruikt als Authentication én Database en dat het werkt! :D

check check dubbelcheck
• The app concept should be centered around a live open data API.                   COMPLETE
• External data should be retrieved using HTTP-requests.                            COMPLETE
• Very small user data should be persisted on the phone (name?, favorites?, etc.).  COMPLETE
• Firebase should be used to save other user data.                                  COMPLETE
• Code should be organized well (keeping in mind a separation of concerns).         COMPLETE
• Code should be documented well (comments as well as READMEs) —> nog toevoegen     TODO
