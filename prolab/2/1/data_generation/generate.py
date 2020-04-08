#!/usr/bin/env python
# -*- coding: utf-8 -*- 
import json

latlongString = open('turkey_latlong_json','r').read()
ilplakaString = open('il_plaka_json','r').read()

latlongDict = json.loads(latlongString)
ilplakaDict = json.loads(ilplakaString)
#print(json.dumps(latlongDict, ensure_ascii=False).encode('utf8'))

finalDict = []

for city in latlongDict:
	citynameinenglish = city['name'].lower().replace(u'â','a').replace(u'ş','s').replace(u'ü','u').replace(u'ı','i').replace(u'ğ','g').replace(u'ö','o').replace(u'ç','c')
	connected = []

	finalDict.append({
		'plateNum': city['id'],
		'name': city['name'],
		'lat': float(city['latitude']),
		'lng': float(city['longitude']),
		'connected': connected
	})

def plakaOf(cityname):
	for city in ilplakaDict:
		if city==cityname:
			return ilplakaDict[city]


with open('komsular','r') as komsular:
	line = komsular.readline()
	linenum = 1
	while line:
		line = line.replace('\n','')
		sehirler = [plakaOf(x) for x in line.split(',')]
		finalDict[linenum-1]['connected'] = sehirler

		line = komsular.readline()
		linenum += 1

open('final.json','w').write(json.dumps(finalDict, ensure_ascii=False, sort_keys=True, indent=2, separators=(',', ': ')).encode('utf8'))


#	{
#		id: 1,
#		name: "Adana",
#		lat: 15,
#		lng: 12,
#		connected: {
#			2, 3, 4, 5
#		}
#	}