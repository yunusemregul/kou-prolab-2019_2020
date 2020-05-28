#!/usr/bin/python
import codecs
import sqlite3
import random

conn = sqlite3.connect("netflix.db")

#conn.execute('''INSERT INTO Program (ad, tip, bolum_sayisi, uzunluk) VALUES ('Recep İvedik 6', 'Film', 1, 145)''')

with codecs.open("netflixdata.txt", "r", encoding="utf8") as fp:
	for line in fp:
		splitted = line.split("\t")
		ad = splitted[0].lstrip().rstrip()
		turler = splitted[1].split(",")
		tip = splitted[2].lstrip().rstrip()

		bolum_sayisi = 1 if tip=="Film" else random.randint(5,60)
		uzunluk = random.randint(60,180) if tip=="Film" else random.randint(45,70)

		for tur in turler:
			tur = tur.lstrip().rstrip()

			cursor = conn.execute("SELECT 1 FROM Tur WHERE ad=?", (tur,))
			data = cursor.fetchall()

			if (len(data)==0):
				conn.execute("INSERT INTO Tur (ad) VALUES (?)", (tur,))
				conn.commit()

		cursor = conn.execute("SELECT 1 FROM Program WHERE ad=?", (ad,))
		data = cursor.fetchall()

		if (len(data)==0):
			conn.execute("INSERT INTO Program (ad, tip, bolum_sayisi, uzunluk) VALUES (?, ?, ?, ?)", (ad, tip, bolum_sayisi, uzunluk,))
			conn.commit()

		for tur in turler:
			tur = tur.lstrip().rstrip()

			cursor = conn.execute("SELECT id FROM Program WHERE ad=?", (ad,))
			data = cursor.fetchone()
			program_id = data[0]


			cursor = conn.execute("SELECT id FROM Tur WHERE ad=?", (tur,))
			data = cursor.fetchone()
			tur_id = data[0]

			cursor = conn.execute("SELECT 1 FROM ProgramTur WHERE program_id=? AND tur_id=?", (program_id,tur_id,))
			data = cursor.fetchall()

			if (len(data)==0):
				conn.execute("INSERT INTO ProgramTur (program_id, tur_id) VALUES (?, ?)", (program_id, tur_id))

print("Changed total of %d rows." % conn.total_changes)
conn.commit()
conn.close()