# NASABot v7.1.0
**Now featuring configurable automated APOD posting times!** A Discord bot that implements the NASA API to provide cool, interesting, and informative artifacts.\
[![Discord Bots](https://top.gg/api/widget/status/748775876077813881.svg)](https://top.gg/bot/748775876077813881)
[![Discord Bots](https://top.gg/api/widget/servers/748775876077813881.svg)](https://top.gg/bot/748775876077813881)
**Upvote us on [Top.gg](https://top.gg/bot/748775876077813881)!**

#### [Discord Invite Link](https://discord.com/api/oauth2/authorize?client_id=748775876077813881&permissions=2214710336&scope=applications.commands%20bot)

This project is a work in progress, and will be updated iteratively as additional features are added.

See [the project board](https://github.com/SniperNoob95/NASABot/projects/1) for insight into upcoming additions/changes.

Join the [Discord server](https://discord.gg/b4wS5q4) if interested.

**Contact Sniper Noob#5606 (181588597558738954) on Discord if needed.**

## Command Usage
#### /setpostchannel
	/setpostchannel <#channel mention>
    Ex: /setpostchannel #lounge
Sets the Post Channel for the server to receive automated APOD postings each day, as well as update announcements from the owner.

#### /removepostchannel
Removes the Post Channel set for the server.

#### /getpostchannel
Returns the Post Channel set for the server.

#### /setposttime
    /setposttime <option>
    Ex: /setposttime 1
    Options are as follows:
        0 (default): 16:00 UTC
        1: 6:00 UTC
        2: 11:00 UTC
        3: 21:00 UTC
Sets the Post Time for the server to receive automated APOD postings each day. By default, all servers use option 0 until overridden.

#### /getposttime
Returns the Post Time set for the server.

#### /apod
    /apod [date (YYYY-MM-DD)]
    Ex: /apod 2020-09-05
Returns the Astronomy Picture of the Day for the previous day, or the given date if provided.

#### /moonphase
Returns the current moon phase and time until next new moon.

#### /iss
Displays the current location of the International Space Station.

#### /image
    /image <search term>
    Ex: /image black hole
Returns an image from the NASA image database that matches the given search term.

#### /info
Returns information about the bot.

#### /help
Sends the necessary information for using the bot.
