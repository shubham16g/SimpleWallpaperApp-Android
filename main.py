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

with open('sample.json', 'w') as f:
    json.dump(fullurl, f)