import snscrape.modules.twitter as twitterScraper
import json

def get_photo_full_url(tweet):
    if tweet.media:
        for medium in tweet.media:
            if isinstance(medium, twitterScraper.Photo):
                return medium.fullUrl.rsplit('=', 1)[0] + '=orig'

fullurl = []

for i,tweet in enumerate(twitterScraper.TwitterSearchScraper('from:ClashHeroes since:2021-02-04').get_items()):
    if i>1000:
        break
    fullurl.append(get_photo_full_url(tweet))
    fullurl = list(filter(None, fullurl))

# with open('wallpapers.json', 'w') as f:
#     json.dump(fullurl, f)

# format to output json
# {
#     [
#         "name" : #assign a int in order from 1 to 1000
#         "url" : #fullurl
#         "previewUrl" : #fullurl + '=small'
#         "categories" : # give constant value "Clash Royale"
#     ]
# }

with open('wallpapers.json', 'w') as f:
    f.write('{\n')
    f.write('    "wallpapers": [\n')
    for i, url in enumerate(fullurl):
        f.write('        {\n')
        f.write('            "name": "' + str(i+1) + '",\n')
        f.write('            "url": "' + url + '",\n')
        f.write('            "previewUrl": "' + url + '=small",\n')
        f.write('            "categories": "Clash Royale"\n')
        f.write('        }')
        if i != len(fullurl)-1:
            f.write(',\n')
        else:
            f.write('\n')
    f.write('    ]\n')
    f.write('}')