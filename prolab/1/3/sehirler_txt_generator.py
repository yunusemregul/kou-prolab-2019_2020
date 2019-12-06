BOLGELER = ["MA","KA","EG","IA","DA","AK","GA"]
ILLER = [
	["BALIKESIR","BILECIK","BURSA","CANAKKALE","EDIRNE","ISTANBUL","KIRKLARELI","KOCAELI","SAKARYA","TEKIRDAG","YALOVA","DUZCE"],
	["AMASYA","ARTVIN","BOLU","CORUM","GIRESUN","GUMUSHANE","KASTAMONU","ORDU","RIZE","SAMSUN","SINOP","TOKAT","TRABZON","ZONGULDAK","BAYBURT","BARTIN","KARABUK"],
	["AFYON", "AYDIN", "DENIZLI", "IZMIR", "KUTAHYA", "MANISA", "MUGLA", "USAK"],
	["ANKARA", "CANKIRI", "ESKISEHIR", "KAYSERI", "KIRSEHIR", "KONYA", "NEVSEHIR", "NIGDE", "SIVAS", "YOZGAT", "AKSARAY", "KARAMAN", "KIRIKKALE"],
	["AGRI", "BINGOL", "BITLIS", "ELAZIG", "ERZINCAN", "ERZURUM", "KARS", "MALATYA", "MUS", "TUNCELI", "VAN", "ARDAHAN", "IGDIR"],
	["ADANA", "ANTALYA", "BURDUR", "HATAY", "ISPARTA", "MERSIN", "KAHRAMANMARAS", "OSMANIYE"],
	["ADIYAMAN", "DIYARBAKIR", "GAZIANTEP", "HAKKARI", "MARDIN", "SIIRT", "SANLIURFA", "BATMAN", "SIRNAK", "KILIS"]
]

def findBolgeForIl(il):
	for bolgeindex, iller in enumerate(ILLER):
		for il in iller:
			if (il==sehir.upper()):
				return BOLGELER[bolgeindex]

generated = open("sehirler_generated.txt","wt")

with open("sehirler.txt") as fp:
	for cnt, line in enumerate(fp):
		sehir = line.split(',')[0]
		bolge = findBolgeForIl(sehir)
		generated.write(str(cnt+1)+','+sehir+','+bolge+','+','.join(line.split(',')[1:]))

generated.close()
