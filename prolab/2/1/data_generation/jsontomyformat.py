#!/usr/bin/env python
# -*- coding: utf-8 -*- 
import json
import io

jsonStr = open('final.json','r').read()
finalDict = json.loads(jsonStr)

with io.open('finalFinal.txt', 'w+', encoding='utf-8') as file:
	for city in finalDict:
		cityStr = ''
		cityStr += 'plaka='+str(city['plateNum'])+' '
		cityStr += 'ad='+city['name']+' '
		cityStr += 'enlem='+str(city['lat'])+' '
		cityStr += 'boylam='+str(city['lng'])+' '
		cityStr += u'komşular='+','.join(str(x) for x in city['connected'])
		cityStr += '\n'
		file.write(cityStr)