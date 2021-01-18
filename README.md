# NASABot
**Now featuring automated APOD posting!** A Discord bot that implements the NASA API to provide cool, interesting, and informative artifacts.\
[![Discord Bots](https://top.gg/api/widget/status/748775876077813881.svg)](https://top.gg/bot/748775876077813881)
[![Discord Bots](https://top.gg/api/widget/servers/748775876077813881.svg)](https://top.gg/bot/748775876077813881)
**Upvote us on [Top.gg](https://top.gg/bot/748775876077813881)!**

#### [Discord Invite Link](https://discord.com/api/oauth2/authorize?client_id=748775876077813881&permissions=67226688&scope=bot)

This project is a work in progress, and will be updated iteratively as features are added.

See [the project board](https://github.com/SniperNoob95/NASABot/projects/1) for insight into upcoming additions/changes.

Join the [Discord server](https://discord.gg/b4wS5q4) if interested.

**Contact Sniper Noob#5606 (181588597558738954) on Discord if needed.**

Credits to https://github.com/JDA-Applications/JDA-Utilities for providing the extremely helpful JDA-Utilities library.

## Command Usage
#### setPostChannel
	NASA_setPostChannel <#channel mention>
    Ex: `NASA_setPostChannel #lounge
Sets the Post Channel for the server to receive automated APOD postings each day, as well as update announcements from the owner.
#### removePostChannel
	NASA_removePostChannel
Removes the Post Channel set for the server.
#### getPostChannel
	NASA_getPostChannel
#### APOD
    NASA_POTD [date (YYYY-MM-DD)]
    Ex: NASA_POTD 2020-09-05
Returns the Astronomy Picture of the Day for the previous day, or the given date if provided.

#### Image
    NASA_image <search term>
    Ex: NASA_image black hole
Returns an image from the NASA image database that matches the given search term.

#### Info
    NASA_info
Returns information about the bot.

#### Help
    NASA_help
Sends the caller a PM with help for using the bot.
